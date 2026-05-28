package cn.zswltech.mithras.service.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.ClientTransferExcelModel;
import cn.zswltech.mithras.service.excel.model.LeaseItemVehicleRegistrationCertificateExcelModel;
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
