package cn.zswltech.mithras.projectprocess.application.projpricing;

public interface ProjPricingUpdateSupport {

    boolean canSave(Long projectId);

    boolean hasRelatedProcess(Long projectId);
}
