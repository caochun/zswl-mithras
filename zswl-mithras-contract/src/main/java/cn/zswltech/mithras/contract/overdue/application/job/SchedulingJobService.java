package cn.zswltech.mithras.contract.overdue.application.job;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/23 10:25
 */
public interface SchedulingJobService {

    /**
     * 每日更新客户逾期信息  更新任务
     */
    void overdueClientInfoUpdateTask();
}
