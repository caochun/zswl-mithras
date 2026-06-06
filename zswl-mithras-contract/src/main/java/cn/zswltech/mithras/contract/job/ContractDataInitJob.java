package cn.zswltech.mithras.contract.job;

import cn.zswltech.mithras.contract.application.job.ContractDataInitJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yangxiong
 * @date 2024/8/27/10:44
 * @description 合同数据初始化任务
 */
@Slf4j
@Component
public class ContractDataInitJob {

    @Resource
    private ContractDataInitJobService contractDataInitJobService;

    @XxlJob(value = "initReceiptStartDate")
    public void initReceiptStartDate() {
        contractDataInitJobService.initReceiptStartDate();
    }
}
