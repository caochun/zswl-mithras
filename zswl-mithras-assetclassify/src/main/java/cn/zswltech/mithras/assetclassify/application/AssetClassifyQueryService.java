package cn.zswltech.mithras.assetclassify.application;

import cn.zswltech.mithras.assetclassify.model.AssetClassify;

import java.time.LocalDate;
import java.util.Optional;

public interface AssetClassifyQueryService {

    Optional<AssetClassify> currentClassify(LocalDate date);

    Optional<AssetClassify> currentClassify();
}
