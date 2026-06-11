package cn.zswltech.mithras.contract.overdue.application.collection;

import cn.zswltech.mithras.contract.overdue.application.OverdueBusinessModule;
import cn.zswltech.mithras.contract.overdue.versioning.CollectionActionLibHandler;
import cn.zswltech.mithras.contract.overdue.model.OverdueCollectionAction;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/8 16:36
 */
@Service
public class CollectionVersionService extends CommonVersionService<OverdueCollectionAction> {
    @Resource
    private CollectionActionLibHandler collectionActionLibHandler;

    @Override
    public void customFlushData(OverdueCollectionAction overdueCollectionAction, String version, boolean needClearLastFlag, Integer versionType) {
        collectionActionLibHandler.flushData(version, overdueCollectionAction.getId(), needClearLastFlag, versionType);
    }

    @Override
    public void customReset(OverdueCollectionAction overdueCollectionAction, CommonVersion commonVersion) {

    }


    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        return null;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, OverdueCollectionAction baseModel, Map<Long, String> userNameMap) {
        return null;
    }

    @Override
    public Enum<?> getBusinessModule() {
        return OverdueBusinessModule.OVERDUE_COLLECTION_ACTION;
    }
}
