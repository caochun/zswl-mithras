package cn.zswltech.mithras.projectprocess.job.data_init;

import cn.zswltech.mithras.projectprocess.job.service.ProjectApprovalAmountInitJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/8/27 08:52
 **/
@Slf4j
@Component
public class ProjectApprovalAmountInitJob {

    @Resource
    private ProjectApprovalAmountInitJobService projectApprovalAmountInitJobService;

    @XxlJob("projectApprovalAmountInitJob")
    public void projectApprovalAmountInitJob() {
        projectApprovalAmountInitJobService.projectApprovalAmountInitJob();
    }
}
