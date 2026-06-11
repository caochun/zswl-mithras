package cn.zswltech.mithras.fund.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.fund.excel.model.FundFinancingRepayEstimateExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Primary
@Component
public class FundFinancingRepayEstimateExporter extends AbstractSimpleExcelExporter<FundFinancingRepayEstimateExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<FundFinancingRepayEstimateExcelModel> modelClz() {
        return FundFinancingRepayEstimateExcelModel.class;
    }
}
