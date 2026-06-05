package cn.zswltech.mithras.projlifecycle.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.projlifecycle.ProjectLifecycleAPI;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.dto.projlifecycle.*;
import cn.zswltech.mithras.projlifecycle.application.ProjectLifecycleApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class ProjectLifecycleController implements ProjectLifecycleAPI {

    @Resource
    private ProjectLifecycleApplicationService projectLifecycleApplicationService;

    @Override
    public R<ProjStageTotalRSP> count() {
        return projectLifecycleApplicationService.count();
    }

    @Override
    public R<PageR<ProjectLifecycleListRSP>> list(@Valid ProjectLifecycleListREQ req) {
        return projectLifecycleApplicationService.list(req);
    }

    @Override
    public R<ProjectLifecycleDetailRSP> detail(@Valid ProjectLifecycleDetailREQ req) {
        return projectLifecycleApplicationService.detail(req);
    }

    @Override
    public R<ProjectLifecycleProjestablishCardRSP> projestablishCard(@Valid ProjectLifecycleCardREQ req) {
        return projectLifecycleApplicationService.projestablishCard(req);
    }

    @Override
    public R<ProjectLifecycleProjreviewCardRSP> projreviewCard(@Valid ProjectLifecycleCardREQ req) {
        return projectLifecycleApplicationService.projreviewCard(req);
    }

    @Override
    public R<List<ProjectLifecycleContractCardRSP>> contractCard(@Valid ProjectLifecycleCardREQ req) {
        return projectLifecycleApplicationService.contractCard(req);
    }

    @Override
    public R<ProjectLifecycleAfterLeaseCheckCardRSP> afterLeaseCheckCard(@Valid ProjectLifecycleCardREQ req) {
        return projectLifecycleApplicationService.afterLeaseCheckCard(req);
    }

    @Override
    public R<List<RentCollectionListRSP>> rentCollectionListCard(@Valid ProjectLifecycleCardREQ req) {
        return projectLifecycleApplicationService.rentCollectionListCard(req);
    }

    @Override
    public R<PageR<ProjectLifecycleMilestoneRSP>> milestone(@Valid ProjectLifecycleEventREQ req) {
        return projectLifecycleApplicationService.milestone(req);
    }
}
