package cn.zswltech.mithras.workflow.process.prepare;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.IdPageREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListREQ;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.persistence.model.prepare.FinancingRepayActualProcessDetail;
import cn.zswltech.mithras.workflow.persistence.model.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.workflow.persistence.mapper.prepare.CommonProcessPrepareMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.notEqual;
import static cn.zswltech.mithras.foundation.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.foundation.enums.JobEnum.leaderincharge;
import static cn.zswltech.mithras.foundation.enums.JobEnum.projmanager;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus.AUTO_COMMITTED;
import static cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus.COMMITTED;

@Slf4j
@Service
public class CommonProcessPrepareService extends ServiceImpl<CommonProcessPrepareMapper, CommonProcessPrepare> {

    @Resource
    private List<AbstractFlowCommitHandle> abstractFlowCommitHandles;
    @Resource
    private FinancingRepayActualProcessDetailService financingRepayActualProcessDetailService;
    @Resource
    private RentCollectionMonthDetailService rentCollectionMonthDetailService;
    @Resource
    private WorkflowUserJobPort workflowUserJobPort;
    @Resource
    private ProcessPrepareCollectionPort processPrepareCollectionPort;
    @Resource
    private ProcessPrepareMessagePort processPrepareMessagePort;
    @Resource
    private ProcessPrepareRenderPort processPrepareRenderPort;
    @Resource
    private ExecutionApi executionApi;

    public void closeByBusinessId(Long businessId, String processType) {
        LambdaQueryWrapper<CommonProcessPrepare> query = Wrappers.lambdaQuery();
        query.eq(CommonProcessPrepare::getBusinessId, businessId);
        query.eq(CommonProcessPrepare::getProcessType, processType);
        query.ne(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.CLOSED.name());
        List<CommonProcessPrepare> prepareList = this.list(query);
        if (CollectionUtil.isNotEmpty(prepareList)) {
            List<CommonProcessPrepare> closeList = prepareList.stream()
                    .map(e -> CommonProcessPrepare.builder().id(e.getId()).status(CommonProcessPrepareStatus.CLOSED.name()).build())
                    .collect(Collectors.toList());
            this.updateBatchById(closeList);
        }
    }

    public void closeByBusinessData(String businessData, String processType) {
        LambdaQueryWrapper<CommonProcessPrepare> query = Wrappers.lambdaQuery();
        query.eq(CommonProcessPrepare::getBusinessData, businessData);
        query.eq(CommonProcessPrepare::getProcessType, processType);
        query.ne(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.CLOSED.name());
        List<CommonProcessPrepare> prepareList = this.list(query);
        if (CollectionUtil.isNotEmpty(prepareList)) {
            List<CommonProcessPrepare> closeList = prepareList.stream()
                    .map(e -> CommonProcessPrepare.builder().id(e.getId()).status(CommonProcessPrepareStatus.CLOSED.name()).build())
                    .collect(Collectors.toList());
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
        updateById(CommonProcessPrepare.builder().id(id).status(CommonProcessPrepareStatus.CLOSED.name()).build());
        for (AbstractFlowCommitHandle handle : abstractFlowCommitHandles) {
            if (handle.needHandle(prepare.getProcessType())) {
                handle.afterDiscard(prepare);
            }
        }
    }

    public Page<FinancingRepayActualProcessDetail> repayDetailList(IdPageREQ req) {
        LambdaQueryWrapper<FinancingRepayActualProcessDetail> wrapper =
                Wrappers.<FinancingRepayActualProcessDetail>lambdaQuery().eq(FinancingRepayActualProcessDetail::getPrepareId, req.getId());
        return financingRepayActualProcessDetailService.page(new Page<>(req.getPage(), req.getPageSize()), wrapper);
    }

    public Page<RentCollectionMonthDetail> detailList(IdPageREQ req) {
        LambdaQueryWrapper<RentCollectionMonthDetail> wrapper =
                Wrappers.<RentCollectionMonthDetail>lambdaQuery().eq(RentCollectionMonthDetail::getPrepareId, req.getId());
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<String> jobs = workflowUserJobPort.queryUserJobList(currentUserId);
        Set<String> jobSet = new HashSet<>(jobs);
        if (jobSet.contains(projmanager.name())
                && !jobSet.contains(leaderincharge.name())
                && !jobSet.contains(businesshead.name())) {
            wrapper.eq(RentCollectionMonthDetail::getSponsorId, currentUserId);
        }
        return rentCollectionMonthDetailService.page(new Page<>(req.getPage(), req.getPageSize()), wrapper);
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
        String status = StrUtil.equalsAny(prepare.getProcessType(), ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name())
                ? AUTO_COMMITTED.name() : COMMITTED.name();
        this.updateById(CommonProcessPrepare.builder().id(id).status(status).build());
    }

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
        LambdaUpdateWrapper<CommonProcessPrepare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CommonProcessPrepare::getId, prepare.getId());
        updateWrapper.set(CommonProcessPrepare::getStatus, COMMITTED.name());
        this.update(updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processApproved(String prepareId, Integer endType, String processInstanceId) {
        log.info("process prepare approved. prepareId:{}, endType:{}, processInstanceId:{}",
                prepareId, endType, processInstanceId);
        if (ProcessBusinessStatusEnum.success(endType)) {
            List<RentCollectionMonthDetail> detailList = rentCollectionMonthDetailService.byPrepareId(Long.valueOf(prepareId));
            Set<Long> sponsorIds = detailList.stream().map(RentCollectionMonthDetail::getSponsorId).collect(Collectors.toSet());
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(processInstanceId);
            req.setCcUserIdList(new ArrayList<>(sponsorIds));
            executionApi.cc(req);
        }
    }

    public byte[] previewNotify(Long id) {
        return processPrepareRenderPort.render(id);
    }

    public void downloadPreview(OutputStream os, Long id) {
        processPrepareRenderPort.downDocx(os, id);
    }

    @SneakyThrows
    public void downloadBatch(OutputStream outputStream, Long id) {
        LambdaQueryWrapper<RentCollectionMonthDetail> wrapper =
                Wrappers.<RentCollectionMonthDetail>lambdaQuery().eq(RentCollectionMonthDetail::getPrepareId, id);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<String> jobs = workflowUserJobPort.queryUserJobList(currentUserId);
        if (jobs.contains(projmanager.name())) {
            wrapper.eq(RentCollectionMonthDetail::getSponsorId, currentUserId);
        }
        List<RentCollectionMonthDetail> detailList = rentCollectionMonthDetailService.list(wrapper);
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
        List<RentCollectionMonthDetail> detailList = rentCollectionMonthDetailService.byPrepareId(id);
        if (!detailList.isEmpty()) {
            List<RentCollectionMonthDetail> updateList = new ArrayList<>();
            for (RentCollectionMonthDetail detail : detailList) {
                RentCollectionMonthDetail.RentCollectionMonthDetailBuilder builder = RentCollectionMonthDetail.builder();
                ProcessPrepareCollectionInfo collection = processPrepareCollectionPort.getById(detail.getCollectionId());
                if (collection == null) {
                    continue;
                }
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
                RentCollectionMonthDetail toUpdate = builder.build();
                if (isNotNull(toUpdate.getId())) {
                    updateList.add(toUpdate);
                }
            }
            if (!updateList.isEmpty()) {
                rentCollectionMonthDetailService.updateBatchById(updateList);
            }
        }
    }

    public void noticeMessage(CommonProcessPrepare prepare) {
        if (ObjectUtil.isEmpty(prepare)) {
            return;
        }
        processPrepareMessagePort.noticeMessage(prepare);
    }
}
