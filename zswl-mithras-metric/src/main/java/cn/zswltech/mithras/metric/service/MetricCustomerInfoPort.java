package cn.zswltech.mithras.metric.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MetricCustomerInfoPort {

    Map<Long, MetricCustomerSnapshot> mapClientsByIds(Collection<Long> clientIds);

    Map<Long, MetricCorpCommerceSnapshot> mapCommerceByClientIds(Collection<Long> clientIds);

    Map<String, MetricIndustryTypeSnapshot> mapIndustryByCodes(Collection<String> industryCodes);

    List<MetricCorpCommerceSnapshot> listNewestCommerceInfo();

    List<MetricCorpCommerceSnapshot> listNewestCommerceInfo(Set<Long> clientIds);
}
