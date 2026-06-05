package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailWriteoffReq;
import cn.zswltech.mithras.api.payment.PaymentWriteOffApi;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffHistoryListReq;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffHistoryListRsp;
import cn.zswltech.mithras.api.payment.writeoff.*;
import cn.zswltech.mithras.dto.SinglePkREQ;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.payment.application.PaymentWriteOffApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class PaymentWriteOffController implements PaymentWriteOffApi {
    @Resource
    private PaymentWriteOffApplicationService paymentWriteOffApplicationService;

    @Override
    public R<PageR<PaymentWriteOffListRsp>> listWriteOff(PaymentWriteOffListReq req) {
        return paymentWriteOffApplicationService.listWriteOff(req);
    }

    @Override
    public R<PaymentWriteOffDetailRsp> detailWriteOff(PaymentWriteOffDetailReq req) {
        return paymentWriteOffApplicationService.detailWriteOff(req);
    }

    @Override
    @Deprecated
    public R<ActualDetailAddRsp> addActual(MultipartFile[] enclosures, ActualDetailPostReq req) {
        return paymentWriteOffApplicationService.addActual(enclosures, req);
    }

    @Override
    public R<ActualDetailListRsp> listActual(ActualDetailListReq req) {
        return paymentWriteOffApplicationService.listActual(req);
    }

    @Override
    public R<ActualDetailDto> detailActual(ActualDetailOperateReq req) {
        return paymentWriteOffApplicationService.detailActual(req);
    }

    @Override
    @Deprecated
    public R<Void> modifyActual(MultipartFile[] enclosures, ActualDetailPostReq req) {
        return paymentWriteOffApplicationService.modifyActual(enclosures, req);
    }

    @Override
    public R<Void> writeOffActual(ActualDetailWriteoffReq req) {
        return paymentWriteOffApplicationService.writeOffActual(req);
    }

    @Override
    public R<Void> removeActual(ActualDetailOperateReq req) {
        return paymentWriteOffApplicationService.removeActual(req);
    }

    @Override
    public R<Void> off(PaymentWriteOffReq req) {
        return paymentWriteOffApplicationService.off(req);
    }

    @Override
    public R<Void> undooff(PaymentWriteOffReq req) {
        return paymentWriteOffApplicationService.undooff(req);
    }

    @Override
    public R<PageR<PaymentWriteOffHistoryListRsp>> list(PaymentWriteOffHistoryListReq req) {
        return paymentWriteOffApplicationService.list(req);
    }

    @Override
    public R<Long> create(@Valid PaymentActualDetailAddReq req) {
        return paymentWriteOffApplicationService.create(req);
    }

    @Override
    public R<Void> modify(@Valid PaymentActualDetailModifyReq req) {
        return paymentWriteOffApplicationService.modify(req);
    }

    @Override
    public R<String> submit(@Valid PaymentActualDetailSubmitReq req) {
        return paymentWriteOffApplicationService.submit(req);
    }

    @Override
    public R<Long> collectionAdd(PaymentCollectionAddReq req) {
        return paymentWriteOffApplicationService.collectionAdd(req);
    }

    @Override
    public R<PaymentCollectionRsp> collectionDetail(Long paymentId) {
        return paymentWriteOffApplicationService.collectionDetail(paymentId);
    }

    @Override
    public R<String> submitReviewInAdvanced(SinglePkREQ req) {
        return paymentWriteOffApplicationService.submitReviewInAdvanced(req);
    }

}
