package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.assetclassify.mapper.AssetClassifyMapper;
import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AssetClassifyMetricReader {

    @Resource
    private AssetClassifyMapper assetClassifyMapper;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;

    public Optional<AssetClassify> currentClassify(LocalDate date) {
        return Optional.ofNullable(assetClassifyMapper.selectOne(Wrappers.<AssetClassify>lambdaQuery()
                .le(date != null, BaseModel::getCreateTime, date)
                .eq(AssetClassify::getFinish, 1)
                .orderByDesc(AssetClassify::getYear)
                .orderByDesc(AssetClassify::getQuarter)
                .last("limit 1")));
    }

    public Set<Long> listAdverseClientIds(Long assetClassifyId) {
        return assetClassifyClientAuxiliaryLibMapper.newestClassifyClientLib(assetClassifyId)
                .stream()
                .filter(assetClassifyClient -> CharSequenceUtil.equalsAny(assetClassifyClient.getClassifyResult(),
                        AssetClassifyResultEnum.LOSS.name(),
                        AssetClassifyResultEnum.SUSPICIOUS.name(),
                        AssetClassifyResultEnum.SECONDARY.name()))
                .map(AssetClassifyClient::getClientId)
                .collect(Collectors.toSet());
    }
}
