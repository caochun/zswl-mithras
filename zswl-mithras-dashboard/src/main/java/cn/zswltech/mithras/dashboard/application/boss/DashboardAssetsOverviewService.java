package cn.zswltech.mithras.dashboard.application.boss;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.dashboard.boss.AssetsOverviewDetailListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.BalanceOverviewRSP;
import cn.zswltech.mithras.dto.dashboard.boss.LoanOverviewRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dashboard.enums.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.AssetsOverviewByCityDTO;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.AssetsOverviewItemDTO;
import cn.zswltech.sleipnir.toolkit.request.guanyuan.GuanYuanDSRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/5/16/14:40
 * @description 资产总览
 */
@Slf4j
@Service
public class DashboardAssetsOverviewService extends GuanYuanBasicService {
    private static final List<String> CHANG_SAN_JIAO_CITY_CODE = ListUtil.of("310100", "320100", "320200", "320300", "320400", "320500", "320600", "320700", "320800", "320900", "321000", "321100", "321200", "321300", "330100", "330200", "330300", "330400", "330500", "330600", "330700", "330800", "330900", "331000", "331100", "340100", "340200", "340300", "340400", "340500", "340600", "340700", "340800", "341000", "341100", "341200", "341300", "341500", "341600", "341700", "341800");
    private static final List<String> ZHU_SAN_JIAO_CITY_CODE = ListUtil.of("440100", "440300", "440600", "441900", "442000", "440400", "440700", "441200", "441300");

    public BalanceOverviewRSP getBalanceOverview() {
        List<AssetsOverviewItemDTO> itemList = this.listItemFromGuanYuan();
        // 定义并取出所需字段
        long balanceGoal = 0L;
        long totalPayAmount = 0L;
        long totalCollectPrincipalAmount = 0L;
        long lastYearPayAmount = 0L;
        long lastYearCollectPrincipalAmount = 0L;
        long thisMonthPayAmount = 0L;
        long thisMonthCollectPrincipalAmount = 0L;
        for (AssetsOverviewItemDTO item : itemList) {
            if (Objects.equals(item.getItemName(), "总投放") && Objects.nonNull(item.getItemAmount())) {
                totalPayAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "去年同期历史投放") && Objects.nonNull(item.getItemAmount())) {
                lastYearPayAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "本月投放") && Objects.nonNull(item.getItemAmount())) {
                thisMonthPayAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "总已收本金") && Objects.nonNull(item.getItemAmount())) {
                totalCollectPrincipalAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "去年同期历史已收本金") && Objects.nonNull(item.getItemAmount())) {
                lastYearCollectPrincipalAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "本月已收本金") && Objects.nonNull(item.getItemAmount())) {
                thisMonthCollectPrincipalAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "本年资产余额目标") && Objects.nonNull(item.getItemAmount())) {
                balanceGoal = item.getItemAmount();
            }
        }
        // 计算并返回结果
        long balance = totalPayAmount - totalCollectPrincipalAmount;
        long balanceLastYear = lastYearPayAmount - lastYearCollectPrincipalAmount;
        long balanceThisMonth = thisMonthPayAmount - thisMonthCollectPrincipalAmount;
        BalanceOverviewRSP rsp = new BalanceOverviewRSP();
        rsp.setAssetsBalance(new ValueUnitDTO(BigDecimal.valueOf(balance).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        rsp.setAssetsBalanceLastYear(new ValueUnitDTO(BigDecimal.valueOf(balanceLastYear).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        rsp.setAssetsBalanceIncrementThisMonth(new ValueUnitDTO(BigDecimal.valueOf(balanceThisMonth).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        if (balanceLastYear > 0) {
            rsp.setAssetsBalanceYearOnYearBasis(new ValueUnitDTO(BigDecimal.valueOf(balance - balanceLastYear).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(balanceLastYear), 2, RoundingMode.HALF_UP).toPlainString(), "%"));
        }
        rsp.setAssetsBalanceGoalThisYear(new ValueUnitDTO(BigDecimal.valueOf(balanceGoal).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        if (balanceGoal > 0) {
            rsp.setCompletionPercentThisYear(new ValueUnitDTO(BigDecimal.valueOf(balance).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(balanceGoal), 2, RoundingMode.HALF_UP).toPlainString(), "%"));
        }
        return rsp;
    }

    public LoanOverviewRSP getLoanOverview() {
        List<AssetsOverviewItemDTO> itemList = this.listItemFromGuanYuan();
        // 定义并取出所需字段
        long thisYearPayAmount = 0L;
        long lastYearPayAmount = 0L;
        long thisMonthPayAmount = 0L;
        long payGoal = 0L;
        for (AssetsOverviewItemDTO item : itemList) {
            if (Objects.equals(item.getItemName(), "本年投放") && Objects.nonNull(item.getItemAmount())) {
                thisYearPayAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "本月投放") && Objects.nonNull(item.getItemAmount())) {
                thisMonthPayAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "去年同期总投放") && Objects.nonNull(item.getItemAmount())) {
                lastYearPayAmount = item.getItemAmount();
            }
            if (Objects.equals(item.getItemName(), "本年投放目标") && Objects.nonNull(item.getItemAmount())) {
                payGoal = item.getItemAmount();
            }
        }
        // 计算并返回结果
        LoanOverviewRSP rsp = new LoanOverviewRSP();
        rsp.setLoanThisYear(new ValueUnitDTO(BigDecimal.valueOf(thisYearPayAmount).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        rsp.setLoanLastYear(new ValueUnitDTO(BigDecimal.valueOf(lastYearPayAmount).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        rsp.setLoanGoalThisYear(new ValueUnitDTO(BigDecimal.valueOf(payGoal).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        rsp.setLoanIncrementThisMonth(new ValueUnitDTO(BigDecimal.valueOf(thisMonthPayAmount).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString(), "亿元"));
        if (lastYearPayAmount != 0) {
            rsp.setLoanYearOnYearBasis(new ValueUnitDTO(BigDecimal.valueOf(thisYearPayAmount - lastYearPayAmount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(lastYearPayAmount), 2, RoundingMode.HALF_UP).toPlainString(), "%"));
        }
        if (payGoal != 0) {
            rsp.setCompletionPercentThisYear(new ValueUnitDTO(BigDecimal.valueOf(thisYearPayAmount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(payGoal), 2, RoundingMode.HALF_UP).toPlainString(), "%"));
        }
        return rsp;
    }

    public List<AssetsOverviewDetailListRSP> listByProvince() {
        List<AssetsOverviewByCityDTO> guanyuanDataList = this.listGroupByCityFromGuanYuan();
        if (CollectionUtil.isEmpty(guanyuanDataList)) {
            return Collections.emptyList();
        }
        // 计算总资产余额
        long total = guanyuanDataList.stream().filter(e -> Objects.nonNull(e.getCityRemainingAmount())).mapToLong(AssetsOverviewByCityDTO::getCityRemainingAmount).sum();
        // 根据省份分组
        Map<String, List<AssetsOverviewByCityDTO>> map = guanyuanDataList.stream().collect(Collectors.groupingBy(AssetsOverviewByCityDTO::getProvinceDisplay));
        return this.convert(total, map);
    }

    public List<AssetsOverviewDetailListRSP> listByArea() {
        List<AssetsOverviewByCityDTO> guanyuanDataList = this.listGroupByCityFromGuanYuan();
        if (CollectionUtil.isEmpty(guanyuanDataList)) {
            return Collections.emptyList();
        }
        // 计算总资产余额
        long total = guanyuanDataList.stream().filter(e -> Objects.nonNull(e.getCityRemainingAmount())).mapToLong(AssetsOverviewByCityDTO::getCityRemainingAmount).sum();
        // 根据经济圈分组
        Map<String, List<AssetsOverviewByCityDTO>> map = guanyuanDataList.stream().collect(Collectors.groupingBy(e -> {
            if (Objects.nonNull(e.getCityCode()) && CHANG_SAN_JIAO_CITY_CODE.contains(e.getCityCode())) {
                return "长三角";
            } else if (Objects.nonNull(e.getCityCode()) && ZHU_SAN_JIAO_CITY_CODE.contains(e.getCityCode())) {
                return "珠三角";
            } else {
                return "其他";
            }
        }));
        return this.convert(total, map);
    }

    private List<AssetsOverviewDetailListRSP> convert(long total, Map<String, List<AssetsOverviewByCityDTO>> map) {
        List<AssetsOverviewDetailListRSP> result = new LinkedList<>();
        for (Map.Entry<String, List<AssetsOverviewByCityDTO>> entry : map.entrySet()) {
            List<AssetsOverviewByCityDTO> list = entry.getValue();
            String name = entry.getKey();
            long remainingAmount = 0L;
            long payAmountThisYear = 0L;
            int continueClient = 0;
            int continueProject = 0;
            for (AssetsOverviewByCityDTO item : list) {
                remainingAmount += Optional.ofNullable(item.getCityRemainingAmount()).orElse(0L);
                payAmountThisYear += Optional.ofNullable(item.getCityPayAmountThisYear()).orElse(0L);
                continueProject += Optional.ofNullable(item.getCityContinueProjectQuantity()).orElse(0);
                continueClient += Optional.ofNullable(item.getCityContinueClientQuantity()).orElse(0);
            }
            AssetsOverviewDetailListRSP rsp = new AssetsOverviewDetailListRSP();
            rsp.setDimensionality(name);
            rsp.setAssetsBalanceL(remainingAmount);
            rsp.setAssetsBalance(new ValueUnitDTO(BigDecimal.valueOf(remainingAmount).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString(), "万元"));
            rsp.setLoanThisYear(new ValueUnitDTO(BigDecimal.valueOf(payAmountThisYear).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString(), "万元"));
            rsp.setStockProjectQuantity(continueProject);
            rsp.setStockClientQuantity(continueClient);
            // 计算占比
            if (total != 0) {
                BigDecimal b = BigDecimal.valueOf(remainingAmount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
                rsp.setAssetsProportion(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            result.add(rsp);
        }
        // 排序
        result.sort(Comparator.comparing(AssetsOverviewDetailListRSP::getAssetsBalanceL).reversed());
        // 编号
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }
        return result;
    }

    private List<AssetsOverviewItemDTO> listItemFromGuanYuan() {
        return this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.AssetsOverviewByCompany, new GuanYuanDSRequest.Body(), AssetsOverviewItemDTO.class);
    }

    private List<AssetsOverviewByCityDTO> listGroupByCityFromGuanYuan() {
        return this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.AssetsOverviewGroupByCityByCompany, new GuanYuanDSRequest.Body(), AssetsOverviewByCityDTO.class);
    }
}
