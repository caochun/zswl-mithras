package cn.zswltech.mithras.api.assetclassify;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyReviewSubmitREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Api(tags = "资产五级分类流程相关接口")
public interface AssetClassifyProcessApi {
    @ApiOperation("发起评审会流程")
    @PostMapping("/assetclassify/process/reviewmeeting/submit")
    R<String> startReviewMeeting(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("发起风委会流程")
    @PostMapping("/assetclassify/process/riskmeeting/submit")
    R<String> startRiskMeeting(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("发起董事会流程")
    @PostMapping("/assetclassify/process/boardmeeting/submit")
    R<String> startBoardMeeting(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("复核审批（提交审批）")
    @PostMapping("/assetclassify/flow/review/submit")
    R<Void> review(@RequestBody @Valid AssetClassifyReviewSubmitREQ req);

}
