package cn.zswltech.mithras.finance.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.finance.excel.model.FinanceAccountAgeItemCheckModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 14:25
 */
@Primary
@Component
public class FinanceAccountAgeItemExcelExporter extends AbstractSimpleExcelExporter<FinanceAccountAgeItemCheckModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<FinanceAccountAgeItemCheckModel> modelClz() {
        return FinanceAccountAgeItemCheckModel.class;
    }
}
