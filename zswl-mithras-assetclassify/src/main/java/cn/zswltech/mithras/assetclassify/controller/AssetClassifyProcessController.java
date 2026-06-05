package cn.zswltech.mithras.assetclassify.controller;

import cn.zswltech.mithras.api.assetclassify.AssetClassifyProcessApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyReviewSubmitREQ;
import javax.validation.Valid;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyProcessApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class AssetClassifyProcessController implements AssetClassifyProcessApi {
    @Resource
    private AssetClassifyProcessApplicationService assetClassifyProcessApplicationService;

    @Override
    public R<String> startReviewMeeting(@Valid SinglePkREQ singlePkREQ) {
        return assetClassifyProcessApplicationService.startReviewMeeting(singlePkREQ);
    }

    @Override
    public R<String> startRiskMeeting(@Valid SinglePkREQ singlePkREQ) {
        return assetClassifyProcessApplicationService.startRiskMeeting(singlePkREQ);
    }

    @Override
    public R<String> startBoardMeeting(@Valid SinglePkREQ singlePkREQ) {
        return assetClassifyProcessApplicationService.startBoardMeeting(singlePkREQ);
    }

    @Override
    public R<Void> review(@Valid AssetClassifyReviewSubmitREQ req) {
        return assetClassifyProcessApplicationService.review(req);
    }

}
