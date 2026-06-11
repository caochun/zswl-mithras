package cn.zswltech.mithras.assetclassify.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyLib;
import cn.zswltech.mithras.assetclassify.versioning.handler.AssetClassifyAbstractLibHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Component
public class AssetClassifyLibHandler extends AssetClassifyAbstractLibHandler<AssetClassifyLib, AssetClassify, ListBaseRSP> {
    @Override
    protected AssetClassifyLib entity2Lib(AssetClassify f) {
        return BeanUtil.copyProperties(f, AssetClassifyLib.class);
    }

    @Override
    protected AssetClassify lib2Entity(AssetClassifyLib t) {
        return BeanUtil.copyProperties(t, AssetClassify.class);
    }

    @Override
    protected ListBaseRSP lib2Rsp(AssetClassifyLib f) {
        return null;
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public boolean isMainTable() {
        return true;
    }
}
