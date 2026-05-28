package cn.zswltech.mithras.service.service.lib.assetclassify.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyCheckContentRSP;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyCheckContent;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyCheckContentLib;
import cn.zswltech.mithras.service.service.lib.assetclassify.handler.AssetClassifyReviewAbstractLibHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/5 7:15 下午
 **/
@Component
public class AssetClassifyCheckContentLibHandler extends AssetClassifyReviewAbstractLibHandler<AssetClassifyCheckContentLib, AssetClassifyCheckContent, AssetClassifyCheckContentRSP> {


    @Override
    protected AssetClassifyCheckContentLib entity2Lib(AssetClassifyCheckContent f) {
        return BeanUtil.copyProperties(f, AssetClassifyCheckContentLib.class);
    }

    @Override
    protected AssetClassifyCheckContent lib2Entity(AssetClassifyCheckContentLib t) {
        return BeanUtil.copyProperties(t, AssetClassifyCheckContent.class);
    }

    @Override
    protected AssetClassifyCheckContentRSP lib2Rsp(AssetClassifyCheckContentLib f) {
        AssetClassifyCheckContentRSP assetClassifyCheckContentRSP = new AssetClassifyCheckContentRSP();
        AssetClassifyCheckContentRSP.Content contentRsp;
        List<AssetClassifyCheckContentRSP.Content> contentList = new ArrayList<>();
        assetClassifyCheckContentRSP.setGroupName(f.getTemplateGroupName());
        contentRsp = BeanUtil.copyProperties(f, AssetClassifyCheckContentRSP.Content.class);
        contentList.add(contentRsp);
        assetClassifyCheckContentRSP.setContentList(contentList);
        return assetClassifyCheckContentRSP;
    }

    @Override
    protected List<AssetClassifyCheckContentRSP> lib2RspList(List<AssetClassifyCheckContentLib> f) {
        Map<String, AssetClassifyCheckContentRSP> map = new HashMap<>();
        AssetClassifyCheckContentRSP assetClassifyCheckContentRSP;
        List<AssetClassifyCheckContentRSP.Content> contentList;
        AssetClassifyCheckContentRSP.Content contentRsp;
        for(AssetClassifyCheckContent content : f){
            if(ObjectUtil.isNull(map.get(content.getTemplateGroupName()))){
                assetClassifyCheckContentRSP = new AssetClassifyCheckContentRSP();
                assetClassifyCheckContentRSP.setGroupName(content.getTemplateGroupName());
                contentList = new ArrayList<>();
                contentRsp = BeanUtil.copyProperties(content, AssetClassifyCheckContentRSP.Content.class);
                contentList.add(contentRsp);
                assetClassifyCheckContentRSP.setContentList(contentList);
                map.put(content.getTemplateGroupName(), assetClassifyCheckContentRSP);
            }else {
                contentRsp = BeanUtil.copyProperties(content, AssetClassifyCheckContentRSP.Content.class);
                map.get(content.getTemplateGroupName()).getContentList().add(contentRsp);
            }
        }
        return new ArrayList<>(map.values());
    }
}
