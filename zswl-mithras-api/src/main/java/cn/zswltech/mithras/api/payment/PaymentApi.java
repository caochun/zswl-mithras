package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.register.RegisterSaveRsp;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractFlowBasicREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseCheckRepeatRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 15:49
 */
@Api(tags = "付款管理-基本信息接口")
public interface PaymentApi {
    @ApiOperation("通过付款id查询FTP考核信息")
    @PostMapping("/payment/ftp/get")
    R<PaymentWrittenOffAmountRsp.FtpAssessDTO> getFtpAssessmentByPaymentId(@RequestBody @Valid SinglePkREQ req);

    // Deprecated 对方账户从选择改为手动输入，不再需要后端查询并集返回
    @Deprecated
    @ApiOperation("获取客户银行账户列表")
    @PostMapping("/payment/client/bankaccount/list")
    R<List<PaymentBankAccountListRsp>> listBankAccount(@RequestBody @Valid PaymentBankAccountListReq req);

    @ApiOperation("检索付款合同")
    @PostMapping("/payment/contract/list")
    R<List<PaymentContractListRsp>> contracts(@RequestBody @Valid PaymentContractListReq req);

    @ApiOperation("付款申请-创建接口")
    @PostMapping("/payment/add")
    R<PaymentAddRsp> add(@RequestBody @Valid PaymentAddReq req);

    @ApiOperation("付款申请-卖方账号信息")
    @PostMapping("/payment/sellerInfo")
    R<PaymentSellerInfoRsp> sellerInfo(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ);

    @ApiOperation("付款申请-列表接口")
    @PostMapping("/payment/list")
    R<PageR<PaymentListRsp>> list(@RequestBody @Valid PaymentListReq req);

    @ApiOperation("付款申请-详情接口")
    @PostMapping("/payment/detail")
    R<PaymentDetailRsp> detail(@RequestBody @Valid PaymentDetailReq req);

    @ApiOperation("付款申请-交易结构信息")
    @PostMapping("/payment/transactionStructureInfo")
    R<List<PaymentTransactionStructureInfoRsp>> transactionStructureInfo (@RequestBody @Valid PaymentTransactionStructureInfoReq req);


    @ApiOperation("付款申请-更新接口")
    @PostMapping("/payment/modify")
    R<Void> modify(@RequestBody @Valid PaymentModifyReq req);

    @ApiOperation("付款申请-关闭接口")
    @PostMapping("/payment/disable")
    R<String> disable(@RequestBody @Valid PaymentCloseReq req);

    @ApiOperation("付款申请-结束投放")
    @PostMapping("/payment/finish")
    R<Void> finish(@RequestBody @Valid PaymentFinishReq req);

    @ApiOperation("付款申请-下载放款审批表模板")
    @PostMapping("/payment/template/loan/download")
    void downloadApprovalTemplate(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("付款申请-获取没有关联借据的付款申请")
    @PostMapping("/payment/noreceipt/list")
    R<List<PaymentSimpleInfoRSP>> listNoReceipt(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("付款申请-获取指定合同下的所有付款核销相关金额信息")
    @PostMapping("/payment/contractWrittenOffAmount/list")
    R<List<PaymentWrittenOffAmountRsp>> listPaymentWrittenOffAmount(@RequestBody @Valid ContractSingleIdREQ req);

    @ApiOperation("付款申请-更新FTP成本")
    @PostMapping("/payment/ftp/modify")
    R<Void> modifyFtp(@RequestBody @Valid PaymentModifyFtpReq req);

    @ApiOperation("付款比对承租人及担保人工商信息")
    @PostMapping("/payment/client/compare/business")
    R<List<ContractCompareBusinessRSP>> compareBusiness(@RequestBody @Valid PaymentClientCompareReq req);

    @ApiOperation("根据付款申请找到对应的租赁物审核管理的中登网查重日期")
    @PostMapping("/payment/leaseitem/checkrepeat/get")
    R<LeaseCheckRepeatRSP> getLeaseCheckRepeat(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("检查付款申请对应项目评审通过是否超时")
    @PostMapping("/payment/projreview/timeout/check")
    R<Integer> checkProjReviewTimeout(@RequestBody @Valid SinglePkREQ req);

    //默认收款日
    @ApiOperation("获取付款对应收款日")
    @PostMapping("/payment/collection/day")
    R<Integer> getCollectionDay(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("修改付款对应收款日")
    @PostMapping("/payment/collection/day/modify")
    R<Void> modifyCollectionDay(@RequestBody @Valid ModifyCollectionDayREQ req);


    @ApiOperation("放款审核-文件生成")
    @PostMapping("/payment/loanReviewFile")
    R<Void> loanReviewFile(@RequestBody @Valid PaymentDetailReq req);

    @ApiOperation("放款审核-文件查询")
    @PostMapping("/payment/loanReviewFile/list")
    R<List<FileListRSP>> loanReviewFileList(@RequestBody @Valid PaymentDetailReq req);
    @ApiOperation("放款审核模板下载")
    @PostMapping("/payment/loanReviewFile/download/template")
    void loanReviewDownloadTemplate();

    @ApiOperation("推送银企直联付款申请单")
    @PostMapping("/payment/send/advance/application")
    R<Void> sendAdvanceApplication(@RequestBody @Valid PaymentDetailReq req);

    @ApiOperation("付款实际核销关闭前检查")
    @PostMapping("/payment/close/before/check")
    R<Void> paymentCloseBeforeCheck(@RequestBody @Valid PaymentDetailReq req);

    @ApiOperation(value = "校验申请金额")
    @PostMapping("/payment/check/apply/amount")
    R<PaymentCheckApplyAmountRsp> checkApplyAmount(@RequestBody @Valid PaymentCheckApplyAmountReq req);

    @ApiOperation("付款申请-中登自动登记")
    @PostMapping("/payment/auto/register")
    R<RegisterSaveRsp> saveRegister(@RequestBody @Valid PaymentAutoRegisterReq req);

}
