package cn.zswltech.mithras.service.service.projlifecycle;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.projlifecycle.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.mapper.model.projlifecycle.ProjLifecycleEvent;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projlifecycle.ProjLifecycleEventMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @create: 2022-10-28
 **/
@Slf4j
@Service
public class ProjectLifecycleEventService {
    @Resource
    private ProjLifecycleEventMapper projLifecycleEventMapper;

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    public void add(String event,String eventType,String eventdesc,Long projId,String projType){
        ProjLifecycleEvent projLifecycleEvent = new ProjLifecycleEvent();
        projLifecycleEvent.setEvent(event);
        projLifecycleEvent.setEventTime(LocalDateTime.now());
        projLifecycleEvent.setEventType(eventType);
        projLifecycleEvent.setOperator(AccountUtil.getLoginInfo().getId());
        projLifecycleEvent.setEventdesc(eventdesc);
        projLifecycleEvent.setProjId(projId);
        projLifecycleEvent.setProjType(projType);
        projLifecycleEventMapper.insert(projLifecycleEvent);
    }

    public void add(String event,String eventType,String eventdesc,Long projReviewId){
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        String projType = projReviewBaseInfo.getRelationDataType() == null ? ReviewRelationDataType.PROJ_ESTABLISH.name() : projReviewBaseInfo.getRelationDataType();
        Long projId = projReviewBaseInfo.getProjEstablishId();
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projType)) {
            projId = projReviewBaseInfo.getId();
        }
        add(event,eventType,eventdesc,projId,projType);
    }

}
