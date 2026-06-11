package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.projectprocess.flow.listener.endhandler.ILifecycleProcessor;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.customer.mapper.model.client.ClientTransfer;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.application.orchestration.client.ClientTransferService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ClientTransferFlow;

/**
 * 客户转交流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class ClientTransferProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {

    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ClientTransferFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        clientTransferService.processEnd(endContext.getBusinessKey(), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
        ProjLifecycleEvent lifecycleEvent = getLifecycleEvent(endContext);
        List<ClientTransfer> clientTransfers = clientTransferService.getBaseMapper().selectList(Wrappers.<ClientTransfer>lambdaQuery().eq(ClientTransfer::getBatchNo, endContext.getBusinessKey()));
        if (CollectionUtil.isNotEmpty(clientTransfers)) {
            List<Long> ids = clientTransfers.stream().map(ClientTransfer::getClientId).distinct().collect(Collectors.toList());
            List<ProjEstablishBaseInfo> projEstablishBaseInfos = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().in(ProjEstablishBaseInfo::getClientId, ids));
            if (CollectionUtil.isNotEmpty(projEstablishBaseInfos)) {
                List<ProjLifecycleEvent> events = new LinkedList<>();
                for (ProjEstablishBaseInfo baseInfo : projEstablishBaseInfos) {
                    ProjLifecycleEvent event = new ProjLifecycleEvent();
                    BeanUtil.copyProperties(lifecycleEvent, event);
                    event.setProjId(baseInfo.getId());
                    event.setProjType(ReviewRelationDataType.PROJ_ESTABLISH.name());
                    events.add(event);
                }
                insertLifecycleEvents(events);
            }
        }
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("客户移交审批"));
    }
}
