package cn.zswltech.mithras.filingmaterials.job;

import cn.zswltech.mithras.filingmaterials.job.service.FilingMaterialEmailJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class FilingMaterialEmailJob {

    @Resource
    private FilingMaterialEmailJobService filingMaterialEmailJobService;

    @XxlJob("filingMaterialEmailJob")
    public void filingMaterialEmailJob() {
        try {
            filingMaterialEmailJobService.sendFilingMaterialEmail(XxlJobHelper.getJobParam());
        } catch (Exception e) {
            log.error("项目资料归档邮件发送任务失败:{}", e.getMessage());
        }
    }

    @XxlJob("projectFilingMaterialInitJob")
    public void projectFilingMaterialInitJob() {
        try {
            filingMaterialEmailJobService.initProjectFilingMaterial(XxlJobHelper.getJobParam());
        } catch (Exception e) {
            log.error("项目资料归档流程发起失败:{}", e.getMessage());
        }
    }
}
