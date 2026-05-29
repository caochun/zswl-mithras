package cn.zswltech.mithras.service.service.lib.client.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 客户版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class ClientLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Transactional(rollbackFor = Exception.class)
    public void flushData(String version, Client client, boolean needClearLast, Integer versionType, Map<String, Object> extraMap) {
        if (!needHandle(client.getId(), ClientType.of(client.getClientType()))) {
            return;
        }
        flushData(version, client.getId(), needClearLast, versionType, extraMap);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param client
     * @param version
     */
    @Transactional(rollbackFor = Exception.class)
    public void reset(Client client, String version) {
        if (!needHandle(client.getId(), ClientType.of(client.getClientType()))) {
            return;
        }
        reset(client.getId(), version);
    }

    @Override
    public String libMainIdFieldName() {
        return "client_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "client_id";
    }

    public void validateData(Client client) {
    }

    public abstract InfoModule getSubModule();

    public abstract boolean needHandle(Long clientId, ClientType clientType);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.CLIENT;
    }

}
