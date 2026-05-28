package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;

import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.writeoff.*;
import cn.zswltech.mithras.dto.SinglePkREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 11:45
 */
@Api(tags = "付款管理-核销接口")
public interface PaymentWriteOffApi {

    @ApiOperation("付款核销主页查询接口")
    @PostMapping("/payment/list/writeoff")
    R<PageR<PaymentWriteOffListRsp>> listWriteOff(@RequestBody @Valid PaymentWriteOffListReq req);

    @ApiOperation("付款核销详情页")
    @PostMapping("/payment/detail/writeoff")
    R<PaymentWriteOffDetailRsp> detailWriteOff(@RequestBody @Valid PaymentWriteOffDetailReq req);

    @ApiOperation("核销付款申请")
    @PostMapping("/payment/writeoff")
    R<Void> off(@RequestBody @Valid PaymentWriteOffReq req);

    @ApiOperation("撤销核销付款申请")
    @PostMapping("/payment/undowriteoff")
    R<Void> undooff(@RequestBody @Valid PaymentWriteOffReq req);

    @Deprecated
    @ApiOperation("添加付款记录")
    @PostMapping(value = "/payment/add/actualdetail")
    R<ActualDetailAddRsp> addActual(@RequestParam MultipartFile[] enclosures, @Valid ActualDetailPostReq req);

    @ApiOperation("查询付款记录列表")
    @PostMapping("/payment/list/actualdetail")
    R<ActualDetailListRsp> listActual(@RequestBody @Valid ActualDetailListReq req);

    @ApiOperation("查询付款记录详情")
    @PostMapping("/payment/detail/actualdetail")
    R<ActualDetailDto> detailActual(@RequestBody @Valid ActualDetailOperateReq req);

    @Deprecated
    @ApiOperation("编辑付款记录")
    @PostMapping(value = "/payment/modify/actualdetail")
    R<Void> modifyActual(@RequestParam MultipartFile[] enclosures, @Valid ActualDetailPostReq req);

    @ApiOperation("核销付款记录")
    @PostMapping("/payment/writeoff/actualdetail")
    R<Void> writeOffActual(@RequestBody @Valid ActualDetailWriteoffReq req);

    @ApiOperation("删除付款记录")
    @PostMapping("/payment/remove/actualdetail")
    R<Void> removeActual(@RequestBody @Valid ActualDetailOperateReq req);

    @ApiOperation("核销记录查询")
    @PostMapping("/payment/write/off/history/list")
    R<PageR<PaymentWriteOffHistoryListRsp>> list(@RequestBody @Valid PaymentWriteOffHistoryListReq req);

    @ApiOperation("付款核销记录明细-新增")
    @PostMapping("/payment/actualdetail/add")
    R<Long> create(@RequestBody @Valid PaymentActualDetailAddReq req);

    @ApiOperation("付款核销记录明细-编辑")
    @PostMapping("/payment/actualdetail/modify")
    R<Void> modify(@RequestBody @Valid PaymentActualDetailModifyReq req);

    @ApiOperation("付款核销提交审批")
    @PostMapping("/payment/actualdetail/submit")
    R<String> submit(@RequestBody @Valid PaymentActualDetailSubmitReq req);

    @ApiOperation("保存收款信息")
    @PostMapping("/payment/collection/add")
    R<Long> collectionAdd(@RequestBody @Valid PaymentCollectionAddReq req);

    @ApiOperation("收款信息列表")
    @GetMapping("/payment/collection/detail")
    R<PaymentCollectionRsp> collectionDetail(@RequestParam Long paymentId);

    @ApiOperation("付款核销运营提前审核提交审批")
    @PostMapping(path = "/payment/reviewinadvanced/submit")
    R<String> submitReviewInAdvanced(@RequestBody @Valid SinglePkREQ req);
}
