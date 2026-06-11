package cn.zswltech.mithras.filingmaterials.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.filingmaterials.excel.model.OtherFilingLedgerManageExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 其他资料归档导出
 *
 */
@Component
public class OtherFilingLedgerManageExcelExporter extends AbstractSimpleExcelExporter<OtherFilingLedgerManageExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<OtherFilingLedgerManageExcelModel> modelClz() {
        return OtherFilingLedgerManageExcelModel.class;
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
