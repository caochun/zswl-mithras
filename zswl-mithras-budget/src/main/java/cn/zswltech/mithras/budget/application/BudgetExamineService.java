package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.budget.application.BudgetExamineApplicationService;
import cn.zswltech.mithras.budget.application.BudgetExamineFlowService;
import cn.zswltech.mithras.budget.domain.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.domain.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessState;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetExamineMapper;
import cn.zswltech.mithras.finance.mapper.finance.FinanceSubjectBalanceAssistMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetExamine;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceSubjectBalanceAssist;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算考核
* @author vico
* @date 2025-04-11
*/
@Service
public class BudgetExamineService extends ServiceImpl<BudgetExamineMapper, BudgetExamine> implements BudgetExamineApplicationService {

    @Resource
    private BudgetExamineMapper budgetExamineMapper;
    @Resource
    private FinanceSubjectBalanceAssistMapper financeSubjectBalanceAssistMapper;
    @Resource
    private BudgetExamineBenefitService budgetExamineBenefitService;
    @Resource
    private BudgetExamineBudgetExecuteService budgetExamineBudgetExecuteService;
    @Resource
    private BudgetExaminePayPlanExecuteService budgetExaminePayPlanExecuteService;
    @Resource
    private BudgetExamineFlowService budgetExamineFlowService;
    @Resource
    private Id2NameService id2NameService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(BudgetExamineAddREQ req) {
        //判断是否有上月数据
        LocalDate month = LocalDate.of(req.getExamineYear(), req.getExamineMonth(), 1);
        LocalDate lastMonth = month.minusMonths(1);
        BudgetExamineDetailREQ detailREQ = new BudgetExamineDetailREQ();
        detailREQ.setExamineYear(lastMonth.getYear());
        detailREQ.setExamineMonth(lastMonth.getMonthValue());
        if (ObjectUtil.isEmpty(this.detail(detailREQ))) {
            throw new MithrasException("上月考核表尚未创建，请先创建上一月的考核表");
        }
        //判断是否有苍穹数据
        if (!(financeSubjectBalanceAssistMapper.selectCount(Wrappers.<FinanceSubjectBalanceAssist>lambdaQuery()
        .eq(FinanceSubjectBalanceAssist::getYear, req.getExamineYear())
        .eq(FinanceSubjectBalanceAssist::getMonth, req.getExamineMonth())) > 0)) {
            throw new MithrasException("苍穹数据尚未同步，无法生成考核表，请先完成苍穹数据同步");
        }
        //查询是否已有对应月份数据
        detailREQ.setExamineYear(req.getExamineYear());
        detailREQ.setExamineMonth(req.getExamineMonth());
        BudgetExamineDetailRSP oldDetail = this.detail(detailREQ);
        // 只有未提交、审批取消、审批拒绝状态下才允许重新新建新数据覆盖旧数据
        if (Objects.nonNull(oldDetail)) {
            if (StrUtil.equals(oldDetail.getApprovalStatus(), ProcessState.COMMIT.name())) {
                throw new MithrasException("当前月份数据审批中，不允许重新创建");
            }
        }
        BudgetExamine info = BeanUtil.copyProperties(req, BudgetExamine.class);
        //创建数据
        info.setExamineName(String.format("%s年%s月预算考核表", req.getExamineYear(), req.getExamineMonth()));
        info.setApprovalStatus(ProcessState.UN_SUBMIT.name());
        info.setSubmitUserId(AccountUtil.getLoginInfo().getId());
        info.setSubmitTime(LocalDate.now());
        if (ObjectUtil.isNotEmpty(oldDetail)) {
            info.setId(oldDetail.getId());
        }
        SpringContextHolder.getBean(BudgetExamineService.class).saveOrUpdate(info);
        //生成明细
        //5.5.2.1 效益考核表
        BudgetExamineBenefitAddREQ benefitAddREQ = new BudgetExamineBenefitAddREQ();
        benefitAddREQ.setBudgetExamineId(info.getId());
        benefitAddREQ.setBudgetExamineYear(info.getExamineYear());
        benefitAddREQ.setBudgetExamineMonth(info.getExamineMonth());
        budgetExamineBenefitService.add(benefitAddREQ);
        //5.5.2.2 预算执行情况表
        BudgetExamineBudgetExecuteAddREQ budgetExecuteAddREQ = new BudgetExamineBudgetExecuteAddREQ();
        budgetExecuteAddREQ.setBudgetExamineId(info.getId());
        budgetExecuteAddREQ.setBudgetExamineYear(info.getExamineYear());
        budgetExecuteAddREQ.setBudgetExamineMonth(info.getExamineMonth());
        budgetExamineBudgetExecuteService.add(budgetExecuteAddREQ);
        //5.5.2.3 投放计划执行情况表
        BudgetExaminePayPlanExecuteAddREQ payPlanExecuteAddREQ = new BudgetExaminePayPlanExecuteAddREQ();
        payPlanExecuteAddREQ.setBudgetExamineId(info.getId());
        budgetExaminePayPlanExecuteService.add(payPlanExecuteAddREQ);
    }

    public BudgetExamineDetailRSP detail(BudgetExamineDetailREQ req) {
        BudgetExamine budgetExamine = this.getOne(Wrappers.<BudgetExamine>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getId()), BudgetExamine::getId, req.getId())
                .eq(ObjectUtil.isNotEmpty(req.getExamineYear()), BudgetExamine::getExamineYear, req.getExamineYear())
                .eq(ObjectUtil.isNotEmpty(req.getExamineMonth()), BudgetExamine::getExamineMonth, req.getExamineMonth())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(budgetExamine)) {
            return null;
        } else {
            return BeanUtil.copyProperties(budgetExamine, BudgetExamineDetailRSP.class);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(BudgetExamineModifyREQ req) {
        BudgetExamine originalInfo = budgetExamineMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BudgetExamine info = BeanUtil.copyProperties(req, BudgetExamine.class);
        budgetExamineMapper.updateById(info);
    }

    public Page<BudgetExamine> list(BudgetExamineListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<BudgetExamine>lambdaQuery()
        .eq(ObjectUtil.isNotEmpty(req.getExamineYear()), BudgetExamine::getExamineYear, req.getExamineYear())
        .eq(ObjectUtil.isNotEmpty(req.getExamineMonth()), BudgetExamine::getExamineMonth, req.getExamineMonth())
                .orderByDesc(BudgetExamine::getId));
    }

    public PageR<BudgetExamineListRSP> pageList(BudgetExamineListREQ req) {
        Page<BudgetExamine> data = this.list(req);
        List<BudgetExamineListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetExamineListRSP.class);
        if (ObjectUtil.isNotEmpty(list)) {
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(list.stream().map(BudgetExamineListRSP::getSubmitUserId).collect(Collectors.toList()));
            list.forEach(e -> e.setSubmitUserName(userId2Name.get(e.getSubmitUserId())));
        }
        return PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize());
    }

    public BudgetExamineListRSP detail(Long id) {
        BudgetExamine byId = this.getById(id);
        if (ObjectUtil.isEmpty(byId)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return BeanUtil.copyProperties(byId, BudgetExamineListRSP.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(BudgetExamineRemoveREQ req) {
        BudgetExamine originalInfo = budgetExamineMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        budgetExamineMapper.deleteById(req.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void submit(IdREQ req) {
        BudgetExamine originalInfo = budgetExamineMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!StrUtil.equalsAny(originalInfo.getApprovalStatus(), ProcessState.UN_SUBMIT.name(), ProcessState.CANCEL.name())) {
            throw new MithrasException("当前状态不允许提交");
        }
        // 发起预算考核表待办流程
        budgetExamineFlowService.createFlow(originalInfo);
        //变更数据
        LambdaUpdateWrapper<BudgetExamine> wrapper = new LambdaUpdateWrapper<BudgetExamine>();
        wrapper.set(BudgetExamine::getApprovalStatus, ProcessState.COMMIT.name());
        wrapper.eq(BudgetExamine::getId, originalInfo.getId());
        budgetExamineMapper.update(null, wrapper);
    }

}
