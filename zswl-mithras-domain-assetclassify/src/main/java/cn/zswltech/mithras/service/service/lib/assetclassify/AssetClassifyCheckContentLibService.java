package cn.zswltech.mithras.service.service.lib.assetclassify;

import cn.zswltech.mithras.service.mapper.lib.assetclassify.AssetClassifyCheckContentLibMapper;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyCheckContent;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyCheckContentLib;
import cn.zswltech.mithras.service.service.lib.assetclassify.handler.impl.AssetClassifyCheckContentLibHandler;
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
