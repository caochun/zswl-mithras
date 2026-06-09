package cn.zswltech.mithras.service.fund.direct.excel;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundDirectFinancingRepayActualExporter
        extends AbstractSimpleExcelExporter<FundDirectFinancingRepayActualExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<FundDirectFinancingRepayActualExcelModel> modelClz() {
        return FundDirectFinancingRepayActualExcelModel.class;
    }
}
