package cn.zswltech.mithras.financeprojectdistribution.job;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.financeprojectdistribution.service.FinanceProjectDistributionProcessPrepareService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @author linlili
 */
@Component
@Slf4j
public class ProjectDistributionInitJob {
    @Resource
    FinanceProjectDistributionProcessPrepareService projectDistributionService;


    /**
     * 项目利润分配
     */
    @XxlJob("projectDistributionInitJob")
    public void projectDistributionInitJob() {
        log.info("projectDistributionInitJob 开始扫描");
        String jobParam = XxlJobHelper.getJobParam();
        if (CharSequenceUtil.isEmpty(jobParam)) {
            log.info("项目利润分配流程待办生成失败，入参为空!");
            return;
        }
        List<String> list = new ArrayList<>(Arrays.asList(jobParam.split(",")));
        for (String contractId : list) {
            /*生成项目利润分配待办*/
            try {
                projectDistributionService.initProcessPrepare(Long.valueOf(contractId));
            } catch (Exception e) {
                log.error("项目利润分配流程待办生成失败:{}", e.getMessage());
            }
        }
    }

}
