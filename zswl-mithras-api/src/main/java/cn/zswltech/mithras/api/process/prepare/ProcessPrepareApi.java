package cn.zswltech.mithras.api.process.prepare;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.IdPageREQ;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.process.prepare.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
@Api(tags = "预备流程接口")
public interface ProcessPrepareApi {

    @PostMapping("/process/prepare/list")
    R<PageR<ProcessPrepareListRSP>> list(@RequestBody @Valid ProcessPrepareListREQ req);

    @PostMapping("/process/prepare/detail")
    R<ProcessPrepareDetailRSP> detail(@RequestBody @Valid IdREQ req);

    @PostMapping("/process/prepare/commit")
    R<Void> commitPrepare(@RequestBody @Valid IdREQ req);

    @PostMapping("/process/prepare/discard")
    R<Void> discardPrepare(@RequestBody @Valid IdREQ req);

    @PostMapping("/process/prepare/detail/list")
    R<PageR<RentCollectionMonthDetailListRSP>> detailList(@RequestBody @Valid IdPageREQ req);

    @ApiOperation("租金支付通知关键信息")
    @PostMapping("/process/prepare/key/list")
    R<RentCollectionMonthKeyListRSP> keyList(@RequestBody @Valid IdPageREQ req);

    @ApiOperation("下载全量清单")
    @PostMapping("/process/prepare/detail/list/download")
    void downloadList(@RequestBody @Valid IdREQ req);

    @ApiOperation(value = "单个-预览通知书", notes = "传入列表记录的id")
    @PostMapping("/process/prepare/detail/single/preview")
    byte[] previewNotify(@RequestBody @Valid IdREQ req);

    @ApiOperation("单个预览通知书-下载")
    @PostMapping("/process/prepare/detail/single/download")
    void downloadPreview(@RequestBody @Valid IdREQ req);

    @ApiOperation(value = "批量下载通知单")
    @PostMapping("/process/prepare/detail/batch/download")
    void downloadBatch(@RequestBody @Valid IdREQ req);

    @ApiOperation("发起人-刷新租金信息")
    @PostMapping("/process/prepare/refresh")
    R<Void> refreshRentInfo(@RequestBody @Valid IdREQ req);

    @ApiOperation("手动推送租金支付通知书")
    @PostMapping("/process/prepare/generateNextMonthRentNotify")
    R<Void> generateNextMonthRentNotify();

    @ApiOperation("修改租金支付通知书的银行账户信息")
    @PostMapping("/process/prepare/bankinfo/modify")
    R<Void> modifyBankAccountInfo(@RequestBody @Valid RentCollectionMonthModifyAccountREQ req);

    @ApiOperation("还本付息计划确认")
    @PostMapping("/process/prepare/detail/repay/list")
    R<PageR<FinancingRepayActualProcessDetailListRSP>> repayDetailList(@RequestBody @Valid IdPageREQ req);
}
