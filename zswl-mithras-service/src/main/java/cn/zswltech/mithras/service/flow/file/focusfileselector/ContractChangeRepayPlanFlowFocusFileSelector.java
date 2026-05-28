package cn.zswltech.mithras.service.flow.file.focusfileselector;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/2/17
 * @description 合同调整还款计划流程-关注文件筛选
 */
@Component
public class ContractChangeRepayPlanFlowFocusFileSelector extends AbstractContractFlowFocusFileSelector {
    @Override
    protected List<String> bizImportantFileTypes() {
        return Arrays.asList(
                ContractTypeEnum.MAIN_CONTRACT.name(),
                ContractTypeEnum.CONSULTING_CONTRACT.name(),
                ContractTypeEnum.GUARANTEE_CONTRACT.name(),
                ContractTypeEnum.MORTGAGE_CONTRACT.name(),
                ContractTypeEnum.PLEDGE_CONTRACT.name(),
                ContractExtraFileTypeEnum.CHANGE.name()
        );
    }

    @Override
    public ProcessModelTypeEnum processType() {
        return ProcessModelTypeEnum.ContractChangeRepayPlanFlow;
    }
}
