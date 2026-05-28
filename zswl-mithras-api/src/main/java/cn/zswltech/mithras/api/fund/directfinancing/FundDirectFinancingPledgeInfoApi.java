package cn.zswltech.mithras.api.fund.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 直接融资-质押明细
 * @date 2023-06-17
 */
@Api(tags = "直接融资-质押明细-接口")
public interface FundDirectFinancingPledgeInfoApi {

    @ApiOperation("新增直接融资-质押明细")
    @PostMapping("/fund/direct/financing/pledge/info/add")
    R<Void> add(@RequestBody @Valid FundDirectFinancingPledgeInfoAddREQ req);

    @ApiOperation("修改直接融资-质押明细")
    @PostMapping("/fund/direct/financing/pledge/info/modify")
    R<Void> modify(@RequestBody @Valid FundDirectFinancingPledgeInfoModifyREQ req);

    @ApiOperation("直接融资-质押明细列表")
    @PostMapping("/fund/direct/financing/pledge/info/list")
    R<PageR<FundDirectFinancingPledgeInfoListRSP>> list(@RequestBody @Valid FundDirectFinancingPledgeInfoListREQ req);

    @ApiOperation("直接融资-质押明细详情")
    @PostMapping("/fund/direct/financing/pledge/info/detail")
    R<FundDirectFinancingPledgeInfoDetailRSP> detail(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("删除直接融资-质押明细")
    @PostMapping("/fund/direct/financing/pledge/info/remove")
    R<Void> remove(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("直接融资-质押明细-导出")
    @PostMapping("/fund/direct/financing/pledge/info/export")
    void exportExcel(@RequestBody @Valid FundDirectFinancingPledgeInfoListREQ req);




}