package cn.zswltech.mithras.assetclassify.application.job;

import java.time.LocalDateTime;

public interface AssetClassifyInitJobService {

    void init(LocalDateTime targetDateTime);
}
