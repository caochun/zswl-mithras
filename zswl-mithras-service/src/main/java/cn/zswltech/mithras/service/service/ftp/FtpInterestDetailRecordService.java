package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpInterestDetailReq;
import cn.zswltech.mithras.dto.ftp.FtpInterestDetailRsp;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.ftp.FtpInterestConvert;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.BillTypeEnum;
import cn.zswltech.mithras.collection.enums.CollectionRecordWriteOffStatus;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpInterestDetailRecordMapper;
import cn.zswltech.mithras.collection.mapper.model.BillManagement;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.collection.BillManagementService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description
 */
@Slf4j
@Service
public class FtpInterestDetailRecordService extends ServiceImpl<FtpInterestDetailRecordMapper, FtpInterestDetailRecord> {
    private static final int CASH = 1;
    private static final int BILL = 2;
    private static final String BEGINNING_ITEM_TEXT = "期初余额";

    @Resource
    private FtpInterestConvert ftpInterestConvert;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private BillManagementService billManagementService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private FundFinancingPledgeInfoService financingPledgeInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService directFinancingPledgeInfoService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;

    public List<FtpInterestDetailRecord> listSpecificByRange(Long ftpInterestId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<FtpInterestDetailRecord> query = Wrappers.lambdaQuery();
        query.eq(FtpInterestDetailRecord::getFtpInterestId, ftpInterestId);
        query.ge(FtpInterestDetailRecord::getInterestDate, startDate);
        query.le(FtpInterestDetailRecord::getInterestDate, endDate);
        return this.list(query);
    }

    public int countByFtpInterestId(Long ftpInterestId) {
        LambdaQueryWrapper<FtpInterestDetailRecord> query = Wrappers.lambdaQuery();
        query.eq(FtpInterestDetailRecord::getFtpInterestId, ftpInterestId);
        return this.count(query);
    }

    public List<FtpInterestDetailRecord> listEndOfMonthByContractYearMonth(int year, int month, Long contractId) {
        List<FtpInterestBaseInfo> ftpInterestBaseInfoList = ftpInterestBaseInfoService.listByContractId(contractId);
        if (CollectionUtil.isEmpty(ftpInterestBaseInfoList)) {
            return Collections.emptyList();
        }
        LocalDate localDate = LocalDate.of(year, month, 1);
        LambdaQueryWrapper<FtpInterestDetailRecord> query = Wrappers.lambdaQuery();
        query.in(FtpInterestDetailRecord::getFtpInterestId, ftpInterestBaseInfoList.stream().map(FtpInterestBaseInfo::getId).collect(Collectors.toSet()));
        query.eq(FtpInterestDetailRecord::getInterestDate, LocalDate.of(year, month, localDate.lengthOfMonth()));
        return this.list(query);
    }

    public PageR<FtpInterestDetailRsp> listDetailRecordWithPage(FtpInterestDetailReq req) {
        Page<FtpInterestDetailRecord> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<FtpInterestDetailRecord> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.eq(FtpInterestDetailRecord::getFtpInterestId, req.getFtpInterestId());
        if (StrUtil.isNotBlank(req.getInterestDateFrom())) {
            conditionQuery.ge(FtpInterestDetailRecord::getInterestDate, LocalDateTimeUtil.parse(req.getInterestDateFrom(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getInterestDateTo())) {
            conditionQuery.le(FtpInterestDetailRecord::getInterestDate, LocalDateTimeUtil.parse(req.getInterestDateTo(), DatePattern.NORM_DATE_PATTERN));
        }
        conditionQuery.orderByAsc(FtpInterestDetailRecord::getInterestDate);
        Page<FtpInterestDetailRecord> dbResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<FtpInterestDetailRsp> list = new ArrayList<>(dbResult.getRecords().size());
        for (FtpInterestDetailRecord record : dbResult.getRecords()) {
            FtpInterestDetailRsp rsp = ftpInterestConvert.entityToFtpInterestDetailRsp(record);
            if (Objects.nonNull(record.getCashFtp())) {
                rsp.setCashFtp(BigDecimal.valueOf(record.getCashFtp() / 10000.0).setScale(2, RoundingMode.HALF_UP).toPlainString());
                // 计算日FTP利率，展示的时候保留2位小数即可
                rsp.setCashFtpDay(FinancialUtil.calculateFtpPerDay(new BigDecimal(rsp.getCashFtp())).setScale(4, RoundingMode.HALF_UP).toPlainString());
            }
            if (Objects.nonNull(record.getBillFtp())) {
                rsp.setBillFtp(BigDecimal.valueOf(record.getBillFtp() / 10000.0).setScale(2, RoundingMode.HALF_UP).toPlainString());
                // 计算日FTP利率，展示的时候保留2位小数即可
                rsp.setBillFtpDay(FinancialUtil.calculateFtpPerDay(new BigDecimal(rsp.getBillFtp())).setScale(4, RoundingMode.HALF_UP).toPlainString());
            }
            list.add(rsp);
        }
        return PageR.of(list, dbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void doDiff(Long ftpInterestId, LocalDate diffDate, long cashDiffAmount, long billDiffAmount, String remark) {
        FtpInterestDetailRecord diffRecord = new FtpInterestDetailRecord();
        diffRecord.setFtpInterestId(ftpInterestId);
        diffRecord.setItemText(LocalDateTimeUtil.format(diffDate, DatePattern.NORM_DATE_PATTERN));
        diffRecord.setInterestDate(diffDate);
        diffRecord.setCashInterest(cashDiffAmount);
        diffRecord.setBillInterest(billDiffAmount);
        diffRecord.setRollingDifferenceMark(YesOrNoNumberEnum.YES.getCode());
        diffRecord.setRemark(remark);
        this.save(diffRecord);
        FtpInterestBaseInfo ftpInterestBaseInfo = ftpInterestBaseInfoService.getById(ftpInterestId);
        FtpInterestBaseInfo update = new FtpInterestBaseInfo();
        update.setId(ftpInterestId);
        update.setTotalInterestAmount(ftpInterestBaseInfo.getTotalInterestAmount() + cashDiffAmount + billDiffAmount);
        ftpInterestBaseInfoService.updateById(update);
    }

    public void calculateFtpInterest(FtpInterestBaseInfo ftpInterestBaseInfo, LocalDate interestDate) {
        // 计息日期是精确到单日的
        MDC.put(GlobalConstants.LOG_TRACE_ID, String.format("FTPInterest-%s-%s-%s", ftpInterestBaseInfo.getId(), LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN), System.currentTimeMillis()));
        try {
            log.info("FTP计息<<<<<<<<<<<<<<<<<<<<<开始[ftpInterestId:{}, interestDate:{}]", ftpInterestBaseInfo.getId(), LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN));
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                try {
                    doCalculate(ftpInterestBaseInfo, interestDate);
                } catch (Exception e) {
                    transactionStatus.setRollbackOnly();
                    log.error("FTP计息发生异常[ftpInterestId:{}, interestDate:{}]", ftpInterestBaseInfo.getId(), LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN), e);
                }
            });
        } finally {
            MDC.clear();
            log.info("FTP计息<<<<<<<<<<<<<<<<<<<<<结束[ftpInterestId:{}, interestDate:{}]", ftpInterestBaseInfo.getId(), LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN));
        }
    }

    private void doCalculate(FtpInterestBaseInfo ftpInterestBaseInfo, LocalDate interestDate) {
        Long ftpInterestId = ftpInterestBaseInfo.getId();
        Long receiptId = ftpInterestBaseInfo.getReceiptId();
        ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
        if (Objects.isNull(contractReceipt)) {
            log.error("借据信息不存在[receiptId:{}]", receiptId);
            throw new MithrasException("借据信息不存在");
        }
        FtpAssessmentInfo ftpAssessmentInfo = ftpAssessmentInfoService.findLatestEffect(interestDate, receiptId);
        if (Objects.isNull(ftpAssessmentInfo)) {
            log.error("FTP价格信息不存在[receiptId:{}, interestDate:{}]", receiptId, LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN));
            throw new MithrasException("FTP价格信息不存在");
        }
        /* ******************************************************************************************************************************** */
        /* ****************************************************** step-1 查询所需数据 ******************************************************* */
        /* ******************************************************************************************************************************** */
        // 当日的FTP计息数据
        FtpInterestDetailRecord existRecord = this.getSpecificRecord(ftpInterestId, interestDate);
        // 前一日的FTP计息数据
        FtpInterestDetailRecord previousRecord = this.getSpecificRecord(ftpInterestId, interestDate.minusDays(1));
        int year = interestDate.getYear();
//        // 如果是年初，且有前一日数据（说明计息期间跨年），需要插入一条期初数据
//        if (interestDate.isEqual(LocalDate.of(year, 1, 1)) && Objects.nonNull(previousRecord)) {
//            previousRecord = this.insertBeginningDetailRecord(ftpInterestId, contractReceipt, previousRecord, interestDate);
//        }
        // 最近一次FTP计息占用
        long previousCashOccupy = 0L;
        long previousBillOccupy = 0L;
        if (Objects.nonNull(previousRecord) && Objects.nonNull(previousRecord.getCashOccupy())) {
            previousCashOccupy = previousRecord.getCashOccupy();
        }
        if (Objects.nonNull(previousRecord) && Objects.nonNull(previousRecord.getBillOccupy())) {
            previousBillOccupy = previousRecord.getBillOccupy();
        }
        // 借据下的付款信息
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
        // 合同下的收款信息
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listBy(contractReceipt.getContractId(), null);
        /* ******************************************************************************************************************************** */
        /* ****************************************************** step-2 计算基础数据 ******************************************************* */
        /* ******************************************************************************************************************************** */
        // 先处理归属于整个合同的收款，目前是只计算在借据编号最小的借据上
        // 提前终止补偿金
        long prepayLoss = 0L;
        // 留购款
        long nominalPrice = 0L;
        if (this.isFirstReceipt(contractReceipt)) {
            Set<Long> prepayLossCollectionIds = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.EARLY_STOP_COMPENSATION.name())).map(CollectionBaseInfo::getId).collect(Collectors.toSet());
            prepayLoss = this.calculatePrepayLoss(prepayLossCollectionIds, interestDate);
            Set<Long> nominalPriceCollectionIds = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.NOMINAL_PRICE.name())).map(CollectionBaseInfo::getId).collect(Collectors.toSet());
            nominalPrice = this.calculateNominalPrice(nominalPriceCollectionIds, interestDate);
        }
        // 再处理归属于当前付款/借据下的收款
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        collectionBaseInfoList.removeIf(e -> !paymentIds.contains(e.getPaymentId()) && !Objects.equals(e.getReceiptId(), contractReceipt.getId()));
        Set<Long> collectionIds = collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet());
        // 付款核销票据到期
        long paymentBillExpire = this.calculatePaymentBillExpire(paymentIds, interestDate);
        // 收款核销票据到期
        long collectionBillExpire = this.calculateCollectionBillExpire(collectionIds, interestDate);
        // 现金收入（非租金）
        Set<Long> notRentCollectionIds = collectionBaseInfoList.stream().filter(e -> !Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name())).map(CollectionBaseInfo::getId).collect(Collectors.toSet());
        long cashInNotRent = this.calculateIn(notRentCollectionIds, interestDate, CASH);
        // 票据收入（非租金）
        long billInNotRent = this.calculateIn(notRentCollectionIds, interestDate, BILL);
        // 现金收入（租金）
        Set<Long> rentCollectionIds = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name())).map(CollectionBaseInfo::getId).collect(Collectors.toSet());
        long cashInRent = this.calculateIn(rentCollectionIds, interestDate, CASH);
        // 票据收入（租金）
        long billInRent = this.calculateIn(rentCollectionIds, interestDate, BILL);
        // 付款核销（现金）
        long cashPaymentOut = this.calculateOut(paymentBaseInfoList, interestDate, CASH);
        // 付款核销（票据）
        long billPaymentOut = this.calculateOut(paymentBaseInfoList, interestDate, BILL);
        // 合同是否被资金合同关联
        boolean isBelongFundFinance = this.isBelongFundFinance(contractReceipt.getContractId(), interestDate);
        // 逾期金额
        long totalOverdue = this.calculateTotalOverdue(contractReceipt, interestDate);
        // FTP价格逾期调整
        Integer ftpOverdueAdjust = 0;
//        if (Objects.nonNull(existRecord)) {
//            // 如果是重算则需要拿第一次算的
//            ftpOverdueAdjust = existRecord.getFtpOverdueAdjust();
//        } else {
            if (totalOverdue > 0) {
                ftpOverdueAdjust = this.calculateFtpOverdueAdjust(contractReceipt, interestDate);
            }
//        }
        log.info("FTP计息基础数据[借据编号:{}, 计息日期:{}, 本日提前终止补偿金:{}, 本日留购款:{}, 本日付款核销(现金):{}, 本日付款核销(票据):{}, 本日现金收入(非租金):{}, 本日票据收入(非租金):{}, 本日现金收入(租金):{}, 本日票据收入(租金):{}, 本日付款核销票据到期:{}, 本日收款核销票据到期:{}, 本日逾期金额:{}, 是否关联融资合同:{}, FTP价格逾期调整:{}]",
                contractReceipt.getReceiptCode(), LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN), prepayLoss, nominalPrice, cashPaymentOut, billPaymentOut, cashInNotRent, billInNotRent, cashInRent, billInRent, paymentBillExpire, collectionBillExpire, totalOverdue, isBelongFundFinance, ftpOverdueAdjust);
        /* ******************************************************************************************************************************** */
        /* ****************************************************** step-3 计算加工数据 ******************************************************* */
        /* ******************************************************************************************************************************** */
        // 现金支出 = 付款核销（现金） + 付款核销票据到期
        long cashOut = cashPaymentOut + paymentBillExpire;
        // 票据支出 = 付款核销（票据） -  付款核销票据到期
        long billOut = billPaymentOut - paymentBillExpire;
        // 现金收入 = 现金收入（非租金） + 现金收入（租金） + 收款票据到期 + 提前终止补偿金 + 留购款
        long cashIn = cashInNotRent + cashInRent + collectionBillExpire + prepayLoss + nominalPrice;
        // 票据收入 = 票据收入（非租金） + 票据收入（租金）  - 收款票据到期
        long billIn = billInNotRent + billInRent - collectionBillExpire;
        // 现金占用 = 上一日现金占用 + 本日现金支出 - 本日现金收入。上一日无值时当作0处理
        long cashOccupy = previousCashOccupy + cashOut - cashIn;
        // 票据占用 = 上一日票据占用 + 本日票据支出 - 本日票据收入。上一日无值时当作0处理
        long billOccupy = previousBillOccupy + billOut - billIn;
        // 现金FTP 这个地方改成从FTP价格表中取考核价格
        Integer cashFtp = Math.toIntExact(ftpAssessmentInfo.getAssessmentPrice());
        // 票据FTP 叶芳说：从payment_base_info表中取
        Integer billFtp = Math.toIntExact(ftpAssessmentInfo.getTicketPrice());
        // 现金计息
        long cashInterest;
        // 票据计息
        long billInterest;
        // 当年累计计息
        long totalInterestThisYear;
        // 重要！！！1.1需要特殊处理
        LocalDate yearBeginDate = LocalDate.of(year, 1, 1);
        if (interestDate.isEqual(yearBeginDate)) {
            // 需重置为剩余本金（现金），计算逻辑= 付款方式/收款方式！=“票据”的：已核销的付款金额 - 已核销的首期租金 - 已核销的本金 - 已核销的保证金（收款））
            // “票据占用”（无需重置，取去年末的值）
            cashOccupy = this.resetCashOccupyByReceipt(contractReceipt);
            // 当年累计计息 = 0
            totalInterestThisYear = 0L;
        } else {
            totalInterestThisYear = this.getBaseMapper().sumTotalInterestBetweenTargetRange(ftpInterestId, yearBeginDate, interestDate.minusDays(1));
        }
        // 逾期金额需要参与计算但不保存到占用字段，FTP价格逾期调整同理
        // 如果计息日期在起租日之前的则无需将负数计息重置为0进行计息
        boolean resetNegative2Zero = !interestDate.isBefore(contractReceipt.getReceiptStartDate());
        cashInterest = this.calculateInterest(cashFtp, cashOccupy, totalOverdue, ftpOverdueAdjust, isBelongFundFinance);
        billInterest = this.calculateInterest(billFtp, billOccupy, totalOverdue, ftpOverdueAdjust, isBelongFundFinance);
        // 资金计息 = 现金计息 + 票据计息
        long fundInterest = cashInterest + billInterest;
        if (fundInterest < 0 && resetNegative2Zero) {
            fundInterest = 0L;
            // 都置为0
            cashInterest = 0L;
            billInterest = 0L;
        }
        // 上一日的当年累计计息 + 当日的资金计息 + 当日的票据计息
        totalInterestThisYear = totalInterestThisYear + fundInterest;
        // 明细展示文案
        String itemText = LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN);
        /* ******************************************************************************************************************************** */
        /* ****************************************************** step-4 保存加工数据 ******************************************************* */
        /* ******************************************************************************************************************************** */
        // 生成FTP计息明细
        FtpInterestDetailRecord ftpInterestDetailRecord = new FtpInterestDetailRecord();
        ftpInterestDetailRecord.setFtpInterestId(ftpInterestId);
        ftpInterestDetailRecord.setItemText(itemText);
        ftpInterestDetailRecord.setInterestDate(interestDate);
        ftpInterestDetailRecord.setCashOut(cashOut);
        ftpInterestDetailRecord.setCashIn(cashIn);
        ftpInterestDetailRecord.setCashFtp(cashFtp);
        ftpInterestDetailRecord.setCashOccupy(cashOccupy);
        ftpInterestDetailRecord.setCashInterest(cashInterest);
        ftpInterestDetailRecord.setBillOut(billOut);
        ftpInterestDetailRecord.setBillIn(billIn);
        ftpInterestDetailRecord.setBillFtp(billFtp);
        ftpInterestDetailRecord.setBillOccupy(billOccupy);
        ftpInterestDetailRecord.setBillInterest(billInterest);
        ftpInterestDetailRecord.setIsOverdue(totalOverdue > 0 ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
        ftpInterestDetailRecord.setTotalInterestThisYear(Util.mithrasLongDecimalTwo(totalInterestThisYear));
        ftpInterestDetailRecord.setTotalOverdue(totalOverdue);
        ftpInterestDetailRecord.setFtpOverdueAdjust(ftpOverdueAdjust);
        if (Objects.nonNull(existRecord)) {
            ftpInterestDetailRecord.setId(existRecord.getId());
            this.updateById(ftpInterestDetailRecord);
        } else {
            this.save(ftpInterestDetailRecord);
        }
        // 重新计算一下本年累计
        ftpInterestBaseInfo.setTotalInterestAmount(this.getBaseMapper().sumTotalInterestBetweenTargetRange(ftpInterestId, yearBeginDate, interestDate));
        ftpInterestBaseInfo.setLastCashFtp(cashFtp);
        ftpInterestBaseInfo.setLastUpdateDate(interestDate);
        ftpInterestBaseInfoService.updateById(ftpInterestBaseInfo);
    }

    private long resetCashOccupyByReceipt(ContractReceipt contractReceipt) {
        // 已核销付款金额（非票据）
        long writeOffPayAmount = 0L;
        // 已核销首期租金（非票据）
        long writeOffDownPaymentAmount = 0L;
        // 已核销本金（非票据）
        long writeOffPrincipleAmount = 0L;
        // 已核销保证金收款（统一按照现金处理）
        long writeOffEarnestCollectAmount = 0L;
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            List<PaymentActualDetail> paymentActualDetailList = this.listWrittenOffPaymentActualList(paymentIds);
            writeOffPayAmount = paymentActualDetailList.stream().filter(item -> Objects.nonNull(item.getPaidInAmount()) && !Objects.equals(item.getPaymentMethod(), GlobalConstants.CQ_PAYMENT_METHOD_PJ)).mapToLong(PaymentActualDetail::getPaidInAmount).sum();
        }
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listBy(contractReceipt.getContractId(), null);
        Map<Long, CollectionBaseInfo> collectionBaseInfoMap = collectionBaseInfoList.stream()
                .filter(item -> Objects.equals(item.getReceiptId(), contractReceipt.getId()) || paymentIds.contains(item.getPaymentId()))
                .filter(item -> Objects.equals(item.getCashFlowItem(), CashFlowItemEnum.FIRST_RENT.name()) || Objects.equals(item.getCashFlowItem(), CashFlowItemEnum.RENT.name()) || Objects.equals(item.getCashFlowItem(), CashFlowItemEnum.EARNEST_MONEY.name()))
                .collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e));
        if (CollectionUtil.isNotEmpty(collectionBaseInfoMap)) {
            List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByCollectionIds(collectionBaseInfoMap.keySet());
            for (CollectionRecordInfo collectionRecordInfo : collectionRecordInfoList) {
                if (Objects.isNull(collectionRecordInfo.getCollectionAmount())) {
                    continue;
                }
                if (Objects.equals(collectionRecordInfo.getCollectionType(), GlobalConstants.CQ_PAYMENT_METHOD_PJ)) {
                    continue;
                }
                CollectionBaseInfo cbi = collectionBaseInfoMap.get(collectionRecordInfo.getCollectionId());
                if (Objects.equals(cbi.getCashFlowItem(), CashFlowItemEnum.RENT.name()) && Objects.nonNull(collectionRecordInfo.getPrincipal())) {
                    writeOffPrincipleAmount = writeOffPrincipleAmount + collectionRecordInfo.getPrincipal();
                }
                if (Objects.equals(cbi.getCashFlowItem(), CashFlowItemEnum.FIRST_RENT.name())) {
                    writeOffDownPaymentAmount = writeOffDownPaymentAmount + collectionRecordInfo.getCollectionAmount();
                }
                if (Objects.equals(cbi.getCashFlowItem(), CashFlowItemEnum.EARNEST_MONEY.name())) {
                    writeOffEarnestCollectAmount = writeOffEarnestCollectAmount + collectionRecordInfo.getCollectionAmount();
                }
            }
        }
        return writeOffPayAmount - writeOffDownPaymentAmount - writeOffPrincipleAmount - writeOffEarnestCollectAmount;
    }

    private boolean isBelongFundFinance(Long contractId, LocalDate interestDate) {
//        // 查间融的关联合同
//        List<FundFinancingPledgeInfo> result1 = financingPledgeInfoService.findContractPledgeList(contractId);
//        // 查直融的关联合同
//        List<FundDirectFinancingPledgeInfo> result2 = directFinancingPledgeInfoService.findContractPledgeList(contractId);
//        return CollectionUtil.isNotEmpty(result1) || CollectionUtil.isNotEmpty(result2);
        boolean pledge1 = false;
        List<FundFinancingPledgeInfo> plist1 = SpringUtil.getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().eq(FundFinancingPledgeInfo::getContractId, contractId));
        if (CollectionUtil.isNotEmpty(plist1)) {
            List<FundFinancingBaseInfo> flist1 = SpringUtil.getBean(FundFinancingBaseInfoService.class).list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().in(FundFinancingBaseInfo::getId, plist1.stream().map(FundFinancingPledgeInfo::getFinancingId).collect(Collectors.toSet())).eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
            if (CollectionUtil.isNotEmpty(flist1)) {
                for (FundFinancingBaseInfo item : flist1) {
                    if (Objects.nonNull(item.getActualLoanDate()) && !interestDate.isBefore(item.getActualLoanDate())) {
                        pledge1 = true;
                        break;
                    }
                }
            }
        }
        boolean pledge2 = false;
        List<FundDirectFinancingPledgeInfo> plist2 = SpringUtil.getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().eq(FundDirectFinancingPledgeInfo::getContractId, contractId));
        if (CollectionUtil.isNotEmpty(plist2)) {
            List<FundDirectFinancingBaseInfo> flist2 = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery().in(FundDirectFinancingBaseInfo::getId, plist2.stream().map(FundDirectFinancingPledgeInfo::getFinancingId).collect(Collectors.toSet())).eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
            if (CollectionUtil.isNotEmpty(flist2)) {
                for (FundDirectFinancingBaseInfo item : flist2) {
                    if (Objects.nonNull(item.getCarryInterestTime()) && !interestDate.isBefore(item.getCarryInterestTime())) {
                        pledge2 = true;
                        break;
                    }
                }
            }
        }
        return pledge1 || pledge2;
    }

    private long calculateTotalOverdue(ContractReceipt contractReceipt, LocalDate interestDate) {
        long overdueAmount = 0L;
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listRentByReceiptId(contractReceipt.getId());
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return overdueAmount;
        }
        // 去掉计划收款日期在计息日当天及之后的
        collectionBaseInfoList.removeIf(e -> (e.getPlanCollectionDate().isAfter(interestDate) || e.getPlanCollectionDate().isEqual(interestDate)));
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return overdueAmount;
        }
        // 查询核销记录
        List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByCollectionIds(collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet()));
        // 去掉核销日期在计息日当天及之后的
        collectionRecordInfoList.removeIf(e -> (e.getCollectionDate().isAfter(interestDate) || e.getCollectionDate().isEqual(interestDate)));
        long planAmount = collectionBaseInfoList.stream().filter(e -> Objects.nonNull(e.getPlanCollectionAmount())).mapToLong(CollectionBaseInfo::getPlanCollectionAmount).sum();
        long actualAmount = collectionRecordInfoList.stream().filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
        overdueAmount = planAmount - actualAmount;
        return overdueAmount > 0 ? overdueAmount : 0;
    }

    private Integer calculateFtpOverdueAdjust(ContractReceipt contractReceipt, LocalDate interestDate) {
        int defaultFtpOverdueAdjust = 5000;
        // 上层调用该方法说明一定存在逾期
        // 先判断是否给资金合同关联
        boolean isBelongFundFinance = this.isBelongFundFinance(contractReceipt.getContractId(), interestDate);
        if (isBelongFundFinance) {
            return defaultFtpOverdueAdjust;
        }
        // 进一步判断最早逾期日是否为系统当月
        List<CollectionBaseInfo> rentList = collectionBaseInfoService.listRentByReceiptId(contractReceipt.getId());
        rentList.removeIf(e -> !e.getPlanCollectionDate().isBefore(interestDate));
        if (CollectionUtil.isEmpty(rentList)) {
            return 0;
        }
        rentList.removeIf(e -> {
           long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
           long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
           return actualRent >= planRent;
        });
        if (CollectionUtil.isEmpty(rentList)) {
            return 0;
        }
        // 剩下的都是存在逾期的
        rentList.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
        LocalDate firstOverduePlanDate = rentList.get(0).getPlanCollectionDate();
        if (firstOverduePlanDate.getYear() == interestDate.getYear() && firstOverduePlanDate.getMonthValue() == interestDate.getMonthValue()) {
            // 最早逾期日如果为系统当月，则正常计算
            return 0;
        } else {
            return defaultFtpOverdueAdjust;
        }
    }

    private long calculateOut(List<PaymentBaseInfo> paymentBaseInfoList, LocalDate interestDate, int type) {
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, e -> e));
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.in(PaymentActualDetail::getPaymentId, paymentBaseInfoMap.keySet());
        query.eq(PaymentActualDetail::getPaidInDate, interestDate);
        query.eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        if (type == BILL) {
            query.eq(PaymentActualDetail::getPaymentMethod, GlobalConstants.CQ_PAYMENT_METHOD_PJ);
        } else {
            query.ne(PaymentActualDetail::getPaymentMethod, GlobalConstants.CQ_PAYMENT_METHOD_PJ);
        }
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.list(query);
        long sum = 0L;
        for (PaymentActualDetail paymentActualDetail : paymentActualDetailList) {
            Long paymentId = paymentActualDetail.getPaymentId();
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMap.get(paymentId);
            if (Objects.isNull(paymentBaseInfo)) {
                continue;
            }
            sum = sum + paymentActualDetail.getPaidInAmount();
        }
        return sum;
    }

    private long calculateIn(Set<Long> collectionIds, LocalDate interestDate, int type) {
        if (CollectionUtil.isEmpty(collectionIds)) {
            return 0L;
        }
        List<CollectionRecordInfo> collectionRecordInfoList = this.listSpecificWrittenRecord(collectionIds, interestDate);
        List<CollectionRecordInfo> result = new LinkedList<>();
        if (type == BILL) {
            // 支付方式为票据且FTP不是同项目FTP
            for (CollectionRecordInfo recordInfo : collectionRecordInfoList) {
                if (!Objects.equals(recordInfo.getCollectionType(), GlobalConstants.CQ_PAYMENT_METHOD_PJ)) {
                    continue;
                }
                if (this.isSameCashFtp(recordInfo)) {
                    continue;
                }
                result.add(recordInfo);
            }
        } else {
            // 支付方式为现金或者同项目FTP的票据
            for (CollectionRecordInfo recordInfo : collectionRecordInfoList) {
                if (Objects.equals(recordInfo.getCollectionType(), GlobalConstants.CQ_PAYMENT_METHOD_PJ)) {
                    if (!this.isSameCashFtp(recordInfo)) {
                        continue;
                    }
                }
                result.add(recordInfo);
            }
        }
        return result.stream().mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
    }

    private long calculatePaymentBillExpire(Set<Long> paymentIds, LocalDate interestDate) {
        LambdaQueryWrapper<BillManagement> query = Wrappers.lambdaQuery();
        query.eq(BillManagement::getBillType, BillTypeEnum.PAYMENT.name());
        query.eq(BillManagement::getBillExpireDate, interestDate);
        List<BillManagement> billManagementList = billManagementService.list(query);
        if (CollectionUtil.isEmpty(billManagementList)) {
            return 0L;
        }
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByIds(billManagementList.stream().map(BillManagement::getMainId).collect(Collectors.toSet()));
        paymentActualDetailList.removeIf(e -> !paymentIds.contains(e.getPaymentId()));
        Map<Long, PaymentActualDetail> paymentActualDetailMap = paymentActualDetailList.stream().collect(Collectors.toMap(PaymentActualDetail::getId, e -> e));
        billManagementList.removeIf(e -> !paymentActualDetailMap.containsKey(e.getMainId()));
        long sum = 0L;
        for (BillManagement billManagement : billManagementList) {
            sum = sum + billManagement.getBillAmount();
        }
        return sum;
    }

    private long calculateCollectionBillExpire(Set<Long> collectionIds, LocalDate interestDate) {
        LambdaQueryWrapper<BillManagement> query = Wrappers.lambdaQuery();
        query.eq(BillManagement::getBillType, BillTypeEnum.COLLECTION.name());
        query.eq(BillManagement::getBillExpireDate, interestDate);
        List<BillManagement> billManagementList = billManagementService.list(query);
        if (CollectionUtil.isEmpty(billManagementList)) {
            return 0L;
        }
        List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByIds(billManagementList.stream().map(BillManagement::getMainId).collect(Collectors.toSet()));
        collectionRecordInfoList.removeIf(e -> !collectionIds.contains(e.getCollectionId()));
        Set<Long> mainIds = collectionRecordInfoList.stream().map(CollectionRecordInfo::getId).collect(Collectors.toSet());
        // 剔除不是目标收款的票据
        billManagementList.removeIf(e -> !mainIds.contains(e.getMainId()));
        // 剔除同项目FTP的票据（收款时已经视同现金处理）
        billManagementList.removeIf(e -> Objects.equals(e.getBillBuyRateType(), 1));
        return billManagementList.stream().mapToLong(BillManagement::getBillAmount).sum();
    }

    private boolean isFirstReceipt(ContractReceipt currentReceipt) {
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByContract(currentReceipt.getContractId());
        Set<Long> receiptIds = contractRentActualList.stream().filter(e -> StrUtil.isNotBlank(e.getCashFlowCode())).map(ContractRentActual::getReceiptId).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(receiptIds)) {
            throw new MithrasException("没有找到第一张有效借据信息，数据存在异常");
        }
        LambdaQueryWrapper<ContractReceipt> query = Wrappers.lambdaQuery();
        query.in(ContractReceipt::getId, receiptIds);
        query.orderByAsc(ContractReceipt::getReceiptCode);
        query.last(StringUtil.mysqlLimitOne());
        ContractReceipt firstReceipt = contractReceiptService.getOne(query);
        return Objects.equals(currentReceipt.getReceiptCode(), firstReceipt.getReceiptCode());
    }

    private List<CollectionRecordInfo> listSpecificWrittenRecord(Collection<Long> collectionIds, LocalDate interestDate) {
        if (CollectionUtil.isEmpty(collectionIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<CollectionRecordInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionRecordInfo::getCollectionId, collectionIds);
        query.eq(CollectionRecordInfo::getCollectionDate, interestDate);
        query.eq(CollectionRecordInfo::getWriteOffStatus, CollectionRecordWriteOffStatus.WRITTEN_OFF.name());
        // 保证金抵扣的核销记录忽略
        query.ne(CollectionRecordInfo::getCollectionType, RecordTypeEnum.REFUND_MARGIN_DEDUCT.name());
        return collectionRecordInfoService.list(query);
    }

    private long calculatePrepayLoss(Collection<Long> collectionIds, LocalDate interestDate) {
        List<CollectionRecordInfo> recordInfoList = this.listSpecificWrittenRecord(collectionIds, interestDate);
        if (CollectionUtil.isEmpty(recordInfoList)) {
            return 0L;
        }
        return recordInfoList.stream().mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
    }

    private long calculateNominalPrice(Collection<Long> collectionIds, LocalDate interestDate) {
        List<CollectionRecordInfo> recordInfoList = this.listSpecificWrittenRecord(collectionIds, interestDate);
        if (CollectionUtil.isEmpty(recordInfoList)) {
            return 0L;
        }
        return recordInfoList.stream().mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
    }

    private boolean isSameCashFtp(CollectionRecordInfo recordInfo) {
        List<BillManagement> billManagementList = billManagementService.listByMainIdAndType(recordInfo.getId(), BillTypeEnum.COLLECTION.name());
        if (CollectionUtil.isNotEmpty(billManagementList)) {
            boolean isSameCashFtp = YesOrNoNumberEnum.YES.getCode().equals(billManagementList.get(0).getBillBuyRateType());
            // 校验一下实收金额是否等于票据面额之和
            long billSum = billManagementList.stream().filter(item -> Objects.nonNull(item.getBillAmount())).mapToLong(BillManagement::getBillAmount).sum();
            if (recordInfo.getCollectionAmount() != billSum) {
                // TODO 暂时打个日志  后续改为发送消息
                log.error("<{}>票据面额不等于实收金额，请检查票据信息!", recordInfo.getFlowId());
            }
            return isSameCashFtp;
        } else {
            // 如果没有找到收款对应的票据信息则报错
            log.error("没有找到收款对应的票据信息[recordInfoId:{}]", recordInfo.getId());
            throw new MithrasException("没有找到收款对应的票据信息");
        }
    }

    public FtpInterestDetailRecord getSpecificRecord(Long ftpInterestId, LocalDate targetDate) {
        LambdaQueryWrapper<FtpInterestDetailRecord> query = Wrappers.lambdaQuery();
        // 轧差标志, 这里查询正常数据的时候需要排除轧差数据
        query.eq(FtpInterestDetailRecord::getRollingDifferenceMark, YesOrNoNumberEnum.NO.getCode());
        query.eq(FtpInterestDetailRecord::getFtpInterestId, ftpInterestId);
        query.eq(FtpInterestDetailRecord::getInterestDate, targetDate);
        query.ne(FtpInterestDetailRecord::getItemText, BEGINNING_ITEM_TEXT);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public FtpInterestDetailRecord getLatestRecord(Long ftpInterestId, LocalDate interestDate) {
        LambdaQueryWrapper<FtpInterestDetailRecord> query = Wrappers.lambdaQuery();
        query.eq(FtpInterestDetailRecord::getFtpInterestId, ftpInterestId);
        query.eq(FtpInterestDetailRecord::getRollingDifferenceMark, YesOrNoNumberEnum.NO.getCode());
        query.le(FtpInterestDetailRecord::getInterestDate, interestDate);
        query.orderByDesc(FtpInterestDetailRecord::getInterestDate);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    private long calculateInterest(Integer ftp, long occupy, long overdue, Integer ftpOverdueAdjust, boolean isBelongFundFinance) {
        long factor = occupy;
        if (isBelongFundFinance) {
            // 被资金合同关联需要加上逾期金额
            factor = factor + overdue;
        }
        return Util.mithrasLongDecimalTwo(BigDecimal.valueOf(ftp + ftpOverdueAdjust).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(factor)).longValue());
    }

    private List<PaymentActualDetail> listWrittenOffPaymentActualList(Collection<Long> paymentIds) {
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.in(PaymentActualDetail::getPaymentId, paymentIds);
        query.eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        return paymentActualDetailService.list(query);
    }
}
