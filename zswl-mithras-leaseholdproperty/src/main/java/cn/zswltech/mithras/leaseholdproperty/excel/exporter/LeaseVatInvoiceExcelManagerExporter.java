package cn.zswltech.mithras.leaseholdproperty.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.leaseholdproperty.excel.model.LeaseVatInvoiceExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author yupengfei
 * @date 2024/5/13 19:38
 */
@Component
public class LeaseVatInvoiceExcelManagerExporter extends AbstractSimpleExcelExporter<LeaseVatInvoiceExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<LeaseVatInvoiceExcelModel> modelClz() {
        return LeaseVatInvoiceExcelModel.class;
    }
}
