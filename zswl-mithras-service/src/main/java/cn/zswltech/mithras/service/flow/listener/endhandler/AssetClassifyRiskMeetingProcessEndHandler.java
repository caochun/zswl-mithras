package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyProcessService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/1/6
 * @description
 */
@Component
public class AssetClassifyRiskMeetingProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AssetClassifyProcessService assetClassifyProcessService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.AssetClassifyRiskMeetingFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        assetClassifyProcessService.endRiskMeeting(endContext);
    }
}
