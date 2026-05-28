package cn.zswltech.mithras.service.service.lib.assetclassify.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientDetailRSP;
import cn.zswltech.mithras.service.convert.assetclassify.AssetClassifyConvert;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClientLib;
import cn.zswltech.mithras.service.service.lib.assetclassify.handler.AssetClassifyReviewAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/5 7:15 下午
 **/
@Component
public class AssetClassifyClientLibHandler extends AssetClassifyReviewAbstractLibHandler<AssetClassifyClientLib, AssetClassifyClient, AssetClassifyClientDetailRSP> {

    @Resource
    private AssetClassifyConvert assetClassifyConvert;

    @Override
    protected AssetClassifyClientLib entity2Lib(AssetClassifyClient f) {
        return BeanUtil.copyProperties(f, AssetClassifyClientLib.class);
    }

    @Override
    protected AssetClassifyClient lib2Entity(AssetClassifyClientLib t) {
        return BeanUtil.copyProperties(t, AssetClassifyClient.class);
    }

    @Override
    protected AssetClassifyClientDetailRSP lib2Rsp(AssetClassifyClientLib f) {
        AssetClassifyClient assetClassifyClient = this.lib2Entity(f);
        return assetClassifyConvert.assetClassifyClient2AssetClassifyClientDetailRSP(assetClassifyClient);
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }
}
