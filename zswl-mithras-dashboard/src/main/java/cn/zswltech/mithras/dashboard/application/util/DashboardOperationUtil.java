package cn.zswltech.mithras.dashboard.application.util;

import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationBaseREQ;
import cn.zswltech.mithras.dashboard.domain.enums.DashboardAdjustPositionEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardAdjustPersonInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardOperationUtil {

    public static List<String> getRiskControlIndustryClassify(DashboardOperationBaseREQ req){
        if(req.getQueryDateFrom() != null && req.getQueryDateTo() != null){
            if(req.getQueryDateFrom().getYear() != req.getQueryDateTo().getYear()){
                throw new MithrasException("不支持跨年份查询");
            }
        }
        String type = req.getType();
        // 公用类
        List<String> publicType = Arrays.asList(RiskControlIndustryClassify.PUBLIC_UTILITIES.name(),
                RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(),
                RiskControlIndustryClassify.TRAVEL.name());

        if(DashboardOperationBaseREQ.publicType.equals(type)){
            return publicType;
        }else if(DashboardOperationBaseREQ.industryType.equals(type)){
            return Arrays.stream(RiskControlIndustryClassify.values()).map(RiskControlIndustryClassify::name)
                    .filter(name -> !publicType.contains(name)).collect(Collectors.toList());
        }else{
            throw new MithrasException("不支持的风控行业类型");
        }
    }

    public static Map<Long, BigDecimal> getAdjustPerson(List<DashboardAdjustPersonInfo> adjustPersonInfoList) {
        Map<Long, BigDecimal> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(adjustPersonInfoList)) {
            Map<Long, List<DashboardAdjustPersonInfo>> collect = adjustPersonInfoList.stream().collect(Collectors.groupingBy(DashboardAdjustPersonInfo::getDeptId));
            collect.forEach((deptId, item) ->{
                BigDecimal reduce = item.stream().map(info -> {
                    DashboardAdjustPositionEnum positionEnum = DashboardAdjustPositionEnum.find(info.getPosition());
                    return positionEnum != null ? positionEnum.getMultiple() : BigDecimal.ZERO;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.put(deptId, reduce);
            });

        }
        return result;
    }
}
