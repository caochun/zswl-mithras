package cn.zswltech.mithras.assetclassify.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyNodeRecord;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyNodeRecordLib;
import cn.zswltech.mithras.assetclassify.versioning.handler.AssetClassifyAbstractLibHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Component
public class AssetClassifyNodeRecordLibHandler extends AssetClassifyAbstractLibHandler<AssetClassifyNodeRecordLib, AssetClassifyNodeRecord, ListBaseRSP> {
    @Override
    protected AssetClassifyNodeRecordLib entity2Lib(AssetClassifyNodeRecord f) {
        return BeanUtil.copyProperties(f, AssetClassifyNodeRecordLib.class);
    }

    @Override
    protected AssetClassifyNodeRecord lib2Entity(AssetClassifyNodeRecordLib t) {
        return BeanUtil.copyProperties(t, AssetClassifyNodeRecord.class);
    }

    @Override
    protected ListBaseRSP lib2Rsp(AssetClassifyNodeRecordLib f) {
        return null;
    }
}
