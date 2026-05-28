package cn.zswltech.mithras.blackgray.controller;


import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessAddREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessDetailREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessModifyREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 审批突破
* @author
* @date 2023-11-28
*/
@RestController
@Api(tags = "黑灰名单审批突破-接口")
public class BlackGrayBreakBusinessController {

    @Resource
    private cn.zswltech.mithras.blackgray.service.BlackGrayBreakBusinessService BlackGrayBreakBusinessService;

    @ApiOperation("新增审批突破")
    @PostMapping("/black/gray/break/business/add")
    public R<Long> add(@RequestBody BlackGrayBreakBusinessAddREQ req) {
        return R.ok(BlackGrayBreakBusinessService.add(req));
    }

    @ApiOperation("修改审批突破")
    @PostMapping("/black/gray/break/business/modify")
    public R<Void> modify(@RequestBody BlackGrayBreakBusinessModifyREQ req){
        BlackGrayBreakBusinessService.modify(req);
        return R.ok();
    }

    @ApiOperation("审批突破列表")
    @PostMapping("/black/gray/break/business/list")
    public R<PageR<BlackGrayBreakBusinessListRSP>> list(@RequestBody BlackGrayBreakBusinessListREQ req){
        return R.ok(BlackGrayBreakBusinessService.list(req));
    }

    @ApiOperation("审批突破详情")
    @PostMapping("/black/gray/break/business/detail")
    public R<BlackGrayBreakBusinessDetailRSP> detail(@RequestBody BlackGrayBreakBusinessDetailREQ req){
        return R.ok(BlackGrayBreakBusinessService.detail(req.getId()));
    }

}