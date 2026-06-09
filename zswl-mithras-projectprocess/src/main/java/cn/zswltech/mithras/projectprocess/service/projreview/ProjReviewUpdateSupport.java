package cn.zswltech.mithras.projectprocess.service.projreview;

public interface ProjReviewUpdateSupport {

    boolean canSave(Long projectId);

    boolean hasRelatedProcess(Long projectId);
}
