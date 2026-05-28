package cn.zswltech.mithras.service.service.lib.afterlease;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.AfterLeaseCheckExternalQueryLibAbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class AfterLeaseCheckExternalQueryVersionService extends CommonVersionService<NewAfterLeaseCheckExternalQuery> {
    @Autowired
    private List<AfterLeaseCheckExternalQueryLibAbstractHandler> libHandlerList;

    @Override
    public void customFlushData(NewAfterLeaseCheckExternalQuery newAfterLeaseCheckExternalQuery, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (AfterLeaseCheckExternalQueryLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, newAfterLeaseCheckExternalQuery.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public void customReset(NewAfterLeaseCheckExternalQuery newAfterLeaseCheckExternalQuery, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (AfterLeaseCheckExternalQueryLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(newAfterLeaseCheckExternalQuery.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, NewAfterLeaseCheckExternalQuery baseModel, Map<Long, String> userNameMap) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.AFTER_LEASE_CHECK_EXTERNAL_QUERY;
    }
}
