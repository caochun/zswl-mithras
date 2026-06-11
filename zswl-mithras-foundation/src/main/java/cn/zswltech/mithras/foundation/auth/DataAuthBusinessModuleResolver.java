package cn.zswltech.mithras.foundation.auth;

/**
 * Resolves module names from annotations to runtime business-module metadata.
 */
public interface DataAuthBusinessModuleResolver {

    DataAuthBusinessModule resolve(String moduleName);
}
