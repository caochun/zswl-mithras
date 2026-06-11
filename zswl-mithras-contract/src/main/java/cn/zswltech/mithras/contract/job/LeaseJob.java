package cn.zswltech.mithras.contract.job;

import cn.zswltech.mithras.contract.job.service.LeaseJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luyujie
 * @date 2025/12/25
 * @description 合同租赁物名称拼接调度
 */
@Slf4j
@Component
public class LeaseJob {

    @Resource
    private LeaseJobService leaseJobService;

    @XxlJob("setContractLeaseItemNameJob")
    public void setContractLeaseItemNameJob() {
        leaseJobService.setContractLeaseItemNameJob();
    }
}
