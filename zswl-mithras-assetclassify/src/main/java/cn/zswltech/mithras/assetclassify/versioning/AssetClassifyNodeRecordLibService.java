package cn.zswltech.mithras.assetclassify.versioning;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyNodeRecordLibMapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyNodeRecord;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyNodeRecordLib;
import cn.zswltech.mithras.assetclassify.versioning.handler.impl.AssetClassifyNodeRecordLibHandler;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Service
public class AssetClassifyNodeRecordLibService extends ServiceImpl<AssetClassifyNodeRecordLibMapper, AssetClassifyNodeRecordLib> {

    @Resource
    private AssetClassifyNodeRecordLibHandler libHandler;

    public AssetClassifyNodeRecord getByVersionAndNodeName(Long assetClassifyId, String version, String nodeName){
        AssetClassifyNodeRecordLib assetClassifyNodeRecordLib = baseMapper.selectOne(Wrappers.<AssetClassifyNodeRecordLib>lambdaQuery()
                .eq(AssetClassifyNodeRecordLib::getAssetClassifyId, assetClassifyId)
                .eq(AssetClassifyNodeRecordLib::getVersion, version)
                .eq(AssetClassifyNodeRecordLib::getNodeName, nodeName)
                .orderByDesc(AssetClassifyNodeRecordLib::getId)
                .last(StringUtil.mysqlLimitOne()));
        return ObjectUtil.isNull(assetClassifyNodeRecordLib) ? null :
                libHandler.actualLib2Entity(assetClassifyNodeRecordLib);
    }

}
