package cn.zswltech.mithras.service.service.lib.assetclassify;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.mapper.lib.assetclassify.AssetClassifyNodeRecordLibMapper;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyNodeRecord;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyNodeRecordLib;
import cn.zswltech.mithras.service.service.lib.assetclassify.handler.impl.AssetClassifyNodeRecordLibHandler;
import cn.zswltech.mithras.common.util.StringUtil;
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
