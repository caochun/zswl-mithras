package cn.zswltech.mithras.projectprocess.service.projestablish;

public interface ProjEstablishUpdateSupport {

    boolean canSave(Long projEstablishId);

    boolean hasRelatedProcess(Long projEstablishId);
}
