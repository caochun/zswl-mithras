package cn.zswltech.mithras.service.adapter.client.job;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.customer.application.client.ClientOverdueJobService;
import cn.zswltech.mithras.service.service.budget.EclExecuteClientPromotionResultService;
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
public class ClientOverdueJobServiceImpl implements ClientOverdueJobService {
    @Resource
    private EclExecuteClientPromotionResultService eclExecuteClientPromotionResultService;


    /**
     * 每天定时更新客户是否可上迁
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void clientPromotionByMonth(String jobParam) {
        try {
            int interval = 6;
            if (StrUtil.isNotBlank(jobParam)) {
                interval = Integer.parseInt(jobParam);
            }
            eclExecuteClientPromotionResultService.savePromotionResult(null, interval);
        } catch (Exception e) {
            log.error("每天定时更新客户是否可上迁任务执行异常" ,e);
        }
    }

}
