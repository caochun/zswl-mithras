package cn.zswltech.mithras.capital.job;

import cn.zswltech.mithras.capital.application.job.CQFinanceJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 维护苍穹推送拉取数据job
 *
 * @author: jackerhe
 * @date: 2024/6/4 3:40 下午
 **/
@Slf4j
@Component
public class CQFinanceJob {

    @Resource
    private CQFinanceJobService cqFinanceJobService;

    // 每日晚上9点 银行流水还未核销完毕，也触发收款单的统一推送
    @XxlJob("sendWriteOffNotice")
    public void sendWriteOffNotice() {
        cqFinanceJobService.sendWriteOffNotice();
    }

    // 每日拉取本月银行流水
    @XxlJob("fullFlowRecord")
    public void fullFlowRecord() {
        cqFinanceJobService.fullFlowRecord();
    }

    // 每日拉取保融银行流水
    @XxlJob("fullBRFlowRecord")
    public void fullBRFlowRecord() {
        cqFinanceJobService.fullBRFlowRecord();
    }
}
