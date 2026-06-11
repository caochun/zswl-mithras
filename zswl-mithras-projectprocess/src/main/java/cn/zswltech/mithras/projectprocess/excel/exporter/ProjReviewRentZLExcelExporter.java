package cn.zswltech.mithras.projectprocess.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description 现金流计划导出（租金表）工具类
 */
@Primary
@Component
public class ProjReviewRentZLExcelExporter extends AbstractSimpleExcelExporter<CashFlowExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<CashFlowExcelModel> modelClz() {
        return CashFlowExcelModel.class;
    }
}
