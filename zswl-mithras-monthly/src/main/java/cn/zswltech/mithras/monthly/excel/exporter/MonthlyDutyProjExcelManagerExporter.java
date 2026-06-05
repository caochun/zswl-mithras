package cn.zswltech.mithras.monthly.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.monthly.excel.model.MonthlyDutyProjExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 月结管理 印花税-项目端
 *
 */
@Component
public class MonthlyDutyProjExcelManagerExporter extends AbstractSimpleExcelExporter<MonthlyDutyProjExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<MonthlyDutyProjExcelModel> modelClz() {
        return MonthlyDutyProjExcelModel.class;
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
