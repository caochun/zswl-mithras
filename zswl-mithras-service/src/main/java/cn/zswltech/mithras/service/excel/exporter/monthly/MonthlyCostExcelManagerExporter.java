package cn.zswltech.mithras.service.excel.exporter.monthly;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.monthly.MonthlyCostExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 月结管理 印花税-资金端
 *
 */
@Component
public class MonthlyCostExcelManagerExporter extends AbstractSimpleExcelExporter<MonthlyCostExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<MonthlyCostExcelModel> modelClz() {
        return MonthlyCostExcelModel.class;
    }

    @Override
    protected boolean writeHead() {
        return true;
    }

    @Override
    protected int startRow() {
        return 1;
    }
}
