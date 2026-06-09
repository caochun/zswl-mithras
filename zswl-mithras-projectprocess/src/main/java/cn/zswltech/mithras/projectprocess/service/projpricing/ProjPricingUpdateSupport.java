package cn.zswltech.mithras.projectprocess.service.projpricing;

public interface ProjPricingUpdateSupport {

    boolean canSave(Long projectId);

    boolean hasRelatedProcess(Long projectId);
}
