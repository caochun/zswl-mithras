package cn.zswltech.mithras.assetclassify.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientDetailRSP;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClientLib;
import cn.zswltech.mithras.assetclassify.versioning.handler.AssetClassifyReviewAbstractLibHandler;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/5 7:15 下午
 **/
@Component
public class AssetClassifyClientLibHandler extends AssetClassifyReviewAbstractLibHandler<AssetClassifyClientLib, AssetClassifyClient, AssetClassifyClientDetailRSP> {
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
        AssetClassifyClientDetailRSP rsp = BeanUtil.copyProperties(assetClassifyClient, AssetClassifyClientDetailRSP.class);
        if (StrUtil.isNotBlank(assetClassifyClient.getStartRentContractCodes())) {
            rsp.setStartRentContractCodes(JSONUtil.toList(assetClassifyClient.getStartRentContractCodes(), String.class));
        }
        if (StrUtil.isNotBlank(assetClassifyClient.getStartRentContractRemainingPrincipal())) {
            rsp.setStartRentContractRemainingPrincipal(JSONUtil.toList(assetClassifyClient.getStartRentContractRemainingPrincipal(), Long.class));
        }
        return rsp;
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
