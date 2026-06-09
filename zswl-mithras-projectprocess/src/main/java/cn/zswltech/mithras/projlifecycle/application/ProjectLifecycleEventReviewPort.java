package cn.zswltech.mithras.projlifecycle.application;

import cn.zswltech.mithras.projlifecycle.application.model.ProjectLifecycleEventProject;

public interface ProjectLifecycleEventReviewPort {

    ProjectLifecycleEventProject resolveProjectByReviewId(Long projReviewId);
}
