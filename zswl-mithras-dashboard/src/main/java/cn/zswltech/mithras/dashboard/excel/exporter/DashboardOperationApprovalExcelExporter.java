package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationApprovalModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardOperationApprovalExcelExporter extends AbstractSimpleExcelExporter<DashboardOperationApprovalModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardOperationApprovalModel> modelClz() {
        return DashboardOperationApprovalModel.class;
    }
}
