package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.blackgray;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.blackgray.application.audit.BlackGrayWarehouseTaskAuditService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.apache.commons.lang3.StringUtils.equalsAny;

/**
 * 合同创建
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:42 AM
 */
@Component
public class BlackGrayWarehouseTaskEndWorker extends AbstractProcessEndHandler {

    @Resource
    private BlackGrayWarehouseTaskAuditService blackGrayWarehouseTaskAuditService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.BLACK_GRAY_WAREHOUSE_TASK.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        blackGrayWarehouseTaskAuditService.finish(Long.parseLong(endContext.getBusinessKey()));
    }

}
