package cn.zswltech.mithras.service.controller.dashboard.todo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.NodeDefineResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardTodoApi;
import cn.zswltech.mithras.api.flow.TaskApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionReturnableNodesRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.service.service.dashboard.todo.DashboardTodoService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程相关-任务-接口
 *
 * @author zhouning
 * @date 2024/6/19 12:42 AM
 */
@RestController
public class DashboardTodoController implements DashboardTodoApi {
    @Resource
    private DashboardTodoService dashboardTodoService;

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
