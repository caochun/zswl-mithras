package cn.zswltech.mithras.assetclassify.versioning;

import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyCheckContentLibMapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyCheckContent;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyCheckContentLib;
import cn.zswltech.mithras.assetclassify.versioning.handler.impl.AssetClassifyCheckContentLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName AssetClassifyCheckContentLibService
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/8 2:40 下午
 * @Version 1.0
 **/
@Service
public class AssetClassifyCheckContentLibService extends ServiceImpl<AssetClassifyCheckContentLibMapper, AssetClassifyCheckContentLib> {

    @Resource
    private AssetClassifyCheckContentLibHandler libHandler;

    public List<AssetClassifyCheckContent> listByVersion(Long assetClassifyClientId, String version){
        List<AssetClassifyCheckContentLib> assetClassifyCheckContentLibs = baseMapper.selectList(Wrappers.<AssetClassifyCheckContentLib>lambdaQuery()
                .eq(AssetClassifyCheckContentLib::getAssetClassifyClientId, assetClassifyClientId)
                .eq(AssetClassifyCheckContentLib::getVersion, version));
        return assetClassifyCheckContentLibs.stream().map(libHandler::actualLib2Entity).collect(Collectors.toList());
    }
}
