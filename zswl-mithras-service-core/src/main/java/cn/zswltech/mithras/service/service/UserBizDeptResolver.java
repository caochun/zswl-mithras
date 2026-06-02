package cn.zswltech.mithras.service.service;

/**
 * Resolves a user's business department for shared business modules.
 */
public interface UserBizDeptResolver {

    String getBizDeptNameByUserId(Long userId);
}
