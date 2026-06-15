package cn.zswltech.mithras.foundation.port;

/**
 * Resolves whether the current user belongs to a business department.
 */
public interface CurrentUserBusinessDeptFlagResolver {

    boolean currentUserIsBizDept();
}
