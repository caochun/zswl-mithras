package cn.zswltech.mithras.service.application.payment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.payment.application.PaymentApplicationService;
import cn.zswltech.mithras.api.payment.PaymentDetailReq;
import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.register.*;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractFlowBasicREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseCheckRepeatRSP;
import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.payment.PaymentAddAuthChecker;
import cn.zswltech.mithras.service.auth.checker.payment.PaymentDisableAuthChecker;
import cn.zswltech.mithras.service.auth.checker.payment.PaymentModifyAuthChecker;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.application.client.CorpAddressInfoFacade;
import cn.zswltech.mithras.service.application.client.CorpCommerceInfoFacade;
import cn.zswltech.mithras.service.convert.FileConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.LendingMaterialType;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpBankAccountLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccountLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.system.service.ExceptionRequestInfoService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.contract.core.application.ContractAccountService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.customer.application.lib.client.CorpBankAccountLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractAccountLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.payment.application.lib.libservice.PaymentBaseInfoLibService;
import cn.zswltech.mithras.payment.application.lib.libservice.PaymentPlanedDetailLibService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.*;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoQueryService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 15:57
 */
@Slf4j
@Service
public class PaymentFacade implements PaymentApplicationService {

    @Resource
    private PaymentBaseInfoService baseInfoService;
    @Resource
    private PaymentBaseInfoLibService baseInfoLibService;
    @Resource
    private PaymentPlanedDetailService planedDetailService;
    @Resource
    private PaymentPlanedDetailLibService planedDetailLibService;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpBankAccountLibService corpBankAccountLibService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractAccountLibService contractAccountLibService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    protected FileConvert fileConvert;
    @Resource
    private ContractAccountService contractAccountService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfosService;
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private ContractPriceService priceService;
    @Resource
    private CorpCommerceInfoFacade corpCommerceInfoController;
    @Resource
    private CorpAddressInfoFacade corpAddressInfoController;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private FileService fileService;

    private final static String BUSINESS_TYPE = "B00000";
    private final static String CHN = "CHN";
    private final static String OTH = "OTH";
    private final static String DEBTOR_TYPE = "0203";
    private final static String ORG_TYPE1 = "02";//企业
    private final static String ORG_TYPE2 = "0203";//租赁公司
    private final static String ORG_TYPE3 = "020302";//内资试点融资租赁公司


    @Override
    public R<PaymentWrittenOffAmountRsp.FtpAssessDTO> getFtpAssessmentByPaymentId(SinglePkREQ req) {
        FtpAssessmentInfo ftpAssessmentInfo = ftpAssessmentInfosService.findFirstByPaymentId(req.getId());
        PaymentWrittenOffAmountRsp.FtpAssessDTO result = BeanUtil.copyProperties(ftpAssessmentInfo, PaymentWrittenOffAmountRsp.FtpAssessDTO.class);
        return R.ok(result);
    }

    @Override
    public R<List<PaymentBankAccountListRsp>> listBankAccount(@Valid PaymentBankAccountListReq req) {
        Client client = clientService.getById(req.getClientId());
        List<CorpBankAccountLib> corpBankAccountLibList = corpBankAccountLibService.listBy(client.getId(), client.getNewestVersion());
        PaymentBaseInfo paymentBaseInfo = baseInfoService.getById(req.getId());
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(paymentBaseInfo.getContractId());
        List<ContractAccountLib> contractAccountLibList = contractAccountLibService.listBy(paymentBaseInfo.getContractId(), contractBaseInfoLib.getVersion());
        List<PaymentBankAccountListRsp> result = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(corpBankAccountLibList)) {
            for (CorpBankAccountLib corpBankAccountLib : corpBankAccountLibList) {
                PaymentBankAccountListRsp rsp = new PaymentBankAccountListRsp();
                rsp.setAccountName(corpBankAccountLib.getAccountName());
                rsp.setAccountNumber(corpBankAccountLib.getAccountNumber());
                rsp.setAccountBank(corpBankAccountLib.getAccountBank());
                result.add(rsp);
            }
        }
        if (CollectionUtil.isNotEmpty(contractAccountLibList)) {
            for (ContractAccountLib contractAccountLib : contractAccountLibList) {
                PaymentBankAccountListRsp rsp = new PaymentBankAccountListRsp();
                rsp.setAccountName(contractAccountLib.getAccountName());
                rsp.setAccountNumber(contractAccountLib.getAccountNum());
                rsp.setAccountBank(contractAccountLib.getAccountAddress());
                result.add(rsp);
            }
        }
        return R.ok(result);
    }

    @Override
    public R<List<PaymentContractListRsp>> contracts(PaymentContractListReq req) {
        List<PaymentContractListRsp> contractPageData = baseInfoService.fuzzyMatchContracts(req);
        return R.ok(contractPageData);
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentAddAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<PaymentAddRsp> add(PaymentAddReq req) {
        PaymentAddRsp rsp = new PaymentAddRsp();
        rsp.setId(baseInfoService.add(req));
        ThreadPoolUtil.getCommonPool().execute(() -> {
            try {
                SpringUtil.getBean(PublicInfoQueryService.class).copyIntervalTable(rsp.getId());
            } catch (Exception e) {
                log.error("创建付款申请后拷贝公开信息查询数据发生异常[paymentId:{}]", rsp.getId());
            }
        });
        return R.ok(rsp);
    }

    @Override
    public R<PaymentSellerInfoRsp> sellerInfo(ContractFlowBasicREQ contractFlowBasicREQ) {
        PaymentSellerInfoRsp paymentSellerInfoRsp = new PaymentSellerInfoRsp();
        Optional.ofNullable(contractBaseInfoService.getById(contractFlowBasicREQ.getContractId())).ifPresent(contractBaseInfo -> {
            List<ContractAccount> contractAccounts = contractAccountService.list(Wrappers.<ContractAccount>lambdaQuery()
                    .eq(ContractAccount::getContractId, contractFlowBasicREQ.getContractId())
                    .eq(ContractAccount::getAccountUse, ContractAccountUseEnum.ZLSK.name()));
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name()) || Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
                String contractAccountPayeeTypeEnum = Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name()) ? ContractAccountPayeeTypeEnum.SELLER.name() : ContractAccountPayeeTypeEnum.YI.name();
                //  直租默认带出合同界面的「卖方」账户，回租默认带出合同界面的「乙方」账户若维护多个「乙方/卖方」账户信息。则只带出维护的第一条账户
                contractAccounts.stream()
                        .filter(contractAccount -> contractAccountPayeeTypeEnum.equals(contractAccount.getPayeeType()))
                        .findFirst()
                        .ifPresent(contractAccount -> {
                            paymentSellerInfoRsp.setOppositeAccount(contractAccount.getAccountNum());
                            paymentSellerInfoRsp.setOppositeAccountName(contractAccount.getAccountName());
                            paymentSellerInfoRsp.setOppositeAccountBank(contractAccount.getAccountAddress());
                        });
            }
        });
        return R.ok(paymentSellerInfoRsp);
    }

    @Override
    public R<PageR<PaymentListRsp>> list(PaymentListReq req) {
        return R.ok(baseInfoService.list(req));
    }

    @Override
    public R<PaymentDetailRsp> detail(PaymentDetailReq req) {
        PaymentDetailRsp detail;
        if (StringUtils.isBlank(req.getVersion())) {
            List<PlanedDetailDto> planedDetails = planedDetailService.list(req.getId());
            detail = baseInfoService.detail(req.getId());
            detail.setPlanedDetails(planedDetails);
            detail.setActualFinishDate(contractBaseInfoService.getContractExpirationDateByRent(Collections.singletonList(detail.getContractId())).get(detail.getContractId()));
            ProcessResp processResp = paymentService.findRelatedProcess(req.getId());
            detail.setCurAssigneeIds(processResp != null ? processResp.getCurAssigneeIds() : null);
        } else {
            List<PlanedDetailDto> planedDetails = planedDetailLibService.list(req.getId(), req.getVersion());
            detail = baseInfoLibService.detail(req.getId(), req.getVersion());
            detail.setPlanedDetails(planedDetails);
        }
        // 补充项目定价数据
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(detail.getContractId());
        ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewId(contractBaseInfo.getProjReviewId());
        if (Objects.nonNull(projPricingBaseInfo)) {
            ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(projPricingBaseInfo.getId());
            if (Objects.nonNull(pricingPriceDetailRSP)) {
                detail.setPricingIrr(pricingPriceDetailRSP.getIrr());
            }
        }
        if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            // 补充FTP价格信息
            if (Objects.nonNull(detail.getReceiptId())) {
                List<FtpAssessmentInfo> ftpAssessmentInfoList = ftpAssessmentInfosService.listAllByReceiptId(detail.getReceiptId());
                if (CollectionUtil.isNotEmpty(ftpAssessmentInfoList)) {
                    ftpAssessmentInfoList.sort(Comparator.comparing(FtpAssessmentInfo::getId));
                    FtpAssessInfo ftpAssessInfo = BeanUtil.copyProperties(ftpAssessmentInfoList.get(0), FtpAssessInfo.class);
                    detail.setFtpAssessInfo(ftpAssessInfo);
                }
            }
        }
        // 补充拟投放金额
        detail.setPlanPayAmount(paymentService.getPaymentAmount(detail.getApplyPaymentAmount(), contractBaseInfo.getProjCode()));
        return R.ok(detail);
    }

    @Override
    public R<List<PaymentTransactionStructureInfoRsp>> transactionStructureInfo(PaymentTransactionStructureInfoReq req) {
        return R.ok(baseInfoService.transactionStructureInfo(req));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @DataAuthCheck(keyFieldName = "id", checkerClass = PaymentModifyAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<Void> modify(PaymentModifyReq req) {
        Long amount = 0L;
        for (PlanedDetailDto detail : req.getDetails()) {
            if (ObjectUtil.isNotEmpty(detail.getPaymentAmount())) {
                amount += detail.getPaymentAmount();
            }
        }
        if (!amount.equals(req.getApplyPaymentAmount())) {
            throw new MithrasException("明细金额累加与申请金额不等");
        }

        baseInfoService.modify(req);
        if (ObjectUtil.isNotEmpty(req.getDetails())) {
            // 根据 id 是否为null 分类
            List<PlanedDetailDto> reqDetails = req.getDetails();
            List<PlanedDetailDto> insertList = new ArrayList<>();
            List<PlanedDetailDto> updateList = new ArrayList<>();
            for (PlanedDetailDto reqDetail : reqDetails) {
                if (null == reqDetail.getId()) {
                    insertList.add(reqDetail);
                } else {
                    updateList.add(reqDetail);
                }
            }
            // 根据update记录 计算出需要删除的ids
            List<PlanedDetailDto> dbDetails = planedDetailService.list(req.getId());
            Map<Long, PlanedDetailDto> map = ObjectUtil.isEmpty(updateList) ? Collections.EMPTY_MAP :
                    updateList.stream().collect(Collectors.toMap(PlanedDetailDto::getId, dto -> dto));
            List<Long> removeIds = new ArrayList<>();
            for (PlanedDetailDto dbDetail : dbDetails) {
                if (!map.containsKey(dbDetail.getId())) {
                    // 如果数据库中的id 参数列表中没有，记录id
                    removeIds.add(dbDetail.getId());
                }
            }
            // 1. 先批量删除
            planedDetailService.removeByIds(removeIds);

            if (ObjectUtil.isNotEmpty(updateList)) {
                // 2. 再更新update数据
                planedDetailService.updateBatchById(updateList);
            }
            // 3. 最后处理insert数据
            if (ObjectUtil.isNotEmpty(insertList)) {
                planedDetailService.saveBatch(insertList);
            }
        } else {
            planedDetailService.removeByPaymentId(req.getId());
        }
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "ids", checkerClass = PaymentDisableAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<String> disable(PaymentCloseReq req) {
        baseInfoService.disable2(req);
        return R.ok();
    }

    @Override
    public R<Void> finish(@Valid PaymentFinishReq req) {
        baseInfoService.finish(req.getPaymentId());
        return R.ok();
    }

    @Override
    public void downloadApprovalTemplate(@Valid SinglePkREQ singlePkREQ) {
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("放款审批表", StandardCharsets.UTF_8.name()) + GlobalConstants.OFFICE_WORD_SUFFIX);
            baseInfoService.chooseApprovalDocTemplate(httpServletResponse.getOutputStream(), singlePkREQ.getId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载放款审批表（底表）发生未知异常", e);
            throw new MithrasException("下载放款审批表（底表）发生未知异常");
        }
    }

    @Override
    public R<List<PaymentSimpleInfoRSP>> listNoReceipt(ContractSingleIdREQ contractSingleIdREQ) {
        List<PaymentBaseInfo> paymentBaseInfoList = baseInfoService.listAddNewReceiptCandidate(contractSingleIdREQ.getContractId());
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return R.ok(Collections.emptyList());
        }
        List<PaymentSimpleInfoRSP> filterList = paymentBaseInfoList.stream()
                .map(e -> {
                    PaymentSimpleInfoRSP rsp = new PaymentSimpleInfoRSP();
                    rsp.setPaymentId(e.getId());
                    rsp.setPaymentCode(e.getPaymentCode());
                    return rsp;
                }).collect(Collectors.toList());
        return R.ok(filterList);
    }

    @Override
    public R<List<PaymentWrittenOffAmountRsp>> listPaymentWrittenOffAmount(@Valid ContractSingleIdREQ req) {
        return R.ok(baseInfoService.listWrittenOffAmountByContractId(req.getContractId()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> modifyFtp(@Valid PaymentModifyFtpReq req) {
        FtpAssessmentInfo assessmentInfo = ftpAssessmentInfosService.getById(req.getId());
        Assert.notNull(assessmentInfo, () -> MithrasException.newException("ftp考核信息" + ResultMsg.RECORD_NOT_EXIST));
        // 设计上这个考核信息是挂在付款申请上的，所以子表的逻辑外键不会为空
        PaymentBaseInfo paymentBaseInfo = baseInfoService.getById(assessmentInfo.getPaymentId());
        Assert.notNull(paymentBaseInfo, () -> MithrasException.newException("付款申请" + ResultMsg.RECORD_NOT_EXIST));
        // 先修改子表的信息
        FtpAssessmentInfo ftpAssessmentInfo = new FtpAssessmentInfo();
        BeanUtil.copyProperties(req, ftpAssessmentInfo);
        if (Objects.nonNull(req.getBasePrice())) {
            ftpAssessmentInfo.setBasePrice(req.getBasePrice());
        } else {
            ftpAssessmentInfo.setBasePrice(assessmentInfo.getBasePrice());
        }
        ftpAssessmentInfo.setId(assessmentInfo.getId());
        ftpAssessmentInfo.setGuidePrice();
        ftpAssessmentInfo.setAssessmentPrice();
        ftpAssessmentInfosService.updateById(ftpAssessmentInfo);

//        PaymentBaseInfo info = new PaymentBaseInfo();
//        info.setId(paymentBaseInfo.getId());
//        info.setCashFtp(Math.toIntExact(ftpAssessmentInfo.getGuidePrice()));
//        info.setBillFtp(Math.toIntExact(ftpAssessmentInfo.getAssessmentPrice()));
//        baseInfoService.updateById(info);
        return R.ok();
    }

    @Override
    public R<List<ContractCompareBusinessRSP>> compareBusiness(@Valid PaymentClientCompareReq req) {
        //提交审批前需要调用天眼查比对
        PaymentBaseInfo paymentBaseInfo = baseInfoService.getById(req.getPaymentId());
        if (ObjectUtil.isNull(paymentBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        boolean isHistory = true;
        //流程中只允许部分岗位调用天眼查比对
        if (ObjectUtil.isNotEmpty(req.getFlowId())) {
            ProcessResp processResp = flowTaskApiService.queryProcessById(req.getFlowId());
            if (ProcessBusinessStatusEnum.RUNNING.getType().equals(processResp.getProcessStatus()) && sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name(), JobEnum.loanreviewpost.name(), JobEnum.projmanager.name())) {
                isHistory = false;
            }
        } else {
            //运营管理部经办人且未提交过
            isHistory =
                    !(PaymentStatusEnum.NEW.name().equals(paymentBaseInfo.getPaymentStatus()) && ProcessStatus.UN_SUBMIT.name().equals(paymentBaseInfo.getPaymentProcessStatus()) &&
                            sysUserService.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name()));
        }
        return R.ok(contractBaseInfoService.compareBusiness(paymentBaseInfo.getContractId(), isHistory));

    }

    @Override
    public R<LeaseCheckRepeatRSP> getLeaseCheckRepeat(@Valid SinglePkREQ req) {
        PaymentBaseInfo paymentBaseInfo = baseInfoService.getById(req.getId());
        if (Objects.isNull(paymentBaseInfo)) {
            return R.fail("付款申请信息不存在");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            return R.fail("合同信息不存在");
        }
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getNewestOne(contractBaseInfo.getProjReviewId());
        if (Objects.isNull(leaseItemInfo)) {
            return R.ok();
        }
        LeaseCheckRepeatRSP rsp = new LeaseCheckRepeatRSP();
        rsp.setRepeatDate(DateUtil.format(leaseItemInfo.getDuplicateCheckingDate(), "yyyy/MM/dd"));
        rsp.setRelevanceFlowId(leaseItemInfo.getRelevanceFlowId());
        return R.ok(rsp);
    }

    @Override
    public R<Integer> checkProjReviewTimeout(@Valid SinglePkREQ req) {
        return R.ok(paymentService.isProjReviewPassBeyondSpecificMonth(req.getId(), 6) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
    }

    @Override
    public R<Integer> getCollectionDay(@Valid SinglePkREQ req) {
        return R.ok(baseInfoService.getDefaultCollectionDay(req.getId()));
    }

    @Override
    public R<Void> modifyCollectionDay(@Valid ModifyCollectionDayREQ req) {
        baseInfoService.modifyDefaultCollectionDay(req);
        return R.ok();
    }

    @Override
    public R<Void> loanReviewFile(PaymentDetailReq req) {
        PaymentBaseInfo paymentBaseInfo = baseInfoService.getById(req.getId());
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请信息不存在");
        }

        if (!Objects.equals(AccountUtil.getLoginInfo().getId(), paymentBaseInfo.getCreateBy())) {
            return R.fail("运营经办人才能进行该操作");
        }
//        删除历史放款审核合同（只删除自动生成的）
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.PAYMENT.name(), Collections.singletonList(PaymentTypeEnum.LOAN_REVIEW.name()), Collections.singletonList(paymentBaseInfo.getId()));
        if (!CollectionUtils.isEmpty(materialsListList)) {
            List<Long> ids = materialsListList.stream()
                    .filter(item -> Objects.equals(item.getSystemGenerate(), YesOrNoNumberEnum.YES.getCode()) &&
                            Objects.equals(item.getIsEdit(), YesOrNoNumberEnum.NO.getCode()))
                    .map(MaterialsList::getId)
                    .collect(Collectors.toList());
            materialsListService.removeByIds(ids);
        }
        paymentService.loanReviewFile(paymentBaseInfo);
        return R.ok();
    }

    @Override
    public R<List<FileListRSP>> loanReviewFileList(PaymentDetailReq req) {
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.PAYMENT.name(), Collections.singletonList(PaymentTypeEnum.LOAN_REVIEW.name()), Collections.singletonList(req.getId()));
        if (ObjectUtils.isEmpty(materialsListList)) {
            return R.ok(new ArrayList<>());
        }
        List<FileListRSP> rspList = materialsListList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
        fileConvert.fillName(rspList);
        rspList.sort(new CommonFileSortComparator());
        return R.ok(rspList);
    }

    @Override
    public void loanReviewDownloadTemplate() {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("放款审核模板.zip", StandardCharsets.UTF_8.name()));
            baseInfoService.downloadTemplate(httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载模板发生未知异常", e);
            throw new MithrasException("下载模板发生未知异常");
        }
    }

    @Override
    public R<Void> sendAdvanceApplication(PaymentDetailReq req) {
        return R.ok(200, paymentActualDetailUnconfirmedService.sendPaymentApply(req.getId()));
    }

    @Override
    public R<Void> paymentCloseBeforeCheck(@Valid PaymentDetailReq req) {
        //查询本次已有银企直联
        List<ExceptionRequestInfo> oldException = SpringContextHolder.getBean(ExceptionRequestInfoService.class)
                .list(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                        .eq(ExceptionRequestInfo::getPlatform, PlatformApiEnum.CQ2_PAYMENT.name())
                        .eq(ExceptionRequestInfo::getSource, ExceptionSourceENUM.BUSINESS_FLOW.name())
                        .eq(ExceptionRequestInfo::getBusinessKey, req.getId())
                        .eq(ExceptionRequestInfo::getRetryFlag, YesOrNoNumberEnum.YES.getCode()));
        if (ObjectUtil.isNotEmpty(oldException)) {
            return R.ok(200, paymentActualDetailUnconfirmedService.removePaymentApply(oldException));
        } else {
            return R.ok();
        }
    }

    @Override
    public R<PaymentCheckApplyAmountRsp> checkApplyAmount(PaymentCheckApplyAmountReq req) {
        return R.ok(paymentService.checkApplyAmount(req));
    }

    @Override
    public R<RegisterSaveRsp> saveRegister(PaymentAutoRegisterReq req) {
        RegisterSaveReq saveReq = new RegisterSaveReq();
        if (req.getFiles() != null && !req.getFiles().isEmpty()) {
            for (MultipartFile file : req.getFiles()) {
                String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
                if (StringUtils.isBlank(suffix) || (!"jpg".equalsIgnoreCase(suffix) && !"pdf".equalsIgnoreCase(suffix))) {
                    throw new MithrasException("租赁物清单只能上传jpg或pdf文件");
                }
            }
        }
        FileListREQ fileListREQ = new FileListREQ();
        fileListREQ.setMainId(req.getPaymentId());
        fileListREQ.setModuleType("PAYMENT");
        List<Pair<String, List<FileListRSP>>> fileListRSP = fileService.listGroup(fileListREQ);
        if (fileListRSP == null || fileListRSP.isEmpty()) {
            throw new AuthCheckException("请在付款材料模块上传租赁物清单文件后，再进行中登网登记操作！");
        }
        Set<String> itemTypeSet = new HashSet<>();
        for (Pair<String, List<FileListRSP>> pair : fileListRSP) {
            itemTypeSet.add(pair.getKey());
        }
        if (!itemTypeSet.contains(LendingMaterialType.LEASE_ITEM.name())) {
            throw new AuthCheckException("请在付款材料模块上传租赁物清单文件后，再进行中登网登记操作！");
        }
        List<FileListRSP> leaseItemFileList = null;
        for (Pair<String, List<FileListRSP>> pair : fileListRSP) {
            if (StringUtils.isNotBlank(pair.getKey()) && (LendingMaterialType.LEASE_ITEM.name().equalsIgnoreCase(pair.getKey()))) {
                List<FileListRSP> fileListRSPList = pair.getValue();
                leaseItemFileList = pair.getValue();
                if (fileListRSPList != null && !fileListRSPList.isEmpty()) {
                    for (FileListRSP fileList : fileListRSPList) {
                        if (StringUtils.isBlank(fileList.getSuffix()) || (!"jpg".equalsIgnoreCase(fileList.getSuffix()) && !"pdf".equalsIgnoreCase(fileList.getSuffix()))) {
                            throw new MithrasException("租赁物清单只能上传jpg或pdf文件");
                        }
                    }
                }
            }
        }
        if (!sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name())) {
            throw new AuthCheckException("非运营管理,不可中登网登记");
        }
        PaymentDetailReq paymentDetailReq = new PaymentDetailReq();
        paymentDetailReq.setId(req.getPaymentId());
        R<PaymentDetailRsp> rspR = this.detail(paymentDetailReq);
        String leaseBusinessType = null;
        ContractBaseInfo contractBaseInfo = null;
        if (rspR != null && rspR.getData() != null) {
            PaymentDetailRsp detail = rspR.getData();
            if (StringUtils.isBlank(detail.getLeasedCurrency())) {
                throw new AuthCheckException("请维护租赁财产币种再进行中登网登记");
            }
            if (ObjectUtil.isNull(detail.getLeasedPrice())) {
                throw new AuthCheckException("请维护租赁财产价值再进行中登网登记");
            }
            if (detail.getContractId() != null) {
                contractBaseInfo = contractBaseInfoService.getById(detail.getContractId());
                if (YesOrNoNumberEnum.YES.getCode().equals(contractBaseInfo.getIsSaveRegister())) {
                    RegisterSaveRsp myObject = new RegisterSaveRsp();
                    myObject.setCode("000001");
                    myObject.setMessage(String.format("%s付款申请已经中登初始登记", contractBaseInfo.getContractCode()));
                    return R.ok(myObject);
                }
                ContractPriceDetailREQ priceDetailREQ = new ContractPriceDetailREQ();
                priceDetailREQ.setContractId(detail.getContractId());
                ContractPriceDetailRSP priceDetailRSP = priceService.detail(priceDetailREQ);
                int regtimeLimitClean = 0;
                if (priceDetailRSP != null && Objects.nonNull(priceDetailRSP.getMonthCount())) {
                    regtimeLimitClean = priceDetailRSP.getMonthCount() + 6;
                    saveReq.setRegtimeLimitClean(regtimeLimitClean);
                }
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, regtimeLimitClean);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(cal.getTime());
                saveReq.setRegDateEnd(formattedDate);
            }
            if (StringUtils.isNotBlank(detail.getLeaseTypeCode())) {
                leaseBusinessType = paymentService.getLeaseBusinessType(detail.getLeaseTypeCode());
            }
        }
        saveReq.setThirdUserId(AccountUtil.getLoginInfo().getId());
        saveReq.setIdentificationType("02");
        saveReq.setRegTypeNo("01");
        saveReq.setBusinessType(BUSINESS_TYPE);
        saveReq.setDebtorType("承租人");
        //债务人信息
        List<Debtor> debtorList = new ArrayList<>();
        saveReq.setDebtorList(debtorList);
        PaymentTransactionStructureInfoReq structureInfoReq = new PaymentTransactionStructureInfoReq();
        structureInfoReq.setPaymentId(req.getPaymentId());
        List<PaymentTransactionStructureInfoRsp> structureInfoRsps = baseInfoService.transactionStructureInfo(structureInfoReq);
        Set<String> nameList = new HashSet<>();
        Map<String, Set<Long>> map = new HashMap<>();
        if (structureInfoRsps != null && !structureInfoRsps.isEmpty()) {
            for (PaymentTransactionStructureInfoRsp infoRsp : structureInfoRsps) {
                if (LesseeTypeEnum.MAIN_LESSSEE.name().equalsIgnoreCase(infoRsp.getTransactionStructureType())
                        || LesseeTypeEnum.JOINT_LESSEE.name().equalsIgnoreCase(infoRsp.getTransactionStructureType())) {
                    nameList.add(infoRsp.getClientName());
                    map.putIfAbsent(infoRsp.getClientName(), new HashSet<>());
                    map.get(infoRsp.getClientName()).add(infoRsp.getClientId());
                }
            }
        }
        if (nameList.isEmpty()) {
            throw new MithrasException("无主承租人/联合承租人");
        }
        for (Map.Entry<String, Set<Long>> entry : map.entrySet()) {
            Debtor debtor = new Debtor();
            String name = entry.getKey();
            Set<Long> clientIdSet = entry.getValue();
            if (!clientIdSet.isEmpty()) {
                for (Long clientId : clientIdSet) {
                    debtor.setIndustrycode("9999");
                    debtor.setName(name);
                    debtorList.add(debtor);
                    CorpCommerceInfoDetailREQ infoDetailREQ = new CorpCommerceInfoDetailREQ();
                    infoDetailREQ.setClientId(clientId);
                    R<CorpCommerceInfoDetailRSP> commerceInfoDetailRSPR = corpCommerceInfoController.detail(infoDetailREQ);
                    if (commerceInfoDetailRSPR != null && commerceInfoDetailRSPR.getData() != null) {
                        CorpCommerceInfoDetailRSP rsp = commerceInfoDetailRSPR.getData();
                        if (StringUtils.isNotBlank(rsp.getOrgType())) {
                            debtor.setType(paymentService.getOrgCode(rsp.getOrgType()));
                        }
                        if (StringUtils.isNotBlank(rsp.getUscCode())) {
                            debtor.setOrgCode(rsp.getUscCode());
                            debtor.setCreditCode(rsp.getUscCode());
                        }
                        if (StringUtils.isNotBlank(rsp.getCorpRepresent())) {
                            debtor.setFrName(rsp.getCorpRepresent());
                        }
                        if (StringUtils.isNotBlank(rsp.getOrgScale())) {
                            debtor.setSize(paymentService.getSize(rsp.getOrgScale()));
                        }
                    }
                    CorpAddressInfoListREQ corpAddressInfoListREQ = new CorpAddressInfoListREQ();
                    corpAddressInfoListREQ.setClientId(clientId);
                    R<PageR<CorpAddressInfoListRSP>> pageR = corpAddressInfoController.list(corpAddressInfoListREQ);
                    if (pageR != null && pageR.getData() != null) {
                        List<CorpAddressInfoListRSP> corpAddressInfoListRSPList = pageR.getData().getList();
                        if (corpAddressInfoListRSPList != null && !corpAddressInfoListRSPList.isEmpty()) {
                            for (CorpAddressInfoListRSP corpAddressInfoListRSP : corpAddressInfoListRSPList) {
                                if (CorpAddressType.REGISTRY_ADDRESS.name().equalsIgnoreCase(corpAddressInfoListRSP.getAddressType())) {
                                    if ("中国".equalsIgnoreCase(corpAddressInfoListRSP.getCountryName())) {
                                        debtor.setAddressType(CHN);
                                    } else {
                                        debtor.setAddressType(OTH);
                                    }
                                    if (StringUtils.isNotBlank(corpAddressInfoListRSP.getDetail())) {
                                        debtor.setAddress(corpAddressInfoListRSP.getDetail());
                                    }
                                    if (StringUtils.isNotBlank(corpAddressInfoListRSP.getProvinceName())) {
                                        debtor.setProvince(corpAddressInfoListRSP.getProvinceName());
                                    }
                                    if (StringUtils.isNotBlank(corpAddressInfoListRSP.getCityName())) {
                                        debtor.setCity(paymentService.getCity(corpAddressInfoListRSP));
                                    }
                                    if (StringUtils.isNotBlank(corpAddressInfoListRSP.getDistrictName())) {
                                        debtor.setCounty(corpAddressInfoListRSP.getDistrictName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //债权人信息
        saveReq.setPawneeType("出租人");
        List<Pawnee> pawneeList = new ArrayList<>();
        Pawnee pawnee = new Pawnee();
        pawneeList.add(pawnee);
        saveReq.setPawneeList(pawneeList);
        pawnee.setType(DEBTOR_TYPE);
        pawnee.setOrgType1(ORG_TYPE1);
        pawnee.setOrgType2(ORG_TYPE2);
        pawnee.setOrgType3(ORG_TYPE3);
        pawnee.setName(FinancialConstants.RZZL_NAME);
        pawnee.setOrgCode("91430000329604053K");
        pawnee.setCreditCode("91430000329604053K");
        pawnee.setFrName("王俊");
        pawnee.setAddressType(CHN);
        pawnee.setAddress("浙江省杭州市萧山区天人大厦11-12楼");
        pawnee.setProvince("浙江省");
        pawnee.setCity("杭州市");
        pawnee.setCounty("萧山区");
        //财产信息
        ContractInfo contractInfo = new ContractInfo();
        if (contractBaseInfo != null) {
            contractInfo.setContractId(String.valueOf(contractBaseInfo.getId()));
            contractInfo.setMainNo(contractBaseInfo.getContractCode());
        }
        contractInfo.setMainType("租赁合同");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(cal.getTime());
        contractInfo.setDebtDateBegin(formattedDate);
        contractInfo.setDebtDateEnd(saveReq.getRegDateEnd());
        contractInfo.setDescribe("详见附件");
        contractInfo.setLeaseBusinessType(leaseBusinessType);
        saveReq.setContractInfo(contractInfo);
        //租赁财产统计
        List<LeasedStatistic> leasedStatistics = new ArrayList<>();
        LeasedStatistic leasedStatistic = new LeasedStatistic();
        leasedStatistic.setLeasedType("15");
        leasedStatistic.setLeasedCount(1);
        if (rspR != null && rspR.getData() != null) {
            PaymentDetailRsp detail = rspR.getData();
            leasedStatistic.setLeasedCurrency(detail.getLeasedCurrency());
            contractInfo.setMainCurrency(detail.getLeasedCurrency());
            contractInfo.setMainSum(new BigDecimal(detail.getLeasedPrice()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP));
            leasedStatistic.setLeasedPrice(new BigDecimal(detail.getLeasedPrice()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP));
        }

        leasedStatistics.add(leasedStatistic);
        saveReq.setLeasedStatistics(leasedStatistics);
        RegisterSaveRsp myObject;
        try {
            String rsp = paymentService.callRemote(saveReq, leaseItemFileList);
            ObjectMapper objectMapper = new ObjectMapper();
            myObject = objectMapper.readValue(rsp, RegisterSaveRsp.class);
        } catch (Exception e) {
            throw new MithrasException(e.getMessage());
        }
        if ("000000".equalsIgnoreCase(myObject.getCode()) && contractBaseInfo != null) {
            LambdaUpdateWrapper<ContractBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ContractBaseInfo::getIsSaveRegister, YesOrNoNumberEnum.YES.getCode());
            updateWrapper.eq(ContractBaseInfo::getId, contractBaseInfo.getId());
            contractBaseInfoMapper.update(null, updateWrapper);
        } else {
            throw new MithrasException(myObject.getMessage());
        }
        return R.ok(myObject);
    }

}
