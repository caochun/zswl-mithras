package cn.zswltech.mithras.service.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.CreditSearchExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 征信查询 列表
 *
 */
@Component
public class CreditSearchListExcelExporter extends AbstractSimpleExcelExporter<CreditSearchExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<CreditSearchExcelModel> modelClz() {
        return CreditSearchExcelModel.class;
    }
}
