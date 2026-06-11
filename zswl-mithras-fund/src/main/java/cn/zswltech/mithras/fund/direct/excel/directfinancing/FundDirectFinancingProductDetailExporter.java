package cn.zswltech.mithras.fund.direct.excel.directfinancing;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 11:01
 */
@Component
public class FundDirectFinancingProductDetailExporter extends AbstractSimpleExcelExporter<FundDirectFinancingProductDetailExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<FundDirectFinancingProductDetailExcelModel> modelClz() {
        return FundDirectFinancingProductDetailExcelModel.class;
    }
}
