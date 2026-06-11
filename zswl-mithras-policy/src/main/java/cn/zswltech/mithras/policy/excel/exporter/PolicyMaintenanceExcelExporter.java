package cn.zswltech.mithras.policy.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.policy.excel.model.PolicyMaintenanceExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 保单台账列表
 *
 */
@Component
public class PolicyMaintenanceExcelExporter extends AbstractSimpleExcelExporter<PolicyMaintenanceExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<PolicyMaintenanceExcelModel> modelClz() {
        return PolicyMaintenanceExcelModel.class;
    }

    @Override
    protected boolean writeHead() {
        return true;
    }

    @Override
    protected int startRow() {
        return 1;
    }
}
