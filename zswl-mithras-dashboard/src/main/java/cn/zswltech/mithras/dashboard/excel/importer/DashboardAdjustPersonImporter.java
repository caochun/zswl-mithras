package cn.zswltech.mithras.dashboard.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardAdjustPersonModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardReviewModel;
import org.springframework.stereotype.Component;

@Component
public class DashboardAdjustPersonImporter extends AbstractSimpleExcelImporter<DashboardAdjustPersonModel> {
    @Override
    protected ExcelConfig config(ExcelReader excelReader) {
        return new ExcelConfig(0, 1, null);
    }

    @Override
    protected CellEditor getCellEditor() {
        return null;
    }

    @Override
    protected Class<DashboardAdjustPersonModel> modelClz() {
        return DashboardAdjustPersonModel.class;
    }
}
