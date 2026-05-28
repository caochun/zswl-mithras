package cn.zswltech.mithras.api.groupcreditestablish;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientIdREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 集团授信立项基本信息表
* @author wangchuanhao
* @date 2022-11-11
*/
@Api(tags = "集团授信立项基本信息-接口")
public interface GroupCreditEstablishBaseInfoApi {

    @ApiOperation("新增集团授信立项基本信息")
    @PostMapping("/group/credit/establish/base/info/add")
    R<GroupCreditEstablishBaseInfoAddRSP> add(@RequestBody @Valid GroupCreditEstablishBaseInfoAddREQ req);

    @ApiOperation("修改集团授信立项基本信息")
    @PostMapping("/group/credit/establish/base/info/modify")
    R<Void> modify(@RequestBody @Valid GroupCreditEstablishBaseInfoModifyREQ req);

    @ApiOperation("集团授信立项基本信息列表")
    @PostMapping("/group/credit/establish/base/info/list")
    R<PageR<GroupCreditEstablishListRSP>> list(@RequestBody @Valid GroupCreditEstablishListREQ req);

    @ApiOperation("集团授信立项基本信息详情")
    @PostMapping("/group/credit/establish/base/info/detail")
    R<GroupCreditEstablishBaseInfoDetailRSP> detail(@RequestBody @Valid GroupCreditEstablishBaseInfoDetailREQ req);

    @ApiOperation("获取集团授信存量风险敞口")
    @PostMapping("/group/credit/exposure")
    R<Long> getClientStockRiskExposure(@RequestBody @Valid ClientIdREQ req);

    @ApiOperation("更新评级信息")
    @PostMapping("/group/credit/establish/base/info/updateRating")
    R<GroupCreditEstalishInfoUpdateRatingRSP> updateRating(@RequestBody @Valid GroupCreditEstalishBaseInfoUpdateRatingREQ req);
}