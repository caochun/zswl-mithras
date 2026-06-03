package cn.zswltech.mithras.finance.view.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceFundsREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceFundsRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.finance.view.entity.DashboardFvCardSnapshot;
import cn.zswltech.mithras.finance.view.entity.DashboardFvFinancingCostSnapshot;
import cn.zswltech.mithras.finance.view.mapper.DashboardFvFinancingCostSnapshotMapper;
import cn.zswltech.mithras.finance.view.service.DashboardFvCardSnapshotService;
import cn.zswltech.mithras.finance.view.service.DashboardFvFinancingCostSnapshotService;
import cn.zswltech.mithras.dashboard.domain.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.DashboardFundFinanceMapper;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardFundCostQuery;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardFundCostResult;
import cn.zswltech.mithras.finance.view.service.DashboardFundFinanceDataProvider;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作台融资成本信息快照表(DashboardFvFinancingCostSnapshot)表服务实现类
 *
 * @author makejava
 * @since 2025-09-02 09:30:35
 */
@Service
public class DashboardFvFinancingCostSnapshotServiceImpl extends ServiceImpl<DashboardFvFinancingCostSnapshotMapper, DashboardFvFinancingCostSnapshot> implements DashboardFvFinancingCostSnapshotService {

    @Resource
    private DashboardFvFinancingCostSnapshotService thisService;
    @Resource
    private DashboardFundFinanceDataProvider dashboardFundFinanceDataProvider;

    @Override
    public void generate(Long mainId, LocalDate dataTime) {
        DashboardFundFinanceFundsREQ query = new DashboardFundFinanceFundsREQ();
        query.setPage(1);
        query.setPageSize(10000);
        query.setQueryDate(dataTime);
        PageR<DashboardFundFinanceFundsRSP> costFunds = dashboardFundFinanceDataProvider.costFunds(query);
        if (costFunds.getTotal() <= 0) {
            return;
        }
        Set<Long> indirectIds = costFunds.getList().stream().filter(e -> e.getFinancingCode().contains("DK")).map(DashboardFundFinanceFundsRSP::getFinancingId).collect(Collectors.toSet());
        Map<Long, List<String>> orgNameListMap = dashboardFundFinanceDataProvider.getOrgNameListMap(indirectIds);

        List<DashboardFvFinancingCostSnapshot> rspList = costFunds.getList().stream().map(result -> {
            DashboardFvFinancingCostSnapshot rsp = new DashboardFvFinancingCostSnapshot();
            rsp.setCardId(mainId);
            rsp.setFinancingCode(result.getFinancingCode());
            rsp.setFinancingId(result.getFinancingId());
            rsp.setOrgName(result.getOrgName());
            if (CharSequenceUtil.isNotBlank(result.getActualExpireDateStr())) {
                rsp.setActualExpireDate(LocalDateTimeUtil.parseDate(result.getActualExpireDateStr(), DatePattern.NORM_DATE_PATTERN));
            }
            if (CharSequenceUtil.isNotBlank(result.getActualLoanDateStr())) {
                rsp.setActualLoanDate(LocalDateTimeUtil.parseDate(result.getActualLoanDateStr(), DatePattern.NORM_DATE_PATTERN));
            }
            FinancingTypeEnum financingType = result.getFinancingCode().contains("ZR") ? FinancingTypeEnum.DIRECT : FinancingTypeEnum.INDIRECT;
            rsp.setFinancingTypeCode(financingType.name());
            rsp.setFinancingTypeDisplay(financingType.getDisplay());
            rsp.setComprehensiveInterestRate(Optional.ofNullable(result.getComprehensiveInterestRate()).map(a -> new BigDecimal(a.getValue())).orElse(BigDecimal.ZERO));
            rsp.setRepayWay(result.getRepayWay());
            rsp.setRelatedContractCodeList(JSONUtil.toJsonStr(result.getRelatedContractCodeList()));
            rsp.setFinancingMonth(result.getFinancingMonth());
            if (result.getFinancingTypeCode().equals(FinancingTypeEnum.DIRECT.name())) {
                rsp.setLoanAmount(new BigDecimal(Optional.ofNullable(result.getLoanAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            }
            if (FinancingTypeEnum.INDIRECT.name().equals(result.getFinancingTypeCode())) {
                List<String> orgNameList = orgNameListMap.get(result.getFinancingId());
                if (CollectionUtil.isNotEmpty(orgNameList)) {
                    rsp.setOrgName(CharSequenceUtil.join("、", orgNameList));
                }
            }
            rsp.setLoanAmount(Optional.ofNullable(result.getLoanAmount())
                    .map(ValueUnitDTO::getValue).map(BigDecimal::new).orElse(BigDecimal.ZERO)
                    .divide(new BigDecimal(10000), 6, RoundingMode.HALF_UP));
            rsp.setRemainingPrincipleAmount(Optional.ofNullable(result.getRemainingPrincipleAmount())
                    .map(ValueUnitDTO::getValue).map(BigDecimal::new).orElse(BigDecimal.ZERO)
                    .divide(new BigDecimal(10000), 6, RoundingMode.HALF_UP));
            return rsp;
        }).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(rspList)) {
            thisService.saveBatch(rspList);
        }
    }

    @Override
    public PageR<DashboardFundFinanceFundsRSP> listCost(DashboardFundCostQuery query) {
        Page<DashboardFvFinancingCostSnapshot> page = new Page<>(1, 10000);
        DashboardFvCardSnapshot cardSnapshot = SpringUtil.getBean(DashboardFvCardSnapshotService.class).getOne(Wrappers.<DashboardFvCardSnapshot>lambdaQuery()
                .eq(DashboardFvCardSnapshot::getDataTime, query.getQueryDate())
                .eq(DashboardFvCardSnapshot::getCardCode, DashboardCardGroupEnum.FUND_FINANCE_REPAY.name()));
        if (cardSnapshot == null) {
            return PageR.empty(query.getPage(), query.getPageSize());
        }
        LambdaQueryWrapper<DashboardFvFinancingCostSnapshot> queryWrapper = Wrappers.<DashboardFvFinancingCostSnapshot>lambdaQuery()
                .in(CollUtil.isNotEmpty(query.getIds()), DashboardFvFinancingCostSnapshot::getId, query.getIds())
                .like(CharSequenceUtil.isNotBlank(query.getFinancingCode()), DashboardFvFinancingCostSnapshot::getFinancingCode, query.getFinancingCode())
                .like(CharSequenceUtil.isNotBlank(query.getOrgName()), DashboardFvFinancingCostSnapshot::getOrgName, query.getOrgName())
                .eq(DashboardFvFinancingCostSnapshot::getCardId, cardSnapshot.getId())
                .eq(CharSequenceUtil.isNotBlank(query.getFinancingTypeCode()), DashboardFvFinancingCostSnapshot::getFinancingTypeCode, query.getFinancingTypeCode())
                .between(Objects.nonNull(query.getStartDate()) && Objects.nonNull(query.getEndDate()),
                        DashboardFvFinancingCostSnapshot::getActualLoanDate, query.getStartDate(), query.getEndDate())
                .orderByDesc(DashboardFvFinancingCostSnapshot::getActualLoanDate);
        Page<DashboardFvFinancingCostSnapshot> snapshotPage = thisService.page(page, queryWrapper);
        if (CollUtil.isEmpty(snapshotPage.getRecords())) {
            return PageR.empty(query.getPage(), query.getPageSize());
        }
        // 转化
        snapshotPage.getRecords().sort(Comparator.comparing(DashboardFvFinancingCostSnapshot::getActualLoanDate).reversed()
                .thenComparing(DashboardFvFinancingCostSnapshot::getRemainingPrincipleAmount).reversed());
        List<DashboardFundFinanceFundsRSP> rspList = snapshotPage.getRecords().stream().map(snapshot -> {
            DashboardFundFinanceFundsRSP rsp = new DashboardFundFinanceFundsRSP();
            rsp.setFinancingCode(snapshot.getFinancingCode());
            rsp.setFinancingId(snapshot.getFinancingId());
            rsp.setOrgName(snapshot.getOrgName());
            rsp.setFinancingTypeCode(snapshot.getFinancingTypeCode());
            rsp.setFinancingTypeDisplay(snapshot.getFinancingTypeDisplay());
            rsp.setFinancingMonth(snapshot.getFinancingMonth());
            rsp.setRepayWay(snapshot.getRepayWay());
            rsp.setLoanAmount(Optional.ofNullable(snapshot.getLoanAmount())
                    .map(a -> new ValueUnitDTO(a.toPlainString(), "元")).orElse(new ValueUnitDTO("0.00", "元")));
            rsp.setRemainingPrincipleAmount(Optional.ofNullable(snapshot.getRemainingPrincipleAmount())
                    .map(a -> new ValueUnitDTO(a.toPlainString(), "元")).orElse(new ValueUnitDTO("0.00", "元")));
            rsp.setComprehensiveInterestRate(Optional.ofNullable(snapshot.getComprehensiveInterestRate())
                    .map(a -> new ValueUnitDTO(a.toPlainString(), "%")).orElse(new ValueUnitDTO("0.00", "%")));
            rsp.setRelatedContractCodeList(JSONUtil.toList(snapshot.getRelatedContractCodeList(), String.class));
            rsp.setActualLoanDateStr(LocalDateTimeUtil.format(snapshot.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setActualExpireDateStr(LocalDateTimeUtil.format(snapshot.getActualExpireDate(), DatePattern.NORM_DATE_PATTERN));
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(rspList, snapshotPage.getTotal());
    }
}

