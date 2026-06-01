package cn.zswltech.mithras.service.service.lib.assetclassify.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;

import java.util.Collections;
import java.util.Set;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/5 7:30 下午
 **/
public abstract class AssetClassifyReviewAbstractLibHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    public String libMainIdFieldName() {
        return "asset_classify_client_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "asset_classify_client_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    protected String businessModuleName() {
        return "ASSET_CLASSIFY_REVIEW";
    }
}
