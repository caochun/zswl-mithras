package cn.zswltech.mithras.application.orchestration.workflow.flow.file.impl;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.payment.enums.LendingMaterialType;
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
 * 付款申请
 *
 * @author wangchuanhao
 * @date 2022/11/23 12:59 PM
 */
@Component
public class PaymentFileHandlerImpl implements IFileHandler {

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
        return materialsListService.list(BusinessModuleEnum.PAYMENT.name(), materialsTypeList, Collections.singletonList(Long.valueOf(processResp.getBusinessKey())));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.PAYMENT;
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return Optional.ofNullable(LendingMaterialType.getByName(materialsType)).map(l -> l.display).orElse(null);
    }

    private void fileTypeCheck(ProcessResp processResp, String materialsType) {
        LendingMaterialType lendingMaterialType = LendingMaterialType.getByName(materialsType);
        if (Objects.isNull(lendingMaterialType)) {
            // 不需要校验的类型
            return;
        }
        switch (lendingMaterialType) {
            case LOAN_APPROVAL:
                if (!"userTask_loanReviewPost".equals(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(LendingMaterialType.LOAN_APPROVAL.display() + "只能在付款申请—放款审核岗审批节点操作");
                }
                break;
            default:
                break;
        }
    }
}
