package cn.zswltech.mithras.capital.application.excel;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 10:59
 */
@Component
public class BusinessFlowFundPaymentExporter
        extends AbstractSimpleExcelExporter<BusinessFlowFundPaymentModel> {


    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<BusinessFlowFundPaymentModel> modelClz() {
        return BusinessFlowFundPaymentModel.class;
    }
}
