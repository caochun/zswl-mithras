package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.map.MapUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author bigbear
 * @date 2024/10/29 13:38
 * @description
 */
@Slf4j
@Component
public class ProjReviewSetApprovedAmountHandler implements DynamicFormHandler {

    @Resource
    private ProjReviewBaseInfoService baseInfoService;
    @Resource
    private ProjReviewAocPriceService aocPriceService;
    @Resource
    private ProjReviewLeasePriceService leasePriceService;
    @Resource
    private ProjReviewFactoringPriceService factoringPriceService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        log.info("projReview_setApprovedAmount check.......");
        DynamicFormHandler.super.check(formMap, taskResp, userTaskExt);
        Map<String, String> map = (Map<String, String>) formMap.get(getType().name());
        if (MapUtil.isEmpty(map)) {
            // 不存在当前表单的情况下直接跳过
            return;
        }
        if (Objects.isNull(map.get("approvedAmount"))) {
            throw MithrasException.newException("请输入项目批复金额");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        log.info("projReview_setApprovedAmount handle.......");
        Map<String, String> map = (Map<String, String>) formMap.get(getType().name());
        Long projectId = Long.valueOf(taskResp.getBusinessKey());
        // 理论上一个评审只有一种报价方案，所以全部类型都更新一遍的情况下是不影响的
        aocPriceService.lambdaUpdate()
                .set(ProjReviewAocPrice::getProjectApprovalAmount, map.get("approvedAmount"))
                .eq(ProjReviewAocPrice::getProjectId, projectId)
                .update();
        leasePriceService.lambdaUpdate()
                .set(ProjReviewLeasePrice::getProjectApprovalAmount, map.get("approvedAmount"))
                .eq(ProjReviewLeasePrice::getProjectId, projectId)
                .update();

        factoringPriceService.lambdaUpdate()
                .set(ProjReviewFactoringPrice::getProjectApprovalAmount, map.get("approvedAmount"))
                .eq(ProjReviewFactoringPrice::getProjectId, projectId)
                .update();
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        log.info("projReview_setApprovedAmount collect.......");
        ProjReviewBaseInfo projReviewBaseInfo = baseInfoService.getById(Long.valueOf(rsp.getBusinessKey()));
        if (Objects.isNull(projReviewBaseInfo)) {
            throw MithrasException.newException("项目不存在");
        }
        // 找到报价方案
        ProjReviewLeasePrice leasePrice = leasePriceService.getByProjectId(projReviewBaseInfo.getId());
        if (Objects.nonNull(leasePrice)) {
            rsp.getDynamicFormData().put(getType().name(), MapUtil.builder("approvedAmount", leasePrice.getProjectApprovalAmount()).build());
            return;
        }
        ProjReviewAocPrice aocPrice = aocPriceService.getByProjectId(projReviewBaseInfo.getId());
        if (Objects.nonNull(aocPrice)) {
            rsp.getDynamicFormData().put(getType().name(), MapUtil.builder("approvedAmount", aocPrice.getProjectApprovalAmount()).build());
            return;
        }
        ProjReviewFactoringPrice factoringPrice = factoringPriceService.getByProjectId(projReviewBaseInfo.getId());
        if (Objects.nonNull(factoringPrice)) {
            rsp.getDynamicFormData().put(getType().name(), MapUtil.builder("approvedAmount", factoringPrice.getProjectApprovalAmount()).build());
            return;
        }
        throw MithrasException.newException("项目不存在报价方案");
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setApprovedAmount;
    }
}
