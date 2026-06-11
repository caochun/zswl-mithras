package cn.zswltech.mithras.application.orchestration.workflow.flow.file.focusfileselector;

import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeMaterialEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/2/17
 * @description 合同变更（其他）流程-关注文件筛选
 */
@Component
public class ContractModifyFlowFocusFileSelector extends AbstractContractFlowFocusFileSelector {
    @Override
    protected List<String> bizImportantFileTypes() {
        return Arrays.asList(
                ContractTypeEnum.MAIN_CONTRACT.name(),
                ContractTypeEnum.CONSULTING_CONTRACT.name(),
                ContractTypeEnum.GUARANTEE_CONTRACT.name(),
                ContractTypeEnum.MORTGAGE_CONTRACT.name(),
                ContractTypeEnum.PLEDGE_CONTRACT.name(),
                ContractChangeMaterialEnum.EXCHANGE_MATERIAL.name()
        );
    }

    @Override
    public ProcessModelTypeEnum processType() {
        return ProcessModelTypeEnum.ContractModifyFlow;
    }
}
