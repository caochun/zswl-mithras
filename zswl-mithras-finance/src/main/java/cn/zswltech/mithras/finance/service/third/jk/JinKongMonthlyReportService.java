package cn.zswltech.mithras.finance.service.third.jk;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricCurrency;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorType;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorMerge;
import cn.zswltech.mithras.metric.service.RiskMetricFactorFileService;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.basedata.persistence.mapper.AccountBalanceCategoryDictionaryMapper;
import cn.zswltech.mithras.basedata.persistence.model.AccountBalanceCategoryDictionary;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceBcmBalanceMf;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceSubjectBalanceAssist;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.finance.service.budget.FinanceBcmBalanceMfService;
import cn.zswltech.mithras.finance.service.FinanceSubjectBalanceAssistService;
import cn.zswltech.mithras.third.jinkong.client.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.third.jinkong.client.req.ReportBcmBalanceMfReq;
import cn.zswltech.mithras.third.jinkong.client.req.ReportBcmFflexfiledAssistMfReq;
import cn.zswltech.mithras.third.jinkong.client.res.*;
import cn.zswltech.mithras.third.yunhu.client.*;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class JinKongMonthlyReportService {
    private static final String CONTRACT_SOURCE = "f000007";
    private static final String DEPT_SOURCE = "f000006";

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
    @Resource
    private FinanceSubjectBalanceAssistService financeSubjectBalanceAssistService;
    @Resource
    private FinanceBcmBalanceMfService financeBcmBalanceMfService;
    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;

    //本年累计/上年同期@贷方金额/借方金额
    private final static String CURRENT_YEAR_TOTAL = "本年累计";
    private final static String LOST_YEAR_SAME = "上年同期";
    private final static String CREDIT_AMOUNT = "贷方金额";
    private final static String DEBIT_AMOUNT = "借方金额";
    private final static String BEGIN_BAL = "期初余额";
    private final static String END_BAL = "期末余额";


    public void syncAccountBalanceData(int year, int month) {
        List<String> allOrgCodeList = JSONUtil.toList(SpringUtil.getBean(SystemConfigService.class).getConfig("orgnazation_code_list").getData().getConfigValue(), String.class);
        for (String orgCode : allOrgCodeList) {
            try {
                // 查询数据
                List<ReportBcmBalanceMfRes.Data> dataList = this.listBcmBalanceData(orgCode, year, month);
                if (CollectionUtil.isEmpty(dataList)) {
                    throw new MithrasException("没有从云湖接口获取到科目余额表数据，请检查接口数据");
                }
                // 执行处理保存逻辑
                this.doSaveAccountBalance(orgCode, year, month, dataList);
                //保存科目余额表原始数据
                financeBcmBalanceMfService.remove(Wrappers.<FinanceBcmBalanceMf>lambdaQuery()
                        .eq(FinanceBcmBalanceMf::getOrg_code, orgCode)
                        .eq(FinanceBcmBalanceMf::getF_year, year)
                        .eq(FinanceBcmBalanceMf::getF_period, month));
                // 标准科目编码分组
                List<AccountBalanceCategoryDictionary> dicList = accountBalanceCategoryDictionaryMapper.selectList(null);
                Map<String, String> dicMap = dicList.stream().collect(Collectors.toMap(AccountBalanceCategoryDictionary::getAccountNo, AccountBalanceCategoryDictionary::getAccountName));
                List<FinanceBcmBalanceMf> financeBcmBalanceMfs = new ArrayList<>();
                dataList.forEach(e -> {
                    String accountNo = e.getAcct_no();
                    //拆分
                    String riskName = dicMap.get(e.getAcct_no());
                    //本年接借贷
                    financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, CURRENT_YEAR_TOTAL, CREDIT_AMOUNT), e.getCredit_ytd()));
                    financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, CURRENT_YEAR_TOTAL, DEBIT_AMOUNT), e.getDebit_ytd()));
                    financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, LOST_YEAR_SAME, CREDIT_AMOUNT), e.getCredit_lytd()));
                    financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, LOST_YEAR_SAME, DEBIT_AMOUNT), e.getDebit_lytd()));
                    //期初 期末
                    if (accountNo.startsWith("1231") || accountNo.startsWith("1535") || accountNo.startsWith("1602") || accountNo.startsWith("1652") || accountNo.startsWith("1702") || accountNo.startsWith("2") || accountNo.startsWith("4")) {
                        financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, BEGIN_BAL, CREDIT_AMOUNT), e.getBegin_bal()));
                        financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, END_BAL, CREDIT_AMOUNT), e.getEnd_bal()));

                    } else if (accountNo.startsWith("1")) {
                        financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, BEGIN_BAL, DEBIT_AMOUNT), e.getBegin_bal()));
                        financeBcmBalanceMfs.add(buildFinanceBcmBalanceMf(e, getRiskName(riskName, END_BAL, DEBIT_AMOUNT), e.getEnd_bal()));
                    }
                });
                financeBcmBalanceMfService.saveBatch(financeBcmBalanceMfs);
            } catch (Exception e) {
                log.error("同步科目余额表发生异常[orgCode:{}]", orgCode, e);
            }
        }
    }

    private String getRiskName(String... codes) {
        return String.join("@", codes);
    }

    private FinanceBcmBalanceMf buildFinanceBcmBalanceMf(ReportBcmBalanceMfRes.Data data, String riskName, BigDecimal riskValue) {
        FinanceBcmBalanceMf financeBcmBalanceMf = BeanUtil.copyProperties(data, FinanceBcmBalanceMf.class);
        if (ObjectUtil.isNotEmpty(data.getYear_period())) {
            String[] split = data.getYear_period().split("-");
            financeBcmBalanceMf.setF_year(Integer.valueOf(split[0]));
            financeBcmBalanceMf.setF_period(Integer.valueOf(split[1]));
        }
        financeBcmBalanceMf.setRiskName(riskName);
        financeBcmBalanceMf.setRiskValue(riskValue);
        return financeBcmBalanceMf;
    }

    //辅助核算维度表
    public void syncBcmFflexfiledAssistMfByDept(int year, int month) {
        financeSubjectBalanceAssistService.remove(
                Wrappers.<FinanceSubjectBalanceAssist>lambdaQuery()
                        .eq(FinanceSubjectBalanceAssist::getYear, year)
                        .eq(FinanceSubjectBalanceAssist::getMonth, month)
        );
        // 查询数据
        List<FinanceSubjectBalanceAssist> assistList = new ArrayList<>();
        int pageSize = 500;
        ReportBcmFflexfiledAssistMfReq req = new ReportBcmFflexfiledAssistMfReq();
        req.setPage_num(1);
        req.setPage_size(pageSize);
        while (true) {
            ReportBcmFflexfiledAssistMfRes res = yunHuReportBcmFflexfiledAssistMfHandler.execute(req);
            if (CollectionUtil.isEmpty(res.getData().getData())) {
                break;
            }
            // 过滤出辅助核算表中所有部门维度的数据
            res.getData().getData().removeIf(item -> !Objects.equals(item.getSource(), DEPT_SOURCE));
            if (CollectionUtil.isNotEmpty(res.getData().getData())) {
                for (ReportBcmFflexfiledAssistMfRes.Data data : res.getData().getData()) {
                    FinanceSubjectBalanceAssist assist = new FinanceSubjectBalanceAssist();
                    assist.setYear(year);
                    assist.setFentryid(data.getFentryid());
                    assist.setFid(String.valueOf(data.getFid()));
                    assist.setMonth(month);
                    assist.setFname(data.getFname());
                    assist.setDimFnumber(data.getDim_fnumber());
                    assist.setDimName(data.getDim_name());
                    assist.setFvalue(data.getFvalue());
                    assistList.add(assist);
                }
            }
            req.setPage_num(req.getPage_num() + 1);
        }

        if (CollectionUtil.isEmpty(assistList)) {
            throw new MithrasException("没有从金控接口获取到科目余额表数据，请检查接口数据");
        }
        // 执行处理保存逻辑 todo 数据过多考虑分批保存
        financeSubjectBalanceAssistService.saveBatch(assistList);
    }

    public List<ReportBcmBalanceMfRes.Data> listBcmBalanceData(String orgCode, int year, int month) {
        List<ReportBcmBalanceMfRes.Data> dataList = new LinkedList<>();
        int pageSize = 500;
        ReportBcmBalanceMfReq req = new ReportBcmBalanceMfReq();
        req.setYear_period(year + "-" + (month < 10 ? "0" + month : month));
        req.setPage_num(1);
        req.setPage_size(pageSize);
        req.setOrg_code(orgCode);
        req.setDt(LocalDateTimeUtil.format(LocalDate.of(year, month, 1), DatePattern.SIMPLE_MONTH_PATTERN));
        while (true) {
            ReportBcmBalanceMfRes res = yunHuReportBcmBalanceMfHandler.execute(req);
            if (res.getData() != null && CollectionUtil.isNotEmpty(res.getData().getData())) {
                dataList.addAll(res.getData().getData());
            }
            if (res.getData() == null || CollectionUtil.isEmpty(res.getData().getData()) || res.getData().getData().size() < pageSize) {
                break;
            } else {
                req.setPage_num(req.getPage_num() + 1);
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
            if (res.getData() == null || CollectionUtil.isEmpty(res.getData().getData())) {
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

    public void jinKongSyncAsset(LocalDate localDate) {
        // 拉取新数据
        List<RiskMetricFactorMerge> localList = this.queryAssetsAndConvert(localDate, GlobalConstants.ZSZL_LOCAL_ORG_CODE);
        List<RiskMetricFactorMerge> mergeList = this.queryAssetsAndConvert(localDate, GlobalConstants.ZSZL_MERGE_ORG_CODE);
        if (CollectionUtil.isEmpty(localList) || CollectionUtil.isEmpty(mergeList)) {
            return;
        }
        List<RiskMetricFactor> riskMetricFactorList = BeanUtil.copyToList(localList, RiskMetricFactor.class);
        List<RiskMetricFactorMerge> riskMetricFactorMergeList = new LinkedList<>();
        riskMetricFactorMergeList.addAll(localList);
        riskMetricFactorMergeList.addAll(mergeList);
        // 处理数据
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                //清理数据
                riskMetricFactorService.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                        .eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.CAPITAL_BALANCE.display)
                        .eq(RiskMetricFactor::getFactorDate, localDate));
                riskMetricFactorMergeService.remove(Wrappers.<RiskMetricFactorMerge>lambdaQuery()
                        .eq(RiskMetricFactorMerge::getFactorTable, RiskMetricFactorTable.CAPITAL_BALANCE.display)
                        .eq(RiskMetricFactorMerge::getFactorDate, localDate));
                riskMetricFactorService.saveBatch(riskMetricFactorList);
                // 取合并报表，最好的做法是在原表中加组织编码区分，但是考虑到原表在很多地方使用，
                // 如果复用原表要改动的地方非常多，并且还不包括其他数据仓库等，所以综合评估另建一张表用于保存带组织编码的报表数据
                riskMetricFactorMergeService.saveBatch(riskMetricFactorMergeList);
                riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.CAPITAL_BALANCE, localDate);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("同步<资产负债表>发生未知异常，事务将回滚", e);
            }
        });
    }

    private List<RiskMetricFactorMerge> queryAssetsAndConvert(LocalDate localDate, String orgCode) {
        ReportBcmAssetMfReq req = new ReportBcmAssetMfReq();
        req.setPage_num(1);
        req.setPage_size(1000);
        req.setFyear("FY" + localDate.getYear());
        req.setFperiod(String.format("M_M%02d", localDate.getMonthValue()));
        req.setForgnumber(orgCode);
        ReportBcmAssetMfRes res = yunHuReportBcmAssetMfHandler.execute(req);
        if (res.getData() == null) {
            throw new MithrasException("没有从云湖接口获取到返回信息");
        }
        List<ReportBcmAssetMfRes.AssetData> contentList = res.getData().getData();
        if (res.getData().getTotal_num() > 1000) {
            ReportBcmAssetMfRes execute;
            int num = new BigDecimal(res.getData().getTotal_num()).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 2; i <= num; i++) {
                req.setPage_num(i);
                execute = yunHuReportBcmAssetMfHandler.execute(req);
                if (execute.getData() != null && ObjectUtil.isNotEmpty(execute)) {
                    contentList.addAll(execute.getData().getData());
                }
            }
        }
        //全部保存
        List<RiskMetricFactorMerge> addMetric = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(contentList)) {
            Set<String> addFprojectnameSet = new HashSet<>();
            for (ReportBcmAssetMfRes.AssetData rsp : contentList) {
                //期末余额
                if(addFprojectnameSet.contains(rsp.getFprojectname())){
                    continue;
                }
                addFprojectnameSet.add(rsp.getFprojectname());
                RiskMetricFactorMerge fendbalance = new RiskMetricFactorMerge();
                fendbalance.setOrgCode(orgCode);
                fendbalance.setFactorName(String.format("%s@期末余额", rsp.getFprojectname()));
                fendbalance.setFactorDate(localDate);
                fendbalance.setFactorTable(RiskMetricFactorTable.CAPITAL_BALANCE.display);
                fendbalance.setFactorValue(LongUtil.other2Long(rsp.getFendbalance().toString()));
                fendbalance.setFactorCurrency(rsp.getFcurrency());
                fendbalance.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fendbalance);
                //年初余额
                RiskMetricFactorMerge fbeginbalanceName = new RiskMetricFactorMerge();
                fbeginbalanceName.setOrgCode(orgCode);
                fbeginbalanceName.setFactorName(String.format("%s@年初余额", rsp.getFprojectname()));
                fbeginbalanceName.setFactorDate(localDate);
                fbeginbalanceName.setFactorTable(RiskMetricFactorTable.CAPITAL_BALANCE.display);
                fbeginbalanceName.setFactorValue(LongUtil.other2Long(rsp.getFbeginbalance().toString()));
                fbeginbalanceName.setFactorCurrency(rsp.getFcurrency());
                fbeginbalanceName.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fbeginbalanceName);
            }
        } else {
            throw new MithrasException("没有从云湖接口获取到数据");
        }
        return addMetric;
    }

    public void jinKongSyncProfit(LocalDate localDate) {
        // 拉取新数据
        List<RiskMetricFactorMerge> localList = this.queryProfitAndConvert(localDate, GlobalConstants.ZSZL_LOCAL_ORG_CODE);
        List<RiskMetricFactorMerge> mergeList = this.queryProfitAndConvert(localDate, GlobalConstants.ZSZL_MERGE_ORG_CODE);
        if (CollectionUtil.isEmpty(localList) || CollectionUtil.isEmpty(mergeList)) {
            return;
        }
        List<RiskMetricFactor> riskMetricFactorList = BeanUtil.copyToList(localList, RiskMetricFactor.class);
        List<RiskMetricFactorMerge> riskMetricFactorMergeList = new LinkedList<>();
        riskMetricFactorMergeList.addAll(localList);
        riskMetricFactorMergeList.addAll(mergeList);
        // 处理数据
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                //清理数据
                riskMetricFactorService.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                        .eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.PROFIT.display)
                        .eq(RiskMetricFactor::getFactorDate, localDate));
                riskMetricFactorMergeService.remove(Wrappers.<RiskMetricFactorMerge>lambdaQuery()
                        .eq(RiskMetricFactorMerge::getFactorTable, RiskMetricFactorTable.PROFIT.display)
                        .eq(RiskMetricFactorMerge::getFactorDate, localDate));
                riskMetricFactorService.saveBatch(riskMetricFactorList);
                // 取合并报表，最好的做法是在原表中加组织编码区分，但是考虑到原表在很多地方使用，
                // 如果复用原表要改动的地方非常多，并且还不包括其他数据仓库等，所以综合评估另建一张表用于保存带组织编码的报表数据
                riskMetricFactorMergeService.saveBatch(riskMetricFactorMergeList);
                riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.PROFIT, localDate);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("同步<利润表>发生未知异常，事务将回滚", e);
            }
        });
    }

    private List<RiskMetricFactorMerge> queryProfitAndConvert(LocalDate localDate, String orgCode) {
        ReportBcmAssetMfReq req = new ReportBcmAssetMfReq();
        req.setPage_num(1);
        req.setPage_size(1000);
        req.setFyear("FY" + localDate.getYear());
        req.setForgnumber(orgCode);
        req.setFperiod(String.format("M_M%02d", localDate.getMonthValue()));
        ReportBcmProfitMfRes res = yunHuReportBcmProfitMfHandler.execute(req);
        if (res.getData() == null) {
            throw new MithrasException("没有从云湖接口获取到返回信息");
        }
        List<ReportBcmProfitMfRes.ProfitData> contentList = res.getData().getData();
        if (res.getData().getTotal_num() > 1000) {
            ReportBcmProfitMfRes execute;
            int num = new BigDecimal(res.getData().getTotal_num()).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 2; i <= num; i++) {
                req.setPage_num(i);
                execute = yunHuReportBcmProfitMfHandler.execute(req);
                if (execute.getData() != null && ObjectUtil.isNotEmpty(execute)) {
                    contentList.addAll(execute.getData().getData());
                }
            }
        }
        //全部保存
        List<RiskMetricFactorMerge> addMetric = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(contentList)) {
            Set<String> addFprojectnameSet = new HashSet<>();
            for (ReportBcmProfitMfRes.ProfitData rsp : contentList) {
                if(addFprojectnameSet.contains(rsp.getFprojectname())){
                    continue;
                }
                addFprojectnameSet.add(rsp.getFprojectname());
                //本期发生数
                RiskMetricFactorMerge fcurramount = new RiskMetricFactorMerge();
                fcurramount.setOrgCode(orgCode);
                fcurramount.setFactorName(String.format("%s@本期发生数", rsp.getFprojectname()));
                fcurramount.setFactorDate(localDate);
                fcurramount.setFactorTable(RiskMetricFactorTable.PROFIT.display);
                fcurramount.setFactorValue(LongUtil.other2Long(rsp.getFcurramount().toString()));
                fcurramount.setFactorCurrency(rsp.getFcurrency());
                fcurramount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fcurramount);
                //本年累计数
                RiskMetricFactorMerge fyearamount = new RiskMetricFactorMerge();
                fyearamount.setOrgCode(orgCode);
                fyearamount.setFactorName(String.format("%s@本年累计数", rsp.getFprojectname()));
                fyearamount.setFactorDate(localDate);
                fyearamount.setFactorTable(RiskMetricFactorTable.PROFIT.display);
                fyearamount.setFactorValue(LongUtil.other2Long(rsp.getFyearamount().toString()));
                fyearamount.setFactorCurrency(rsp.getFcurrency());
                fyearamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fyearamount);
                //上年同期累计数
                RiskMetricFactorMerge fpreamount = new RiskMetricFactorMerge();
                fpreamount.setOrgCode(orgCode);
                fpreamount.setFactorName(String.format("%s@上年同期累计数", rsp.getFprojectname()));
                fpreamount.setFactorDate(localDate);
                fpreamount.setFactorTable(RiskMetricFactorTable.PROFIT.display);
                fpreamount.setFactorValue(LongUtil.other2Long(rsp.getFpreamount().toString()));
                fpreamount.setFactorCurrency(rsp.getFcurrency());
                fpreamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fpreamount);
            }
        } else {
            throw new MithrasException("没有从云湖接口获取到数据");
        }
        return addMetric;
    }

    public void jinKongSyncCashFlow(LocalDate localDate) {
        // 拉取新数据
        List<RiskMetricFactorMerge> localList = this.queryCashFlowAndConvert(localDate, GlobalConstants.ZSZL_LOCAL_ORG_CODE);
        List<RiskMetricFactorMerge> mergeList = this.queryCashFlowAndConvert(localDate, GlobalConstants.ZSZL_MERGE_ORG_CODE);
        if (CollectionUtil.isEmpty(localList) || CollectionUtil.isEmpty(mergeList)) {
            return;
        }
        List<RiskMetricFactor> riskMetricFactorList = BeanUtil.copyToList(localList, RiskMetricFactor.class);
        List<RiskMetricFactorMerge> riskMetricFactorMergeList = new LinkedList<>();
        riskMetricFactorMergeList.addAll(localList);
        riskMetricFactorMergeList.addAll(mergeList);
        // 处理数据
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                //清理数据
                riskMetricFactorService.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                        .eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.CASH_FLOW.display)
                        .eq(RiskMetricFactor::getFactorDate, localDate));
                riskMetricFactorMergeService.remove(Wrappers.<RiskMetricFactorMerge>lambdaQuery()
                        .eq(RiskMetricFactorMerge::getFactorTable, RiskMetricFactorTable.CASH_FLOW.display)
                        .eq(RiskMetricFactorMerge::getFactorDate, localDate));
                riskMetricFactorService.saveBatch(riskMetricFactorList);
                // 取合并报表，最好的做法是在原表中加组织编码区分，但是考虑到原表在很多地方使用，
                // 如果复用原表要改动的地方非常多，并且还不包括其他数据仓库等，所以综合评估另建一张表用于保存带组织编码的报表数据
                riskMetricFactorMergeService.saveBatch(riskMetricFactorMergeList);
                riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.CASH_FLOW, localDate);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("同步<现金流量表>发生未知异常，事务将回滚", e);
            }
        });
    }

    private List<RiskMetricFactorMerge> queryCashFlowAndConvert(LocalDate localDate, String orgCode) {
        ReportBcmAssetMfReq req = new ReportBcmAssetMfReq();
        req.setPage_num(1);
        req.setPage_size(1000);
        req.setFyear("FY" + localDate.getYear());
        req.setFperiod(String.format("M_M%02d", localDate.getMonthValue()));
        req.setForgnumber(orgCode);
        ReportBcmCashflowMfRes res = yunHuReportBcmCashflowMfHandler.execute(req);
        if (res.getData() == null) {
            throw new MithrasException("没有从云湖接口获取到返回信息");
        }
        List<ReportBcmCashflowMfRes.CashflowData> contentList = res.getData().getData();
        if (res.getData().getTotal_num() > 1000) {
            ReportBcmCashflowMfRes execute;
            int num = new BigDecimal(res.getData().getTotal_num()).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 2; i <= num; i++) {
                req.setPage_num(i);
                execute = yunHuReportBcmCashflowMfHandler.execute(req);
                if (ObjectUtil.isNotEmpty(execute) && execute.getData() != null) {
                    contentList.addAll(execute.getData().getData());
                }
            }
        }
        //全部保存
        List<RiskMetricFactorMerge> addMetric = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(contentList)) {
            Set<String> addFprojectnameSet = new HashSet<>();
            for (ReportBcmCashflowMfRes.CashflowData rsp : contentList) {
                if(addFprojectnameSet.contains(rsp.getFprojectname())){
                    continue;
                }
                addFprojectnameSet.add(rsp.getFprojectname());
                //本期发生数
                RiskMetricFactorMerge fcurramount = new RiskMetricFactorMerge();
                fcurramount.setOrgCode(orgCode);
                fcurramount.setFactorName(String.format("%s@本期发生数", rsp.getFprojectname()));
                fcurramount.setFactorDate(localDate);
                fcurramount.setFactorTable(RiskMetricFactorTable.CASH_FLOW.display);
                fcurramount.setFactorValue(LongUtil.other2Long(rsp.getFcurramount().toString()));
                fcurramount.setFactorCurrency(rsp.getFcurrency());
                fcurramount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fcurramount);
                //本年累计数
                RiskMetricFactorMerge fyearamount = new RiskMetricFactorMerge();
                fyearamount.setOrgCode(orgCode);
                fyearamount.setFactorName(String.format("%s@本年累计数", rsp.getFprojectname()));
                fyearamount.setFactorDate(localDate);
                fyearamount.setFactorTable(RiskMetricFactorTable.CASH_FLOW.display);
                fyearamount.setFactorValue(LongUtil.other2Long(rsp.getFyearamount().toString()));
                fyearamount.setFactorCurrency(rsp.getFcurrency());
                fyearamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fyearamount);
                //上年同期累计数
                RiskMetricFactorMerge fpreamount = new RiskMetricFactorMerge();
                fpreamount.setOrgCode(orgCode);
                fpreamount.setFactorName(String.format("%s@上年同期累计数", rsp.getFprojectname()));
                fpreamount.setFactorDate(localDate);
                fpreamount.setFactorTable(RiskMetricFactorTable.CASH_FLOW.display);
                fpreamount.setFactorValue(LongUtil.other2Long(rsp.getFpreamount().toString()));
                fpreamount.setFactorCurrency(rsp.getFcurrency());
                fpreamount.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
                addMetric.add(fpreamount);
            }
        } else {
            throw new MithrasException("没有从云湖接口获取到数据");
        }
        return addMetric;
    }

    private void doSaveAccountBalance(String orgCode, int year, int month, List<ReportBcmBalanceMfRes.Data> dataList) {
        // 同一个组织编码的账套币种应该是一样的，这里取第一个
        RiskMetricCurrency riskMetricCurrency = RiskMetricCurrency.findByDisplay(dataList.get(0).getBasecurrency_name());
        LocalDate localDate = LocalDate.of(year, month, 1);
        LocalDate factorDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
        LocalDate temp = factorDate.minusMonths(1);
        LocalDate lastFactorDate = LocalDate.of(temp.getYear(), temp.getMonthValue(), temp.lengthOfMonth());
        // 找到上个月的科目余额表数据
        LambdaQueryWrapper<RiskMetricFactorMerge> lastFactorQuery = Wrappers.lambdaQuery();
        lastFactorQuery.eq(RiskMetricFactorMerge::getOrgCode, orgCode);
        lastFactorQuery.eq(RiskMetricFactorMerge::getFactorDate, lastFactorDate);
        lastFactorQuery.eq(RiskMetricFactorMerge::getFactorTable, RiskMetricFactorTable.SUBJECT_BALANCE.display);
        List<RiskMetricFactorMerge> lastMonthDataList = riskMetricFactorMergeService.list(lastFactorQuery);
//        if (CollectionUtil.isEmpty(lastMonthDataList)) {
//            throw new MithrasException("没有上个月的<科目余额表>数据，不执行数据导入逻辑，请先检查数据");
//        }
        // 分组
        Map<String, RiskMetricFactorMerge> lastDataMap = lastMonthDataList.stream().collect(Collectors.toMap(RiskMetricFactorMerge::getFactorName, e -> e));
        // 按照科目编码分组
        Map<String, List<ReportBcmBalanceMfRes.Data>> dataMap = dataList.stream().collect(Collectors.groupingBy(ReportBcmBalanceMfRes.Data::getAcct_no));
        // 标准科目编码分组
        List<AccountBalanceCategoryDictionary> dicList = accountBalanceCategoryDictionaryMapper.selectList(null);
        Map<String, String> dicMap = dicList.stream().collect(Collectors.toMap(AccountBalanceCategoryDictionary::getAccountNo, AccountBalanceCategoryDictionary::getAccountName));
        // 生成待保存的数据
        List<RiskMetricFactorMerge> riskMetricFactorMergeList = new LinkedList<>();
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
            riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, debitCurPrdSum, accountName + "@本期发生额@借方金额", accountNo, orgCode, riskMetricCurrency));
            riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, creditCurPrdSum, accountName + "@本期发生额@贷方金额", accountNo, orgCode, riskMetricCurrency));
            riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, debitYtdSum, accountName + "@本年累计@借方金额", accountNo, orgCode, riskMetricCurrency));
            riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, creditYtdSum, accountName + "@本年累计@贷方金额", accountNo, orgCode, riskMetricCurrency));
            if (accountNo.startsWith("1231") || accountNo.startsWith("1535") || accountNo.startsWith("1602") || accountNo.startsWith("1652") || accountNo.startsWith("1702") || accountNo.startsWith("2") || accountNo.startsWith("4")) {
                riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, beginBalSum, accountName + "@期初余额@贷方金额", accountNo, orgCode, riskMetricCurrency));
                riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, endBalSum, accountName + "@期末余额@贷方金额", accountNo, orgCode, riskMetricCurrency));
            } else if (accountNo.startsWith("1")) {
                riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, beginBalSum, accountName + "@期初余额@借方金额", accountNo, orgCode, riskMetricCurrency));
                riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, endBalSum, accountName + "@期末余额@借方金额", accountNo, orgCode, riskMetricCurrency));
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
            riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, Optional.ofNullable(debitRmf).map(RiskMetricFactor::getFactorValue).orElse(0L), accountName + "@年初余额@借方金额", accountNo, orgCode, riskMetricCurrency));
            riskMetricFactorMergeList.add(this.buildAccountBalanceModel(factorDate, Optional.ofNullable(creditRmf).map(RiskMetricFactor::getFactorValue).orElse(0L), accountName + "@年初余额@贷方金额", accountNo, orgCode, riskMetricCurrency));
        }
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                if (StrUtil.equals(orgCode, GlobalConstants.ZSZL_LOCAL_ORG_CODE)) {
                    // 如果是本级数据，需要单独存一份，为了兼容历史逻辑
                    // 删除对应月份的数据（如果有的话）
                    LambdaQueryWrapper<RiskMetricFactor> query = Wrappers.lambdaQuery();
                    query.eq(RiskMetricFactor::getFactorDate, factorDate);
                    query.eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.SUBJECT_BALANCE.display);
                    riskMetricFactorService.remove(query);
                    // 执行保存
                    List<RiskMetricFactor> riskMetricFactorList = BeanUtil.copyToList(riskMetricFactorMergeList, RiskMetricFactor.class);
                    riskMetricFactorService.saveBatch(riskMetricFactorList);
                }
                // 删除对应月份的数据（如果有的话）
                LambdaQueryWrapper<RiskMetricFactorMerge> query = Wrappers.lambdaQuery();
                query.eq(RiskMetricFactorMerge::getOrgCode, orgCode);
                query.eq(RiskMetricFactorMerge::getFactorDate, factorDate);
                query.eq(RiskMetricFactorMerge::getFactorTable, RiskMetricFactorTable.SUBJECT_BALANCE.display);
                riskMetricFactorMergeService.remove(query);
                // 执行保存
                riskMetricFactorMergeService.saveBatch(riskMetricFactorMergeList);
                // 更新导入记录
                riskMetricFactorFileService.trySaveOrUpdate(RiskMetricFactorTable.SUBJECT_BALANCE, factorDate);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("保存科目余额表数据异常[{}]", JSONUtil.toJsonStr(riskMetricFactorMergeList), e);
            }
        });
    }

    private RiskMetricFactorMerge buildAccountBalanceModel(LocalDate factorDate, Long factorValue, String factorName, String factorCode, String orgCode, RiskMetricCurrency riskMetricCurrency) {
        RiskMetricFactorMerge riskMetricFactor = new RiskMetricFactorMerge();
        riskMetricFactor.setFactorTable(RiskMetricFactorTable.SUBJECT_BALANCE.display);
        riskMetricFactor.setFactorDate(factorDate);
        riskMetricFactor.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
        riskMetricFactor.setFactorName(factorName);
        riskMetricFactor.setFactorCode(factorCode);
        riskMetricFactor.setFactorValue(factorValue);
        riskMetricFactor.setOrgCode(orgCode);
        riskMetricFactor.setFactorCurrency(Optional.ofNullable(riskMetricCurrency).map(Enum::name).orElse(null));
        return riskMetricFactor;
    }
}
