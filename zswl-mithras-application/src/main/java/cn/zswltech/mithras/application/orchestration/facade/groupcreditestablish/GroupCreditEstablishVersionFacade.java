package cn.zswltech.mithras.application.orchestration.facade.groupcreditestablish;

import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishVersionApplicationService;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishRatingCheckRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.version.GroupCreditEstablishEffectREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.version.GroupCreditEstablishVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.contract.enums.contract.ProjItemStatus;
import cn.zswltech.mithras.credit.groupcredit.establish.enums.GroupCreditEstablishProcessStatus;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.workflow.model.ProcessModifyRemark;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.ProcessModifyRemarkService;
import cn.zswltech.mithras.application.orchestration.client.ClientAuthorityService;
import cn.zswltech.mithras.application.orchestration.groupcredit.establish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.credit.application.groupcredit.establish.impl.GroupCreditEstablishService;
import cn.zswltech.mithras.credit.application.groupcredit.establish.impl.GroupCreditEstablishVersionServiceImpl;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @author wangchuanhao
 * @description 集团授信立项基本信息表
 * @date 2022-11-11
 */
@Service
public class GroupCreditEstablishVersionFacade implements GroupCreditEstablishVersionApplicationService {

    @Resource
    private GroupCreditEstablishVersionServiceImpl versionService;
    @Resource
    private GroupCreditEstablishService groupCreditEstablishService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;

    @Override
    public R<Void> effect(GroupCreditEstablishEffectREQ req) {
        // 加锁
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name(), req.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoService.getById(req.getId());

            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            if (Objects.equals(baseInfo.getGroupCreditEstablishStatus(), TAKE_EFFECT) && !Objects.equals(baseInfo.getGroupCreditEstablishProcessStatus(), GroupCreditEstablishProcessStatus.CHANGING_UN_SUBMIT.name())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            // 是否可提交 简单校验
            if (Objects.nonNull(groupCreditEstablishService.findRelatedProcess(req.getId()))) {
                throw new MithrasException("该立项数据变动处于流程中，无法提交数据");
            }
            if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(baseInfo.getClientId())) {
                throw new MithrasException("无所选客户管护权，无权进行操作");
            }
            groupCreditEstablishService.effectCheck(baseInfo);
            groupCreditEstablishBaseInfoService.checkClientRating(baseInfo);

            // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
            ChangeDTO changeDTO = versionService.checkActualChange(req.getId());
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            if (!ProjItemStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditEstablishStatus())) {
                groupCreditEstablishService.effect(req.getId());
            } else if (!req.getOnlyCheck()) {
                req.getRemarkAddREQ().check();
                getBean(ProcessModifyRemarkService.class).saveOrUpdateByKey(copyProperties(req.getRemarkAddREQ(), ProcessModifyRemark.class));
                groupCreditEstablishService.effect(req.getId());
            }
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name());
        }
        PageR<CommonVersionListRSP> data = versionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(GroupCreditEstablishVersionDiffREQ req) {
        return R.ok(versionService.comparePreVersion(req.getId()));
    }

    @Override
    public R<GroupCreditEstablishRatingCheckRSP> checkRatingInfo(SinglePkREQ req) {
        return R.ok(groupCreditEstablishBaseInfoService.checkRatingInfo(req.getId()));
    }

}