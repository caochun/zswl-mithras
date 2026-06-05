package cn.zswltech.mithras.monthly.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.monthly.excel.model.MonthlyDutyFinExcelModel;
import cn.zswltech.mithras.monthly.excel.model.MonthlyDutyProjExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 月结管理 印花税-资金端
 *
 */
@Component
public class MonthlyDutyFinExcelManagerExporter extends AbstractSimpleExcelExporter<MonthlyDutyFinExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<MonthlyDutyFinExcelModel> modelClz() {
        return MonthlyDutyFinExcelModel.class;
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
