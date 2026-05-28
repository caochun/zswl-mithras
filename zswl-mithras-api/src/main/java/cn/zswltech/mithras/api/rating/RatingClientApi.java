package cn.zswltech.mithras.api.rating;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.ratingclient.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "客户评级-基本信息接口")
public interface RatingClientApi {
    @ApiOperation("修改区域模型定量指标数据")
    @PostMapping(path = "/rating/client/area/indicator/modify")
    R<Void> modifyAreaIndicator(@RequestBody @Valid RatingClientAreaIndicatorModifyREQ req);

    @ApiOperation("批量修改区域模型定量指标数据")
    @PostMapping(path = "/rating/client/area/indicator/batchmodify")
    R<Void> batchModifyAreaIndicator(@RequestBody @Valid RatingClientAreaIndicatorBatchModifyREQ req);

    @ApiOperation("客户评级新增")
    @PostMapping("/rating/client/add")
    R<RatingClientAddRSP> ratingClientAdd(@RequestBody @Valid RatingClientAddREQ req);

    @ApiOperation("客户评级准入校验")
    @PostMapping("/rating/client/accessCheck")
    R<RatingClientAccessCheckRSP> ratingClientAccessCheck(@RequestBody @Valid RatingClientAccessCheckREQ req);

    @ApiOperation("客户评级修改")
    @PostMapping("/rating/client/update")
    R<RatingClientUpdateRSP> ratingClientUpdate(@RequestBody @Valid RatingClientUpdateREQ req);

    @ApiOperation("客户评级删除")
    @PostMapping("/rating/client/delete")
    R<Void> ratingClientDelete(@RequestBody @Valid RatingClientDeleteREQ req);

//    @ApiOperation("答题卡选项恢复")
//    @PostMapping("/rating/client/restore")
//    R<RatingClientRestoreRSP> ratingClientRestore(@RequestBody @Valid RatingClientRestoreREQ req);

    @ApiOperation("客户信息")
    @PostMapping("/rating/client/info")
    R<RatingClientInfoRSP> ratingClientInfo(@RequestBody @Valid RatingClientInfoREQ req);

    @ApiOperation("模型获取")
    @PostMapping("/rating/modelQuery")
    R<List<RatingModelQueryRSP>> modelQuery(@RequestBody @Valid RatingModelQueryREQ req);

    @ApiOperation("问卷获取")
    @PostMapping("/rating/client/paramInfo")
    R<RatingParamInfoDuoApprovalRSP> paramInfo(@RequestBody @Valid RatingParamInfoREQ req);

    @ApiOperation("试算")
    @PostMapping("/rating/client/execute")
    R<RatingExecuteRSP> execute(@RequestBody @Valid RatingExecuteREQ req);

    @ApiOperation("确认完成评级/保存")
    @PostMapping("/rating/client/finish")
    R<Void> clientFinish(@RequestBody @Valid RatingClientFinishREQ req);

    @ApiOperation("客户评级列表")
    @PostMapping("/rating/client/page")
    R<PageR<RatingClientPageRSP>> ratingClientPage(@RequestBody @Valid RatingClientPageREQ req);

    @ApiOperation("客户评级详情")
    @PostMapping("/rating/client/detail")
    R<RatingClientDetailRSP> ratingClientDetail(@RequestBody @Valid RatingClientDetailREQ req);

    @ApiOperation("提交审批")
    @PostMapping("/rating/client/effect")
    R<Void> ratingClientEffect(@RequestBody @Valid RatingClientEffectREQ req);

    @ApiOperation("指标审批")
    @PostMapping("/rating/client/indexApproval")
    R<Void> ratingClientIndexApproval(@RequestBody @Valid RatingReportApprovalRSP req);

    @ApiOperation("评级报告")
    @PostMapping("/rating/client/report")
    R<RatingClientReportRSP> clientReport(@RequestBody RatingClientReportREQ req);

    @ApiOperation("评级推翻")
    @PostMapping("/rating/client/overturn")
    R<Void> overturn(@RequestBody @Valid RatingClientOverturnREQ req);

    @ApiOperation("评级调整")
    @PostMapping("/rating/client/adjust")
    R<Void> adjust(@RequestBody @Valid RatingClientOverturnREQ req);

//    @ApiOperation("评级推翻审核")
//    @PostMapping("/rating/client/overturn/approval")
//    R<Void> overturnApproval(@RequestBody RatingOverturnApprovalREQ req);

    @ApiOperation("摘要信息")
    @PostMapping("/rating/client/abstract")
    R<RatingClientAbstractRSP> clientAbstract(@RequestBody RatingClientAbstractREQ req);

    @ApiOperation("推翻记录")
    @PostMapping("/rating/client/overturnRecord")
    R<List<RatingClientOverturnRecordRSP>> clientOverturnRecord(@RequestBody RatingClientOverturnRecordREQ req);

    @ApiOperation("指标校验")
    @PostMapping("/rating/client/indexCheck")
    R<Void> clientIndexCheck(@RequestBody RatingClientIndexCheckREQ req);
}