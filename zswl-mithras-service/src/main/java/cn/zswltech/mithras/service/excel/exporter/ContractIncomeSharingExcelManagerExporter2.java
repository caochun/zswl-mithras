package cn.zswltech.mithras.service.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.ContractIncomeSharingExcelModel2;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author yupengfei
 * @date 2024/6/11 16:19
 */
@Component
public class ContractIncomeSharingExcelManagerExporter2 extends AbstractSimpleExcelExporter<ContractIncomeSharingExcelModel2> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractIncomeSharingExcelModel2> modelClz() {
        return ContractIncomeSharingExcelModel2.class;
    }
}
