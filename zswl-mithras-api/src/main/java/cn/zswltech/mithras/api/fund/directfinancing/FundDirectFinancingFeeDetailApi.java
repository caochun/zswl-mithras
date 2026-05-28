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
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Api(tags = "直接融资-费用明细-接口")
public interface FundDirectFinancingFeeDetailApi {

    @ApiOperation("新增直接融资-融资方案编辑")
    @PostMapping("/fund/direct/financing/programme/modify")
    R<Void> modifyProgramme(@RequestBody @Valid FundDirectFinancingProgrammeModifyREQ req);

    @ApiOperation("新增直接融资-费用明细")
    @PostMapping("/fund/direct/financing/fee/add")
    R<Void> add(@RequestBody @Valid FundDirectFinancingFeeDetailAddREQ req);

    @ApiOperation("修改直接融资-费用明细")
    @PostMapping("/fund/direct/financing/fee/modify")
    R<Void> modifyFee(@RequestBody @Valid FundDirectFinancingFeeDetailModifyREQ req);

    @ApiOperation("直接融资-费用明细详情")
    @PostMapping("/fund/direct/financing/fee/detail")
    R<FundDirectFinancingFeeDetailRSP> detail(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("直接融资-费用明细列表")
    @PostMapping("/fund/direct/financing/fee/list")
    R<FundDirectFinancingFeeDetailListRSP> list(@RequestBody @Valid FundDirectFinancingFeeDetailListREQ req);

    @ApiOperation("删除直接融资-费用明细")
    @PostMapping("/fund/direct/financing/fee/remove")
    R<Void> remove(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

}