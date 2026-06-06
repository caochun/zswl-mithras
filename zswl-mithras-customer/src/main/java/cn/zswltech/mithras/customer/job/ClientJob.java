package cn.zswltech.mithras.customer.job;

import cn.zswltech.mithras.customer.application.client.ClientJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ClientJob {

    @Resource
    private ClientJobService clientJobService;

    @XxlJob("clientAuthTypeModify")
    public void clientAuthTypeModify() {
        clientJobService.clientAuthTypeModify();
    }

    @XxlJob("releaseClientJob")
    public void releaseClientJob() {
        clientJobService.releaseClient(XxlJobHelper.getJobParam());
    }

    @XxlJob("supplementClientCode")
    public void getClientCode() {
        clientJobService.supplementClientCode(XxlJobHelper.getJobParam());
    }
}
