package cn.zswltech.mithras.fund.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.fund.excel.model.FundFinancingRepayActualExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingRepayActualExporter extends AbstractSimpleExcelExporter<FundFinancingRepayActualExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<FundFinancingRepayActualExcelModel> modelClz() {
        return FundFinancingRepayActualExcelModel.class;
    }
}
