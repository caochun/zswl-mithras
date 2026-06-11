package cn.zswltech.mithras.api.rating;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.ratingamount.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(tags = "债项评级-基本信息接口")
public interface RatingAmountApi {

    @ApiOperation("债项评级列表")
    @PostMapping("/rating/amount/page")
    R<PageR<RatingAmountPageRSP>> ratingAmountPage(@RequestBody @Valid RatingAmountPageREQ req);

    @ApiOperation("债项评级详情")
    @PostMapping("/rating/amount/detail")
    R<RatingAmountDetailRSP> ratingAmountDetail(@RequestBody @Valid RatingAmountDetailREQ req);

    @ApiOperation("债项评级评估主体下拉框")
    @PostMapping("/rating/amount/lesseeInfo")
    R<RatingAmountInfoRSP> ratingAmountInfo(@RequestBody @Valid RatingAmountInfoREQ req);

    @ApiOperation("自动匹配模型")
    @PostMapping("/rating/amount/modelMatch")
    R<RatingAmountModelQueryRSP> ratingAmountModelQuery(@RequestBody @Valid RatingAmountModelQueryREQ req);

    @ApiOperation("债项评级新增")
    @PostMapping("/rating/amount/add")
    R<RatingAmountAddRSP> ratingAmountAdd(@RequestBody @Valid RatingAmountAddREQ req);

    @ApiOperation("债项评级准入校验")
    @PostMapping("/rating/amount/accessCheck")
    R<RatingAmountAccessCheckRSP> ratingAmountAccessCheck(@RequestBody @Valid RatingAmountAccessCheckREQ req);

    @ApiOperation("债项评级修改")
    @PostMapping("/rating/amount/update")
    R<RatingAmountUpdateRSP> ratingAmountUpdate(@RequestBody @Valid RatingAmountUpdateREQ req);

    @ApiOperation("债项评级删除")
    @PostMapping("/rating/amount/delete")
    R<Void> ratingAmountDelete(@RequestBody @Valid RatingAmountDeleteREQ req);

    @ApiOperation("债项评级项目信息")
    @PostMapping("/rating/amount/projInfo")
    R<RatingAmountProjInfoRSP> ratingAmountProjInfo(@RequestBody @Valid RatingAmountProjInfoREQ req);

    @ApiOperation("问卷获取")
    @PostMapping("/rating/amount/paramInfo")
    R<RatingParamInfoDuoApprovalRSP> paramInfo(@RequestBody @Valid RatingParamInfoREQ req);

    @ApiOperation("试算")
    @PostMapping("/rating/amount/execute")
    R<RatingExecuteRSP> execute(@RequestBody @Valid RatingExecuteREQ req);

    @ApiOperation("确认完成评级/保存")
    @PostMapping("/rating/amount/finish")
    R<Void> amountFinish(@RequestBody @Valid RatingAmountFinishREQ req);

    @ApiOperation("提交审批")
    @PostMapping("/rating/amount/effect")
    R<Void> ratingAmountEffect(@RequestBody @Valid RatingAmountEffectREQ req);

    @ApiOperation("指标审批")
    @PostMapping("/rating/amount/indexApproval")
    R<Void> ratingAmountIndexApproval(@RequestBody @Valid RatingReportApprovalRSP req);

    @ApiOperation("评级报告")
    @PostMapping("/rating/amount/report")
    R<RatingAmountReportRSP> amountReport(@RequestBody RatingAmountReportREQ req);

//    @PostMapping("/rating/amount/job")
//    R<Void> xxlJobTest();
}