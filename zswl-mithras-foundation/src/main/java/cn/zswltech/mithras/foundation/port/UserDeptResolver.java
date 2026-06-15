package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

import java.util.List;

/**
 * Resolves departments attached to a specific user.
 */
public interface UserDeptResolver {

    List<OrgDO> userDeptList(Long userId);
}
