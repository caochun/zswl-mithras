package cn.zswltech.mithras.capital.excel.exporter;

import cn.zswltech.mithras.capital.excel.model.BusinessFlowFundPaymentModel;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
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
