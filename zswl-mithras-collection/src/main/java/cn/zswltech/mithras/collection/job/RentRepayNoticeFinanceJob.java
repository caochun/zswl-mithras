package cn.zswltech.mithras.collection.job;

import cn.zswltech.mithras.collection.application.job.RentRepayNoticeFinanceJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yangxiong
 * @date 2023/11/22/18:51
 * @description 定时扫描租金还款表，提醒财务尽快核销
 */
@Slf4j
@Component
public class RentRepayNoticeFinanceJob {

    @Resource
    private RentRepayNoticeFinanceJobService rentRepayNoticeFinanceJobService;

    @XxlJob(value = "rentRepayNoticeFinance")
    public void rentRepayNoticeFinance() {
        rentRepayNoticeFinanceJobService.rentRepayNoticeFinance();
    }
}
