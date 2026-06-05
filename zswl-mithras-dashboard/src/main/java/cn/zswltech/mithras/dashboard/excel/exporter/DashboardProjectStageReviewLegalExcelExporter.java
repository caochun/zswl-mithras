package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectStageReviewLegalModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class DashboardProjectStageReviewLegalExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectStageReviewLegalModel> {
    @Override
    protected void customStrategy(Workbook workbook) {
        // 实现或扩展此方法以适应DashboardProjectStageReview的具体导出策略
    }

    @Override
    protected Class<DashboardProjectStageReviewLegalModel> modelClz() {
        return DashboardProjectStageReviewLegalModel.class;
    }
}