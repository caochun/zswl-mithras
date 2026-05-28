package cn.zswltech.mithras.service.job;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.service.budget.EclExecuteClientPromotionResultService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 客户相关权限
 * @author: jackerhe
 * @date: 2023/11/7 2:21 下午
 **/
@Slf4j
@Component
public class ClientOverdueJob {
    @Resource
    private EclExecuteClientPromotionResultService eclExecuteClientPromotionResultService;


    /**
     * 每天定时更新客户是否可上迁
     */
    @XxlJob("clientPromotionByMonth")
    @Transactional(rollbackFor = Throwable.class)
    public void clientPromotionByMonth() {
        try {
            int interval = 6;
            String jobParam = XxlJobHelper.getJobParam();
            if (StrUtil.isNotBlank(jobParam)) {
                interval = Integer.parseInt(jobParam);
            }
            eclExecuteClientPromotionResultService.savePromotionResult(null, interval);
        } catch (Exception e) {
            log.error("每天定时更新客户是否可上迁任务执行异常" ,e);
        }
    }

}
