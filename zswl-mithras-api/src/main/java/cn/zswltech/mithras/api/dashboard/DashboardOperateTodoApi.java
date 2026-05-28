package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoCountRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoMyTodoProcessTaskRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoProcessRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoREQ;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveREQ;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveRSP;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoListREQ;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "业务工作台-运营视图-待办统计")
@RequestMapping(path = "/dashboard/operation")
public interface DashboardOperateTodoApi {
    @ApiOperation("业务工作台-运营视图-待办统计")
    @PostMapping(path = "/todo/statistics")
    R<List<DashboardOperateTodoArriveRSP>> todoStatistics(@RequestBody @Valid DashboardOperateTodoArriveREQ req);

}
