package cn.zswltech.mithras.finance.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.finance.excel.model.ProfitCalculateExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@Component
public class ProfitCalculateExcelExporter extends AbstractSimpleExcelExporter<ProfitCalculateExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ProfitCalculateExcelModel> modelClz() {
        return ProfitCalculateExcelModel.class;
    }
}
