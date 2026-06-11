package cn.zswltech.mithras.projectprocess.projlifecycle.application;

import cn.zswltech.mithras.projectprocess.projlifecycle.application.model.ProjectLifecycleEventProject;

public interface ProjectLifecycleEventReviewPort {

    ProjectLifecycleEventProject resolveProjectByReviewId(Long projReviewId);
}
