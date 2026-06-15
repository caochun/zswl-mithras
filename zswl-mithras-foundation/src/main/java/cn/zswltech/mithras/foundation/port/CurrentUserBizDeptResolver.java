package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

/**
 * Resolves current user's business department for shared business modules.
 */
public interface CurrentUserBizDeptResolver {

    OrgDO currentUserBizDept();
}
