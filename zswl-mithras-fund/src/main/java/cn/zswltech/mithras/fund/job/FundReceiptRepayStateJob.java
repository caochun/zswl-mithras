package cn.zswltech.mithras.fund.job;

import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayStateCronService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 资金收付款模块状态更新任务
 *
 * @author wangchuanhao
 * @date 2023/2/20 10:56 AM
 */
@Component
@Slf4j
public class FundReceiptRepayStateJob {

    @Resource
    private FundReceiptRepayStateCronService fundReceiptRepayStateCronService;

    @XxlJob("fundReceiptRepayStateJob")
    @Transactional(rollbackFor = Exception.class)
    public void updateState() {
        try {
            fundReceiptRepayStateCronService.handleCronState();
        } catch (Exception e){
            log.error("fundReceiptRepayStateJob 错误", e);
        }
    }

}
