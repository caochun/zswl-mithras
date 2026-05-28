package cn.zswltech.mithras.metric.financialcloudmetric.mapper;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 金融云指标
 * @date 2023-04-12
 */
public interface FinancialCloudMetricValueMapper extends BaseMapper<FinancialCloudMetricValue> {

    Page<FinancialCloudMetricValue> advanceList(Page<FinancialCloudMetricValue> page, @Param("query") FinancialCloudMetricValueQuery query);

    List<FinancialCloudMetricValue> reportList(@Param("query") FinancialCloudMetricValueQuery query);
}