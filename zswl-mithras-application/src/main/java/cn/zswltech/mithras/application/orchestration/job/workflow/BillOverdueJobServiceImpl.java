package cn.zswltech.mithras.application.orchestration.job.workflow;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.application.orchestration.adapter.third.providence.BillOverdueCommitHandle;
import cn.zswltech.mithras.workflow.process.prepare.job.BillOverdueJobService;
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
public class BillOverdueJobServiceImpl implements BillOverdueJobService {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @Override
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
