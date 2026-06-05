package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectStageContractModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class DashboardProjectStageContractExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectStageContractModel> {
    @Override
    protected void customStrategy(Workbook workbook) {
        // 实现或扩展此方法以适应DashboardProjectStageContract的具体导出策略
    }

    @Override
    protected Class<DashboardProjectStageContractModel> modelClz() {
        return DashboardProjectStageContractModel.class;
    }
}