package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.service.service.process.prepare.handle.BillOverdueCommitHandle;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/10/27
 * @description
 */
@Slf4j
@Component
public class BillOverdueJob {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @XxlJob("billOverdueDraftGenerateTodoJob")
    public void billOverdueDraftGenerateTodoJob() {
        // 找到评审会秘书人员
        JobEnum jobEnum = JobEnum.secretaryjury;
        List<Long> targetUserIds = sysUserService.queryJobUserIds(jobEnum.name());
        if (CollectionUtil.isEmpty(targetUserIds)) {
            log.error("没有找到岗位为{}的用户，无法生成票交所逾期名单导入待办任务", jobEnum.display());
            return;
        }
        // 生成待办
        LocalDateTime now = LocalDateTime.now();
        CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                .processType(BillOverdueCommitHandle.PROCESS_TYPE)
                .formName(String.format("%s上海票交所逾期名单导入待办", LocalDateTimeUtil.format(now, "yyyy年MM月")))
                .currentNode(jobEnum.display() + "确认")
                .currentAssignee(JSONUtil.toJsonStr(targetUserIds))
                .applyTime(now)
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareService.save(commonProcessPrepare);
    }
}
