package cn.zswltech.mithras.metric.financialcloudmetric.converter;

import cn.zswltech.mithras.dto.financialcloudmetric.FinancialCloudMetricValueListREQ;
import cn.zswltech.mithras.dto.financialcloudmetric.FinancialCloudMetricValueListRSP;
import cn.zswltech.mithras.metric.financialcloudmetric.mapper.FinancialCloudMetricValueQuery;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetric;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import org.mapstruct.Mapper;

/**
 * @author zhaozhengkang
 * @description 金融云指标
 * @date 2023-04-12
 */
@Mapper(componentModel = "spring")
public interface FinancialCloudMetricValueConverter {

    FinancialCloudMetricValueListRSP entity2ListRsp(FinancialCloudMetricValue metricValue);

    FinancialCloudMetricValueQuery listReq2Query(FinancialCloudMetricValueListREQ req);

    FinancialCloudMetricValue metric2Value(FinancialCloudMetric metric);
    
}
