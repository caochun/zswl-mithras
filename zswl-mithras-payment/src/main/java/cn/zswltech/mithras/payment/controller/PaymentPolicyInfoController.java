package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentPolicyInfoApi;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.dto.payment.lib.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import cn.zswltech.mithras.payment.application.PaymentPolicyInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class PaymentPolicyInfoController implements PaymentPolicyInfoApi {
    @Resource
    private PaymentPolicyInfoApplicationService paymentPolicyInfoApplicationService;

    @Override
    public R<Void> add(MultipartFile[] files, Long paymentId, PaymentPolicyInfoAddREQ req) {
        return paymentPolicyInfoApplicationService.add(files, paymentId, req);
    }

    @Override
    public R<Void> modify(MultipartFile[] files, Long id, PaymentPolicyInfoModifyREQ req) {
        return paymentPolicyInfoApplicationService.modify(files, id, req);
    }

    @Override
    public R<Void> modifyFlag(@Valid PaymentPolicyInfoModifyFlagREQ req) {
        return paymentPolicyInfoApplicationService.modifyFlag(req);
    }

    @Override
    public R<PageR<PaymentPolicyInfoListRSP>> list(PaymentPolicyInfoListREQ req) {
        return paymentPolicyInfoApplicationService.list(req);
    }

    @Override
    public R<Void> export(@Valid PaymentPolicyInfoExportREQ req) {
        return paymentPolicyInfoApplicationService.export(req);
    }

    @Override
    public R<Void> remove(PaymentPolicyInfoRemoveREQ req) {
        return paymentPolicyInfoApplicationService.remove(req);
    }

    @Override
    public R<Void> removeBatch(@Valid PaymentPolicyInfoRemoveBatchREQ req) {
        return paymentPolicyInfoApplicationService.removeBatch(req);
    }

    @Override
    public R<String> importExcel(@Valid PaymentPoliceImportREQ req) {
        return paymentPolicyInfoApplicationService.importExcel(req);
    }

    @Override
    public R<String> downloadTemplate() {
        return paymentPolicyInfoApplicationService.downloadTemplate();
    }

}
