package cn.zswltech.mithras.projectprocess.adapter.projlifecycle;

import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.ProjectLifecycleEventReviewPort;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.model.ProjectLifecycleEventProject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProjectLifecycleEventReviewPortAdapter implements ProjectLifecycleEventReviewPort {

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    @Override
    public ProjectLifecycleEventProject resolveProjectByReviewId(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        String projType = projReviewBaseInfo.getRelationDataType() == null
                ? ReviewRelationDataType.PROJ_ESTABLISH.name()
                : projReviewBaseInfo.getRelationDataType();
        Long projId = projReviewBaseInfo.getProjEstablishId();
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projType)) {
            projId = projReviewBaseInfo.getId();
        }
        ProjectLifecycleEventProject project = new ProjectLifecycleEventProject();
        project.setProjId(projId);
        project.setProjType(projType);
        return project;
    }
}
