package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.service.AssociationBusinessSituationService;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.factory.mapper.ContractReceiptBottomMapper;
import cn.zswltech.mithras.factory.model.ContractReceiptBottom;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBusinessSituation;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.dashboard.GuanYuanOperationService;
import cn.zswltech.mithras.dashboard.application.guanyuandata.PayIncomeDTO;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2025/4/18
 * @description 业务情况表
 */
@Slf4j
@Component
public class AssociationBusinessSituationData extends AbstractDataStore<AssociationBusinessSituation> {
    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;
    @Resource
    private RiskMetricFactorService riskMetricFactorService;
    @Resource
    private GuanYuanOperationService guanYuanOperationService;
    @Resource
    private ContractReceiptBottomMapper contractReceiptBottomMapper;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        LocalDate dataDate = DateUtil.endOfMonth(LocalDate.of(year, period, 1));
        // 利润表
        Map<String, Long> profitMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.PROFIT.display, dataDate.getYear(), dataDate.getMonthValue());
        // 科目余额表
        Map<String, Long> subjectBalanceMap = riskMetricFactorService.findMetricValueMap(RiskMetricFactorTable.SUBJECT_BALANCE.display, dataDate.getYear(), dataDate.getMonthValue());
        // 借据底表
        List<ContractReceiptBottom> contractReceiptBottomList = contractReceiptBottomMapper.selectList(Wrappers.<ContractReceiptBottom>lambdaQuery().ge(BaseModel::getCreateTime, dataDate.plusDays(1).atStartOfDay()).le(BaseModel::getCreateTime, DateUtil.endOfDay(dataDate.plusDays(1))));
        // 投放收益率表
        List<PayIncomeDTO> payIncomeList = guanYuanOperationService.listPayIncome(LocalDate.of(dataDate.getYear(), 1, 1), dataDate);
        boolean condition1 = CollectionUtil.isNotEmpty(profitMap);
        boolean condition2 = CollectionUtil.isNotEmpty(subjectBalanceMap);
        boolean condition3 = CollectionUtil.isNotEmpty(contractReceiptBottomList);
        boolean condition4 = CollectionUtil.isNotEmpty(payIncomeList);
        log.info("金融局报送【业务情况表】自动取值-前置数据校验结果:利润表 = {}, 科目余额表 = {}, 借据底表 = {}, 投放收益率表 = {}", condition1, condition2, condition3, condition4);
        return condition1 && condition2 && condition3 && condition4;
    }

    @Override
    protected List<AssociationBusinessSituation> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【业务情况表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<业务情况表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司业务情况表（月报）");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<业务情况表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationBusinessSituation> list = new LinkedList<>();
        try {
           /* if (rows.size() < 22) {
                throw new MithrasException("表格格式不正确，确实必要行");
            }*/
            list.add(this.convert(rows, dictNameMap));
        } catch (Exception e) {
            log.error("金融局报送【业务情况表】数据处理异常", e);
            throw new MithrasException("数据处理异常");
        }
        return list;
    }

    @Override
    protected List<AssociationBusinessSituation> parseFromSystemData(AssociationReport currentReport) {
        // 是否1月份
        boolean isJan = Objects.equals(currentReport.getReportPeriod(), 1);
        // 取上一期
        int[] yearPeriod = this.ensureLastOneYearPeriod(currentReport);
        AssociationReport lastReport = associationReportQueryService.findByCategoryYearPeriod(currentReport.getReportCategoryCode(), yearPeriod[0], yearPeriod[1]);
        if (Objects.isNull(lastReport)) {
            throw new MithrasException(String.format("金融局报送【业务情况表】自动取值-没有找到上一期报送记录[current:%s]", currentReport.getReportInstanceId()));
        }
        AssociationBusinessSituation lastReportValue = SpringUtil.getBean(AssociationBusinessSituationService.class).getModelByReportInstanceId(lastReport.getReportInstanceId());
        // 查询财务报表
        LocalDate targetDate = this.ensureMetricDate(currentReport);
        Map<String, Long> profitMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.PROFIT.display, targetDate.getYear(), targetDate.getMonthValue());
        // 查询借据底表
        List<ContractReceiptBottom> contractReceiptBottomList = contractReceiptBottomMapper.selectList(Wrappers.<ContractReceiptBottom>lambdaQuery().ge(BaseModel::getCreateTime, targetDate.plusDays(1).atStartOfDay()).le(BaseModel::getCreateTime, DateUtil.endOfDay(targetDate.plusDays(1))));
        // TODO 合同维度去重（因报表数据多借据的情况下都使用了合同数据，所以多借据的合同会重复计算，报表该逻辑修改后此处需要拿掉去重逻辑）
        Map<Long, ContractReceiptBottom> contractReceiptBottomMap = contractReceiptBottomList.stream().collect(Collectors.toMap(ContractReceiptBottom::getContractId, e -> e, (a,b) -> b));
        contractReceiptBottomList = new LinkedList<>(contractReceiptBottomMap.values());
        BigDecimal dirtLeasAstAeop = BigDecimal.ZERO;
        BigDecimal slbkAstAeop = BigDecimal.ZERO;
        BigDecimal operLeasAstAeop = BigDecimal.ZERO;
        BigDecimal iprvSlbkAstBalAeop = BigDecimal.ZERO;
        BigDecimal iprvFnlAstBalAeop = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(contractReceiptBottomList)) {
            for (ContractReceiptBottom contractReceiptBottom : contractReceiptBottomList) {
                // 计算剩余本金并转成万元
                BigDecimal remainingPrincipal = Util.millimeterLong2WanBigDecimal(Optional.ofNullable(contractReceiptBottom.getRemainPrincipal()).orElse(0L));
                if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                    remainingPrincipal = BigDecimal.ZERO;
                }
                if (StrUtil.equals(contractReceiptBottom.getLeaseType(), LeaseType.zhi_zu.name())) {
                    dirtLeasAstAeop = dirtLeasAstAeop.add(remainingPrincipal);
                    if (StrUtil.isNotBlank(contractReceiptBottom.getProvince()) && !contractReceiptBottom.getProvince().equals("330000")) {
                        iprvFnlAstBalAeop = iprvFnlAstBalAeop.add(remainingPrincipal);
                    }
                }
                if (StrUtil.equals(contractReceiptBottom.getLeaseType(), LeaseType.hui_zu.name())) {
                    slbkAstAeop = slbkAstAeop.add(remainingPrincipal);
                    if (StrUtil.isNotBlank(contractReceiptBottom.getProvince()) && !contractReceiptBottom.getProvince().equals("330000")) {
                        iprvSlbkAstBalAeop = iprvSlbkAstBalAeop.add(remainingPrincipal);
                        iprvFnlAstBalAeop = iprvFnlAstBalAeop.add(remainingPrincipal);
                    }
                }
                if (StrUtil.equals(contractReceiptBottom.getLeaseType(), LeaseType.jyx_zu.name())) {
                    operLeasAstAeop = operLeasAstAeop.add(remainingPrincipal);
                }
            }
        }
        // 查询投放收益率底表
        List<PayIncomeDTO> payIncomeList = guanYuanOperationService.listPayIncome(LocalDate.of(targetDate.getYear(), 1, 1), targetDate);
//        // TODO 合同维度去重（因报表数据多借据的情况下都使用了合同数据，所以多借据的合同会重复计算，报表该逻辑修改后此处需要拿掉去重逻辑）
//        Map<String, PayIncomeDTO> payIncomeMap = payIncomeList.stream().collect(Collectors.toMap(PayIncomeDTO::getContractCode, e -> e, (a,b) -> b));
//        payIncomeList = new LinkedList<>(payIncomeMap.values());
        // 处理数据
        AssociationBusinessSituation currentReportValue = new AssociationBusinessSituation();
        // 总收入-期初数 = 上一期报送的期末数，若为1月报表则为0
        currentReportValue.setTotIncmAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getTotIncmAeop).orElse(BigDecimal.ZERO));
        // 总收入-期末数 = 利润表：一、营业总收入@本年累计数
        currentReportValue.setTotIncmAeop(Optional.ofNullable(profitMap.get("一、营业总收入@本年累计数")).map(Util::millimeterLong2WanBigDecimal).orElse(BigDecimal.ZERO));
        // 经营租赁业务收入-期初数 = 上一期报送的期末数，若为1月报表则为0
        currentReportValue.setOperLeasBusiIncmAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getOperLeasBusiIncmAeop).orElse(BigDecimal.ZERO));
        // 经营租赁业务收入-期末数 = 科目余额表：主营业务收入_租赁收入_内部_经营租赁收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_经营租赁收入@本年累计@贷方金额
        BigDecimal operLeasBusiIncmAeopBD = riskMetricFactorMergeService.subjectBalanceSum(targetDate.getYear(), targetDate.getMonthValue(), ListUtil.toList(
                "主营业务收入_租赁收入_内部_经营租赁收入@本年累计@贷方金额",
                "主营业务收入_租赁收入_外部_经营租赁收入@本年累计@贷方金额"
        ));
        currentReportValue.setOperLeasBusiIncmAeop(Util.millimeterLong2WanBigDecimal(operLeasBusiIncmAeopBD.longValue()));
        // 其他收入-期初数 = 上一期报送的期末数，若为1月报表则为0
        currentReportValue.setOthIncmAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getOthIncmAeop).orElse(BigDecimal.ZERO));
        // 其他收入-期末数 = 科目余额表：主营业务收入_其他收入_外部@本年累计@贷方金额+主营业务收入_其他收入_内部@本年累计@贷方金额
        BigDecimal othIncmAeopBD = riskMetricFactorMergeService.subjectBalanceSum(targetDate.getYear(), targetDate.getMonthValue(), ListUtil.toList(
                "主营业务收入_其他收入_外部@本年累计@贷方金额",
                "主营业务收入_其他收入_内部@本年累计@贷方金额"
        ));
        currentReportValue.setOthIncmAeop(Util.millimeterLong2WanBigDecimal(othIncmAeopBD.longValue()));
        // 融资租赁业务收入-期初数 = 上一期报送的期末数，若为1月报表则为0
        currentReportValue.setFnlBusiIncmAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getFnlBusiIncmAeop).orElse(BigDecimal.ZERO));
        // 费用收入-期初数 = 上一期报送的期末数，若为1月报则为0
        currentReportValue.setFeeIncmAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getFeeIncmAeop).orElse(BigDecimal.ZERO));
        // 费用收入-期末数 = 科目余额表：主营业务收入_租赁收入_外部_售后回租收入_服务费收入@本年累计@贷方金额+主营业务收入_租赁收入_外部_融资租赁收入_服务费收入@本年累计@贷方金额+主营业务收入_租赁收入_内部_售后回租收入_服务费收入@本年累计@贷方金额+主营业务收入_租赁收入_内部_融资租赁收入_服务费收入@本年累计@贷方金额
        BigDecimal feeIncmAeopBD = riskMetricFactorMergeService.subjectBalanceSum(targetDate.getYear(), targetDate.getMonthValue(), ListUtil.toList(
                "主营业务收入_租赁收入_外部_售后回租收入_服务费收入@本年累计@贷方金额",
                "主营业务收入_租赁收入_外部_融资租赁收入_服务费收入@本年累计@贷方金额",
                "主营业务收入_租赁收入_内部_售后回租收入_服务费收入@本年累计@贷方金额",
                "主营业务收入_租赁收入_内部_融资租赁收入_服务费收入@本年累计@贷方金额"
        ));
        currentReportValue.setFeeIncmAeop(Util.millimeterLong2WanBigDecimal(feeIncmAeopBD.longValue()));
        // 利息收入-期初数 = 上一期报送的期末数，若为1月报则为0
        currentReportValue.setIntrIncmAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getIntrIncmAeop).orElse(BigDecimal.ZERO));
        // 直接租赁资产-期初数 = 上一期报送的期末数
        currentReportValue.setDirtLeasAstAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getDirtLeasAstAeop).orElse(BigDecimal.ZERO));
        // 直接租赁资产-期末数 = 借据底表 直租 剩余未还本金
        currentReportValue.setDirtLeasAstAeop(dirtLeasAstAeop);
        // 售后回租资产-期初数 = 上一期报送的期末数
        currentReportValue.setSlbkAstAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getSlbkAstAeop).orElse(BigDecimal.ZERO));
        // 售后回租资产-期末数 = 借据底表 回租 剩余未还本金
        currentReportValue.setSlbkAstAeop(slbkAstAeop);
        // 融资租赁资产-期初数 = 上一期报送的期末数
        currentReportValue.setFinLeasAstAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getFinLeasAstAeop).orElse(BigDecimal.ZERO));
        // 经营租赁资产-期初数 = 上一期报送的期末数
        currentReportValue.setOperLeasAstAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getOperLeasAstAeop).orElse(BigDecimal.ZERO));
        // 经营租赁资产-期末数 = 借据底表 经营性租赁 剩余未还本金
        currentReportValue.setOperLeasAstAeop(operLeasAstAeop);
        // 租赁资产-期初数 = 上一期报送的期末数
        currentReportValue.setLeasAstAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getLeasAstAeop).orElse(BigDecimal.ZERO));
        // 跨省售后回租资产余额-期初数 = 上一期报送的期末数
        currentReportValue.setIprvSlbkAstBalAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getIprvSlbkAstBalAeop).orElse(BigDecimal.ZERO));
        // 跨省售后回租资产余额-期末数 = 借据底表 地区为浙江省以外的回租资产剩余未还本金
        currentReportValue.setIprvSlbkAstBalAeop(iprvSlbkAstBalAeop);
        // 跨省融资租赁资产余额（承租人为省外）-期初数 = 上一期报送的期末数
        currentReportValue.setIprvFnlAstBalAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getIprvFnlAstBalAeop).orElse(BigDecimal.ZERO));
        // 跨省融资租赁资产余额（承租人为省外）-期末数 = 借据底表 地区为浙江省以外的直租及回租资产剩余未还本金
        currentReportValue.setIprvFnlAstBalAeop(iprvFnlAstBalAeop);
        // 直接租赁投放额-期初数 = 上一期报送的期末数，若为1月报则为0
        currentReportValue.setDirtLeasRelsAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getDirtLeasRelsAeop).orElse(BigDecimal.ZERO));
        // 直接租赁投放额-期末数 = 投放收益率情况表本年合计直租项目金额(投放金额)
        currentReportValue.setDirtLeasRelsAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> StrUtil.equals(e.getLeaseTypeDisplay(), LeaseType.zhi_zu.display()))
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum()
        ));
        // 售后回租投放额-期初数 = 上一期报送的期末数，若为1月报则为0
        currentReportValue.setSlbkRelsAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getSlbkRelsAeop).orElse(BigDecimal.ZERO));
        // 售后回租投放额-期末数 = 投放收益率情况表本年合计回租项目金额(投放金额)
        currentReportValue.setSlbkRelsAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> StrUtil.equals(e.getLeaseTypeDisplay(), LeaseType.hui_zu.display()))
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum()
        ));
        // 融资租赁投放额-期初数 = 上一期报送的期末数，若为1月报则为0
        currentReportValue.setFnlRelsAbop(isJan ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getFnlRelsAeop).orElse(BigDecimal.ZERO));
        // 固定收益类证券投资余额-期初数 = 上一期报送的期末数
        currentReportValue.setFixPayfScrIvsmAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getFixPayfScrIvsmAeop).orElse(BigDecimal.ZERO));
        // 固定收益类证券投资余额-期末数 = 0
        currentReportValue.setFixPayfScrIvsmAeop(BigDecimal.ZERO);
        // 国债余额-期初数 = 上一期报送的期末数
        currentReportValue.setTreaAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getTreaAeop).orElse(BigDecimal.ZERO));
        // 国债余额-期末数 = 0
        currentReportValue.setTreaAeop(BigDecimal.ZERO);
        // 资产减值损失准备-期初数 = 上一期报送的期末数
        currentReportValue.setIpoaLossAbop(Optional.ofNullable(lastReportValue).map(AssociationBusinessSituation::getIpoaLossAeop).orElse(BigDecimal.ZERO));
        // 资产减值损失准备-期末数 = 科目余额表：坏账准备_长期应收款坏账准备@期末余额@贷方金额+坏账准备_预付账款坏账准备@期末余额@贷方金额
        BigDecimal ipoaLossAeopBD = riskMetricFactorMergeService.subjectBalanceSum(targetDate.getYear(), targetDate.getMonthValue(), ListUtil.toList(
                "坏账准备_长期应收款坏账准备@期末余额@贷方金额",
                "坏账准备_预付账款坏账准备@期末余额@贷方金额"
        ));
        currentReportValue.setIpoaLossAeop(Util.millimeterLong2WanBigDecimal(ipoaLossAeopBD.longValue()));
        // 计算加工数据
        currentReportValue.calculate();
        return Collections.singletonList(currentReportValue);
    }

    @Override
    protected void check(List<AssociationBusinessSituation> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            // 融资租赁投放额期初数必填
            if (isNotEqualBigDecimalSum(e.getFnlRelsAbop(), e.getDirtLeasRelsAbop(), e.getSlbkRelsAbop())) {
                errorList.add("（期初数）序号14：融资租赁投放额=序号15：直接租赁投放额+序号16：售后回租投放额");
            }
            // 总收入_期初数必填
            if (isNotEqualBigDecimalSum(e.getTotIncmAbop(), e.getOperLeasBusiIncmAbop() , e.getFnlBusiIncmAbop(), e.getOthIncmAbop())) {
                errorList.add("（期初数）序号1：总收入=序号2：经营租赁业务收入+序号3：融资租赁业务收入+序号6：其他收入");
            }

            // 融资租赁业务收入_期初数必填
            if (isNotEqualBigDecimalSum(e.getFnlBusiIncmAbop(), e.getIntrIncmAbop(), e.getFeeIncmAbop())) {
                errorList.add("（期初数）序号3：融资租赁业务收入=序号4：利息收入+序号5：费用收入");
            }

            // 租赁资产_期初数必填
            if (isNotEqualBigDecimalSum(e.getLeasAstAbop(), e.getOperLeasAstAbop(), e.getFinLeasAstAbop())) {
                errorList.add("（期初数）序号7：租赁资产=序号8：经营租赁资产+序号9：融资租赁资产");
            }

            // 直接租赁资产_期初数必填
            if (isNotEqualBigDecimalSum(e.getFinLeasAstAbop(), e.getDirtLeasAstAbop(), e.getSlbkAstAbop())) {
                errorList.add("（期初数）序号9：融资租赁资产=序号10：直接租赁资产+序号11：售后回租资产");
            }

            // 融资租赁投放额_期末数必填
            if (isNotEqualBigDecimalSum(e.getFnlRelsAeop(), e.getDirtLeasRelsAeop(), e.getSlbkRelsAeop())) {
                errorList.add("（期末数）序号14：融资租赁投放额=序号15：直接租赁投放额+序号16：售后回租投放额");
            }

            // 总收入_期末数必填
            if (isNotEqualBigDecimalSum(e.getTotIncmAeop(), e.getOperLeasBusiIncmAeop(), e.getFnlBusiIncmAeop(), e.getOthIncmAeop())) {
                errorList.add("（期末数）序号1：总收入=序号2：经营租赁业务收入+序号3：融资租赁业务收入+序号6：其他收入");
            }

            // 利息收入_期末数必填
            if (isNotEqualBigDecimalSum(e.getFnlBusiIncmAeop(), e.getIntrIncmAeop(), e.getFeeIncmAeop())) {
                errorList.add("（期末数）序号3：融资租赁业务收入=序号4：利息收入+序号5：费用收入");
            }

            // 租赁资产_期末数必填
            if (isNotEqualBigDecimalSum(e.getLeasAstAeop(), e.getOperLeasAstAeop(), e.getFinLeasAstAeop())) {
                errorList.add("（期末数）序号7：租赁资产=序号8：经营租赁资产+序号9：融资租赁资产");
            }

            //直接租赁资产_期末数必填
            if (isNotEqualBigDecimalSum(e.getFinLeasAstAeop(), e.getDirtLeasAstAeop(), e.getSlbkAstAeop())) {
                errorList.add("（期末数）序号9：融资租赁资产=序号10：直接租赁资产+序号11：售后回租资产");
            }
        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationBusinessSituation> serviceBean() {
        return SpringUtil.getBean(AssociationBusinessSituationService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0005;
    }

    private AssociationBusinessSituation convert(List<List<Object>> excelRows, Map<String, Map<String, String>> dictNameMap) {
        AssociationBusinessSituation bean = new AssociationBusinessSituation();
        bean.setRowNum(1);
        bean.setOp("insert");
        // ########### 年初数(元)
        // 行3 - 总收入
        bean.setTotIncmAbop(parseBigDecimal(excelRows.get(3).get(2)));  // C列
        bean.setTotIncmAotc(parseBigDecimal(excelRows.get(3).get(3)));  // D列
        bean.setTotIncmAeop(parseBigDecimal(excelRows.get(3).get(4)));  // E列

        // 行4 - 经营租赁业务收入
        bean.setOperLeasBusiIncmAbop(parseBigDecimal(excelRows.get(4).get(2)));
        bean.setOperLeasBusiIncmAotc(parseBigDecimal(excelRows.get(4).get(3)));
        bean.setOperLeasBusiIncmAeop(parseBigDecimal(excelRows.get(4).get(4)));

        // 行5 - 融资租赁业务收入
        bean.setFnlBusiIncmAbop(parseBigDecimal(excelRows.get(5).get(2)));
        bean.setFnlBusiIncmAotc(parseBigDecimal(excelRows.get(5).get(3)));
        bean.setFnlBusiIncmAeop(parseBigDecimal(excelRows.get(5).get(4)));

        // 行6 - 利息收入
        bean.setIntrIncmAbop(parseBigDecimal(excelRows.get(6).get(2)));
        bean.setIntrIncmAotc(parseBigDecimal(excelRows.get(6).get(3)));
        bean.setIntrIncmAeop(parseBigDecimal(excelRows.get(6).get(4)));

        // 行7 - 费用收入
        bean.setFeeIncmAbop(parseBigDecimal(excelRows.get(7).get(2)));
        bean.setFeeIncmAotc(parseBigDecimal(excelRows.get(7).get(3)));
        bean.setFeeIncmAeop(parseBigDecimal(excelRows.get(7).get(4)));

        // 行8 - 其他收入
        bean.setOthIncmAbop(parseBigDecimal(excelRows.get(8).get(2)));
        bean.setOthIncmAotc(parseBigDecimal(excelRows.get(8).get(3)));
        bean.setOthIncmAeop(parseBigDecimal(excelRows.get(8).get(4)));

        // 行9 - 租赁资产
        bean.setLeasAstAbop(parseBigDecimal(excelRows.get(9).get(2)));
        bean.setLeasAstAotc(parseBigDecimal(excelRows.get(9).get(3)));
        bean.setLeasAstAeop(parseBigDecimal(excelRows.get(9).get(4)));

        // 行10 - 经营性租赁资产
        bean.setOperLeasAstAbop(parseBigDecimal(excelRows.get(10).get(2)));
        bean.setOperLeasAstAotc(parseBigDecimal(excelRows.get(10).get(3)));
        bean.setOperLeasAstAeop(parseBigDecimal(excelRows.get(10).get(4)));

        // 行11 - 融资租赁资产
        bean.setFinLeasAstAbop(parseBigDecimal(excelRows.get(11).get(2)));
        bean.setFinLeasAstAotc(parseBigDecimal(excelRows.get(11).get(3)));
        bean.setFinLeasAstAeop(parseBigDecimal(excelRows.get(11).get(4)));

        // 行12 - 直接租赁资产
        bean.setDirtLeasAstAbop(parseBigDecimal(excelRows.get(12).get(2)));
        bean.setDirtLeasAstAotc(parseBigDecimal(excelRows.get(12).get(3)));
        bean.setDirtLeasAstAeop(parseBigDecimal(excelRows.get(12).get(4)));

        // 行13 - 售后回租资产
        bean.setSlbkAstAbop(parseBigDecimal(excelRows.get(13).get(2)));
        bean.setSlbkAstAotc(parseBigDecimal(excelRows.get(13).get(3)));
        bean.setSlbkAstAeop(parseBigDecimal(excelRows.get(13).get(4)));

        // 行14 - 跨省融资租赁资产
        bean.setIprvFnlAstBalAbop(parseBigDecimal(excelRows.get(14).get(2)));
        bean.setIprvFnlAstBalAotc(parseBigDecimal(excelRows.get(14).get(3)));
        bean.setIprvFnlAstBalAeop(parseBigDecimal(excelRows.get(14).get(4)));

        // 行15 - 跨省售后回租资产
        bean.setIprvSlbkAstBalAbop(parseBigDecimal(excelRows.get(15).get(2)));
        bean.setIprvSlbkAstBalAotc(parseBigDecimal(excelRows.get(15).get(3)));
        bean.setIprvSlbkAstBalAeop(parseBigDecimal(excelRows.get(15).get(4)));

        // 行16 - 融资租赁投放额
        bean.setFnlRelsAbop(parseBigDecimal(excelRows.get(16).get(2)));
        bean.setFnlRelsAotc(parseBigDecimal(excelRows.get(16).get(3)));
        bean.setFnlRelsAeop(parseBigDecimal(excelRows.get(16).get(4)));

        // 行17 - 直接租赁投放额
        bean.setDirtLeasRelsAbop(parseBigDecimal(excelRows.get(17).get(2)));
        bean.setDirtLeasRelsAotc(parseBigDecimal(excelRows.get(17).get(3)));
        bean.setDirtLeasRelsAeop(parseBigDecimal(excelRows.get(17).get(4)));

        // 行18 - 售后回租投放额
        bean.setSlbkRelsAbop(parseBigDecimal(excelRows.get(18).get(2)));
        bean.setSlbkRelsAotc(parseBigDecimal(excelRows.get(18).get(3)));
        bean.setSlbkRelsAeop(parseBigDecimal(excelRows.get(18).get(4)));

        // 行19 - 固定收益类证券投资
        bean.setFixPayfScrIvsmAbop(parseBigDecimal(excelRows.get(19).get(2)));
        bean.setFixPayfScrIvsmAotc(parseBigDecimal(excelRows.get(19).get(3)));
        bean.setFixPayfScrIvsmAeop(parseBigDecimal(excelRows.get(19).get(4)));

        // 行20 - 国债
        bean.setTreaAbop(parseBigDecimal(excelRows.get(20).get(2)));
        bean.setTreaAotc(parseBigDecimal(excelRows.get(20).get(3)));
        bean.setTreaAeop(parseBigDecimal(excelRows.get(20).get(4)));

        // 行21 - 资产减值损失
        bean.setIpoaLossAbop(parseBigDecimal(excelRows.get(21).get(2)));
        bean.setIpoaLossAotc(parseBigDecimal(excelRows.get(21).get(3)));
        bean.setIpoaLossAeop(parseBigDecimal(excelRows.get(21).get(4)));
        return bean;
    }
}
