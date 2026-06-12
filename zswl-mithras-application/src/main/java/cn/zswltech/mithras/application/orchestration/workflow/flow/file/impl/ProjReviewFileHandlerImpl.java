package cn.zswltech.mithras.application.orchestration.workflow.flow.file.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
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
 * 项目评审
 *
 * @author wangchuanhao
 * @date 2022/11/23 11:18 AM
 */
@Component
public class ProjReviewFileHandlerImpl implements IFileHandler {

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
        return materialsListService.list(BusinessModuleEnum.PROJ_REVIEW.name(), materialsTypeList, Collections.singletonList(Long.valueOf(processResp.getBusinessKey())));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.PROJ_REVIEW;
    }
    
    private void fileTypeCheck(ProcessResp processResp, String materialsType) {
        ProjReviewMaterialsEnum projReviewMaterialsEnum = ProjReviewMaterialsEnum.getByName(materialsType);
        if (Objects.isNull(projReviewMaterialsEnum)) {
            // 不需要校验的类型
            return;
        }
        switch (projReviewMaterialsEnum) {
            // 这4个东西只能在审批中进行操作
            case RISK_REVIEW_REPORT:
                boolean uploadRiskFile = false;
                for (String s : processResp.getCurTaskActivityIds().split(",")) {
                    if (ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.getCanHandleActivityIdList().contains(s)) {
                        uploadRiskFile = true;
                        break;
                    }
                }
                Assert.isTrue(uploadRiskFile, () -> MithrasException.newException(ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.getDisplay() + "只能在项目评审—风控经理审批节点操作"));
                break;
            case LEGAL_COMPLIANCE_REPORT:
                boolean uploadLawFile = false;
                for (String s : processResp.getCurTaskActivityIds().split(",")) {
                    if (ProjReviewMaterialsEnum.LEGAL_COMPLIANCE_REPORT.getCanHandleActivityIdList().contains(s)) {
                        uploadLawFile = true;
                        break;
                    }
                }
                Assert.isTrue(uploadLawFile, () -> MithrasException.newException(ProjReviewMaterialsEnum.LEGAL_COMPLIANCE_REPORT.getDisplay() + "只能在项目评审—法务经理审批、法务经理复核节点操作"));
                break;
//            case YIELD_REVIEW_REPORT:
//                if (!ProjReviewMaterialsEnum.YIELD_REVIEW_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
//                    throw new MithrasException(ProjReviewMaterialsEnum.YIELD_REVIEW_REPORT.getDisplay() + "只能在项目评审—财务主管审批节点操作");
//                }
//                break;
            case MEETING_REVIEW_REPORT:
                if (!ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.getDisplay() + "只能在项目评审—评审会汇票节点或项目评审-会议纪要审批汇票节点操作");
                }
                break;
            case MEETING_REVIEW_RECORD:
                if (!ProjReviewMaterialsEnum.MEETING_REVIEW_RECORD.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(ProjReviewMaterialsEnum.MEETING_REVIEW_RECORD.getDisplay() + "只能在项目评审—评审会汇票节点或项目评审-会议纪要审批汇票节点操作");
                }
                break;
            case GMO_MEETING_MINUTES:
                if (!ProjReviewMaterialsEnum.GMO_MEETING_MINUTES.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(ProjReviewMaterialsEnum.GMO_MEETING_MINUTES.getDisplay() + "只能在项目定价审批—总经办秘书审批节点操作");
                }
                break;
            case DIRECTOR_MEETING_REPORT:
                if (!ProjReviewMaterialsEnum.DIRECTOR_MEETING_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(ProjReviewMaterialsEnum.DIRECTOR_MEETING_REPORT.getDisplay() + "只能在项目评审—董事会秘书汇票操作");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return Optional.ofNullable(ProjReviewMaterialsEnum.getByName(materialsType)).map(ProjReviewMaterialsEnum::getDisplay).orElse(null);
    }

}
