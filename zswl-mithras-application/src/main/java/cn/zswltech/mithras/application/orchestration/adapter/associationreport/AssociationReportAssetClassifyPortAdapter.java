package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.assetclassify.mapper.AssetClassifyClientMapper;
import cn.zswltech.mithras.assetclassify.mapper.AssetClassifyMapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.associationreport.application.AssociationReportAssetClassifyPort;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

@Component
public class AssociationReportAssetClassifyPortAdapter implements AssociationReportAssetClassifyPort {

    @Resource
    private AssetClassifyMapper assetClassifyMapper;
    @Resource
    private AssetClassifyClientMapper assetClassifyClientMapper;

    @Override
    public boolean isLatestEffectiveClassifyLastThree(Long clientId) {
        String classifyResult = findLatestClassifyByClientId(clientId);
        return StrUtil.equalsAny(
                classifyResult,
                AssetClassifyResultEnum.SECONDARY.name(),
                AssetClassifyResultEnum.SUSPICIOUS.name(),
                AssetClassifyResultEnum.LOSS.name());
    }

    private String findLatestClassifyByClientId(Long clientId) {
        AssetClassify latest = assetClassifyMapper.selectOne(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getFinish, YesOrNoNumberEnum.YES.getCode())
                .orderByDesc(AssetClassify::getId)
                .last("limit 1"));
        if (Objects.isNull(latest)) {
            return null;
        }
        AssetClassifyClient assetClassifyClient = assetClassifyClientMapper.selectOne(Wrappers.<AssetClassifyClient>lambdaQuery()
                .eq(AssetClassifyClient::getAssetClassifyId, latest.getId())
                .eq(AssetClassifyClient::getClientId, clientId)
                .orderByDesc(AssetClassifyClient::getId)
                .last("limit 1"));
        return Optional.ofNullable(assetClassifyClient).map(AssetClassifyClient::getClassifyResult).orElse(null);
    }
}
