package cn.zswltech.mithras.api.fund.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 直接融资-详情信息
 * @date 2023-06-17
 */
@Api(tags = "直接融资-详情信息-接口")
public interface FundDirectFinancingBaseInfoApi {

    @ApiOperation("新增直接融资-详情信息")
    @PostMapping("/fund/direct/financing/base/info/add")
    R<Long> add(@RequestBody @Valid FundDirectFinancingBaseInfoAddREQ req);

    @ApiOperation("修改直接融资-详情信息")
    @PostMapping("/fund/direct/financing/base/info/modify")
    R<Void> modify(@RequestBody @Valid FundDirectFinancingBaseInfoModifyREQ req);

    @ApiOperation("直接融资-详情信息列表")
    @PostMapping("/fund/direct/financing/base/info/list")
    R<PageR<FundDirectFinancingBaseInfoListRSP>> list(@RequestBody @Valid FundDirectFinancingBaseInfoListREQ req);

    @ApiOperation("直接融资-详情信息合计行")
    @PostMapping("/fund/direct/financing/base/info/sum")
    R<FundDirectFinancingBaseInfoListRSP> sum(@RequestBody @Valid FundDirectFinancingBaseInfoListREQ req);

    @ApiOperation("直接融资-详情信息详情")
    @PostMapping("/fund/direct/financing/base/info/detail")
    R<FundDirectFinancingBaseInfoDetailRSP> detail(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("直接融资-作废")
    @PostMapping("/fund/direct/financing/base/info/obsolete")
    R<Void> obsolete(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("直接融资-同步信息至还本付息")
    @PostMapping("/fund/direct/financing/base/info/sync")
    R<Void> sync(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("直接融资-删除")
    @PostMapping("/fund/direct/financing/base/info/delete")
    R<Void> delete(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("直接融资-结清")
    @Deprecated
    @PostMapping("/fund/direct/financing/base/info/settle")
    R<Void> settle(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

    @ApiOperation("批量下载")
    @PostMapping("/fund/direct/financing/batchDownload")
    void batchDownload(@RequestBody @Valid FundFinancingBatchDownloadREQ req);

    @ApiOperation("下载文件")
    @GetMapping("/fund/direct/financing/download")
    void download(@Valid FileDownloadREQ fileDownloadREQ);
}