package cn.zswltech.mithras.foundation.port;

/**
 * Resolves organization ids by organization code.
 */
public interface OrgCodeResolver {

    Long getOrgIdByCode(String orgCode);
}
