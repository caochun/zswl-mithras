package cn.zswltech.mithras.blackgray.application.audit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalSubmitREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayApprovalSubmitRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseTaskApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.persistence.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.persistence.mapper.BlackGrayWarehouseTaskMapper;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayWarehouseTask;
import cn.zswltech.mithras.blackgray.application.port.BlackGrayApprovalProcessPort;
import cn.zswltech.mithras.blackgray.application.port.BlackGrayApprovalProcessType;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseTaskService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 黑灰名单审批-入库申请
 *
 */
@Slf4j
@Service
public class BlackGrayWarehouseTaskAuditService {

    @Resource
    private BlackGrayWarehouseTaskService blackGrayWarehouseTaskService;
    @Resource
    private BlackGrayWarehouseTaskMapper blackGrayWarehouseTaskMapper;
    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;
    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;
    @Resource
    private BlackGrayApprovalProcessPort blackGrayApprovalProcessPort;

    @Transactional(rollbackFor = Throwable.class)
    public BlackGrayApprovalSubmitRSP approvalSubmit(BlackGrayApprovalSubmitREQ req) {
        BlackGrayWarehouseTask detail = blackGrayWarehouseTaskService.detail(req.getId());

        if (ObjectUtil.isEmpty(detail)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkSubmit(req);
        changeBusinessStatus(req.getId(), (int) AuditStatusEnum.AUDIT.getCode(), null);
        String taskId = blackGrayApprovalProcessPort.start(BlackGrayApprovalProcessType.WAREHOUSE_TASK,
                detail.getId(), detail.getTaskNum(), null);
        //修改业务状态
        BlackGrayApprovalSubmitRSP blackGrayApprovalSubmitRSP = new BlackGrayApprovalSubmitRSP();
        blackGrayApprovalSubmitRSP.setBizId(req.getId());
        blackGrayApprovalSubmitRSP.setTaskId(taskId);
        return blackGrayApprovalSubmitRSP;
    }

    private void checkSubmit(BlackGrayApprovalSubmitREQ req){
        BlackGrayWarehouseTask blackGrayWarehouseTask = blackGrayWarehouseTaskService.detail(req.getId());
        if(ObjectUtil.isEmpty(blackGrayWarehouseTask)){
            throw new MithrasException("记录不存在");
        }
        if(ObjectUtil.equals(AuditStatusEnum.AUDIT.getCode(), blackGrayWarehouseTask.getAuditStatus()) || ObjectUtil.equals(AuditStatusEnum.FINISH.getCode(), blackGrayWarehouseTask.getAuditStatus())){
            throw new MithrasException("已在流程中或流程已结束，不可发起审批");
        }
    }

    public PageR<BlackGrayWarehouseTaskApprovalTaskRSP> auditList(BlackGrayWarehouseApprovalTaskREQ req){
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayWarehouseTaskApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPS = blackGrayWarehouseTaskMapper.queryForAudit(req);
        PageInfo<BlackGrayWarehouseTaskApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPPageInfo = new PageInfo<>(blackGrayWarehouseApprovalTaskRSPS);
        return PageR.of(blackGrayWarehouseApprovalTaskRSPS, blackGrayWarehouseApprovalTaskRSPPageInfo.getTotal());
    }

    public void changeBusinessStatus(Long id, Integer status, Long taskId){
        //修改业务状态
        blackGrayWarehouseTaskService.updateStatue(id, status, taskId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void finish(Long id) {
        changeBusinessStatus(id, (int) AuditStatusEnum.FINISH.getCode(), null);

        BlackGrayWarehouseTask blackGrayWarehouseTask = blackGrayWarehouseTaskService.detail(id);
        Example example = new Example(BlackGrayWarehouseRecord.class);
        example.createCriteria()
                .andEqualTo(BlackGrayWarehouseRecord.TASK_NUM, blackGrayWarehouseTask.getTaskNum());
        List<BlackGrayWarehouseRecord> blackGrayWarehouseRecords = blackGrayWarehouseRecordMapper.selectByExample(example);
        if (ObjectUtil.isEmpty(blackGrayWarehouseRecords)) {
            return;
        }

        List<BlackGrayLibrary> blackGrayLibraries = new ArrayList<>();
        blackGrayWarehouseRecords.forEach(blackGrayWarehouseRecord -> {
            BlackGrayLibrary blackGrayLibrary = BeanUtil.copyProperties(blackGrayWarehouseRecord, BlackGrayLibrary.class, "id");
            if (ObjectUtil.isEmpty(blackGrayLibrary.getWarehouseTime())) {
                blackGrayLibrary.setWarehouseTime(new Date());
            }
            blackGrayLibrary.setRecordId(blackGrayWarehouseRecord.getId());
            if (BlackGraySourceEnum.isExternal(blackGrayLibrary.getSource())) {
                blackGrayLibrary.setPlanOutboundTime(BlackGrayTypeEnum.getPlanOutboundTime(blackGrayLibrary.getWarehouseTime(), null));
            } else {
                blackGrayLibrary.setPlanOutboundTime(BlackGrayTypeEnum.getPlanOutboundTime(
                        blackGrayLibrary.getWarehouseTime(), BlackGrayTypeEnum.of(blackGrayLibrary.getBlackGrayType())));
            }
            blackGrayWarehouseRecord.setPlanOutboundTime(blackGrayLibrary.getPlanOutboundTime());
            if (ObjectUtil.isEmpty(blackGrayWarehouseRecord.getWarehouseTime())) {
                blackGrayWarehouseRecord.setWarehouseTime(blackGrayLibrary.getWarehouseTime());
            }
            blackGrayWarehouseRecordMapper.updateByPrimaryKey(blackGrayWarehouseRecord);

            Date date = new Date();
            blackGrayLibrary.setCreateTime(date);
            blackGrayLibrary.setUpdateTime(date);
            blackGrayLibrary.setReportFlag(YesOrNoNumberEnum.NO.getCode());
            blackGrayLibraries.add(blackGrayLibrary);

            if (ObjectUtil.isNotEmpty(blackGrayWarehouseRecord.getGroupBlackGrayType())
                    && BlackGrayTypeEnum.BLACK_LIST.name().equals(blackGrayWarehouseRecord.getBlackGrayType())) {
                blackGrayLibraryService.radiationSubsidiary(blackGrayLibrary);
            }
        });
        blackGrayLibraryService.attemptBatchWarehouse(blackGrayLibraries);
    }


}
