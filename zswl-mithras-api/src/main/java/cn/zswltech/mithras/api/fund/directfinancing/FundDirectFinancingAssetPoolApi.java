package cn.zswltech.mithras.api.fund.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 直接融资-资产池信息
* @author zhaozhengkang
* @date 2023-06-17
*/
@Api(tags = "直接融资-资产池信息-接口")
public interface FundDirectFinancingAssetPoolApi {

    @ApiOperation("修改直接融资-资产池信息")
    @PostMapping("/fund/direct/financing/asset/pool/modify")
    R<Void> modify(@RequestBody @Valid FundDirectFinancingAssetPoolModifyREQ req);


    @ApiOperation("直接融资-资产池信息详情")
    @PostMapping("/fund/direct/financing/asset/pool/detail")
    R<FundDirectFinancingAssetPoolDetailRSP> detail(@RequestBody @Valid FundDirectFinancingAssetPoolDetailREQ req);

}