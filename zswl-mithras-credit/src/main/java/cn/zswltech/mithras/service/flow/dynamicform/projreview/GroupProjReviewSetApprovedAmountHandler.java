package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.map.MapUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author bigbear
 * @date 2024/10/29 16:31
 * @description
 */
@Slf4j
@Component
public class GroupProjReviewSetApprovedAmountHandler implements DynamicFormHandler {

    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        DynamicFormHandler.super.check(formMap, taskResp, userTaskExt);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        GroupCreditReviewBaseInfo creditReviewBaseInfo = groupCreditReviewBaseInfoMapper.selectById(Long.valueOf(taskResp.getBusinessKey()));
        if (Objects.isNull(creditReviewBaseInfo)) {
            log.error("<集团授信评审>数据不存在[id:{}]", taskResp.getBusinessKey());
            throw MithrasException.newException("[集团授信信息]数据不存在");
        }
        Map<String, String> map = (Map<String, String>) formMap.get(getType().name());
        if (Objects.isNull(map)) {
            log.error("<集团授信评审>表单数据不存在[id:{}]", taskResp.getBusinessKey());
            throw MithrasException.newException("[集团授信信息-批复金额]数据不能为空");
        }
        creditReviewBaseInfo.setProjectApprovalAmount(Long.valueOf(map.get("approvedAmount")));
        groupCreditReviewBaseInfoMapper.updateById(creditReviewBaseInfo);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        GroupCreditReviewBaseInfo creditReviewBaseInfo = groupCreditReviewBaseInfoMapper.selectById(Long.valueOf(rsp.getBusinessKey()));
        if (Objects.isNull(creditReviewBaseInfo)) {
            log.error("<集团授信评审>数据不存在[id:{}]", rsp.getBusinessKey());
            throw MithrasException.newException("[集团授信信息]数据不存在");
        }
        rsp.getDynamicFormData().put(getType().name(), MapUtil.builder().put("approvedAmount", creditReviewBaseInfo.getProjectApprovalAmount()).build());
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.group_projReview_setApprovedAmount;
    }
}
