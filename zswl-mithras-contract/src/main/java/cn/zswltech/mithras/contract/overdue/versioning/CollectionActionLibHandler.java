package cn.zswltech.mithras.contract.overdue.versioning;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.contract.overdue.application.OverdueBusinessModule;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionActionDto;
import cn.zswltech.mithras.contract.overdue.model.OverdueCollectionAction;
import cn.zswltech.mithras.contract.overdue.model.OverdueCollectionActionLib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/8 16:25
 */
@Service
public class CollectionActionLibHandler extends
        LibAbstractHandler<OverdueCollectionActionLib, OverdueCollectionAction, CollectionActionDto> {

    @Override
    protected OverdueCollectionActionLib entity2Lib(OverdueCollectionAction f) {
        return BeanUtil.copyProperties(f, OverdueCollectionActionLib.class);
    }

    @Override
    protected OverdueCollectionAction lib2Entity(OverdueCollectionActionLib t) {
        return BeanUtil.copyProperties(t, OverdueCollectionAction.class);
    }

    @Override
    protected CollectionActionDto lib2Rsp(OverdueCollectionActionLib f) {
        return BeanUtil.copyProperties(f, CollectionActionDto.class);
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    public Enum<?> businessModuleEnum() {
        return OverdueBusinessModule.OVERDUE_COLLECTION_ACTION;
    }
}
