package cn.zswltech.mithras.credit.application.groupcredit.establish.impl;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.credit.groupcredit.establish.enums.GroupCreditEstablishProcessStatus;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.time.LocalDateTime;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ONLY_BIZ_DEPT_DO;

/**
 * @author wangchuanhao
 * @description GroupCreditEstablish更新后执行的切面操作
 * @date 2022-11-14
 */
public interface GroupCreditEstablishUpdateAdvice {

    default void saveCheck(Long groupCreditEstablishId) {
        GroupCreditEstablishService groupCreditEstablishService = SpringContextHolder.getBean(GroupCreditEstablishService.class);
        GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper = SpringContextHolder.getBean(GroupCreditReviewBaseInfoMapper.class);
        SysUserService sysUserService = SpringContextHolder.getBean(SysUserService.class);
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        if (Objects.isNull(groupCreditEstablishId)) {
            throw new MithrasException("立项信息不存在，不可修改数据");
        }
        if (!groupCreditEstablishService.canSave(groupCreditEstablishId)) {
            throw new MithrasException("立项信息处于审批流程中，不可修改数据");
        }
        GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoMapper.selectOne(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                .eq(GroupCreditReviewBaseInfo::getGroupCreditEstablishId, groupCreditEstablishId)
                .notIn(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .last("LIMIT 1")
        );
        if (Objects.nonNull(groupCreditReviewBaseInfo)) {
            throw new MithrasException("集团授信立项已被评审模块引用，不可修改");
        }
    }

    default void recordStatus(Long groupCreditEstablishId) {
        GroupCreditEstablishService groupCreditEstablishService = SpringContextHolder.getBean(GroupCreditEstablishService.class);
        GroupCreditEstablishBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(GroupCreditEstablishBaseInfoMapper.class);
        ProcessResp processResp = groupCreditEstablishService.findRelatedProcess(groupCreditEstablishId);
        GroupCreditEstablishBaseInfo baseInfo = baseInfoMapper.selectById(groupCreditEstablishId);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或状态为新建时 只更新最后更新时间 不更新客户状态
        if (RecordStatus.NEW.name().equals(baseInfo.getGroupCreditEstablishStatus()) || Objects.nonNull(processResp)) {
            // 只更新 最后更新时间
            LambdaUpdateWrapper<GroupCreditEstablishBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(GroupCreditEstablishBaseInfo::getId, baseInfo.getId());
            updateWrapper.set(GroupCreditEstablishBaseInfo::getUpdateTime, LocalDateTime.now());
            baseInfoMapper.update(null, updateWrapper);
            return;
        }
        groupCreditEstablishService.recordEstablishStatus(groupCreditEstablishId, null, GroupCreditEstablishProcessStatus.CHANGING_UN_SUBMIT);
    }

}
