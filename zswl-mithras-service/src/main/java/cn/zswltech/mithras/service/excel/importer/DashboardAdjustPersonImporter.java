package cn.zswltech.mithras.service.excel.importer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardAdjustPersonModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardReviewModel;
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
