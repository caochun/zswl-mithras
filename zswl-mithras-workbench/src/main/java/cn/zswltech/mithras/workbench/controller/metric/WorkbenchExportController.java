package cn.zswltech.mithras.workbench.controller.metric;

import cn.zswltech.mithras.api.workbench.WorkbenchExportApi;
import cn.zswltech.mithras.dto.workbench.CardListExportReq;
import cn.zswltech.mithras.dto.workbench.ProjectMetricExportReq;
import cn.zswltech.mithras.workbench.application.WorkbenchExportApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class WorkbenchExportController implements WorkbenchExportApi {

    @Resource
    private WorkbenchExportApplicationService workbenchExportApplicationService;

    @Override
    public void clientListExport(CardListExportReq req) throws Exception {
        workbenchExportApplicationService.clientListExport(req);
    }

    @Override
    public void projListExport(CardListExportReq req) throws Exception {
        workbenchExportApplicationService.projListExport(req);
    }

    @Override
    public void reviewList(CardListExportReq req) throws Exception {
        workbenchExportApplicationService.reviewList(req);
    }

    @Override
    public void paymentList(CardListExportReq req) throws Exception {
        workbenchExportApplicationService.paymentList(req);
    }

    @Override
    public void overdueList(CardListExportReq req) throws Exception {
        workbenchExportApplicationService.overdueList(req);
    }

    @Override
    public void undesirableList(CardListExportReq req) throws Exception {
        workbenchExportApplicationService.undesirableList(req);
    }

    @Override
    public void reviewList(ProjectMetricExportReq req) throws Exception {
        workbenchExportApplicationService.reviewList(req);
    }

    @Override
    public void launchList(ProjectMetricExportReq req) throws Exception {
        workbenchExportApplicationService.launchList(req);
    }
}
