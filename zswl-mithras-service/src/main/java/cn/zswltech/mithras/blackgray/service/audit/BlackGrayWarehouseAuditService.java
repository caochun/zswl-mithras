package cn.zswltech.mithras.blackgray.service.audit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalSubmitREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayApprovalSubmitRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRecordService;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 黑灰名单审批-入库申请
 *
 */
@Slf4j
@Service
public class BlackGrayWarehouseAuditService {


    @Resource
    private BlackGrayWarehouseRecordService blackGrayWarehouseRecordService;
    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;
    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService processApiService;

    @Transactional(rollbackFor = Throwable.class)
    public BlackGrayApprovalSubmitRSP approvalSubmit(BlackGrayApprovalSubmitREQ req) {
        BlackGrayWarehouseRecord blackGrayWarehouseRecord = blackGrayWarehouseRecordMapper.selectByPrimaryKey(req.getId());
        if(ObjectUtil.isEmpty(blackGrayWarehouseRecord)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //todo 提交审批
        checkSubmit(req);
        //修改业务状态
        changeBusinessStatus(req.getId(), (int) AuditStatusEnum.AUDIT.getCode());
        // 生成流程实例
        StartProcessReq startProcessReq = buildCommonStartProcessReq(blackGrayWarehouseRecord);
        startProcessReq.setModelKey(ProcessModelTypeEnum.BLACK_GRAY_WAREHOUSE.name());
        processApiService.start(startProcessReq);

        return BeanUtil.copyProperties(null, BlackGrayApprovalSubmitRSP.class);
    }


    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return
     */
    private StartProcessReq buildCommonStartProcessReq(BlackGrayWarehouseRecord blackGrayWarehouseRecord) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(blackGrayWarehouseRecord.getId()));
        startProcessReq.setProcessInstanceName(blackGrayWarehouseRecord.getEnterpriseName());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(blackGrayWarehouseRecord.getApplyDept()).orElse(null));
        //增加法律合规部负责人
        startProcessReq.setVariables(MapUtil.of(
        ));
        return startProcessReq;
    }

    private void checkSubmit(BlackGrayApprovalSubmitREQ req){
        BlackGrayWarehouseRecord blackGrayWarehouseRecord = blackGrayWarehouseRecordMapper.selectByPrimaryKey(req.getId());
        if(ObjectUtil.isEmpty(blackGrayWarehouseRecord)){
            throw new MithrasException("记录不存在");
        }
        if(ObjectUtil.equals(AuditStatusEnum.AUDIT.getCode(), blackGrayWarehouseRecord.getAuditStatus()) || ObjectUtil.equals(AuditStatusEnum.FINISH.getCode(), blackGrayWarehouseRecord.getAuditStatus())){
            throw new MithrasException("已在流程中或流程已结束，不可发起审批");
        }
    }

    public PageR<BlackGrayWarehouseApprovalTaskRSP> auditList(BlackGrayApprovalTaskREQ req){
        if (sysUserService.getUserDept() == null) {
            throw new MithrasException("当前用户无机构");
        }
        req.setCurrentOperator(String.valueOf(AccountUtil.getLoginInfo().getId()));
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayWarehouseApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPS = blackGrayWarehouseRecordMapper.queryForAudit(req);
        PageInfo<BlackGrayWarehouseApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPPageInfo = new PageInfo<>(blackGrayWarehouseApprovalTaskRSPS);
        return PageR.of(blackGrayWarehouseApprovalTaskRSPS, blackGrayWarehouseApprovalTaskRSPPageInfo.getTotal());
    }


    /*@Transactional(rollbackFor = Throwable.class)
    public void doSomethingWhileFinish(AuditTask auditTask) {
        //修改状态
        changeBusinessStatus(auditTask.getBizId(), (int) AuditStatusEnum.FINISH.getCode());
        //同步数据到黑灰名单库
        BlackGrayWarehouseRecord blackGrayWarehouseRecord = blackGrayWarehouseRecordMapper.selectByPrimaryKey(auditTask.getBizId());
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
        blackGrayLibraryService.attemptBatchWarehouse(Collections.singletonList(blackGrayLibrary));
        //todo 这里拉黑子公司
        if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord.getGroupBlackGrayType())){
            blackGrayLibraryService.radiationSubsidiary(blackGrayLibrary);
        }
        // 发送通知
        BlackGrayWarehouseRecordDetailRSP noticeBusinessDO = getNoticeBusinessDO(auditTask);
        String content = String.format(
                AuditNoticeContentTemplateConstant.TODO_CONTENT,
                AuditNoticeModelAndUrlEnum.BLACK_GRAY_BUSINESS_BREAK.getModelName(),
                noticeBusinessDO.getEnterpriseName(),
                AuditStatusEnum.noticeStatusName(auditTask.getAuditStatus())
        );
        // 发通知到发起人
        String dealUser = getStartUser(auditTask.getId());
        String jumpUrl = String.format(AuditNoticeModelAndUrlEnum.BLACK_GRAY_WAREHOUSE.getDetailUrl(), auditTask.getBizId());
        Map<String,Object> params = buildParams(content, AuditNoticeModelAndUrlEnum.BLACK_GRAY_WAREHOUSE, NoticeTypeEnum.MESSAGE,dealUser,jumpUrl,ThreadContext.getUser().getRootOrg().getCode());
        sendNotice(auditTask,params);

        // 参数配置发送通知
        for(String noticeUser : commonServiceComponent.getFinishNoticeUser(RiskConfigTypeEnum.STAFF.getType())){
            // FIXME 优化
            Map<String,Object> param = buildParams(content, AuditNoticeModelAndUrlEnum.BLACK_GRAY_WAREHOUSE,NoticeTypeEnum.MESSAGE,noticeUser,jumpUrl,ThreadContext.getUser().getRootOrg().getCode());
            sendNotice(auditTask,param);
        }

        // 刷新黑灰名单统计缓存
        xxlJobHandler.blackGrayCount();
    }*/

    public void changeBusinessStatus(Long id, Integer status){
        //修改业务状态
        blackGrayWarehouseRecordService.updateStatue(id, status);
    }


}
