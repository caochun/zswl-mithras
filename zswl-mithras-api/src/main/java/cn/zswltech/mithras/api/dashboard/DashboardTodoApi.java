package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Api(tags = "业务工作台-统一待办")
@RequestMapping(path = "/dashboard/todo")
public interface DashboardTodoApi {
    @ApiOperation("业务工作台-统一待办-待办")
    @PostMapping(path = "/list")
    R<List<DashboardTodoMyTodoProcessTaskRSP>> todoPageList(@RequestBody @Valid DashboardTodoREQ req);

    @ApiOperation("业务工作台-统一待办-我发起的")
    @PostMapping(path = "/myprocess/apply")
    R<PageR<DashboardTodoProcessRSP>> myProcessApplyPageList(@RequestBody @Valid DashboardTodoREQ req);

    @ApiOperation("业务工作台-统一待办-在办")
    @PostMapping(path = "/myprocess/doing")
    R<PageR<DashboardTodoProcessRSP>> myProcessDoingPageList(@RequestBody @Valid DashboardTodoREQ req);

    @ApiOperation("业务工作台-统一待办-已办")
    @PostMapping(path = "/myprocess/finish")
    R<PageR<DashboardTodoProcessRSP>> myProcessFinishPageList(@RequestBody @Valid DashboardTodoREQ req);


    @ApiOperation("业务工作台-统一待办-流程抄送")
    @PostMapping(path = "/myReceive/cc/list")
    R<PageR<DashBoardProcessCcListRSP>> myReceiveCcList(@RequestBody @Valid DashboardTodoREQ req);

    @ApiOperation("各栏目数量")
    @GetMapping("/count")
    R<DashboardTodoCountRSP> myTabCount();
}
