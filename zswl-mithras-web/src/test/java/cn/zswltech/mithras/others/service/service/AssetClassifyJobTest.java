package cn.zswltech.mithras.others.service.service;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyCheckContentService;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyCheckContent;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyClientService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AssetClassifyJobTest
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/9 1:44 下午
 * @Version 1.0
 **/
public class AssetClassifyJobTest extends ApplicationTest {

    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private AssetClassifyCheckContentService assetClassifyCheckContentService;

    //生成报告信息
    @Test
    public void initClient() {
        List<Long> assetClassIds = ListUtil.toList(298L);
        List<AssetClassifyClient> toSaveClientList = assetClassifyClientService.list(Wrappers.<AssetClassifyClient>lambdaQuery()
                .in(AssetClassifyClient::getAssetClassifyId, assetClassIds));
        //保存每个客户的检查信息
        List<AssetClassifyCheckContent> classifyCheckContents = new ArrayList<>();
        for (AssetClassifyClient assetClassifyClient : toSaveClientList) {
            classifyCheckContents.addAll(assetClassifyClientService.getLastCheckProjectReportByClientId(assetClassifyClient.getId()));
        }
        assetClassifyCheckContentService.saveBatch(classifyCheckContents);
        //批量转存附件
        assetClassifyClientService.saveLastCheckProjectReportFile(classifyCheckContents);
    }


}
