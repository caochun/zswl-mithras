package cn.zswltech.mithras.others.数据订正;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.contract.core.application.ContractPledgeService;
import cn.zswltech.mithras.contract.core.application.ContractMortgageService;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.CollectionPenaltyReductionRelation;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionOverdueHistory;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.kpi.mapper.model.KpiProvisionDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.afterlease.application.CollectionPenaltyReductionService;
import cn.zswltech.mithras.afterlease.application.impl.CollectionPenaltyReductionRelationService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.collection.application.CollectionOverdueHistoryService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionService;
import cn.zswltech.mithras.service.service.kpi.KpiProvisionDetailService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractMortgageLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractPledgeLibService;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingPledgeInfoLibService;
import cn.zswltech.mithras.kpi.service.lib.KpiProjectDistributionBaseInfoLibService;
import cn.zswltech.mithras.payment.application.lib.libservice.PaymentBaseInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.ContractUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/7/28
 * @description
 */
@Slf4j
public class FixContractCode extends ApplicationTest {
    ThreadLocal<List<Pair<String, String>>> threadLocal = new ThreadLocal<>();

    @Resource
    private TransactionTemplate transactionTemplate;

    @Test
    public void fix() {
        List<FixContractInfo> todoList = this.getTodoContract();
        if (CollectionUtil.isEmpty(todoList)) {
            log.info("没有需要订正的数据");
            return;
        }
        List<Pair<String, String>> l = new LinkedList<>();
        threadLocal.set(l);
        StopWatch stopWatch = StopWatch.create("修订合同编号");
        for (FixContractInfo fixContractInfo : todoList) {
            Long contractId = fixContractInfo.getContractId();
            String oldContractCode = fixContractInfo.getOldContractCode();
            String targetContractCode = fixContractInfo.getNewContractCode();
            stopWatch.start(oldContractCode + " > " + targetContractCode);
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                try {
                    // 合同主表
                    fixContractBaseInfo(contractId, targetContractCode);
                    // 合同-担保措施
                    fixContractGuarantor(contractId, oldContractCode, targetContractCode);
                    // 合同-抵押措施
                    fixContractMortgage(contractId, oldContractCode, targetContractCode);
                    // 合同-质押措施
                    fixContractPledge(contractId, oldContractCode, targetContractCode);
                    // 合同-借据
                    fixContractReceipt(contractId, targetContractCode);
                    // 合同-实际租金表
                    fixContractRentActual(contractId, targetContractCode);
                    // 付款主表
                    fixPaymentBaseInfo(contractId, targetContractCode);
                    // 收款主表
                    fixCollectionBaseInfo(contractId, targetContractCode);
                    // 收款-历史逾期
                    fixCollectionOverdueHistory(contractId, targetContractCode);
                    // 租后-罚息减免
                    fixCollectionPenaltyReductionRelation(contractId, targetContractCode);
                    // 财务管理-FTP计息
                    fixFtpInterestBaseInfo(contractId, targetContractCode);
                    // 财务管理-拨备计提
                    fixProvision(contractId, targetContractCode);
                    // 资金管理-融资管理-质押明细
                    fixFundFinancingPledge(contractId, targetContractCode);
                    // 绩效管理-项目分配
                    fixKpiProjectDistribution(contractId, targetContractCode);
                } catch (Exception e) {
                    transactionStatus.setRollbackOnly();
                    log.error("订正合同编号发生异常[contractId:{}, targetContractCode:{}]", contractId, targetContractCode, e);
                }
            });
            stopWatch.stop();
        }
        log.info("变更数据明细：{}", JSONUtil.toJsonStr(threadLocal.get()));
        log.info("修订合同编号耗时情况:{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    private List<FixContractInfo> getTodoContract() {
        List<FixContractInfo> contractInfoList = new LinkedList<>();
        contractInfoList.add(new FixContractInfo(1175L, "浙商租【2023】保理字第(B-0012)号", "浙商租【2022】保理字第(B-0006)号"));
//        contractInfoList.add(new FixContractInfo(1222L, "浙商租【2023】保理字第(B-0018)号", "浙商租【2023】保理字第(B-0008)号"));
//        contractInfoList.add(new FixContractInfo(1229L, "浙商租【2023】保理字第(B-0022)号", "浙商租【2023】保理字第(B-0010)号"));
//        contractInfoList.add(new FixContractInfo(1232L, "浙商租【2023】保理字第(B-0024)号", "浙商租【2023】保理字第(B-0007)号"));
        contractInfoList.add(new FixContractInfo(1204L, "浙商租【2023】保理字第(B-0015)号", "浙商租【2023】保理字第(B-0005)号"));
//        contractInfoList.add(new FixContractInfo(1243L, "浙商租【2023】保理字第(B-0027)号", "浙商租【2023】保理字第(B-0012)号"));
//        contractInfoList.add(new FixContractInfo(1241L, "浙商租【2023】保理字第(B-0025)号", "浙商租【2023】保理字第(B-0013)号"));
//        contractInfoList.add(new FixContractInfo(1212L, "浙商租【2023】保理字第(B-0017)号", "浙商租【2023】保理字第(B-0006)号"));
//        contractInfoList.add(new FixContractInfo(1175L, "浙商租【2023】保理字第(B-0012)号", "浙商租【2022】保理字第(B-0006)号"));
        contractInfoList.add(new FixContractInfo(1179L, "浙商租【2023】保理字第(B-0013)号", "浙商租【2023】保理字第(B-0003)号"));
        return contractInfoList;
    }

    private void fixContractBaseInfo(Long contractId, String targetContractCode) {
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            log.info("主表数据不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        // 修改主合同编号
        threadLocal.get().add(new Pair<>(contractBaseInfo.getContractCode(), targetContractCode));
        contractBaseInfo.setContractCode(targetContractCode);
        // 修改咨询合同编号
        String newConsultingContractCode = targetContractCode.replace("租", "咨询").replace("保理", "咨询").replace("转租", "咨询").replace("转让", "咨询");
        threadLocal.get().add(new Pair<>(contractBaseInfo.getConsultingContractCode(), newConsultingContractCode));
        contractBaseInfo.setConsultingContractCode(newConsultingContractCode);
        SpringUtil.getBean(ContractBaseInfoService.class).updateById(contractBaseInfo);
        // 修改版本表
        List<ContractBaseInfoLib> contractBaseInfoLibList = SpringUtil.getBean(ContractBaseInfoLibService.class).list(Wrappers.<ContractBaseInfoLib>lambdaQuery().eq(ContractBaseInfoLib::getOriginId, contractId));
        if (CollectionUtil.isEmpty(contractBaseInfoLibList)) {
            return;
        }
        for (ContractBaseInfoLib contractBaseInfoLib : contractBaseInfoLibList) {
            contractBaseInfoLib.setContractCode(contractBaseInfo.getContractCode());
            contractBaseInfoLib.setConsultingContractCode(contractBaseInfo.getConsultingContractCode());
        }
        SpringUtil.getBean(ContractBaseInfoLibService.class).updateBatchById(contractBaseInfoLibList);
    }

    private void fixContractGuarantor(Long contractId, String oldMainContractCode, String targetContractCode) {
        List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractGuarantorList)) {
            log.info("合同-担保措施不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (ContractGuarantor contractGuarantor : contractGuarantorList) {
            if (StrUtil.isBlank(contractGuarantor.getGuarantorContractCode())) {
                continue;
            }
            String newGuarantorContractCode = fixSubContractCode(contractGuarantor.getGuarantorContractCode(), oldMainContractCode, targetContractCode);
            threadLocal.get().add(new Pair<>(contractGuarantor.getGuarantorContractCode(), newGuarantorContractCode));
            contractGuarantor.setGuarantorContractCode(newGuarantorContractCode);
        }
        SpringUtil.getBean(ContractGuarantorService.class).updateBatchById(contractGuarantorList);
        // 更新版本表
        List<ContractGuarantorLib> contractGuarantorLibList = SpringUtil.getBean(ContractGuarantorLibService.class).list(Wrappers.<ContractGuarantorLib>lambdaQuery().eq(ContractGuarantor::getContactId, contractId));
        if (CollectionUtil.isEmpty(contractGuarantorLibList)) {
            return;
        }
        for (ContractGuarantorLib contractGuarantorLib : contractGuarantorLibList) {
            if (StrUtil.isBlank(contractGuarantorLib.getGuarantorContractCode())) {
                continue;
            }
            contractGuarantorLib.setGuarantorContractCode(fixSubContractCode(contractGuarantorLib.getGuarantorContractCode(), oldMainContractCode, targetContractCode));
        }
        SpringUtil.getBean(ContractGuarantorLibService.class).updateBatchById(contractGuarantorLibList);
    }

    private void fixContractMortgage(Long contractId, String oldMainContractCode, String targetContractCode) {
        List<ContractMortgage> contractMortgageList = SpringUtil.getBean(ContractMortgageService.class).listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractMortgageList)) {
            log.info("合同-抵押措施不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (ContractMortgage contractMortgage : contractMortgageList) {
            if (StrUtil.isBlank(contractMortgage.getMortgageContractCode())) {
                continue;
            }
            String newMortgageContractCode = fixSubContractCode(contractMortgage.getMortgageContractCode(), oldMainContractCode, targetContractCode);
            threadLocal.get().add(new Pair<>(contractMortgage.getMortgageContractCode(), newMortgageContractCode));
            contractMortgage.setMortgageContractCode(newMortgageContractCode);
        }
        SpringUtil.getBean(ContractMortgageService.class).updateBatchById(contractMortgageList);
        // 更新版本表
        List<ContractMortgageLib> contractMortgageLibList = SpringUtil.getBean(ContractMortgageLibService.class).list(Wrappers.<ContractMortgageLib>lambdaQuery().eq(ContractMortgage::getContractId, contractId));
        if (CollectionUtil.isEmpty(contractMortgageLibList)) {
            return;
        }
        for (ContractMortgageLib contractMortgageLib : contractMortgageLibList) {
            if (StrUtil.isBlank(contractMortgageLib.getMortgageContractCode())) {
                continue;
            }
            contractMortgageLib.setMortgageContractCode(fixSubContractCode(contractMortgageLib.getMortgageContractCode(), oldMainContractCode, targetContractCode));
        }
        SpringUtil.getBean(ContractMortgageLibService.class).updateBatchById(contractMortgageLibList);
    }

    private void fixContractPledge(Long contractId, String oldMainContractCode, String targetContractCode) {
        List<ContractPledge> contractPledgeList = SpringUtil.getBean(ContractPledgeService.class).listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractPledgeList)) {
            log.info("合同-质押措施不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (ContractPledge contractPledge : contractPledgeList) {
            if (StrUtil.isBlank(contractPledge.getPledgeContractCode())) {
                continue;
            }
            String newPledgeContractCode = fixSubContractCode(contractPledge.getPledgeContractCode(), oldMainContractCode, targetContractCode);
            threadLocal.get().add(new Pair<>(contractPledge.getPledgeContractCode(), newPledgeContractCode));
            contractPledge.setPledgeContractCode(newPledgeContractCode);
        }
        SpringUtil.getBean(ContractPledgeService.class).updateBatchById(contractPledgeList);
        // 更新版本表
        List<ContractPledgeLib> contractPledgeLibList = SpringUtil.getBean(ContractPledgeLibService.class).list(Wrappers.<ContractPledgeLib>lambdaQuery().eq(ContractPledge::getContractId, contractId));
        if (CollectionUtil.isEmpty(contractPledgeLibList)) {
            return;
        }
        for (ContractPledgeLib contractPledgeLib : contractPledgeLibList) {
            if (StrUtil.isBlank(contractPledgeLib.getPledgeContractCode())) {
                continue;
            }
            contractPledgeLib.setPledgeContractCode(fixSubContractCode(contractPledgeLib.getPledgeContractCode(), oldMainContractCode, targetContractCode));
        }
        SpringUtil.getBean(ContractPledgeLibService.class).updateBatchById(contractPledgeLibList);
    }

    private void fixContractReceipt(Long contractId, String targetContractCode) {
        List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            log.info("合同-借据不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            log.info("合同主表信息不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
        }
        for (ContractReceipt contractReceipt : contractReceiptList) {
            if (StrUtil.isBlank(contractReceipt.getReceiptCode())) {
                continue;
            }
            contractReceipt.setReceiptCode(fixReceiptCode(contractReceipt.getReceiptCode(), targetContractCode));
        }
        SpringUtil.getBean(ContractReceiptService.class).updateBatchById(contractReceiptList);
        // 更新版本表
        List<ContractReceiptLib> contractReceiptLibList = SpringUtil.getBean(ContractReceiptLibService.class).list(Wrappers.<ContractReceiptLib>lambdaQuery().eq(ContractReceipt::getContractId, contractId));
        if (CollectionUtil.isEmpty(contractReceiptLibList)) {
            return;
        }
        for (ContractReceiptLib contractReceiptLib : contractReceiptLibList) {
            if (StrUtil.isBlank(contractReceiptLib.getReceiptCode())) {
                continue;
            }
            contractReceiptLib.setReceiptCode(fixReceiptCode(contractReceiptLib.getReceiptCode(), targetContractCode));
        }
        SpringUtil.getBean(ContractReceiptLibService.class).updateBatchById(contractReceiptLibList);
    }

    private void fixContractRentActual(Long contractId, String targetContractCode) {
        List<ContractRentActual> contractRentActualList = SpringUtil.getBean(ContractRentActualService.class).listByContract(contractId);
        if (CollectionUtil.isEmpty(contractRentActualList)) {
            log.info("合同-实际租金表不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (ContractRentActual contractRentActual : contractRentActualList) {
            if (StrUtil.isBlank(contractRentActual.getCashFlowCode())) {
                continue;
            }
            contractRentActual.setCashFlowCode(fixCashFlowCode(contractRentActual.getCashFlowCode(), targetContractCode));
        }
        SpringUtil.getBean(ContractRentActualService.class).updateBatchById(contractRentActualList);
        // 更新版本表
        List<ContractRentActualLib> contractRentActualLibList = SpringUtil.getBean(ContractRentActualLibService.class).list(Wrappers.<ContractRentActualLib>lambdaQuery().eq(ContractRentActual::getContractId, contractId));
        if (CollectionUtil.isEmpty(contractRentActualLibList)) {
            return;
        }
        for (ContractRentActualLib contractRentActualLib : contractRentActualLibList) {
            if (StrUtil.isBlank(contractRentActualLib.getCashFlowCode())) {
                continue;
            }
            contractRentActualLib.setCashFlowCode(fixCashFlowCode(contractRentActualLib.getCashFlowCode(), targetContractCode));
        }
        SpringUtil.getBean(ContractRentActualLibService.class).updateBatchById(contractRentActualLibList);
    }

    private void fixPaymentBaseInfo(Long contractId, String targetContractCode) {
        List<PaymentBaseInfo> paymentBaseInfoList = SpringUtil.getBean(PaymentBaseInfoService.class).list(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            log.info("付款信息不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            String newPaymentCode = fixPaymentCode(paymentBaseInfo.getPaymentCode(), targetContractCode);
            threadLocal.get().add(new Pair<>(paymentBaseInfo.getPaymentCode(), newPaymentCode));
            paymentBaseInfo.setPaymentCode(newPaymentCode);
            paymentBaseInfo.setContractCode(targetContractCode);
            if (StrUtil.isNotBlank(paymentBaseInfo.getReceiptCode())) {
                if (StrUtil.isBlank(paymentBaseInfo.getReceiptCode())) {
                    continue;
                }
                paymentBaseInfo.setReceiptCode(fixReceiptCode(paymentBaseInfo.getReceiptCode(), targetContractCode));
            }
        }
        SpringUtil.getBean(PaymentBaseInfoService.class).updateBatchById(paymentBaseInfoList);
        // 更新版本表
        List<PaymentBaseInfoLib> paymentBaseInfoLibList = SpringUtil.getBean(PaymentBaseInfoLibService.class).list(Wrappers.<PaymentBaseInfoLib>lambdaQuery().eq(PaymentBaseInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(paymentBaseInfoLibList)) {
            return;
        }
        for (PaymentBaseInfoLib paymentBaseInfoLib : paymentBaseInfoLibList) {
            paymentBaseInfoLib.setPaymentCode(fixPaymentCode(paymentBaseInfoLib.getPaymentCode(), targetContractCode));
            paymentBaseInfoLib.setContractCode(targetContractCode);
            if (StrUtil.isNotBlank(paymentBaseInfoLib.getReceiptCode())) {
                if (StrUtil.isBlank(paymentBaseInfoLib.getReceiptCode())) {
                    continue;
                }
                paymentBaseInfoLib.setReceiptCode(fixReceiptCode(paymentBaseInfoLib.getReceiptCode(), targetContractCode));
            }
        }
        SpringUtil.getBean(PaymentBaseInfoLibService.class).updateBatchById(paymentBaseInfoLibList);
    }

    private void fixCollectionBaseInfo(Long contractId, String targetContractCode) {
        List<CollectionBaseInfo> collectionBaseInfoList = SpringUtil.getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            log.info("收款信息不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            collectionBaseInfo.setContractCode(targetContractCode);
            if (StrUtil.isNotBlank(collectionBaseInfo.getPaymentCode())) {
                collectionBaseInfo.setPaymentCode(fixPaymentCode(collectionBaseInfo.getPaymentCode(), targetContractCode));
            }
            if (StrUtil.isNotBlank(collectionBaseInfo.getReceiptCode())) {
                if (StrUtil.isBlank(collectionBaseInfo.getReceiptCode())) {
                    continue;
                }
                collectionBaseInfo.setReceiptCode(fixReceiptCode(collectionBaseInfo.getReceiptCode(), targetContractCode));
            }
            if (StrUtil.isNotBlank(collectionBaseInfo.getCode())) {
                collectionBaseInfo.setCode(fixCollectionCode(collectionBaseInfo.getCode(), targetContractCode));
            }
        }
        SpringUtil.getBean(CollectionBaseInfoService.class).updateBatchById(collectionBaseInfoList);
    }

    private void fixCollectionOverdueHistory(Long contractId, String targetContractCode) {
        List<CollectionOverdueHistory> collectionOverdueHistoryList = SpringUtil.getBean(CollectionOverdueHistoryService.class).list(Wrappers.<CollectionOverdueHistory>lambdaQuery().eq(CollectionOverdueHistory::getContractId, contractId));
        if (CollectionUtil.isEmpty(collectionOverdueHistoryList)) {
            log.info("收款-历史逾期记录不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (CollectionOverdueHistory collectionOverdueHistory : collectionOverdueHistoryList) {
            collectionOverdueHistory.setContractCode(targetContractCode);
            if (StrUtil.isNotBlank(collectionOverdueHistory.getCollectionCode())) {
                collectionOverdueHistory.setCollectionCode(fixCollectionCode(collectionOverdueHistory.getCollectionCode(), targetContractCode));
            }
        }
        SpringUtil.getBean(CollectionOverdueHistoryService.class).updateBatchById(collectionOverdueHistoryList);
    }

    private void fixCollectionPenaltyReductionRelation(Long contractId, String targetContractCode) {
        List<CollectionPenaltyReductionInfo> reductionInfoList = SpringUtil.getBean(CollectionPenaltyReductionService.class).list(Wrappers.<CollectionPenaltyReductionInfo>lambdaQuery().eq(CollectionPenaltyReductionInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(reductionInfoList)) {
            log.info("租后-罚息减免信息不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        Set<Long> reduceIds = reductionInfoList.stream().map(CollectionPenaltyReductionInfo::getId).collect(Collectors.toSet());
        List<CollectionPenaltyReductionRelation> reductionRelationList = SpringUtil.getBean(CollectionPenaltyReductionRelationService.class).list(Wrappers.<CollectionPenaltyReductionRelation>lambdaQuery().in(CollectionPenaltyReductionRelation::getReduceId, reduceIds));
        if (CollectionUtil.isEmpty(reductionRelationList)) {
            return;
        }
        for (CollectionPenaltyReductionRelation reductionRelation : reductionRelationList) {
            if (StrUtil.isNotBlank(reductionRelation.getPaymentCode())) {
                reductionRelation.setPaymentCode(fixPaymentCode(reductionRelation.getPaymentCode(), targetContractCode));
            }
        }
        SpringUtil.getBean(CollectionPenaltyReductionRelationService.class).updateBatchById(reductionRelationList);
    }

    private void fixFtpInterestBaseInfo(Long contractId, String targetContractCode) {
        List<FtpInterestBaseInfo> ftpInterestBaseInfoList = SpringUtil.getBean(FtpInterestBaseInfoService.class).list(Wrappers.<FtpInterestBaseInfo>lambdaQuery().eq(FtpInterestBaseInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(ftpInterestBaseInfoList)) {
            log.info("财务管理-FTP计息信息不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (FtpInterestBaseInfo ftpInterestBaseInfo : ftpInterestBaseInfoList) {
            ftpInterestBaseInfo.setContractCode(targetContractCode);
            if (StrUtil.isNotBlank(ftpInterestBaseInfo.getReceiptCode())) {
                if (StrUtil.isBlank(ftpInterestBaseInfo.getReceiptCode())) {
                    continue;
                }
                ftpInterestBaseInfo.setReceiptCode(fixReceiptCode(ftpInterestBaseInfo.getReceiptCode(), targetContractCode));
            }
        }
        SpringUtil.getBean(FtpInterestBaseInfoService.class).updateBatchById(ftpInterestBaseInfoList);
    }

    private void fixFundFinancingPledge(Long contractId, String targetContractCode) {
        List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = SpringUtil.getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().eq(FundFinancingPledgeInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(fundFinancingPledgeInfoList)) {
            log.info("资金管理-融资管理-质押信息不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (FundFinancingPledgeInfo fundFinancingPledgeInfo : fundFinancingPledgeInfoList) {
            fundFinancingPledgeInfo.setContractCode(targetContractCode);
        }
        SpringUtil.getBean(FundFinancingPledgeInfoService.class).updateBatchById(fundFinancingPledgeInfoList);
        // 更新版本表
        List<FundFinancingPledgeInfoLib> fundFinancingPledgeInfoLibList = SpringUtil.getBean(FundFinancingPledgeInfoLibService.class).list(Wrappers.<FundFinancingPledgeInfoLib>lambdaQuery().eq(FundFinancingPledgeInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(fundFinancingPledgeInfoLibList)) {
            return;
        }
        for (FundFinancingPledgeInfoLib fundFinancingPledgeInfoLib : fundFinancingPledgeInfoLibList) {
            fundFinancingPledgeInfoLib.setContractCode(targetContractCode);
        }
        SpringUtil.getBean(FundFinancingPledgeInfoLibService.class).updateBatchById(fundFinancingPledgeInfoLibList);
    }

    private void fixKpiProjectDistribution(Long contractId, String targetContractCode) {
        List<KpiProjectDistribution> kpiProjectDistributionList = SpringUtil.getBean(KpiProjectDistributionService.class).list(Wrappers.<KpiProjectDistribution>lambdaQuery().eq(KpiProjectDistribution::getContractId, contractId));
        if (CollectionUtil.isEmpty(kpiProjectDistributionList)) {
            log.info("绩效管理-项目分配不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        Set<Long> projectDistributionIds = kpiProjectDistributionList.stream().map(KpiProjectDistribution::getId).collect(Collectors.toSet());
        List<KpiProjectDistributionBaseInfo> kpiProjectDistributionBaseInfoList = SpringUtil.getBean(KpiProjectDistributionBaseInfoService.class).list(Wrappers.<KpiProjectDistributionBaseInfo>lambdaQuery().in(KpiProjectDistributionBaseInfo::getProjectDistributionId, projectDistributionIds));
        if (CollectionUtil.isEmpty(kpiProjectDistributionBaseInfoList)) {
            return;
        }
        for (KpiProjectDistributionBaseInfo kpiProjectDistributionBaseInfo : kpiProjectDistributionBaseInfoList) {
            kpiProjectDistributionBaseInfo.setContractCode(targetContractCode);
        }
        SpringUtil.getBean(KpiProjectDistributionBaseInfoService.class).updateBatchById(kpiProjectDistributionBaseInfoList);
        // 更新版本表
        List<KpiProjectDistributionBaseInfoLib> kpiProjectDistributionBaseInfoLibList = SpringUtil.getBean(KpiProjectDistributionBaseInfoLibService.class).list(Wrappers.<KpiProjectDistributionBaseInfoLib>lambdaQuery().in(KpiProjectDistributionBaseInfo::getProjectDistributionId, projectDistributionIds));
        if (CollectionUtil.isEmpty(kpiProjectDistributionBaseInfoLibList)) {
            return;
        }
        for (KpiProjectDistributionBaseInfoLib kpiProjectDistributionBaseInfoLib : kpiProjectDistributionBaseInfoLibList) {
            kpiProjectDistributionBaseInfoLib.setContractCode(targetContractCode);
        }
        SpringUtil.getBean(KpiProjectDistributionBaseInfoLibService.class).updateBatchById(kpiProjectDistributionBaseInfoLibList);
    }

    private void fixProvision(Long contractId, String targetContractCode) {
        List<KpiProvisionDetail> kpiProvisionDetailList = SpringUtil.getBean(KpiProvisionDetailService.class).list(Wrappers.<KpiProvisionDetail>lambdaQuery().eq(KpiProvisionDetail::getContractId, contractId));
        if (CollectionUtil.isEmpty(kpiProvisionDetailList)) {
            log.info("财务管理-拨备计提信息不存在[contractId:{}, targetContractCode:{}]", contractId, targetContractCode);
            return;
        }
        for (KpiProvisionDetail kpiProvisionDetail : kpiProvisionDetailList) {
            kpiProvisionDetail.setContractCode(targetContractCode);
        }
        SpringUtil.getBean(KpiProvisionDetailService.class).updateBatchById(kpiProvisionDetailList);
    }

    private String fixSubContractCode(String oldSubContractCode, String oldMainContractCode, String newMainContractCode) {
        int oldLeft = oldMainContractCode.indexOf('(');
        int oldRight = oldMainContractCode.lastIndexOf(')');
        String oldSubStr = oldMainContractCode.substring(oldLeft, oldRight);
        int newLeft = newMainContractCode.indexOf('(');
        int newRight = newMainContractCode.lastIndexOf(')');
        String newSubStr = newMainContractCode.substring(newLeft, newRight);
        return oldSubContractCode.replace(oldSubStr, newSubStr);
    }

    private String fixReceiptCode(String oldReceiptCode, String targetContractCode) {
        String newTempReceiptCode = ContractUtil.generateReceiptCode(targetContractCode, 1, false);
        String s1 = newTempReceiptCode.substring(0, newTempReceiptCode.indexOf("-"));
        String s2 = oldReceiptCode.substring(oldReceiptCode.indexOf("-"));
        return s1 + s2;
    }

    private String fixCashFlowCode(String oldCashFlowCode, String targetContractCode) {
        String newTempReceiptCode = ContractUtil.generateReceiptCode(targetContractCode, 1, false);
        String s1 = newTempReceiptCode.substring(0, newTempReceiptCode.indexOf("-"));
        String s2 = oldCashFlowCode.substring(oldCashFlowCode.indexOf("-"));
        return s1 + s2;
    }

    private String fixPaymentCode(String oldPaymentCode, String targetContractCode) {
        String newTempPaymentCode = PaymentBaseInfoService.generatePaymentCode(targetContractCode, 1);
        String s1 = newTempPaymentCode.substring(0, newTempPaymentCode.indexOf("-"));
        String s2 = oldPaymentCode.substring(oldPaymentCode.indexOf("-"));
        return s1 + s2;
    }

    private String fixCollectionCode(String oldCollectionCode, String targetContractCode) {
        String newTempReceiptCode = ContractUtil.generateReceiptCode(targetContractCode, 1, false);
        String s1 = newTempReceiptCode.substring(0, newTempReceiptCode.indexOf("-"));
        String s2 = oldCollectionCode.substring(oldCollectionCode.indexOf("-"));
        return s1 + s2;
    }

    public static void main(String[] args) {
        FixContractCode fixContractCode = new FixContractCode();
//        System.out.println(fixContractCode.fixReceiptCode("2023A0068-03", "浙商租【2022】租字第(A-0084)号"));
//        System.out.println(fixContractCode.fixCashFlowCode("2023A0068-01-012", "浙商租【2022】租字第(A-0084)号"));
//        System.out.println(fixContractCode.fixPaymentCode("2023A0047-02", "浙商租【2022】租字第(A-0084)号"));
//        System.out.println(fixContractCode.fixCollectionCode("2022A0094-sf", "浙商租【2022】租字第(A-0084)号"));
        System.out.println(fixContractCode.fixSubContractCode("浙商租【2023】保证字第(A-0074-01)号", "浙商租【2023】租字第(A-0074)号", "浙商租【2023】租字第(A-0075)号"));
    }

    @AllArgsConstructor
    @Getter
    private static class FixContractInfo {
        private final Long contractId;
        private final String oldContractCode;
        private final String newContractCode;
    }
}
