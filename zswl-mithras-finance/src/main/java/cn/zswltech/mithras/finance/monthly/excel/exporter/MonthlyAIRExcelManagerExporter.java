package cn.zswltech.mithras.finance.monthly.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.finance.monthly.excel.model.MonthlyAIRExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 月结管理 AIR
 *
 */
@Component
public class MonthlyAIRExcelManagerExporter extends AbstractSimpleExcelExporter<MonthlyAIRExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<MonthlyAIRExcelModel> modelClz() {
        return MonthlyAIRExcelModel.class;
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
