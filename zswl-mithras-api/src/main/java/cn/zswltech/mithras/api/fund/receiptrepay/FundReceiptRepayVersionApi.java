package cn.zswltech.mithras.api.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchReceiptDownloadREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchReceiptListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchSubmitREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.CreateBatchREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * 资金收付款审批相关接口
 *
 * @author wangchuanhao
 * @date 2023/2/18 5:09 PM
 */
@Api(tags = "资金收付款审批相关接口")
@RequestMapping("/fund/receipt/repay")
public interface FundReceiptRepayVersionApi {

    @ApiOperation("单个提交审批")
    @PostMapping("/submit")
    R<Void> submit(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("创建批次")
    @PostMapping("/createBatch")
    R<Long> createBatch(@RequestBody @Valid CreateBatchREQ req);

    @ApiOperation("批量提交审批")
    @PostMapping("/batchSubmit")
    R<Void> batchSubmit(@RequestBody @Valid BatchSubmitREQ req);

    @ApiOperation("批量操作-详情")
    @PostMapping("/batchDetail")
    R<BatchDetailRSP> batchDetail(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("批量操作-付款列表")
    @PostMapping("/batchReceiptList")
    R<List<BatchReceiptListRSP>> batchReceiptList(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("批量操作-付款列表下载")
    @PostMapping("/batchReceiptDownload")
    void batchReceiptDownload(@RequestBody @Valid BatchReceiptDownloadREQ req);


    @ApiOperation("资金收付款版本列表")
    @PostMapping("/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("资金收付款版本比较详情（与上一版本比较）")
    @PostMapping("/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid SinglePkREQ req);

}
