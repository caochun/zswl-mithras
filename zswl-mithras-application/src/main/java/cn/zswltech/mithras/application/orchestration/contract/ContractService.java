package cn.zswltech.mithras.application.orchestration.contract;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractAocPriceService;
import cn.zswltech.mithras.contract.core.ContractFactoringPriceService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.ContractPrepaymentService;
import cn.zswltech.mithras.contract.core.ContractRemindRecordService;
import cn.zswltech.mithras.contract.core.ContractDeductRentInfoService;
import cn.zswltech.mithras.workflow.enums.ProcessVarEnum;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.*;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseAdjustEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.*;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.workflow.flow.helper.CalBoardRuleHelper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractMortgageMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractPledgeMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.workflow.model.ProcessModifyRemark;
import cn.zswltech.mithras.afterlease.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.workflow.process.FlowAssistService;
import cn.zswltech.mithras.contract.event.ContractPriceChangeEvent;
import cn.zswltech.mithras.collection.event.CollectionAddEvent;
import cn.zswltech.mithras.workflow.process.ProcessModifyRemarkService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.projectprocess.application.bo.ContractConstitutionFileBO;
import cn.zswltech.mithras.application.orchestration.client.ClientAuthorityService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.application.orchestration.client.ClientTransferService;
import cn.zswltech.mithras.application.orchestration.client.ProjClientRoleService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.effectcheck.ContractEffectCheckFactory;
import cn.zswltech.mithras.contract.core.operationprepare.ContractOperationPrepare;
import cn.zswltech.mithras.contract.application.process.prepare.ContractOperationPrepareFactory;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractVersionService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.ProjectLifecycleEventService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import cn.zswltech.mithras.collection.application.financial.FinancialManagerService;
import cn.zswltech.mithras.creditreport.util.CreditReportUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.associationreport.constant.MithrasConstants.ERR_IN_TRANSFER;
import static cn.zswltech.mithras.foundation.enums.CashFlowItemEnum.*;
import static cn.zswltech.mithras.foundation.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import cn.zswltech.mithras.contract.core.ContractConstitutionFileService;
import cn.zswltech.mithras.contract.core.ContractSettlePlanService;
import cn.zswltech.mithras.contract.core.ContractSpecialTraderService;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.contract.core.ContractRentActualService;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Slf4j
@Service
public class ContractService implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractVersionService contractVersionService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractBaseInfoService baseInfoService;
    @Resource
    private ContractPriceService priceService;
    @Resource
    private ContractLeasePriceService leasePriceService;
    @Resource
    private ContractFactoringPriceService factoringPriceService;
    @Resource
    private ContractAocPriceService aocPriceService;
    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private CalBoardRuleHelper calBoardRuleHelper;
    @Resource
    private ContractRemindRecordService contractRemindRecordService;
    @Resource
    private ContractSpecialTraderService contractSpecialTraderService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private ContractPrepaymentService contractPrepaymentService;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractRentActualLibMapper contractRentActualLibMapper;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;
    @Resource
    private FinancialManagerService financialManagerService;
    @Resource
    private MetricComputeEventBus metricComputeEventBus;

    @Resource
    private ProjectLifecycleEventService projectLifecycleEventService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ContractPledgeMapper contractPledgeMapper;
    @Resource
    private ContractMortgageMapper contractMortgageMapper;
    @Resource
    private ContractConstitutionFileService contractConstitutionFileService;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private ContractDeductRentInfoService contractDeductRentInfoService;

    public FtpIndustryCategoryEnum getFtpIndustryCategory(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(contractBaseInfo.getProjReviewId());
        if (Objects.nonNull(projReviewBaseInfo)) {
            ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoService.getPricingByReviewIdExcludeProjName(projReviewBaseInfo.getId());
            if (Objects.nonNull(projPricingBaseInfo) && StrUtil.isNotBlank(projPricingBaseInfo.getFtpIndustryCategory())) {
                return FtpIndustryCategoryEnum.getByName(projPricingBaseInfo.getFtpIndustryCategory());
            }
            if (StrUtil.isNotBlank(projReviewBaseInfo.getFtpIndustryCategory())) {
                return FtpIndustryCategoryEnum.getByName(projReviewBaseInfo.getFtpIndustryCategory());
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void syncProjReviewInfo(ContractBaseInfo contractBaseInfo, ProjReviewBaseInfo projReviewBaseInfo) {
        // 覆盖合同基本信息表
        this.replaceInBaseInfo(contractBaseInfo, projReviewBaseInfo);
        contractBaseInfoService.updateById(contractBaseInfo);
        // 覆盖报价方案（只覆盖冗余字段和合同变更无法修改的字段），保理和债权转让没有不可修改字段，不处理
        if (Objects.equals(ProjectBizType.ZL.name(), contractBaseInfo.getBizType()) || Objects.equals(ProjectBizType.ZZ.name(), contractBaseInfo.getBizType())) {
            ContractLeasePrice contractLeasePrice = leasePriceService.getByContractId(contractBaseInfo.getId());
            if (Objects.nonNull(contractLeasePrice)) {
                this.replaceInLeasePrice(contractLeasePrice, projReviewBaseInfo);
                leasePriceService.updateById(contractLeasePrice);
            }
        }
        if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.NEW.name())) {
            // 新建状态无需生成数据版本
            return;
        }
        // 合同数据升级版本
        contractVersionService.recordVersion(contractBaseInfo.getMainId(), VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
    }

    public void prepareContractOperation(Long contractId, String operation) {
        /*提交还款和合同结清 需要添加校验 如果当前有保证金退抵流程在流程中，不可以操作*/
        if(ContractOperationEnum.SETTLE_NORMAL.name().equals(operation) || ContractOperationEnum.SETTLE_IN_ADVANCE.name().equals(operation) ||
                ContractOperationEnum.CHANGE_REPAYMENT_IN_ADVANCE.name().equals(operation)){
            if(!contractDeductRentInfoService.checkByContractId(contractId.toString())){
                throw new MithrasException("合同处于保证金退抵流程中，请等待结束后操作！");
            }
        }
        ContractOperationEnum contractOperationEnum = ContractOperationEnum.find(operation);
        if (Objects.isNull(contractOperationEnum)) {
            log.error("未定义的合同操作场景[contractId: {}, operation: {}]", contractId, operation);
            throw new MithrasException("未定义的合同操作场景");
        }
        ContractOperationPrepare contractOperationPrepare = ContractOperationPrepareFactory.getInstance(contractOperationEnum);
        if (Objects.isNull(contractOperationPrepare)) {
            log.error("没有找到符合条件的合同操作前置处理器[contractId: {}, operation: {}]", contractId, operation);
            throw new MithrasException("未定义的合同操作场景");
        }
        //只有合同结清才会加该校验
        if(ContractOperationEnum.SETTLE_NORMAL.name().equals(operation)){
            //查询条件：1、该笔合同下的；2、非核销完毕；3、现金流项目包括（租金、首期租金、名义价款）
            List<CollectionBaseInfo> collectionBaseInfoList = getBean(CollectionBaseInfoMapper.class).selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, contractId)
                    .ne(CollectionBaseInfo::getWriteOffStatus,WRITE_OFF_COMPLETED.name())
                    .in(CollectionBaseInfo::getCashFlowItem,RENT.name(), FIRST_RENT.name(), NOMINAL_PRICE.name())
            );
            //如果存在未核销完毕的租金，循环查询现金流项目名称，然后拼接提示信息
            if(!collectionBaseInfoList.isEmpty()){
                StringBuilder cashFlowItem = new StringBuilder();
                for(CollectionBaseInfo collectionBaseInfo:collectionBaseInfoList){
                    //判断是否租金
                    if(!collectionBaseInfo.getCashFlowItem().isEmpty()&&RENT.name().equals(collectionBaseInfo.getCashFlowItem())){
                        if(!cashFlowItem.toString().contains("、"+RENT.display)){
                            cashFlowItem.append("、").append(RENT.display);
                        }
                    }
                    //判断是否首期租金
                    if(!collectionBaseInfo.getCashFlowItem().isEmpty()&&FIRST_RENT.name().equals(collectionBaseInfo.getCashFlowItem())){
                        if(!cashFlowItem.toString().contains(FIRST_RENT.display)){
                            cashFlowItem.append("、").append(FIRST_RENT.display);
                        }
                    }
                    //判断是否名义价款
                    if(!collectionBaseInfo.getCashFlowItem().isEmpty()&&NOMINAL_PRICE.name().equals(collectionBaseInfo.getCashFlowItem())){
                        if(!cashFlowItem.toString().contains(NOMINAL_PRICE.display)){
                            cashFlowItem.append("、").append(NOMINAL_PRICE.display);
                        }
                    }
                }
                cashFlowItem.deleteCharAt(0);//去掉最前的字符（、）
                throw new MithrasException("NO_WRITE_OFF_COMPLETED@现金流项目"+cashFlowItem+"未核销完毕，请完成后再进行结清操作！");
            }
        }


        //合同结清 准备发起的时候将合同下退抵信息（审批通过）对应的附件  全部给到这里
        if(ContractOperationEnum.SETTLE_NORMAL.name().equals(operation) || ContractOperationEnum.CHANGE_REPAYMENT_IN_ADVANCE.name().equals(operation)){
            contractDeductRentInfoService.sendFileToSettle(contractId, operation);
        }
        contractOperationPrepare.prepare(contractId);
    }

    public Boolean checkCombinedIrr(Long contractId, String operation, Long commitPrepareId) {
        if (Objects.isNull(commitPrepareId) && Objects.isNull(contractId)) {
            log.error("合同ID和提交准备ID不能同时为空");
            throw new MithrasException("未关联到合同信息");
        }
        if (Objects.isNull(contractId)) {
            CommonProcessPrepare prepare = getBean(CommonProcessPrepareService.class).getById(commitPrepareId);
            PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoService.class).getById(Long.valueOf(prepare.getBusinessId()));
            contractId = Long.valueOf(paymentBaseInfo.getContractId());
            if (Objects.isNull(contractId)) {
                log.error("未关联到业务信息");
                throw new MithrasException("未关联到合同信息");
            }
        }

        // 只处理合同起租和新增投放
        if (!StrUtil.equals(ContractOperationEnum.START_RENT.name(), operation) &&
                !StrUtil.equals(ContractOperationEnum.NEW_RECEIPT.name(), operation)) {
            log.error("仅在合同起租和新增投放时校验综合IRR");
            throw new MithrasException("未定义的合同操作场景");
        }

        // 最新生效或已投放付款记录, PaymentStatusEnum ,TAKE_EFFECT("生效"),  FINISHED("已投放");
        List<PaymentBaseInfo> paymentBaseInfos = getBean(PaymentBaseInfoMapper.class).selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getContractId, contractId)
                .in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.FINISHED.name(), PaymentStatusEnum.TAKE_EFFECT.name()))
                .orderByDesc(PaymentBaseInfo::getPaidInDate));
        if (CollectionUtil.isEmpty(paymentBaseInfos) || Objects.isNull(paymentBaseInfos.get(0).getLowestIrr())) {
            log.error("未找到该笔合同下有效最低IRR[contractId: {}]", contractId);
            throw new MithrasException("未找到该笔合同下有效最低IRR");
        }
        //paymentBaseInfos按日期倒序，日期取值字段paid_in_date，如果paid_in_date没有值取apply_payment_date
        paymentBaseInfos = paymentBaseInfos.stream()
                .sorted(Comparator.comparing(
                        item -> Optional.ofNullable(item.getPaidInDate())
                                .map(LocalDate::atStartOfDay)
                                .orElseGet(() -> Optional.ofNullable(item.getApplyPaymentDate())
                                        .map(LocalDateTime::toLocalDate)
                                        .orElse(null).atStartOfDay()),
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList());
        // 最低IRR
        BigDecimal lowestIrr = BigDecimal.valueOf(paymentBaseInfos.get(0).getLowestIrr());
        log.info("最新的最低IRR：" + lowestIrr);
        // 查询合同下所有借据 ，contract_receipt
        List<ContractReceipt> allContractReceipts = getBean(ContractReceiptMapper.class).selectList(Wrappers.<ContractReceipt>lambdaQuery()
                .eq(ContractReceipt::getContractId, contractId));
        List<ContractReceipt> contractReceipts = allContractReceipts.stream().filter(contractReceipt -> contractReceipt.getActualIrr() != null).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(allContractReceipts)) {
            log.error("未找到该笔合同下存在实际irr的借据[contractId: {}]", contractId);
            return false;
        }
        //提取contractReceipts的id
        List<Long> receiptIds = contractReceipts.stream().map(ContractReceipt::getId).collect(Collectors.toList());
        // 按id分组，建立 借据id 和 实际irr 映射
        Map<Long, Integer> actualIrrMap = contractReceipts.stream().filter(contractReceipt -> contractReceipt.getActualIrr() != null).collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getActualIrr));
        // 查询实际租金表，contract_rent_actual
        List<ContractRentActual> allContractRentActuals = getBean(ContractRentActualMapper.class).selectList(Wrappers.<ContractRentActual>lambdaQuery()
                .eq(ContractRentActual::getContractId, contractId));
        List<ContractRentActual> contractRentActuals = allContractRentActuals.stream().filter(contractRentActual -> receiptIds.contains(contractRentActual.getReceiptId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contractRentActuals)) {
            log.error("未找到该笔合同下对应借据的实际租金表");
            return false;
        }
        // 计算分子
        BigDecimal allPart = BigDecimal.ZERO;
        for (ContractReceipt contractReceipt : contractReceipts) {
            // 实际IRR
            Integer actualIrr = actualIrrMap.get(contractReceipt.getId());
            // 实际租金本金总和
            BigDecimal principal = countPrincipal(contractReceipt.getId(), contractRentActuals);
            if (principal == null) {
                log.info("未计算出该笔借据实际租金表本金总和，跳过；借据id = " + contractReceipt.getId());
                continue;
            }
            BigDecimal part = principal.divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(actualIrr).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP));
            allPart = allPart.add(part);
        }
        log.info("加权平均IRR分子：" + allPart);
        // 分母，计算contractRentActuals 所有本金总额
        BigDecimal countApplyAmt = contractRentActuals.stream()
                .map(contractRentActual ->
                        Optional.ofNullable(contractRentActual.getPrincipal())
                                .map(BigDecimal::valueOf)
                                .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP);
        log.info("加权平均IRR分母：" + countApplyAmt);
        // 加权平均IRR
        BigDecimal combinedIrr = allPart.divide(countApplyAmt, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000));
        // 判断加权平均IRR 是否小于最低IRR,小于则需要发出提示
        if (combinedIrr.compareTo(lowestIrr) < 0) {
            return false;
        }
        return true;
    }


    private BigDecimal countPrincipal(Long id, List<ContractRentActual> contractRentActuals) {
        List<ContractRentActual> rentActuals = contractRentActuals.stream().filter(contractRentActual -> contractRentActual.getReceiptId().equals(id)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(rentActuals)) {
            return null;
        }
        return rentActuals.stream()
                .map(contractRentActual ->
                        Optional.ofNullable(contractRentActual.getPrincipal())
                                .map(BigDecimal::valueOf)
                                .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }


    public boolean checkCanDoChange(Long contractId, String changeType) {
        if (Objects.equals(changeType, ContractChangeTypeEnum.OTHER.name())) {
            // 其他类型的合同变更不做限制
            return true;
        }
        // 查询合同最新版本
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(contractId);
        if (Objects.isNull(contractBaseInfoLib)) {
            return false;
        }
        // 查询实际租金表的版本表，最新版本只要存在有现金流编号的数据就认为存在有效借据，则允许发起合同变更，反之则不允许
        LambdaQueryWrapper<ContractRentActualLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActual::getContractId, contractId);
        query.eq(ContractRentActualLib::getVersion, contractBaseInfoLib.getVersion());
        List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibMapper.selectList(query);
        if (CollectionUtil.isEmpty(contractRentActualLibList)) {
            return false;
        }
        for (ContractRentActualLib contractRentActualLib : contractRentActualLibList) {
            if (StrUtil.isNotBlank(contractRentActualLib.getCashFlowCode())) {
                return true;
            }
        }
        return false;
    }

    public void checkZhongZhengCode(Long clientId) {
//        CorpCommerceInfo detail = commerceInfoService.detail(clientId);
//        if (Objects.isNull(detail) || StringUtils.isBlank(detail.getZhongZhengCode())) {
//            throw new MithrasException("中征码为空，请至【客户管理】维护！");
//        }
        Client client = clientService.getById(clientId);
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在"));
        Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("客户生效数据不存在"));
        CorpCommerceInfoDetailRSP corpCommerceInfoDetailRSP = corpCommerceInfoLibService.detail(clientId, client.getNewestVersion());
        Assert.notBlank(corpCommerceInfoDetailRSP.getZhongZhengCode(), () -> MithrasException.newException("中征码为空，请至【客户管理】维护！"));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void effect(ContractFlowBasicREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (clientTransferService.inTransfer(contractBaseInfo.getClientId())) {
            err(ERR_IN_TRANSFER);
        }
        if (!Objects.equals(ContractStatus.NEW.name(), contractBaseInfo.getContractStatus())) {
            throw new MithrasException("合同非新建状态");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(contractBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        //项目评审非失效状态
        if (SpringContextHolder.getBean(ProjReviewBaseInfoService.class).count(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getId, contractBaseInfo.getProjReviewId())
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.EXPIRE.name())) > 0) {
            throw new MithrasException("此项目评审已失效！流程无法提交");
        }
        List<ContractTenantry> tList = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getResolutionType, ResolutionTypeEnum.OTHER.name())
                .eq(ContractTenantry::getResolutionFileId, JSON.toJSONString(Collections.emptyList()))
                .eq(ContractTenantry::getContractId, contractBaseInfo.getId()));
        List<ContractGuarantor> gList = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                .eq(ContractGuarantor::getResolutionType, ResolutionTypeEnum.OTHER.name())
                .eq(ContractGuarantor::getResolutionFileId, JSON.toJSONString(Collections.emptyList()))
                .eq(ContractGuarantor::getContractId, contractBaseInfo.getId()));
        if (CollectionUtils.isNotEmpty(tList) || CollectionUtils.isNotEmpty(gList)) {
            throw new MithrasException("决议类型为其他的《决议文件》不存在，请上传《决议文件》后再提交流程！");
        }
        checkPricingIrr(contractBaseInfo);
        if (this.isInProcess(contractBaseInfo.getId())) {
            throw new MithrasException("该合同数据变动已处于流程中，无法提交数据");
        }
        // 校验
        ContractEffectCheckFactory.getInstance(contractBaseInfo.getBizType()).check(contractBaseInfo, false);
        if (ObjectUtil.isNotNull(projReviewBaseInfoService.findRelatedProcess(contractBaseInfo.getProjReviewId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交合同流程！");
        }
        //检查抵质押文件
        checkMortgagePledgeFile(contractBaseInfo);
        if (req.getOnlyCheck()) {
            return;
        }
        //更新部门领导
        contractBaseInfoService.renewLeader(contractBaseInfo.getId());
        // 生成流程实例
        StartProcessReq startProcessReq = buildCommonStartProcessReq(contractBaseInfo);
        startProcessReq.setModelKey(ProcessModelTypeEnum.ContractCreateFlow.name());
        startProcessReq.setSubModule(ContractFlowSubModuleEnum.CREATE_ALL.name());
        if (CharSequenceUtil.isNotBlank(req.getRemark())) {
            startProcessReq.getVariables().putAll(MapUtil.of(ProcessNodeVariableEnum.START_USER_NODE_MESSAGE.getName(), req.getRemark()));
        }
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
        projectLifecycleEventService.add("合同创建审批", ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", contractBaseInfo.getProjReviewId());
        // 合同流程状态变更为新建审批中
        ContractBaseInfo toUpdate = new ContractBaseInfo();
        toUpdate.setId(contractBaseInfo.getId());
        toUpdate.setContractProcessStatus(ContractProcessStatusEnum.NEW_COMMIT.name());
        contractBaseInfoService.updateById(toUpdate);

        projClientRoleService.projContractSubmit(contractBaseInfo.getId());
    }

    private void checkPricingIrr(ContractBaseInfo contractBaseInfo) {
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.detail(new ContractPriceDetailREQ(contractBaseInfo.getId()));
        if (ObjectUtil.isNotEmpty(contractPriceDetailRSP)) {
            ProjReviewBaseInfo reviewBaseInfo = SpringContextHolder.getBean(ProjReviewBaseInfoService.class).getById(contractBaseInfo.getProjReviewId());
            ProjPricingBaseInfo pricingBaseInfo = projPricingBaseInfoService.getPricingByReview(reviewBaseInfo);
            ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(pricingBaseInfo.getId());
            if (contractPriceDetailRSP.getIrr() < pricingPriceDetailRSP.getIrr()) {
                throw new MithrasException("当前合同概算IRR低于项目定价IRR，请调整后再提交流程！");
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void confirmEffect(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //校验
        if (!ContractProcessStatusEnum.START_RENT_PASS.name().equals(contractBaseInfo.getContractProcessStatus())) {
            throw new MithrasException("仅结清审批通过合同可发起");
        }
        // 生成流程实例
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(contractBaseInfo.getId()));
        startProcessReq.setProcessInstanceName(contractBaseInfo.getContractCode());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(contractBaseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        processApiService.start(startProcessReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addNewReceipt(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 校验实际IRR
        // modify 260202 新增投放增加前置校验，校验合同加权平均IRR是否大于最新通过的付款申请的最低IRR
        //contractReceiptService.checkActualIrrByContractId(contractId);
        if (clientTransferService.inTransfer(contractBaseInfo.getClientId())) {
            err(ERR_IN_TRANSFER);
        }
        if (!Objects.equals(ContractStatus.TAKE_EFFECT.name(), contractBaseInfo.getContractStatus()) && !Objects.equals(ContractStatus.START_RENT.name(), contractBaseInfo.getContractStatus())) {
            throw new MithrasException("合同生效或起租才可以新增借据");
        }
        if (this.isInProcess(contractId)) {
            throw new MithrasException("该合同数据变动已处于流程中，无法提交数据");
        }
        ChangeDTO changeDTO = contractVersionService.checkActualChange(contractId);
        Assert.isTrue(changeDTO.getChangeFlag(), () -> MithrasException.newException("数据未变动，无需提交"));
        if (ObjectUtil.isNotNull(projReviewBaseInfoService.findRelatedProcess(contractBaseInfo.getProjReviewId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交合同流程！");
        }
        //更新部门领导
        contractBaseInfoService.renewLeader(contractId);
        // 计算借据的FTP价格考核信息
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractReceiptList)) {
            contractReceiptList.forEach(contractReceipt -> SpringUtil.getBean(FtpAssessmentInfoService.class).tryRecalculateFtpAssessmentInfo(contractReceipt.getId()));
        }
        StartProcessReq startProcessReq = buildCommonStartProcessReq(contractBaseInfo);
        startProcessReq.setModelKey(ProcessModelTypeEnum.ContractAddNewReceiptFlow.name());
        startProcessReq.setSubModule(ContractFlowSubModuleEnum.ADD_NEW_RECEIPT.name());
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
        projectLifecycleEventService.add("合同新增借据审批", ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", contractBaseInfo.getProjReviewId());
        // 合同流程状态变更为新增借据审批中
        ContractBaseInfo toUpdate = new ContractBaseInfo();
        toUpdate.setId(contractBaseInfo.getId());
        toUpdate.setContractProcessStatus(ContractProcessStatusEnum.NEW_RECEIPT_COMMIT.name());
        contractBaseInfoService.updateById(toUpdate);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void startRent(ContractFlowStartRentREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!Objects.equals(ContractStatus.TAKE_EFFECT.name(), contractBaseInfo.getContractStatus())) {
            throw new MithrasException("合同非生效状态");
        }
        if (this.isInProcess(req.getContractId())) {
            throw new MithrasException("该合同数据变动已处于流程中，无法提交数据");
        }
        // 校验实际IRR
        // modify 260202 新增投放增加前置校验，校验合同加权平均IRR是否大于最新通过的付款申请的最低IRR
        // contractReceiptService.checkActualIrrByContractId(contractBaseInfo.getId());
        ChangeDTO changeDTO = contractVersionService.checkActualChange(req.getContractId());
        Assert.isTrue(changeDTO.getChangeFlag(), () -> MithrasException.newException("数据未变动，无需提交"));
        startRentCheck(contractBaseInfo);
        if (ObjectUtil.isNotNull(projReviewBaseInfoService.findRelatedProcess(contractBaseInfo.getProjReviewId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交合同流程！");
        }
        // 计算借据的FTP价格考核信息
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractReceiptList)) {
            contractReceiptList.forEach(contractReceipt -> SpringUtil.getBean(FtpAssessmentInfoService.class).tryRecalculateFtpAssessmentInfo(contractReceipt.getId()));
        }
        //更新部门领导
        contractBaseInfoService.renewLeader(req.getContractId());
        StartProcessReq startProcessReq = buildCommonStartProcessReq(contractBaseInfo);
        startProcessReq.setModelKey(ProcessModelTypeEnum.ContractStartRentFlow.name());
        startProcessReq.setSubModule(ContractFlowSubModuleEnum.START_RENT.name());
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
        projectLifecycleEventService.add("合同起租审批", ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", contractBaseInfo.getProjReviewId());
        // 合同流程状态变更为起租审批中
        ContractBaseInfo toUpdate = new ContractBaseInfo();
        toUpdate.setId(contractBaseInfo.getId());
        toUpdate.setContractProcessStatus(ContractProcessStatusEnum.START_RENT_COMMIT.name());
        contractBaseInfoService.updateById(toUpdate);
    }

    public void startRentCheck(ContractBaseInfo contractBaseInfo) {
        Assert.notNull(contractBaseInfo.getActualLeaseDate(), () -> MithrasException.newException("实际起租日不能为空"));
        // 校验实际租金表是否有现金流编号
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByContract(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractRentActualList)) {
            for (ContractRentActual contractRentActual : contractRentActualList) {
                Assert.notBlank(contractRentActual.getCashFlowCode(), () -> MithrasException.newException("实际租金表现金流编号不能为空"));
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public String change(ContractFlowChangeREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (clientTransferService.inTransfer(contractBaseInfo.getClientId())) {
            err(ERR_IN_TRANSFER);
        }
        if (this.isInProcess(req.getContractId())) {
            throw new MithrasException("该合同数据变动已处于流程中，无法提交数据");
        }
        checkPricingIrr(contractBaseInfo);
        ChangeDTO changeDTO = contractVersionService.checkActualChange(req.getContractId());
        Assert.isTrue(changeDTO.getChangeFlag(), () -> MithrasException.newException("数据未变动，无需提交"));
        if (ObjectUtil.isNotNull(projReviewBaseInfoService.findRelatedProcess(contractBaseInfo.getProjReviewId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交合同流程！");
        }
        ContractChangeTypeEnum changeType = Optional.ofNullable(req.getChangeType()).map(ContractChangeTypeEnum::of).orElseThrow(() -> new MithrasException("合同变更类型不合法"));
        String event = null;
        if (!checkCanDoChange(contractBaseInfo.getId(), changeType.name())) {
            throw new MithrasException("合同不存在生效的借据");
        }
        //更新部门领导
        contractBaseInfoService.renewLeader(req.getContractId());
        StartProcessReq startProcessReq = buildCommonStartProcessReq(contractBaseInfo);
        Map<String, Object> varMap = startProcessReq.getVariables();
        if (Objects.isNull(varMap)) {
            varMap = new HashMap<>();
        }
        String result;
        switch (changeType) {
            case LPR_CHANGE:
                // 校验实际IRR
                //contractReceiptService.checkActualIrrByContractId(contractBaseInfo.getId());
                result = this.checkLowestIrrAndkAverageIrr(contractBaseInfo, changeType);
                if (req.getOnlyCheck() && StringUtils.isNotBlank(result)){
                    return result;
                }
                //检查抵质押文件
                checkMortgagePledgeFile(contractBaseInfo);
                startProcessReq.setModelKey(ProcessModelTypeEnum.ContractLPRChangeFlow.name());
                startProcessReq.setSubModule(ContractFlowSubModuleEnum.LPR_CHANGE.name());
                //baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_COMMIT.name(), ContractChangeTypeEnum.LPR_CHANGE.name(), req.getContractId());
                event = "合同LPR调整审批";
                break;
            case EARLY_REPAYMENT:
                // 校验实际IRR
                contractReceiptService.checkActualIrrByContractId(contractBaseInfo.getId());
                //提前展期判断
//                contractPrepaymentCheck(contractBaseInfo);
                startProcessReq.setModelKey(ProcessModelTypeEnum.ContractEarlyRepayFlow.name());
                startProcessReq.setSubModule(ContractFlowSubModuleEnum.EARLY_REPAYMENT.name());
                // 保存提交流程时候的提前还款日期
                ContractPrepayment contractPrepayment = contractPrepaymentService.getList(new ContractIdListREQ(contractBaseInfo.getId()));
                if (Objects.isNull(contractPrepayment) || Objects.isNull(contractPrepayment.getApplayRepaymentDate())) {
                    throw new MithrasException("<提前还款日期>不能为空");
                }
                varMap.put(ProcessVarEnum.contractEarlyRepayOriginalDate.name(), LocalDateTimeUtil.format(contractPrepayment.getApplayRepaymentDate(), DatePattern.NORM_DATE_PATTERN));
                //baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_COMMIT.name(), ContractChangeTypeEnum.EARLY_REPAYMENT.name(), req.getContractId());
                event = "合同提前还款审批";
                break;
            case EXTENSION:
                // 校验实际IRR
                //contractReceiptService.checkActualIrrByContractId(contractBaseInfo.getId());
                result = this.checkLowestIrrAndkAverageIrr(contractBaseInfo, changeType);
                if (req.getOnlyCheck() && StringUtils.isNotBlank(result)){
                    return result;
                }
                //检查抵质押文件
                checkMortgagePledgeFile(contractBaseInfo);
                //展期租后检查
                if (ObjectUtil.isNull(afterLeaseAdjustInfoService.adjustBaseLastByprojId(contractBaseInfo.getProjReviewId(), AfterLeaseAdjustEnum.EXTEND.name()))) {
                    throw new MithrasException("请先在【租后管理】申请项目调整！");
                }
                fillVarMap(contractBaseInfo, varMap);
                startProcessReq.setModelKey(ProcessModelTypeEnum.ContractExtensionFlow.name());
                startProcessReq.setSubModule(ContractFlowSubModuleEnum.EXTENSION.name());
                //baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_COMMIT.name(), ContractChangeTypeEnum.EXTENSION.name(), req.getContractId());
                event = "合同展期审批";
                break;
            case CHANGE_REPAY_PLAN:
                // 校验实际IRR
                //contractReceiptService.checkActualIrrByContractId(contractBaseInfo.getId());
                result = this.checkLowestIrrAndkAverageIrr(contractBaseInfo, changeType);
                if (req.getOnlyCheck() && StringUtils.isNotBlank(result)){
                    return result;
                }
                //检查抵质押文件
                checkMortgagePledgeFile(contractBaseInfo);
                //调整还款计划租后检查
                if (ObjectUtil.isNull(afterLeaseAdjustInfoService.adjustBaseLastByprojId(contractBaseInfo.getProjReviewId(), AfterLeaseAdjustEnum.REPAYMENT.name()))) {
                    throw new MithrasException("请先在【租后管理】申请项目调整！");
                }
                startProcessReq.setModelKey(ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name());
                startProcessReq.setSubModule(ContractFlowSubModuleEnum.CHANGE_REPAY_PLAN.name());
                if (ObjectUtil.isNotEmpty(req.getActualLeaseDate())) {
                    baseInfoService.updateActualLeaseDate(LocalDateTimeUtil.parse(req.getActualLeaseDate(), DatePattern.NORM_DATE_PATTERN).toLocalDate(), req.getContractId());
                } else {
                    throw new MithrasException("调整还款计划-实际起租日不可为空");
                }
                fillVarMap(contractBaseInfo, varMap);
                //baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_COMMIT.name(), ContractChangeTypeEnum.CHANGE_REPAY_PLAN.name(), req.getContractId());
                event = "合同调整还款计划审批";
                break;
            case OTHER: {
                // 前置校验
                result = this.checkLowestIrrAndkAverageIrr(contractBaseInfo, changeType);
                if (req.getOnlyCheck() && StringUtils.isNotBlank(result)){
                    return result;
                }
                //检查抵质押文件
                checkMortgagePledgeFile(contractBaseInfo);
                ContractEffectCheckFactory.getInstance(contractBaseInfo.getBizType()).check(contractBaseInfo, false);
                if (!req.getOnlyCheck()) {
                    req.getRemarkAddREQ().check();
                    getBean(ProcessModifyRemarkService.class).saveOrUpdateByKey(copyProperties(req.getRemarkAddREQ(), ProcessModifyRemark.class));
                }
                projClientRoleService.projContractSubmit(contractBaseInfo.getId());
            }
            default:
                //检查抵质押文件
                checkMortgagePledgeFile(contractBaseInfo);
                startProcessReq.setModelKey(ProcessModelTypeEnum.ContractModifyFlow.name());
                startProcessReq.setSubModule(ContractFlowSubModuleEnum.MODIFY_ALL.name());
                //baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_COMMIT.name(), ContractChangeTypeEnum.OTHER.name(), req.getContractId());
                event = "合同变更审批";
                break;
        }
        if (req.getOnlyCheck()) {
            return "";
        }
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
        projectLifecycleEventService.add(event, ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", contractBaseInfo.getProjReviewId());
        // 合同流程状态变更为变更审批中
        ContractBaseInfo toUpdate = new ContractBaseInfo();
        toUpdate.setId(contractBaseInfo.getId());
        toUpdate.setContractProcessStatus(ContractProcessStatusEnum.CHANGE_COMMIT.name());
        toUpdate.setContractProcessChangeStatus(changeType.name());
        contractBaseInfoService.updateById(toUpdate);
        return "";
    }

    private void fillVarMap(ContractBaseInfo contractBaseInfo, Map<String, Object> varMap) {
        // 是否标准合同
        ContractTextInfo textInfo = getBean(ContractTextInfoService.class).getOneByContractId(contractBaseInfo.getId());
        if (ObjectUtil.isNull(textInfo) || CharSequenceUtil.isBlank(textInfo.getTextType())) {
            log.error("{}>>合同文本类型为空，不影响流程启动，默认标准，请知悉", contractBaseInfo.getContractCode());
            varMap.put("is_standard_contract", true);
            return;
        }
        List<String> split = CharSequenceUtil.split(textInfo.getTextType(), ",");
        if (CollectionUtil.isNotEmpty(split) && split.contains(ContractTextTypeEnum.STANDARD_TEXT.name())) {
            varMap.put("is_standard_contract", true);
        } else {
            varMap.put("is_standard_contract", false);
        }
    }

    public void contractPrepaymentCheck(ContractBaseInfo baseInfo) {
        ContractPrepayment bean = contractPrepaymentService.getList(new ContractIdListREQ(baseInfo.getId()));
        if (ObjectUtil.isEmpty(bean) || ObjectUtil.isEmpty(bean.getEarlyRepayment()) || ObjectUtil.isEmpty(bean.getApplayRepaymentDate())) {
            throw new MithrasException("请补全提前还款日期， 提前归还本金");
        }
    }

    public void changeConserve(ContractFlowChangeConserveREQ req) {
        ContractCanChangeRSP rsp = baseInfoService.canUpdateContractProcessStatus(Collections.singletonList(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name()), req.getContractId());
        if (Boolean.FALSE.equals(rsp.getCanProcess())) {
            throw new MithrasException(rsp.getMessage());
        }
        ContractChangeTypeEnum changeType = Optional.ofNullable(req.getChangeType()).map(ContractChangeTypeEnum::of).orElseThrow(() -> new MithrasException("合同变更类型不合法"));
        if (StringUtils.isNotBlank(rsp.getTwoStatus()) && !rsp.getTwoStatus().equals(changeType.name())) {
            throw new MithrasException("合同变更处于" + ContractChangeTypeEnum.of(rsp.getTwoStatus()).display);
        }
        ContractBaseInfo baseInfo = contractBaseInfoService.getById(req.getContractId());
        ContractPriceDetailRSP priceDetail = priceService.detail(new ContractPriceDetailREQ(req.getContractId()));
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        switch (changeType) {
            case LPR_CHANGE:
                if (ObjectUtil.isEmpty(req.getLprPercent()) || req.getLprPercent() < 0) {
                    throw new MithrasException("LPR不合法");
                }
                if (req.getLprPercent().equals(priceDetail.getLprPercent())) {
                    throw new MithrasException("数据未变化");
                }
                if (RateType.FIXED.name().equals(priceDetail.getRateType())) {
                    throw new MithrasException("本合同为固定利率，不支持变更类型=LPR调整！");
                }
                if (Objects.equals(baseInfo.getBizType(), ProjectBizType.BL.name())) {
                    ContractFactoringPrice cfp = new ContractFactoringPrice();
                    cfp.setId(priceDetail.getId());
                    cfp.setLprPercent(req.getLprPercent());
                    factoringPriceService.updateById(cfp);
                } else if (Objects.equals(baseInfo.getBizType(), ProjectBizType.ZR.name())) {
                    ContractAocPrice cap = new ContractAocPrice();
                    cap.setId(priceDetail.getId());
                    cap.setLprPercent(req.getLprPercent());
                    aocPriceService.updateById(cap);
                } else {
                    //合同lpr填充还款月数
                    ContractLeasePrice clp = new ContractLeasePrice();
                    ContractPriceDetailRSP detail = contractPriceService.detail(new ContractPriceDetailREQ(req.getContractId()));
                    if (ObjectUtil.isNotNull(detail)) {
                        clp.setRepayTimesTotal(detail.getRepayTimesTotal());
                    }
                    clp.setId(priceDetail.getId());
                    clp.setLprPercent(req.getLprPercent());
                    leasePriceService.updateById(clp);
                }
                // 状态维护统一到ContractOperationPrepare中了
//                baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.LPR_CHANGE.name(), req.getContractId());
                break;
            /*case EARLY_REPAYMENT:
                //提前还款
                baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.EARLY_REPAYMENT.name(), req.getContractId());
                break;*/
            case EXTENSION:
                if (ObjectUtil.isEmpty(req.getLeaseMonthCount()) || req.getLeaseMonthCount() < 0) {
                    throw new MithrasException("展期数据不合法");
                }
                //展期月数
                AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustInfoService.adjustBaseLastByprojId(baseInfo.getProjReviewId(), AfterLeaseAdjustEnum.EXTEND.name());
                if (ObjectUtil.isNull(afterLeaseAdjustInfo)) {
                    throw new MithrasException("请先在【租后管理】申请项目调整！");
                }
                /*Long month = afterLeaseAdjustInfo.getExtensionmonth();
                ProjReviewPriceDetailRSP detail = projReviewPriceService.detail(afterLeaseAdjustInfo.getProjId());
                //租赁
                if(ObjectUtil.isNotEmpty(detail.getLeasePriceDetailRSP())){
                    month += detail.getLeasePriceDetailRSP().getLeaseMonthCount();
                }else if(ObjectUtil.isNotEmpty(detail.getFactoringPriceDetailRSP())){
                    month += detail.getFactoringPriceDetailRSP().getFactoringCreditTerm();
                }else if(ObjectUtil.isNotEmpty(detail.getAocPriceDetailRSP())){
                    month += detail.getAocPriceDetailRSP().getAocCreditTerm();
                }
                if(req.getLeaseMonthCount() > month){
                    throw new MithrasException("已超出可申请最大月数，请先前往租后调整申请");
                }*/
                if (req.getLeaseMonthCount().equals(priceDetail.getMonthCount())) {
                    throw new MithrasException("数据未变化");
                }
                if (Objects.equals(baseInfo.getBizType(), ProjectBizType.BL.name())) {
                    ContractFactoringPrice cfp = new ContractFactoringPrice();
                    cfp.setId(priceDetail.getId());
                    cfp.setFactoringCreditTerm(req.getLeaseMonthCount());
                    factoringPriceService.updateById(cfp);
                } else if (Objects.equals(baseInfo.getBizType(), ProjectBizType.ZR.name())) {
                    ContractAocPrice cap = new ContractAocPrice();
                    cap.setId(priceDetail.getId());
                    cap.setAocCreditTerm(req.getLeaseMonthCount());
                    aocPriceService.updateById(cap);
                } else {
                    //合同展期填充还款月数
                    ContractLeasePrice clp = new ContractLeasePrice();
                    ContractPriceDetailRSP detail = contractPriceService.detail(new ContractPriceDetailREQ(req.getContractId()));
                    if (ObjectUtil.isNotNull(detail)) {
                        clp.setRepayTimesTotal(detail.getRepayTimesTotal());
                    }
                    clp.setId(priceDetail.getId());
                    clp.setLeaseMonthCount(req.getLeaseMonthCount());
                    leasePriceService.updateById(clp);
                }
                // 状态维护统一到ContractOperationPrepare中了
//                baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.EXTENSION.name(), req.getContractId());
                break;
            case CHANGE_REPAY_PLAN:
                //调整还款计划租后检查
                if (ObjectUtil.isNull(afterLeaseAdjustInfoService.adjustBaseLastByprojId(baseInfo.getProjReviewId(), AfterLeaseAdjustEnum.REPAYMENT.name()))) {
                    throw new MithrasException("请先在【租后管理】申请项目调整！");
                }
                // 状态维护统一到ContractOperationPrepare中了
//                baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.CHANGE_REPAY_PLAN.name(), req.getContractId());
                break;
            /*case OTHER:
                baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.OTHER.name(), req.getContractId());
                break;*/
            default:
                throw new MithrasException("无此变更类型");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void settle(ContractFlowSettleREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (clientTransferService.inTransfer(contractBaseInfo.getClientId())) {
            err(ERR_IN_TRANSFER);
        }
        if (!Objects.equals(ContractStatus.START_RENT.name(), contractBaseInfo.getContractStatus())) {
            throw new MithrasException("合同非起租状态");
        }
        if (this.isInProcess(req.getContractId())) {
            throw new MithrasException("该合同数据变动已处于流程中，无法提交数据");
        }
        if (!Objects.equals(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), contractBaseInfo.getContractProcessStatus())) {
            throw new MithrasException("数据无变动，不允许提交");
        }
        if (ObjectUtil.isNotNull(projReviewBaseInfoService.findRelatedProcess(contractBaseInfo.getProjReviewId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交合同流程！");
        }
        //更新部门领导
        contractBaseInfoService.renewLeader(req.getContractId());
        ContractAdvanceEnum type = ContractAdvanceEnum.of(req.getSettlePlanType());
        StartProcessReq startProcessReq = buildCommonStartProcessReq(contractBaseInfo);
        String event = null;
        if (type == ContractAdvanceEnum.SETTLE_NORMAL) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.ContractNormalSettleFlow.name());
            startProcessReq.setSubModule(ContractFlowSubModuleEnum.NORMAL_SETTLE.name());
            event = "合同正常结清审批";
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.ContractEarlySettleFlow.name());
            startProcessReq.setSubModule(ContractFlowSubModuleEnum.EARLY_SETTLE.name());
            event = "合同提前结清审批";
        }
        // 抄送给出纳
        List<UserDO> jobUsers = Optional.ofNullable(getBean(UserService.class).getUsersByjobcod(JobEnum.cashier.name())).orElse(new ArrayList<>());
        List<String> ccUserIdList = Optional.ofNullable(startProcessReq.getCcUserIdList()).orElse(new ArrayList<>());
        ccUserIdList.addAll(jobUsers.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toSet()));
        startProcessReq.setCcUserIdList(ccUserIdList);
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
        projectLifecycleEventService.add(event, ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", contractBaseInfo.getProjReviewId());
        // 合同流程状态变更为结清审批中
        ContractBaseInfo toUpdate = new ContractBaseInfo();
        toUpdate.setId(contractBaseInfo.getId());
        toUpdate.setContractProcessStatus(ContractProcessStatusEnum.SETTLE_COMMIT.name());
        toUpdate.setContractProcessChangeStatus(type.name());
        contractBaseInfoService.updateById(toUpdate);
        //修改罚息状态
        List<CollectionBaseInfo> collectionList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, RENT.name())
                .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId()));
        if (ObjectUtil.isNotEmpty(collectionList)) {
            List<Long> collectionIds = collectionList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
            LambdaUpdateWrapper<CollectionBaseInfo> collectionBaseInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            collectionBaseInfoLambdaUpdateWrapper.set(CollectionBaseInfo::getPenaltyInterestCalculateFlag, YesOrNoNumberEnum.YES.getCode());
            collectionBaseInfoLambdaUpdateWrapper.in(CollectionBaseInfo::getId, collectionIds);
            collectionBaseInfoService.update(null, collectionBaseInfoLambdaUpdateWrapper);
        }
    }

    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return
     */
    private StartProcessReq buildCommonStartProcessReq(ContractBaseInfo contractBaseInfo) {
        // 重新查一下最新的数据
        ContractBaseInfo baseInfo = contractBaseInfoService.getById(contractBaseInfo.getId());
        Boolean projReviewNeedBoardApproveFlag = calBoardRuleHelper.needBoardApprove(baseInfo.getProjReviewId());
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(baseInfo.getId()));
        startProcessReq.setProcessInstanceName(baseInfo.getContractCode());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        //增加法律合规部负责人
        Long flhgbLeader = getBean(FlowAssistService.class).deptLeader(FlowAssistService.FLHGB, FlowAssistService.FLHGB_DESC, businesshead.name());
        Long yyglbLeader = getBean(FlowAssistService.class).deptLeader(FlowAssistService.YYGLB, FlowAssistService.YYGLB_DESC, businesshead.name());
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("flhgbDeptLeader", Objects.isNull(flhgbLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(flhgbLeader))),
                Pair.of("yyglbbusinesshead", Objects.isNull(yyglbLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(yyglbLeader))),
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of(ProcessVarEnum.projReviewNeedBoradApprove.name(), projReviewNeedBoardApproveFlag),
                // 总经理节点审批人需要计算 项目评审需要董事会审批 ? 无需审批人 : 分管领导
                Pair.of("generalManagerNodeApprover", projReviewNeedBoardApproveFlag ? new ArrayList<>() : Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>())
        ));
        startProcessReq.setCcUserIdList(StringUtils.isBlank(baseInfo.getProjCosponsorUserIds()) ? new ArrayList<>() : JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
        return startProcessReq;
    }

    /**
     * 流程结束
     *
     * @param modelKey
     * @param contractId
     * @param endType
     * @param startUserId
     */
    @Deprecated
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        // 合同相关审批流没有设计拒绝操作，如果不是审批通过则是发起人取消
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.getByName(modelKey);
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (!processPass) {
            // 发起人取消 回退版本
            contractVersionService.recordVersion(contractId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
            contractVersionService.reset(contractId);
            //更新部门领导
            contractBaseInfoService.renewLeader(contractId);
        }
        // 根据具体流程和审批结果变更状态
        switch (processModelTypeEnum) {
            // 合同创建流程
            case ContractCreateFlow: {
                if (processPass) {

                    // 填充合同生效时间
                    ContractBaseInfo toUpdate = new ContractBaseInfo();
                    toUpdate.setId(contractBaseInfo.getId());
                    toUpdate.setPaymentPlanDate(LocalDate.now());
                    contractBaseInfoService.updateById(toUpdate);
                    recordContractStatus(contractId, ContractStatus.TAKE_EFFECT, ContractProcessStatusEnum.NEW_PASS);
                } else {
                    recordContractStatus(contractId, null, ContractProcessStatusEnum.NEW_CANCEL);
                }
                break;
            }
            // 合同起租流程
            case ContractStartRentFlow: {
                if (processPass) {
                    recordContractStatus(contractId, ContractStatus.START_RENT, ContractProcessStatusEnum.START_RENT_PASS);
                    // 删除可能存在的起租提醒记录
                    contractRemindRecordService.deleteByContractId(contractId);
                } else {
                    recordContractStatus(contractId, null, ContractProcessStatusEnum.START_RENT_CANCEL);
                }
                break;
            }
            // 新增借据流程
            case ContractAddNewReceiptFlow: {
                if (processPass) {
                    recordContractStatus(contractId, null, ContractProcessStatusEnum.NEW_RECEIPT_PASS);
                } else {
                    recordContractStatus(contractId, null, ContractProcessStatusEnum.NEW_RECEIPT_CANCEL);
                }
                break;
            }
            // 合同变更流程
            case ContractModifyFlow:
            case ContractLPRChangeFlow:
            case ContractEarlyRepayFlow:
            case ContractChangeRepayPlanFlow:
            case ContractExtensionFlow: {
                if (processPass) {
                    if (ProcessModelTypeEnum.ContractExtensionFlow.equals(processModelTypeEnum)) {
                        // 展期 记录特殊交易
                        recordSpecialTrade(contractId, ContractSpecialTradeTypeEnum.CHANGE_EXTENSION, processInstanceId);
                    }
                    recordContractStatus(contractId, null, ContractProcessStatusEnum.CHANGE_PASS);
                    // 清除流程子类型数据
                    contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_PASS.name(), null, contractId);
                } else {
                    recordContractStatus(contractId, null, ContractProcessStatusEnum.CHANGE_CANCEL);
                }
                break;
            }
            // 合同结清流程
            case ContractNormalSettleFlow:
            case ContractEarlySettleFlow: {
                if (processPass) {
                    if (ProcessModelTypeEnum.ContractEarlySettleFlow.equals(processModelTypeEnum)) {
                        // 提前结清 记录特殊交易
                        recordSpecialTrade(contractId, ContractSpecialTradeTypeEnum.SETTLE_IN_ADVANCE, processInstanceId);
                    }
                    // 结清需要进一步校验合同下的所有现金流是否都已核销完毕
                    boolean bjz = false;
                    boolean cashFlow = false;
                    // 校验保证金余额是否为0
                    long earnestBalance = marginBaseInfoService.getMarginBalance(contractId);
                    if (earnestBalance == 0) {
                        bjz = true;
                    } else {
                        log.info("合同结清流程审批通过，但是保证金余额不等于0，不执行合同状态变更动作[contractId: {}]", contractId);
                    }
                    // 校验合同下的所有现金流是否都已核销完毕
                    if (collectionBaseInfoService.allCashFlowIsVerified(contractId)) {
                        cashFlow = true;
                    } else {
                        log.info("合同结清流程审批通过，但是存在未核销完毕的现金流，不执行合同状态变更动作[contractId: {}]", contractId);
                    }
                    if (bjz && cashFlow) {
                        recordContractStatus(contractId, ContractStatus.SETTLE, ContractProcessStatusEnum.SETTLE_PASS);
                    }
                    // 清除流程子类型数据
                    contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.SETTLE_PASS.name(), null, contractId);
                } else {
                    // 删除结清方案
                    contractSettlePlanService.removeByContractId(contractId);
                    recordContractStatus(contractId, null, ContractProcessStatusEnum.SETTLE_CANCEL);
                }
                //通知苍穹结清退保证金
                financialManagerService.earnestRecord(contractId);
                break;
            }
        }
        // 根据审批结果执行业务数据变更并发送通知事件
        if (processPass) {
            // 审批通过 新增版本
            contractVersionService.recordVersion(contractId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
            // 通知收款模块
            this.notifyCollection(processModelTypeEnum, contractId);
        }
        //合同状态变更，通知风险敞口
        ApplicationContextUtil.getApplicationContext().publishEvent(new ContractPriceChangeEvent(this, contractId));
        //提前还款流程后清空数据
        if (ProcessModelTypeEnum.ContractEarlyRepayFlow.name().equals(modelKey)) {
            contractPrepaymentService.remove(Wrappers.<ContractPrepayment>lambdaQuery()
                    .eq(ContractPrepayment::getContractId, contractId));
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
                metricComputeEventBus.post(metricComputeEvent);
            }
        });
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordContractStatus(Long contractId, ContractStatus contractStatus, ContractProcessStatusEnum processStatus) {
        if (contractId == null || (contractStatus == null && processStatus == null)) {
            return;
        }
        UpdateWrapper<ContractBaseInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", contractId);
        if (contractStatus != null) {
            updateWrapper.set("contract_status", contractStatus.name());
            if (ContractStatus.SETTLE == contractStatus) {
                updateWrapper.set("settle_time", LocalDateTime.now());
            }
        }
        if (processStatus != null) {
            updateWrapper.set("contract_process_status", processStatus.name());
        }
        updateWrapper.set("update_time", LocalDateTime.now());
        contractBaseInfoService.update(null, updateWrapper);
    }


    /**
     * 寻找某合同id 进行中的 流程
     *
     * @param contractId
     * @return
     */
    public ProcessResp findRelatedProcess(Long contractId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(contractId));
        processPageReq.setModelKeyList(BusinessModuleEnum.CONTRACT.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public boolean isInProcess(Long contractId) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(contractId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(BusinessModuleEnum.CONTRACT.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = taskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        return !Objects.isNull(processResp);
    }

    private void notifyCollection(ProcessModelTypeEnum processModelTypeEnum, Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        switch (processModelTypeEnum) {
            // 合同创建流程
            case ContractCreateFlow:
                break;
//                {
//                this.notifyOn(contractBaseInfo, ContractProcessStatusEnum.NEW_PASS);
//                break;
//            }
            // 合同变更流程
            case ContractModifyFlow:
                break;
            case ContractLPRChangeFlow:
            case ContractChangeRepayPlanFlow:
            case ContractExtensionFlow:
                break;
            case ContractEarlyRepayFlow: {
                this.notifyOnPrepayment(contractBaseInfo);
                break;
            }
            case ContractEarlySettleFlow: {
                this.notifyOnSettlePass(contractBaseInfo, processModelTypeEnum);
                this.notifyOnEarlySettlePass(contractBaseInfo, processModelTypeEnum);
                break;
            }
            // 合同结清流程
            case ContractNormalSettleFlow: {
                this.notifyOnSettlePass(contractBaseInfo, processModelTypeEnum);
                break;
            }
            case ContractStartRentFlow: {
                this.notifyOnStartRentPass(contractBaseInfo, processModelTypeEnum);
                break;
            }
        }
        // 通知收款模块租金表可能发生变更，收款模块需要比对一下
        if (!Objects.equals(processModelTypeEnum, ProcessModelTypeEnum.ContractCreateFlow)) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(processModelTypeEnum.name(), contractId, RENT);
            collectionAddEvent.setProcessModelTypeEnum(processModelTypeEnum);
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
    }

    //合同结清完成事件
    public void notifyOnSettlePass(ContractBaseInfo contractBaseInfo, ProcessModelTypeEnum processModelTypeEnum) {
        log.info("notifyOnSettlePass {}, {}", contractBaseInfo, processModelTypeEnum);
        //结清不在生成
        /*ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractBaseInfo.getId());
        //if (Objects.nonNull(contractSettlePlan.getNominalPrice())) {
        CollectionAddEvent collectionAddEvent = new CollectionAddEvent(ContractProcessStatusEnum.SETTLE_PASS.name(), contractBaseInfo.getId(), CashFlowItemEnum.NOMINAL_PRICE, LongUtil.null2zero(contractSettlePlan.getNominalPrice()), LocalDate.now());
        collectionAddEvent.setProcessModelTypeEnum(processModelTypeEnum);
        applicationEventPublisher.publishEvent(collectionAddEvent);*/

//        else {
//            // 结清方案没有则用报价方案填充
//            ContractPriceHelperBO contractPriceHelperBO = this.getContractPriceHelpBO(contractBaseInfo);
//            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(ContractProcessStatusEnum.SETTLE_PASS.name(), contractBaseInfo.getId(), CashFlowItemEnum.NOMINAL_PRICE, contractPriceHelperBO.getNominalPrice(),LocalDate.now());
//            applicationEventPublisher.publishEvent(collectionAddEvent);
//        }
        //20251216结清不在生成名义价款
//        ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractBaseInfo.getId());
//        Page<ContractRentActual> page = contractRentActualService.page(new Page<>(1, 1),
//                Wrappers.<ContractRentActual>lambdaQuery().eq(ContractRentActual::getContractId, contractBaseInfo.getId())
//                        .orderByDesc(ContractRentActual::getCashFlowPhase));
//        ContractRentActual contractRentActual = new ContractRentActual();
//        if (CollectionUtils.isNotEmpty(page.getRecords())) {
//            contractRentActual = page.getRecords().get(0);
//        }
//        CollectionAddEvent collectionAddEvent = new CollectionAddEvent(ContractProcessStatusEnum.SETTLE_PASS.name(), contractBaseInfo.getId(),
//                CashFlowItemEnum.NOMINAL_PRICE, LongUtil.null2zero(contractSettlePlan.getNominalPrice()), contractRentActual.getCashFlowDate(), contractRentActual.getCashFlowPhase());
//        collectionAddEvent.setProcessModelTypeEnum(processModelTypeEnum);
//        applicationEventPublisher.publishEvent(collectionAddEvent);
         marginBaseInfoMapper.updateStatus(contractBaseInfo.getId());
    }

    //起租事件
    public void notifyOnStartRentPass(ContractBaseInfo contractBaseInfo, ProcessModelTypeEnum processModelTypeEnum) {
        //20251216年起又调整为合同起租 生成 名义价款
        ContractPriceDetailRSP priceDetail = priceService.detail(new ContractPriceDetailREQ(contractBaseInfo.getId()));
        Page<ContractRentActual> page = contractRentActualService.page(new Page<>(1, 1), Wrappers.<ContractRentActual>lambdaQuery().eq(ContractRentActual::getContractId, contractBaseInfo.getId())
                .orderByDesc(ContractRentActual::getCashFlowPhase));
        ContractRentActual contractRentActual = new ContractRentActual();
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            contractRentActual = page.getRecords().get(0);
        }
        //取最后一期 租金计划 计划还款日
        CollectionAddEvent collectionAddEvent = new CollectionAddEvent(ContractProcessStatusEnum.START_RENT_PASS.name(),
                contractBaseInfo.getId(), CashFlowItemEnum.NOMINAL_PRICE,
                LongUtil.null2zero(priceDetail.getNominalPrice()),
                contractRentActual.getCashFlowDate(), contractRentActual.getCashFlowPhase());
        collectionAddEvent.setProcessModelTypeEnum(processModelTypeEnum);
        applicationEventPublisher.publishEvent(collectionAddEvent);
    }

    public void notifyOnEarlySettlePass(ContractBaseInfo contractBaseInfo, ProcessModelTypeEnum processModelTypeEnum) {
        ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractBaseInfo.getId());
        if (Objects.nonNull(contractSettlePlan.getLoss())) {
            //提前终止补充金-减免金额 = 应收补偿金 最小为0
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(contractBaseInfo.getContractCode(), contractBaseInfo.getId(), CashFlowItemEnum.EARLY_STOP_COMPENSATION,
                    Math.max(0L, NumberUtil.sub(LongUtil.null2zero(contractSettlePlan.getLoss()), LongUtil.null2zero(contractSettlePlan.getApplyDerateAmount())).longValue()), LocalDate.now());
            collectionAddEvent.setProcessModelTypeEnum(processModelTypeEnum);
            applicationEventPublisher.publishEvent(collectionAddEvent);
        }
    }

    public void notifyOnPrepayment(ContractBaseInfo contractBaseInfo) {
        ContractIdListREQ req = new ContractIdListREQ();
        req.setContractId(contractBaseInfo.getId());
        ContractPrepayment prepayment = contractPrepaymentService.getList(req);
        //不在生成名义货价收款
        /*if (Objects.nonNull(prepayment.getNominalPrice())) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(contractBaseInfo.getContractCode(), contractBaseInfo.getId(), CashFlowItemEnum.NOMINAL_PRICE, prepayment.getNominalPrice(), LocalDate.now());
            applicationEventPublisher.publishEvent(collectionAddEvent);
        }*/
        if (Objects.nonNull(prepayment)) {
            long lossAmount = Optional.ofNullable(prepayment.getLoss()).orElse(0L);
            long lossDerateAmount = Optional.ofNullable(prepayment.getApplyDerateAmount()).orElse(0L);
            long amount = lossAmount - lossDerateAmount;
            if (amount > 0) {
                CollectionAddEvent collectionAddEvent = new CollectionAddEvent(contractBaseInfo.getContractCode(), contractBaseInfo.getId(), CashFlowItemEnum.EARLY_STOP_COMPENSATION, amount, prepayment.getApplayRepaymentDate());
                applicationEventPublisher.publishEvent(collectionAddEvent);
            }
        }
    }

//    private void notifyOn(ContractBaseInfo contractBaseInfo, ContractProcessStatusEnum contractProcessStatusEnum) {
//        ContractPriceHelperBO contractPriceHelperBO = this.getContractPriceHelpBO(contractBaseInfo);
//        Assert.notNull(contractPriceHelperBO.getEarnestMoney(), () -> MithrasException.newException("保证金金额为空"));
//        // 发送事件
//        if (Objects.nonNull(contractPriceHelperBO.getEarnestMoney())) {
//            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(contractProcessStatusEnum.name(), contractBaseInfo.getId(), CashFlowItemEnum.EARNEST_MONEY, contractPriceHelperBO.getEarnestMoney());
//            applicationEventPublisher.publishEvent(collectionAddEvent);
//        }
//        if (Objects.nonNull(contractPriceHelperBO.getConsultingFee()) && contractPriceHelperBO.getConsultingFee() > 0) {
//            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(contractProcessStatusEnum.name(), contractBaseInfo.getId(), CashFlowItemEnum.OTHERAMOUNT, contractPriceHelperBO.getConsultingFee());
//            applicationEventPublisher.publishEvent(collectionAddEvent);
//        }
//        if (Objects.nonNull(contractPriceHelperBO.getDownPayment()) && contractPriceHelperBO.getDownPayment() > 0) {
//            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(contractProcessStatusEnum.name(), contractBaseInfo.getId(), CashFlowItemEnum.FIRST_RENT, contractPriceHelperBO.getDownPayment());
//            applicationEventPublisher.publishEvent(collectionAddEvent);
//        }
//    }

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 记录合同特殊交易
     *
     * @param contractId
     * @param specialTradeTypeEnum
     * @param processInstanceId
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordSpecialTrade(Long contractId, ContractSpecialTradeTypeEnum specialTradeTypeEnum, String processInstanceId) {
        // 找到所有已核销完毕的付款申请
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getContractId, contractId)
                .eq(PaymentBaseInfo::getWriteOffStatus, PaymentWriteOffStatus.WRITTEN_OFF)
        );
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return;
        }
        List<ContractSpecialTrade> contractSpecialTradeList = new ArrayList<>();
        if (ContractSpecialTradeTypeEnum.CHANGE_EXTENSION.equals(specialTradeTypeEnum)) {
            // 展期
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                // 到期日变更月数 = 该付款申请对应的实际租金表的最后一期的日期差
                LocalDate lastRent = calPaymentLastRentDate(paymentBaseInfo);
                //取实际结清日期 最后一笔收款日期
                LocalDate lastRentLib = calPaymentLastRentDateLib1(paymentBaseInfo, processInstanceId);

                ContractSpecialTrade contractSpecialTrade = ContractSpecialTrade.builder()
                        .contractId(contractId)
                        .paymentId(paymentBaseInfo.getId())
                        .paymentCode(paymentBaseInfo.getPaymentCode())
                        .type(specialTradeTypeEnum.name())
                        .tradeDate(LocalDate.now())
                        .changeMonthCount(CreditReportUtil.calMonthDiff(lastRentLib, lastRent))
                        .build();
                contractSpecialTradeList.add(contractSpecialTrade);
            }

        } else if (ContractSpecialTradeTypeEnum.SETTLE_IN_ADVANCE.equals(specialTradeTypeEnum)) {
            // 提前结清
//            ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractId);
            ContractPrepayment contractPrepayment = contractPrepaymentService.getList(new ContractIdListREQ(contractId));
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                // 到期日变更月数 = 该付款申请对应的实际租金表的最后一期的日期差
                LocalDate lastRentLib = calPaymentLastRentDateLib1(paymentBaseInfo, processInstanceId);

                ContractSpecialTrade contractSpecialTrade = ContractSpecialTrade.builder()
                        .contractId(contractId)
                        .paymentId(paymentBaseInfo.getId())
                        .paymentCode(paymentBaseInfo.getPaymentCode())
                        .type(specialTradeTypeEnum.name())
                        .tradeDate(LocalDate.now())
//                        .changeMonthCount(CreditReportUtil.calMonthDiff(contractSettlePlan.getApplySettleDate(), lastRentLib))
                        .changeMonthCount(CreditReportUtil.calMonthDiff(contractPrepayment.getApplayRepaymentDate(), lastRentLib))
                        .build();
                contractSpecialTradeList.add(contractSpecialTrade);
            }
        }

        // 处理交易金额逻辑-未核销本金 fixme
        Map<Long, List<CollectionBaseInfo>> collectionBaseInfoMap = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractSpecialTradeList.stream().map(ContractSpecialTrade::getContractId).collect(Collectors.toSet()))
                .ne(CollectionBaseInfo::getPhase, 0)
                .eq(CollectionBaseInfo::getCashFlowItem, RENT.name())
        ).stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));


        contractSpecialTradeList.forEach(c -> {
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMap.getOrDefault(c.getContractId(), new ArrayList<>());
            long tradeAmount = 0L;
            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                tradeAmount += LongUtil.null2zero(collectionBaseInfo.getPrincipal());
                tradeAmount -= LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal());
            }
            c.setTradeAmount(tradeAmount);
        });

        if (CollectionUtils.isNotEmpty(contractSpecialTradeList)) {
            contractSpecialTraderService.saveBatch(contractSpecialTradeList);
        }
    }

    /**
     * 寻找借据对应的实际租金表的最后一期租金预计还款日期（编辑区）（新的）
     *
     * @param paymentBaseInfo
     * @return
     */
    private LocalDate calPaymentLastRentDate(PaymentBaseInfo paymentBaseInfo) {
        if (Objects.isNull(paymentBaseInfo.getReceiptId())) {
            return null;
        }
        ContractReceipt contractReceipt = contractReceiptMapper.selectById(paymentBaseInfo.getReceiptId());
        if (Objects.isNull(contractReceipt)) {
            return null;
        }
        ContractRentActual lastRent = contractRentActualMapper.selectOne(Wrappers.<ContractRentActual>lambdaQuery()
                .eq(ContractRentActual::getReceiptId, contractReceipt.getId())
                .orderByDesc(ContractRentActual::getCashFlowDate)
                .last("LIMIT 1")
        );
        return Optional.ofNullable(lastRent).map(ContractRentActual::getCashFlowDate).orElse(null);
    }

    /**
     * 寻找借据对应的实际租金表的最后一期租金预计还款日期（版本表）（旧的）
     *
     * @param paymentBaseInfo
     * @return
     */
    @Deprecated
    private LocalDate calPaymentLastRentDateLib(PaymentBaseInfo paymentBaseInfo) {
        if (Objects.isNull(paymentBaseInfo.getReceiptId())) {
            return null;
        }
        // 查询所有版本号
        QueryWrapper<ContractReceiptLib> versionQuery = Wrappers.query();
        versionQuery.select("distinct(version)");
        versionQuery.eq("origin_id", paymentBaseInfo.getReceiptId());
        versionQuery.eq("version_type", VersionTypeConstants.NORMAL);
        versionQuery.orderByDesc("version");
        List<ContractReceiptLib> contractReceiptLibList = contractReceiptLibMapper.selectList(versionQuery);
        if (CollectionUtil.isEmpty(contractReceiptLibList)) {
            log.info("没有找到符合条件的版本号[无借据版本数据]");
            return null;
        }
        if (contractReceiptLibList.size() < 2) {
            log.info("没有找到符合条件的版本号[借据版本数据少于2条]");
            return null;
        }
        // 取倒数第二个版本
        String targetVersion = contractReceiptLibList.get(1).getVersion();
        if (StrUtil.isBlank(targetVersion)) {
            log.info("没有找到符合条件的版本号");
            return null;
        }
        // 取对应版本的实际租金表
        LambdaQueryWrapper<ContractRentActualLib> rentQuery = Wrappers.lambdaQuery();
        rentQuery.eq(ContractRentActual::getReceiptId, paymentBaseInfo.getReceiptId());
        rentQuery.eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL);
        rentQuery.eq(ContractRentActualLib::getVersion, targetVersion);
        rentQuery.orderByDesc(ContractRentActual::getCashFlowPhase);
        rentQuery.last(StringUtil.mysqlLimitOne());
        ContractRentActualLib contractRentActualLib = contractRentActualLibMapper.selectOne(rentQuery);
        return Optional.ofNullable(contractRentActualLib).map(ContractRentActual::getCashFlowDate).orElse(null);
//        ContractReceiptLib contractReceiptLib = contractReceiptLibMapper.selectOne(Wrappers.<ContractReceiptLib>lambdaQuery()
//                .eq(ContractReceiptLib::getOriginId, paymentBaseInfo.getReceiptId())
//                .eq(ContractReceiptLib::getVersionType, VersionTypeConstants.NORMAL)
//                .orderByDesc(ContractReceiptLib::getVersion)
//                .last("LIMIT 1"));
//        if (Objects.isNull(contractReceiptLib)) {
//            return null;
//        }
//        List<ContractRentActualLib> lastRent = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
//                .eq(ContractRentActualLib::getReceiptId, contractReceiptLib.getOriginId())
//                .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
//                .orderByDesc(ContractRentActualLib::getVersion)
//                .last("LIMIT 2")
//        );
//
//        if(CollUtil.isNotEmpty(lastRent)){
//            lastRent.sort(Comparator.comparing(ContractRentActualLib::getVersion));
//            return lastRent.get(0).getCashFlowDate();
//        }
//        return null;
    }

    /**
     * 寻找借据对应的实际收款最晚的日期
     *
     * @param paymentBaseInfo
     * @return
     */
    private LocalDate calPaymentLastRentDateLib2(PaymentBaseInfo paymentBaseInfo) {
        if (Objects.isNull(paymentBaseInfo.getReceiptId())) {
            return null;
        }

        //这里由于多了一个结清确认流程，只有这两个流程通过之后计划才会变更
        return Optional.ofNullable(collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getReceiptId, paymentBaseInfo.getReceiptIdFinal())
                        .eq(CollectionBaseInfo::getCashFlowItem, RENT.name())
                        .orderByDesc(CollectionBaseInfo::getPlanCollectionDate)
                        .last(StringUtil.mysqlLimitOne())))
                .map(CollectionBaseInfo::getPlanCollectionDate).orElse(null);
    }

    /**
     * 取展期上一个版本的实际租金表最后一期日期
     */
    public LocalDate calPaymentLastRentDateLib1(PaymentBaseInfo paymentBaseInfo, String processInstanceId) {
        if (Objects.isNull(paymentBaseInfo.getReceiptId())) {
            return null;
        }
        if (CharSequenceUtil.isBlank(processInstanceId)) {
            return null;
        }

        CommonVersion commonVersion = SpringContextHolder.getBean(CommonVersionMapper.class).selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getProcessInstanceId, processInstanceId)
                .eq(CommonVersion::getMainId, paymentBaseInfo.getContractId())
                .last(StringUtil.mysqlLimitOne()));
        log.info("processInstanceId:{},commonVersion:{}", processInstanceId, commonVersion);

        ContractRentActualLib contractRentActualLib = contractRentActualLibMapper.selectOne(Wrappers.<ContractRentActualLib>lambdaQuery()
                .lt(ContractRentActualLib::getVersion, commonVersion.getVersion())
                .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                .isNotNull(ContractRentActual::getCashFlowCode)
                .eq(ContractRentActualLib::getReceiptId, paymentBaseInfo.getReceiptId())
                .orderByDesc(ContractRentActualLib::getVersion)
                .orderByDesc(ContractRentActual::getCashFlowPhase)
                .last(StringUtil.mysqlLimitOne()));
        log.info("contractRentActualLib:{}", contractRentActualLib);

        return Optional.ofNullable(contractRentActualLib)
                .map(ContractRentActualLib::getCashFlowDate).orElse(null);
    }

    private void replaceInBaseInfo(ContractBaseInfo contractBaseInfo, ProjReviewBaseInfo projReviewBaseInfo) {
        contractBaseInfo.setProjName(projReviewBaseInfo.getProjName());
        contractBaseInfo.setProjCode(projReviewBaseInfo.getProjCode());
        contractBaseInfo.setProjSource(projReviewBaseInfo.getProjSource());
        contractBaseInfo.setProjItem(projReviewBaseInfo.getProjectClassify());
        contractBaseInfo.setProjCosponsorUserIds(projReviewBaseInfo.getProjCosponsorUserIds());
        contractBaseInfo.setProjBackground(projReviewBaseInfo.getProjBackground());
        contractBaseInfo.setUpdateTime(contractBaseInfo.getUpdateTime());
        contractBaseInfo.setFundsPurpose(projReviewBaseInfo.getFundsPurpose());
        contractBaseInfo.setAssignor(projReviewBaseInfo.getAssignor());
        contractBaseInfo.setRiskControlIndustryClassify(projReviewBaseInfo.getRiskControlIndustryClassify());
    }

    private void replaceInLeasePrice(ContractLeasePrice contractLeasePrice, ProjReviewBaseInfo projReviewBaseInfo) {
        ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceService.getByProjectId(projReviewBaseInfo.getId());
        if (Objects.nonNull(projReviewLeasePrice)) {
            contractLeasePrice.setProjLeaseMonthCount(projReviewLeasePrice.getLeaseMonthCount());
            contractLeasePrice.setProjCreditAmount(projReviewLeasePrice.getApplyCreditAmount());
            contractLeasePrice.setProjDownPayment(projReviewLeasePrice.getDownPayment());
            contractLeasePrice.setProjEarnestMoney(projReviewLeasePrice.getEarnestMoney());
            contractLeasePrice.setProjConsultingFee(projReviewLeasePrice.getConsultingFee());
            contractLeasePrice.setProjIrrPercent(projReviewLeasePrice.getIrrPercent());
            contractLeasePrice.setUpdateTime(contractLeasePrice.getUpdateTime());
            contractLeasePrice.setPayType(projReviewLeasePrice.getPayType());
            contractLeasePrice.setRentalCalcType(projReviewLeasePrice.getRentalCalcType());
        }
    }

    public Map<Long, Long> getProjReviewCycleQuotaReturnAmount(List<Long> projReviewIds) {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getCreditAmountLoop, YesOrNoNumberEnum.YES.getCode())
                .in(ContractBaseInfo::getProjReviewId, projReviewIds));
        if (ObjectUtil.isEmpty(contractBaseInfoList)) {
            return MapUtil.empty();
        }
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.listByContractIds(contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> contractId2ReturnAmount = new HashMap<>();
        collectionBaseInfos.forEach(e -> {
            Long orDefault = contractId2ReturnAmount.getOrDefault(e.getContractId(), 0L);
            contractId2ReturnAmount.put(e.getContractId(), LongUtil.null2zero(e.getCollectionPrincipal()) + orDefault);
        });
        return contractId2ReturnAmount;
    }

    public void checkMortgagePledgeFile(ContractBaseInfo contractBaseInfo) {
        List<ContractPledge> contractPledgeList = contractPledgeMapper.selectList(Wrappers.<ContractPledge>lambdaQuery()
                .eq(ContractPledge::getContractId, contractBaseInfo.getId()));
        if (contractPledgeList != null && !contractPledgeList.isEmpty()) {
            for (ContractPledge contractPledge : contractPledgeList) {
                if (PledgeTypeEnum.ACCOUNTS_RECEIVABLE_PLEDGE.name().equalsIgnoreCase(contractPledge.getContractPledgeType())) {
                    //查询抵质押文件id
                    ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                            .contractId(contractPledge.getContractId())
                            .tenantryId(contractPledge.getId())
                            .fileType(ContractConstitutionFileTypeEnum.PLEDGE.name()).build();
                    List<Long> list = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
                    if (list == null || list.isEmpty()) {
                        throw new MithrasException("质押措施中的质押类型选择为「应收账款质押」，请上传【应收账款基础材料】相关资料文件后再提交流程！");
                    }
                }
            }
        }
        List<ContractMortgage> contractMortgageList = contractMortgageMapper.selectList(Wrappers.<ContractMortgage>lambdaQuery()
                .eq(ContractMortgage::getContractId, contractBaseInfo.getId()));
        if (contractMortgageList != null && !contractMortgageList.isEmpty()) {
            for (ContractMortgage contractMortgage : contractMortgageList) {
                if (MortgageTypeEnum.REAL_ESTATE_MORTGAGE.name().equalsIgnoreCase(contractMortgage.getContractMortgageType())) {
                    //查询抵质押文件id
                    ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                            .contractId(contractMortgage.getContractId())
                            .tenantryId(contractMortgage.getId())
                            .fileType(ContractConstitutionFileTypeEnum.MORTGAGE.name()).build();
                    List<Long> list = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
                    if (list == null || list.isEmpty()) {
                        throw new MithrasException("抵押措施中的抵押类型选择为「不动产抵押」，请上传【不动产权登记证书】相关资料文件后再提交流程！");
                    }
                }
            }
        }
    }

    public void effectCheck(ContractFlowBasicREQ contractFlowBasicREQ) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractFlowBasicREQ.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (clientTransferService.inTransfer(contractBaseInfo.getClientId())) {
            err(ERR_IN_TRANSFER);
        }
        if (!Objects.equals(ContractStatus.NEW.name(), contractBaseInfo.getContractStatus())) {
            throw new MithrasException("合同非新建状态");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(contractBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        //项目评审非失效状态
        if (SpringContextHolder.getBean(ProjReviewBaseInfoService.class).count(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getId, contractBaseInfo.getProjReviewId())
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.EXPIRE.name())) > 0) {
            throw new MithrasException("此项目评审已失效！流程无法提交");
        }
        List<ContractTenantry> tList = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getResolutionType, ResolutionTypeEnum.OTHER.name())
                .eq(ContractTenantry::getResolutionFileId, JSON.toJSONString(Collections.emptyList()))
                .eq(ContractTenantry::getContractId, contractBaseInfo.getId()));
        List<ContractGuarantor> gList = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                .eq(ContractGuarantor::getResolutionType, ResolutionTypeEnum.OTHER.name())
                .eq(ContractGuarantor::getResolutionFileId, JSON.toJSONString(Collections.emptyList()))
                .eq(ContractGuarantor::getContractId, contractBaseInfo.getId()));
        if (CollectionUtils.isNotEmpty(tList) || CollectionUtils.isNotEmpty(gList)) {
            throw new MithrasException("决议类型为其他的《决议文件》不存在，请上传《决议文件》后再提交流程！");
        }
        checkPricingIrr(contractBaseInfo);
        if (this.isInProcess(contractBaseInfo.getId())) {
            throw new MithrasException("该合同数据变动已处于流程中，无法提交数据");
        }
        // 校验
        ContractEffectCheckFactory.getInstance(contractBaseInfo.getBizType()).check(contractBaseInfo, false);
        if (ObjectUtil.isNotNull(projReviewBaseInfoService.findRelatedProcess(contractBaseInfo.getProjReviewId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交合同流程！");
        }
        //检查抵质押文件
        checkMortgagePledgeFile(contractBaseInfo);
    }

    public void changeCheck(ContractFlowChangeREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
    }


    //校验
    private String checkLowestIrrAndkAverageIrr( ContractBaseInfo contractBaseInfo,ContractChangeTypeEnum changeType) {
        Long contractId = contractBaseInfo.getId();
        //获取irr
        ContractIrrRSP rsp = this.getLowestIrrAndkAverageIrr( contractId, contractBaseInfo);
        if (rsp.getAverageIrr() < rsp.getLowestIrr()){
            if (StringUtils.isBlank(contractBaseInfo.getAdjustRemark()) &&
                    ((Objects.equals(ContractChangeTypeEnum.EXTENSION,changeType)) || Objects.equals(ContractChangeTypeEnum.CHANGE_REPAY_PLAN,changeType))){
                throw new MithrasException("合同加权平均IRR低于最低IRR要求，请填写调整说明！");
            }
            return "合同加权平均IRR低于最低IRR要求";
        }
        return null;
    }

    /**
     * 查询合同最新生效版本的定价IRR、最低IRR、合同加权平均IRR
     * 200602027 补充新逻辑
     * 最低irr取值逻辑
     * 合同下最新审批通过的付款申请中填写的最低IRR＞0，则最低IRR=该值；否则最低IRR=该合同最新生效版本的定价IRR；
     * 加权平均irr取值逻辑
     * 生效状态- 加权平均irr=概算irr
     * 起租状态- 合同加权平均IRR=sum(借据实际irr*借据金额)/借据金额之和
     */
    public ContractIrrRSP getLowestIrrAndkAverageIrr(Long contractId, ContractBaseInfo contractBaseInfo) {
        ContractIrrRSP rsp = new ContractIrrRSP();
        rsp.setContractId(contractId);
        //获取合同定价Irr
        ContractPriceDetailRSP priceDetailRSP = contractPriceService.detail(new ContractPriceDetailREQ(contractBaseInfo.getId()));
        if (Objects.nonNull(priceDetailRSP) && Objects.nonNull(priceDetailRSP.getPricingIrr())) {
            rsp.setPricingIrr(priceDetailRSP.getPricingIrr());
            rsp.setLowestIrr(priceDetailRSP.getPricingIrr());
            rsp.setAverageIrr(priceDetailRSP.getIrr());
        }
        //获取付款最低irr
        List<PaymentBaseInfo> paymentBaseInfoList = SpringContextHolder.getBean(PaymentBaseInfoService.class).queryListWithContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            rsp.setPayLowIrr(paymentBaseInfoList.get(0).getLowestIrr());
            if (Objects.nonNull(rsp.getPayLowIrr()) && rsp.getPayLowIrr() >= 0) {
                rsp.setLowestIrr(rsp.getPayLowIrr());
            }
        }
        //获取平均irr
        boolean startRent = Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name());
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractId);
        //起租 必须要要有irr否则就是有问题
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            if (startRent){
                throw new MithrasException(String.format("合同<%s>下没有借据", contractId));
            }
            return rsp;
        }
        Map<Long,BigDecimal> irrMap = new HashMap<>();
        boolean resFlag = Boolean.FALSE;
        for (ContractReceipt contractReceipt : contractReceiptList) {
            if (Objects.isNull(contractReceipt.getActualIrr())) {
                if (startRent){
                    throw new MithrasException(String.format("借据<%s>的实际IRR为空，请补充", contractReceipt.getReceiptCode()));
                }
                resFlag = Boolean.TRUE;
            }else {
                //irr是%，保留2位小数 即保留4位有效小数就可以了
                irrMap.put(contractReceipt.getId(),new BigDecimal(contractReceipt.getActualIrr()).divide(new BigDecimal(1000000), 4, RoundingMode.HALF_UP));
            }
        }
        if (resFlag){
            return rsp;
        }
        //取实际租金表本金之和
        List<ContractRentActual> contractRentActuals = contractRentActualService.listByContract(contractId);
        if (CollectionUtils.isEmpty(contractRentActuals)){
            if (startRent){
                throw new MithrasException(String.format("合同{}实际租金表为空，请补充", contractBaseInfo.getContractCode()));
            }
            return rsp;
        }
        Map<Long, BigDecimal> contractReceiptMap = contractRentActuals.stream()
                .filter(contractRentActual -> contractRentActual.getReceiptId() != null)
                .collect(Collectors.groupingBy(
                        ContractRentActual::getReceiptId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                contractRentActual ->
                                        contractRentActual.getPrincipal() == null ? BigDecimal.ZERO : LongUtil.tenThousand2Dollar(contractRentActual.getPrincipal()),
                                BigDecimal::add  // 金额累加
                        )
                ));
        if (contractReceiptMap.keySet().size() != contractReceiptList.size()){
            if (startRent){
                throw new MithrasException(String.format("合同{}存在借据对应的实际租金表为空，请补充", contractBaseInfo.getContractCode()));
            }
            return rsp;
        }
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            //计算金额/10000
            Map<Long, BigDecimal> paymentMap = paymentBaseInfoList.stream()
                    .filter(paymentBaseInfo -> paymentBaseInfo.getReceiptId() != null)
                    .collect(Collectors.groupingBy(
                            PaymentBaseInfo::getReceiptId,
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    paymentBaseInfo -> paymentBaseInfo.getApplyPaymentAmount() == null ?BigDecimal.ZERO: LongUtil.tenThousand2Dollar(paymentBaseInfo.getApplyPaymentAmount()),
                                    BigDecimal::add)));
            //覆盖投放金额
            if (Objects.nonNull(paymentMap)){
                contractReceiptMap.putAll(paymentMap);
            }
        }
        BigDecimal sumAmount = BigDecimal.ZERO;
        BigDecimal sumIrrAmount = BigDecimal.ZERO;
        //合同加权平均IRR=sum(借据irr*借据金额)/借据金额之和
        for (Map.Entry<Long, BigDecimal> entry : contractReceiptMap.entrySet()) {
            Long receiptId = entry.getKey();
            BigDecimal irr = irrMap.get(receiptId);
            BigDecimal amount = contractReceiptMap.get(receiptId);
            sumIrrAmount = sumIrrAmount.add(irr.multiply(amount));
            sumAmount = sumAmount.add(amount);
        }
        if (sumAmount.compareTo(BigDecimal.ZERO) == 0){
            //借据对应金额为0
            rsp.setAverageIrr(0);
        }else {
            BigDecimal avIrr = sumIrrAmount.divide(sumAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("1000000"));
            rsp.setAverageIrr(avIrr.intValue());
        }
        return rsp;
    }
}
