package cn.zswltech.mithras.fund.directfinancing.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.fund.directfinancing.excel.model.PropertyExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author ylzhang5
 * @date 2025/12/15
 */
@Component
public class PropertyExcelManagerExporter extends AbstractSimpleExcelExporter<PropertyExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<PropertyExcelModel> modelClz() {
        return PropertyExcelModel.class;
    }
}
