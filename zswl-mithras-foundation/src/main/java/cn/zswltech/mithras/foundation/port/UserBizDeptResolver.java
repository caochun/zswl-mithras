package cn.zswltech.mithras.foundation.port;

/**
 * Resolves a user's business department for shared business modules.
 */
public interface UserBizDeptResolver {

    String getBizDeptNameByUserId(Long userId);
}
