package cn.zswltech.mithras.api.projestablish;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projestablish.baseinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@Api(tags = "立项基本信息表-接口")
public interface ProjEstablishBaseInfoApi {

    @ApiOperation("新增立项基本信息表")
    @PostMapping("/proj/establish/base/info/add")
    R<ProjEstablishBaseInfoAddRSP> add(@RequestBody @Valid ProjEstablishBaseInfoAddREQ req);

    @ApiOperation("更新评级信息")
    @PostMapping("/proj/establish/base/info/updateRating")
    R<ProjEstablishBaseInfoUpdateRatingRSP> updateRating(@RequestBody @Valid ProjEstablishBaseInfoUpdateRatingREQ req);

    @ApiOperation("修改立项基本信息表")
    @PostMapping("/proj/establish/base/info/modify")
    R<Void> modify(@RequestBody @Valid ProjEstablishBaseInfoModifyREQ req);

    @ApiOperation("立项基本信息表列表")
    @PostMapping("/proj/establish/base/info/list")
    R<PageR<ProjEstablishBaseInfoListRSP>> list(@RequestBody @Valid ProjEstablishBaseInfoListREQ req);

    @ApiOperation("立项基本信息详情")
    @PostMapping("/proj/establish/base/info/detail")
    R<ProjEstablishBaseInfoListRSP> detail(@RequestBody @Valid ProjEstablishBaseInfoDetailREQ req);

    @ApiOperation("删除立项基本信息")
    @PostMapping("/proj/establish/base/info/disable")
    R<Void> disable(@RequestBody @Valid ProjEstablishBaseInfoRemoveREQ req);

    @ApiOperation("获取法人客户存量风险敝口")
    @PostMapping("/proj/establish/base/info/exposure")
    R<ClientStockRiskExposureRSP> getClientStockRiskExposure(@RequestBody @Valid ClientIdREQ req);

    @ApiOperation("获取法人客户地址信息")
    @PostMapping("/proj/establish/get/client/address")
    R<ClientAddressRSP> getClientAddress(@RequestBody @Valid ClientIdREQ req);


}