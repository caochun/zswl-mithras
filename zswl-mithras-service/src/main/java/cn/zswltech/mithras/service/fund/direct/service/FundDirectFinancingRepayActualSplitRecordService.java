package cn.zswltech.mithras.service.fund.direct.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.capital.BankFlowProcessingCenterFinancePaymentCashFlowRSP;
import cn.zswltech.mithras.dto.capital.FinancePaymentWriteOffREQ;
import cn.zswltech.mithras.dto.capital.FinanceRepaySplitRecordREQ;
import cn.zswltech.mithras.dto.capital.FinanceRepaySplitRecordRSP;
import cn.zswltech.mithras.dto.third.financial.ThirdFinancialWithdrawREQ;
import cn.zswltech.mithras.service.enums.capital.FinancePaymentWriteOffOrderEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.fund.direct.entity.*;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingRepayActualSplitRecordMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/3/30
 * @description
 */
@Slf4j
@Service
public class FundDirectFinancingRepayActualSplitRecordService extends ServiceImpl<FundDirectFinancingRepayActualSplitRecordMapper, FundDirectFinancingRepayActualSplitRecord> {
    @Resource
    private FundDirectFinancingRepayActualSplitService repayActualSplitService;
    @Resource
    private FundDirectFinancingRepayActualService repayActualService;
    @Resource
    private FundDirectFinancingProductDetailService productDetailService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;

    public List<FundDirectFinancingRepayActualSplitRecord> listByCashFlowCode(String cashFlowCode) {
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplitRecord> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingRepayActualSplitRecord::getCashFlowCode, cashFlowCode);
        return this.list(query);
    }

    public List<FundDirectFinancingRepayActualSplitRecord> listByCashFlowCodes(Collection<String> cashFlowCodes) {
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplitRecord> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingRepayActualSplitRecord::getCashFlowCode, cashFlowCodes);
        return this.list(query);
    }

    public List<FundDirectFinancingRepayActualSplitRecord> listByParentCashFlowCodes(Collection<String> parentCashFlowCodes) {
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplitRecord> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingRepayActualSplitRecord::getCashFlowCodeParent, parentCashFlowCodes);
        return this.list(query);
    }

    public List<FinanceRepaySplitRecordRSP> listRepaySplitWriteOffRecord(FinanceRepaySplitRecordREQ req) {
        List<FundDirectFinancingRepayActualSplit> repayActualSplitList = repayActualSplitService.listByParentCashFlowCodes(req.getCashFlowCodeList());
        if (CollectionUtil.isEmpty(repayActualSplitList)) {
            return Collections.emptyList();
        }
        Set<String> cashFlowCodes = repayActualSplitList.stream().map(FundDirectFinancingRepayActualSplit::getCashFlowCode).collect(Collectors.toSet());
        List<FundDirectFinancingRepayActualSplitRecord> splitRecordList = this.listByCashFlowCodes(cashFlowCodes);
        if (CollectionUtil.isEmpty(splitRecordList)) {
            return Collections.emptyList();
        }
        Map<String, FundDirectFinancingRepayActualSplit> repayActualSplitMap = repayActualSplitList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActualSplit::getCashFlowCode, e -> e));
        Set<Long> financingIds = repayActualSplitList.stream().map(FundDirectFinancingRepayActualSplit::getFinancingId).collect(Collectors.toSet());
        Set<Long> productIds = repayActualSplitList.stream().map(FundDirectFinancingRepayActualSplit::getProductDetailId).collect(Collectors.toSet());
        // 取融资信息
        List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfoList = directFinancingBaseInfoService.listByIds(financingIds);
        Map<Long, FundDirectFinancingBaseInfo> baseInfoMap = fundDirectFinancingBaseInfoList.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e));
        List<FundDirectFinancingProductDetail> fundDirectFinancingProductDetailList = productDetailService.listByIds(productIds);
        Map<Long, FundDirectFinancingProductDetail> detailInfoMap = fundDirectFinancingProductDetailList.stream().collect(Collectors.toMap(FundDirectFinancingProductDetail::getId, e -> e));
        return splitRecordList.stream().map(e -> {
            FundDirectFinancingRepayActualSplit repayActualSplit = repayActualSplitMap.get(e.getCashFlowCode());
            FundDirectFinancingBaseInfo baseInfo = baseInfoMap.get(e.getFinancingId());
            FundDirectFinancingProductDetail productDetail = detailInfoMap.get(e.getProductDetailId());
            FinanceRepaySplitRecordRSP rsp = new FinanceRepaySplitRecordRSP();
            rsp.setId(e.getId());
            rsp.setFinancingId(e.getFinancingId());
            rsp.setSplitCashFlowCode(e.getCashFlowCode());
            rsp.setCashFlowItem(e.getCashFlowItem());
            rsp.setWriteOffAmount(e.getWriteOffAmount());
            rsp.setFinancingCode(Optional.ofNullable(baseInfo).map(FundDirectFinancingBaseInfo::getFinancingCode).orElse(null));
            rsp.setSecuritiesCode(Optional.ofNullable(productDetail).map(FundDirectFinancingProductDetail::getSecuritiesCode).orElse(null));
            rsp.setAbbreviation(Optional.ofNullable(productDetail).map(FundDirectFinancingProductDetail::getAbbreviation).orElse(null));
            rsp.setPlanRepayDate(Optional.ofNullable(repayActualSplit).map(FundDirectFinancingRepayActualSplit::getRepayDate).orElse(null));
            rsp.setPhase(Optional.ofNullable(repayActualSplit).map(FundDirectFinancingRepayActualSplit::getPhase).orElse(null));
            return rsp;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void writeOffRepayActualSplit(boolean isAuto, List<FinancePaymentWriteOffREQ.RepaySplitInfo> writeOffList, List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> toBeWriteOffList) {
        log.info("资金端核销拆分后的还款计划[isAuto:{}, writeOffList:{}, toBeWriteOffList:{}]", isAuto, JSONUtil.toJsonStr(writeOffList), JSONUtil.toJsonStr(toBeWriteOffList));
        if (CollectionUtil.isEmpty(writeOffList)) {
            return;
        }
        // 去掉核销金额是0的或者是空的
        writeOffList.removeIf(e -> Objects.isNull(e.getWriteOffAmount()) || e.getWriteOffAmount() == 0);
        Map<String, List<FinancePaymentWriteOffREQ.RepaySplitInfo>> reqMap = writeOffList.stream().collect(Collectors.groupingBy(FinancePaymentWriteOffREQ.RepaySplitInfo::getCashFlowCode));
        // 根据现金流编号找到所有对应的拆分后还款计划
        List<FundDirectFinancingRepayActualSplit> repayActualSplitList = repayActualSplitService.listByCashFlowCodes(writeOffList.stream().map(FinancePaymentWriteOffREQ.RepaySplitInfo::getRepaySplitCashFlowCode).collect(Collectors.toList()));
        Map<Long, FundDirectFinancingRepayActualSplit> repayActualSplitMap = repayActualSplitList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActualSplit::getId, e -> e));
        // 根据现金流编号查询实际核销明细
        Set<String> splitCashFlowCodes = repayActualSplitMap.values().stream().map(FundDirectFinancingRepayActualSplit::getCashFlowCode).collect(Collectors.toSet());
        List<FundDirectFinancingRepayActualSplitRecord> existRecordList = this.listByCashFlowCodes(splitCashFlowCodes);
        Map<String, List<FundDirectFinancingRepayActualSplitRecord>> existRecordMap = existRecordList.stream().collect(Collectors.groupingBy(FundDirectFinancingRepayActualSplitRecord::getCashFlowCode));
        // 根据现金流编号找到所有对应的拆分前还款计划
        List<FundDirectFinancingRepayActual> repayActualList = repayActualService.listByCashFlowCodes(reqMap.keySet());
        Map<String, FundDirectFinancingRepayActual> repayActualMap = repayActualList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActual::getCashFlowCode, e -> e));
        Map<String, List<BankFlowProcessingCenterFinancePaymentCashFlowRSP>> writeOffDetailMap = toBeWriteOffList.stream().collect(Collectors.groupingBy(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getCashFlowCode));
        // 拆分核销
        List<FundDirectFinancingRepayActualSplitRecord> insertList = new LinkedList<>();
        for (Map.Entry<String, List<FinancePaymentWriteOffREQ.RepaySplitInfo>> entry : reqMap.entrySet()) {
            String cashFlowCodeParent = entry.getKey();
            List<FinancePaymentWriteOffREQ.RepaySplitInfo> reqWriteOffList = entry.getValue();
            FundDirectFinancingRepayActual repayActual = repayActualMap.get(cashFlowCodeParent);
            List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> fundReceiptFlowDetailList = writeOffDetailMap.get(cashFlowCodeParent);
            long reqPrincipal = reqWriteOffList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())).filter(e -> Objects.nonNull(e.getWriteOffAmount())).mapToLong(FinancePaymentWriteOffREQ.RepaySplitInfo::getWriteOffAmount).sum();
            long reqInterest = reqWriteOffList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name())).filter(e -> Objects.nonNull(e.getWriteOffAmount())).mapToLong(FinancePaymentWriteOffREQ.RepaySplitInfo::getWriteOffAmount).sum();
            long principal;
            long interest;
            if (isAuto) {
                principal = fundReceiptFlowDetailList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())).filter(e -> Objects.nonNull(e.getActualDetailAmount())).mapToLong(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getActualDetailAmount).sum();
                interest = fundReceiptFlowDetailList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name())).filter(e -> Objects.nonNull(e.getActualDetailAmount())).mapToLong(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getActualDetailAmount).sum();
            } else {
                principal = fundReceiptFlowDetailList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())).filter(e -> Objects.nonNull(e.getOriginalWriteOffAmount())).mapToLong(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getOriginalWriteOffAmount).sum();
                interest = fundReceiptFlowDetailList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name())).filter(e -> Objects.nonNull(e.getOriginalWriteOffAmount())).mapToLong(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getOriginalWriteOffAmount).sum();
            }
            if (reqPrincipal != principal) {
                throw new MithrasException(String.format("现金流编号<%s>的总核销本金不等于拆分核销本金之和", cashFlowCodeParent));
            }
            if (reqInterest != interest) {
                throw new MithrasException(String.format("现金流编号<%s>的总核销利息不等于拆分核销利息之和", cashFlowCodeParent));
            }
            List<FundDirectFinancingRepayActualSplitRecord> list = reqWriteOffList.stream().map(e -> {
                FundDirectFinancingRepayActualSplitRecord record = new FundDirectFinancingRepayActualSplitRecord();
                record.setFinancingId(repayActual.getFinancingId());
                record.setCashFlowCodeParent(e.getCashFlowCode());
                record.setCashFlowCode(e.getRepaySplitCashFlowCode());
                record.setCashFlowItem(e.getCashFlowItem());
                record.setWriteOffAmount(e.getWriteOffAmount());
                record.setProductDetailId(Optional.ofNullable(repayActualSplitMap.get(e.getRepaySplitId())).map(FundDirectFinancingRepayActualSplit::getProductDetailId).orElse(null));
                return record;
            }).collect(Collectors.toList());
            insertList.addAll(list);
        }
        if (CollectionUtil.isNotEmpty(insertList)) {
            Map<String, List<FundDirectFinancingRepayActualSplitRecord>> splitRecordMap = insertList.stream().collect(Collectors.groupingBy(FundDirectFinancingRepayActualSplitRecord::getCashFlowCode));
            // 根据本次核销情况调整拆分明细（暂时不考虑钆差后小于0的情况）
            /* 核销后在直融管理-融资合同详情页面的各产品还款计划中根据核销金额更修正核销当期金额，并在核销下一期将本期核销和计划的本金差额钆平，并依据新的剩余本金重算利息，即：
             * 核销当期本金=该产品核销本金
             * 核销当期利息=该产品核销利息
             * 核销当期剩余本金=上一期剩余本金-该产品核销本金
             * 核销下一期本金=原计划本金+（核销当期计划本金-核销当期核销本金）
             * 核销下一期利息=核销当期剩余本金*产品发行利率/360*间隔天数
             */
            List<FundDirectFinancingRepayActualSplit> splitInsertList = new LinkedList<>();
            Set<Long> removeIds = new HashSet<>();
            Set<String> cashFlowCodes = insertList.stream().map(FundDirectFinancingRepayActualSplitRecord::getCashFlowCode).collect(Collectors.toSet());
            List<FundDirectFinancingRepayActualSplit> currentPhaseList = repayActualSplitService.listByCashFlowCodes(cashFlowCodes);
            for (FundDirectFinancingRepayActualSplit currentPhase : currentPhaseList) {
                List<FundDirectFinancingRepayActualSplitRecord> currentRecordList = splitRecordMap.get(currentPhase.getCashFlowCode());
                if (CollectionUtil.isEmpty(currentRecordList)) {
                    continue;
                }
                List<FundDirectFinancingRepayActualSplitRecord> currentExistRecordList = existRecordMap.get(currentPhase.getCashFlowCode());
                long currentPrincipal = currentRecordList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())).mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).sum();
                if (CollectionUtil.isNotEmpty(currentExistRecordList)) {
                    currentPrincipal = currentPrincipal + currentExistRecordList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())).mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).sum();
                }
                long currentInterest = currentRecordList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name())).mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).sum();
                if (CollectionUtil.isNotEmpty(currentExistRecordList)) {
                    currentInterest = currentInterest + currentExistRecordList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name())).mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).sum();
                }
                FundDirectFinancingRepayActualSplit newCurrentSplit = BeanUtil.copyProperties(currentPhase, FundDirectFinancingRepayActualSplit.class);
                newCurrentSplit.reset();
                newCurrentSplit.setPrincipalAmount(currentPrincipal);
                newCurrentSplit.setInterestAmount(currentInterest);
                newCurrentSplit.setRepayAmount(currentPrincipal + currentInterest);
                // 新的剩余本金 = 老的剩余本金 + （老的本金 - 新的本金）
                newCurrentSplit.setRemainingPrincipalAmount(currentPhase.getRemainingPrincipalAmount() + (currentPhase.getPrincipalAmount() - currentPrincipal));
                newCurrentSplit.setWriteOffStatus(CashFlowState.WRITTEN_OFF.name());
                splitInsertList.add(newCurrentSplit);
                removeIds.add(currentPhase.getId());
                // FIXME 可以优化成非循环查库（但是逻辑会复杂一些），考虑到实际场景核销的时候不会有很多条，暂时循环查库
                FundDirectFinancingRepayActualSplit nextPhase = this.findNextPhase(currentPhase);
                if (Objects.nonNull(nextPhase)) {
                    FundDirectFinancingRepayActualSplit newNextSplit = BeanUtil.copyProperties(nextPhase, FundDirectFinancingRepayActualSplit.class);
                    newNextSplit.reset();
                    newNextSplit.setPrincipalAmount(currentPhase.getPrincipalAmount() - currentPrincipal + nextPhase.getPrincipalAmount());
                    FundDirectFinancingProductDetail productDetail = productDetailService.getById(nextPhase.getProductDetailId());
                    BigDecimal interestRate = BigDecimal.valueOf(productDetail.getIssuanceRate()).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP);
                    long days = LocalDateTimeUtil.between(newCurrentSplit.getRepayDate().atStartOfDay(), newNextSplit.getRepayDate().atStartOfDay(), ChronoUnit.DAYS);
                    newNextSplit.setInterestAmount(Util.mithrasLongDecimalTwo(BigDecimal.valueOf(newCurrentSplit.getRemainingPrincipalAmount()).multiply(interestRate).multiply(BigDecimal.valueOf(days)).longValue()));
                    newNextSplit.setRepayAmount(newNextSplit.getPrincipalAmount() + newNextSplit.getInterestAmount());
                    splitInsertList.add(newNextSplit);
                    removeIds.add(nextPhase.getId());
                }
            }
            if (CollectionUtil.isNotEmpty(insertList)) {
                this.saveBatch(insertList);
            }
            // 删除老的数据（为了方便追溯，不再原数据上修改）
            if (CollectionUtil.isNotEmpty(removeIds)) {
                repayActualSplitService.removeByIds(removeIds);
            }
            if (CollectionUtil.isNotEmpty(splitInsertList)) {
                repayActualSplitService.saveBatch(splitInsertList);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void withdraw(List<Long> fundReceiptFlowDetailIds, List<ThirdFinancialWithdrawREQ.FundDirectRepayActualSplitRecordInfo> splitRecordInfoList) {
        if (CollectionUtil.isEmpty(splitRecordInfoList)) {
            return;
        }
        List<ThirdFinancialWithdrawREQ.FundDirectRepayActualSplitRecordInfo> todoList = new LinkedList<>();
        for (ThirdFinancialWithdrawREQ.FundDirectRepayActualSplitRecordInfo reqInfo : splitRecordInfoList) {
            if (reqInfo.getAmount() < 0) {
                throw new MithrasException("金额不能是负数");
            }
            if (reqInfo.getAmount() > 0) {
                todoList.add(reqInfo);
            }
        }
        if (CollectionUtil.isEmpty(todoList)) {
            return;
        }
        Map<Long, Long> reqMap = todoList.stream().collect(Collectors.toMap(ThirdFinancialWithdrawREQ.FundDirectRepayActualSplitRecordInfo::getId, ThirdFinancialWithdrawREQ.FundDirectRepayActualSplitRecordInfo::getAmount));
        List<FundDirectFinancingRepayActualSplitRecord> dbList = this.listByIds(reqMap.keySet());
        if (CollectionUtil.isEmpty(dbList)) {
            return;
        }
        Set<String> cashFlowCodes = dbList.stream().map(FundDirectFinancingRepayActualSplitRecord::getCashFlowCode).collect(Collectors.toSet());
        // 找到拆分后的还款计划
        List<FundDirectFinancingRepayActualSplit> repayActualSplitList = repayActualSplitService.listByCashFlowCodes(cashFlowCodes);
        if (CollectionUtil.isEmpty(repayActualSplitList)) {
            throw new MithrasException("没有找到拆分后的还款计划");
        }
        Map<String, FundDirectFinancingRepayActualSplit> repayActualSplitMap = repayActualSplitList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActualSplit::getCashFlowCode, e -> e));
        List<FinancePaymentWriteOffREQ.RepaySplitInfo> writeOffList = new LinkedList<>();
        for (FundDirectFinancingRepayActualSplitRecord record : dbList) {
            FundDirectFinancingRepayActualSplit repayActualSplit = repayActualSplitMap.get(record.getCashFlowCode());
            if (Objects.isNull(repayActualSplit)) {
                throw new MithrasException(String.format("没有找到编号为%s的现金流拆分明细", record.getCashFlowCode()));
            }
            long writeOffAmount = reqMap.get(record.getId());
            if (writeOffAmount > record.getWriteOffAmount()) {
                throw new MithrasException("核销金额不能大于已付金额");
            }
            FinancePaymentWriteOffREQ.RepaySplitInfo repaySplitInfo = new FinancePaymentWriteOffREQ.RepaySplitInfo();
            repaySplitInfo.setRepaySplitId(repayActualSplit.getId());
            repaySplitInfo.setRepaySplitCashFlowCode(repayActualSplit.getCashFlowCode());
            repaySplitInfo.setCashFlowCode(repayActualSplit.getCashFlowCodeParent());
            repaySplitInfo.setCashFlowItem(record.getCashFlowItem());
            repaySplitInfo.setWriteOffAmount(writeOffAmount * -1);
            writeOffList.add(repaySplitInfo);
//            BankFlowProcessingCenterFinancePaymentCashFlowRSP rsp = new BankFlowProcessingCenterFinancePaymentCashFlowRSP();
//            rsp.setCashFlowCode(repayActualSplit.getCashFlowCodeParent());
//            rsp.setCashFlowItem(record.getCashFlowItem());
//            rsp.setActualDetailAmount(writeOffAmount * -1);
//            toBeWriteOffList.add(rsp);
        }
        // 通过核销明细来确定已核销信息
        List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> toBeWriteOffList = new LinkedList<>();
        List<FundReceiptFlowDetail> fundReceiptFlowDetailList = SpringUtil.getBean(FundReceiptFlowDetailService.class).listByIds(fundReceiptFlowDetailIds);
        Map<String, List<FundReceiptFlowDetail>> fundReceiptFlowDetailMap = fundReceiptFlowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        for (Map.Entry<String, List<FundReceiptFlowDetail>> entry : fundReceiptFlowDetailMap.entrySet()) {
            long principal = entry.getValue().stream().filter(e -> Objects.nonNull(e.getPrincipalAmount())).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum();
            long interest = entry.getValue().stream().filter(e -> Objects.nonNull(e.getInterestAmount())).mapToLong(FundReceiptFlowDetail::getInterestAmount).sum();
            if (principal > 0) {
                BankFlowProcessingCenterFinancePaymentCashFlowRSP rsp = new BankFlowProcessingCenterFinancePaymentCashFlowRSP();
                rsp.setCashFlowCode(entry.getKey());
                rsp.setCashFlowItem(FinancePaymentWriteOffOrderEnum.PRINCIPAL.name());
                rsp.setActualDetailAmount(principal * -1);
                rsp.setOriginalWriteOffAmount(principal * -1);
                toBeWriteOffList.add(rsp);
            }
            if (interest > 0) {
                BankFlowProcessingCenterFinancePaymentCashFlowRSP rsp = new BankFlowProcessingCenterFinancePaymentCashFlowRSP();
                rsp.setCashFlowCode(entry.getKey());
                rsp.setCashFlowItem(FinancePaymentWriteOffOrderEnum.INTEREST.name());
                rsp.setActualDetailAmount(interest * -1);
                rsp.setOriginalWriteOffAmount(interest * -1);
                toBeWriteOffList.add(rsp);
            }
        }
        // 反核销本质是核销一笔负数的钱，复用核销接口
        SpringUtil.getBean(FundDirectFinancingRepayActualSplitRecordService.class).writeOffRepayActualSplit(false, writeOffList, toBeWriteOffList);
    }

    private FundDirectFinancingRepayActualSplit findNextPhase(FundDirectFinancingRepayActualSplit currentPhase) {
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplit> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingRepayActualSplit::getProductDetailId, currentPhase.getProductDetailId());
        query.gt(FundDirectFinancingRepayActualSplit::getPhase, currentPhase.getPhase());
        query.orderByAsc(FundDirectFinancingRepayActualSplit::getPhase);
        query.orderByAsc(FundDirectFinancingRepayActualSplit::getRepayDate);
        query.last(StringUtil.mysqlLimitOne());
        return repayActualSplitService.getOne(query);
    }

    //FinancePaymentWriteOffOrderEnum.PRINCIPAL.name()
    public List<FundDirectFinancingRepayActualSplitRecord> getProductRepayDetail(Long productId, String cashFlowItem) {
        return this.baseMapper.selectList(Wrappers.<FundDirectFinancingRepayActualSplitRecord>lambdaQuery()
                .eq(FundDirectFinancingRepayActualSplitRecord::getProductDetailId, productId)
                .eq(FundDirectFinancingRepayActualSplitRecord::getCashFlowItem, cashFlowItem)
        );
    }

    //<productId, Record>
    public Map<Long, List<FundDirectFinancingRepayActualSplitRecord>> getBatchProductRepayDetail(List<Long> productIds, String cashFlowItem) {
        if (CollectionUtil.isEmpty(productIds)) {
            return MapUtil.empty();
        }
        return this.baseMapper.selectList(Wrappers.<FundDirectFinancingRepayActualSplitRecord>lambdaQuery()
                .in(FundDirectFinancingRepayActualSplitRecord::getProductDetailId, productIds)
                .eq(FundDirectFinancingRepayActualSplitRecord::getCashFlowItem, cashFlowItem)).stream().collect(Collectors.groupingBy(FundDirectFinancingRepayActualSplitRecord::getProductDetailId));
    }
}
