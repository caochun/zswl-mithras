package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewUpdateSupport;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProjReviewUpdateSupportAdapter implements ProjReviewUpdateSupport {

    @Resource
    private ProjReviewService projReviewService;

    @Override
    public boolean canSave(Long projectId) {
        return projReviewService.canSave(projectId);
    }

    @Override
    public boolean hasRelatedProcess(Long projectId) {
        return projReviewService.findRelatedProcess(projectId) != null;
    }
}
