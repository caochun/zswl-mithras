package cn.zswltech.mithras.contract.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.contract.excel.model.ContractExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 14:25
 */
@Primary
@Component
public class ContractExcelExporter extends AbstractSimpleExcelExporter<ContractExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractExcelModel> modelClz() {
        return ContractExcelModel.class;
    }
}
