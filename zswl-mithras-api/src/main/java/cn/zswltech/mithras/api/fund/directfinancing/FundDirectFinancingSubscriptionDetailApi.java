package cn.zswltech.mithras.api.fund.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 直接融资-认购明细
 * @date 2023-06-17
 */
@Api(tags = "直接融资-认购明细-接口")
public interface FundDirectFinancingSubscriptionDetailApi {

    @ApiOperation("新增直接融资-认购明细")
    @PostMapping("/fund/direct/financing/subscription/add")
    R<Void> add(@RequestBody @Valid FundDirectFinancingSubscriptionDetailAddREQ req);

    @ApiOperation("修改直接融资-认购明细")
    @PostMapping("/fund/direct/financing/subscription/modify")
    R<Void> modify(@RequestBody @Valid FundDirectFinancingSubscriptionDetailModifyREQ req);

    @ApiOperation("直接融资-认购明细列表")
    @PostMapping("/fund/direct/financing/subscription/list")
    R<FundDirectFinancingSubscriptionDetailListRSP> list(@RequestBody @Valid FundDirectFinancingSubscriptionDetailListREQ req);

    @ApiOperation("删除直接融资-认购明细")
    @PostMapping("/fund/direct/financing/subscription/remove")
    R<Void> remove(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);


    @ApiOperation("直接融资-认购明细-导出")
    @PostMapping("/fund/direct/financing/subscription/export")
    void exportExcel(@RequestBody @Valid FundDirectFinancingProductDetailListREQ req);


}