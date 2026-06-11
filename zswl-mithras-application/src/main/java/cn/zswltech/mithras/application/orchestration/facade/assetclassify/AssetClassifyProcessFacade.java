package cn.zswltech.mithras.application.orchestration.facade.assetclassify;

import cn.zswltech.mithras.assetclassify.application.AssetClassifyProcessApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyReviewSubmitREQ;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyProcessService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Service
public class AssetClassifyProcessFacade implements AssetClassifyProcessApplicationService {
    @Resource
    private AssetClassifyProcessService assetClassifyProcessService;

    @Resource
    private AssetClassifyVersionService assetClassifyVersionService;

    @Override
    public R<String> startReviewMeeting(@Valid SinglePkREQ singlePkREQ) {
        return R.ok(assetClassifyProcessService.startReviewMeeting(singlePkREQ.getId()));
    }

    @Override
    public R<String> startRiskMeeting(@Valid SinglePkREQ singlePkREQ) {
        return R.ok(assetClassifyProcessService.startRiskMeeting(singlePkREQ.getId()));
    }

    @Override
    public R<String> startBoardMeeting(@Valid SinglePkREQ singlePkREQ) {
        return R.ok(assetClassifyProcessService.startBoardMeeting(singlePkREQ.getId()));
    }

    @Override
    /*@DataAuthCheck(keyFieldName = "id", checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = "ASSET_CLASSIFY_REVIEW")*/
    // 资产管理岗进行复核确认,无需审批流程直接生效
    // 需求变动，需要审核
    public R<Void> review(@Valid AssetClassifyReviewSubmitREQ req) {
        assetClassifyVersionService.reviewSubmit(req);
        return R.ok();
    }
}
