package cn.zswltech.mithras.service.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 * @author dingqi
 * @date 2022/10/27
 * @description
 */
@Primary
@Component
public class ProjPricingRentZRExcelExporter extends AbstractSimpleExcelExporter<CashFlowExcelModel> {
    @Override
    protected LinkedHashMap<String, String> getHeaderAliasMap() {
        LinkedHashMap<String, String> headerAlias = super.getHeaderAliasMap();
        headerAlias.put("rent", "回收款（元）");
        return headerAlias;
    }

    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<CashFlowExcelModel> modelClz() {
        return CashFlowExcelModel.class;
    }
}
