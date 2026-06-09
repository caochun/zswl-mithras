package cn.zswltech.mithras.others.bigbear;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.FinancingBalanceCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.AccumulativeIncreaseCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Department;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Industry;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Region;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.capital.domain.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.fund.application.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.FundOrganizationService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import liquibase.pro.packaged.L;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/7/10 16:41
 **/

public class MatricCloudSystemExportTest extends ApplicationTest {

    private final LocalDate calculateTime = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

    @Autowired
    private List<FinancingBalanceCalculator> calculators;
    @Autowired
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Autowired
    private FundReceiptFlowDetailService receiptFlowDetailService;
    @Autowired
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Autowired
    private FundOrganizationService organizationService;
    @Autowired
    private FundFinancingCreditRefService financingCreditRefService;

    @Test
    public void listFinancialSystemTodoListTest() {
        LocalDate now = LocalDate.now();
        List<FundFinancingBaseInfo> todoList = fundFinancingBaseInfoService.listFinancialSystemTodoList(LocalDate.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth()));
        System.out.println(JSONUtil.toJsonStr(todoList.stream().map(FundFinancingBaseInfo::getFinancingCode).collect(Collectors.toSet())));
    }

    @Test
    public void fundFinancingExport() {
        calculators.forEach(calculator -> {
            // 结束日为当月最后一天
            LocalDate end = calculateTime.with(TemporalAdjusters.lastDayOfMonth());
            Set<Long> financingIds = calculator.getFinancingIds(calculateTime);
            Map<Long, FundFinancingBaseInfo> baseInfoMap = fundFinancingBaseInfoService.listByIds(financingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity()));

            // 这里改从还本付息拿数据
            List<FundReceiptRepayBaseInfo> receiptRepayBaseInfos = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                    .isNull(FundReceiptRepayBaseInfo::getFinancingType));
            if (CollUtil.isEmpty(receiptRepayBaseInfos)) {
                return;
            }

            Map<Long, FundReceiptRepayBaseInfo> longMap = receiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity()));

            List<FundReceiptFlowDetail> detailList = receiptFlowDetailService.list(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                    .le(FundReceiptFlowDetail::getCashFlowDate, end)
                    .eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())
                    .in(FundReceiptFlowDetail::getReceiptRepayId, receiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList())));

            Map<Long, List<FundReceiptFlowDetail>> longListMap = detailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getReceiptRepayId));

            // 批量查询融资机构
            Map<Long, List<FundFinancingCreditRef>> orgMap = financingCreditRefService.queryBatchByFinancingId(financingIds);
            Set<Long> orgIds = orgMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
            Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);

            // 分融资导出详细账
            List<List<Object>> list = new ArrayList<>();
            for (Map.Entry<Long, FundReceiptRepayBaseInfo> entry : longMap.entrySet()) {
                List<FundReceiptFlowDetail> fundReceiptFlowDetails = longListMap.get(entry.getValue().getId());
                FundFinancingBaseInfo baseInfo = baseInfoMap.get(entry.getValue().getFinancingId());
                BigDecimal res = BigDecimal.valueOf(baseInfo.getFinancingAmount()).divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP);
                if (CollUtil.isNotEmpty(fundReceiptFlowDetails)) {
                    res = new BigDecimal(entry.getValue().getFinancingAmount())
                            .subtract(fundReceiptFlowDetails.stream().map(FundReceiptFlowDetail::getPrincipalAmount)
                                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add))
                            .divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
                }
                if (res.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                list.add(ListUtil.of(baseInfo.getFinancingCode(),
                        orgMap.get(baseInfo.getId()).stream().map(FundFinancingCreditRef::getOrganizationId).map(orgIdNameMap::get).collect(Collectors.joining(",")),
                        new BigDecimal(baseInfo.getFinancingAmount()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        res
                ));
            }
            // 写excel 放到用户下载目录里面
            String file = "/Users/bigbear/Downloads/" + calculator.metricCode() + ".xlsx";
            ExcelWriter excelWriter = new ExcelWriter(new File(file));
            excelWriter.writeHeadRow(ListUtil.of("融资编号", "融资机构", "融资金额", "结果"));
            excelWriter.write(list);
            excelWriter.flush();
        });
    }

    /**
     * 剩余本金导出
     */
    @Test
    public void exportRemainingPrincipal() {
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setEndDate(LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
        Map<Long, BigDecimal> remainingPrincipals = SpringUtil.getBean(RemainingPrincipalServiceImpl.class).remainingPrincipalGroupByContractId(dto);
        // 找到合同编号
        List<ContractBaseInfo> contractBaseInfos = SpringUtil.getBean(ContractBaseInfoService.class).listByIds(remainingPrincipals.keySet());
        Map<Long, ContractBaseInfo> contractCodeMap = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity(), (k1, k2) -> k1));
        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getClientId, Function.identity(), (k1, k2) -> k1));
        // 找到客户名称
        Map<Long, String> clientId2Name = SpringUtil.getBean(Id2NameService.class).clientId2Name(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
        // 写excel
        String file = "/Users/bigbear/Downloads/修正版剩余本金-最新版.xlsx";
        ExcelWriter excelWriter = new ExcelWriter(new File(file));
        excelWriter.writeHeadRow(ListUtil.of("客户名称", "业务部门", "行业分类", "地区分类", "剩余本金(万元)"));
        // 还需要按照客户id进行分组，然后将剩余本金加总
        Map<Long, BigDecimal> clientIdRemainingPrincipalMap = new HashMap<>();
        remainingPrincipals.forEach((contractId, remainingPrincipal) -> {
            ContractBaseInfo contractBaseInfo = contractCodeMap.get(contractId);
            if (remainingPrincipal == null || remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            clientIdRemainingPrincipalMap.put(contractBaseInfo.getClientId(), clientIdRemainingPrincipalMap.getOrDefault(contractBaseInfo.getClientId(), BigDecimal.ZERO).add(remainingPrincipal));
        });
        clientIdRemainingPrincipalMap.forEach((clientId, remainingPrincipal) -> {
            excelWriter.writeRow(ListUtil.of(clientId2Name.get(clientId),
                    deptMap.get(contractBaseInfoMap.get(clientId).getId()),
                    clientIndustryMap.get(clientId),
                    regionMap.get(contractBaseInfoMap.get(clientId).getId()),
                    remainingPrincipal.divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP)));
        });
        excelWriter.flush();
    }

    private static final Map<Long, String> clientIndustryMap = new HashMap<>();
    private static final Map<Long, String> regionMap = new HashMap<>();
    private static final Map<Long, String> deptMap = new HashMap<>();
    private static final Map<String, Long> DEPT_CODE_ID = new HashMap<>();


    @Before
    public void before() {
        Map<String, List<CorpCommerceInfoLib>> industryGroup = SpringUtil.getBean(CorpCommerceInfoLibService.class)
                .listNewestCommerceInfo(new CorpCommerceInfoLibDto()).stream().collect(Collectors.groupingBy(corpCommerceInfoLib -> {
                    String industryType = corpCommerceInfoLib.getIndustryType();
                    if (ObjectUtil.isEmpty(industryType)) {
                        return "NONE";
                    }
                    char c = industryType.charAt(0);
                    if (c >= 'A' && c <= 'T') {
                        return String.valueOf(c);
                    }
                    return "NONE";
                }));
        industryGroup.forEach((s, corpCommerceInfoLibs) -> {
            if (s.equals("NONE")) {
                return;
            }
            Set<Long> clientIds = corpCommerceInfoLibs.stream().map(ClientBaseModel::getClientId)
                    .collect(Collectors.toSet());
            for (Long clientId : clientIds) {
                clientIndustryMap.put(clientId, Industry.valueOf(s).display());
            }
        });


        if (DEPT_CODE_ID.isEmpty()) {
            DEPT_CODE_ID.putAll(SpringUtil.getBean(OrgDOMapper.class).queryAll().stream()
                    .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
        }
        for (Department department : Department.values()) {
            List<Long> contractIds = SpringUtil.getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .eq(ContractBaseInfo::getBizDeptId, DEPT_CODE_ID.get(department.name())))
                    .stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            for (Long contractId : contractIds) {
                deptMap.put(contractId, department.getName());
            }
        }

        //查询符合的项目
        for (Region region : Region.values()) {
            List<Long> projReviewIds = SpringUtil.getBean(ProjReviewBaseInfoService.class).list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                            .eq(ProjReviewBaseInfo::getProvince, region.code()))
                    .stream()
                    .map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(projReviewIds)) {
                continue;
            } else {
                //按照合同所属业务部门查询符合条件的数据
                SpringUtil.getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                                .in(ContractBaseInfo::getProjReviewId, projReviewIds))
                        .stream()
                        .map(ContractBaseInfo::getId).collect(Collectors.toList())
                        .forEach(contractId -> regionMap.put(contractId, region.getName()));
            }
        }
    }


    @Resource
    private PaymentActualDetailService actualDetailService;

    @Test
    public void exportNewClientPayment() {
        LocalDate dateTime = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate start = dateTime.with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        //有实际投放的新增客户====》20250715变更逻辑：历史未投放过的都算为新增客户
        List<Long> existClientIdList = actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .le(PaymentActualDetail::getPaidInDate, start))
                .stream().map(PaymentActualDetail::getClientId).collect(Collectors.toList());

        List<PaymentActualDetail> detailList = actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .notIn(PaymentActualDetail::getClientId, existClientIdList)
                .ge(PaymentActualDetail::getPaidInDate, start)
                .le(PaymentActualDetail::getPaidInDate, end));
        List<Long> clientIds = detailList.stream().map(PaymentActualDetail::getClientId).distinct().collect(Collectors.toList());
        Map<Long, List<PaymentActualDetail>> collect = detailList.stream().collect(Collectors.groupingBy(PaymentActualDetail::getClientId));

        Id2NameService id2NameService = SpringUtil.getBean(Id2NameService.class);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);

        // 导出Excel
        String file = "/Users/bigbear/Downloads/截止630新增客户投放.xlsx";
        ExcelWriter excelWriter = new ExcelWriter(new File(file));
        excelWriter.writeHeadRow(Arrays.asList("客户名称", "新增投放金额(元)"));
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : collect.entrySet()) {
            excelWriter.writeRow(Arrays.asList(
                    clientId2Name.get(entry.getKey()),
                    Util.toYuan(entry.getValue().stream()
                            .filter(e -> Objects.nonNull(e.getPaidInAmount()))
                            .mapToLong(PaymentActualDetail::getPaidInAmount).sum(), true)
            ));
        }
        excelWriter.flush();
    }
}
