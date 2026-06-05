package cn.zswltech.mithras.afterlease.application.convert;

import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQueryClientInfo;
import cn.zswltech.mithras.afterlease.application.bo.AfterLeaseClientDataBO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/17 17:34
 */
@Mapper(componentModel = "spring")
public interface ExternalQueryConverter {

    AfterLeaseCheckExternalQueryListRsp entity2ListRsp(NewAfterLeaseCheckExternalQuery record);
    List<AfterLeaseCheckExternalQueryListRsp> entity2ListRsp(List<NewAfterLeaseCheckExternalQuery> records);

    AfterLeaseCheckExternalQueryDetailRsp entity2DetailRsp(NewAfterLeaseCheckExternalQuery entity);

    AfterLeaseCheckExternalQueryClientInfoListRsp clientInfoEntity2DetailRsp(NewAfterLeaseCheckExternalQueryClientInfo c);

    NewAfterLeaseCheckExternalQuery modifyReq2Entity(AfterLeaseCheckExternalQueryModifyReq req);

    NewAfterLeaseCheckExternalQuery conclusionModifyReq2Entity(AfterLeaseCheckExternalQueryConclusionModifyReq req);

    NewAfterLeaseCheckExternalQueryClientInfo clientInfoModifyReq2Entity(AfterLeaseCheckExternalQueryClientInfoModifyReq req);

    NewAfterLeaseCheckExternalQuery dataBo2Entity(AfterLeaseClientDataBO bo);
}
