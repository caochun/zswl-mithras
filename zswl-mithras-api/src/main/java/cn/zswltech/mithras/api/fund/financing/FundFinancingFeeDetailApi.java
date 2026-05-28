package cn.zswltech.mithras.api.fund.financing;

import cn.hutool.db.Page;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.fee.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 间融-费用明细
 * @date 2023-06-17
 */
@Api(tags = "间融-费用明细-接口")
public interface FundFinancingFeeDetailApi {

    @ApiOperation("新增费用项")
    @PostMapping("/fund/financing/fee/add")
    R<Void> add(@RequestBody @Valid FundFinancingFeeDetailAddREQ req);

    @ApiOperation("修改费用")
    @PostMapping("/fund/financing/fee/modify")
    R<Void> modifyFee(@RequestBody @Valid FundFinancingFeeDetailModifyREQ req);

//    @ApiOperation("费用项详情")
//    @PostMapping("/fund/financing/fee/detail")
//    R<FundDirectFinancingFeeDetailRSP> detail(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("费用项列表")
    @PostMapping("/fund/financing/fee/list")
    R<PageR<FundFinancingFeeDetailRSP>> list(@RequestBody @Valid FundFinancingFeeDetailListREQ req);

    @ApiOperation("删除费用项")
    @PostMapping("/fund/financing/fee/remove")
    R<Void> remove(@RequestBody @Valid FundFinancingFeeSingleIdREQ req);

}