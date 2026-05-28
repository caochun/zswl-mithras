package cn.zswltech.mithras.service.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.LeaseLedgerManageExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 租赁物台账导出
 *
 * @author yangxiong
 * @since 2023-09-20
 */
@Component
public class LeaseLedgerManageExcelExporter extends AbstractSimpleExcelExporter<LeaseLedgerManageExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<LeaseLedgerManageExcelModel> modelClz() {
        return LeaseLedgerManageExcelModel.class;
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
