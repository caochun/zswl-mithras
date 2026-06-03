package cn.zswltech.mithras.service.providence;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.riskcontrol.monitor.ClientMonitorOpinionDetailRsp;
import cn.zswltech.mithras.riskcontrol.monitor.ClientMonitorWarnDetailRsp;
import cn.zswltech.mithras.customer.interfaces.providence.dto.ClientMonitorListRsp;
import cn.zswltech.mithras.service.providence.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:19
 */
@RestController
@Slf4j
@Api(tags = "客户舆情预警监控")
public class ClientMonitorController {

    @Resource
    private ClientMonitorService clientMonitorService;

    @ApiOperation(value = "客户监控列表")
    @PostMapping("/clientMonitor/list")
    public R<PageR<ClientMonitorListRsp>> clientMonitorList(@RequestBody ClientMonitorListReq req){
        return R.ok(clientMonitorService.clientMonitorList(req));
    }

    @ApiOperation(value = "客户监控预警列表")
    @PostMapping("/clientMonitor/warn/list")
    public R<PageR<ClientMonitorWarnListRsp>> clientMonitorWarnList(@RequestBody ClientMonitorWarnListReq req){
        return R.ok(clientMonitorService.clientMonitorWarnList(req));
    }

    @ApiOperation(value = "客户监控逾期列表")
    @PostMapping("/clientMonitor/opinion/list")
    public R<PageR<ClientMonitorOpinionListRsp>> clientMonitorOpinionList(@RequestBody ClientMonitorOpinionListReq req){
        return R.ok(clientMonitorService.clientMonitorOpinionList(req));
    }

    @ApiOperation(value = "客户监控统计")
    @PostMapping("/clientMonitor/statistic")
    public R<ClientMonitorStatisticRsp> clientMonitorStatistic(){

        return R.ok(clientMonitorService.clientMonitorStatistic());
    }

    @ApiOperation(value = "风险客户数量折线图")
    @PostMapping("/clientMonitor/risk/linechart")
    public R<ClientMonitorRiskLineChartRsp> clientMonitorRiskLineChart(){

        return R.ok(clientMonitorService.clientMonitorRiskLineChart());
    }

    @ApiOperation(value = "客户监控饼图")
    @PostMapping("/clientMonitor/risk/piechart")
    public R<ClientMonitorPieChartRsp> clientMonitorPieChart(){
        return R.ok(clientMonitorService.clientMonitorPieChart());
    }

    @ApiOperation(value = "客户监控预警详情")
    @PostMapping("/clientMonitor/detail/warn")
    public R<List<ClientMonitorWarnDetailRsp>> clientMonitorWarnDetail(@RequestBody ClientMonitorDetailReq req){

        return R.ok(clientMonitorService.clientMonitorWarnDetail(req.getClientId()));
    }

    @ApiOperation(value = "客户监控舆情详情")
    @PostMapping("/clientMonitor/detail/opinion")
    public R<List<ClientMonitorOpinionDetailRsp>> clientMonitorOpDetail(@RequestBody ClientMonitorDetailReq req){

        return R.ok(clientMonitorService.clientMonitorOpDetail(req.getClientId()));
    }

}
