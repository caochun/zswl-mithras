package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 绩效-拨备表
* @author vico
* @date 2023-06-19
*/
@Api(tags = "绩效-拨备表-接口")
public interface KpiProvisionBaseInfoApi {

    /**
     *新增绩效-拨备表
     **/
    @ApiOperation("新增绩效-拨备表")
    @PostMapping("/kpi/provision/base/info/add")
    R<KpiProvisionBaseInfoAddRSP> add(@RequestBody @Valid KpiProvisionBaseInfoAddREQ req);

    /**
     *刷新绩效-拨备表
     **/
    @ApiOperation("刷新绩效-拨备表")
    @PostMapping("/kpi/provision/base/info/refresh")
    R<Void> refresh(@RequestBody @Valid KpiProvisionBaseInfoDetailREQ req);

    /**
     *确认绩效-拨备表
     **/
    @ApiOperation("确认绩效-拨备表")
    @PostMapping("/kpi/provision/base/info/effect")
    R<Void> effect(@RequestBody @Valid KpiProvisionBaseInfoDetailREQ req);

    /**
     *修改绩效-拨备表
     **/
    @ApiOperation("修改绩效-拨备表")
    @PostMapping("/kpi/provision/base/info/modify")
    R<Void> modify(@RequestBody @Valid KpiProvisionBaseInfoModifyREQ req);

    /**
     *绩效-拨备表列表
     **/
    @ApiOperation("绩效-拨备表列表")
    @PostMapping("/kpi/provision/base/info/list")
    R<PageR<KpiProvisionBaseInfoListRSP>> list(@RequestBody @Valid KpiProvisionBaseInfoListREQ req);

    /**
     *绩效详情-拨备表
     **/
    @ApiOperation("删除绩效-拨备表")
    @PostMapping("/kpi/provision/base/info/detail")
    R<KpiProvisionBaseInfoDetailRSP> detail(@RequestBody @Valid KpiProvisionBaseInfoDetailREQ req);

    /**
     * 绩效-项目测算表-人员详情
     **/
    @ApiOperation("绩效-项目测算表-人员详情")
    @GetMapping("/kpi/provision/detail/export")
    R<Void> detailExport(@Valid KpiProvisionBaseInfoDetailExportREQ req);

}