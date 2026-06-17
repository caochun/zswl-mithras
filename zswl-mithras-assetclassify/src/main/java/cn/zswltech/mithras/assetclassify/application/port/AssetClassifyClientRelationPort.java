package cn.zswltech.mithras.assetclassify.application.port;

import java.util.List;

public interface AssetClassifyClientRelationPort {

    List<Long> listClientIdsByRelated(Integer isRelated);
}
