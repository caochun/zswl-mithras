package cn.zswltech.mithras.finance.view.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBaseREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceStatisticsRSP;
import cn.zswltech.mithras.finance.view.entity.*;
import cn.zswltech.mithras.finance.view.mapper.DashboardFvCardSnapshotMapper;
import cn.zswltech.mithras.finance.view.service.*;
import cn.zswltech.mithras.finance.view.enums.FinanceDashboardCardGroupEnum;
import cn.zswltech.mithras.finance.view.service.DashboardFundFinanceDataProvider;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 工作台融资视图卡片快照信息表(DashboardFvCardSnapshot)表服务实现类
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
@Slf4j
@Service
public class DashboardFvCardSnapshotServiceImpl extends ServiceImpl<DashboardFvCardSnapshotMapper, DashboardFvCardSnapshot> implements DashboardFvCardSnapshotService {

    @Resource
    private DashboardFvCardSnapshotService thisService;
    @Resource
    private DashboardFundFinanceDataProvider dashboardFundFinanceDataProvider;
    @Resource
    private DashboardFvCreditInfoSnapshotService creditInfoSnapshotService;
    @Resource
    private DashboardFvFinanceInfoSnapshotService financeInfoSnapshotService;
    @Resource
    private DashboardFvFinancingCostSnapshotService financingCostSnapshotService;
    @Resource
    private DashboardFvRepayPrincipalInterestSnapshotService repayPrincipalInterestSnapshotService;
    @Resource
    private DashboardFvRepayPrincipalInterestSubService repayPrincipalInterestSubService;

    @Override
    public void generate(LocalDate dataTime) {
        log.info("开始生成融资视图快照信息..........");
        try {
            DashboardFundFinanceBaseREQ baseReq = new DashboardFundFinanceBaseREQ();
            baseReq.setQueryDate(dataTime);
            baseReq.setPageSize(100);
            Map<String, DashboardFundFinanceStatisticsRSP> map = dashboardFundFinanceDataProvider.statisticsList(baseReq).stream().collect(Collectors.toMap(DashboardFundFinanceStatisticsRSP::getGroupCode, Function.identity()));
            // 查询已经存在的数据
            Map<String, DashboardFvCardSnapshot> existMap = new HashMap<>();
            List<DashboardFvCardSnapshot> snapshotList = thisService.list(Wrappers.<DashboardFvCardSnapshot>lambdaQuery()
                    .eq(DashboardFvCardSnapshot::getDataTime, dataTime));
            if (CollUtil.isNotEmpty(snapshotList)) {
                existMap = snapshotList.stream().collect(Collectors.toMap(DashboardFvCardSnapshot::getCardCode, Function.identity()));
            }
            // 建立6个completableFuture对象
            Map<String, DashboardFvCardSnapshot> finalExistMap = existMap;
            CompletableFuture<Void> repayPrincipalInterestSnapshotFuture = CompletableFuture.runAsync(() -> {
                log.info("开始生成还本付息快照信息..........");
                try {
                    DashboardFundFinanceStatisticsRSP rsp = map.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_REPAY.name());
                    Long mainId = generateDashboardFvCardSnapshot(FinanceDashboardCardGroupEnum.FUND_FINANCE_REPAY, dataTime, JSONUtil.toJsonStr(rsp));
                    repayPrincipalInterestSnapshotService.generate(mainId, dataTime);
                    // 尝试删除老数据
                    DashboardFvCardSnapshot cardSnapshot = finalExistMap.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_REPAY.name());
                    if (Objects.nonNull(cardSnapshot)) {
                        thisService.removeById(cardSnapshot.getId());
                        // 删除子表数据
                        LambdaQueryWrapper<DashboardFvRepayPrincipalInterestSnapshot> queryWrapper = Wrappers.<DashboardFvRepayPrincipalInterestSnapshot>lambdaQuery()
                                .eq(DashboardFvRepayPrincipalInterestSnapshot::getCardId, cardSnapshot.getId());
                        List<DashboardFvRepayPrincipalInterestSnapshot> listed = repayPrincipalInterestSnapshotService.list(queryWrapper);
                        if (CollUtil.isNotEmpty(listed)) {
                            repayPrincipalInterestSubService.remove(Wrappers.<DashboardFvRepayPrincipalInterestSub>lambdaQuery()
                                    .in(DashboardFvRepayPrincipalInterestSub::getMainId, listed.stream().map(DashboardFvRepayPrincipalInterestSnapshot::getId).collect(Collectors.toList())));
                        }
                        repayPrincipalInterestSnapshotService.remove(queryWrapper);
                    }
                    log.info("生成还本付息快照信息结束..........");
                } catch (Exception e) {
                    log.error("生成还本付息快照信息失败", e);
                }
            });
            CompletableFuture<Void> creditInfoSnapshotFuture = CompletableFuture.runAsync(() -> {
                log.info("生成授信信息快照信息开始..........");
                try {
                    DashboardFundFinanceStatisticsRSP rsp = map.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_CREDIT.name());
                    Long mainId = generateDashboardFvCardSnapshot(FinanceDashboardCardGroupEnum.FUND_FINANCE_CREDIT, dataTime, JSONUtil.toJsonStr(rsp));
                    creditInfoSnapshotService.generate(mainId, dataTime);
                    // 尝试删除老数据
                    DashboardFvCardSnapshot cardSnapshot = finalExistMap.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_CREDIT.name());
                    if (Objects.nonNull(cardSnapshot)) {
                        thisService.removeById(cardSnapshot.getId());
                        LambdaQueryWrapper<DashboardFvCreditInfoSnapshot> queryWrapper = Wrappers.<DashboardFvCreditInfoSnapshot>lambdaQuery()
                                .eq(DashboardFvCreditInfoSnapshot::getCardId, cardSnapshot.getId());
                        creditInfoSnapshotService.remove(queryWrapper);
                    }
                    log.info("生成授信信息快照信息结束..........");
                } catch (Exception e) {
                    log.error("生成授信信息快照信息失败", e);
                }
            });
            CompletableFuture<Void> financeCostSnapshotFuture = CompletableFuture.runAsync(() -> {
                log.info("生成资金成本快照信息开始..........");
                try {
                    DashboardFundFinanceStatisticsRSP rsp = map.get(FinanceDashboardCardGroupEnum.FOND_FINANCE_COST_FOUNDS.name());
                    Long mainId = generateDashboardFvCardSnapshot(FinanceDashboardCardGroupEnum.FOND_FINANCE_COST_FOUNDS, dataTime, JSONUtil.toJsonStr(rsp));
                    financingCostSnapshotService.generate(mainId, dataTime);
                    // 尝试删除老数据
                    DashboardFvCardSnapshot cardSnapshot = finalExistMap.get(FinanceDashboardCardGroupEnum.FOND_FINANCE_COST_FOUNDS.name());
                    if (Objects.nonNull(cardSnapshot)) {
                        thisService.removeById(cardSnapshot.getId());
                        // 删除子表信息
                        financingCostSnapshotService.remove(Wrappers.<DashboardFvFinancingCostSnapshot>lambdaQuery().eq(DashboardFvFinancingCostSnapshot::getCardId, cardSnapshot.getId()));
                    }
                    log.info("生成资金成本快照信息结束..........");
                } catch (Exception e) {
                    log.error("生成资金成本快照信息失败", e);
                }
            });
            CompletableFuture<Void> financeInfoSnapshotAllFuture = CompletableFuture.runAsync(() -> {
                log.info("生成融资信息（存量）快照信息开始..........");
                try {
                    DashboardFundFinanceStatisticsRSP rsp = map.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN.name());
                    Long mainId = generateDashboardFvCardSnapshot(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN, dataTime, JSONUtil.toJsonStr(rsp));
                    financeInfoSnapshotService.generateAll(mainId, dataTime);
                    // 尝试删除老数据
                    DashboardFvCardSnapshot cardSnapshot = finalExistMap.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN.name());
                    if (Objects.nonNull(cardSnapshot)) {
                        thisService.removeById(cardSnapshot.getId());
                        // 删除子表信息
                        financeInfoSnapshotService.remove(Wrappers.<DashboardFvFinanceInfoSnapshot>lambdaQuery().eq(DashboardFvFinanceInfoSnapshot::getCardId, cardSnapshot.getId()));
                    }
                    log.info("生成融资信息（存量）快照信息结束..........");
                } catch (Exception e) {
                    log.error("生成融资信息（存量）快照信息出错，错误信息：%s", e);
                }
            });
            CompletableFuture<Void> financeInfoSnapshotYearFuture = CompletableFuture.runAsync(() -> {
                log.info("生成融资信息（当年）快照信息开始");
                try {
                    DashboardFundFinanceStatisticsRSP rsp = map.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_YEAR.name());
                    Long mainId = generateDashboardFvCardSnapshot(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_YEAR, dataTime, JSONUtil.toJsonStr(rsp));
                    financeInfoSnapshotService.generateYear(mainId, dataTime);
                    // 尝试删除老数据
                    DashboardFvCardSnapshot cardSnapshot = finalExistMap.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_YEAR.name());
                    if (Objects.nonNull(cardSnapshot)) {
                        thisService.removeById(cardSnapshot.getId());
                        // 删除子表信息
                        financeInfoSnapshotService.remove(Wrappers.<DashboardFvFinanceInfoSnapshot>lambdaQuery().eq(DashboardFvFinanceInfoSnapshot::getCardId, cardSnapshot.getId()));
                    }
                } catch (Exception e) {
                    log.error("生成融资信息（当年）快照信息出错", e);
                }
            });
            CompletableFuture<Void> financeInfoSnapshotMonthFuture = CompletableFuture.runAsync(() -> {
                log.info("生成融资信息（当月）快照信息开始..........");
                try {
                    DashboardFundFinanceStatisticsRSP rsp = map.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_MONTH.name());
                    Long mainId = generateDashboardFvCardSnapshot(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_MONTH, dataTime, JSONUtil.toJsonStr(rsp));
                    financeInfoSnapshotService.generateMonth(mainId, dataTime);
                    // 尝试删除老数据
                    DashboardFvCardSnapshot cardSnapshot = finalExistMap.get(FinanceDashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_MONTH.name());
                    if (Objects.nonNull(cardSnapshot)) {
                        thisService.removeById(cardSnapshot.getId());
                        // 删除子表信息
                        financeInfoSnapshotService.remove(Wrappers.<DashboardFvFinanceInfoSnapshot>lambdaQuery().eq(DashboardFvFinanceInfoSnapshot::getCardId, cardSnapshot.getId()));
                    }
                    log.info("融资信息（当月）快照信息生成成功.........");
                } catch (Exception e) {
                    log.error("生成融资信息（当月）快照信息失败", e);
                    log.info("生成融资信息（当月）重试参数： {}", dataTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                }
            });

            // 等待多线程结束
            List<CompletableFuture<Void>> futureList = ListUtil.of(repayPrincipalInterestSnapshotFuture, creditInfoSnapshotFuture, financeCostSnapshotFuture,
                    financeInfoSnapshotAllFuture, financeInfoSnapshotYearFuture, financeInfoSnapshotMonthFuture);
            // 使用new CompletableFuture[0]是为了指定toArray方法返回的数组类型
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("多线程生成融资视图快照信息异常.........  ", e);
        }
        log.info("多线程生成融资视图快照信息完成.........  ");
    }

    private Long generateDashboardFvCardSnapshot(FinanceDashboardCardGroupEnum cardGroup,
                                                 LocalDate dataTime,
                                                 Object data) {
        return thisService.generateDashboardFvCardSnapshot(cardGroup.name(), cardGroup.getDisplay(), dataTime, data);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long generateDashboardFvCardSnapshot(String cardCode,
                                                String cardName,
                                                LocalDate dataTime,
                                                Object data) {
        DashboardFvCardSnapshot dashboardFvCardSnapshot = new DashboardFvCardSnapshot();
        dashboardFvCardSnapshot.setDataTime(dataTime);
        dashboardFvCardSnapshot.setCardCode(cardCode);
        dashboardFvCardSnapshot.setCardName(cardName);
        dashboardFvCardSnapshot.setCardData(JSONUtil.toJsonStr(data));
        baseMapper.insert(dashboardFvCardSnapshot);
        return dashboardFvCardSnapshot.getId();
    }

    @Override
    public List<DashboardFundFinanceStatisticsRSP> statisticsList(LocalDate queryDate) {
        List<DashboardFvCardSnapshot> snapshotList = thisService.list(Wrappers.<DashboardFvCardSnapshot>lambdaQuery()
                .eq(DashboardFvCardSnapshot::getDataTime, queryDate));
        if (CollUtil.isEmpty(snapshotList)) {
            return Collections.emptyList();
        }
        return snapshotList.stream().map(snapshot -> JSONUtil.toBean(snapshot.getCardData(), DashboardFundFinanceStatisticsRSP.class))
                .peek(e -> e.setSort(Optional.ofNullable(FinanceDashboardCardGroupEnum.ofName(e.getGroupCode()))
                        .map(FinanceDashboardCardGroupEnum::getSort).orElse(0))).collect(Collectors.toList());
    }
}

