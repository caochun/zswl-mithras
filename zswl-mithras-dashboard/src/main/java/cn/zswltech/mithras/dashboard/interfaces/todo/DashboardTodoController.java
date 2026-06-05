package cn.zswltech.mithras.dashboard.interfaces.todo;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardTodoApi;
import cn.zswltech.mithras.dashboard.application.todo.DashboardTodoApplicationService;
import cn.zswltech.mithras.dto.dashboard.*;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程相关-任务-接口
 *
 * @author zhouning
 * @date 2024/6/19 12:42 AM
 */
@RestController
public class DashboardTodoController implements DashboardTodoApi {
    @Resource
    private DashboardTodoApplicationService dashboardTodoService;

    @Override
    public R<List<DashboardTodoMyTodoProcessTaskRSP>> todoPageList(DashboardTodoREQ req) {
        return R.ok(dashboardTodoService.todoPageList(req));
    }

    @Override
    public R<PageR<DashboardTodoProcessRSP>> myProcessApplyPageList(DashboardTodoREQ req) {
        return R.ok(dashboardTodoService.myProcessApplyPageList(req));
    }

    @Override
    public R<PageR<DashboardTodoProcessRSP>> myProcessDoingPageList(DashboardTodoREQ req) {
        return R.ok(dashboardTodoService.myProcessDoingPageList(req));
    }

    @Override
    public R<PageR<DashboardTodoProcessRSP>> myProcessFinishPageList(DashboardTodoREQ req) {
        return R.ok(dashboardTodoService.myProcessFinishPageList(req));
    }

    @Override
    public R<PageR<DashBoardProcessCcListRSP>> myReceiveCcList(DashboardTodoREQ req) {
        return R.ok(dashboardTodoService.myReceiveCcList(req));
    }

    @Override
    public R<DashboardTodoCountRSP> myTabCount() {
        return R.ok(dashboardTodoService.myTabCount());
    }


}
