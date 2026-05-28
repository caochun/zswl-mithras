package cn.zswltech.mithras.service.controller.projpricing;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.projpricing.ProjPricingVersionApi;
import cn.zswltech.mithras.dto.projpricing.ProjPricingEffectREQ;
import cn.zswltech.mithras.dto.projpricing.ProjPricingVersionDiffREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingCompareREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.ProcessModifyRemark;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.ProcessModifyRemarkService;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.client.ClientTransferService;
import cn.zswltech.mithras.service.service.lib.projpricing.impl.ProjPricingVersionServiceImpl;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.constant.MithrasConstants.ERR_IN_TRANSFER;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.enums.contract.ProjItemStatus.TAKE_EFFECT;
import static cn.zswltech.mithras.service.others.MithrasException.err;


@RestController
public class ProjPricingVersionController implements ProjPricingVersionApi {

    @Resource
    private ProjPricingVersionServiceImpl projPricingVersionService;
    @Resource
    private ProjPricingService projPricingService;
    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Autowired
    private ProjPricingBaseInfoService baseInfoService;

    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ClientTransferService clientTransferService;
    
    
    @Override
    public R<Void> effect(ProjPricingEffectREQ req) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.PROJ_PRICING.name(), req.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            ProjPricingBaseInfo baseInfo = baseInfoService.getById(req.getId());
            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            if (clientTransferService.inTransfer(baseInfo.getClientId())) {
                err(ERR_IN_TRANSFER);
            }
            if (RecordStatus.CLOSED.name().equals(baseInfo.getProjPricingStatus())) {
                throw new MithrasException("定价已关闭，不能提交审核");
            }
            if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(baseInfo.getClientId())) {
                throw new MithrasException("无所选客户管护权，无权进行操作");
            }
            projPricingService.pricingApprovalCheck(baseInfo);
            List<ProcessResp> relatedProcess = projPricingService.findRelatedProcesses(req.getId());
            // 判断是否存在相关定价流程
            if (ObjectUtil.isNotEmpty(relatedProcess)) {
                for (ProcessResp process : relatedProcess) {
                    ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.valueOf(process.getModelKey());
                    if (ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.equals(processModelTypeEnum)
                            || ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.equals(processModelTypeEnum)){
                        throw new MithrasException(String.format("项目定价已处于'%s'中，提交审批失败", processModelTypeEnum.getDisplay()));
                    }
                }
            }
//            ChangeDTO changeDTO = projPricingVersionService.checkPricingChange(req.getId());
//            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
//                throw new MithrasException("数据未变动，无需提交数据");
//            }

            if (!TAKE_EFFECT.name().equals(baseInfo.getProjPricingStatus())) {
                projPricingService.effect(req.getId());
            }else if(!req.getOnlyCheck()){
                req.getRemarkAddREQ().check();
                getBean(ProcessModifyRemarkService.class).saveOrUpdateByKey(copyProperties(req.getRemarkAddREQ(), ProcessModifyRemark.class));
                projPricingService.effect(req.getId());
            }
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.PROJ_PRICING.name());
        }
        PageR<CommonVersionListRSP> data = projPricingVersionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ProjPricingVersionDiffREQ req) {
        return R.ok(projPricingVersionService.comparePreVersion(req.getId()));
    }

    @Override
    public R<Map<String, DiffValue>> projPricingReviewBaseInfoCompare(ProjPricingCompareREQ req) {
        return R.ok(projPricingVersionService.projPricingReviewBaseInfoCompare(req));
    }

    @Override
    public R<Map<String, DiffValue>> projPricingReviewPriceCompare(ProjPricingCompareREQ req) {
        return R.ok(projPricingVersionService.projPricingReviewPriceCompare(req));
    }

    @Override
    public R<List<Map<String, DiffValue>>> projPricingReviewCashFlowPlanCompare(ProjPricingCompareREQ req) {
        return R.ok(projPricingVersionService.projPricingReviewCashFlowPlanCompare(req));
    }
}