package cn.zswltech.mithras.service.service.lib.assetclassify.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientListRSP;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.service.service.lib.assetclassify.handler.AssetClassifyAbstractLibHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Component
public class AssetClassifyClientAuxiliaryLibHandler extends AssetClassifyAbstractLibHandler<AssetClassifyClientAuxiliaryLib, AssetClassifyClient, AssetClassifyClientListRSP> {
    @Override
    protected AssetClassifyClientAuxiliaryLib entity2Lib(AssetClassifyClient f) {
        return BeanUtil.copyProperties(f, AssetClassifyClientAuxiliaryLib.class);
    }

    @Override
    protected AssetClassifyClient lib2Entity(AssetClassifyClientAuxiliaryLib t) {
        return BeanUtil.copyProperties(t, AssetClassifyClient.class);
    }

    @Override
    protected AssetClassifyClientListRSP lib2Rsp(AssetClassifyClientAuxiliaryLib f) {
        AssetClassifyClient assetClassifyClient = this.lib2Entity(f);
        return BeanUtil.copyProperties(assetClassifyClient, AssetClassifyClientListRSP.class);
    }
}
