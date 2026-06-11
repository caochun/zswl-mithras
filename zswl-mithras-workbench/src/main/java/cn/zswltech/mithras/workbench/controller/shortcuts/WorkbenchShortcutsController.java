package cn.zswltech.mithras.workbench.controller.shortcuts;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchShortcutsApi;
import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsMaintainReq;
import cn.zswltech.mithras.workbench.application.WorkbenchShortcutsApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class WorkbenchShortcutsController implements WorkbenchShortcutsApi {

    @Resource
    private WorkbenchShortcutsApplicationService workbenchShortcutsApplicationService;

    @Override
    public R<List<WorkbenchShortcutsListRsp>> list() {
        return workbenchShortcutsApplicationService.list();
    }

    @Override
    public R<Void> add(WorkbenchShortcutsMaintainReq req) {
        return workbenchShortcutsApplicationService.add(req);
    }
}
