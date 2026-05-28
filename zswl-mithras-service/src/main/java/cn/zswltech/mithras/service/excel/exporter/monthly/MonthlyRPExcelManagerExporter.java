package cn.zswltech.mithras.service.excel.exporter.monthly;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.monthly.MonthlyRPExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 月结管理 RP
 *
 */
@Component
public class MonthlyRPExcelManagerExporter extends AbstractSimpleExcelExporter<MonthlyRPExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<MonthlyRPExcelModel> modelClz() {
        return MonthlyRPExcelModel.class;
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
