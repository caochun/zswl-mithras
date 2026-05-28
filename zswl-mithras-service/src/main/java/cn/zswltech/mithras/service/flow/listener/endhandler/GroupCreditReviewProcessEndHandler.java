package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 集团授信评审模块流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class GroupCreditReviewProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private GroupCreditReviewService groupCreditReviewService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        groupCreditReviewService.processEnd(endContext.getModelKey(), Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
    }

}
