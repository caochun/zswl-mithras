package cn.zswltech.mithras.service.flow.listener.endhandler.blackgray;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundAddREQ;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayLibraryMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayManualOutboundMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.model.BlackGrayManualOutbound;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayOutboundAuditService;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.service.others.MithrasException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 合同创建
 *
 */
@Component
public class BlackGrayOutboundEndWorker extends AbstractProcessEndHandler {

    @Resource
    private BlackGrayOutboundAuditService blackGrayOutboundAuditService;
    @Resource
    private BlackGrayManualOutboundMapper blackGrayManualOutboundMapper;
    @Resource
    private BlackGrayLibraryMapper blackGrayLibraryMapper;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.BLACK_GRAY_OUTBOUND.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        //修改状态
        blackGrayOutboundAuditService.changeBusinessStatus(Long.parseLong(endContext.getBusinessKey()), (int) AuditStatusEnum.FINISH.getCode());
        BlackGrayManualOutbound blackGrayManualOutbound = blackGrayManualOutboundMapper.selectByPrimaryKey(Long.parseLong(endContext.getBusinessKey()));
        if(blackGrayManualOutbound == null){
            throw new MithrasException("突破记录不存在");
        }
        List<BlackGrayManualOutboundAddREQ.Reason> reasons = JSONUtil.toList(blackGrayManualOutbound.getApplyReason(), BlackGrayManualOutboundAddREQ.Reason.class);
        // 多条记录出库
        List<Long> outIds = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(reasons)){
            outIds.addAll(reasons.stream().filter(e -> ObjectUtil.equals(e.getStatus(), 1)).map(BlackGrayManualOutboundAddREQ.Reason::getId).collect(Collectors.toList()));
        } else {
            outIds.add(blackGrayManualOutbound.getBlackGrayId());
        }
        //出库 区分是否金控人员 按照
        outIds.forEach(id -> {
            BlackGrayLibrary blackGrayLibrary = new BlackGrayLibrary();
            blackGrayLibrary.setId(id);
            blackGrayLibrary.setStockStatus(1);
            blackGrayLibraryMapper.updateByPrimaryKeySelective(blackGrayLibrary);
        });
    }

}
