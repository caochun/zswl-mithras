package cn.zswltech.mithras.workbench.interfaces.announcement;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchAnnouncementApi;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.workbench.application.WorkbenchAnnouncementApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class WorkbenchAnnouncementController implements WorkbenchAnnouncementApi {

    @Resource
    private WorkbenchAnnouncementApplicationService workbenchAnnouncementApplicationService;

    @Override
    public R<Long> add(WorkbenchAnnouncementAddReq req) {
        return workbenchAnnouncementApplicationService.add(req);
    }

    @Override
    public R<Void> modify(WorkbenchAnnouncementModifyReq req) {
        return workbenchAnnouncementApplicationService.modify(req);
    }

    @Override
    public R<PageR<WorkbenchAnnouncementListRsp>> list(WorkbenchAnnouncementListReq req) {
        return workbenchAnnouncementApplicationService.list(req);
    }

    @Override
    public R<Void> remove(WorkbenchAnnouncementSingleReq req) {
        return workbenchAnnouncementApplicationService.remove(req);
    }

    @Override
    public R<WorkbenchAnnouncementDetailRsp> detail(WorkbenchAnnouncementSingleReq req) {
        return workbenchAnnouncementApplicationService.detail(req);
    }

    @Override
    public R<Void> top(WorkbenchAnnouncementSingleReq req) {
        return workbenchAnnouncementApplicationService.top(req);
    }
}
