package cn.zswltech.mithras.contract.job;

import cn.zswltech.mithras.contract.application.job.ContractJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/9/14
 * @description 合同相关定时任务
 */
@Slf4j
@Component
public class ContractJob {

    @Resource
    private ContractJobService contractJobService;

    @XxlJob("tryAutoStartRentJob")
    public void tryAutoStartRentJob() {
        contractJobService.tryAutoStartRentJob();
    }

    @XxlJob("contractStartRentRemindJobHandler")
    public ReturnT<String> contractStartRentRemindJobHandler(String param) {
        return contractJobService.contractStartRentRemindJobHandler(param);
    }
}
