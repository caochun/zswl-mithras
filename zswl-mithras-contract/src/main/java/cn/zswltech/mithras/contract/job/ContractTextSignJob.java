package cn.zswltech.mithras.contract.job;

import cn.zswltech.mithras.contract.application.port.ContractTextSignJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 合同文本用印
 */
@Slf4j
@Component
public class ContractTextSignJob {

    @Resource
    private ContractTextSignJobPort contractTextSignJobService;

    @XxlJob("contractTextSign")
    public void contractTextSign() throws Exception {
        contractTextSignJobService.contractTextSign();
    }

    @XxlJob("contractTextSignRent")
    public void contractTextSignRent() throws Exception {
        contractTextSignJobService.contractTextSignRent();
    }
}
