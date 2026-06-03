package cn.zswltech.mithras.service.service.dashboard.boss;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.dashboard.boss.AssetsFiveClassifyListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.dashboard.domain.enums.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.AssetClassifyStatisticsDTO;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.AssetClassifyYearQuarterDTO;
import cn.zswltech.sleipnir.toolkit.enums.GuanYuanFilterTypeEnum;
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
 * @description 资产五级分类
 */
@Slf4j
@Service
public class DashboardAssetsFiveClassifyService extends GuanYuanBasicService {
    public List<AssetsFiveClassifyListRSP> list() {
        // 获取所有已完成的五级分类所属的年份和季度
        List<AssetClassifyYearQuarterDTO> yearQuartList = this.listFinishedYearQuarter();
        if (CollectionUtil.isEmpty(yearQuartList)) {
            return Collections.emptyList();
        }
        // 排序
        yearQuartList.sort(Comparator.comparing(AssetClassifyYearQuarterDTO::getYear).thenComparing(AssetClassifyYearQuarterDTO::getQuarter).reversed());
        // 拿到最近两次的数据
        AssetClassifyYearQuarterDTO yearQuarter = yearQuartList.get(0);
        List<AssetClassifyStatisticsDTO> newList = this.listByYearQuarter(yearQuarter.getYear(), yearQuarter.getQuarter());
        List<AssetClassifyStatisticsDTO> oldList;
        if (yearQuartList.size() > 1) {
            AssetClassifyYearQuarterDTO lastYearQuarter = yearQuartList.get(1);
            oldList = this.listByYearQuarter(lastYearQuarter.getYear(), lastYearQuarter.getQuarter());
        } else {
            oldList = Collections.emptyList();
        }
        // 保存总数
        //long newRiskExposureTotal = newList.stream().filter(e -> Objects.nonNull(e.getStockRiskExposureTotal())).mapToLong(AssetClassifyStatisticsDTO::getStockRiskExposureTotal).sum();
        long newAssetBalanceTotal = newList.stream().filter(e -> Objects.nonNull(e.getAssetBalanceTotal())).mapToLong(AssetClassifyStatisticsDTO::getAssetBalanceTotal).sum();
        // 按类型分组
        Map<String, AssetClassifyStatisticsDTO> newMap = newList.stream().collect(Collectors.toMap(AssetClassifyStatisticsDTO::getClassifyResult, e -> e));
        Map<String, AssetClassifyStatisticsDTO> oldMap = oldList.stream().collect(Collectors.toMap(AssetClassifyStatisticsDTO::getClassifyResult, e -> e));
        // 处理数据
        List<AssetsFiveClassifyListRSP> result = new LinkedList<>();
        for (AssetClassifyResultEnum item : AssetClassifyResultEnum.values()) {
            AssetsFiveClassifyListRSP rsp = new AssetsFiveClassifyListRSP();
            rsp.setAssetClassifyResultCode(item.name());
            rsp.setAssetClassifyResultDisplay(item.getDisplay());
            int newQuantity = Optional.ofNullable(newMap.get(item.name())).map(AssetClassifyStatisticsDTO::getQuantity).orElse(0);
            rsp.setQuantity(newQuantity);
            // 风险敞口
            long newRiskExposure = Optional.ofNullable(newMap.get(item.name())).map(AssetClassifyStatisticsDTO::getStockRiskExposureTotal).orElse(0L);
            //long oldRiskExposure = Optional.ofNullable(oldMap.get(item.name())).map(AssetClassifyStatisticsDTO::getStockRiskExposureTotal).orElse(0L);
            //资产余额
            long newAssetBalance = Optional.ofNullable(newMap.get(item.name())).map(AssetClassifyStatisticsDTO::getAssetBalanceTotal).orElse(0L);
            long oldAssetBalance = Optional.ofNullable(oldMap.get(item.name())).map(AssetClassifyStatisticsDTO::getAssetBalanceTotal).orElse(0L);

            BigDecimal riskExposureBD = BigDecimal
                    .valueOf(newRiskExposure)
                    .divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP);
            rsp.setRiskExposure(new ValueUnitDTO(riskExposureBD.toPlainString(), "亿"));
            BigDecimal assetBalanceBD = BigDecimal
                    .valueOf(newAssetBalance)
                    .divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP);
            rsp.setAssetBalance(new ValueUnitDTO(assetBalanceBD.toPlainString(), "亿"));

            // 本次占比
            /*if (newRiskExposureTotal != 0) {
                BigDecimal newProportionBD = BigDecimal.valueOf(newRiskExposure).divide(BigDecimal.valueOf(newRiskExposureTotal), 20, RoundingMode.HALF_UP);
                rsp.setProportion(new ValueUnitDTO(newProportionBD.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
            }*/
            if (newAssetBalanceTotal != 0) {
                BigDecimal newProportionBD = BigDecimal.valueOf(newAssetBalance).divide(BigDecimal.valueOf(newAssetBalanceTotal), 20, RoundingMode.HALF_UP);
                rsp.setProportion(new ValueUnitDTO(newProportionBD.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
            }

            // 环比
            /*if (oldRiskExposure != 0) {
                BigDecimal chainRatioBD = BigDecimal.valueOf(newRiskExposure - oldRiskExposure).divide(BigDecimal.valueOf(oldRiskExposure), 20, RoundingMode.HALF_UP);
                rsp.setChainRatio(new ValueUnitDTO(chainRatioBD.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
            }*/
            if (oldAssetBalance != 0) {
                BigDecimal chainRatioBD = BigDecimal.valueOf(newAssetBalance - oldAssetBalance).divide(BigDecimal.valueOf(oldAssetBalance), 20, RoundingMode.HALF_UP);
                rsp.setChainRatio(new ValueUnitDTO(chainRatioBD.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
            }

            result.add(rsp);
        }
        return result;
    }

    private List<AssetClassifyYearQuarterDTO> listFinishedYearQuarter() {
        String dsId = guanYuanDsInfoService.getGuanYuanDsId(BossDashboardGuanYuanDataSourceKeyEnum.AssetsFiveClassifyYearQuarter);
        if (StrUtil.isBlank(dsId)) {
            throw new MithrasException("数据集配置不存在[" + BossDashboardGuanYuanDataSourceKeyEnum.AssetsFiveClassifyYearQuarter.getDisplay() + "]");
        }
        List<Map<String, String>> sourceList = getData(dsId, new GuanYuanDSRequest.Body());
        return sourceList.stream().map(e -> {
            AssetClassifyYearQuarterDTO assetClassifyYearQuarterDTO = new AssetClassifyYearQuarterDTO();
            assetClassifyYearQuarterDTO.populate(e);
            return assetClassifyYearQuarterDTO;
        }).collect(Collectors.toList());
    }

    private List<AssetClassifyStatisticsDTO> listByYearQuarter(Integer year, Integer quarter) {
        // TODO 先取公司维度统计的，后续如果要做权限，需要根据当前登录用户的身份来选用不同的数据集
        GuanYuanDSRequest.Filter filter1 = new GuanYuanDSRequest.Filter();
        filter1.setName("year");
        filter1.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
        filter1.setFilterValue(Collections.singletonList(year.toString()));
        GuanYuanDSRequest.Filter filter2 = new GuanYuanDSRequest.Filter();
        filter2.setName("quarter");
        filter2.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
        filter2.setFilterValue(Collections.singletonList(quarter.toString()));
        GuanYuanDSRequest.Body body = new GuanYuanDSRequest.Body();
        body.setFilters(ListUtil.of(filter1, filter2));
        return this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.AssetsFiveClassifyStatisticsByCompany, body, AssetClassifyStatisticsDTO.class);
    }
}
