package cn.zswltech.mithras.application.orchestration.adapter.assetclassify;

import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyClientRelationPort;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class AssetClassifyClientRelationPortAdapter implements AssetClassifyClientRelationPort {

    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;

    @Override
    public List<Long> listClientIdsByRelated(Integer isRelated) {
        return corpCommerceInfoMapper.selectObjs(Wrappers.<CorpCommerceInfo>lambdaQuery()
                        .select(CorpCommerceInfo::getClientId)
                        .eq(CorpCommerceInfo::getIsRelated, isRelated))
                .stream()
                .map(value -> value == null ? null : Long.valueOf(value.toString()))
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }
}
