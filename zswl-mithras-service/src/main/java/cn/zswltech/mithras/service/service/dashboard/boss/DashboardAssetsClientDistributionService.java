package cn.zswltech.mithras.service.service.dashboard.boss;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionAssetsIndustryListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionClientDepartmentListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionClientStatisticsListRSP;
import cn.zswltech.mithras.dashboard.domain.enums.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.AssetsIndustryDistributionDTO;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.DeptClientStatisticsDTO;
import cn.zswltech.mithras.dashboard.application.guanyuandata.boss.StageClientStatisticsDTO;
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
 * @description 资产客户分布
 */
@Slf4j
@Service
public class DashboardAssetsClientDistributionService extends GuanYuanBasicService {
    public List<DistributionAssetsIndustryListRSP> listIndustryDistribution() {
        List<AssetsIndustryDistributionDTO> guanyuanList = this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.AssetsIndustryDistributionByCompany, new GuanYuanDSRequest.Body(), AssetsIndustryDistributionDTO.class);
        if (CollectionUtil.isEmpty(guanyuanList)) {
            return Collections.emptyList();
        } else {
            guanyuanList.forEach(e -> {
                if (Objects.isNull(e.getBalance())) {
                    e.setBalance(0L);
                }
            });
        }
        // 排序
        guanyuanList.sort(Comparator.comparing(AssetsIndustryDistributionDTO::getBalance).reversed());
        return guanyuanList.stream().map(e -> {
            DistributionAssetsIndustryListRSP rsp = new DistributionAssetsIndustryListRSP();
            rsp.setName(e.getIndustryDisplay());
            rsp.setValue(BigDecimal.valueOf(e.getBalance()).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP).toPlainString());
            rsp.setUnit("亿元");
            return rsp;
        }).collect(Collectors.toList());
    }

    public List<DistributionClientDepartmentListRSP> listClientByDept() {
        List<DeptClientStatisticsDTO> guanyuanList = this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.AssetsClientDistributionClientDeptByCompany, new GuanYuanDSRequest.Body(), DeptClientStatisticsDTO.class);
        if (CollectionUtil.isEmpty(guanyuanList)) {
            return Collections.emptyList();
        } else {
            guanyuanList.forEach(e -> {
                if (Objects.isNull(e.getQuantity())) {
                    e.setQuantity(0);
                }
            });
        }
        // 排序
        guanyuanList.sort(Comparator.comparing(DeptClientStatisticsDTO::getQuantity).reversed());
        return guanyuanList.stream().map(e -> {
            DistributionClientDepartmentListRSP rsp = new DistributionClientDepartmentListRSP();
            rsp.setName(e.getDeptName());
            rsp.setValue(Optional.ofNullable(e.getQuantity()).map(Object::toString).orElse("0"));
            rsp.setUnit("个");
            return rsp;
        }).collect(Collectors.toList());
    }

    public List<DistributionClientStatisticsListRSP> listClientStatistics() {
        List<StageClientStatisticsDTO> guanyuanList = this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.AssetsClientDistributionClientStatisticsByCompany, new GuanYuanDSRequest.Body(), StageClientStatisticsDTO.class);
        if (CollectionUtil.isEmpty(guanyuanList)) {
            return Collections.emptyList();
        }
        Map<String, Integer> guanyuanMap = guanyuanList.stream().collect(Collectors.toMap(StageClientStatisticsDTO::getTitle, StageClientStatisticsDTO::getQuantity));
        List<DistributionClientStatisticsListRSP> result = new LinkedList<>();
        result.add(new DistributionClientStatisticsListRSP("总客户数", guanyuanMap.get("总客户数"), guanyuanMap.get("总客户数本月新增"), "TOTAL"));
        result.add(new DistributionClientStatisticsListRSP("存续客户数", guanyuanMap.get("存续客户数"), guanyuanMap.get("存续客户数本月新增"), "EXISTING"));
        result.add(new DistributionClientStatisticsListRSP("逾期客户数", guanyuanMap.get("逾期客户数"), guanyuanMap.get("逾期客户数本月新增"), "OVERDUE"));
        result.add(new DistributionClientStatisticsListRSP("已结清客户数", guanyuanMap.get("已结清客户数"), guanyuanMap.get("已结清客户数本月新增"), "SETTLED"));
        return result;
    }
}
