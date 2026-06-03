package cn.zswltech.mithras.afterlease.application;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckReportFieldConfigMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportFieldConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/11/14
 * @description
 */
@Service
public class NewAfterLeaseCheckReportFieldConfigService extends ServiceImpl<NewAfterLeaseCheckReportFieldConfigMapper, NewAfterLeaseCheckReportFieldConfig> {
    public Map<String, NewAfterLeaseCheckReportFieldConfig> getMapByReportType(String reportType) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportFieldConfig> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportFieldConfig::getReportType, reportType);
        List<NewAfterLeaseCheckReportFieldConfig> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportFieldConfig::getFieldName, e -> e));
    }
}
