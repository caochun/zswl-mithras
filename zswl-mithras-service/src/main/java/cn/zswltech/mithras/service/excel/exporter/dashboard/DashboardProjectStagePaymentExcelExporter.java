package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectStagePaymentModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class DashboardProjectStagePaymentExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectStagePaymentModel> {
    @Override
    protected void customStrategy(Workbook workbook) {
        // 实现或扩展此方法以适应DashboardProjectStagePayment的具体导出策略
    }

    @Override
    protected Class<DashboardProjectStagePaymentModel> modelClz() {
        return DashboardProjectStagePaymentModel.class;
    }
}