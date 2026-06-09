package cn.zswltech.mithras.service.service.third.yunhu;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorType;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorFileService;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.basedata.mapper.AccountBalanceCategoryDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.AccountBalanceCategoryDictionary;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.*;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.req.ReportBcmBalanceMfReq;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.req.ReportBcmFflexfiledAssistMfReq;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.res.*;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.YunHuReportBcmAssetMfHandler;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.YunHuReportBcmBalanceMfHandler;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.YunHuReportBcmCashflowMfHandler;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.YunHuReportBcmFflexfiledAssistMfHandler;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.YunHuReportBcmProfitMfHandler;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName JinKongMonthlyReportService
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/8/9 5:15 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class YunHuMonthlyReportService {

    private static final String CONTRACT_SOURCE = "f000007";

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private AccountBalanceCategoryDictionaryMapper accountBalanceCategoryDictionaryMapper;
    @Resource
    private YunHuReportBcmAssetMfHandler yunHuReportBcmAssetMfHandler;
    @Resource
    private RiskMetricFactorService riskMetricFactorService;
    @Resource
    private YunHuReportBcmProfitMfHandler yunHuReportBcmProfitMfHandler;
    @Resource
    private YunHuReportBcmCashflowMfHandler yunHuReportBcmCashflowMfHandler;
    @Resource
    private YunHuReportBcmBalanceMfHandler yunHuReportBcmBalanceMfHandler;
    @Resource
    private YunHuReportBcmFflexfiledAssistMfHandler yunHuReportBcmFflexfiledAssistMfHandler;
    @Resource
    private RiskMetricFactorFileService riskMetricFactorFileService;

    public void syncAccountBalanceData(int year, int month) {
        // 查询数据
        List<ReportBcmBalanceMfRes.Data> dataList = this.listBcmBalanceData(year, month);
        if (CollectionUtil.isEmpty(dataList)) {
            throw new MithrasException("没有从金控接口获取到科目余额表数据，请检查接口数据");
        }
        // 执行处理保存逻辑
        this.doSaveAccountBalance(year, month, dataList);
    }

    public List<ReportBcmBalanceMfRes.Data> listBcmBalanceData(int year, int month) {
        List<ReportBcmBalanceMfRes.Data> dataList = new LinkedList<>();
        int pageSize = 200;
        ReportBcmBalanceMfReq req = new ReportBcmBalanceMfReq();
        req.setYear_period(year + "-" + (month < 10 ? "0" + month : month));
        req.setPage_size(1);
        req.setPage_num(pageSize);
        while (true) {
            ReportBcmBalanceMfRes res = yunHuReportBcmBalanceMfHandler.execute(req);
            if (CollectionUtil.isNotEmpty(res.getData().getData())) {
                dataList.addAll(res.getData().getData());
            }
            if (CollectionUtil.isEmpty(res.getData().getData()) || res.getData().getData().size() < pageSize) {
                break;
            } else {
                req.setPage_size(req.getPage_size() + 1);
            }
        }
        return dataList;
    }

    public Map<Long, String> listBcmFflexAssistData() {
        Map<Long, String> resultMap = new HashMap<>();
        int pageSize = 200;
        ReportBcmFflexfiledAssistMfReq req = new ReportBcmFflexfiledAssistMfReq();
        req.setPage_num(1);
        req.setPage_size(pageSize);
        while (true) {
            ReportBcmFflexfiledAssistMfRes res = yunHuReportBcmFflexfiledAssistMfHandler.execute(req);
            if (CollectionUtil.isEmpty(res.getData().getData())) {
                break;
            }
            // 过滤出辅助核算表中所有合同维度的数据
            res.getData().getData().removeIf(item -> !Objects.equals(item.getSource(), CONTRACT_SOURCE));
            if (CollectionUtil.isNotEmpty(res.getData().getData())) {
                for (ReportBcmFflexfiledAssistMfRes.Data data : res.getData().getData()) {
                    resultMap.putIfAbsent(data.getFid(), data.getFname());
                }
            }
            req.setPage_num(req.getPage_num() + 1);
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void jinKongSyncAsset(LocalDate localDate) {
        ReportBcmAssetMfReq req = new ReportBcmAssetMfReq();
        req.setPage_num(1);
        req.setPage_size(1000);
        req.setFyear("FY" + localDate.getYear());
        req.setFperiod(String.format("M_M%02d", localDate.getMonthValue()));
        ReportBcmAssetMfRes res = yunHuReportBcmAssetMfHandler.execute(req);
        List<ReportBcmAssetMfRes.AssetData> contentList = res.getData().getData();
        if (res.getData().getTotal_num() > 1000) {
            ReportBcmAssetMfRes execute;
            int num = new BigDecimal(res.getData().getTotal_num()).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 2; i <= num; i++) {
                req.setPage_num(i);
                execute = yunHuReportBcmAssetMfHandler.execute(req);
                if (ObjectUtil.isNotEmpty(execute)) {
                    contentList.addAll(execute.getData().getData());
                }
            }
        }
        //全部保存
        List<RiskMetricFactor> addMetric = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(contentList)) {
            //清理数据
            riskMetricFactorService.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                    .eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.CAPITAL_BALANCE.display)
                    .eq(RiskMetricFactor::getFactorDate, localDate));
            Set<String> addFprojectnameSet = new HashSet<>();
            for (ReportBcmAssetMfRes.AssetData rsp : contentList) {
                //期末余额
                if(addFprojectnameSet.contains(rsp.getFprojectname())){
                    continue;
                }
                addFprojectnameSet.add(rsp.getFprojectname());
                RiskMetricFactor fendbalance = new RiskMetricFactor();
                fendbalance.setFactorName(String.format("%s@期末余额", rsp.getFprojectname()));
                fendbalance.setFactorDate(localDate);
                fendbalance.setFactorTable(RiskMetricFactorTable.CAPITAL_BALANCE.display);
                fendbalance.setFactorValue(LongUtil.other2Long(rsp.getFendbalance().toString()));
                fendbalance.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fendbalance);
                //年初余额
                RiskMetricFactor fbeginbalanceName = new RiskMetricFactor();
                fbeginbalanceName.setFactorName(String.format("%s@年初余额", rsp.getFprojectname()));
                fbeginbalanceName.setFactorDate(localDate);
                fbeginbalanceName.setFactorTable(RiskMetricFactorTable.CAPITAL_BALANCE.display);
                fbeginbalanceName.setFactorValue(LongUtil.other2Long(rsp.getFbeginbalance().toString()));
                fbeginbalanceName.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fbeginbalanceName);
            }
        } else {
            throw new MithrasException("没有从金控接口获取到数据");
        }
        if (addMetric.size() > 0) {
            riskMetricFactorService.saveBatch(addMetric);
            riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.CAPITAL_BALANCE, localDate);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void jinKongSyncProfit(LocalDate localDate) {
        ReportBcmAssetMfReq req = new ReportBcmAssetMfReq();
        req.setPage_num(1);
        req.setPage_size(1000);
        req.setFyear("FY" + localDate.getYear());
        req.setFperiod(String.format("M_M%02d", localDate.getMonthValue()));
        ReportBcmProfitMfRes res = yunHuReportBcmProfitMfHandler.execute(req);
        List<ReportBcmProfitMfRes.ProfitData> contentList = res.getData().getData();
        if (res.getData().getTotal_num() > 1000) {
            ReportBcmProfitMfRes execute;
            int num = new BigDecimal(res.getData().getTotal_num()).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 2; i <= num; i++) {
                req.setPage_num(i);
                execute = yunHuReportBcmProfitMfHandler.execute(req);
                if (ObjectUtil.isNotEmpty(execute)) {
                    contentList.addAll(execute.getData().getData());
                }
            }
        }
        //全部保存
        List<RiskMetricFactor> addMetric = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(contentList)) {
            //清理数据
            riskMetricFactorService.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                    .eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.PROFIT.display)
                    .eq(RiskMetricFactor::getFactorDate, localDate));
            Set<String> addFprojectnameSet = new HashSet<>();
            for (ReportBcmProfitMfRes.ProfitData rsp : contentList) {
                if(addFprojectnameSet.contains(rsp.getFprojectname())){
                    continue;
                }
                addFprojectnameSet.add(rsp.getFprojectname());
                //本期发生数
                RiskMetricFactor fcurramount = new RiskMetricFactor();
                fcurramount.setFactorName(String.format("%s@本期发生数", rsp.getFprojectname()));
                fcurramount.setFactorDate(localDate);
                fcurramount.setFactorTable(RiskMetricFactorTable.PROFIT.display);
                fcurramount.setFactorValue(LongUtil.other2Long(rsp.getFcurramount().toString()));
                fcurramount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fcurramount);
                //本年累计数
                RiskMetricFactor fyearamount = new RiskMetricFactor();
                fyearamount.setFactorName(String.format("%s@本年累计数", rsp.getFprojectname()));
                fyearamount.setFactorDate(localDate);
                fyearamount.setFactorTable(RiskMetricFactorTable.PROFIT.display);
                fyearamount.setFactorValue(LongUtil.other2Long(rsp.getFyearamount().toString()));
                fyearamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fyearamount);
                //上年同期累计数
                RiskMetricFactor fpreamount = new RiskMetricFactor();
                fpreamount.setFactorName(String.format("%s@上年同期累计数", rsp.getFprojectname()));
                fpreamount.setFactorDate(localDate);
                fpreamount.setFactorTable(RiskMetricFactorTable.PROFIT.display);
                fpreamount.setFactorValue(LongUtil.other2Long(rsp.getFpreamount().toString()));
                fpreamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fpreamount);
            }
        } else {
            throw new MithrasException("没有从金控接口获取到数据");
        }
        if (addMetric.size() > 0) {
            riskMetricFactorService.saveBatch(addMetric);
            riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.PROFIT, localDate);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void jinKongSyncCashFlow(LocalDate localDate) {
        ReportBcmAssetMfReq req = new ReportBcmAssetMfReq();
        req.setPage_num(1);
        req.setPage_size(1000);
        req.setFyear("FY" + localDate.getYear());
        req.setFperiod(String.format("M_M%02d", localDate.getMonthValue()));
        ReportBcmCashflowMfRes res = yunHuReportBcmCashflowMfHandler.execute(req);
        List<ReportBcmCashflowMfRes.CashflowData> contentList = res.getData().getData();
        if (res.getData().getTotal_num() > 1000) {
            ReportBcmCashflowMfRes execute;
            int num = new BigDecimal(res.getData().getTotal_num()).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 2; i <= num; i++) {
                req.setPage_num(i);
                execute = yunHuReportBcmCashflowMfHandler.execute(req);
                if (ObjectUtil.isNotEmpty(execute)) {
                    contentList.addAll(execute.getData().getData());
                }
            }
        }
        //全部保存
        List<RiskMetricFactor> addMetric = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(contentList)) {
            //清理数据
            riskMetricFactorService.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                    .eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.CASH_FLOW.display)
                    .eq(RiskMetricFactor::getFactorDate, localDate));
            Set<String> addFprojectnameSet = new HashSet<>();
            for (ReportBcmCashflowMfRes.CashflowData rsp : contentList) {
                if(addFprojectnameSet.contains(rsp.getFprojectname())){
                    continue;
                }
                addFprojectnameSet.add(rsp.getFprojectname());
                //本期发生数
                RiskMetricFactor fcurramount = new RiskMetricFactor();
                fcurramount.setFactorName(String.format("%s@本期发生数", rsp.getFprojectname()));
                fcurramount.setFactorDate(localDate);
                fcurramount.setFactorTable(RiskMetricFactorTable.CASH_FLOW.display);
                fcurramount.setFactorValue(LongUtil.other2Long(rsp.getFcurramount().toString()));
                fcurramount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fcurramount);
                //本年累计数
                RiskMetricFactor fyearamount = new RiskMetricFactor();
                fyearamount.setFactorName(String.format("%s@本年累计数", rsp.getFprojectname()));
                fyearamount.setFactorDate(localDate);
                fyearamount.setFactorTable(RiskMetricFactorTable.CASH_FLOW.display);
                fyearamount.setFactorValue(LongUtil.other2Long(rsp.getFyearamount().toString()));
                fyearamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fyearamount);
                //上年同期累计数
                RiskMetricFactor fpreamount = new RiskMetricFactor();
                fpreamount.setFactorName(String.format("%s@上年同期累计数", rsp.getFprojectname()));
                fpreamount.setFactorDate(localDate);
                fpreamount.setFactorTable(RiskMetricFactorTable.CASH_FLOW.display);
                fpreamount.setFactorValue(LongUtil.other2Long(rsp.getFpreamount().toString()));
                fpreamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fpreamount);
            }
        } else {
            throw new MithrasException("没有从金控接口获取到数据");
        }
        if (addMetric.size() > 0) {
            riskMetricFactorService.saveBatch(addMetric);
            riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.CASH_FLOW, localDate);
        }
    }

    private void doSaveAccountBalance(int year, int month, List<ReportBcmBalanceMfRes.Data> dataList) {
        LocalDate localDate = LocalDate.of(year, month, 1);
        LocalDate factorDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
        LocalDate temp = factorDate.minusMonths(1);
        LocalDate lastFactorDate = LocalDate.of(temp.getYear(), temp.getMonthValue(), temp.lengthOfMonth());
        // 找到上个月的科目余额表数据
        LambdaQueryWrapper<RiskMetricFactor> lastFactorQuery = Wrappers.lambdaQuery();
        lastFactorQuery.eq(RiskMetricFactor::getFactorDate, lastFactorDate);
        lastFactorQuery.eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.SUBJECT_BALANCE.display);
        List<RiskMetricFactor> lastMonthDataList = riskMetricFactorService.list(lastFactorQuery);
        if (CollectionUtil.isEmpty(lastMonthDataList)) {
            throw new MithrasException("没有上个月的<科目余额表>数据，不执行数据导入逻辑，请先检查数据");
        }
        // 分组
        Map<String, RiskMetricFactor> lastDataMap = lastMonthDataList.stream().collect(Collectors.toMap(RiskMetricFactor::getFactorName, e -> e));
        // 按照科目编码分组
        Map<String, List<ReportBcmBalanceMfRes.Data>> dataMap = dataList.stream().collect(Collectors.groupingBy(ReportBcmBalanceMfRes.Data::getAcct_no));
        // 标准科目编码分组
        List<AccountBalanceCategoryDictionary> dicList = accountBalanceCategoryDictionaryMapper.selectList(null);
        Map<String, String> dicMap = dicList.stream().collect(Collectors.toMap(AccountBalanceCategoryDictionary::getAccountNo, AccountBalanceCategoryDictionary::getAccountName));
        // 生成待保存的数据
        List<RiskMetricFactor> toSaveList = new LinkedList<>();
        for (Map.Entry<String, String> entry : dicMap.entrySet()) {
            /*
             * 返回接口中没有区分借贷方向的，需按照下述规则确定借贷方向：
             * 1类：借方，除了备抵科目（1231、1535、1602、1652、1702开头的科目，默认在贷方）
             * 2类：贷方
             * 4类：贷方
             * 5类：过渡科目，可忽略
             * 6类：每月结转损益，没有余额
             */
            String accountNo = entry.getKey();
            String accountName = entry.getValue();
            if (StrUtil.isBlank(accountNo)) {
                continue;
            }
            if (accountNo.startsWith("5")) {
                continue;
            }
            List<ReportBcmBalanceMfRes.Data> list = dataMap.get(accountNo);
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            // 期初余额
            long beginBalSum = 0L;
            // 期末余额
            long endBalSum = 0L;
            // 本期发生额-借方金额
            long debitCurPrdSum = 0L;
            // 本期发生额-贷方金额
            long creditCurPrdSum = 0L;
            // 本年累计-借方金额
            long debitYtdSum = 0L;
            // 本年累计-贷方金额
            long creditYtdSum = 0L;
            for (ReportBcmBalanceMfRes.Data data : list) {
                beginBalSum += Optional.ofNullable(data.getBegin_bal()).map(item -> Util.mithrasLongDecimalTwo(item.multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
                endBalSum += Optional.ofNullable(data.getEnd_bal()).map(item -> Util.mithrasLongDecimalTwo(item.multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
                debitCurPrdSum += Optional.ofNullable(data.getDebit_cur_prd()).map(item -> Util.mithrasLongDecimalTwo(item.multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
                creditCurPrdSum += Optional.ofNullable(data.getCredit_cur_prd()).map(item -> Util.mithrasLongDecimalTwo(item.multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
                debitYtdSum += Optional.ofNullable(data.getDebit_ytd()).map(item -> Util.mithrasLongDecimalTwo(item.multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
                creditYtdSum += Optional.ofNullable(data.getCredit_ytd()).map(item -> Util.mithrasLongDecimalTwo(item.multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
            }
            toSaveList.add(this.buildAccountBalanceModel(factorDate, debitCurPrdSum, accountName + "@本期发生额@借方金额", accountNo));
            toSaveList.add(this.buildAccountBalanceModel(factorDate, creditCurPrdSum, accountName + "@本期发生额@贷方金额", accountNo));
            toSaveList.add(this.buildAccountBalanceModel(factorDate, debitYtdSum, accountName + "@本年累计@借方金额", accountNo));
            toSaveList.add(this.buildAccountBalanceModel(factorDate, creditYtdSum, accountName + "@本年累计@贷方金额", accountNo));
            if (accountNo.startsWith("1231") || accountNo.startsWith("1535") || accountNo.startsWith("1602") || accountNo.startsWith("1652") || accountNo.startsWith("1702") || accountNo.startsWith("2") || accountNo.startsWith("4")) {
                toSaveList.add(this.buildAccountBalanceModel(factorDate, beginBalSum, accountName + "@期初余额@贷方金额", accountNo));
                toSaveList.add(this.buildAccountBalanceModel(factorDate, endBalSum, accountName + "@期末余额@贷方金额", accountNo));
            } else if (accountNo.startsWith("1")) {
                toSaveList.add(this.buildAccountBalanceModel(factorDate, beginBalSum, accountName + "@期初余额@借方金额", accountNo));
                toSaveList.add(this.buildAccountBalanceModel(factorDate, endBalSum, accountName + "@期末余额@借方金额", accountNo));
            }
            // 年初余额-借方金额(1月取上一个月的期末余额，其他月份取上一个月的年初余额)
            // 年初余额-贷方金额(1月取上一个月的期末余额，其他月份取上一个月的年初余额)
            RiskMetricFactor debitRmf;
            RiskMetricFactor creditRmf;
            if (factorDate.getMonthValue() == 1) {
                debitRmf = lastDataMap.get(accountName + "@期末余额@借方金额");
                creditRmf = lastDataMap.get(accountName + "@期末余额@贷方金额");
            } else {
                debitRmf = lastDataMap.get(accountName + "@年初余额@借方金额");
                creditRmf = lastDataMap.get(accountName + "@年初余额@贷方金额");
            }
            toSaveList.add(this.buildAccountBalanceModel(factorDate, Optional.ofNullable(debitRmf).map(RiskMetricFactor::getFactorValue).orElse(0L), accountName + "@年初余额@借方金额", accountNo));
            toSaveList.add(this.buildAccountBalanceModel(factorDate, Optional.ofNullable(creditRmf).map(RiskMetricFactor::getFactorValue).orElse(0L), accountName + "@年初余额@贷方金额", accountNo));
        }
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 删除对应月份的数据（如果有的话）
                LambdaQueryWrapper<RiskMetricFactor> query = Wrappers.lambdaQuery();
                query.eq(RiskMetricFactor::getFactorDate, factorDate);
                query.eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.SUBJECT_BALANCE.display);
                riskMetricFactorService.remove(query);
                // 执行保存
                riskMetricFactorService.saveBatch(toSaveList);
                // 更新导入记录
                riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.SUBJECT_BALANCE, factorDate);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("保存科目余额表数据异常[{}]", JSONUtil.toJsonStr(toSaveList), e);
            }
        });
    }

    private RiskMetricFactor buildAccountBalanceModel(LocalDate factorDate, Long factorValue, String factorName, String factorCode) {
        RiskMetricFactor riskMetricFactor = new RiskMetricFactor();
        riskMetricFactor.setFactorTable(RiskMetricFactorTable.SUBJECT_BALANCE.display);
        riskMetricFactor.setFactorDate(factorDate);
        riskMetricFactor.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
        riskMetricFactor.setFactorName(factorName);
        riskMetricFactor.setFactorCode(factorCode);
        riskMetricFactor.setFactorValue(factorValue);
        return riskMetricFactor;
    }


}
