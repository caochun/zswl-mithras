package cn.zswltech.mithras.contract.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.contract.excel.model.ContractIncomeSharingExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author yupengfei
 * @date 2024/6/11 16:19
 */
@Component
public class ContractIncomeSharingExcelManagerExporter extends AbstractSimpleExcelExporter<ContractIncomeSharingExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractIncomeSharingExcelModel> modelClz() {
        return ContractIncomeSharingExcelModel.class;
    }
}
