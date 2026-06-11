package cn.zswltech.mithras.finance.view.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceRepayREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceRepayRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.finance.view.entity.DashboardFvCardSnapshot;
import cn.zswltech.mithras.finance.view.entity.DashboardFvRepayPrincipalInterestSnapshot;
import cn.zswltech.mithras.finance.view.entity.DashboardFvRepayPrincipalInterestSub;
import cn.zswltech.mithras.finance.view.mapper.DashboardFvRepayPrincipalInterestSnapshotMapper;
import cn.zswltech.mithras.finance.view.service.DashboardFvCardSnapshotService;
import cn.zswltech.mithras.finance.view.service.DashboardFvRepayPrincipalInterestSnapshotService;
import cn.zswltech.mithras.finance.view.service.DashboardFvRepayPrincipalInterestSubService;
import cn.zswltech.mithras.dashboard.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.dashboard.model.DashboardFundRepayQuery;
import cn.zswltech.mithras.finance.view.service.DashboardFundFinanceDataProvider;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作台还本付息信息表(DashboardFvRepayPrincipalInterestSnapshot)表服务实现类
 *
 * @author makejava
 * @since 2025-09-02 09:30:35
 */
@Service
public class DashboardFvRepayPrincipalInterestSnapshotServiceImpl extends ServiceImpl<DashboardFvRepayPrincipalInterestSnapshotMapper, DashboardFvRepayPrincipalInterestSnapshot> implements DashboardFvRepayPrincipalInterestSnapshotService {

    @Resource
    private DashboardFundFinanceDataProvider dashboardFundFinanceDataProvider;
    @Resource
    private DashboardFvRepayPrincipalInterestSnapshotService thisService;
    @Resource
    private DashboardFvRepayPrincipalInterestSubService repayPrincipalInterestSubService;

    @Override
    public void generate(Long mainId, LocalDate dataTime) {
        // 获取接口数据
        DashboardFundFinanceRepayREQ query = new DashboardFundFinanceRepayREQ();
        query.setRepayDateFrom(dataTime.with(TemporalAdjusters.firstDayOfYear()));
        query.setRepayDateTo(dataTime.with(TemporalAdjusters.lastDayOfMonth()));
        List<DashboardFundFinanceRepayRSP> dbList = dashboardFundFinanceDataProvider.listRepay(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return;
        }
        List<DashboardFvRepayPrincipalInterestSub> subList = new ArrayList<>();
        dbList.forEach(repayResult -> {
            DashboardFvRepayPrincipalInterestSnapshot rsp = new DashboardFvRepayPrincipalInterestSnapshot();
            rsp.setReceiptRepayCashFlowId(repayResult.getReceiptRepayCashFlowId());
            rsp.setIsDirect(repayResult.getIsDirect());
            rsp.setCardId(mainId);
            rsp.setFinancingId(repayResult.getFinancingId());
            rsp.setFinancingCode(repayResult.getFinancingCode());
            rsp.setWriteOffState(repayResult.getWriteOffState());
            rsp.setWriteOffStateDisplay(Optional.ofNullable(CashFlowState.of(repayResult.getWriteOffState())).map(CashFlowState::display).orElse(""));
            rsp.setOrgName(repayResult.getOrgName());
            rsp.setActualRepayAmount(new BigDecimal(Optional.ofNullable(repayResult.getActualRepayAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            rsp.setRepayBalanceAmount(new BigDecimal(Optional.ofNullable(repayResult.getRepayBalanceAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            rsp.setActualRepayDate(repayResult.getActualRepayDate());
            rsp.setLoanAmount(new BigDecimal(Optional.ofNullable(repayResult.getLoanAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            rsp.setLoanBalanceAmount(new BigDecimal(Optional.ofNullable(repayResult.getLoanBalanceAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            rsp.setRepayInterestAmount(new BigDecimal(Optional.ofNullable(repayResult.getRepayInterestAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            rsp.setRepayPrincipalAmount(new BigDecimal(Optional.ofNullable(repayResult.getRepayPrincipalAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            rsp.setRepayDate(repayResult.getRepayDate());
            rsp.setRepayTotalAmount(new BigDecimal(Optional.ofNullable(repayResult.getRepayTotalAmount()).map(ValueUnitDTO::getValue).orElse("0")));
            thisService.save(rsp);
            subList.addAll(repayResult.getSubListInfo().stream().map(obj -> {
                DashboardFvRepayPrincipalInterestSub sub = new DashboardFvRepayPrincipalInterestSub();
                sub.setRelatedProjName(obj.getRelatedProjName());
                sub.setMainId(rsp.getId());
                sub.setRelatedContractCode(obj.getRelatedContractCode());
                sub.setRentPlanCollectionAmount(Optional.ofNullable(obj.getRentPlanCollectionAmount())
                        .map(ValueUnitDTO::getValue).map(BigDecimal::new).orElse(BigDecimal.ZERO));
                sub.setRentPlanCollectionDate(obj.getRentPlanCollectionDate());
                sub.setBankAccountTypeCode(obj.getBankAccountTypeCode());
                sub.setBankAccountTypeDisplay(obj.getBankAccountTypeDisplay());
                return sub;
            }).collect(Collectors.toList()));
        });
        if (CollUtil.isNotEmpty(subList)) {
            repayPrincipalInterestSubService.saveBatch(subList);
        }
    }

    @Override
    public List<DashboardFundFinanceRepayRSP> listRepay(DashboardFundRepayQuery query) {
        DashboardFvCardSnapshot cardSnapshot = SpringUtil.getBean(DashboardFvCardSnapshotService.class).getOne(Wrappers.<DashboardFvCardSnapshot>lambdaQuery()
                .eq(DashboardFvCardSnapshot::getDataTime, query.getRepayDateTo())
                .eq(DashboardFvCardSnapshot::getCardCode, DashboardCardGroupEnum.FUND_FINANCE_REPAY.name()));
        if (cardSnapshot == null) {
            return Collections.emptyList();
        }
        List<DashboardFvRepayPrincipalInterestSnapshot> snapshots = thisService.list(Wrappers.<DashboardFvRepayPrincipalInterestSnapshot>lambdaQuery()
                .eq(DashboardFvRepayPrincipalInterestSnapshot::getCardId, cardSnapshot.getId())
                .in(CollUtil.isNotEmpty(query.getIds()), DashboardFvRepayPrincipalInterestSnapshot::getId, query.getIds())
                .like(CharSequenceUtil.isNotBlank(query.getFinancingCode()), DashboardFvRepayPrincipalInterestSnapshot::getFinancingCode, query.getFinancingCode())
                .like(CharSequenceUtil.isNotBlank(query.getFinancingName()), DashboardFvRepayPrincipalInterestSnapshot::getOrgName, query.getFinancingName()));

        if (CollUtil.isEmpty(snapshots)) {
            return Collections.emptyList();
        }
        // 获取子列表信息
        List<DashboardFvRepayPrincipalInterestSub> subs = repayPrincipalInterestSubService.list(Wrappers.<DashboardFvRepayPrincipalInterestSub>lambdaQuery()
                .in(DashboardFvRepayPrincipalInterestSub::getMainId, snapshots.stream().map(DashboardFvRepayPrincipalInterestSnapshot::getId).collect(Collectors.toList())));
        Map<Long, List<DashboardFvRepayPrincipalInterestSub>> map = new HashMap<>();
        if (CollUtil.isNotEmpty(subs)) {
            map = subs.stream().collect(Collectors.groupingBy(DashboardFvRepayPrincipalInterestSub::getMainId));
        }
        // 生成结果返回
        Map<Long, List<DashboardFvRepayPrincipalInterestSub>> finalMap = map;
        return snapshots.stream().map(main -> {
            DashboardFundFinanceRepayRSP rsp = new DashboardFundFinanceRepayRSP();
            rsp.setReceiptRepayCashFlowId(main.getReceiptRepayCashFlowId());
            rsp.setIsDirect(main.getIsDirect());
            rsp.setFinancingId(main.getFinancingId());
            rsp.setFinancingCode(main.getFinancingCode());
            rsp.setWriteOffState(main.getWriteOffState());
            rsp.setWriteOffStateDisplay(main.getWriteOffStateDisplay());
            rsp.setOrgName(main.getOrgName());
            rsp.setLoanAmount(Optional.ofNullable(main.getLoanAmount()).map(a -> new ValueUnitDTO(a.toPlainString(), "万元")).orElse(new ValueUnitDTO("0", "万元")));
            rsp.setLoanBalanceAmount(Optional.ofNullable(main.getLoanBalanceAmount()).map(a -> new ValueUnitDTO(a.toPlainString(), "万元")).orElse(new ValueUnitDTO("0", "万元")));
            rsp.setRepayPrincipalAmount(Optional.ofNullable(main.getRepayPrincipalAmount()).map(a -> new ValueUnitDTO(a.toPlainString(), "元")).orElse(new ValueUnitDTO("0", "元")));
            rsp.setRepayInterestAmount(Optional.ofNullable(main.getRepayInterestAmount()).map(a -> new ValueUnitDTO(a.toPlainString(), "元")).orElse(new ValueUnitDTO("0", "元")));
            rsp.setRepayBalanceAmount(Optional.ofNullable(main.getRepayBalanceAmount()).map(a -> new ValueUnitDTO(a.toPlainString(), "元")).orElse(new ValueUnitDTO("0", "元")));
            rsp.setActualRepayAmount(Optional.ofNullable(main.getActualRepayAmount()).map(a -> new ValueUnitDTO(a.toPlainString(), "元")).orElse(new ValueUnitDTO("0", "元")));
            rsp.setRepayTotalAmount(new ValueUnitDTO(main.getRepayTotalAmount().toPlainString(), "元"));
            rsp.setRepayDate(main.getRepayDate());
            rsp.setActualRepayDate(main.getActualRepayDate());
            List<DashboardFvRepayPrincipalInterestSub> subList = finalMap.get(main.getId());
            List<DashboardFundFinanceRepayRSP.SubListInfo> subListInfo = subList.stream().map(sub -> {
                DashboardFundFinanceRepayRSP.SubListInfo info = new DashboardFundFinanceRepayRSP.SubListInfo();
                info.setRelatedProjName(sub.getRelatedProjName());
                info.setRelatedContractCode(sub.getRelatedContractCode());
                info.setRentPlanCollectionAmount(new ValueUnitDTO(Optional.ofNullable(sub.getRentPlanCollectionAmount()).map(BigDecimal::toPlainString).orElse("0"), "元"));
                info.setRentPlanCollectionDate(sub.getRentPlanCollectionDate());
                info.setBankAccountTypeCode(sub.getBankAccountTypeCode());
                info.setBankAccountTypeDisplay(sub.getBankAccountTypeDisplay());
                return info;
            }).collect(Collectors.toList());
            rsp.setSubListInfo(subListInfo);
            return rsp;
        }).collect(Collectors.toList());
    }
}

