package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.price.ContractIRRSaveREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


/**
* @description 合同-租赁报价方案表
* @author vico
* @date 2022-08-12
*/
@Api(tags = "合同-租赁报价方案表-接口")
public interface ContractPriceApi {

    @ApiOperation("合同修改报价方案")
    @PostMapping("/contract/price/modify")
    R<Void> modify(@RequestBody @Valid ContractPriceModifyREQ req);

    @ApiOperation("合同查询报价方案")
    @PostMapping("/contract/price/detail")
    R<ContractPriceDetailRSP> detail(@RequestBody @Valid ContractPriceDetailREQ req);

    @ApiOperation("合同查询最近版本报价方案")
    @PostMapping("/contract/price/old/detail")
    R<ContractPriceDetailRSP> oldDetail(@RequestBody @Valid ContractPriceDetailREQ req);

    @ApiOperation("保存合同irr")
    @PostMapping("/contract/price/irr/save")
    R<Void> saveIrrPercent(@RequestBody @Valid ContractIRRSaveREQ req);

}