package cn.zswltech.mithras.assetclassify.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyCheckContentModifyREQ;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyCheckContentPackRSP;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyCheckContentREQ;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyCheckContentRSP;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.AssetClassifyCheckContentMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyCheckContent;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyCheckContentAuxiliaryLibService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyCheckContentLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/8 2:39 下午
 **/
@Service
public class AssetClassifyCheckContentService extends ServiceImpl<AssetClassifyCheckContentMapper, AssetClassifyCheckContent> {

    @Resource
    private AssetClassifyCheckContentLibService assetClassifyCheckContentLibService;
    @Resource
    private AssetClassifyCheckContentAuxiliaryLibService assetClassifyCheckContentAuxiliaryLibService;

    public AssetClassifyCheckContentPackRSP getCheckReport(AssetClassifyCheckContentREQ req){
        AssetClassifyCheckContentPackRSP assetClassifyCheckContentPackRSP = new AssetClassifyCheckContentPackRSP();
        List<AssetClassifyCheckContent> assetClassifyCheckContents;
        if (ObjectUtil.isNull(req.getVersion())) {
            assetClassifyCheckContents = this.baseMapper.selectList(Wrappers.<AssetClassifyCheckContent>lambdaQuery()
                    .eq(AssetClassifyCheckContent::getAssetClassifyClientId, req.getAssetClassifyClientId()));
        } else {
            if (ProcessModelTypeEnum.AssetClassifyReviewFlow.name().equals(req.getProcessType())) {
                assetClassifyCheckContents = assetClassifyCheckContentLibService.listByVersion(req.getAssetClassifyClientId(), req.getVersion());
            } else {
                assetClassifyCheckContents = assetClassifyCheckContentAuxiliaryLibService.listByVersion(req.getAssetClassifyClientId(),
                        req.getVersion());
            }
        }
        Map<String, AssetClassifyCheckContentRSP> map = new HashMap<>();
        AssetClassifyCheckContentRSP assetClassifyCheckContentRSP;
        List<AssetClassifyCheckContentRSP.Content> contentList;
        AssetClassifyCheckContentRSP.Content contentRsp;
        for(AssetClassifyCheckContent content : assetClassifyCheckContents){
            if("TABLE_HEAD".equals(content.getTemplateCode())){
                assetClassifyCheckContentPackRSP.setTitle(content.getContent());
                break;
            }
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
        assetClassifyCheckContentPackRSP.setAssetClassifyCheckContent(new ArrayList<>(map.values()));
        return assetClassifyCheckContentPackRSP;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void checkReportUpdate(AssetClassifyCheckContentModifyREQ req){
        List<AssetClassifyCheckContentModifyREQ.Content> contentList = req.getContentList();
        AssetClassifyCheckContent content;
        List<AssetClassifyCheckContent> toUpdateContent = new ArrayList<>();
        for(AssetClassifyCheckContentModifyREQ.Content contentReq : contentList){
            content = new AssetClassifyCheckContent();
            content.setId(contentReq.getId());
            content.setContent(contentReq.getContent());
            toUpdateContent.add(content);
        }
        this.updateBatchById(toUpdateContent);
    }

}
