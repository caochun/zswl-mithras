package cn.zswltech.mithras.api.projlifecycle;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.dto.projlifecycle.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2022-10-21
 **/
@Api(tags = "项目全周期-接口")
public interface ProjectLifecycleAPI {

    @ApiOperation("项目各阶段数据统计")
    @GetMapping("/proj/lifecycle/quantity/count")
    R<ProjStageTotalRSP> count();

    @ApiOperation("项目阶段列表")
    @PostMapping("/proj/lifecycle/list")
    R<PageR<ProjectLifecycleListRSP>> list(@RequestBody @Valid ProjectLifecycleListREQ req);

    @ApiOperation("项目周期基本信息")
    @PostMapping("/proj/lifecycle/detail")
    R<ProjectLifecycleDetailRSP> detail(@RequestBody @Valid ProjectLifecycleDetailREQ req);

    @ApiOperation("项目立项卡片")
    @PostMapping("/proj/lifecycle/projestablish/card")
    R<ProjectLifecycleProjestablishCardRSP> projestablishCard(@RequestBody @Valid ProjectLifecycleCardREQ req);

    @ApiOperation("项目评审卡片")
    @PostMapping("/proj/lifecycle/projreview/card")
    R<ProjectLifecycleProjreviewCardRSP> projreviewCard(@RequestBody @Valid ProjectLifecycleCardREQ req);

    @ApiOperation("项目合同卡片")
    @PostMapping("/proj/lifecycle/contract/card")
    R<List<ProjectLifecycleContractCardRSP>> contractCard(@RequestBody @Valid ProjectLifecycleCardREQ req);

    @ApiOperation("项目租后检查卡片")
    @PostMapping("/proj/lifecycle/afterleasecheck/card")
    R<ProjectLifecycleAfterLeaseCheckCardRSP> afterLeaseCheckCard(@RequestBody @Valid ProjectLifecycleCardREQ req);

    @ApiOperation("租金催收卡片")
    @PostMapping("/proj/lifecycle/rentcollection/card")
    R<List<RentCollectionListRSP>> rentCollectionListCard(@RequestBody @Valid ProjectLifecycleCardREQ req);

    @ApiOperation("项目旅程")
    @PostMapping("/proj/lifecycle/milestone")
    R<PageR<ProjectLifecycleMilestoneRSP>> milestone(@RequestBody @Valid ProjectLifecycleEventREQ req);

}
