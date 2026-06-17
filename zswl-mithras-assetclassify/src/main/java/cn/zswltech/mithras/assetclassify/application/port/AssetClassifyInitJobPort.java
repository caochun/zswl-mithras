package cn.zswltech.mithras.assetclassify.application.port;

import java.time.LocalDateTime;

public interface AssetClassifyInitJobPort {

    void init(LocalDateTime targetDateTime);
}
