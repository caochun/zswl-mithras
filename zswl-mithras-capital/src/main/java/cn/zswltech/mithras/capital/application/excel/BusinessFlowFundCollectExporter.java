package cn.zswltech.mithras.capital.application.excel;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 10:59
 */
@Component
public class BusinessFlowFundCollectExporter
        extends AbstractSimpleExcelExporter<BusinessFlowFundCollectModel> {


    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<BusinessFlowFundCollectModel> modelClz() {
        return BusinessFlowFundCollectModel.class;
    }
}
