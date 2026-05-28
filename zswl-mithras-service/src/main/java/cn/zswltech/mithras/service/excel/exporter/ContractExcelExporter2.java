package cn.zswltech.mithras.service.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.ContractExcelModel2;
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
public class ContractExcelExporter2 extends AbstractSimpleExcelExporter<ContractExcelModel2> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractExcelModel2> modelClz() {
        return ContractExcelModel2.class;
    }
}
