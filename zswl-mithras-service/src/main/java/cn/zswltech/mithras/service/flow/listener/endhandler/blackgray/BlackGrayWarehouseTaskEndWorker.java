package cn.zswltech.mithras.service.flow.listener.endhandler.blackgray;
import cn.zswltech.mithras.common.util.StringUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseTask;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseTaskService;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayWarehouseTaskAuditService;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Resource
    private BlackGrayWarehouseTaskService blackGrayWarehouseTaskService;
    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;
    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.BLACK_GRAY_WAREHOUSE_TASK.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        //修改状态
        blackGrayWarehouseTaskAuditService.changeBusinessStatus(Long.parseLong(endContext.getBusinessKey()), (int) AuditStatusEnum.FINISH.getCode(), null);
        //同步数据到黑灰名单库
        BlackGrayWarehouseTask blackGrayWarehouseTask = blackGrayWarehouseTaskService.detail(Long.parseLong(endContext.getBusinessKey()));
        //获取任务下所有记录
        Example example = new Example(BlackGrayWarehouseRecord.class);
        example.createCriteria()
                .andEqualTo(BlackGrayWarehouseRecord.TASK_NUM, blackGrayWarehouseTask.getTaskNum());
        List<BlackGrayWarehouseRecord> blackGrayWarehouseRecords = blackGrayWarehouseRecordMapper.selectByExample(example);
        if(ObjectUtil.isEmpty(blackGrayWarehouseRecords)){
            return;
        }
        List<BlackGrayLibrary> blackGrayWarehouseRecordsList = new ArrayList<>();
        blackGrayWarehouseRecords.forEach(blackGrayWarehouseRecord -> {
            BlackGrayLibrary blackGrayLibrary = BeanUtil.copyProperties(blackGrayWarehouseRecord, BlackGrayLibrary.class, "id");
            if(ObjectUtil.isEmpty(blackGrayLibrary.getWarehouseTime())) {
                blackGrayLibrary.setWarehouseTime(new Date());
            }
            //非外部更新
            blackGrayLibrary.setRecordId(blackGrayWarehouseRecord.getId());
            if(BlackGraySourceEnum.isExternal(blackGrayLibrary.getSource())){
                blackGrayLibrary.setPlanOutboundTime(BlackGrayTypeEnum.getPlanOutboundTime(blackGrayLibrary.getWarehouseTime(), null));
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
            blackGrayWarehouseRecordsList.add(blackGrayLibrary);
            // 集团主企业是黑，子企业也变黑。 主企业是灰，子企业不会加入
            if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord.getGroupBlackGrayType()) && BlackGrayTypeEnum.BLACK_LIST.name().equals(blackGrayWarehouseRecord.getBlackGrayType())){
                blackGrayLibraryService.radiationSubsidiary(blackGrayLibrary);
            }
        });
        blackGrayLibraryService.attemptBatchWarehouse(blackGrayWarehouseRecordsList);
    }

}
