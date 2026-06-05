package cn.zswltech.mithras.service.application.workbench;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.service.service.workbench.WorkbenchAnnouncementService;
import cn.zswltech.mithras.workbench.application.WorkbenchAnnouncementApplicationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Service
public class WorkbenchAnnouncementFacade implements WorkbenchAnnouncementApplicationService {

    @Resource
    private WorkbenchAnnouncementService baseService;

    @Override
    public R<Long> add(WorkbenchAnnouncementAddReq req) {
        Long add = baseService.add(req);
        return R.ok(add);
    }

    @Override
    public R<Void> modify(WorkbenchAnnouncementModifyReq req) {
        baseService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<WorkbenchAnnouncementListRsp>> list(WorkbenchAnnouncementListReq req) {
        return R.ok(baseService.list(req));
    }

    @Override
    public R<Void> remove(WorkbenchAnnouncementSingleReq req) {
        baseService.remove(req);
        return R.ok();
    }

    @Override
    public R<WorkbenchAnnouncementDetailRsp> detail(WorkbenchAnnouncementSingleReq req) {
        return R.ok(baseService.detail(req));
    }

    @Override
    public R<Void> top(WorkbenchAnnouncementSingleReq req) {
        baseService.top(req);
        return R.ok();
    }

}
