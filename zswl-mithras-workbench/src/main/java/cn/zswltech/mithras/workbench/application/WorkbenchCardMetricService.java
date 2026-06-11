package cn.zswltech.mithras.workbench.application;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.workbench.CardMetricChooseDto;
import cn.zswltech.mithras.dto.workbench.WorkBenchRoleListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchCardMetricListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.workbench.application.cardcal.CardCalculator;
import cn.zswltech.mithras.workbench.application.convert.WorkbenchCardMetricConverter;
import cn.zswltech.mithras.workbench.application.job.WorkbenchCardMetricCalculator;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricRole;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricTimeScope;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricUnit;
import cn.zswltech.mithras.workbench.mapper.WorkbenchCardMetricMapper;
import cn.zswltech.mithras.workbench.mapper.model.WorkbenchCardMetric;
import cn.zswltech.mithras.workbench.mapper.model.WorkbenchCardUserRef;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchCardMetricService
        extends ServiceImpl<WorkbenchCardMetricMapper, WorkbenchCardMetric>
        implements WorkbenchCardMetricCalculator {

    public static final String CLIENT_COUNT_CARD_NAME = "本年新增投放客户数";
    public static final String PROJ_ESTABLISH_COUNT_CARD_NAME = "本年新增立项数";
    public static final String PROJ_REVIEW_COUNT_CARD_NAME = "本年新增项目评审数";
    public static final String PROJ_REVIEW_PASS_RATE_CARD_NAME = "本年项目评审通过率";
    public static final String TOTAL_LAUNCH_AMOUNT_CARD_NAME = "本年累计投放金额";
    public static final String REMAINING_PRINCIPAL_CARD_NAME = "剩余本金";
    public static final String INVENTORY_PROJECT_COUNT_CARD_NAME = "存量项目个数";
    public static final String OVERDUE_PROJECT_COUNT_CARD_NAME = "逾期项目数";
    public static final String DEFECTIVE_PROJECT_COUNT_CARD_NAME = "不良项目数";
    public static final String MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME = "本月新增立项数";
    public static final String MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME = "本月累计投放金额";

    @Resource
    private WorkbenchCardMetricConverter cardMetricConverter;
    @Resource
    private WorkbenchCardMetricPort cardMetricPort;
    @Resource
    private List<CardCalculator> cardCalculators;
    @Resource
    private WorkbenchCardUserRefService workbenchCardUserRefService;

    public List<WorkbenchCardMetricListRsp> list(WorkbenchMetricReq req) {
        String scope;
        if (req.getCurrentRoleCode().equals(WorkbenchMetricRole.COMPREHENSIVE_MANAGEMENT.name())) {
            scope = "ZSZL";
        } else {
            scope = req.getCurrentRoleCode().split("_")[0];
        }
        Long userId = AccountUtil.getLoginInfo().getId();
        Set<String> metrics = selectedMetricNames(userId);
        if (metrics.isEmpty()) {
            return Collections.emptyList();
        }
        List<WorkbenchCardMetric> workbenchCardMetrics = list(Wrappers.<WorkbenchCardMetric>lambdaQuery()
                .in(WorkbenchCardMetric::getMetricName, metrics)
                .eq(WorkbenchCardMetric::getScope, scope));
        List<WorkbenchCardMetricListRsp> rsps = new ArrayList<>();
        for (WorkbenchCardMetric metric : workbenchCardMetrics) {
            WorkbenchCardMetricListRsp rsp = cardMetricConverter.entity2ListRsp(metric);
            WorkbenchMetricUnit unit = WorkbenchMetricUnit.valueOf(rsp.getUnit());
            rsp.setUnitDisplay(unit.display());
            rsps.add(rsp);
        }
        return rsps;
    }

    public List<WorkbenchCardMetricListRsp> listBizPersonMetrics() {
        Long userId = AccountUtil.getLoginInfo().getId();
        Set<String> metrics = selectedMetricNames(userId);
        if (metrics.isEmpty()) {
            return Collections.emptyList();
        }
        List<WorkbenchCardMetricListRsp> rsps = new ArrayList<>();
        LocalDate startDay = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());

        if (metrics.contains(CLIENT_COUNT_CARD_NAME)) {
            rsps.add(metricRsp(CLIENT_COUNT_CARD_NAME, "户",
                    cardMetricPort.countFirstLaunchClientsBySponsor(userId, startDay.atStartOfDay())));
        }
        if (metrics.contains(PROJ_ESTABLISH_COUNT_CARD_NAME)) {
            rsps.add(metricRsp(PROJ_ESTABLISH_COUNT_CARD_NAME, "个",
                    cardMetricPort.countProjectEstablishBySponsor(userId, startDay.atStartOfDay())));
        }

        WorkbenchCardReviewStats reviewStats = null;
        if (metrics.contains(PROJ_REVIEW_COUNT_CARD_NAME) || metrics.contains(PROJ_REVIEW_PASS_RATE_CARD_NAME)) {
            reviewStats = cardMetricPort.reviewStatsBySponsor(userId, startDay.atStartOfDay());
        }
        if (metrics.contains(PROJ_REVIEW_COUNT_CARD_NAME)) {
            rsps.add(metricRsp(PROJ_REVIEW_COUNT_CARD_NAME, "个", reviewStats.getReviewCount()));
        }
        if (metrics.contains(PROJ_REVIEW_PASS_RATE_CARD_NAME)) {
            rsps.add(metricRsp(PROJ_REVIEW_PASS_RATE_CARD_NAME, "%", reviewStats.getPassRate()));
        }
        if (metrics.contains(TOTAL_LAUNCH_AMOUNT_CARD_NAME)) {
            rsps.add(metricRsp(TOTAL_LAUNCH_AMOUNT_CARD_NAME, "万元",
                    cardMetricPort.calculateLaunchAmountBySponsor(userId, startDay)));
        }
        if (metrics.contains(REMAINING_PRINCIPAL_CARD_NAME)) {
            rsps.add(metricRsp(REMAINING_PRINCIPAL_CARD_NAME, "万元",
                    cardMetricPort.calculateRemainingPrincipalBySponsor(userId)));
        }
        if (metrics.contains(INVENTORY_PROJECT_COUNT_CARD_NAME)) {
            rsps.add(metricRsp(INVENTORY_PROJECT_COUNT_CARD_NAME, "个",
                    cardMetricPort.countInventoryProjectsBySponsor(userId)));
        }
        if (metrics.contains(OVERDUE_PROJECT_COUNT_CARD_NAME)) {
            rsps.add(metricRsp(OVERDUE_PROJECT_COUNT_CARD_NAME, "个",
                    cardMetricPort.countOverdueProjectsBySponsor(userId)));
        }
        if (metrics.contains(DEFECTIVE_PROJECT_COUNT_CARD_NAME)) {
            rsps.add(metricRsp(DEFECTIVE_PROJECT_COUNT_CARD_NAME, "个",
                    cardMetricPort.countDefectiveProjectsBySponsor(userId)));
        }

        LocalDate monthStartDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        if (metrics.contains(MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME)) {
            rsps.add(metricRsp(MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME, "个",
                    cardMetricPort.countProjectEstablishBySponsor(userId, monthStartDay.atStartOfDay())));
        }
        if (metrics.contains(MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME)) {
            rsps.add(metricRsp(MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME, "万元",
                    cardMetricPort.calculateLaunchAmountBySponsor(userId, monthStartDay)));
        }
        return rsps;
    }

    public WorkBenchRoleListRsp listRole() {
        Set<String> targetRoles = Arrays.stream(WorkbenchMetricRole.values())
                .map(Enum::name).collect(Collectors.toSet());
        List<String> currentUserRoles = cardMetricPort.getCurrentUserRoles();
        currentUserRoles.retainAll(targetRoles);
        WorkBenchRoleListRsp rsp = new WorkBenchRoleListRsp();
        List<SelectRSP> roleList = new ArrayList<>();
        int code = 99;
        for (String roleCode : currentUserRoles) {
            WorkbenchMetricRole workbenchMetricRole = WorkbenchMetricRole.valueOf(roleCode);
            SelectRSP selectRSP = new SelectRSP();
            selectRSP.setLabel(workbenchMetricRole.display());
            selectRSP.setValue(roleCode);
            roleList.add(selectRSP);
            if (code > workbenchMetricRole.sortCode()) {
                rsp.setDefaultRole(selectRSP);
                code = workbenchMetricRole.sortCode();
            }
        }
        rsp.setRoleList(roleList);
        return rsp;
    }

    @Override
    public void calculate() {
        List<WorkbenchCardMetric> workbenchCardMetrics = new ArrayList<>();
        workbenchCardMetrics.addAll(fillMetric(CLIENT_COUNT_CARD_NAME, cardMetricPort.countFirstLaunchClientsByDeptScope()));
        workbenchCardMetrics.addAll(fillMetric(PROJ_ESTABLISH_COUNT_CARD_NAME,
                cardMetricPort.countProjectEstablishByDeptScope(WorkbenchMetricTimeScope.YEARLY)));
        workbenchCardMetrics.addAll(fillReviewMetrics(cardMetricPort.reviewStatsByDeptScope()));
        workbenchCardMetrics.addAll(fillMetric(TOTAL_LAUNCH_AMOUNT_CARD_NAME,
                cardMetricPort.calculateLaunchAmountByDeptScope(WorkbenchMetricTimeScope.YEARLY)));
        workbenchCardMetrics.addAll(fillMetric(REMAINING_PRINCIPAL_CARD_NAME,
                cardMetricPort.calculateRemainingPrincipalByDeptScope()));
        workbenchCardMetrics.addAll(fillMetric(INVENTORY_PROJECT_COUNT_CARD_NAME,
                cardMetricPort.countInventoryProjectsByDeptScope()));
        workbenchCardMetrics.addAll(fillMetric(OVERDUE_PROJECT_COUNT_CARD_NAME,
                cardMetricPort.countOverdueProjectsByDeptScope()));
        workbenchCardMetrics.addAll(fillMetric(DEFECTIVE_PROJECT_COUNT_CARD_NAME,
                cardMetricPort.countDefectiveProjectsByDeptScope()));
        workbenchCardMetrics.addAll(fillMetric(MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME,
                cardMetricPort.countProjectEstablishByDeptScope(WorkbenchMetricTimeScope.MONTHLY)));
        workbenchCardMetrics.addAll(fillMetric(MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME,
                cardMetricPort.calculateLaunchAmountByDeptScope(WorkbenchMetricTimeScope.MONTHLY)));

        Set<String> metricCodes = cardCalculators.stream().map(CardCalculator::metricCode).collect(Collectors.toSet());
        Map<String, WorkbenchCardMetric> metricMap = baseMapper.selectList(Wrappers.<WorkbenchCardMetric>lambdaQuery()
                        .in(WorkbenchCardMetric::getMetricCode, metricCodes)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getMetricCode, v -> v));
        for (CardCalculator cardCalculator : cardCalculators) {
            WorkbenchCardMetric metric = metricMap.get(cardCalculator.metricCode());
            if (metric != null) {
                metric.setValue(cardCalculator.calculate());
            }
        }
        workbenchCardMetrics.addAll(metricMap.values());
        updateBatchById(workbenchCardMetrics);
    }

    public List<CardMetricChooseDto> currentUserChooseVo() {
        List<SelectRSP> selectRsps = listRole().getRoleList();
        if (selectRsps.isEmpty()) {
            return new ArrayList<>();
        }
        String scope = "JSSYB";
        for (SelectRSP selectRsp : selectRsps) {
            if ("COMPREHENSIVE_MANAGEMENT".equals(selectRsp.getValue())) {
                scope = "ZSZL";
                break;
            }
        }
        List<WorkbenchCardMetric> targetCards = list(Wrappers.<WorkbenchCardMetric>lambdaQuery()
                .eq(WorkbenchCardMetric::getScope, scope));

        Long userId = AccountUtil.getLoginInfo().getId();
        Set<String> choosedMetricNames = selectedMetricNames(userId);
        List<CardMetricChooseDto> result = new ArrayList<>();
        for (WorkbenchCardMetric targetCard : targetCards) {
            CardMetricChooseDto vo = new CardMetricChooseDto();
            vo.setMetricName(targetCard.getMetricName());
            vo.setSelected(choosedMetricNames.contains(targetCard.getMetricName()));
            result.add(vo);
        }
        return result;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyChoosedCard(List<CardMetricChooseDto> dtos) {
        Set<String> selectedCards = dtos.stream()
                .filter(CardMetricChooseDto::getSelected)
                .map(CardMetricChooseDto::getMetricName)
                .collect(Collectors.toSet());
        Long userId = AccountUtil.getLoginInfo().getId();
        workbenchCardUserRefService.remove(Wrappers.<WorkbenchCardUserRef>lambdaQuery()
                .eq(WorkbenchCardUserRef::getUserId, userId));

        List<WorkbenchCardUserRef> refs = new ArrayList<>();
        for (String selectedCard : selectedCards) {
            WorkbenchCardUserRef ref = new WorkbenchCardUserRef();
            ref.setUserId(userId);
            ref.setMetricName(selectedCard);
            refs.add(ref);
        }
        workbenchCardUserRefService.saveBatch(refs);
    }

    private Set<String> selectedMetricNames(Long userId) {
        return workbenchCardUserRefService.list(Wrappers.<WorkbenchCardUserRef>lambdaQuery()
                        .eq(WorkbenchCardUserRef::getUserId, userId))
                .stream()
                .map(WorkbenchCardUserRef::getMetricName)
                .collect(Collectors.toSet());
    }

    private WorkbenchCardMetricListRsp metricRsp(String metricName, String unitDisplay, String value) {
        return new WorkbenchCardMetricListRsp()
                .setMetricName(metricName)
                .setUnitDisplay(unitDisplay)
                .setValue(value);
    }

    private List<WorkbenchCardMetric> fillMetric(String metricName, Map<String, String> scopeValues) {
        List<WorkbenchCardMetric> metrics = baseMapper.selectList(
                Wrappers.<WorkbenchCardMetric>lambdaQuery().eq(WorkbenchCardMetric::getMetricName, metricName));
        for (WorkbenchCardMetric metric : metrics) {
            metric.setValue(scopeValues.getOrDefault(metric.getScope(), "0"));
        }
        return metrics;
    }

    private List<WorkbenchCardMetric> fillReviewMetrics(Map<String, WorkbenchCardReviewStats> scopeValues) {
        Map<String, WorkbenchCardMetric> metricMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .in(WorkbenchCardMetric::getMetricName,
                                        PROJ_REVIEW_COUNT_CARD_NAME, PROJ_REVIEW_PASS_RATE_CARD_NAME))
                .stream()
                .collect(Collectors.toMap(metric -> String.join("-", metric.getScope(), metric.getMetricName()), v -> v));
        for (Map.Entry<String, WorkbenchCardReviewStats> entry : scopeValues.entrySet()) {
            WorkbenchCardMetric count = metricMap.get(String.join("-", entry.getKey(), PROJ_REVIEW_COUNT_CARD_NAME));
            if (count != null) {
                count.setValue(entry.getValue().getReviewCount());
            }
            WorkbenchCardMetric passRate = metricMap.get(String.join("-", entry.getKey(), PROJ_REVIEW_PASS_RATE_CARD_NAME));
            if (passRate != null) {
                passRate.setValue(entry.getValue().getPassRate());
            }
        }
        return new ArrayList<>(metricMap.values());
    }
}
