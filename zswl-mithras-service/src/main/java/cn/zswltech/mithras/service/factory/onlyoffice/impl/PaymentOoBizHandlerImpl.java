package cn.zswltech.mithras.service.factory.onlyoffice.impl;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.dto.onlyoffice.DocDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentTypeEnum;
import cn.zswltech.mithras.system.onlyoffice.OoBizHandler;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 付款
 * 编辑预览
 */
@Component
public class PaymentOoBizHandlerImpl implements OoBizHandler {
    private static final String[] canEditJobList = {
            JobEnum.yunYingGuanLi.name(),
            JobEnum.headofyyglb.name(),
            JobEnum.yunYingGuanLiReview.name(),
            JobEnum.legalmanager.name(),
            JobEnum.operationManagement.name(),
            JobEnum.loanreviewpost.name(),
            JobEnum.headoflegalcompliance.name()
    };

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public void handle(DocDetailRSP docDetailRSP, MaterialsList materialsList) {
        // 付款模块的文件
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(materialsList.getBelongId());
        if (Objects.isNull(paymentBaseInfo)) {
            // 没关联到付款 不操作
            return;
        }
        ProcessResp processResp = findRelatedProcess(paymentBaseInfo.getId(), Collections.singletonList(ProcessModelTypeEnum.PaymentCreateFlow.name()));
        //  流程发起时，只有发起人可编辑。流程发起后，当前审批人和岗位人员可编辑
        if (BusinessModuleEnum.PAYMENT.name().equals(materialsList.getBusinessType()) && PaymentTypeEnum.LOAN_REVIEW.name().equals(materialsList.getMaterialsType())) {
//            if ((Objects.isNull(processResp) && Objects.equals(AccountUtil.getLoginInfo().getId(), materialsList.getCreateBy())) ||
//                    (Objects.nonNull(processResp)
//                            && Stream.of(processResp.getCurAssigneeIds().split(",")).map(Long::valueOf).filter(a -> AccountUtil.getLoginInfo().getId().equals(a)).count() > 0
//                            && !Objects.equals(AccountUtil.getLoginInfo().getId(), materialsList.getCreateBy())
//                            && (sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name()) ||
//                            sysUserService.currentUserIsSpecificJob(JobEnum.headofyyglb.name()) ||
//                            sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLiReview.name()) ||
//                            sysUserService.currentUserIsSpecificJob(JobEnum.legalmanager.name()) ||
//                            sysUserService.currentUserIsSpecificJob(JobEnum.operationManagement.name()) ||
//                            sysUserService.currentUserIsSpecificJob(JobEnum.loanreviewpost.name())) ||
//                            sysUserService.currentUserIsSpecificJob(JobEnum.headoflegalcompliance.name()))) {
//                docDetailRSP.getDocument().getPermissions().setEdit(true);
//                docDetailRSP.getEditorConfig().setMode("edit");
//            } else {
//                docDetailRSP.getDocument().getPermissions().setEdit(false);
//                docDetailRSP.getEditorConfig().setMode("view");
//            }
            if (Objects.isNull(processResp)) {
                // 仅发起人可编辑
                if (Objects.equals(AccountUtil.getLoginInfo().getId(), materialsList.getCreateBy())) {
                    docDetailRSP.getDocument().getPermissions().setEdit(true);
                    docDetailRSP.getEditorConfig().setMode("edit");
                } else {
                    docDetailRSP.getDocument().getPermissions().setEdit(false);
                    docDetailRSP.getEditorConfig().setMode("view");
                }
            } else {
                // 流程发起后当前审批人且对应复核岗位人员可编辑
                Long currentUserId = AccountUtil.getLoginInfo().getId();
                boolean jobHit = sysUserService.userIsSpecificJob(currentUserId, canEditJobList);
                boolean assigneeHit = this.isAssignee(currentUserId, processResp);
                if (jobHit && assigneeHit) {
                    docDetailRSP.getDocument().getPermissions().setEdit(true);
                    docDetailRSP.getEditorConfig().setMode("edit");
                } else {
                    docDetailRSP.getDocument().getPermissions().setEdit(false);
                    docDetailRSP.getEditorConfig().setMode("view");
                }
            }
        }

    }

    @Override
    public String getBusinessModule() {
        return BusinessModuleEnum.PAYMENT.name();
    }

    private ProcessResp findRelatedProcess(Long contractId, List<String> modelKeyList) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(contractId));
        processPageReq.setModelKeyList(modelKeyList);
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    private boolean isAssignee(Long userId, ProcessResp processResp) {
        String[] ids = processResp.getCurAssigneeIds().split(",");
        for (String id : ids) {
            if (Objects.equals(userId.toString(), id)) {
                return true;
            }
        }
        return false;
    }

}
