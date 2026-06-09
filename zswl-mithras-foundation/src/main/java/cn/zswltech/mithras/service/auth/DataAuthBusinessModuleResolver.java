package cn.zswltech.mithras.service.auth;

/**
 * Resolves module names from annotations to runtime business-module metadata.
 */
public interface DataAuthBusinessModuleResolver {

    DataAuthBusinessModule resolve(String moduleName);
}
