package cn.zswltech.mithras.service.service.lib.assetclassify.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;

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
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.ASSET_CLASSIFY;
    }
}
