package cn.zswltech.mithras.service.excel.exporter.dashboard;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationContractReturnModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;


@Component
public class DashboardOperationContractReturnExcelExporter extends AbstractSimpleExcelExporter<DashboardOperationContractReturnModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<DashboardOperationContractReturnModel> modelClz() {
        return DashboardOperationContractReturnModel.class;
    }
}
