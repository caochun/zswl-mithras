package cn.zswltech.mithras.dashboard.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPledgeModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardProjectPledgeExcelExporter extends AbstractSimpleExcelExporter<DashboardProjectPledgeModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardProjectPledgeModel> modelClz() {
        return DashboardProjectPledgeModel.class;
    }
}
