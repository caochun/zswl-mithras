package cn.zswltech.mithras.contract.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.contract.excel.model.ContractRentActualExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Component
public class ContractActualRentBLExcelExporter extends AbstractSimpleExcelExporter<ContractRentActualExcelModel> {
    @Override
    protected LinkedHashMap<String, String> getHeaderAliasMap() {
        LinkedHashMap<String, String> headerAlias = super.getHeaderAliasMap();
        headerAlias.put("rent", "应收保理款（元）");
        return headerAlias;
    }

    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractRentActualExcelModel> modelClz() {
        return ContractRentActualExcelModel.class;
    }
}
