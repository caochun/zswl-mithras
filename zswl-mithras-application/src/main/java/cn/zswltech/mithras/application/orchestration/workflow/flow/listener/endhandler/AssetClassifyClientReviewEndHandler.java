package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyVersionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/23 14:31
 */
@Component
public class AssetClassifyClientReviewEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AssetClassifyVersionService assetClassifyVersionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.AssetClassifyReview.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        // 同步更新状态
//        AssetClassifyClient classifyClient = assetClassifyClientService.getById(Long.valueOf(endContext.getBusinessKey()));
//        classifyClient.setReviewStatus(AssetClassifyReviewStatusEnum.FINISH.name());
//        assetClassifyClientService.updateById(classifyClient);
        // 这里还需要记录版本吗
        assetClassifyVersionService.reviewProcessEnd(endContext);
    }
}

