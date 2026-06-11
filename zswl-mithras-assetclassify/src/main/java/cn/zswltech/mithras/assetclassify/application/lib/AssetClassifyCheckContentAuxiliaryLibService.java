package cn.zswltech.mithras.assetclassify.application.lib;

import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyCheckContentAuxiliaryLibMapper;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyCheckContent;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyCheckContentAuxiliaryLib;
import cn.zswltech.mithras.assetclassify.application.lib.handler.impl.AssetClassifyCheckContentAuxiliaryLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/1/9
 * @description
 */
@Service
public class AssetClassifyCheckContentAuxiliaryLibService extends ServiceImpl<AssetClassifyCheckContentAuxiliaryLibMapper, AssetClassifyCheckContentAuxiliaryLib> {

    @Resource
    private AssetClassifyCheckContentAuxiliaryLibHandler libHandler;

    public List<AssetClassifyCheckContent> listByVersion(Long assetClassifyClientId, String version){
        List<AssetClassifyCheckContentAuxiliaryLib> assetClassifyCheckContentAuxiliaryLibs = baseMapper.selectList(Wrappers.<AssetClassifyCheckContentAuxiliaryLib>lambdaQuery()
                .eq(AssetClassifyCheckContentAuxiliaryLib::getAssetClassifyClientId, assetClassifyClientId)
                .eq(AssetClassifyCheckContentAuxiliaryLib::getVersion, version));
        return assetClassifyCheckContentAuxiliaryLibs.stream().map(libHandler::actualLib2Entity).collect(Collectors.toList());
    }
}
