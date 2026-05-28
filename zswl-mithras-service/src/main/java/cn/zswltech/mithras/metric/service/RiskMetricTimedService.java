package cn.zswltech.mithras.metric.service;

import cn.zswltech.mithras.dto.metric.timed.RiskMetricTimedListReq;
import cn.zswltech.mithras.metric.mapper.RiskMetricTimedMapper;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.mapper.model.condition.RiskMetricTimedCustomConditions;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author yibin
 */
@Slf4j
@Service
public class RiskMetricTimedService extends ServiceImpl<RiskMetricTimedMapper, RiskMetricTimed> {

    public Page<RiskMetricTimed> list(RiskMetricTimedListReq req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<RiskMetricTimed>lambdaQuery()
                .eq(isNotNull(req.getDataTime()), RiskMetricTimed::getDataTime, req.getDataTime())
                .eq(isNotBlank(req.getProjType()), RiskMetricTimed::getProjType, req.getProjType())
                .eq(isNotBlank(req.getIndustryType()), RiskMetricTimed::getIndustryType, req.getIndustryType())
                .eq(isNotBlank(req.getLevel5Type()), RiskMetricTimed::getLevel5Type, req.getLevel5Type())
                .like(isNotBlank(req.getClientName()), RiskMetricTimed::getClientName, req.getClientName())
                .like(isNotBlank(req.getBelongGroupName()), RiskMetricTimed::getBelongGroupName, req.getBelongGroupName())
                .eq(isNotNull(req.getRelated()), RiskMetricTimed::getRelated, req.getRelated())
        );
    }

    public Long areaStat(List<String> provCodeList) {
        RiskMetricTimedCustomConditions conditions = new RiskMetricTimedCustomConditions();
        conditions.setProvCodeList(provCodeList);
        return this.baseMapper.stat(conditions);
    }

    public Long areaStat(List<String> cityCodeList, List<String> industryTypeList) {
        RiskMetricTimedCustomConditions conditions = new RiskMetricTimedCustomConditions();
        conditions.setCityCodeList(cityCodeList);
        conditions.setIndustryTypeList(industryTypeList);
        return this.baseMapper.stat(conditions);
    }
}
