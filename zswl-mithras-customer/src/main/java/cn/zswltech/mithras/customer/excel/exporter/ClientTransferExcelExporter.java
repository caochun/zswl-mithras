package cn.zswltech.mithras.customer.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.customer.excel.ClientTransferExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * ocr车证导出
 *
 * @author yangxiong
 * @since 2023-09-20
 */
@Component
public class ClientTransferExcelExporter extends AbstractSimpleExcelExporter<ClientTransferExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ClientTransferExcelModel> modelClz() {
        return ClientTransferExcelModel.class;
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
