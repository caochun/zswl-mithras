package cn.zswltech.mithras.assetclassify.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyCheckContent;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyCheckContentAuxiliaryLib;
import cn.zswltech.mithras.assetclassify.application.lib.handler.AssetClassifyAbstractLibHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/1/9
 * @description
 */
@Component
public class AssetClassifyCheckContentAuxiliaryLibHandler extends AssetClassifyAbstractLibHandler<AssetClassifyCheckContentAuxiliaryLib, AssetClassifyCheckContent, ListBaseRSP> {
    @Override
    protected AssetClassifyCheckContentAuxiliaryLib entity2Lib(AssetClassifyCheckContent f) {
        return BeanUtil.copyProperties(f, AssetClassifyCheckContentAuxiliaryLib.class);
    }

    @Override
    protected AssetClassifyCheckContent lib2Entity(AssetClassifyCheckContentAuxiliaryLib t) {
        return BeanUtil.copyProperties(t, AssetClassifyCheckContent.class);
    }

    @Override
    protected ListBaseRSP lib2Rsp(AssetClassifyCheckContentAuxiliaryLib f) {
        return null;
    }
}
