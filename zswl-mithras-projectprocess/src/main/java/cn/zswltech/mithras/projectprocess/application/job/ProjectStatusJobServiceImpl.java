package cn.zswltech.mithras.projectprocess.application.job;

import cn.zswltech.mithras.projectprocess.application.port.ProjectStatusSupportPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目状态维护job
 **/
@Slf4j
@Component
public class ProjectStatusJobServiceImpl implements ProjectStatusJobService {

    @Resource
    private ProjectStatusSupportPort projectStatusSupportPort;

    /**
     * 维护项目/授信相关立项/评审数据
     **/
    @Override
    public void projStatusJob() {
        try {
            //项目立项
            projectStatusSupportPort.expireProjEstablish(90);
            //项目评审
            projectStatusSupportPort.expireProjReview(365);
            //授信立项
            projectStatusSupportPort.expireGroupCreditEstablish(90);
            //授信评审
            projectStatusSupportPort.expireGroupCreditReview(365);
        } catch (Exception e) {
            log.warn("维护项目/授信相关立项/评审数据异常", e);
        }

    }

}
