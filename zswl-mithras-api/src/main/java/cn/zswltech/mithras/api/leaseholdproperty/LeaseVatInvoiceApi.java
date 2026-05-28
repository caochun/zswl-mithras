package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author yupengfei
 * @date 2024/5/8 17:00
 */
@Api(tags = "租赁物-增值税发票接口")
@RequestMapping("lease/vatInvoice")
public interface LeaseVatInvoiceApi {

    @ApiOperation("增值税发票上传")
    @PostMapping("upload")
    R<Void> vatInvoiceUpload(@Valid LeaseVatInvoiceUploadREQ req);

    @ApiOperation(value = "增值税发票重新上传")
    @PostMapping(value = "anewUpload")
    R<Void> anewUpload(@Valid LeaseVatInvoiceUploadREQ req);

    @ApiOperation("增值税发票分页列表")
    @PostMapping("list")
    R<PageR<LeaseVatInvoiceListRSP>> queryVatInvoiceList(@RequestBody LeaseVatInvoiceQueryREQ req);

    @ApiOperation("增值税发票批量删除")
    @PostMapping("delete")
    R<Void> remove(@Valid @RequestBody LeaseVatInvoiceRemoveREQ req);

    @ApiOperation("增值税发票批量修改")
    @PostMapping("update")
    R<Void> update(@Valid @RequestBody LeaseVatInvoiceUpdateREQ req);

    @ApiOperation("统计各状态发票数量")
    @PostMapping("count")
    R<LeaseVatInvoiceCountRSP> count(@Valid @RequestBody LeaseItemIdREQ req);

    @ApiOperation(value = "增值税发票金额校验")
    @PostMapping("amountCheckout")
    R<String> amountCheckout(@Valid @RequestBody LeaseItemIdREQ req);

    @ApiOperation(value = "增值税发票重新验真")
    @PostMapping("retest")
    R<Void> retest(@Valid @RequestBody LeaseVatInvoiceRetestREQ req);

    @ApiOperation(value = "增值税发票下载")
    @PostMapping("exportExcel")
    void exportExcel(@RequestBody LeaseVatInvoiceQueryREQ req);

    @ApiOperation("发票锁定，解锁")
    @PostMapping("locked")
    R<Void> locked(@Valid @RequestBody LeaseVatInvoiceLockREQ req);
}