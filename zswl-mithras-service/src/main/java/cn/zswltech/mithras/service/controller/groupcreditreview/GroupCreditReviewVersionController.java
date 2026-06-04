package cn.zswltech.mithras.service.controller.groupcreditreview;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewVersionApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewRatingCheckRSP;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewEffectREQ;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ProjItemStatus;
import cn.zswltech.mithras.credit.domain.groupcredit.review.enums.GroupCreditReviewProcessStatus;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.ProcessModifyRemark;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.ProcessModifyRemarkService;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewService;
import cn.zswltech.mithras.credit.application.groupcredit.review.service.impl.GroupCreditReviewVersionServiceImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.EXPIRE;

/**
 * @author wangchuanhao
 * @description 集团授信评审基本信息表
 * @date 2022-11-11
 */
@RestController
public class GroupCreditReviewVersionController implements GroupCreditReviewVersionApi {

    @Resource
    private GroupCreditReviewVersionServiceImpl versionService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;


    @Override
    public R<Void> effect(GroupCreditReviewEffectREQ req) {
        // 加锁
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.GROUP_CREDIT_REVIEW.name(), req.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoService.getById(req.getId());

            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            if (CLOSED.name().equals(baseInfo.getGroupCreditReviewStatus()) || EXPIRE.name().equals(baseInfo.getGroupCreditReviewStatus())) {
                throw new MithrasException("评审已关闭，不能提交审核");
            }
            if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(baseInfo.getClientId())) {
                throw new MithrasException("无所选客户管护权，无权进行操作");
            }
            if (Objects.equals(baseInfo.getGroupCreditReviewStatus(), RecordStatus.TAKE_EFFECT) && !Objects.equals(baseInfo.getGroupCreditReviewProcessStatus(), GroupCreditReviewProcessStatus.CHANGING_UN_SUBMIT.name())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            // 是否可提交 简单校验
            if (Objects.nonNull(groupCreditReviewService.findRelatedProcess(req.getId()))) {
                throw new MithrasException("该评审数据变动处于流程中，无法提交数据");
            }
            groupCreditReviewService.effectCheck(baseInfo);
            groupCreditReviewBaseInfoService.checkClientRating(baseInfo);

            // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
            ChangeDTO changeDTO = versionService.checkActualChange(req.getId());
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            if (!ProjItemStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditReviewStatus())) {
                groupCreditReviewService.effect(req.getId());
            } else if (!req.getOnlyCheck()) {
                req.getRemarkAddREQ().check();
                getBean(ProcessModifyRemarkService.class).saveOrUpdateByKey(copyProperties(req.getRemarkAddREQ(), ProcessModifyRemark.class));
                groupCreditReviewService.effect(req.getId());
            }
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.GROUP_CREDIT_REVIEW.name());
        }
        PageR<CommonVersionListRSP> data = versionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(GroupCreditReviewVersionDiffREQ req) {
        return R.ok(versionService.comparePreVersion(req.getId()));
    }

    @Override
    public R<GroupCreditReviewRatingCheckRSP> checkRatingInfo(SinglePkREQ req) {
        return R.ok(groupCreditReviewBaseInfoService.checkRatingInfo(req.getId()));
    }

}