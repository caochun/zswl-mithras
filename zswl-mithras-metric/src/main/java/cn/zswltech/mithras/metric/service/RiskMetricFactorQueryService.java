package cn.zswltech.mithras.metric.service;

import cn.zswltech.mithras.metric.mapper.RiskMetricFactorMapper;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Lightweight metric factor query service for calculators.
 */
@Service
public class RiskMetricFactorQueryService extends ServiceImpl<RiskMetricFactorMapper, RiskMetricFactor> {
}
