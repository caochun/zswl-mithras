package cn.zswltech.mithras.api.groupcreditestablish;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishRatingCheckRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.version.GroupCreditEstablishEffectREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.version.GroupCreditEstablishVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Api(tags = "集团授信立项管理-版本管理接口")
public interface GroupCreditEstablishVersionApi {

    /**
     * 立项信息生效（或提交审批）
     * @param req
     * @return
     */
    @ApiOperation("集团授信立项信息生效（或提交审批）")
    @PostMapping("/group/credit/establish/effect")
    R<Void> effect(@RequestBody @Valid GroupCreditEstablishEffectREQ req);

    @ApiOperation("集团授信立项信息版本表列")
    @PostMapping("/group/credit/establish/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("集团授信立项信息版本比较详情（与上一版本比较）")
    @PostMapping("/group/credit/establish/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid GroupCreditEstablishVersionDiffREQ req);


    @ApiOperation("检查立项相关评级信息")
    @PostMapping("/group/credit/establish/rating/check")
    R<GroupCreditEstablishRatingCheckRSP> checkRatingInfo(@RequestBody @Valid SinglePkREQ req);
}