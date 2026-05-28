package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectStageRepaymentModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class DashboardProjectStageRepaymentExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectStageRepaymentModel> {
    @Override
    protected void customStrategy(Workbook workbook) {
        // 实现或扩展此方法以适应DashboardProjectStageRepayment的具体导出策略
    }

    @Override
    protected Class<DashboardProjectStageRepaymentModel> modelClz() {
        return DashboardProjectStageRepaymentModel.class;
    }
}