package cn.zswltech.mithras.metric.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.metric.RiskMetricAddReq;
import cn.zswltech.mithras.dto.metric.RiskMetricListReq;
import cn.zswltech.mithras.dto.metric.RiskMetricModifyReq;
import cn.zswltech.mithras.metric.mapper.RiskMetricMapper;
import cn.zswltech.mithras.metric.mapper.RiskMetricValueMapper;
import cn.zswltech.mithras.metric.mapper.model.RiskMetric;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricValue;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author yibin
 */
@Service
public class RiskMetricService extends ServiceImpl<RiskMetricMapper, RiskMetric> {
    @Resource
    private RiskMetricValueMapper metricValueMapper;

    public Long add(RiskMetricAddReq req) {
        if (this.count(Wrappers.<RiskMetric>lambdaQuery()
                .or().eq(RiskMetric::getMetricCode, req.getMetricCode())
                .or(w -> w.eq(RiskMetric::getMetricName, req.getMetricName())
                )) > 0) {
            err("名称或code已存在");
        }


        RiskMetric riskMetric = BeanUtil.copyProperties(req, RiskMetric.class);
        this.save(riskMetric);
        return riskMetric.getId();
    }

    public void remove(Long riskMetricId) {
        if (metricValueMapper.selectCount(Wrappers.<RiskMetricValue>lambdaQuery().eq(RiskMetricValue::getRiskMetricId, riskMetricId)) > 0) {
            err("存在相关的指标值，不允许删除");
        }
        this.removeById(riskMetricId);
    }

    public void modify(RiskMetricModifyReq req) {
        if (this.count(Wrappers.<RiskMetric>lambdaQuery()
                .ne(RiskMetric::getId, req.getId())
                .and(true, w -> w
                        .or().eq(true, RiskMetric::getMetricName, req.getMetricName())
                        .or().eq(true, RiskMetric::getMetricCode, req.getMetricCode())
                )) > 0) {
            err("名称或code已存在");
        }
        RiskMetric riskMetric = BeanUtil.copyProperties(req, RiskMetric.class);
        riskMetric.setId(req.getId());
        this.updateById(riskMetric);
    }

    public Page<RiskMetric> list(RiskMetricListReq req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<RiskMetric>lambdaQuery()
                .like(isNotBlank(req.getMetricName()), RiskMetric::getMetricName, req.getMetricName())
                .like(isNotBlank(req.getMetricCode()), RiskMetric::getMetricCode, req.getMetricCode())
        );
    }
}
