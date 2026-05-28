package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationApprovalModel;
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
