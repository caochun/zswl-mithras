package cn.zswltech.mithras.service.job;

import cn.zswltech.mithras.service.util.StringUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessMigrationService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程图变更时同步更新在途流程的任务类
 * 该任务用于当流程定义发生变更时，自动将旧版本流程实例迁移到新版本
 * 保证在途流程能够按照最新的流程定义继续执行
 */
@Component
@Slf4j
public class SyncUpdateInProcessFlowOnDiagramChangeJob {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ProcessMigrationService processMigrationService;

    @XxlJob("SyncUpdateInProcessFlowOnDiagramChangeJob")
    public void execute() {
        // 1. 获取任务参数（从XXL-Job上下文获取）
        String processDefKey = XxlJobHelper.getJobParam();
        log.info("SyncUpdateInProcessFlowOnDiagramChangeJob任务，接收到输入参数processDefKey:{}", processDefKey);
        if (StringUtils.isEmpty(processDefKey)) {
            XxlJobHelper.handleFail("任务参数不能为空，请传入正确的参数");
            return;
        }
        // 2、获取最新流程版本以及流程上一版本id
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefKey)
                .orderByProcessDefinitionVersion() // 按版本号排序
                .desc() // 降序（最新版本在前）
                .list();
        if (processDefinitions.size() <= 1) {
            log.info("该流程不存在多版本，无需更新在途流程数据！");
            return;
        }
        String lastProcessId = processDefinitions.get(0).getId();
        String oldProcessId = processDefinitions.get(1).getId();
        // 3、执行迁移计划
        processMigrationService
                .createProcessInstanceMigrationBuilder()
                .migrateToProcessDefinition(lastProcessId)
                .migrateProcessInstances(oldProcessId);
    }

}
