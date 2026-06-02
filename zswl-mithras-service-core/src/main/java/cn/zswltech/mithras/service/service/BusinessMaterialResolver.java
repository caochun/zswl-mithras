package cn.zswltech.mithras.service.service;

/**
 * Resolves whether a business record has uploaded or generated materials.
 */
public interface BusinessMaterialResolver {

    boolean hasMaterials(String businessType, Long belongId);
}
