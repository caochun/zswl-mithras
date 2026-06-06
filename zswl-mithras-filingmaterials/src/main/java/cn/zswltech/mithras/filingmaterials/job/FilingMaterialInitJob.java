package cn.zswltech.mithras.filingmaterials.job;

import cn.zswltech.mithras.filingmaterials.application.job.FilingMaterialInitJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class FilingMaterialInitJob {

    @Resource
    private FilingMaterialInitJobService filingMaterialInitJobService;

    @XxlJob("afterFilingMaterialInitJob")
    public void afterFilingMaterialInitJob() {
        try {
            filingMaterialInitJobService.initAfterFilingMaterial();
        } catch (Exception e) {
            log.error("租后资料归档流程发起初始化任务失败:{}", e.getMessage());
        }
    }

    @XxlJob("afterFilingMaterialBankJob")
    public void afterFilingMaterialBankJob() {
        try {
            filingMaterialInitJobService.returnAfterFilingMaterial(XxlJobHelper.getJobParam());
        } catch (Exception e) {
            log.error("租后资料归档系统退回:{}", e.getMessage());
        }
    }

    @XxlJob("fundFilingMaterialInitJob")
    public void fundFilingMaterialInitJob() {
        try {
            filingMaterialInitJobService.initFundFilingMaterial(XxlJobHelper.getJobParam());
        } catch (Exception e) {
            log.error("资金资料归档流程发起初始化任务失败:{}", e.getMessage());
        }
    }

    @XxlJob("projectFilingMaterialCloseJob")
    public void projectFilingMaterialCloseJob() {
        try {
            filingMaterialInitJobService.closeProjectFilingMaterial(XxlJobHelper.getJobParam());
        } catch (Exception e) {
            log.error("项目资料关闭流程关闭失败:{}", e.getMessage());
        }
    }
}
