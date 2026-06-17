package cn.zswltech.mithras.projectprocess.job;

import cn.zswltech.mithras.projectprocess.application.job.ProjectStatusJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目状态维护job
 **/
@Slf4j
@Component
public class ProjectStatusJob {

    @Resource
    private ProjectStatusJobService projectStatusJobService;

    /**
     * 维护项目/授信相关立项/评审数据
     **/
    @XxlJob("projStatusJob")
    public void projStatusJob() {
        projectStatusJobService.projStatusJob();
    }
}
