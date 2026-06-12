package cn.zswltech.mithras.application.orchestration.workflow.flow.file.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.workflow.flow.file.IFileHandler;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 租后调整
 *
 * @author wangchuanhao
 * @date 2022/11/23 11:18 AM
 */
@Component
public class AfterLeaseAdjustFileHandlerImpl implements IFileHandler {

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public void uploadCheck(ProcessResp processResp, String materialsType, String taskId) {
        fileTypeCheck(processResp, materialsType);
    }

    @Override
    public void removeCheck(ProcessResp processResp, MaterialsList materialsList, String taskId) {
        fileTypeCheck(processResp, materialsList.getMaterialsType());
    }

    @Override
    public List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList) {
        return materialsListService.list(BusinessModuleEnum.ADJUST.name(), materialsTypeList, Collections.singletonList(Long.valueOf(processResp.getBusinessKey())));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.ADJUST;
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return Optional.ofNullable(AfterLeaseAdjustMaterialsEnum.getByName(materialsType)).map(AfterLeaseAdjustMaterialsEnum::getDisplay).orElse(null);
    }

    private void fileTypeCheck(ProcessResp processResp, String materialsType) {
        AfterLeaseAdjustMaterialsEnum afterLeaseAdjustMaterialsEnum = AfterLeaseAdjustMaterialsEnum.getByName(materialsType);
        if (Objects.isNull(afterLeaseAdjustMaterialsEnum)) {
            // 不需要校验的类型
            return;
        }
        switch (afterLeaseAdjustMaterialsEnum) {
            // 这4个东西只能在审批中进行操作
            case RISK_REVIEW_REPORT:
                boolean uploadRiskFile = false;
                for (String s : processResp.getCurTaskActivityIds().split(",")) {
                    if (AfterLeaseAdjustMaterialsEnum.RISK_REVIEW_REPORT.getCanHandleActivityIdList().contains(s)) {
                        uploadRiskFile = true;
                        break;
                    }
                }
                Assert.isTrue(uploadRiskFile, () -> MithrasException.newException(ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.getDisplay() + "只能在租后调整—风控经理审批节点操作"));
                break;
            case LEGAL_COMPLIANCE_REPORT:
                boolean uploadLawFile = false;
                for (String s : processResp.getCurTaskActivityIds().split(",")) {
                    if (AfterLeaseAdjustMaterialsEnum.LEGAL_COMPLIANCE_REPORT.getCanHandleActivityIdList().contains(s)) {
                        uploadLawFile = true;
                        break;
                    }
                }
                Assert.isTrue(uploadLawFile, () -> MithrasException.newException(ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.getDisplay() + "只能在租后调整—法务经理审批节点操作"));
                break;
            case MEETING_REVIEW_REPORT:
                if (!AfterLeaseAdjustMaterialsEnum.MEETING_REVIEW_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(AfterLeaseAdjustMaterialsEnum.MEETING_REVIEW_REPORT.getDisplay() + "只能在租后调整—评审会汇票节点或租后调整-会议纪要审批汇票节点操作");
                }
                break;
            case MEETING_REVIEW_RECORD:
                if (!AfterLeaseAdjustMaterialsEnum.MEETING_REVIEW_RECORD.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(AfterLeaseAdjustMaterialsEnum.MEETING_REVIEW_RECORD.getDisplay() + "只能在租后调整—评审会汇票节点或租后调整-会议纪要审批汇票节点操作");
                }
                break;
            default:
                break;
        }
    }

}
