package cn.zswltech.mithras.application.orchestration.groupcredit.review;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.credit.groupcredit.review.enums.GroupCreditReviewProcessStatus;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.time.LocalDateTime;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ONLY_BIZ_DEPT_DO;

/**
 * @author wangchuanhao
 * @description GroupCreditReview更新后执行的切面操作
 * @date 2022-11-14
 */
public interface GroupCreditReviewUpdateAdvice {

    default void saveCheck(Long groupCreditReviewId) {
        GroupCreditReviewService groupCreditReviewService = SpringContextHolder.getBean(GroupCreditReviewService.class);
        ProjReviewBaseInfoMapper projReviewBaseInfoMapper = SpringContextHolder.getBean(ProjReviewBaseInfoMapper.class);
        SysUserService sysUserService = SpringContextHolder.getBean(SysUserService.class);
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        if (Objects.isNull(groupCreditReviewId)) {
            throw new MithrasException("评审信息不存在，不可修改数据");
        }
        if (!groupCreditReviewService.canSave(groupCreditReviewId)) {
            throw new MithrasException("评审信息处于审批流程中，不可修改数据");
        }
        ProjReviewBaseInfo relationProjReviewBaseInfo = projReviewBaseInfoMapper.selectOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getRelationDataType, ReviewRelationDataType.GROUP_CREDIT_REVIEW.name())
                .eq(ProjReviewBaseInfo::getGroupCreditReviewId, groupCreditReviewId)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_REJECT.name())
                .last("LIMIT 1")
        );
        if (Objects.nonNull(relationProjReviewBaseInfo)) {
            throw new MithrasException("该授信评审已有客户发起用信，不可修改");
        }
    }

    default void recordStatus(Long groupCreditReviewId) {
        GroupCreditReviewService groupCreditReviewService = SpringContextHolder.getBean(GroupCreditReviewService.class);
        GroupCreditReviewBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(GroupCreditReviewBaseInfoMapper.class);
        ProcessResp processResp = groupCreditReviewService.findRelatedProcess(groupCreditReviewId);
        GroupCreditReviewBaseInfo baseInfo = baseInfoMapper.selectById(groupCreditReviewId);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或状态为新建时 只更新最后更新时间 不更新客户状态
        if (RecordStatus.NEW.name().equals(baseInfo.getGroupCreditReviewStatus()) || Objects.nonNull(processResp)) {
            // 只更新 最后更新时间
            LambdaUpdateWrapper<GroupCreditReviewBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(GroupCreditReviewBaseInfo::getId, baseInfo.getId());
            updateWrapper.set(GroupCreditReviewBaseInfo::getUpdateTime, LocalDateTime.now());
            baseInfoMapper.update(null, updateWrapper);
            return;
        }
        groupCreditReviewService.recordReviewStatus(groupCreditReviewId, null, GroupCreditReviewProcessStatus.CHANGING_UN_SUBMIT);
    }

}
