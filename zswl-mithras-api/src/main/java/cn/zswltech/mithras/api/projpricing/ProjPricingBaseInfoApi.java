package cn.zswltech.mithras.api.projpricing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projpricing.ProjPricingButtonStatusRsp;
import cn.zswltech.mithras.dto.projpricing.ProjPricingCreateREQ;
import cn.zswltech.mithras.dto.projpricing.ProjPricingCreateRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@Api(tags = "项目定价-基本信息接口")
public interface ProjPricingBaseInfoApi {
    @ApiOperation("创建项目定价")
    @PostMapping("/proj/pricing/create")
    R<ProjPricingCreateRSP> create(@RequestBody @Valid ProjPricingCreateREQ req);

    @Deprecated
    @ApiOperation("新增项目定价基本信息表")
    @PostMapping("/proj/pricing/base/info/add")
    R<ProjPricingBaseInfoAddRSP> add(@RequestBody @Valid ProjPricingBaseInfoAddREQ req);

    @Deprecated
    @ApiOperation("新增项目定价基本信息表(选取集团授信)")
    @PostMapping("/proj/pricing/base/info/addByGroupCredit")
    R<ProjPricingBaseInfoAddRSP> addByGroupCredit(@RequestBody @Valid ProjPricingBaseInfoAddByGroupCreditREQ req);

    @ApiOperation("修改项目定价基本信息")
    @PostMapping("/proj/pricing/base/info/modify")
    R<Void> modify(@RequestBody @Valid ProjPricingBaseInfoModifyREQ req);

    @ApiOperation("项目定价基本信息列表查询")
    @PostMapping("/proj/pricing/base/info/list")
    R<PageR<ProjPricingBaseInfoListRSP>> list(@RequestBody @Valid ProjPricingBaseInfoListREQ req);

    @ApiOperation("项目定价基本信息详情查询")
    @PostMapping("/proj/pricing/base/info/detail")
    R<ProjPricingBaseInfoDetailRSP> detail(@RequestBody @Valid ProjPricingBaseInfoDetailREQ req);

    @ApiOperation("删除立项基本信息")
    @PostMapping("/proj/pricing/base/info/disable")
    R<Void> disable(@RequestBody @Valid ProjPricingBaseInfoRemoveREQ req);

    @ApiOperation("获取定价按钮状态")
    @PostMapping("/proj/pricing/button/status")
    R<ProjPricingButtonStatusRsp> buttonStatus(@RequestBody @Valid ProjPricingBaseInfoDetailREQ req);

}