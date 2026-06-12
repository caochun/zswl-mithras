package cn.zswltech.mithras.application.orchestration.facade.liquidityrisk;

import cn.zswltech.mithras.liquidity.application.liquidityrisk.CapitalOutflowApplicationService;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.organization.FundOrganization;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.application.orchestration.liquidityrisk.CapitalOutflowService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @create: 2023-05-15
 **/

@Slf4j
@Service
public class CapitalOutflowFacade implements CapitalOutflowApplicationService {
    @Autowired
    private HttpServletResponse response;

    @Resource
    private CapitalOutflowService capitalOutflowService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private FundOrganizationService organizationService;

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<FundsCashOutflowListRsp> fundsCashOutflowList(@Valid CashOutflowListReq req) {
        return R.ok(capitalOutflowService.fundsCashOutflowList2(req));
    }

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<AssetsCashOutflowListRsp> assetsCashOutflowList(@Valid CashOutflowListReq req) {
        return R.ok(capitalOutflowService.assetsCashOutflowList(req));
    }

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<ChartQueryRSP> estimateCashOutflowList(@Valid CashOutflowListReq req) {
        return R.ok(capitalOutflowService.estimateCashOutflowList(req));
    }

    @Override
    //@DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<ChartQueryRSP> stressTestingOutflowList(@Valid CashOutflowListReq req) {
        return R.ok(capitalOutflowService.stressTestingOutflowList(req));
    }

    @Override
    //@DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<Void> cashOutflowExport(@Valid CashOutflowListReq req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("现金流流出明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            capitalOutflowService.cashOutflowExport(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流流出明细发生未知异常", e);
            throw new MithrasException("导出现金流流出明细发生未知异常");
        }
        return R.ok();
    }

    @Override
    //@DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<List<CashInOutStatRSP>> cashInOutStat(@Valid CashInOutStatREQ req) {
        long days = LocalDateTimeUtil.between(
                LocalDateTime.of(req.getDateFrom(), LocalTime.MIN),
                LocalDateTime.of(req.getDateTo(), LocalTime.MIN),
                ChronoUnit.DAYS);
        if (Math.abs(days) > 90) {
            err("时间跨度不能超过90天");
        }
        List<CashInOutStatRSP> result = inOutData(req.getDateFrom(), req.getDateTo());
        if (TRUE.equals(req.getLackBalance())) {
            result = result.stream().filter(e -> TRUE.equals(e.getLackBalance())).collect(Collectors.toList());
        }
        if (TRUE.equals(req.getMismatchBalance())) {
            result = result.stream().filter(e -> TRUE.equals(e.getMismatchBalance())).collect(Collectors.toList());
        }
        return R.ok(result);
    }

    private List<CashInOutStatRSP> inOutData(LocalDate start, LocalDate end) {
        List<CashInOutStatRSP> result = new ArrayList<>();
        //某月份的所有融资现金流
        List<FundReceiptRepayCashFlow> cashFlowList = getBean(FundReceiptRepayCashFlowService.class).list(
                Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .ge(FundReceiptRepayCashFlow::getRepayDate, start)
                        .le(FundReceiptRepayCashFlow::getRepayDate, end));
        if (cashFlowList.isEmpty()) {
            return result;
        }
        //质押数据一次查询，减少for循环查询
        Map<Long, List<FundDirectFinancingPledgeInfo>> directMap = getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                        .in(FundDirectFinancingPledgeInfo::getFinancingId, cashFlowList.stream().map(FundReceiptRepayCashFlow::getFinancingId).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
        Map<Long, List<FundFinancingPledgeInfo>> nonDirectMap = getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                        .in(FundFinancingPledgeInfo::getFinancingId, cashFlowList.stream().map(FundReceiptRepayCashFlow::getFinancingId).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));

        //
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(cashFlowList.stream().map(FundReceiptRepayCashFlow::getFinancingId).collect(Collectors.toList()));
        for (FundReceiptRepayCashFlow cashFlow : cashFlowList) {
            try {
                CashInOutStatRSP record = new CashInOutStatRSP();
                FundReceiptRepayBaseInfo fundReceiptInfo = getFundReceiptInfo(cashFlow.getReceiptRepayId());
                String financingType = fundReceiptInfo.getFinancingType();
                record.setFinancingType(financingType);
                record.setFinancingId(cashFlow.getFinancingId());
                if ("DIRECT".equals(financingType)) {//直融
                    FundDirectFinancingBaseInfo info = getBean(FundDirectFinancingBaseInfoService.class).getById(cashFlow.getFinancingId());
                    record.setFinancialChannel(Collections.singletonList("直租-" + info.getProductName()));
                    record.setFinancialCode(info.getFinancingCode());
                    record.setFinancialAmount(info.getFinancingAmount());

                } else {
                    List<FundOrganization> organizationList = orgMap.getOrDefault(cashFlow.getFinancingId(), Collections.emptyList());
                    FundFinancingBaseInfo info = getFundFinancingInfo(cashFlow.getFinancingId());
                    record.setFinancialChannel(Optional.ofNullable(organizationList).map(m -> m.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList())).orElse(Collections.emptyList()));
                    record.setFinancialCode(info.getFinancingCode());
                    record.setFinancialAmount(info.getFinancingAmount());
                }

                //--资金端
                record.setCashOutTotal(Optional.ofNullable(cashFlow.getRepayAmount()).orElse(0L));
                record.setCashOutPrincipal(Optional.ofNullable(cashFlow.getPrincipleAmount()).orElse(0L));
                record.setCashOutInterest(Optional.ofNullable(cashFlow.getInterestAmount()).orElse(0L));
                record.setCashOutDate(cashFlow.getRepayDate());
                //--资产端
                List<Long> pledgeContractIdList = new ArrayList<>();
                if ("DIRECT".equals(financingType)) {//直融
                    pledgeContractIdList = Optional.ofNullable(directMap.get(cashFlow.getFinancingId())).orElse(new ArrayList<>())
                            .stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList());
                } else {
                    pledgeContractIdList = Optional.ofNullable(nonDirectMap.get(cashFlow.getFinancingId())).orElse(new ArrayList<>())
                            .stream().map(FundFinancingPledgeInfo::getContractId).collect(Collectors.toList());

                }

                List<CashInOutStatRSP.InRecord> inRecords = new ArrayList<>();
                for (Long contractId : pledgeContractIdList) {
                    try {
                        ContractBaseInfo contractBaseInfo = getContractBaseInfo(contractId);
                        List<CollectionBaseInfo> collectionBaseInfoList = getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                .eq(CollectionBaseInfo::getContractId, contractId)
                                .ge(CollectionBaseInfo::getPlanCollectionDate, start.with(TemporalAdjusters.firstDayOfMonth()))
                                .le(CollectionBaseInfo::getPlanCollectionDate, end.with(TemporalAdjusters.lastDayOfMonth()))
                                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        );
                        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                            CashInOutStatRSP.InRecord inRecord = new CashInOutStatRSP.InRecord();
                            inRecord.setContractCode(contractBaseInfo.getContractCode());
                            inRecord.setProjName(contractBaseInfo.getProjName());
                            inRecord.setContractAmount(contractBaseInfo.getApplyCreditAmount());
                            inRecord.setCashInDate(collectionBaseInfo.getPlanCollectionDate());
                            inRecord.setCashInTotal(collectionBaseInfo.getPlanCollectionAmount());
                            inRecord.setCashInPrincipal(collectionBaseInfo.getPrincipal());
                            inRecord.setCashInInterest(collectionBaseInfo.getInterest());
                            inRecords.add(inRecord);
                        }
                    } catch (Exception ee) {
                        log.error("", ee);
                    }
                }
                record.setInRecordList(inRecords);
                result.add(record);
            } catch (Exception e) {
                log.error("", e);
            }
            fillErr(result);
        }
        return result;
    }

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public void downloadInOutStat(CashInOutStatREQ req) {
        String filename = format("%s~%s资金流动性统计表.xlsx", req.getDateFrom().format(ofPattern("yyyy-MM-dd")), req.getDateTo().format(ofPattern("yyyy-MM-dd")));
        List<CashInOutStatRSP> result = inOutData(req.getDateFrom(), req.getDateTo());
        getBean(CashInOutStatExporter.class).export(httpServletResponse, result, filename);
    }

    /**
     * 填充金额不足或日期错配
     *
     * @param result
     */
    private static void fillErr(List<CashInOutStatRSP> result) {
        result.sort(Comparator.comparing(m -> Optional.ofNullable(m.getFinancialChannel()).map(outStat -> outStat.get(0)).orElse(null)));
        Set<String> already = new HashSet<>();
        for (CashInOutStatRSP record : result) {
            already.add(record.getFinancialCode());
            List<CashInOutStatRSP.InRecord> inList = record.getInRecordList();
            //金额不足
            long inTotal = inList.stream().mapToLong(CashInOutStatRSP.InRecord::getCashInTotal).sum();
            record.setLackBalance(inTotal < record.getCashOutTotal());
            //日期错配
            long beforeInTotal = inList.stream().filter(e -> !e.getCashInDate().isAfter(record.getCashOutDate()))
                    .mapToLong(CashInOutStatRSP.InRecord::getCashInTotal).sum();
            record.setMismatchBalance(
                    /*总资金足够*/inTotal >= record.getCashOutTotal()
                            &&
                            /*日期过滤后不足*/beforeInTotal < record.getCashOutTotal()
            );
        }
    }

    private ContractBaseInfo getContractBaseInfo(Long id) {
        if (contractBaseInfoMap.containsKey(id)) {
            return contractBaseInfoMap.get(id);
        }
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getById(id);
        contractBaseInfoMap.put(id, baseInfo);
        return contractBaseInfoMap.get(id);
    }

    private FundReceiptRepayBaseInfo getFundReceiptInfo(Long id) {
        if (fundReceiptRepayBaseInfoMap.containsKey(id)) {
            return fundReceiptRepayBaseInfoMap.get(id);
        }
        FundReceiptRepayBaseInfo baseInfo = getBean(FundReceiptRepayBaseInfoService.class).getById(id);
        fundReceiptRepayBaseInfoMap.put(id, baseInfo);
        return fundReceiptRepayBaseInfoMap.get(id);
    }

    /**
     * 根据融资id获取融资信息
     *
     * @param id
     * @return
     */

    private FundFinancingBaseInfo getFundFinancingInfo(Long id) {
        if (fundFinnancingRepayBaseInfoMap.containsKey(id)) {
            return fundFinnancingRepayBaseInfoMap.get(id);
        }
        FundFinancingBaseInfo baseInfo = getBean(FundFinancingBaseInfoService.class).getById(id);
        fundFinnancingRepayBaseInfoMap.put(id, baseInfo);
        return fundFinnancingRepayBaseInfoMap.get(id);
    }

    Map<Long, ContractBaseInfo> contractBaseInfoMap = new HashMap<>();
    Map<Long, FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfoMap = new HashMap<>();
    Map<Long, FundFinancingBaseInfo> fundFinnancingRepayBaseInfoMap = new HashMap<>();


}
