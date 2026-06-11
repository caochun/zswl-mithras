package cn.zswltech.mithras.assetclassify.versioning;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassify;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.assetclassify.versioning.handler.AssetClassifyAbstractLibHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Component
public class AssetClassifyLibVersionService extends CommonVersionService<AssetClassify> {
    @Autowired
    private List<AssetClassifyAbstractLibHandler> libHandlerList;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void customFlushData(AssetClassify assetClassify, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (AssetClassifyAbstractLibHandler libHandler : libHandlerList) {
            libHandler.flushData(version, assetClassify.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void customReset(AssetClassify assetClassify, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (AssetClassifyAbstractLibHandler libHandler : libHandlerList) {
            libHandler.reset(assetClassify.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        return null;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, AssetClassify baseModel, Map<Long, String> userNameMap) {
        return null;
    }

    @Override
    protected String getBusinessModuleName() {
        return "ASSET_CLASSIFY";
    }
}
