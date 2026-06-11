package cn.zswltech.mithras.api.property;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyListREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyRSP;
import cn.zswltech.mithras.dto.property.PutPropertyBaseInfoListREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ylzhang5
 * @description 投放资产列表
 * @date 20251213
 */
@Api(tags = "投放资产-接口")
public interface PutPopertyBaseInfoApi {
    @ApiOperation("间接融资-投放资产明细列表")
    @PostMapping("/fund/financing/property/list")
    R<List<FundFinancingPropertyRSP>> fundList(@RequestBody @Valid FundFinancingPropertyListREQ req);

    @ApiOperation("直接融资-投放资产明细列表")
    @PostMapping("/direct/financing/property/list")
    R<List<FundFinancingPropertyRSP>> directList(@RequestBody @Valid FundFinancingPropertyListREQ req);

    @ApiOperation("资金管理-投放资产列表")
    @PostMapping("/financing/property/list")
    R<List<FundFinancingPropertyRSP>> putPropertyList(@RequestBody @Valid PutPropertyBaseInfoListREQ req);

    @ApiOperation(value = "资金管理-投放资产列表导出")
    @PostMapping("/financing/property/list/download")
    void putPropertyListDownload(@RequestBody @Valid PutPropertyBaseInfoListREQ req);

}