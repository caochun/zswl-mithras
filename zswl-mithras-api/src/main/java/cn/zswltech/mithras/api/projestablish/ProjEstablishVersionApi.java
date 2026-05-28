package cn.zswltech.mithras.api.projestablish;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishEffectREQ;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDetailREQ;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDetailRSP;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description 立项信息版本表
 * @date 2022-07-19
 */
@Api(tags = "立项管理-版本管理接口")
public interface ProjEstablishVersionApi {

    /**
     * 立项信息生效（或提交审批）
     * @param req
     * @return
     */
    @ApiOperation("立项信息生效（或提交审批）")
    @PostMapping("/proj/establish/effect")
    R<Void> effect(@RequestBody @Valid ProjEstablishEffectREQ req);

    @ApiOperation("立项信息版本表列")
    @PostMapping("/proj/establish/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("立项信息版本比较详情（与上一版本比较）")
    @PostMapping("/proj/establish/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid ProjEstablishVersionDiffREQ req);

    @ApiOperation("立项版本详情接口")
    @PostMapping("/proj/establish/version/detail")
    R<ProjEstablishVersionDetailRSP> versionDetail(@RequestBody @Valid ProjEstablishVersionDetailREQ req);
}