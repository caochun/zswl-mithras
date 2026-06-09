package cn.zswltech.mithras.contract.overdue.application.collection;

/**
 * Resolves whether a business record has uploaded or generated materials.
 */
public interface BusinessMaterialResolver {

    boolean hasMaterials(String businessType, Long belongId);
}
