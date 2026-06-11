package cn.zswltech.mithras.projectprocess.application.projreview;

public interface ProjReviewUpdateSupport {

    boolean canSave(Long projectId);

    boolean hasRelatedProcess(Long projectId);
}
