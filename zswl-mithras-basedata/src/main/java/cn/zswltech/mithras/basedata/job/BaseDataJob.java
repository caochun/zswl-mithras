package cn.zswltech.mithras.basedata.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/12/5
 * @description
 */
@Slf4j
@Component
public class BaseDataJob {

    @Resource
    private BaseDataJobService baseDataJobService;

    /**
     * 汇率设置待办任务
     */
    @XxlJob("baseDataExchangeRateTodoJob")
    public void generateExchangeRateTodo() {
        baseDataJobService.generateExchangeRateTodo(XxlJobHelper.getJobParam());
    }

    /**
     * 放假调休数据导入提醒
     *
     * @return 任务执行结果
     */
    @XxlJob("specialDataRemindJob")
    public ReturnT<String> specialDataRemindJob() {
        return baseDataJobService.specialDataRemindJob();
    }

    /**
     * 当月LPR数据导入提醒
     *
     * @return 任务执行结果
     */
    @XxlJob("lprRemindJob")
    public ReturnT<String> lprRemindJob() {
        return baseDataJobService.lprRemindJob();
    }
}
