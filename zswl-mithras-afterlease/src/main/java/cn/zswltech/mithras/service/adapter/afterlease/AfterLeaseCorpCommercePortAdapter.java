package cn.zswltech.mithras.service.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCorpCommercePort;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
public class AfterLeaseCorpCommercePortAdapter implements AfterLeaseCorpCommercePort {
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;

    @Override
    public Optional<Map<Long, String>> selectIndustryTypeBatchByIds(Collection<Long> clientIds) {
        return corpCommerceInfoService.selectIndustryTypeBatchByIds(clientIds);
    }
}
