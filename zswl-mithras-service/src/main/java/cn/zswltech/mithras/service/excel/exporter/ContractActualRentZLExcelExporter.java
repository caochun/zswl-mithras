package cn.zswltech.mithras.service.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.ContractRentActualExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/8/29
 * @description
 */
@Component
public class ContractActualRentZLExcelExporter extends AbstractSimpleExcelExporter<ContractRentActualExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractRentActualExcelModel> modelClz() {
        return ContractRentActualExcelModel.class;
    }
}
