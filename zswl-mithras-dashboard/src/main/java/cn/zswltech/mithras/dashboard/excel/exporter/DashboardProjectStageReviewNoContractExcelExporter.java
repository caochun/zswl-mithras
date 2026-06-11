package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectStageReviewNoContractModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class DashboardProjectStageReviewNoContractExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectStageReviewNoContractModel> {
    @Override
    protected void customStrategy(Workbook workbook) {
        // 实现或扩展此方法以适应DashboardProjectStageReview的具体导出策略
    }

    @Override
    protected Class<DashboardProjectStageReviewNoContractModel> modelClz() {
        return DashboardProjectStageReviewNoContractModel.class;
    }
}