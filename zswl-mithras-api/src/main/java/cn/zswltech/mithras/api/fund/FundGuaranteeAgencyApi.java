package cn.zswltech.mithras.api.fund;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@Api(tags = "担保机构-接口")
public interface FundGuaranteeAgencyApi {

    @ApiOperation("新增担保机构并同步天眼查信息")
    @PostMapping("/fund/guarantee/agency/add")
    R<Long> addAndSync(@RequestBody @Valid FundGuaranteeAgencyAddREQ req);

    @ApiOperation("新增用于手动录入的担保机构")
    @PostMapping("/fund/guarantee/agency/addhalf")
    R<Long> addHalf(@RequestBody @Valid FundGuaranteeAgencyAddREQ req);


    @ApiOperation("修改担保机构")
    @PostMapping("/fund/guarantee/agency/modify")
    R<Void> modify(@RequestBody @Valid FundGuaranteeAgencyModifyREQ req);

    @ApiOperation("同步天眼查数据")
    @PostMapping("/fund/guarantee/agency/sync")
    R<FundGuaranteeAgencyDetailRSP> sync(@RequestBody @Valid FundGuaranteeAgencySyncREQ req);

    @ApiOperation("担保机构列表")
    @PostMapping("/fund/guarantee/agency/list")
    R<PageR<FundGuaranteeAgencyListRSP>> list(@RequestBody @Valid FundGuaranteeAgencyListREQ req);

    @ApiOperation("担保机构下拉列表")
    @PostMapping("/fund/guarantee/agency/pulldown")
    R<List<FundGuaranteeAgencyListRSP>> pulldown(@RequestBody @Valid FundGuaranteeAgencyPullDownREQ req);

    @ApiOperation("担保机构详情")
    @PostMapping("/fund/guarantee/agency/detail")
    R<FundGuaranteeAgencyDetailRSP> detail(@RequestBody @Valid FundGuaranteeAgencyDetailREQ req);

    @ApiOperation("删除担保机构")
    @PostMapping("/fund/guarantee/agency/remove")
    R<Void> remove(@RequestBody @Valid FundGuaranteeAgencyRemoveREQ req);

    @ApiOperation("额度使用详情")
    @PostMapping("/fund/guarantee/info/limitDetail")
    R<FundGuaranteeLimitDetailRSP> limitDetail(@RequestBody @Valid FundGuaranteeSingletonIdREQ req);

}