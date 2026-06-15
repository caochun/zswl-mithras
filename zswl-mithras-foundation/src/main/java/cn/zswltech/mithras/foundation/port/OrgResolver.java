package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

import java.util.List;

/**
 * Resolves organization departments for shared domain services.
 */
public interface OrgResolver {

    List<OrgDO> listAllDept();
}
