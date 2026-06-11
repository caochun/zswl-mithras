package cn.zswltech.mithras.foundation.auth;

/**
 * Checks whether current user can operate asset-manager-only data.
 */
public interface DataAuthAssetManagerGuard {

    void check();
}
