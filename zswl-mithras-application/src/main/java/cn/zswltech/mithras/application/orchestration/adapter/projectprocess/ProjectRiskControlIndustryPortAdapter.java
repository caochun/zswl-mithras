package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.projectprocess.application.port.ProjectRiskControlIndustryPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProjectRiskControlIndustryPortAdapter implements ProjectRiskControlIndustryPort {

    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;

    @Override
    public Map<Long, String> mapRiskControlIndustryClassify(Collection<Long> clientIds) {
        if (clientIds == null || clientIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                        .in(CorpCommerceInfo::getClientId, clientIds))
                .stream()
                .filter(item -> item.getClientId() != null)
                .filter(item -> Objects.nonNull(item.getRiskControlIndustryClassify()))
                .collect(Collectors.toMap(CorpCommerceInfo::getClientId, CorpCommerceInfo::getRiskControlIndustryClassify, (a, b) -> a));
    }
}
