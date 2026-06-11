package cn.zswltech.mithras.blackgray.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRuleConfigDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRuleConfigListRSP;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRuleConfig;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRuleConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
* @description 黑灰名单库-入库原因参数配置
* @author 
* @date 2024-01-18
*/
@RestController
@Api(tags = "黑灰名单库-入库原因参数配置-接口")
public class BlackGrayWarehouseRuleConfigController {

    @Resource
    private BlackGrayWarehouseRuleConfigService blackGrayWarehouseRuleConfigService;

    @ApiOperation("新增黑灰名单库-入库原因参数配置")
    @PostMapping("/black/gray/warehouse/rule/config/add")
    public R<Void> add(@RequestBody @Valid BlackGrayWarehouseRuleConfigAddREQ req) {
        blackGrayWarehouseRuleConfigService.add(req);
        return R.ok();
    }

    @ApiOperation("修改黑灰名单库-入库原因参数配置")
    @PostMapping("/black/gray/warehouse/rule/config/modify")
    public R<Void> modify(@RequestBody @Valid BlackGrayWarehouseRuleConfigModifyREQ req){
        blackGrayWarehouseRuleConfigService.modify(req);
        return R.ok();
    }
    @ApiOperation("黑灰名单库-入库原因参数配置列表")
    @PostMapping("/black/gray/warehouse/rule/config/list")
    public R<PageR<BlackGrayWarehouseRuleConfigListRSP>> list(@RequestBody @Valid BlackGrayWarehouseRuleConfigListREQ req){
        PageR<BlackGrayWarehouseRuleConfig> data = blackGrayWarehouseRuleConfigService.list(req);
        List<BlackGrayWarehouseRuleConfigListRSP> list = new ArrayList<>();
        if(data.getList() != null){
            data.getList().forEach(detail -> {
                BlackGrayWarehouseRuleConfigListRSP rsp = BeanUtil.copyProperties(detail, BlackGrayWarehouseRuleConfigListRSP.class);
                if(ObjectUtil.isNotEmpty(detail.getSuitBusiness())){
                    rsp.setSuitBusiness(JSONUtil.toList(detail.getSuitBusiness(), String.class));
                }
                if(ObjectUtil.isNotEmpty(detail.getSuitOrg())){
                    rsp.setSuitOrg(JSONUtil.toList(detail.getSuitOrg(), String.class));
                }
                list.add(rsp);
            });
        }
        return R.ok(PageR.of(list, data.getTotal()));
    }

    @ApiOperation("黑灰名单库-入库原因参数配置详情")
    @PostMapping("/black/gray/warehouse/rule/config/detail")
    public R<BlackGrayWarehouseRuleConfigDetailRSP> detail(@RequestBody @Valid BlackGrayWarehouseRuleConfigDetailREQ req){
        BlackGrayWarehouseRuleConfig detail = blackGrayWarehouseRuleConfigService.detail(req.getId());
        BlackGrayWarehouseRuleConfigDetailRSP rsp = null;
        if(ObjectUtil.isNotEmpty(detail)){
            rsp = BeanUtil.copyProperties(detail, BlackGrayWarehouseRuleConfigDetailRSP.class);
            if(ObjectUtil.isNotEmpty(detail.getSuitBusiness())){
                rsp.setSuitBusiness(JSONUtil.toList(detail.getSuitBusiness(), String.class));
            }
            if(ObjectUtil.isNotEmpty(detail.getSuitOrg())){
                rsp.setSuitOrg(JSONUtil.toList(detail.getSuitOrg(), String.class));
            }
        }
        return R.ok(rsp);
    }

    @ApiOperation("删除黑灰名单库-入库原因参数配置")
    @PostMapping("/black/gray/warehouse/rule/config/remove")
    public R<Void> remove(@RequestBody @Valid BlackGrayWarehouseRuleConfigRemoveREQ req){
        blackGrayWarehouseRuleConfigService.remove(req);
        return R.ok();
    }

    @ApiOperation("黑灰名单库-入库原因参数配置启用停用接口")
    @PostMapping("/black/gray/warehouse/rule/config/switch")
    public R<Void> switchConfig(@RequestBody @Valid BlackGrayWarehouseRuleConfigSwitchREQ req){
        blackGrayWarehouseRuleConfigService.switchConfig(req);
        return R.ok();
    }

}