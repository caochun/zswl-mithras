package cn.zswltech.mithras.projectprocess.flow.listener.endhandler;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.ProjLifecycleEventMapper;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目全周期处理器
 *
 * @author wangchuanhao
 * @date 2022/11/9 3:59 PM
 */
public interface ILifecycleProcessor {

    /**
     * 处理
     * @param endContext
     */
    default void processLifecycle(ProcessEndContext endContext) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ProjLifecycleEventMapper projLifecycleEventMapper = ApplicationContextUtil.getBean(ProjLifecycleEventMapper.class);
                ProjLifecycleEvent endEvent = getLifecycleEvent(endContext);
                projLifecycleEventMapper.insert(endEvent);
            }
        });
    }

    /**
     * 处理
     * @param endContext
     */
    default ProjLifecycleEvent getLifecycleEvent(ProcessEndContext endContext) {
        FlowTaskApiService flowTaskApiService = ApplicationContextUtil.getBean(FlowTaskApiService.class);
        ProcessResp processResp = flowTaskApiService.queryProcessById(endContext.getProcessInstanceId());

        ProjLifecycleEvent endEvent = new ProjLifecycleEvent();
        endEvent.setEventTime(LocalDateTime.now());
        endEvent.setEventType(ProjLifecycleEventTypeEnum.APPROVAL.name());
        endEvent.setOperator(Long.valueOf(processResp.getLastOperatorId()));
        if (ProcessBusinessStatusEnum.success(endContext.getEndType())) {
            endEvent.setEventdesc(ProcessBusinessStatusEnum.PASS.getDisplay());
        } else if (ProcessBusinessStatusEnum.REJECT.getType().equals(endContext.getEndType()) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endContext.getEndType())) {
            endEvent.setEventdesc(ProcessBusinessStatusEnum.REJECT.getDisplay());
        } else if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType())) {
            endEvent.setEventdesc(ProcessBusinessStatusEnum.CANCEL.getDisplay());
        }
        customfillLifcycleEvent(endEvent, endContext);
        return endEvent;
    }

    /**
     * 批量插入
     * @param events
     */
    default void insertLifecycleEvents(List<ProjLifecycleEvent> events) {
        ProjLifecycleEventMapper projLifecycleEventMapper = ApplicationContextUtil.getBean(ProjLifecycleEventMapper.class);
        projLifecycleEventMapper.insertList(events);
    }

    /**
     * 自定义处理参数
     * @param endEvent
     * @param endContext
     */
    void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext);


    default void getProjIdAndProjType(ProjLifecycleEvent endEvent, ProjReviewBaseInfo baseInfo){
        String projType = baseInfo.getRelationDataType() == null ? ReviewRelationDataType.PROJ_ESTABLISH.name() : baseInfo.getRelationDataType();
        Long projId = baseInfo.getProjEstablishId();
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projType)) {
            projId = baseInfo.getId();
        }
        endEvent.setProjId(projId);
        endEvent.setProjType(projType);
    }

    default void getProjPricingIdAndProjType(ProjLifecycleEvent endEvent, ProjPricingBaseInfo baseInfo){
        String projType = baseInfo.getRelationDataType() == null ? ReviewRelationDataType.PROJ_ESTABLISH.name() : baseInfo.getRelationDataType();
        Long projId = baseInfo.getProjEstablishId();
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projType)) {
            projId = baseInfo.getId();
        }
        endEvent.setProjId(projId);
        endEvent.setProjType(projType);
    }
}
