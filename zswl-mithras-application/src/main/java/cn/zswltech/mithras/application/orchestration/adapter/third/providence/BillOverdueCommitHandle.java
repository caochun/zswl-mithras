package cn.zswltech.mithras.application.orchestration.adapter.third.providence;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.third.providence.service.impl.BillOverdueDraftService;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/10/24
 * @description
 */
@Slf4j
@Component
public class BillOverdueCommitHandle extends AbstractFlowCommitHandle {
    // 该待办没有流程，自定义一个key，前端需要用于区分
    public static final String PROCESS_TYPE = "overdueListTodo";

    @Resource
    private BillOverdueDraftService billOverdueDraftService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, PROCESS_TYPE);
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        billOverdueDraftService.effect();
        return "";
    }

    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        SpringUtil.getBean(BillOverdueDraftService.class).clear();
    }
}
