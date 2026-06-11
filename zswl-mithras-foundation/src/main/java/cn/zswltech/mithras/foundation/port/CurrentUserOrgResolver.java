package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

import java.util.List;

/**
 * Resolves current user's organization context for business domain modules.
 */
public interface CurrentUserOrgResolver {

    OrgDO getUserDept();

    List<OrgDO> getUserDeptList();
}
