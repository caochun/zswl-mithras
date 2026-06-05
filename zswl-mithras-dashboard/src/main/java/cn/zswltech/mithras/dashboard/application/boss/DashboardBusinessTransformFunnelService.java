package cn.zswltech.mithras.dashboard.application.boss;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.dashboard.boss.BusinessTransformFunnelListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dashboard.domain.enums.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.BusinessStageStatisticsDTO;
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
 * @description 业务转化漏斗
 */
@Slf4j
@Service
public class DashboardBusinessTransformFunnelService extends GuanYuanBasicService {
    private static final List<String> STAGE_LIST = ListUtil.of("访客", "立项", "尽调", "评审", "签约", "投放");

    public List<BusinessTransformFunnelListRSP> listBusinessTransformFunnel() {
        // 查询数据
        List<BusinessStageStatisticsDTO> all = this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.BusinessTransformFunnelAllByCompany, new GuanYuanDSRequest.Body(), BusinessStageStatisticsDTO.class);
        List<BusinessStageStatisticsDTO> thisYear = this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.BusinessTransformFunnelThisYearByCompany, new GuanYuanDSRequest.Body(), BusinessStageStatisticsDTO.class);
        Map<String, BusinessStageStatisticsDTO> allMap = all.stream().collect(Collectors.toMap(BusinessStageStatisticsDTO::getStage, e -> e));
        Map<String, BusinessStageStatisticsDTO> thisYearMap = thisYear.stream().collect(Collectors.toMap(BusinessStageStatisticsDTO::getStage, e -> e));
        // 计算对应转化率
        Map<String, String> allRatioMap = this.calculateRatioMap(allMap);
        Map<String, String> thisYearRatioMap = this.calculateRatioMap(thisYearMap);
        // 处理返回参数
        List<BusinessTransformFunnelListRSP> result = new ArrayList<>(STAGE_LIST.size());
        for (String stage : STAGE_LIST) {
            BusinessStageStatisticsDTO historyStage = allMap.get(stage);
            BusinessStageStatisticsDTO thisYearStage = thisYearMap.get(stage);
            BusinessTransformFunnelListRSP rsp = new BusinessTransformFunnelListRSP();
            rsp.setStage(stage);
            if (!CharSequenceUtil.equalsAny(stage, "访客", "尽调")) {
                rsp.setTotal(new ValueUnitDTO(Optional.ofNullable(historyStage).map(BusinessStageStatisticsDTO::getQuantity).map(Object::toString).orElse(null), ""));
                rsp.setIncrementThisYear(new ValueUnitDTO(Optional.ofNullable(thisYearStage).map(BusinessStageStatisticsDTO::getQuantity).map(Object::toString).orElse(null), ""));
                rsp.setConversionRate(new ValueUnitDTO(Optional.ofNullable(allRatioMap.get(stage)).orElse(null), "%"));
                rsp.setIncrementConversionRateThisYear(new ValueUnitDTO(Optional.ofNullable(thisYearRatioMap.get(stage)).orElse(null), "%"));
            }
            result.add(rsp);
        }
        return result;
    }

    private Map<String, String> calculateRatioMap(Map<String, BusinessStageStatisticsDTO> map) {
        Map<String, String> resultMap = new HashMap<>();
        String establishTransformRatio = this.calculateRatio(map.get("立项").getQuantity(), map.get("评审").getQuantity());
        String reviewTransformRatio = this.calculateRatio(map.get("评审").getQuantity(), map.get("签约").getQuantity());
        String payTransformRatio = this.calculateRatio(map.get("签约").getQuantity(), map.get("投放").getQuantity());
        resultMap.put("立项", establishTransformRatio);
        resultMap.put("评审", reviewTransformRatio);
        resultMap.put("签约", payTransformRatio);
        return resultMap;
    }

    private String calculateRatio(Integer total, Integer target) {
        if (Objects.isNull(total) || total == 0) {
            return null;
        }
        if (Objects.isNull(target)) {
            target = 0;
        }
        return BigDecimal.valueOf(target)
                .divide(BigDecimal.valueOf(total), 20, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}
