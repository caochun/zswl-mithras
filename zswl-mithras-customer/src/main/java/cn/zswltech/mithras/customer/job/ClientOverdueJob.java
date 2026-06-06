package cn.zswltech.mithras.customer.job;

import cn.zswltech.mithras.customer.application.client.ClientOverdueJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ClientOverdueJob {

    @Resource
    private ClientOverdueJobService clientOverdueJobService;

    @XxlJob("clientPromotionByMonth")
    public void clientPromotionByMonth() {
        clientOverdueJobService.clientPromotionByMonth(XxlJobHelper.getJobParam());
    }
}
