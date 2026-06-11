package cn.zswltech.mithras.projectprocess.application.projestablish;

public interface ProjEstablishUpdateSupport {

    boolean canSave(Long projEstablishId);

    boolean hasRelatedProcess(Long projEstablishId);
}
