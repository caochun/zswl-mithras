package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectStageEstablishModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class DashboardProjectStageEstablishExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectStageEstablishModel> {
    @Override
    protected void customStrategy(Workbook workbook) {
        // 实现或扩展此方法以适应DashboardProjectStageEstablish的具体导出策略
    }

    @Override
    protected Class<DashboardProjectStageEstablishModel> modelClz() {
        return DashboardProjectStageEstablishModel.class;
    }
}
