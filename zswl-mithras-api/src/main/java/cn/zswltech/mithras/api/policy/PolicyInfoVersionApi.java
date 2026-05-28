package cn.zswltech.mithras.api.policy;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.policy.*;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2023-06-15
 **/
@Api(tags = "保单版本接口")
public interface PolicyInfoVersionApi {

    @ApiOperation("提交审批")
    @PostMapping("/policy/info/effect")
    R<Void> effect(@RequestBody @Valid PolicyInfoEffectREQ req);

    @ApiOperation("取消操作")
    @PostMapping("/policy/info/cancel")
    R<Void> cancel(@RequestBody @Valid PolicyInfoCancelREQ req);

    @ApiOperation("保单版本表列")
    @PostMapping("/policy/info/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("保单信息版本比较详情（与上一版本比较）")
    @PostMapping("/policy/info/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid PolicyInfoVersionDiffREQ req);


}
