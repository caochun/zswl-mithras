package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyProcessService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description 资产五级分类评审会流程结束处理器
 */
@Component
public class AssetClassifyReviewMeetingProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AssetClassifyProcessService assetClassifyProcessService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.AssetClassifyReviewMeetingFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        assetClassifyProcessService.endReviewMeeting(endContext);
    }
}
