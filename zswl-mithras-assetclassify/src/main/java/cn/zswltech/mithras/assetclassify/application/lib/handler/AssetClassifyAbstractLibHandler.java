package cn.zswltech.mithras.assetclassify.application.lib.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;

import java.util.Collections;
import java.util.Set;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
public abstract class AssetClassifyAbstractLibHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    public String libMainIdFieldName() {
        return "asset_classify_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "asset_classify_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    protected String businessModuleName() {
        return "ASSET_CLASSIFY";
    }
}
