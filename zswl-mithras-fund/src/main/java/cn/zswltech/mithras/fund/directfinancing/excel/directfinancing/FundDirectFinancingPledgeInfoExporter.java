package cn.zswltech.mithras.fund.directfinancing.excel.directfinancing;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 10:59
 */
@Component
public class FundDirectFinancingPledgeInfoExporter
        extends AbstractSimpleExcelExporter<NewFundDirectFinancingPledgeInfoExcelModel> {


    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<NewFundDirectFinancingPledgeInfoExcelModel> modelClz() {
        return NewFundDirectFinancingPledgeInfoExcelModel.class;
    }
}
