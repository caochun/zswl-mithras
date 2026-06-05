package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentApi;
import cn.zswltech.mithras.api.payment.PaymentDetailReq;
import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.register.*;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractFlowBasicREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseCheckRepeatRSP;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.*;
import cn.zswltech.mithras.payment.application.PaymentApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class PaymentController implements PaymentApi {
    @Resource
    private PaymentApplicationService paymentApplicationService;

    @Override
    public R<PaymentWrittenOffAmountRsp.FtpAssessDTO> getFtpAssessmentByPaymentId(SinglePkREQ req) {
        return paymentApplicationService.getFtpAssessmentByPaymentId(req);
    }

    @Override
    public R<List<PaymentBankAccountListRsp>> listBankAccount(@Valid PaymentBankAccountListReq req) {
        return paymentApplicationService.listBankAccount(req);
    }

    @Override
    public R<List<PaymentContractListRsp>> contracts(PaymentContractListReq req) {
        return paymentApplicationService.contracts(req);
    }

    @Override
    public R<PaymentAddRsp> add(PaymentAddReq req) {
        return paymentApplicationService.add(req);
    }

    @Override
    public R<PaymentSellerInfoRsp> sellerInfo(ContractFlowBasicREQ contractFlowBasicREQ) {
        return paymentApplicationService.sellerInfo(contractFlowBasicREQ);
    }

    @Override
    public R<PageR<PaymentListRsp>> list(PaymentListReq req) {
        return paymentApplicationService.list(req);
    }

    @Override
    public R<PaymentDetailRsp> detail(PaymentDetailReq req) {
        return paymentApplicationService.detail(req);
    }

    @Override
    public R<List<PaymentTransactionStructureInfoRsp>> transactionStructureInfo(PaymentTransactionStructureInfoReq req) {
        return paymentApplicationService.transactionStructureInfo(req);
    }

    @Override
    public R<Void> modify(PaymentModifyReq req) {
        return paymentApplicationService.modify(req);
    }

    @Override
    public R<String> disable(PaymentCloseReq req) {
        return paymentApplicationService.disable(req);
    }

    @Override
    public R<Void> finish(@Valid PaymentFinishReq req) {
        return paymentApplicationService.finish(req);
    }

    @Override
    public void downloadApprovalTemplate(@Valid SinglePkREQ singlePkREQ) {
        paymentApplicationService.downloadApprovalTemplate(singlePkREQ);
    }

    @Override
    public R<List<PaymentSimpleInfoRSP>> listNoReceipt(ContractSingleIdREQ contractSingleIdREQ) {
        return paymentApplicationService.listNoReceipt(contractSingleIdREQ);
    }

    @Override
    public R<List<PaymentWrittenOffAmountRsp>> listPaymentWrittenOffAmount(@Valid ContractSingleIdREQ req) {
        return paymentApplicationService.listPaymentWrittenOffAmount(req);
    }

    @Override
    public R<Void> modifyFtp(@Valid PaymentModifyFtpReq req) {
        return paymentApplicationService.modifyFtp(req);
    }

    @Override
    public R<List<ContractCompareBusinessRSP>> compareBusiness(@Valid PaymentClientCompareReq req) {
        return paymentApplicationService.compareBusiness(req);
    }

    @Override
    public R<LeaseCheckRepeatRSP> getLeaseCheckRepeat(@Valid SinglePkREQ req) {
        return paymentApplicationService.getLeaseCheckRepeat(req);
    }

    @Override
    public R<Integer> checkProjReviewTimeout(@Valid SinglePkREQ req) {
        return paymentApplicationService.checkProjReviewTimeout(req);
    }

    @Override
    public R<Integer> getCollectionDay(@Valid SinglePkREQ req) {
        return paymentApplicationService.getCollectionDay(req);
    }

    @Override
    public R<Void> modifyCollectionDay(@Valid ModifyCollectionDayREQ req) {
        return paymentApplicationService.modifyCollectionDay(req);
    }

    @Override
    public R<Void> loanReviewFile(PaymentDetailReq req) {
        return paymentApplicationService.loanReviewFile(req);
    }

    @Override
    public R<List<FileListRSP>> loanReviewFileList(PaymentDetailReq req) {
        return paymentApplicationService.loanReviewFileList(req);
    }

    @Override
    public void loanReviewDownloadTemplate() {
        paymentApplicationService.loanReviewDownloadTemplate();
    }

    @Override
    public R<Void> sendAdvanceApplication(PaymentDetailReq req) {
        return paymentApplicationService.sendAdvanceApplication(req);
    }

    @Override
    public R<Void> paymentCloseBeforeCheck(@Valid PaymentDetailReq req) {
        return paymentApplicationService.paymentCloseBeforeCheck(req);
    }

    @Override
    public R<PaymentCheckApplyAmountRsp> checkApplyAmount(PaymentCheckApplyAmountReq req) {
        return paymentApplicationService.checkApplyAmount(req);
    }

    @Override
    public R<RegisterSaveRsp> saveRegister(PaymentAutoRegisterReq req) {
        return paymentApplicationService.saveRegister(req);
    }

}
