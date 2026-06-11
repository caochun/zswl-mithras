package cn.zswltech.mithras.projectprocess.projlifecycle.application;

import cn.zswltech.mithras.projectprocess.projlifecycle.application.model.ProjectLifecycleEventProject;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.ProjLifecycleEventMapper;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ProjectLifecycleEventService {

    @Resource
    private ProjLifecycleAuthPort authPort;
    @Resource
    private ProjectLifecycleEventReviewPort eventReviewPort;
    @Resource
    private ProjLifecycleEventMapper projLifecycleEventMapper;

    public void add(String event, String eventType, String eventdesc, Long projId, String projType) {
        ProjLifecycleEvent projLifecycleEvent = new ProjLifecycleEvent();
        projLifecycleEvent.setEvent(event);
        projLifecycleEvent.setEventTime(LocalDateTime.now());
        projLifecycleEvent.setEventType(eventType);
        projLifecycleEvent.setOperator(authPort.currentUserId());
        projLifecycleEvent.setEventdesc(eventdesc);
        projLifecycleEvent.setProjId(projId);
        projLifecycleEvent.setProjType(projType);
        projLifecycleEventMapper.insert(projLifecycleEvent);
    }

    public void add(String event, String eventType, String eventdesc, Long projReviewId) {
        ProjectLifecycleEventProject project = eventReviewPort.resolveProjectByReviewId(projReviewId);
        add(event, eventType, eventdesc, project.getProjId(), project.getProjType());
    }
}
