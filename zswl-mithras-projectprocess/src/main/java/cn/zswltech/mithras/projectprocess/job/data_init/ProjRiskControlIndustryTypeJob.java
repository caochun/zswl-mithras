package cn.zswltech.mithras.projectprocess.job.data_init;

import cn.zswltech.mithras.projectprocess.job.service.ProjRiskControlIndustryTypeJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @date 2025/5/15 18:20
 * @description
 */
@Slf4j
@Component
public class ProjRiskControlIndustryTypeJob {

    @Resource
    private ProjRiskControlIndustryTypeJobService projRiskControlIndustryTypeJobService;

    @XxlJob("projRiskControlIndustryTypeJob")
    public void projRiskControlIndustryTypeJob() {
        projRiskControlIndustryTypeJobService.projRiskControlIndustryTypeJob();
    }
}
