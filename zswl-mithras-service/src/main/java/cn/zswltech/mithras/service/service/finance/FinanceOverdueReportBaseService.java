package cn.zswltech.mithras.service.service.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueReportBaseAddREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueReportBaseListREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueReportBaseRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverduePlanStatueEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverdueRecordStatueEnum;
import cn.zswltech.mithras.service.mapper.finance.FinanceOverdueReportBaseMapper;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueIntegration;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueReportBase;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueSettlement;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
* @description 逾期报送计划表
* @author vico
* @date 2025-09-15
*/
@Service
public class FinanceOverdueReportBaseService extends ServiceImpl<FinanceOverdueReportBaseMapper, FinanceOverdueReportBase> {

    @Resource
    private FinanceOverdueReportBaseMapper financeOverdueReportBaseMapper;
    @Resource
    private FinanceOverdueIntegrationService financeOverdueIntegrationService;
    @Resource
    private FinanceOverdueSettlementService financeOverdueSettlementService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @Transactional(rollbackFor = Throwable.class)
    public Long add(FinanceOverdueReportBaseAddREQ req) {
        //检查
        req.setPlanDate(req.getPlanDate().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).minusDays(1));
        checkAdd(req);
        FinanceOverdueReportBase info = BeanUtil.copyProperties(req, FinanceOverdueReportBase.class);
        info.setReportStatus(OverduePlanStatueEnum.NEW.name());
        financeOverdueReportBaseMapper.insert(info);
        //创建明细
        financeOverdueIntegrationService.create(info.getId(), req.getPlanDate());
        financeOverdueSettlementService.create(info.getId(), req.getPlanDate());
        //回调待发起
        String format = info.getPlanDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        List<CommonProcessPrepare> commonProcessPrepares = commonProcessPrepareService.list(Wrappers.<CommonProcessPrepare>lambdaQuery()
                .eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.FinanceOverdue.name())
                .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name())
                .eq(CommonProcessPrepare::getBusinessData, format));
        if (ObjectUtil.isNotEmpty(commonProcessPrepares)) {
            commonProcessPrepares.forEach(e -> {
                e.setStatus(CommonProcessPrepareStatus.CLOSED.name());
            });
            commonProcessPrepareService.updateBatchById(commonProcessPrepares);
        }
        return info.getId();
    }

    private void checkAdd(FinanceOverdueReportBaseAddREQ req) {
        if (this.count(Wrappers.<FinanceOverdueReportBase>lambdaQuery()
                .eq(FinanceOverdueReportBase::getPlanDate, req.getPlanDate())
                .eq(FinanceOverdueReportBase::getReportStatus, OverduePlanStatueEnum.NEW.name())) > 0) {
            throw new MithrasException("报送计划已存在，请关闭后重新创建");
        }

    }

    public void modifyReportStatus(Long id) {
        FinanceOverdueReportBase originalInfo = financeOverdueReportBaseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<FinanceOverdueIntegration> integrationList = financeOverdueIntegrationService.list(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .eq(FinanceOverdueIntegration::getOverdueReportId, id));
        List<FinanceOverdueSettlement> overdueSettlementList = financeOverdueSettlementService.list(Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                .eq(FinanceOverdueSettlement::getOverdueReportId, id));
        long count = 0L;
        long sum = 0L;
        if (CollectionUtil.isNotEmpty(integrationList)) {
             count = count + integrationList.stream().filter(e -> OverdueRecordStatueEnum.REPORT_SUCCESS.name().equals(e.getRecordStatus())).count();
             sum = sum + integrationList.size();
        }
        if (CollectionUtil.isNotEmpty(overdueSettlementList)) {
            count = count + overdueSettlementList.stream().filter(e -> OverdueRecordStatueEnum.REPORT_SUCCESS.name().equals(e.getRecordStatus())).count();
            sum = sum + overdueSettlementList.size();
        }
        if (count > 0 && count < sum) {
            originalInfo.setReportStatus(OverduePlanStatueEnum.PART.name());
        } else if (count >= sum) {
            originalInfo.setReportStatus(OverduePlanStatueEnum.FINISHED.name());
        }
        updateById(originalInfo);
    }

    public Page<FinanceOverdueReportBase> list(FinanceOverdueReportBaseListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FinanceOverdueReportBase>lambdaQuery()
        .orderByDesc(FinanceOverdueReportBase::getPlanDate));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void close(FinanceOverdueReportBaseRemoveREQ req) {
        FinanceOverdueReportBase originalInfo = financeOverdueReportBaseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ObjectUtil.equals(originalInfo.getReportStatus(), OverduePlanStatueEnum.CLOSE.name())) {
            throw new MithrasException("该月份已关闭");
        }

        //判断是否有推送成功的
        if (financeOverdueIntegrationService.count(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .eq(FinanceOverdueIntegration::getOverdueReportId, req.getId())
                .in(FinanceOverdueIntegration::getApprovalStatus, ProjProcessState.APPROVAL_PASS.name(), ProjProcessState.UNDER_APPROVAL.name(),
                        ProjProcessState.NEW_APPROVAL_PASS.name(), ProjProcessState.NEW_UNDER_APPROVAL.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name(), ProjProcessState.CHANGING_UNDER_APPROVAL.name())) > 0){
            throw new MithrasException("存在审批中或审批通过数据，不可关闭");
        }

        //判断是否有推送成功的
        if (financeOverdueSettlementService.count(Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                .eq(FinanceOverdueSettlement::getOverdueReportId, req.getId())
                .in(FinanceOverdueSettlement::getApprovalStatus, ProjProcessState.APPROVAL_PASS.name(), ProjProcessState.UNDER_APPROVAL.name(),
                        ProjProcessState.NEW_APPROVAL_PASS.name(), ProjProcessState.NEW_UNDER_APPROVAL.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name(), ProjProcessState.CHANGING_UNDER_APPROVAL.name())) > 0){
            throw new MithrasException("存在审批中或审批通过数据，不可关闭");
        }
        //判断是否有推送成功的
        if (financeOverdueIntegrationService.count(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .eq(FinanceOverdueIntegration::getOverdueReportId, req.getId())
        .eq(FinanceOverdueIntegration::getRecordStatus, OverdueRecordStatueEnum.REPORT_SUCCESS.name())) > 0){
            throw new MithrasException("存在推送苍穹数据，不可关闭");
        }

        //判断是否有推送成功的
        if (financeOverdueSettlementService.count(Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                .eq(FinanceOverdueSettlement::getOverdueReportId, req.getId())
                .eq(FinanceOverdueSettlement::getRecordStatus, OverdueRecordStatueEnum.REPORT_SUCCESS.name())) > 0){
            throw new MithrasException("存在推送苍穹数据，不可关闭");
        }
        originalInfo.setReportStatus(OverduePlanStatueEnum.CLOSE.name());
        this.updateById(originalInfo);
    }

}