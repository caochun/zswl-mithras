package cn.zswltech.mithras.service.service.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.incomeSharing.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.contract.enums.OverdueTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.excel.exporter.ContractIncomeSharingExcelManagerExporter;
import cn.zswltech.mithras.service.excel.exporter.ContractIncomeSharingExcelManagerExporter2;
import cn.zswltech.mithras.service.excel.model.ContractIncomeSharingExcelModel;
import cn.zswltech.mithras.service.excel.model.ContractIncomeSharingExcelModel2;
import cn.zswltech.mithras.contract.mapper.contract.ContractIncomeSharingMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractIncomeSharing;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.service.bo.DailyDiscountRateCalcResultBO;
import cn.zswltech.mithras.projectprocess.service.bo.IncomeSharingCashFlowBO;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.monthly.event.MonthlyManageUpdateEvent;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/4/9
 * @description
 */
@Slf4j
@Service
public class ContractIncomeSharingService extends ServiceImpl<ContractIncomeSharingMapper, ContractIncomeSharing> {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ContractIncomeSharingExcelManagerExporter contractIncomeSharingExcelManagerExporter;
    @Resource
    private ContractIncomeSharingExcelManagerExporter2 contractIncomeSharingExcelManagerExporter2;
    @Resource
    private ContractIncomeSharingMapper contractIncomeSharingMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;

    public long calculateIncomeRP(Long receiptId, LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            return 0L;
        }
        LambdaQueryWrapper<ContractIncomeSharing> query1 = Wrappers.lambdaQuery();
        query1.eq(ContractIncomeSharing::getReceiptId, receiptId);
        query1.le(ContractIncomeSharing::getIncomeDate, startDate);
        query1.orderByDesc(ContractIncomeSharing::getIncomeDate);
        query1.last(StringUtil.mysqlLimitOne());
        ContractIncomeSharing cis1 = this.getOne(query1);
        LambdaQueryWrapper<ContractIncomeSharing> query2 = Wrappers.lambdaQuery();
        query2.eq(ContractIncomeSharing::getReceiptId, receiptId);
        query2.ge(ContractIncomeSharing::getIncomeDate, endDate);
        query2.orderByAsc(ContractIncomeSharing::getIncomeDate);
        query2.last(StringUtil.mysqlLimitOne());
        ContractIncomeSharing cis2 = this.getOne(query2);
        return Optional.ofNullable(cis2).map(ContractIncomeSharing::getIncome).orElse(0L) - Optional.ofNullable(cis1).map(ContractIncomeSharing::getIncome).orElse(0L);
    }

    public void export(OutputStream outputStream, IncomeConfirmTypeEnum incomeConfirmTypeEnum, LocalDate startDate, LocalDate endDate) {
        // 查起租合同
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).list(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                        .eq(ContractBaseInfo::getIncomeConfirmType, incomeConfirmTypeEnum.name())
        );
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return;
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            // 查借据
            List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(contractReceiptList)) {
                continue;
            }
            for (ContractReceipt contractReceipt : contractReceiptList) {
                List<ContractIncomeSharing> list = this.list(
                        Wrappers.<ContractIncomeSharing>lambdaQuery()
                                .eq(ContractIncomeSharing::getContractId, contractBaseInfo.getId())
                                .eq(ContractIncomeSharing::getReceiptId, contractReceipt.getId())
                                .ge(ContractIncomeSharing::getIncomeDate, startDate)
                                .le(ContractIncomeSharing::getIncomeDate, endDate)
                                .orderByAsc(ContractIncomeSharing::getIncomeDate)
                );
                try {
                    if (CollectionUtil.isNotEmpty(list)) {
                        excelWriter.setSheet(contractReceipt.getReceiptCode());
                        excelWriter.getSheet().setDefaultColumnWidth(22);
                        excelWriter.writeHeadRow(ListUtil.of("日期", "期次", "期初余额", "应收租金", "收入", "不含税收入", "税额", "期末余额", "日折现率", "合同编号", "借据编号"));
                        for (ContractIncomeSharing contractIncomeSharing : list) {
                            excelWriter.writeRow(ListUtil.of(
                                            LocalDateTimeUtil.format(contractIncomeSharing.getIncomeDate(), DatePattern.NORM_DATE_PATTERN),
                                            contractIncomeSharing.getIncomePhase(),
                                            BigDecimal.valueOf(contractIncomeSharing.getBeginOfTermBalance()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP),
                                            BigDecimal.valueOf(contractIncomeSharing.getRent()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP),
                                            BigDecimal.valueOf(contractIncomeSharing.getIncome()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP),
                                            BigDecimal.valueOf(contractIncomeSharing.getIncomeWithoutTax()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP),
                                            BigDecimal.valueOf(contractIncomeSharing.getTax()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP),
                                            BigDecimal.valueOf(contractIncomeSharing.getEndOfTermBalance()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP),
                                            contractIncomeSharing.getDailyDiscountRate(),
                                            contractBaseInfo.getContractCode(),
                                            contractReceipt.getReceiptCode()
                                    )
                            );
                        }
                    }
                } catch (Exception e) {
                    log.error("写excel发生异常", e);
                }
            }
        }
        excelWriter.flush(outputStream, true);
    }

    public int countByReceiptId(Long receiptId) {
        LambdaQueryWrapper<ContractIncomeSharing> query = Wrappers.lambdaQuery();
        query.eq(ContractIncomeSharing::getReceiptId, receiptId);
        return this.count(query);
    }

    public List<ContractIncomeSharing> listByReceiptId(Long receiptId) {
        LambdaQueryWrapper<ContractIncomeSharing> query = Wrappers.lambdaQuery();
        query.eq(ContractIncomeSharing::getReceiptId, receiptId);
        return this.list(query);
    }

    public void deleteByReceiptId(Long receiptId) {
        LambdaQueryWrapper<ContractIncomeSharing> query = Wrappers.lambdaQuery();
        query.eq(ContractIncomeSharing::getReceiptId, receiptId);
        this.remove(query);
    }

    public void calculateIncomeSharing(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        IncomeConfirmTypeEnum incomeConfirmTypeEnum = IncomeConfirmTypeEnum.find(contractBaseInfo.getIncomeConfirmType());
        if (Objects.isNull(incomeConfirmTypeEnum)) {
            throw new MithrasException("未定义的收入确认方式");
        }
        if (incomeConfirmTypeEnum == IncomeConfirmTypeEnum.AIR) {
            this.doCalculateByAIR(contractBaseInfo);
        } else if (incomeConfirmTypeEnum == IncomeConfirmTypeEnum.RP) {
            this.doCalculateByRP(contractBaseInfo);
        } else {
            throw new MithrasException("未实现的收入确认计算方式");
        }
    }

    private void doCalculateByAIR(ContractBaseInfo contractBaseInfo) {
        // 查询借据
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            return;
        }
        for (ContractReceipt contractReceipt : contractReceiptList) {
            try {
                int count = this.countByReceiptId(contractReceipt.getId());
                if (count > 0) {
                    this.doUpdateCalculateIncomeSharingByAIR(contractBaseInfo, contractReceipt);
                } else {
                    this.doAddCalculateIncomeSharingByAIR(contractBaseInfo, contractReceipt);
                }
            } catch (Exception e) {
                log.error("实际利率法-尝试计算收入分摊明细发生异常[{}]", contractReceipt.getReceiptCode(), e);
            }
        }
    }

    private void doCalculateByRP(ContractBaseInfo contractBaseInfo) {
        // 查询借据
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            return;
        }
        for (ContractReceipt contractReceipt : contractReceiptList) {
            try {
                int count = this.countByReceiptId(contractReceipt.getId());
                if (count > 0) {
                    this.doUpdateCalculateIncomeSharingByRP(contractBaseInfo, contractReceipt);
                } else {
                    this.doAddCalculateIncomeSharingByRP(contractBaseInfo, contractReceipt);
                }
            } catch (Exception e) {
                log.error("剩余本金法-尝试计算收入分摊明细发生异常[{}]", contractReceipt.getReceiptCode(), e);
            }
        }
    }

    private void doAddCalculateIncomeSharingByRP(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt) {
        String batchSequence = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        List<ContractIncomeSharing> toInsertList = this.listCalculateIncomeSharingResultByRP(contractBaseInfo, contractReceipt, batchSequence);
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                try {
                    saveBatch(toInsertList);
                } catch (Exception e) {
                    transactionStatus.setRollbackOnly();
                    log.error("剩余本金法-新增收益分摊明细异常[receiptCode:{}]", contractReceipt.getReceiptCode(), e);
                }
            });
        }
    }

    private void doUpdateCalculateIncomeSharingByRP(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt) {
        if (Objects.isNull(contractReceipt.getIncomeSharingFlag()) || Objects.equals(contractReceipt.getIncomeSharingFlag(), YesOrNoNumberEnum.YES.getCode())) {
            log.info("剩余本金法-{}未被标记为需要重算收入明细，忽略不处理", contractReceipt.getReceiptCode());
            return;
        }
//        LocalDate changeDate = contractReceipt.getChangeDate();
        LocalDate changeDate = this.ensureChangeDate(contractReceipt);
        if (Objects.isNull(changeDate)) {
            throw new MithrasException("剩余本金法-变更日期非法");
        }
        String batchSequence = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        List<ContractIncomeSharing> oldList = this.listByReceiptId(contractReceipt.getId());
        List<ContractIncomeSharing> newList = this.listCalculateIncomeSharingResultByRP(contractBaseInfo, contractReceipt, batchSequence);
        if (CollectionUtil.isEmpty(oldList) || CollectionUtil.isEmpty(newList)) {
            log.error("剩余本金法-执行收入分摊变更逻辑存在数据缺失[contractId:{}, receiptId:{}]", contractBaseInfo.getId(), contractReceipt.getId());
            throw new MithrasException("剩余本金法-执行收入分摊变更逻辑存在数据缺失");
        }

        LinkedList<ContractIncomeSharing> toSaveList = new LinkedList<>();
        // 合并新老
        Iterator<ContractIncomeSharing> oldIter = oldList.iterator();
        while (oldIter.hasNext()) {
            ContractIncomeSharing item = oldIter.next();
            if (item.getIncomeDate().isBefore(changeDate)) {
                item.setId(null);
                item.setBatchSequence(batchSequence);
                item.setCreateBy(null);
                item.setUpdateBy(null);
                item.setCreateTime(null);
                item.setUpdateTime(null);
                toSaveList.add(item);
            }
            oldIter.remove();
        }
        Iterator<ContractIncomeSharing> newIter = newList.iterator();
        while (newIter.hasNext()) {
            ContractIncomeSharing item = newIter.next();
            if (!item.getIncomeDate().isBefore(changeDate)) {
                toSaveList.add(item);
            }
            newIter.remove();
        }
        // 排序
        toSaveList.sort(Comparator.comparing(ContractIncomeSharing::getIncomeDate));
        // 更新数据
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 删除老的，保存新的
                deleteByReceiptId(contractReceipt.getId());
                if (CollectionUtil.isNotEmpty(toSaveList)) {
                    saveBatch(toSaveList);
                }
                // 重置标识位
                contractReceipt.setIncomeSharingFlag(YesOrNoNumberEnum.YES.getCode());
                contractReceiptService.updateById(contractReceipt);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("剩余本金法-变更收益分摊明细异常[receiptCode:{}]", contractReceipt.getReceiptCode(), e);
            }
        });
        // 发通知尝试更新月结管理数据
        ThreadPoolUtil.getCommonPool().execute(() -> {
            try {
                MonthlyManageUpdateEvent event = new MonthlyManageUpdateEvent("剩余本金法-变更", new MonthlyManageUpdateEvent.DataObject(MonthlyModuleTypeEnum.RP.name(), contractReceipt.getId(), null));
                applicationEventPublisher.publishEvent(event);
            } catch (Exception e) {
                log.error("剩余本金法-变更通知发送异常[receiptCode:{}]", contractReceipt.getReceiptCode(), e);
            }
        });
    }

    private List<ContractIncomeSharing> listCalculateIncomeSharingResultByRP(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt, String batchSequence) {
        // 查询实际租金表
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipt(contractReceipt.getId());
        if (CollectionUtil.isEmpty(contractRentActualList)) {
            throw new MithrasException("实际租金表为空，无法计算收入明细");
        }
        // 模型转换
        List<CashFlowBO> cashFlowList = contractRentActualList.stream().map(e -> {
            CashFlowBO item = new CashFlowBO();
            item.setCashFlowPhase(e.getCashFlowPhase());
            item.setCashFlowDate(e.getCashFlowDate());
            item.setPrincipal(e.getPrincipal());
            item.setRent(e.getRent());
            item.setInterest(e.getInterest());
            return item;
        }).collect(Collectors.toList());
        // 查询合同利率
        ContractPriceDetailREQ req = new ContractPriceDetailREQ();
        req.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP rsp = contractPriceService.detail(req);
        int lprRate = Optional.ofNullable(rsp.getLprPercent()).orElse(0) + Optional.ofNullable(rsp.getLprAddPercent()).orElse(0);
        // 计算日利率
        BigDecimal dailyInterestRate = BigDecimal.valueOf(lprRate).divide(BigDecimal.valueOf(10000 * 100 * 360), 20, RoundingMode.HALF_UP);
        // 使用剩余本金法计算收入明细
        List<IncomeSharingCashFlowBO> result = FinancialUtil.calculateIncomeSharingByRP(contractReceipt.getReceiptStartDate(), cashFlowList, dailyInterestRate);
        return this.convert(contractBaseInfo, contractReceipt, result, batchSequence, dailyInterestRate);
    }

    private void doAddCalculateIncomeSharingByAIR(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt) {
        String batchSequence = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        List<ContractIncomeSharing> toInsertList = this.listCalculateIncomeSharingResultByAIR(contractBaseInfo, contractReceipt, batchSequence);
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                try {
                    saveBatch(toInsertList);
                } catch (Exception e) {
                    transactionStatus.setRollbackOnly();
                    log.error("实际利率法-新增收益分摊明细异常[receiptCode:{}]", contractReceipt.getReceiptCode(), e);
                }
            });
        }
    }

    private void doUpdateCalculateIncomeSharingByAIR(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt) {
        if (Objects.isNull(contractReceipt.getIncomeSharingFlag()) || Objects.equals(contractReceipt.getIncomeSharingFlag(), YesOrNoNumberEnum.YES.getCode())) {
            log.info("实际利率法-{}未被标记为需要重算收入明细，忽略不处理", contractReceipt.getReceiptCode());
            return;
        }
//        LocalDate changeDate = contractReceipt.getChangeDate();
        LocalDate changeDate = this.ensureChangeDate(contractReceipt);
        if (Objects.isNull(changeDate)) {
            throw new MithrasException("实际利率法-变更日期非法");
        }
        String batchSequence = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        List<ContractIncomeSharing> oldList = this.listByReceiptId(contractReceipt.getId());
        List<ContractIncomeSharing> newList = this.listCalculateIncomeSharingResultByAIR(contractBaseInfo, contractReceipt, batchSequence);
        if (CollectionUtil.isEmpty(oldList) || CollectionUtil.isEmpty(newList)) {
            log.error("实际利率法-执行收入分摊变更逻辑存在数据缺失[contractId:{}, receiptId:{}]", contractBaseInfo.getId(), contractReceipt.getId());
            throw new MithrasException("实际利率法-执行收入分摊变更逻辑存在数据缺失");
        }
        LinkedList<ContractIncomeSharing> toSaveList = new LinkedList<>();
        // 计算截至到变更日期前的老的收入合计
        long oldTotalIncomeBeforeChange = 0L;
        long oldTotalIncomeWithoutTaxBeforeChange = 0L;
        Iterator<ContractIncomeSharing> oldIter = oldList.iterator();
        while (oldIter.hasNext()) {
            ContractIncomeSharing item = oldIter.next();
            if (item.getIncomeDate().isBefore(changeDate)) {
                oldTotalIncomeBeforeChange += Optional.ofNullable(item.getIncome()).orElse(0L);
                oldTotalIncomeWithoutTaxBeforeChange += Optional.ofNullable(item.getIncomeWithoutTax()).orElse(0L);
                item.setId(null);
                item.setBatchSequence(batchSequence);
                item.setCreateBy(null);
                item.setUpdateBy(null);
                item.setCreateTime(null);
                item.setUpdateTime(null);
                toSaveList.add(item);
            }
            oldIter.remove();
        }
        // 计算截至到变更日期前的新的收入合计
        long newTotalIncomeBeforeChange = 0L;
        long newTotalIncomeWithoutTaxBeforeChange = 0L;
        Iterator<ContractIncomeSharing> newIter = newList.iterator();
        while (newIter.hasNext()) {
            ContractIncomeSharing item = newIter.next();
            if (item.getIncomeDate().isBefore(changeDate)) {
                newTotalIncomeBeforeChange += Optional.ofNullable(item.getIncome()).orElse(0L);
                newTotalIncomeWithoutTaxBeforeChange += Optional.ofNullable(item.getIncomeWithoutTax()).orElse(0L);
            } else {
                toSaveList.add(item);
            }
            newIter.remove();
        }
        // 新老数据相减结果作为一条新数据待插入
        long a = newTotalIncomeBeforeChange - oldTotalIncomeBeforeChange;
        if (a != 0) {
            ContractIncomeSharing newOne = new ContractIncomeSharing();
            newOne.setContractId(contractBaseInfo.getId());
            newOne.setReceiptId(contractReceipt.getId());
            newOne.setBatchSequence(batchSequence);
            newOne.setIncomeDate(changeDate);
            newOne.setIncomePhase(-1);
            newOne.setIncome(a);
            newOne.setIncomeWithoutTax(newTotalIncomeWithoutTaxBeforeChange - oldTotalIncomeWithoutTaxBeforeChange);
            newOne.setTax(newOne.getIncome() - newOne.getIncomeWithoutTax());
            newOne.setBeginOfTermBalance(0L);
            newOne.setEndOfTermBalance(0L);
            toSaveList.add(newOne);
        }
        // 排序
        toSaveList.sort(Comparator.comparing(ContractIncomeSharing::getIncomeDate));
        // 更新数据
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 删除老的，保存新的
                deleteByReceiptId(contractReceipt.getId());
                if (CollectionUtil.isNotEmpty(toSaveList)) {
                    saveBatch(toSaveList);
                }
                // 重置标识位
                contractReceipt.setIncomeSharingFlag(YesOrNoNumberEnum.YES.getCode());
                contractReceiptService.updateById(contractReceipt);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("实际利率法-变更收益分摊明细异常[receiptCode:{}]", contractReceipt.getReceiptCode(), e);
            }
        });
        // 发通知尝试更新月结管理数据
        ThreadPoolUtil.getCommonPool().execute(() -> {
            try {
                MonthlyManageUpdateEvent event = new MonthlyManageUpdateEvent("实际利率法-变更", new MonthlyManageUpdateEvent.DataObject(MonthlyModuleTypeEnum.AIR.name(), contractReceipt.getId(), null));
                applicationEventPublisher.publishEvent(event);
            } catch (Exception e) {
                log.error("实际利率法-变更通知发送异常[receiptCode:{}]", contractReceipt.getReceiptCode(), e);
            }
        });

    }

    private List<ContractIncomeSharing> listCalculateIncomeSharingResultByAIR(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt, String batchSequence) {
        // 查询付款信息
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return null;
        }
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, e -> e));
        Set<Long> paymentIds = paymentBaseInfoMap.keySet();
        // 查询付款实际核销
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentIds(paymentIds);
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return null;
        }
        // 暂存首次投放时间
        paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
        // 构建现金流
        List<CashFlowBO> cashFlowList = paymentActualDetailList.stream().map(e -> {
            CashFlowBO cashFlowBO = new CashFlowBO();
            cashFlowBO.setCashFlowPhase(0);
            cashFlowBO.setCashFlowDate(e.getPaidInDate());
            cashFlowBO.setCashFlowAmount(-1 * e.getPaidInAmount());
            return cashFlowBO;
        }).collect(Collectors.toCollection(LinkedList::new));
        // 添加第0期收款（只取首期租金(付款申请中选择包含)、客户保证金、手续费、首期利息）
        List<CashFlowBO> zeroCollectionCashFlow = new LinkedList<>();
        long downPayment = 0L;
        long earnest = 0L;
        long commission = 0L;
        long firstInterest = 0L;
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listByPaymentIds(paymentIds);
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            Map<Long, CollectionBaseInfo> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e));
            // 查询实际核销记录
            List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByCollectionIds(collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(collectionRecordInfoList)) {
                for (CollectionRecordInfo e : collectionRecordInfoList) {
                    boolean need = false;
                    CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMap.get(e.getCollectionId());
                    if (Objects.equals(CashFlowItemEnum.COMMISSION.name(), collectionBaseInfo.getCashFlowItem())) {
                        commission = commission + Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                        need = true;
                    }
                    if (Objects.equals(CashFlowItemEnum.RENT.name(), collectionBaseInfo.getCashFlowItem()) && Objects.equals(collectionBaseInfo.getPhase(), 0)) {
                        firstInterest = firstInterest + Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                        need = true;
                    }
                    if (Objects.equals(CashFlowItemEnum.EARNEST_MONEY.name(), collectionBaseInfo.getCashFlowItem())) {
                        earnest = earnest + Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                        need = true;
                    }
                    if (Objects.equals(CashFlowItemEnum.FIRST_RENT.name(), collectionBaseInfo.getCashFlowItem())) {
                        // 首期租金需要判断是否包含
                        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMap.get(collectionBaseInfo.getPaymentId());
                        if (Objects.nonNull(paymentBaseInfo) && Objects.equals(paymentBaseInfo.getDownPaymentType(), YesOrNoNumberEnum.YES.getCode())) {
                            downPayment = downPayment + (-1) * Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                            need = true;
                        }
                    }
                    if (need) {
                        CashFlowBO cashFlowBO = new CashFlowBO();
                        cashFlowBO.setCashFlowPhase(0);
                        cashFlowBO.setCashFlowDate(e.getCollectionDate());
                        cashFlowBO.setCashFlowAmount(e.getCollectionAmount());
                        zeroCollectionCashFlow.add(cashFlowBO);
                    }
                }
                cashFlowList.addAll(zeroCollectionCashFlow);
            }
        }
        // 添加实际租金表
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipt(contractReceipt.getId());
        if (CollectionUtil.isNotEmpty(contractRentActualList)) {
            // 去掉实际起租日之前的（正常情况就是租前息）
            contractRentActualList.removeIf(e -> e.getCashFlowDate().isBefore(contractBaseInfo.getActualLeaseDate()));
            List<CashFlowBO> rentList = contractRentActualList.stream().map(e -> {
                CashFlowBO cashFlowBO = new CashFlowBO();
                cashFlowBO.setCashFlowPhase(e.getCashFlowPhase());
                cashFlowBO.setCashFlowDate(e.getCashFlowDate());
                cashFlowBO.setCashFlowAmount(Optional.ofNullable(e.getRent()).orElse(0L));
                cashFlowBO.setRent(Optional.ofNullable(e.getRent()).orElse(0L));
                cashFlowBO.setPrincipal(Optional.ofNullable(e.getPrincipal()).orElse(0L));
                cashFlowBO.setInterest(Optional.ofNullable(e.getInterest()).orElse(0L));
                return cashFlowBO;
            }).collect(Collectors.toList());
            cashFlowList.addAll(rentList);
        }
        cashFlowList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        // 最后一期减去保证金
        CashFlowBO lastPhase = cashFlowList.get(cashFlowList.size() - 1);
        lastPhase.setCashFlowAmount(lastPhase.getCashFlowAmount() - earnest);
        // 计算日折现率
        DailyDiscountRateCalcResultBO dailyDiscountRateCalcResultBO = FinancialUtil.calculateDailyDiscountRate(cashFlowList);
        log.info("{}计算日折现率相关金额: 首期租金 {}, 保证金 {}, 手续费 {}, 首期利息 {}", contractReceipt.getReceiptCode(), downPayment, earnest, commission, firstInterest);
        log.info("{}计算日折现率结果:{}", contractReceipt.getReceiptCode(), JSONUtil.toJsonStr(dailyDiscountRateCalcResultBO));
        // 计算收益分摊明细表
        List<CashFlowBO> zeroCashFlowList = new LinkedList<>();
        List<CashFlowBO> rentCashFlowList = new LinkedList<>();
        for (CashFlowBO cashFlowBO : cashFlowList) {
            if (Objects.equals(cashFlowBO.getCashFlowPhase(), 0)) {
                zeroCashFlowList.add(cashFlowBO);
            } else {
                rentCashFlowList.add(cashFlowBO);
            }
        }
        zeroCashFlowList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        rentCashFlowList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        List<IncomeSharingCashFlowBO> result = FinancialUtil.calculateIncomeSharingByAIR(zeroCashFlowList, rentCashFlowList, commission + firstInterest, dailyDiscountRateCalcResultBO.getDailyDiscountRate(), contractReceipt.getReceiptStartDate());
        // 返回结果
        return this.convert(contractBaseInfo, contractReceipt, result, batchSequence, dailyDiscountRateCalcResultBO.getDailyDiscountRate());
    }

    private LocalDate ensureChangeDate(ContractReceipt contractReceipt) {
        if (Objects.isNull(contractReceipt.getChangeDate())) {
            return null;
        }
        LambdaQueryWrapper<ContractIncomeSharing> queryLastConfirmed = Wrappers.lambdaQuery();
        queryLastConfirmed.eq(ContractIncomeSharing::getContractId, contractReceipt.getContractId());
        queryLastConfirmed.eq(ContractIncomeSharing::getReceiptId, contractReceipt.getId());
        queryLastConfirmed.eq(ContractIncomeSharing::getIsConfirmed, YesOrNoNumberEnum.YES.getCode());
        queryLastConfirmed.orderByDesc(ContractIncomeSharing::getIncomeDate);
        queryLastConfirmed.last(StringUtil.mysqlLimitOne());
        ContractIncomeSharing lastConfirmedOne = this.getOne(queryLastConfirmed);
        if (Objects.isNull(lastConfirmedOne)) {
            return contractReceipt.getChangeDate();
        }
        // 如果有已确认的数据，获取未确认的第一条数据日期（不直接取是防止可能有中间数据未确认，所以只能取最新的确认后再取一次未确认）
        LambdaQueryWrapper<ContractIncomeSharing> queryFirstUnconfirmed = Wrappers.lambdaQuery();
        queryFirstUnconfirmed.eq(ContractIncomeSharing::getContractId, contractReceipt.getContractId());
        queryFirstUnconfirmed.eq(ContractIncomeSharing::getReceiptId, contractReceipt.getId());
        queryFirstUnconfirmed.eq(ContractIncomeSharing::getIsConfirmed, YesOrNoNumberEnum.NO.getCode());
        queryFirstUnconfirmed.gt(ContractIncomeSharing::getIncomeDate, lastConfirmedOne.getIncomeDate());
        queryFirstUnconfirmed.orderByAsc(ContractIncomeSharing::getIncomeDate);
        queryFirstUnconfirmed.last(StringUtil.mysqlLimitOne());
        ContractIncomeSharing firstUnconfirmedOne = this.getOne(queryFirstUnconfirmed);
        if (Objects.isNull(firstUnconfirmedOne)) {
            return null;
        }
        if (contractReceipt.getChangeDate().isBefore(firstUnconfirmedOne.getIncomeDate())) {
            return firstUnconfirmedOne.getIncomeDate();
        } else {
            return contractReceipt.getChangeDate();
        }
    }

    private List<ContractIncomeSharing> convert(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt, List<IncomeSharingCashFlowBO> sourceList, String batchSequence, BigDecimal dailyDiscountRate) {
        FtpAssessmentInfo ftpAssessmentInfo = ftpAssessmentInfoService.findLatestEffect(LocalDate.now(), contractReceipt.getId());
        return sourceList.stream().map(e -> {
            ContractIncomeSharing contractIncomeSharing = new ContractIncomeSharing();
            contractIncomeSharing.setContractId(contractBaseInfo.getId());
            contractIncomeSharing.setReceiptId(contractReceipt.getId());
            contractIncomeSharing.setBatchSequence(batchSequence);
            contractIncomeSharing.setIncomeDate(e.getCashFlowDate());
            contractIncomeSharing.setIncomePhase(e.getCashFlowPhase());
            contractIncomeSharing.setBeginOfTermBalance(e.getBeginOfTermBalance());
            contractIncomeSharing.setRent(Optional.ofNullable(e.getRent()).orElse(0L));
            contractIncomeSharing.setIncome(e.getIncome());
            // 计算不含税金额 = 含税金额 / (1 + 税率)
            long incomeWithoutTax;
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                incomeWithoutTax = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(e.getIncome()).divide(BigDecimal.ONE.add(GlobalConstants.TAX_RATE_ZHI_ZU), 0, RoundingMode.HALF_UP).longValue());
            } else {
                incomeWithoutTax = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(e.getIncome()).divide(BigDecimal.ONE.add(GlobalConstants.TAX_RATE_FEI_ZHI_ZU), 0, RoundingMode.HALF_UP).longValue());
            }
            contractIncomeSharing.setIncomeWithoutTax(incomeWithoutTax);
            contractIncomeSharing.setTax(contractIncomeSharing.getIncome() - contractIncomeSharing.getIncomeWithoutTax());
            contractIncomeSharing.setEndOfTermBalance(e.getEndOfTermBalance());
            contractIncomeSharing.setDailyDiscountRate(dailyDiscountRate.toPlainString());
            // 若该合同“是否为特殊事项=是”，则该合同不进行收入分摊，即分摊金额均设置为“0”
            if(Objects.nonNull(ftpAssessmentInfo) && Objects.equals(ftpAssessmentInfo.getIsSpecialMatter(), YesOrNoNumberEnum.YES.getCode())){
                contractIncomeSharing.setBeginOfTermBalance(0L);
                contractIncomeSharing.setRent(0L);
                contractIncomeSharing.setIncome(0L);
                contractIncomeSharing.setIncomeWithoutTax(0L);
                contractIncomeSharing.setTax(0L);
                contractIncomeSharing.setEndOfTermBalance(0L);
                contractIncomeSharing.setDailyDiscountRate("0");
            }
            return contractIncomeSharing;
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    public PageR<IncomeSharingListRSP> incomeSharingQuery(IncomeSharingListREQ req) {
        //先处理一下查询时间条件
        LocalDate localDate = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), DatePattern.NORM_MONTH_PATTERN);
        LocalDate queryStartDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
        LocalDate queryEndDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
        //再查询出来需要的数据
        // 分摊表数据以借据为维度，取合同状态为起租的10条数据
        ReceiptConditionQuery query = BeanUtil.copyProperties(req, ReceiptConditionQuery.class);
        Page<ReceiptConditionResult> pageResult = contractReceiptMapper.queryReceiptCondition(new Page<>(req.getPage(),req.getPageSize()),query);
        List<ReceiptConditionResult> records = pageResult.getRecords();
        if(CollectionUtil.isEmpty(records)){
            return PageR.of(new ArrayList<>(), pageResult.getTotal(), req.getPage(), req.getPageSize());
        }
        List<Long> receiptIdList = records.stream().map(ReceiptConditionResult::getReceiptId).collect(Collectors.toList());
        List<ContractIncomeSharing> contractIncomeSharingByIdList = this.list(Wrappers.<ContractIncomeSharing>lambdaQuery().in(ContractIncomeSharing::getReceiptId,receiptIdList));

        /*
            以借据维度查询，故而借据编号只有一个，借据是挂在合同下的，所以借据id相同的分摊表数据合同id也一定相同。
            合同又是与客户一对一绑定。所以客户名称也是唯一的。所以通过合同id、借据id可得到：合同详情，借据详情。
            合同详情包含：合同编号，项目名称，合同状态,收入分摊方式,客户id 。通过客户id拿到客户的名称
            借据详情包含：借据编号
            其他字段从分摊表直接获取。
        */
        //根据借据id分组
        Map<Long, List<ContractIncomeSharing>> incomeMap = contractIncomeSharingByIdList.stream().collect(Collectors.groupingBy(ContractIncomeSharing::getReceiptId));
        //遍历得到的分摊表数据 （Map:key - 借据id , value - 借据维度的分摊表数据集合）
        List<IncomeSharingListRSP> incomeSharingListRSPList = records.stream()
                .map(result -> {
                    IncomeSharingListRSP rsp = BeanUtil.copyProperties(result, IncomeSharingListRSP.class);
                    if (CharSequenceUtil.equalsAny(result.getRiskControlIndustryClassify(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(),
                            RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.TRAVEL.name())) {
                        rsp.setBusinessType("公用类");
                    }else {
                        rsp.setBusinessType("产业类");
                    }

                    //以借据为维度的收入分摊数据处理
                    List<ContractIncomeSharing> contractIncomeSharingList = incomeMap.get(result.getReceiptId());
                    if (CollectionUtils.isNotEmpty(contractIncomeSharingList)) {
                        //含税收入合计：取该借据收入分摊表里含税收入的汇总
                        long incomeSum = contractIncomeSharingList.stream().mapToLong(ContractIncomeSharing::getIncome).sum();
                        //不含税收入合计：取该借据收入分摊表里不含税收入的汇总
                        long incomeWithoutTaxSum = contractIncomeSharingList.stream().mapToLong(ContractIncomeSharing::getIncomeWithoutTax).sum();
                        //已确认收入合计：收入分摊表中“收入确认标记”为已确认的收入数据合计
                        long confirmedIncomeSum = contractIncomeSharingList.stream().filter(item -> item.getIsConfirmed() == 1).mapToLong(ContractIncomeSharing::getIncome).sum();
                        //未确认收入合计：收入分摊表中“收入确认标记”为未确认的收入数据合计
                        long unconfirmedIncomeSum = contractIncomeSharingList.stream().filter(item -> item.getIsConfirmed() == 0).mapToLong(ContractIncomeSharing::getIncome).sum();
                        //月度含税收入合计：取该借据该月份分摊的每天含税收入金额的合计数
                        long monthlyIncomeSum = contractIncomeSharingList.stream()
                                .filter(item -> Objects.nonNull(item.getIncomeDate())
                                        && !item.getIncomeDate().isBefore(queryStartDate)
                                        && !item.getIncomeDate().isAfter(queryEndDate))
                                .mapToLong(ContractIncomeSharing::getIncome).sum();
                        //月度不含税收入合计：取该借据该月份分摊的每天不含税收入金额的合计数
                        long monthlyIncomeWithoutTaxSum = contractIncomeSharingList.stream()
                                .filter(item -> Objects.nonNull(item.getIncomeDate())
                                        && !item.getIncomeDate().isBefore(queryStartDate)
                                        && !item.getIncomeDate().isAfter(queryEndDate))
                                .mapToLong(ContractIncomeSharing::getIncomeWithoutTax).sum();
                        //月度已确认不含税收入合计：取该借据该月份已确认的不含税收入金额的合计数
                        long monthlyConfirmedIncomeWithoutTaxSum = contractIncomeSharingList.stream()
                                .filter(item -> item.getIsConfirmed() == 1
                                        && Objects.nonNull(item.getConfirmTime())
                                        && !item.getIncomeDate().isBefore(queryStartDate)
                                        && !item.getIncomeDate().isAfter(queryEndDate))
                                .mapToLong(ContractIncomeSharing::getIncomeWithoutTax).sum();

                        rsp.setIncomeSum(incomeSum);
                        rsp.setIncomeWithoutTaxSum(incomeWithoutTaxSum);
                        rsp.setConfirmedIncomeSum(confirmedIncomeSum);
                        rsp.setUnconfirmedIncomeSum(unconfirmedIncomeSum);
                        rsp.setMonthlyIncomeSum(monthlyIncomeSum);
                        rsp.setMonthlyIncomeWithoutTaxSum(monthlyIncomeWithoutTaxSum);
                        rsp.setMonthlyConfirmedIncomeWithoutTaxSum(monthlyConfirmedIncomeWithoutTaxSum);
                    }
                    rsp.setBelongDeptId(result.getBelongDeptId());
                    rsp.setBelongDeptName(result.getBelongDeptName());
                    rsp.setSponsorUserId(result.getSponsorUserId());
                    rsp.setSponsorUserName(result.getSponsorUserName());
                    return rsp;
                }).collect(Collectors.toList());

        List<IncomeSharingListRSP> rspList;
        //最后一个筛选条件：逾期或不逾期
        if (StringUtils.isNotBlank(req.getOverdueType())) {
            if (OverdueTypeEnum.NOT_OVERDUE.name().equals(req.getOverdueType())) {
                rspList = incomeSharingListRSPList.stream().filter(item -> OverdueTypeEnum.NOT_OVERDUE.name().equals(item.getOverdueType())).collect(Collectors.toList());
            } else {
                rspList = incomeSharingListRSPList.stream().filter(item -> OverdueTypeEnum.OVERDUE.name().equals(item.getOverdueType())).collect(Collectors.toList());
            }
        } else {
            rspList = incomeSharingListRSPList;
        }

        return PageR.of(rspList, pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    public R<PageR<IncomeSharingRSP>> incomeSharingDetail(IncomeSharingDetailREQ req) {
        //分页
        PageHelper.startPage(req.getPage(), req.getPageSize());
        //根据借据id查询收入分摊表
        if (StringUtils.isNotBlank(req.getYearAndMonth())) {
            String[] split = req.getYearAndMonth().split("-");
            LocalDate date = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1].replace("0", "")), 1);
            req.setIncomeDateFrom(date.with(TemporalAdjusters.firstDayOfMonth()));
            req.setIncomeDateTo(date.with(TemporalAdjusters.lastDayOfMonth()));
        }
        List<IncomeSharingRSP> incomeSharingList = contractIncomeSharingMapper.queryDetail(req);
        //为空返回
        if (CollectionUtils.isEmpty(incomeSharingList)) {
            return R.ok();
        }
        PageInfo<IncomeSharingRSP> pageInfo = new PageInfo<>(incomeSharingList);

        return R.ok(PageR.of(incomeSharingList, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPages()));
    }

    public void exportIncomeSharingList(IncomeSharingListREQ req, ServletOutputStream outputStream) {
        //查询出来需要的数据
        List<IncomeSharingListRSP> rspList = this.incomeSharingQuery(req).getList();
        if (CollectionUtils.isEmpty(rspList)) {
            throw new MithrasException("数据不存在");
        }

        //封装数据
        List<ContractIncomeSharingExcelModel2> excelModelList = rspList.stream().map(item -> {
            ContractIncomeSharingExcelModel2 incomeSharingExcelModel = new ContractIncomeSharingExcelModel2();
            BeanUtil.copyProperties(item, incomeSharingExcelModel);

            ContractStatus contractStatus = ContractStatus.find(item.getContractStatus());
            if (Objects.nonNull(contractStatus)) {
                incomeSharingExcelModel.setContractStatus(contractStatus.display());
            }
            IncomeConfirmTypeEnum confirmTypeEnum = IncomeConfirmTypeEnum.find(item.getIncomeConfirmType());
            if (Objects.nonNull(confirmTypeEnum)) {
                incomeSharingExcelModel.setIncomeConfirmType(confirmTypeEnum.getDisplay());
            }
            OverdueTypeEnum overdueTypeEnum = OverdueTypeEnum.find(item.getOverdueType());
            if (Objects.nonNull(overdueTypeEnum)) {
                incomeSharingExcelModel.setOverdueType(overdueTypeEnum.getDisplay());
            }
            incomeSharingExcelModel.setStartRentTime(item.getStartRentTime());
            incomeSharingExcelModel.setOverdueStartTime(item.getOverdueStartTime());
            incomeSharingExcelModel.setIncomeSum(Util.toYuan(item.getIncomeSum()));
            incomeSharingExcelModel.setIncomeWithoutTaxSum(Util.toYuan(item.getIncomeWithoutTaxSum()));
            incomeSharingExcelModel.setConfirmedIncomeSum(Util.toYuan(item.getConfirmedIncomeSum()));
            incomeSharingExcelModel.setUnconfirmedIncomeSum(Util.toYuan(item.getUnconfirmedIncomeSum()));
            incomeSharingExcelModel.setMonthlyIncomeSum(Util.toYuan(item.getMonthlyIncomeSum()));
            incomeSharingExcelModel.setMonthlyIncomeWithoutTaxSum(Util.toYuan(item.getMonthlyIncomeWithoutTaxSum()));
            incomeSharingExcelModel.setMonthlyConfirmedIncomeWithoutTaxSum(Util.toYuan(item.getMonthlyConfirmedIncomeWithoutTaxSum()));
            incomeSharingExcelModel.setSponsorUserName(item.getSponsorUserName());
            incomeSharingExcelModel.setBelongDeptName(item.getBelongDeptName());
            incomeSharingExcelModel.setBusinessType(item.getBusinessType());
            return incomeSharingExcelModel;
        }).collect(Collectors.toList());

        contractIncomeSharingExcelManagerExporter2.exportExcel(excelModelList, outputStream);
    }

    public void exportIncomeSharingDetail(IncomeSharingDetailREQ req, ServletOutputStream outputStream) {
        //先将数据查出来
        if (StringUtils.isNotBlank(req.getYearAndMonth())) {
            String[] split = req.getYearAndMonth().split("-");
            LocalDate date = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1].replace("0", "")), 1);
            req.setIncomeDateFrom(date.with(TemporalAdjusters.firstDayOfMonth()));
            req.setIncomeDateTo(date.with(TemporalAdjusters.lastDayOfMonth()));
        }
        List<ContractIncomeSharing> incomeSharingList = this.list(Wrappers.<ContractIncomeSharing>lambdaQuery()
                .eq(ContractIncomeSharing::getReceiptId, req.getReceiptId())
                .between(CharSequenceUtil.isNotBlank(req.getYearAndMonth()), ContractIncomeSharing::getIncomeDate, req.getIncomeDateFrom(), req.getIncomeDateTo())
                .eq(ContractIncomeSharing::getDeleted, 0));
        //为空抛出异常
        if (CollectionUtils.isEmpty(incomeSharingList)) {
            throw new MithrasException("不存在数据，导出失败");
        }

        //合同编号，借据编号
        ContractReceipt contractReceipt = contractReceiptService.getOne(Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getId, req.getReceiptId()));
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getOne(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getId, contractReceipt.getContractId()));
        List<ContractIncomeSharingExcelModel> excelModelList = incomeSharingList.stream()
                .map(item -> {
                    ContractIncomeSharingExcelModel excelModel = new ContractIncomeSharingExcelModel();
                    excelModel.setIncomeDate(item.getIncomeDate().toString());
                    excelModel.setDailyDiscountRate(item.getDailyDiscountRate());
                    excelModel.setContractCode(contractBaseInfo.getContractCode());
                    excelModel.setReceiptCode(contractReceipt.getReceiptCode());
                    excelModel.setBeginOfTermBalance(Util.toYuan(item.getBeginOfTermBalance()));
                    excelModel.setRent(Util.toYuan(item.getRent()));
                    excelModel.setIncome(Util.toYuan(item.getIncome()));
                    excelModel.setIncomeWithoutTax(Util.toYuan(item.getIncomeWithoutTax()));
                    excelModel.setTax(Util.toYuan(item.getTax()));
                    excelModel.setEndOfTermBalance(Util.toYuan(item.getEndOfTermBalance()));
                    if (Objects.nonNull(item.getIncomePhase())) {
                        excelModel.setIsConfirmed(item.getIsConfirmed() == 1 ? "是" : "否");
                    }
                    return excelModel;
                }).collect(Collectors.toList());

        contractIncomeSharingExcelManagerExporter.exportExcel(excelModelList, outputStream);
    }

    /**
     * 获取借据的应计利息 分批次计算
     * @param receiptIdList
     * @return
     */
    public Map<Long, Long> getAccruedInterestByBatch(Collection<Long> receiptIdList, LocalDate lastDate){
        Map<Long, Long> result = new HashMap<>();
        // 空集合校验
        if (receiptIdList == null || receiptIdList.isEmpty()) {
            return result;
        }

        // 转换为List以便分片
        List<Long> list = new ArrayList<>(receiptIdList);
        int batchSize = 30; // 每批处理量
        int totalSize = list.size();

        StopWatch st = new StopWatch("getAccruedInterestByBatch");
        // 分批处理
        for (int i = 0; i < totalSize; i += batchSize) {
            st.start(String.valueOf(i));
            int end = Math.min(i + batchSize, totalSize);
            Collection<Long> batch = list.subList(i, end);
            result.putAll(getAccruedInterest(batch, lastDate));
            st.stop();
        }
        log.info("getAccruedInterestByBatch {}", st.prettyPrint(TimeUnit.SECONDS));
        return result;
    }

    /**
     * 获取借据的应计利息
     * @param receiptIdList
     * @return
     */
    public Map<Long, Long> getAccruedInterest(Collection<Long> receiptIdList, LocalDate lastDate){
        Map<Long, Long> result = Collections.emptyMap();
        LocalDate startOfDay = lastDate.with(TemporalAdjusters.firstDayOfMonth());
        if(CollectionUtil.isEmpty(receiptIdList)){
            return result;
        }
        List<ContractIncomeSharing> incomeSharingList = list(Wrappers.<ContractIncomeSharing>lambdaQuery().in(ContractIncomeSharing::getReceiptId, receiptIdList));
        if(CollectionUtil.isEmpty(incomeSharingList)){
            return result;
        }
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByIds(receiptIdList);
        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoService.listByIds(contractReceiptList.stream().map(ContractReceipt::getContractId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity()));
        // 借据维度的收入确认方式
        Map<Long, String> receiptIncomeTypeMap = contractReceiptList.stream().collect(Collectors.toMap(ContractReceipt::getId, receipt -> {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(receipt.getContractId());
            return Optional.ofNullable(contractBaseInfo).map(ContractBaseInfo::getIncomeConfirmType).orElse("");
        }));
        // 实际核销
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractBaseInfoMap.keySet())
                .in(CollectionBaseInfo::getCashFlowItem, Arrays.asList(CashFlowItemEnum.RENT.name(), CashFlowItemEnum.COMMISSION.name(), CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name())));
        //查找借据下对应付款
        List<Long> receiptIds = contractReceiptList.stream().map(ContractReceipt::getId).collect(Collectors.toList());
        Map<Long, List<PaymentBaseInfo>> receiptId2PaymentBaseInfoList = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getReceiptId, receiptIds))
                .stream().collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptId));
        Map<Long, Long> interestAmountMap = Collections.emptyMap();
        Map<Long, Long> commissionAmountMap = Collections.emptyMap();
        Map<Long, Long> firstAmountMap = new HashMap<>();
        LocalDate now = LocalDate.now();
        if(CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            Map<Long, CollectionBaseInfo> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.toMap(CollectionBaseInfo::getId, Function.identity()));
            Map<Long, List<CollectionRecordInfo>> recordInfoMap = collectionRecordInfoService.listByCollectionIdsAndPlanDate(collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()), lastDate)
                    .stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
            // 借据维度：已核销利息
            interestAmountMap = recordInfoMap.entrySet().stream().filter(f -> {
                return Objects.equals(Optional.ofNullable(collectionBaseInfoMap.get(f.getKey())).map(CollectionBaseInfo::getCashFlowItem).orElse(null), CashFlowItemEnum.RENT.name()) &&
                        Objects.nonNull(collectionBaseInfoMap.get(f.getKey()).getReceiptId());
            }).collect(Collectors.groupingBy(entry -> collectionBaseInfoMap.get(entry.getKey()).getReceiptId(), Collectors.summingLong(entry -> {
                return entry.getValue().stream().mapToLong(m -> LongUtil.null2zero(m.getInterest())).sum();
            })));
            //未来的减去计划
            List<CollectionBaseInfo> forCollectionBaseInfos = collectionBaseInfoList.stream().filter(e -> ObjectUtil.equals(e.getWriteOffStatus(), CollectionWriteOffStatusEnum.UNCOLLECTION.name())).filter(e -> !e.getPlanCollectionDate().isBefore(now) && !e.getPlanCollectionDate().isAfter(lastDate)).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(forCollectionBaseInfos)) {
                for (CollectionBaseInfo e : forCollectionBaseInfos) {
                    interestAmountMap.put(e.getReceiptId(), LongUtil.null2zero(e.getInterest()) + interestAmountMap.getOrDefault(e.getReceiptId(), 0L));
                }
            }

            //付款维度：已核销首期利息
            Map<Long, Long> paymentId2Amount = recordInfoMap.entrySet().stream().filter(f -> {
                return Objects.equals(Optional.ofNullable(collectionBaseInfoMap.get(f.getKey())).map(CollectionBaseInfo::getCashFlowItem).orElse(null), CashFlowItemEnum.RENT.name()) &&
                        Objects.nonNull(collectionBaseInfoMap.get(f.getKey()).getPaymentId()) && ObjectUtil.equals(0, collectionBaseInfoMap.get(f.getKey()).getPhase());
            }).collect(Collectors.groupingBy(entry -> collectionBaseInfoMap.get(entry.getKey()).getPaymentId(), Collectors.summingLong(entry -> {
                return entry.getValue().stream().mapToLong(m -> LongUtil.null2zero(m.getCollectionAmount())).sum();
            })));
            receiptIds.forEach(id -> {
                List<PaymentBaseInfo> paymentBaseInfos = receiptId2PaymentBaseInfoList.get(id);
                if (ObjectUtil.isNotEmpty(paymentBaseInfos)){
                    Long sum = 0L;
                    for (PaymentBaseInfo e : paymentBaseInfos) {
                        sum = sum + LongUtil.null2zero(paymentId2Amount.get(e.getId()));
                    }
                    firstAmountMap.put(id, sum);
                }
            });

            // 合同维度：已核销手续费
            commissionAmountMap = recordInfoMap.entrySet().stream().filter(f -> {
                return ObjectUtil.notEqual(Optional.ofNullable(collectionBaseInfoMap.get(f.getKey())).map(CollectionBaseInfo::getCashFlowItem).orElse(null), CashFlowItemEnum.RENT.name());
            }).collect(Collectors.groupingBy(entry -> collectionBaseInfoMap.get(entry.getKey()).getContractId(), Collectors.summingLong(entry -> {
                return entry.getValue().stream().mapToLong(m -> LongUtil.null2zero(m.getCollectionAmount())).sum();
            })));

        }

        Map<Long, List<ContractIncomeSharing>> incomeSharingMap = incomeSharingList.stream().collect(Collectors.groupingBy(ContractIncomeSharing::getReceiptId));
        Map<Long, Long> finalInterestAmountMap = interestAmountMap;
        Map<Long, Long> finalCommissionAmountMap = commissionAmountMap;
        //取税率
        Map<Long, Long> receiptId2Tax = contractReceiptList.stream().filter(e -> ObjectUtil.isNotEmpty(e.getTaxRate())).collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getTaxRate, (a, b) -> b));

        result = incomeSharingMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            // 剩余本金法，则取拨备计提创建月份当月的“月度不含税收入合计”（收入分摊表）
            if (Objects.equals(receiptIncomeTypeMap.get(entry.getKey()), IncomeConfirmTypeEnum.RP.name())) {
                return entry.getValue().stream().filter(f -> !f.getIncomeDate().isAfter(lastDate) && !f.getIncomeDate().isBefore(startOfDay))
                        .mapToLong(m -> LongUtil.null2zero(m.getIncomeWithoutTax())).sum();
            }
            // 实际利率法，则取截至到拨备计提创建月份当月“不含税收入合计”（收入分摊表）-已核销利息-已核销手续费
            if (Objects.equals(receiptIncomeTypeMap.get(entry.getKey()), IncomeConfirmTypeEnum.AIR.name())) {
                long sum = entry.getValue().stream().filter(f -> !f.getIncomeDate().isAfter(lastDate)).mapToLong(m -> LongUtil.null2zero(m.getIncomeWithoutTax())).sum();
                Long interest = calculateTax(finalInterestAmountMap.getOrDefault(entry.getKey(), 0L), receiptId2Tax.get(entry.getKey()));
                Long commission = calculateTax(finalCommissionAmountMap.getOrDefault(entry.getValue().get(0).getContractId(), 0L), receiptId2Tax.get(entry.getKey()));
                Long firstAmount = calculateTax(firstAmountMap.getOrDefault(entry.getKey(), 0L), receiptId2Tax.get(entry.getKey()));
                log.info("ContractIncomeSharingService getAccruedInterest receipt = {}  获取应付利息 sum = {}, interest = {}, commission= {}, firstAmount = {}", entry.getKey(), sum, interest, commission, firstAmount);
                return Math.max(sum - interest - commission - LongUtil.null2zero(firstAmount), 0L);
            }
            return 0L;
        }));

        return result;

    }

    private Long calculateTax(Long a, Long tax) {
        if (ObjectUtil.isEmpty(a) || ObjectUtil.isEmpty(tax)) {
            return a;
        }
        BigDecimal taxBigDecimal = LongUtil.tenThousand2Dollar(tax).divide(new BigDecimal("100"), 10, BigDecimal.ROUND_UP).add(new BigDecimal(1));
        return new BigDecimal(a).divide(taxBigDecimal, 4, BigDecimal.ROUND_UP).longValue();
    }
}
