package cn.zswltech.mithras.assetclassify.application.lib;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.AssetClassifyClientMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.lib.AssetClassifyClientLibMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClientLib;
import cn.zswltech.mithras.assetclassify.application.lib.handler.impl.AssetClassifyClientLibHandler;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2023/1/5 7:00 下午
 **/
@Service
public class AssetClassifyClientLibService extends ServiceImpl<AssetClassifyClientLibMapper, AssetClassifyClientLib> {

    @Resource
    private AssetClassifyClientLibHandler libHandler;
    @Resource
    private AssetClassifyClientMapper assetClassifyClientMapper;

    public AssetClassifyClient getByVersion(Long originId, String version) {
        AssetClassifyClientLib assetClassifyClientLib = baseMapper.selectOne(Wrappers.<AssetClassifyClientLib>lambdaQuery()
                .eq(AssetClassifyClientLib::getOriginId, originId)
                .eq(AssetClassifyClientLib::getVersion, version)
                .orderByDesc(AssetClassifyClientLib::getId)
                .last(StringUtil.mysqlLimitOne()));
        return ObjectUtil.isNull(assetClassifyClientLib) ? null : libHandler.actualLib2Entity(assetClassifyClientLib);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveList(String version,Long assetClassifyId){
        LambdaQueryWrapper<AssetClassifyClient> query = Wrappers.lambdaQuery();
        query.eq(AssetClassifyClient::getAssetClassifyId, assetClassifyId);
        List<AssetClassifyClient> clientList = assetClassifyClientMapper.selectList(query);
        if (CollectionUtil.isNotEmpty(clientList)){
            List<AssetClassifyClientLib> clientLibList = new ArrayList<>();
            for (AssetClassifyClient client : clientList) {
                AssetClassifyClientLib lib = BeanUtil.copyProperties(client, AssetClassifyClientLib.class,"createTime");
                lib.setVersion(version);
                lib.setVersionType(VersionTypeConstants.NORMAL);
                lib.setOriginId(client.getId());
                lib.setDataCreateTime(client.getCreateTime());
                lib.setDataCreateBy(client.getCreateBy());
                lib.setDataUpdateTime(client.getUpdateTime());
                lib.setDataUpdateBy(client.getUpdateBy());
                clientLibList.add(lib);
            }
            this.saveBatch(clientLibList);
        }
    }
}
