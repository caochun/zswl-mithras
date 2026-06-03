package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.projlifecycle.mapper.model.ProjLifecycleEvent;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.service.Listener.ProjReviewApprovalPassEvent;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ProjReviewCreateFlow;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ProjReviewModifyFlow;

/**
 * 项目评审流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class ProjReviewProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {

    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientService clientService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProjReviewCreateFlow.name(),
                ProjReviewModifyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        Long projReviewId = Long.valueOf(endContext.getBusinessKey());
        projReviewService.processEnd(projReviewId,
                endContext.getEndType(), Long.valueOf(endContext.getStartUserId()),
                endContext.getProcessInstanceId(), endContext.getModelKey());
        processLifecycle(endContext);
        //项目评审生效时，给信息岗发送消息通知
        sendMessage(projReviewId);

        // 先注释，覆盖合同信息时会生成新版本，有可能把合同的未提交数据生成了新版本，这里存在问题，需重新考虑实现方式
//        // 项目评审通过，异步发送事件
//        boolean pass = ProcessBusinessStatusEnum.success(endContext.getEndType());
//        if (pass) {
//            ThreadPoolUtil.getCommonPool().execute(() -> applicationEventPublisher.publishEvent(new ProjReviewApprovalPassEvent(projReviewId)));
//        }
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper
                .selectById(Long.valueOf(endContext.getBusinessKey()));
        getProjIdAndProjType(endEvent, projReviewBaseInfo);
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey()))
                .map(ProcessEventDescEnum::getEvent).orElse("立项评审审批"));

    }

    private void sendMessage(Long projReviewId) {
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        if (Objects.nonNull(baseInfo)) {
            Client client = clientService.getById(baseInfo.getClientId());
            //客户编码不为空，不处理
            if (Objects.nonNull(client) && CharSequenceUtil.isBlank(client.getClientCode())) {
                List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.InformationTechnologyPost.name());
                if (CollUtil.isNotEmpty(userIds)) {
                    for (Long userId : userIds) {
                        MessageAddREQ addRequest = new MessageAddREQ();
                        addRequest.setTo(Collections.singletonList(userId));
                        addRequest.setMessageType(MessageTypeEnum.PROJ_REVIEW_AUDITED.name());
                        addRequest.setNeedOa(true);
                        addRequest.setNoticeSource(BusinessModuleEnum.PROJ_REVIEW.name());
                        addRequest.setPcurl(String.format(MessageUrlEnum.CLIENT_NEW.pcUrl, client.getId(), client.getClientType()));
                        addRequest.setFrom("系统通知");
                        addRequest.setFlowid(UUID.randomUUID().toString());
                        addRequest.setRelation(String.format("项目【%s】评审通过，需至客商系统创建客户【%s】", baseInfo.getProjName(), client.getClientName()));
                        addRequest.setBusinessId(baseInfo.getId().toString());
                        addRequest.setContent(client.getClientName());
                        messageService.sendMessage(messageConver.reqToMessage(addRequest));
                    }
                }
            }
        }
    }
}
