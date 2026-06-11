package cn.zswltech.mithras.application.orchestration.facade.projestablish;

import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishVersionApplicationService;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishEffectREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDetailREQ;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDetailRSP;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.rating.service.RatingClientService;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.projectprocess.convert.projestablish.ProjEstablishBaseInfoConverter;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.client.ClientAuthorityService;
import cn.zswltech.mithras.application.orchestration.client.ClientTransferService;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.impl.ProjEstablishVersionServiceImpl;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.associationreport.constant.MithrasConstants.ERR_IN_TRANSFER;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.*;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;

/**
 * @author zhaozhengkang
 * @description 立项信息版本表
 * @date 2022-07-19
 */
@Service
public class ProjEstablishVersionFacade implements ProjEstablishVersionApplicationService {

    @Resource
    private ProjEstablishVersionServiceImpl versionService;
    @Resource
    private ProjEstablishService projEstablishService;
    @Resource
    private ProjEstablishBaseInfoService baseInfoService;
    @Resource
    private ProjEstablishPriceService priceService;
    @Resource
    private ProjEstablishBaseInfoLibService baseInfoLibService;
    @Resource
    private ProjEstablishBaseInfoConverter baseInfoConverter;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private RatingClientService ratingClientService;

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = "PROJ_ESTABLISH"
    )
    public R<Void> effect(ProjEstablishEffectREQ req) {
        // 加锁
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.PROJ_ESTABLISH.name(), req.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            ProjEstablishBaseInfo baseInfo = baseInfoService.getById(req.getId());
            if (clientTransferService.inTransfer(baseInfo.getClientId())) {
                err(ERR_IN_TRANSFER);
            }

            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            if (CLOSED.name().equals(baseInfo.getProjEstablishStatus()) || EXPIRE.name().equals(baseInfo.getProjEstablishStatus())) {
                throw new MithrasException("立项已关闭/失效，不能提交审核");
            }
            if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(baseInfo.getClientId())) {
                throw new MithrasException("无所选客户管护权，无权进行操作");
            }
            // 是否可提交 简单校验
            if (Objects.nonNull(projEstablishService.findRelatedProcess(req.getId()))) {
                throw new MithrasException("该立项数据变动处于流程中，无法提交数据");
            }
//            if (Objects.equals(baseInfo.getRecordStatus(), TAKE_EFFECT)
//                    && !Objects.equals(baseInfo.getProcessStatus(), ProjProcessState.CHANGING_UN_SUBMIT)) {
//                throw new MithrasException("数据未变动");
//            }
            projEstablishService.effectCheck(baseInfo);

            /*添加限制  风控行业分类为公用事业、民生消费类  则校验客户是否完成评级*/
            Set<String> set = new HashSet<>(Arrays.asList(
                    RiskControlIndustryClassify.PUBLIC_UTILITIES.name(),
                    RiskControlIndustryClassify.CIVIL_CONSUMPTION.name()
            ));
            if(baseInfo.getRiskControlIndustryClassify() != null  && set.contains(baseInfo.getRiskControlIndustryClassify())) {
                ratingClientService.effectClientCheck(baseInfo.getEvaluationSubjectId(), baseInfo.getClientId());
            }
            // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
            ChangeDTO changeDTO = versionService.checkActualChange(req.getId());
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            projEstablishService.effect(req.getId());
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.PROJ_ESTABLISH.name());
        }
        PageR<CommonVersionListRSP> data = versionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ProjEstablishVersionDiffREQ req) {
        return R.ok(versionService.comparePreVersion(req.getId()));
    }

    @Override
    public R<ProjEstablishVersionDetailRSP> versionDetail(ProjEstablishVersionDetailREQ req) {
        ProjEstablishBaseInfoListRSP baseInfoRsp = baseInfoConverter.entityToDetailRSP(
                baseInfoLibService.getOne(Wrappers.<ProjEstablishBaseInfoLib>lambdaQuery()
                        .eq(ProjEstablishBaseInfoLib::getOriginId, req.getProjEstablishId())
                        .eq(ProjEstablishBaseInfoLib::getVersion, req.getVersion())));
        baseInfoService.join(baseInfoRsp);
        ProjEstablishPriceDetailRSP priceDetailRSP = priceService.versionDetail(req.getProjEstablishId(), req.getVersion());
        ProjEstablishVersionDetailRSP rsp = new ProjEstablishVersionDetailRSP().setVersion(req.getVersion())
                .setBaseInfo(baseInfoRsp).setPrice(priceDetailRSP);
        return R.ok(rsp);
    }
}