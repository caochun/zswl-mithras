package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyQueryService;
import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlAssetClassifyPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RiskControlAssetClassifyPortAdapter implements RiskControlAssetClassifyPort {

    @Resource
    private AssetClassifyQueryService assetClassifyQueryService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;

    @Override
    public Optional<Long> currentClassifyId(LocalDate date) {
        return assetClassifyQueryService.currentClassify(date).map(AssetClassify::getMainId);
    }

    @Override
    public Set<Long> lastThreeClassifyClientIds(Long assetClassifyId) {
        if (assetClassifyId == null) {
            return Collections.emptySet();
        }
        return assetClassifyClientAuxiliaryLibService.lastThreeNewestClassifyClientLib(assetClassifyId).stream()
                .map(AssetClassifyClient::getClientId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Long, String> latestClassifyResults(Collection<Long> clientIds) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        return assetClassifyClientAuxiliaryLibMapper.listNewestClassifyLibByClientId(new HashSet<>(clientIds)).stream()
                .collect(Collectors.toMap(AssetClassifyClient::getClientId, AssetClassifyClient::getClassifyResult, (first, second) -> second));
    }
}
