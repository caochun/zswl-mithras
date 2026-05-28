package cn.zswltech.mithras.finance.view.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.finance.view.entity.DashboardFvCardSnapshot;
import cn.zswltech.mithras.finance.view.entity.DashboardFvFinanceInfoSnapshot;
import cn.zswltech.mithras.finance.view.mapper.DashboardFvCardSnapshotMapper;
import cn.zswltech.mithras.finance.view.mapper.DashboardFvFinanceInfoSnapshotMapper;
import cn.zswltech.mithras.finance.view.service.DashboardFvFinanceInfoSnapshotService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.dashboard.DashboardCardGroupEnum;
import cn.zswltech.mithras.service.service.dashboard.DashboardFundFinanceService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 工作台融资情况快照表(DashboardFvFinanceInfoSnapshot)表服务实现类
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
@Service
public class DashboardFvFinanceInfoSnapshotServiceImpl extends ServiceImpl<DashboardFvFinanceInfoSnapshotMapper, DashboardFvFinanceInfoSnapshot> implements DashboardFvFinanceInfoSnapshotService {

    @Resource
    private DashboardFundFinanceService dashboardFundFinanceService;
    @Resource
    private DashboardFvFinanceInfoSnapshotService thisService;
    @Resource
    private DashboardFvCardSnapshotMapper dashboardFvCardSnapshotMapper;

    @Override
    public void generateAll(Long mainId, LocalDate dataTime) {
        // 获取接口数据
        DashboardFundFinanceLoanInfoREQ req = new DashboardFundFinanceLoanInfoREQ();
        req.setIsThisMonth(0);
        req.setIsThisYear(0);
        req.setQueryDate(dataTime);
        List<DashboardFvFinanceInfoSnapshot> list = getList(req, mainId);
        if (CollUtil.isNotEmpty(list)) {
            thisService.saveBatch(list);
        }
    }

    @Override
    public void generateYear(Long mainId, LocalDate dataTime) {
        DashboardFundFinanceLoanInfoREQ req = new DashboardFundFinanceLoanInfoREQ();
        req.setIsThisMonth(0);
        req.setIsThisYear(1);
        List<DashboardFvFinanceInfoSnapshot> list = getList(req, mainId);
        if (CollUtil.isNotEmpty(list)) {
            thisService.saveBatch(list);
        }
    }

    @Override
    public void generateMonth(Long mainId, LocalDate dataTime) {
        DashboardFundFinanceLoanInfoREQ req = new DashboardFundFinanceLoanInfoREQ();
        req.setIsThisMonth(1);
        req.setIsThisYear(0);
        List<DashboardFvFinanceInfoSnapshot> list = getList(req, mainId);
        if (CollUtil.isNotEmpty(list)) {
            thisService.saveBatch(list);
        }
    }

    @Override
    public PageR<DashboardFundFinanceLoanInfoRSP> listLoanInfo(DashboardFundFinanceLoanInfoREQ req) {
        Page<DashboardFvFinanceInfoSnapshot> page = new Page<>(req.getPage(), req.getPageSize());
        List<DashboardFvCardSnapshot> dashboardFvCardSnapshots = dashboardFvCardSnapshotMapper.selectList(Wrappers.<DashboardFvCardSnapshot>lambdaQuery()
                .eq(DashboardFvCardSnapshot::getDataTime, req.getQueryDate()));
        if (CollUtil.isEmpty(dashboardFvCardSnapshots)) {
            return PageR.of(Collections.emptyList(), 0);
        }
        Map<String, DashboardFvCardSnapshot> cardSnapshotMap = dashboardFvCardSnapshots.stream().collect(Collectors.toMap(DashboardFvCardSnapshot::getCardCode, Function.identity(), (a, b) -> b));
        LambdaQueryWrapper<DashboardFvFinanceInfoSnapshot> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(CharSequenceUtil.isNotBlank(req.getOrgName()), DashboardFvFinanceInfoSnapshot::getOrgName, req.getOrgName());
        queryWrapper.eq(CharSequenceUtil.isNotBlank(req.getFinancingTypeCode()), DashboardFvFinanceInfoSnapshot::getFinancingTypeCode, req.getFinancingTypeCode());
        queryWrapper.in(CollUtil.isNotEmpty(req.getIds()), DashboardFvFinanceInfoSnapshot::getId, req.getIds());
        if (Objects.equals(req.getIsThisYear(), YesOrNoNumberEnum.YES.getCode())
                && Objects.equals(req.getIsThisMonth(), YesOrNoNumberEnum.NO.getCode())) {
            DashboardFvCardSnapshot cardSnapshot = cardSnapshotMap.get(DashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_YEAR.name());
            queryWrapper.eq(DashboardFvFinanceInfoSnapshot::getCardId, cardSnapshot.getId());
        } else if (Objects.equals(req.getIsThisMonth(), YesOrNoNumberEnum.YES.getCode())) {
            DashboardFvCardSnapshot cardSnapshot = cardSnapshotMap.get(DashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_MONTH.name());
            queryWrapper.eq(DashboardFvFinanceInfoSnapshot::getCardId, cardSnapshot.getId());
        } else {
            DashboardFvCardSnapshot cardSnapshot = cardSnapshotMap.get(DashboardCardGroupEnum.FUND_FINANCE_LOAN.name());
            queryWrapper.eq(DashboardFvFinanceInfoSnapshot::getCardId, cardSnapshot.getId());
        }
        queryWrapper.orderByDesc(DashboardFvFinanceInfoSnapshot::getActualLoanDate);
        Page<DashboardFvFinanceInfoSnapshot> infoSnapshotList = thisService.page(page, queryWrapper);
        if (infoSnapshotList.getTotal() <= 0) {
            return PageR.of(Collections.emptyList(), 0);
        }
        // 转化
        List<DashboardFundFinanceLoanInfoRSP> rspList = page.getRecords().stream().map(e -> {
            DashboardFundFinanceLoanInfoRSP rsp = new DashboardFundFinanceLoanInfoRSP();
            rsp.setIdKey(String.format("%s_%s", e.getFinancingTypeCode(), e.getFinancingId()));
            rsp.setFinancingCode(e.getFinancingCode());
            rsp.setFinancingTypeCode(e.getFinancingTypeCode());
            rsp.setFinancingTypeDisplay(e.getFinancingTypeDisplay());
            rsp.setOrgName(e.getOrgName());
            rsp.setLoanAmount(Optional.ofNullable(e.getLoanAmount()).map(BigDecimal::toPlainString).orElse("0"));
            rsp.setBalanceAmount(Optional.ofNullable(e.getRemainingPrincipleAmount()).map(BigDecimal::toPlainString).orElse("0"));
            rsp.setInterestRate(Optional.ofNullable(e.getInterestRate()).map(BigDecimal::toPlainString).orElse("0"));
            rsp.setDuration(e.getFinancingMonth());
            rsp.setRepayWay(e.getRepayWay());
            rsp.setRelatedContractCodeList(Optional.ofNullable(e.getPledgeContractCode()).map(a -> JSONUtil.toList(a, String.class)).orElse(Collections.emptyList()));
            rsp.setActualLoanDate(e.getActualLoanDate());
            rsp.setActualLoanDateStr(e.getActualLoanDate() == null ? null : LocalDateTimeUtil.format(e.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setActualExpireDate(e.getActualExpireDate());
            rsp.setActualExpireDateStr(e.getActualExpireDate() == null ? null : LocalDateTimeUtil.format(e.getActualExpireDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setComprehensiveFinancingCost(Optional.ofNullable(e.getComprehensiveInterestRate()).map(BigDecimal::toPlainString).orElse("0"));
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(rspList, infoSnapshotList.getTotal());
    }

    private List<DashboardFvFinanceInfoSnapshot> getList(DashboardFundFinanceLoanInfoREQ req, Long mainId) {
        req.setPageSize(10000);
        PageR<DashboardFundFinanceLoanInfoRSP> pageR = dashboardFundFinanceService.listLoanInfo(req);
        if (CollUtil.isEmpty(pageR.getList())) {
            return Collections.emptyList();
        }
        return pageR.getList().stream().map(item -> {
            DashboardFvFinanceInfoSnapshot snapshot = new DashboardFvFinanceInfoSnapshot();
            snapshot.setCardId(mainId);
            Long financeId = Optional.ofNullable(item.getIdKey()).map(idKey -> idKey.substring(idKey.lastIndexOf('_') + 1))
                    .map(Long::valueOf).orElse(null);
            snapshot.setFinancingId(financeId);
            snapshot.setFinancingCode(item.getFinancingCode());
            snapshot.setOrgName(item.getOrgName());
            snapshot.setFinancingTypeCode(item.getFinancingTypeCode());
            snapshot.setFinancingTypeDisplay(item.getFinancingTypeDisplay());
            snapshot.setFinancingMonth(item.getDuration());
            snapshot.setRepayWay(item.getRepayWay());
            snapshot.setLoanAmount(new BigDecimal(item.getLoanAmount()));
            snapshot.setRemainingPrincipleAmount(new BigDecimal(item.getBalanceAmount()));
            snapshot.setComprehensiveInterestRate(new BigDecimal(item.getComprehensiveFinancingCost()));
            snapshot.setInterestRate(new BigDecimal(item.getInterestRate()));
            snapshot.setActualLoanDate(item.getActualLoanDate());
            snapshot.setActualExpireDate(item.getActualExpireDate());
            snapshot.setPledgeContractCode(JSONUtil.toJsonStr(item.getRelatedContractCodeList()));
            return snapshot;
        }).collect(Collectors.toList());
    }
}

