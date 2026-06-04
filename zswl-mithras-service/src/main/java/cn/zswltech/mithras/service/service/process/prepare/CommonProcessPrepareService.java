package cn.zswltech.mithras.service.service.process.prepare;
import cn.zswltech.mithras.workflow.application.process.prepare.RentCollectionMonthDetailService;
import cn.zswltech.mithras.workflow.application.process.prepare.FinancingRepayActualProcessDetailService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.IdPageREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import cn.zswltech.mithras.message.mapper.message.NoticeMessageBody;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.FinancingRepayActualProcessDetail;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.service.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.notEqual;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus.AUTO_COMMITTED;
import static cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus.COMMITTED;
import static cn.zswltech.mithras.service.enums.JobEnum.*;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author luyi
 */
@Slf4j
@Service
public class CommonProcessPrepareService extends ServiceImpl<CommonProcessPrepareMapper, CommonProcessPrepare> {
    //综合管理部
    @Value("${mithras.org.generalManagementCode}")
    private String generalManagementOrgCode;
    @Resource
    private List<AbstractFlowCommitHandle> abstractFlowCommitHandles;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;

    public void closeByBusinessId(Long businessId, String processType) {
        LambdaQueryWrapper<CommonProcessPrepare> query = Wrappers.lambdaQuery();
        query.eq(CommonProcessPrepare::getBusinessId, businessId);
        query.eq(CommonProcessPrepare::getProcessType, processType);
        query.ne(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.CLOSED.name());
        List<CommonProcessPrepare> commonProcessPrepareList = this.list(query);
        if (CollectionUtil.isNotEmpty(commonProcessPrepareList)) {
            List<CommonProcessPrepare> closeList = commonProcessPrepareList.stream().map(e -> CommonProcessPrepare.builder().id(e.getId()).status(CommonProcessPrepareStatus.CLOSED.name()).build()).collect(Collectors.toList());
            this.updateBatchById(closeList);
        }
    }

    public void closeByBusinessData(String businessData, String processType) {
        LambdaQueryWrapper<CommonProcessPrepare> query = Wrappers.lambdaQuery();
        query.eq(CommonProcessPrepare::getBusinessData, businessData);
        query.eq(CommonProcessPrepare::getProcessType, processType);
        query.ne(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.CLOSED.name());
        List<CommonProcessPrepare> commonProcessPrepareList = this.list(query);
        if (CollectionUtil.isNotEmpty(commonProcessPrepareList)) {
            List<CommonProcessPrepare> closeList = commonProcessPrepareList.stream().map(e -> CommonProcessPrepare.builder().id(e.getId()).status(CommonProcessPrepareStatus.CLOSED.name()).build()).collect(Collectors.toList());
            this.updateBatchById(closeList);
        }
    }

    public Page<CommonProcessPrepare> list(ProcessPrepareListREQ req) {
        Long currentUserId = req.getStartUserId() != null ? Long.valueOf(req.getStartUserId()) : AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<CommonProcessPrepare> wrapper = Wrappers.<CommonProcessPrepare>lambdaQuery()
                .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name())
                .eq(ObjectUtil.isNotEmpty(req.getBusinessId()), CommonProcessPrepare::getBusinessId, req.getBusinessId())
                .in(CollUtil.isNotEmpty(req.getProcessTypeList()), CommonProcessPrepare::getProcessType, req.getProcessTypeList())
                .like(isNotBlank(req.getFormName()), CommonProcessPrepare::getFormName, req.getFormName())
                .like(isNotBlank(req.getProcessInstanceId()), CommonProcessPrepare::getId, req.getProcessInstanceId())
                .apply(true, "json_contains(current_assignee,'" + currentUserId + "')")
                .orderByDesc(CommonProcessPrepare::getId);
        Page<CommonProcessPrepare> result = this.page(new Page<>(req.getPage(), req.getPageSize()), wrapper);
        if (CollectionUtil.isNotEmpty(result.getRecords())) {
            result.getRecords().forEach(e -> {
                if (Objects.isNull(e.getApplyTime())) {
                    // 用任务创建时间补全申请时间
                    e.setApplyTime(e.getCreateTime());
                }
            });
        }
        return result;
    }

    public Long myCount() {
        return list(new ProcessPrepareListREQ()).getTotal();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void discard(Long id) {
        CommonProcessPrepare prepare = getById(id);
        if (!prepare.getStatus().equals(CommonProcessPrepareStatus.PEND_COMMIT.name())) {
            err("该状态下不允许取消");
        }
        CommonProcessPrepare toUpdate = CommonProcessPrepare.builder().id(id).status(CommonProcessPrepareStatus.CLOSED.name()).build();
        updateById(toUpdate);
        for (AbstractFlowCommitHandle handle : abstractFlowCommitHandles) {
            if (handle.needHandle(prepare.getProcessType())) {
                handle.afterDiscard(prepare);
            }
        }
    }

    public Page<FinancingRepayActualProcessDetail> repayDetailList(IdPageREQ req) {
        LambdaQueryWrapper<FinancingRepayActualProcessDetail> wrapper =
                Wrappers.<FinancingRepayActualProcessDetail>lambdaQuery().eq(FinancingRepayActualProcessDetail::getPrepareId, req.getId());
        return getBean(FinancingRepayActualProcessDetailService.class).page(new Page<>(req.getPage(), req.getPageSize()), wrapper);
    }


    public Page<RentCollectionMonthDetail> detailList(IdPageREQ req) {
        LambdaQueryWrapper<RentCollectionMonthDetail> wrapper =
                Wrappers.<RentCollectionMonthDetail>lambdaQuery().eq(RentCollectionMonthDetail::getPrepareId, req.getId());
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<String> jobs = getBean(SysUserService.class).queryUserJobList(currentUserId);
        Set<String> jobSet = new HashSet<>(jobs);
        if (jobSet.contains(projmanager.name())
                && !jobSet.contains(leaderincharge.name())
                && !jobSet.contains(businesshead.name())) {
            //如果是项目经理，则认为是主办。所以看自己名下的。
            wrapper.eq(RentCollectionMonthDetail::getSponsorId, currentUserId);
        }
        return getBean(RentCollectionMonthDetailService.class).page(new Page<>(req.getPage(), req.getPageSize()), wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void commit(Long id) {
        CommonProcessPrepare prepare = getById(id);
        if (!prepare.getStatus().equals(CommonProcessPrepareStatus.PEND_COMMIT.name())) {
            err("该状态下不允许提交");
        }
        for (AbstractFlowCommitHandle handle : abstractFlowCommitHandles) {
            if (handle.needHandle(prepare.getProcessType())) {
                handle.commit(prepare);
            }
        }
        // 首次租后计划自动归为自动确认，避免页面展示
        String status = StrUtil.equalsAny(prepare.getProcessType(), ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name()) ? AUTO_COMMITTED.name() : COMMITTED.name();
        CommonProcessPrepare toUpdate = CommonProcessPrepare.builder().id(id).status(status).build();
        this.updateById(toUpdate);
    }

    //用于业务已经提交回调
    @Transactional(rollbackFor = Exception.class)
    public void commitCallback(String processType, String businessId) {
        CommonProcessPrepare prepare = this.getOne(Wrappers.<CommonProcessPrepare>lambdaQuery()
                .eq(CommonProcessPrepare::getProcessType, processType)
                .eq(CommonProcessPrepare::getBusinessId, businessId)
                .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name())
                .orderByAsc(CommonProcessPrepare::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(prepare)) {
            return;
        }
        //直接关闭，不做其他校验，如有需求，自实现
        LambdaUpdateWrapper<CommonProcessPrepare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CommonProcessPrepare::getId, prepare.getId());
        updateWrapper.set(CommonProcessPrepare::getStatus, COMMITTED.name());
        this.update(updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processApproved(String prepareId, Integer endType, String processInstanceId) {
        log.info("process prepare approved. prepareId:{}, endType:{}, processInstanceId:{}",
                prepareId, endType, processInstanceId);
        boolean passed = ProcessBusinessStatusEnum.success(endType);
        if (passed) {
//            //3. 抄送综合管理部门人员、主办
//            UserQuery query = new UserQuery();
//            query.setOrgCode(generalManagementOrgCode);
//            List<Long> idList = getBean(UserService.class).queryUserSys(query).getContents().stream().map(UserDO::getId).collect(toList());
            //抄送主办.根据每位客户所属的项目经理抄送给对应项目经理。
            List<RentCollectionMonthDetail> detaiList = getBean(RentCollectionMonthDetailService.class).byPrepareId(Long.valueOf(prepareId));
            Set<Long> sponsorIds = detaiList.stream().map(RentCollectionMonthDetail::getSponsorId).collect(Collectors.toSet());
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(processInstanceId);
            req.setCcUserIdList(new ArrayList<>(sponsorIds));
            getBean(ExecutionApi.class).cc(req);
        }
    }

    public byte[] previewNotify(Long id) {
        return getBean(CommonProcessPrepareRender.class).render(id);
    }

    public void downloadPreview(OutputStream os, Long id) {
        getBean(CommonProcessPrepareRender.class).downDocx(os, id);
    }

    @SneakyThrows
    public void downloadBatch(OutputStream outputStream, Long id) {
        LambdaQueryWrapper<RentCollectionMonthDetail> wrapper =
                Wrappers.<RentCollectionMonthDetail>lambdaQuery().eq(RentCollectionMonthDetail::getPrepareId, id);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<String> jobs = getBean(SysUserService.class).queryUserJobList(currentUserId);
        if (jobs.contains(projmanager.name())) {
            //如果是项目经理，则认为是主办。所以看自己名下的。
            wrapper.eq(RentCollectionMonthDetail::getSponsorId, currentUserId);
        }
        List<RentCollectionMonthDetail> detailList = getBean(RentCollectionMonthDetailService.class).list(wrapper);
        if (!detailList.isEmpty()) {
            int seq = 1;
            ZipOutputStream zos = new ZipOutputStream(outputStream);
            for (RentCollectionMonthDetail detail : detailList) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                downloadPreview(bos, detail.getId());
                ZipEntry zEntry = new ZipEntry(seq++ + "、" + detail.getContractCode() + "#" + detail.getRepayDate() + "-支付通知书.docx");
                zos.putNextEntry(zEntry);
                zos.write(bos.toByteArray());
                zos.closeEntry();
                zos.flush();
            }
            zos.close();
        }
    }

    public void refreshRentInfo(Long id) {
        CommonProcessPrepare prepare = getById(id);
        err(prepare.getStatus().equalsIgnoreCase(CommonProcessPrepareStatus.CLOSED.name()), "此状态下不可刷新");
        List<RentCollectionMonthDetail> detailList = getBean(RentCollectionMonthDetailService.class).byPrepareId(id);
        if (!detailList.isEmpty()) {
            List<RentCollectionMonthDetail> updateList = new ArrayList<>();
            for (RentCollectionMonthDetail detail : detailList) {
                RentCollectionMonthDetail.RentCollectionMonthDetailBuilder builder = RentCollectionMonthDetail.builder();
                //租金信息
                CollectionBaseInfo collection = getBean(CollectionBaseInfoService.class).getById(detail.getCollectionId());
                if (notEqual(detail.getRent(), collection.getPlanCollectionAmount())
                        || notEqual(detail.getPrincipal(), collection.getPrincipal())
                        || notEqual(detail.getRepayDate(), collection.getPlanCollectionDate())
                        || notEqual(detail.getInterest(), collection.getInterest())) {
                    builder.id(detail.getId())
                            .rent(collection.getPlanCollectionAmount())
                            .principal(collection.getPrincipal())
                            .repayDate(collection.getPlanCollectionDate())
                            .interest(collection.getInterest()).build();

                }
                //银行信息
//                Long pledgeId = detail.getPledgeId();
//                if (isNotNull(pledgeId)) {
//                    String financingType = detail.getFinancingType();
//                    if (indirect.name().equals(financingType)) {
//                        FundFinancingPledgeInfo pledgeInfo = getBean(FundFinancingPledgeInfoService.class).getById(pledgeId);
//                        if (isNotNull(pledgeInfo)) {
//                            if (notEqual(pledgeInfo.getAccountBank(), detail.getBankName())
//                                    || notEqual(pledgeInfo.getAccountName(), detail.getBankAccountName())
//                                    || notEqual(pledgeInfo.getAccountNumber(), detail.getBankAccountNumber())) {
//
//                                builder.id(detail.getId()).bankName(pledgeInfo.getAccountBank())
//                                        .bankAccountName(pledgeInfo.getAccountName())
//                                        .bankAccountNumber(pledgeInfo.getAccountNumber());
//                            }
//                        }
//                    } else {
//                        FundDirectFinancingPledgeInfo pledgeInfo = getBean(FundDirectFinancingPledgeInfoService.class).getById(pledgeId);
//                        if (isNotNull(pledgeInfo)) {
//                            if (notEqual(pledgeInfo.getAccountBank(), detail.getBankName())
//                                    || notEqual(pledgeInfo.getAccountName(), detail.getBankAccountName())
//                                    || notEqual(pledgeInfo.getAccountNumber(), detail.getBankAccountNumber())) {
//                                builder.id(detail.getId()).bankName(pledgeInfo.getAccountBank())
//                                        .bankAccountName(pledgeInfo.getAccountName())
//                                        .bankAccountNumber(pledgeInfo.getAccountNumber());
//                            }
//                        }
//                    }
//                } else {
//                    BaseDataBankAccount defaultBank = getBean(NextMonthRentNotify.class).defaultBank();
//                    if (isNotNull(defaultBank)) {
//                        if (notEqual(defaultBank.getAccountBank(), detail.getBankAccountNumber())
//                                || notEqual(defaultBank.getAccountName(), detail.getBankAccountName())
//                                || notEqual(defaultBank.getAccountNumber(), detail.getBankAccountNumber())) {
//                            builder.id(detail.getId()).bankName(defaultBank.getAccountBank())
//                                    .bankAccountName(defaultBank.getAccountName())
//                                    .bankAccountNumber(defaultBank.getAccountNumber());
//                        }
//                    }
//                }
                //
                RentCollectionMonthDetail toUpdate = builder.build();
                if (isNotNull(toUpdate.getId())) {
                    updateList.add(toUpdate);
                }

            }
            if (!updateList.isEmpty()) {
                getBean(RentCollectionMonthDetailService.class).updateBatchById(updateList);
            }
        }
    }

    //发送消息通知
    public void noticeMessage(CommonProcessPrepare prepare) {
        if (ObjectUtil.isEmpty(prepare)) {
            return;
        }
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.PROCESS_PREPARE.name());
        messageAddREQ.setContent(prepare.getBusinessId());
        messageAddREQ.setPcurl(String.format(MessageUrlEnum.PROCESS_PREPARE.pcUrl, prepare.getId()));
        List<Long> to = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(prepare.getCurrentAssignee())) {
            to.addAll(JSONUtil.toList(prepare.getCurrentAssignee(), Long.class));
        }
        messageAddREQ.setTo(to);
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setNeedQa(false);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.PROCESS_PREPARE.name());
        MessageModel messageModel = messageConvert.reqToMessage(messageAddREQ);
        NoticeMessageBody bodie = (NoticeMessageBody) messageModel.getBodie();
        // 先校验流程状态，流程状态为结清审批通过才支持修改合同状态，不然的话把合同状态的变更放到审批通过后去做
        bodie.setTitle(prepare.getFormName());
        //发送通知
        messageService.sendMessage(messageModel);
    }
}
