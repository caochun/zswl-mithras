package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.vo.AccountVO;

import java.util.List;

/**
 * Resolves a user's visible department scope.
 */
public interface UserDataScopeResolver {

    /**
     * @return null means all departments are visible, empty means none are visible.
     */
    List<Long> canViewDeptIds(AccountVO accountVO);
}
