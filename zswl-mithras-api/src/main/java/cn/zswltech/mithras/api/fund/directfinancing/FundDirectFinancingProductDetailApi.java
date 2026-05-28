package cn.zswltech.mithras.api.fund.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-产品明细
 * @date 2023-06-17
 */
@Api(tags = "直接融资-产品明细-接口")
public interface FundDirectFinancingProductDetailApi {

    @ApiOperation("新增直接融资-产品明细")
    @PostMapping("/fund/direct/financing/product/add")
    R<Void> add(@RequestBody @Valid FundDirectFinancingProductDetailAddREQ req);

    @ApiOperation("修改直接融资-产品明细")
    @PostMapping("/fund/direct/financing/product/modify")
    R<Void> modify(@RequestBody @Valid FundDirectFinancingProductDetailModifyREQ req);

    @ApiOperation("直接融资-产品明细列表")
    @PostMapping("/fund/direct/financing/product/list")
    R<FundDirectFinancingProductDetailListRSP> list(@RequestBody @Valid FundDirectFinancingProductDetailListREQ req);

    @ApiOperation("直接融资-产品明细详情")
    @PostMapping("/fund/direct/financing/product/detail")
    R<FundDirectFinancingProductDetailRSP> detail(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("删除直接融资-产品明细")
    @PostMapping("/fund/direct/financing/product/remove")
    R<Void> remove(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("直接融资-产品明细-导出")
    @PostMapping("/fund/direct/financing/product/export")
    void exportExcel(@RequestBody @Valid FundDirectFinancingProductDetailListREQ req);


    @ApiOperation("直接融资-产品明细-下拉接口")
    @PostMapping("/fund/direct/financing/product/select")
    R<List<ProductSelectRsp>> select(@RequestBody @Valid FundDirectFinancingProductDetailListREQ req);

}