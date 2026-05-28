package cn.zswltech.mithras.blackgray.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalSubmitREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.*;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayBreakBusinessAuditService;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayOutboundAuditService;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayWarehouseAuditService;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayWarehouseTaskAuditService;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 黑灰名单审批控制层
 * @author: jackerhe
 * @date: 2023/11/29 3:05 下午
 **/
@RestController
@RequestMapping("/black/gray/approval")
public class BlackGrayApprovalController {

    @Resource
    private BlackGrayWarehouseAuditService blackGrayWarehouseAuditService;
    @Resource
    private BlackGrayBreakBusinessAuditService blackGrayBreakBusinessAuditService;
    @Resource
    private BlackGrayOutboundAuditService blackGrayOutboundAuditService;
    @Resource
    private BlackGrayWarehouseTaskAuditService blackGrayWarehouseTaskAuditService;


    @ApiOperation("黑灰名单库入库审批")
    @PostMapping("/warehouse/submit")
    public R<BlackGrayApprovalSubmitRSP> warehouseSubmit(@RequestBody BlackGrayApprovalSubmitREQ req) {
        return R.ok(blackGrayWarehouseAuditService.approvalSubmit(req));
    }

    @ApiOperation("黑灰名单入库-审批查询")
    @GetMapping("/warehouse/auditList")
    public R<PageR<BlackGrayWarehouseApprovalTaskRSP>> warehouseAuditList(BlackGrayApprovalTaskREQ req) {
        return R.ok(blackGrayWarehouseAuditService.auditList(req));
    }


    @ApiOperation("黑灰名单库突破审批")
    @PostMapping("/break/business")
    public R<BlackGrayApprovalSubmitRSP> breakBusiness(@RequestBody BlackGrayApprovalSubmitREQ req) {
        return R.ok(blackGrayBreakBusinessAuditService.approvalSubmit(req));
    }

    @ApiOperation("黑灰名单突破-审批查询")
    @GetMapping("/break/business/auditList")
    public R<PageR<BlackGrayBreakBusinessApprovalTaskRSP>> breakBusinessAuditList(BlackGrayApprovalTaskREQ req) {
        return R.ok(blackGrayBreakBusinessAuditService.auditList(req));
    }


    @ApiOperation("黑灰名单库出库审批")
    @PostMapping("/manual/outbound")
    public R<BlackGrayApprovalSubmitRSP> outbound(@RequestBody BlackGrayApprovalSubmitREQ req) {
        return R.ok(blackGrayOutboundAuditService.approvalSubmit(req));
    }

    @ApiOperation("黑灰名单出库-审批查询")
    @GetMapping("/manual/outbound/auditList")
    public R<PageR<BlackGrayManualOutboundApprovalTaskRSP>> outboundAuditList(BlackGrayApprovalTaskREQ req) {
        return R.ok(blackGrayOutboundAuditService.auditList(req));
    }

    @ApiOperation("黑灰名单主任务审批")
    @PostMapping("/manual/task")
    public R<BlackGrayApprovalSubmitRSP> taskOutbound(@RequestBody BlackGrayApprovalSubmitREQ req) {
        return R.ok(blackGrayWarehouseTaskAuditService.approvalSubmit(req));
    }

    @ApiOperation("黑灰名单主任务-审批查询")
    @GetMapping("/manual/task/auditList")
    public R<PageR<BlackGrayWarehouseTaskApprovalTaskRSP>> taskOutboundAuditList(BlackGrayWarehouseApprovalTaskREQ req) {
        return R.ok(blackGrayWarehouseTaskAuditService.auditList(req));
    }

}
