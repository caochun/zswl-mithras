package cn.zswltech.mithras.leaseholdproperty.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.leaseholdproperty.excel.model.LeaseItemVehicleRegistrationCertificateExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * ocr车证导出
 *
 * @author yangxiong
 * @since 2023-09-20
 */
@Component
public class LeaseItemVehicleRegistrationCertificateExcelExporter extends AbstractSimpleExcelExporter<LeaseItemVehicleRegistrationCertificateExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<LeaseItemVehicleRegistrationCertificateExcelModel> modelClz() {
        return LeaseItemVehicleRegistrationCertificateExcelModel.class;
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
