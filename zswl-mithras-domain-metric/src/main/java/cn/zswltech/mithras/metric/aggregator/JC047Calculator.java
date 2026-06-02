package cn.zswltech.mithras.metric.aggregator;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricDict;
import cn.zswltech.mithras.metric.service.RiskMetricDictService;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yibin
 */
@Component
public class JC047Calculator implements MetricCalculator {

    @Resource
    private RiskMetricTimedService metricTimedService;
    @Resource
    private RiskMetricDictService metricDictService;

    @Override
    public String metricCode() {
        return "A10000396_JC047";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(2);
        //
        RiskMetricDict dict = metricDictService.getOne(Wrappers.<RiskMetricDict>lambdaQuery()
                .eq(RiskMetricDict::getDictType, "SUPPORT_LIMIT")
                .eq(RiskMetricDict::getDictKey, "PROPER")
        );
        result.put("a", Long.parseLong(dict.getDickValue()) * 100_000_000L * 10000L);
        //
        dict = metricDictService.getOne(Wrappers.<RiskMetricDict>lambdaQuery()
                .eq(RiskMetricDict::getDictType, "SUPPORT_REGION")
                .eq(RiskMetricDict::getDictKey, "PROPER")
        );
        String jsonStr = dict.getDickValue();
        List<String> areaCodeList = JSONUtil.toBean(jsonStr, new TypeReference<List<String>>() {
        }, true);
        Long value = metricTimedService.areaStat(areaCodeList);
        result.put("b", value);
        //
        return result;
    }

    /**
     * 鼓励支持类区域风险敞口额度（暂写死）-该区域在租项目剩余本金
     */
    @Override
    public String calcExpression() {
        return "${a-b}";
    }
}
