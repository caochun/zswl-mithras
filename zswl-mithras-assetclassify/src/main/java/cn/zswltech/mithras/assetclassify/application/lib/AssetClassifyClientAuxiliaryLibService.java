package cn.zswltech.mithras.assetclassify.application.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyCommonService;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientListREQ;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.assetclassify.application.lib.handler.impl.AssetClassifyClientAuxiliaryLibHandler;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Service
public class AssetClassifyClientAuxiliaryLibService extends ServiceImpl<AssetClassifyClientAuxiliaryLibMapper, AssetClassifyClientAuxiliaryLib> {
    @Resource
    private AssetClassifyCommonService assetClassifyCommonService;
    @Resource
    private AssetClassifyClientAuxiliaryLibHandler libHandler;

    public List<AssetClassifyClientAuxiliaryLib> listSpecificVersionData(Long assetClassifyId, String version) {
        LambdaQueryWrapper<AssetClassifyClientAuxiliaryLib> query = Wrappers.lambdaQuery();
        query.eq(AssetClassifyClient::getAssetClassifyId, assetClassifyId);
        query.eq(AssetClassifyClientAuxiliaryLib::getVersion, version);
        return this.list(query);
    }

    public Page<AssetClassifyClientAuxiliaryLib> pageList(AssetClassifyClientListREQ req) {
        // 分页条件
        Page<AssetClassifyClientAuxiliaryLib> pageQuery = new Page<>();
        pageQuery.setCurrent(req.getPage());
        pageQuery.setSize(req.getPageSize());
        // 业务条件
        LambdaQueryWrapper<AssetClassifyClientAuxiliaryLib> conditionQuery = assetClassifyCommonService.buildQuery(req);
        conditionQuery.eq(AssetClassifyClientAuxiliaryLib::getVersion, req.getVersion());
        // 分页查询结果
        return this.page(pageQuery, conditionQuery);
    }

    public AssetClassifyClient getByVersion(Long originId, String version) {
        AssetClassifyClientAuxiliaryLib assetClassifyClientLib = baseMapper.selectOne(Wrappers.<AssetClassifyClientAuxiliaryLib>lambdaQuery()
                .eq(AssetClassifyClientAuxiliaryLib::getOriginId, originId)
                .eq(AssetClassifyClientAuxiliaryLib::getVersion, version)
                .orderByDesc(AssetClassifyClientAuxiliaryLib::getId)
                .last(StringUtil.mysqlLimitOne()));
        return ObjectUtil.isNull(assetClassifyClientLib) ? null : libHandler.actualLib2Entity(assetClassifyClientLib);
    }

    public List<AssetClassifyClientAuxiliaryLib> newestClassifyClientLib(Long mainId) {
        return baseMapper.newestClassifyClientLib(mainId);
    }

    public List<AssetClassifyClientAuxiliaryLib> lastThreeNewestClassifyClientLib(Long mainId) {
        return baseMapper.newestClassifyClientLib(mainId).stream()
                .filter(lib -> "SECONDARY".equals(lib.getClassifyResult())
                        || "SUSPICIOUS".equals(lib.getClassifyResult())
                        || "LOSS".equals(lib.getClassifyResult()))
                .collect(Collectors.toList());
    }

    public AssetClassifyClientAuxiliaryLib getEffectLatestOneByClientId(Long clientId) {
        LambdaQueryWrapper<AssetClassifyClientAuxiliaryLib> query = Wrappers.lambdaQuery();
        query.eq(AssetClassifyClient::getClientId, clientId);
        query.eq(AssetClassifyClientAuxiliaryLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(AssetClassifyClient::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}
