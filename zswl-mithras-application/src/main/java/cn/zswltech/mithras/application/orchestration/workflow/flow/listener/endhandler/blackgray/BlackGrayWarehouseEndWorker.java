package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.blackgray;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayWarehouseAuditService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 合同创建
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:42 AM
 */
@Component
public class BlackGrayWarehouseEndWorker extends AbstractProcessEndHandler {

    @Resource
    private BlackGrayWarehouseAuditService blackGrayWarehouseAuditService;
    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;
    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.BLACK_GRAY_WAREHOUSE.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        //修改状态
        blackGrayWarehouseAuditService.changeBusinessStatus(Long.parseLong(endContext.getBusinessKey()), (int) AuditStatusEnum.FINISH.getCode());
        //同步数据到黑灰名单库
        BlackGrayWarehouseRecord blackGrayWarehouseRecord = blackGrayWarehouseRecordMapper.selectByPrimaryKey(Long.parseLong(endContext.getBusinessKey()));
        BlackGrayLibrary blackGrayLibrary = BeanUtil.copyProperties(blackGrayWarehouseRecord, BlackGrayLibrary.class, "id");
        blackGrayLibrary.setWarehouseTime(new Date());
        //非外部更新
        blackGrayLibrary.setRecordId(blackGrayWarehouseRecord.getId());
        if(BlackGraySourceEnum.isExternal(blackGrayLibrary.getSource())){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(blackGrayLibrary.getWarehouseTime());
            calendar.add(Calendar.MONTH, 36);
            blackGrayLibrary.setPlanOutboundTime(calendar.getTime());
        } else {
            blackGrayLibrary.setPlanOutboundTime(BlackGrayTypeEnum.getPlanOutboundTime(blackGrayLibrary.getWarehouseTime(), BlackGrayTypeEnum.of(blackGrayLibrary.getBlackGrayType())));
        }
        blackGrayWarehouseRecord.setPlanOutboundTime(blackGrayLibrary.getPlanOutboundTime());
        if(ObjectUtil.isEmpty(blackGrayWarehouseRecord.getWarehouseTime())){
            blackGrayWarehouseRecord.setWarehouseTime(blackGrayLibrary.getWarehouseTime());
        }
        blackGrayWarehouseRecordMapper.updateByPrimaryKey(blackGrayWarehouseRecord);
        //更新入库
        Date date = new Date();
        blackGrayLibrary.setCreateTime(date);
        blackGrayLibrary.setUpdateTime(date);
        //报送金控，目前未对接，写死0
        blackGrayLibrary.setReportFlag(YesOrNoNumberEnum.NO.getCode());
        blackGrayLibraryService.attemptBatchWarehouse(Collections.singletonList(blackGrayLibrary));
        //todo 这里拉黑子公司
        if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord.getGroupBlackGrayType())){
            blackGrayLibraryService.radiationSubsidiary(blackGrayLibrary);
        }
    }

}
