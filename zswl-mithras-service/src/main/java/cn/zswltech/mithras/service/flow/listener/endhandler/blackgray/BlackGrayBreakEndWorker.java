package cn.zswltech.mithras.service.flow.listener.endhandler.blackgray;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayWarehouseAuditService;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 合同创建
 *
 */
@Component
public class BlackGrayBreakEndWorker extends AbstractProcessEndHandler {

    @Resource
    private BlackGrayWarehouseAuditService blackGrayWarehouseAuditService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.BLACK_GRAY_BREAK.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        //修改状态
        blackGrayWarehouseAuditService.changeBusinessStatus(Long.parseLong(endContext.getBusinessKey()), (int) AuditStatusEnum.FINISH.getCode());
    }

}
