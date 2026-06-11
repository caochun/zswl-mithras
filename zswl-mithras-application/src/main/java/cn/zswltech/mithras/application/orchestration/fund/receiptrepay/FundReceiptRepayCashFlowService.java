package cn.zswltech.mithras.application.orchestration.fund.receiptrepay;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowModifyREQ;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundReceiptRepayCashFlowConverter;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.fund.mapper.lib.receiptrepay.FundReceiptRepayCashFlowLibMapper;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayCashFlowService
        extends ServiceImpl<FundReceiptRepayCashFlowMapper, FundReceiptRepayCashFlow> {
    @Resource
    private FundReceiptRepayCashFlowConverter baseConverter;
    @Resource
    private FundReceiptRepayCashFlowLibMapper baseLibMapper;
    @Resource
    private FundReceiptRepayStateService fundReceiptRepayStateService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<FundReceiptRepayCashFlowModifyREQ> req) {
        if (ObjectUtil.isEmpty(req)) {
            throw new MithrasException("编辑数据不能为空");
        }
        List<FundReceiptRepayCashFlow> toBeModify = baseConverter.modifyReq2Entity(req);
        for (FundReceiptRepayCashFlow item : toBeModify) {
            item.setUpdateTime(LocalDateTime.now());
        }
        updateBatchById(toBeModify);
        FundReceiptRepayCashFlow one = baseMapper.selectById(req.get(0).getId());
        fundReceiptRepayStateService.modifyUpdateProcessState(one.getReceiptRepayId());
    }

    /**
     * 获取收付款的现金流, 本月应付款的记录必须出现在页面上并标红
     *
     * @param req
     * @return
     */
    public PageR<FundReceiptRepayCashFlowListRSP> list(FundReceiptRepayCashFlowListREQ req) {
        int pageNum = req.getPage();
        // 查询版本
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            LocalDate lastDayOfThatMonth = LocalDate.of(1700, 1, 1);
            LocalDate firstDayOfThatMonth = LocalDate.of(1700, 1, 1);
            if (pageNum == -1) {
                List<FundReceiptRepayCashFlowLib> libs = baseLibMapper.selectList(Wrappers.<FundReceiptRepayCashFlowLib>lambdaQuery()
                        .eq(FundReceiptRepayCashFlow::getReceiptRepayId, req.getReceiptRepayId())
                        .eq(FundReceiptRepayCashFlowLib::getVersion, req.getVersion())
                        .orderByAsc(FundReceiptRepayCashFlowLib::getRepayDate));
                if (ObjectUtil.isEmpty(libs)) {
                    return PageR.of(new Page<>(0, req.getPageSize()), Collections.emptyList());
                }
                int count = 0;
                for (FundReceiptRepayCashFlowLib cashFlowLib : libs) {
                    if (!CashFlowState.NO_WRITE_OFF.name().equals(cashFlowLib.getWriteOffState())) {
                        count++;
                    } else {
                        if (cashFlowLib.getRepayDate().isBefore(lastDayOfThatMonth)) {
                            pageNum = count / req.getPageSize();
                            break;
                        }
                    }
                }
            }
            // 查询并转换
            Page<FundReceiptRepayCashFlowLib> page = baseLibMapper.selectPage(
                    new Page<>(pageNum, req.getPageSize()),
                    Wrappers.<FundReceiptRepayCashFlowLib>lambdaQuery()
                            .eq(FundReceiptRepayCashFlowLib::getReceiptRepayId, req.getReceiptRepayId())
                            .eq(FundReceiptRepayCashFlowLib::getVersion, req.getVersion())
                            .orderByAsc(FundReceiptRepayCashFlowLib::getRepayDate));
            if (CollectionUtils.isEmpty(page.getRecords())) {
                return PageR.of(page, new ArrayList<>());
            }
            List<FundReceiptRepayCashFlowListRSP> listRSPS = new ArrayList<>();
            lastDayOfThatMonth = page.getRecords().get(0).getCreateTime().toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
            firstDayOfThatMonth = page.getRecords().get(0).getCreateTime().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());

            for (FundReceiptRepayCashFlowLib cashFlowLib : page.getRecords()) {
                FundReceiptRepayCashFlowListRSP rsp = baseConverter.lib2ListRsp(cashFlowLib);
                // 计算红色标记
                rsp.setIsRed(false);
                if (Util.dateLe(cashFlowLib.getRepayDate(), lastDayOfThatMonth) && !CashFlowState.WRITTEN_OFF.name().equals(cashFlowLib.getWriteOffState())) {
                    // 月底前 未核销完毕数据 更新为 红色
                    rsp.setIsRed(true);
                } else if (Objects.nonNull(cashFlowLib.getApprovalPassDate()) && Util.dateGe(cashFlowLib.getApprovalPassDate(), firstDayOfThatMonth) && Util.dateLe(cashFlowLib.getApprovalPassDate(), lastDayOfThatMonth)) {
                    // 核销审批通过时间在本月内
                    rsp.setIsRed(true);
                }
                listRSPS.add(rsp);
            }
            return PageR.of(page, listRSPS);
        }
        // 查询编辑区
        LocalDate lastDayOfThisMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfThisMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        if (req.getPage() == -1) {
            List<FundReceiptRepayCashFlow> listAll = baseMapper.selectList(
                    Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                            .select(FundReceiptRepayCashFlow::getRepayDate, FundReceiptRepayCashFlow::getWriteOffState)
                            .eq(FundReceiptRepayCashFlow::getReceiptRepayId, req.getReceiptRepayId())
                            .orderByAsc(FundReceiptRepayCashFlow::getRepayDate));
            int count = 0;
            for (FundReceiptRepayCashFlow cashFlow : listAll) {
                if (!CashFlowState.NO_WRITE_OFF.name().equals(cashFlow.getWriteOffState())) {
                    count++;
                } else {
                    if (cashFlow.getRepayDate().isBefore(lastDayOfThisMonth)) {
                        pageNum = count / req.getPageSize();
                        break;
                    }
                }
            }
        }
        // 查询并转换
        Page<FundReceiptRepayCashFlow> page = baseMapper.selectPage(new Page<>(pageNum, req.getPageSize()),
                Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .eq(FundReceiptRepayCashFlow::getReceiptRepayId, req.getReceiptRepayId())
                        .orderByAsc(FundReceiptRepayCashFlow::getRepayDate));
        List<FundReceiptRepayCashFlowListRSP> rspList = new ArrayList<>();
        // 查询实际核销记录
        List<FundReceiptFlowDetail> detailList = SpringUtil.getBean(FundReceiptFlowDetailService.class).listByReceiptRepayId(req.getReceiptRepayId());
        Map<String, List<FundReceiptFlowDetail>> map = detailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        for (FundReceiptRepayCashFlow cashFlow : page.getRecords()) {
            FundReceiptRepayCashFlowListRSP rsp = baseConverter.entity2ListRsp(cashFlow);
            // 计算红色标记
            rsp.setIsRed(false);
            if (Util.dateLe(cashFlow.getRepayDate(), lastDayOfThisMonth) && !CashFlowState.WRITTEN_OFF.name().equals(cashFlow.getWriteOffState())) {
                // 月底前 未核销完毕数据 更新为 红色
                rsp.setIsRed(true);
            } else if (Objects.nonNull(cashFlow.getApprovalPassDate()) && Util.dateGe(cashFlow.getApprovalPassDate(), firstDayOfThisMonth) && Util.dateLe(cashFlow.getApprovalPassDate(), lastDayOfThisMonth)) {
                // 核销审批通过时间在本月内
                rsp.setIsRed(true);
            }
            // 填充实付
            long actualPrincipal = 0L;
            long actualInterest = 0L;
            List<FundReceiptFlowDetail> list = map.get(cashFlow.getCashFlowCode());
            if (CollectionUtil.isNotEmpty(list)) {
                for (FundReceiptFlowDetail detail : list) {
                    actualPrincipal += Optional.ofNullable(detail.getPrincipalAmount()).orElse(0L);
                    actualInterest += Optional.ofNullable(detail.getInterestAmount()).orElse(0L);
                }
            }
            rsp.setActualPrincipalAmount(actualPrincipal);
            rsp.setActualInterestAmount(actualInterest);
            rspList.add(rsp);
        }
        return PageR.of(page, rspList);
    }

    /**
     * 获取收付款的现金流
     *
     * @param ids 收付款id
     * @return key:收付款id value:现金流
     */
    public Map<Long, List<FundReceiptRepayCashFlow>> listByReceiptRepayIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return baseMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .in(FundReceiptRepayCashFlow::getReceiptRepayId, ids))
                .stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getReceiptRepayId));
    }

    public Optional<List<FundReceiptRepayCashFlow>> listByReceiptRepayId(Long id, @Nullable String version) {
        if (ObjectUtil.isNotEmpty(version)) {
            List<FundReceiptRepayCashFlowLib> cashFlowLibs = baseLibMapper.selectList(Wrappers.<FundReceiptRepayCashFlowLib>lambdaQuery()
                    .eq(FundReceiptRepayCashFlowLib::getReceiptRepayId, id)
                    .eq(FundReceiptRepayCashFlowLib::getVersion, version)
                    .orderByAsc(FundReceiptRepayCashFlowLib::getRepayDate));
            return Optional.ofNullable(baseConverter.lib2Entity(cashFlowLibs));
        }
        return Optional.ofNullable(baseMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .eq(FundReceiptRepayCashFlow::getReceiptRepayId, id)));
    }

    //维护核销状态
    @Transactional(rollbackFor = Throwable.class)
    public void doWriteOffStateModify(Long receiptRepayId, String cashFlowCode) {
        LambdaQueryWrapper<FundReceiptRepayCashFlow> queryRepayCashFlow = Wrappers.lambdaQuery();
        queryRepayCashFlow.eq(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayId);
        queryRepayCashFlow.eq(FundReceiptRepayCashFlow::getCashFlowCode, cashFlowCode);
        FundReceiptRepayCashFlow fundReceiptRepayCashFlow = this.getOne(queryRepayCashFlow);
        if (ObjectUtil.isEmpty(fundReceiptRepayCashFlow)) {
            return;
        }
        long hasVerifyAmountRepayCashFlow = SpringContextHolder.getBean(FundReceiptFlowDetailService.class).sum(fundReceiptRepayCashFlow.getReceiptRepayId(), fundReceiptRepayCashFlow.getCashFlowCode());
        long remainingAmountRepayCashFlow = Optional.ofNullable(fundReceiptRepayCashFlow.getPrincipleAmount()).orElse(0L) + Optional.ofNullable(fundReceiptRepayCashFlow.getInterestAmount()).orElse(0L) - hasVerifyAmountRepayCashFlow;
        // TODO 还本付息临时方案，不设置金额校验
        if (hasVerifyAmountRepayCashFlow <= 0 ) {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
        } else if (remainingAmountRepayCashFlow < 0) {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.BEYOND_WRITTEN_OFF.name());
        }  else if (remainingAmountRepayCashFlow > 0) {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        this.updateById(fundReceiptRepayCashFlow);
    }


    public Map<String, FundReceiptRepayCashFlow> queryByFinancingId(Long financingId, FinancingTypeEnum financingType){
        if(Objects.nonNull(financingId)) {
            // 过滤类型
            LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper = Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId);
            if(financingType.equals(FinancingTypeEnum.DIRECT)){
                wrapper.eq(FundReceiptRepayBaseInfo::getFinancingType, financingType);
            }else{
                wrapper.isNull(FundReceiptRepayBaseInfo::getFinancingType);
            }
            List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(wrapper);
            if (CollectionUtil.isNotEmpty(repayBaseInfoList)) {
                List<FundReceiptRepayCashFlow> cashFlowList = this.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .in(FundReceiptRepayCashFlow::getReceiptRepayId, repayBaseInfoList.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList())));
                return cashFlowList.stream().collect(Collectors.toMap(FundReceiptRepayCashFlow::getCashFlowCode, Function.identity()));
            }
        }
        return Collections.emptyMap();
    }


    public Map<Long, List<FundReceiptRepayCashFlow>> queryByFinancingIds(Collection<Long> financingIdList, FinancingTypeEnum financingType){
        if(CollectionUtil.isNotEmpty(financingIdList)) {
            // 过滤类型
            LambdaQueryWrapper<FundReceiptRepayBaseInfo> wrapper = Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .in(FundReceiptRepayBaseInfo::getFinancingId, financingIdList);
            if(financingType.equals(FinancingTypeEnum.DIRECT)){
                wrapper.eq(FundReceiptRepayBaseInfo::getFinancingType, financingType);
            }else{
                wrapper.isNull(FundReceiptRepayBaseInfo::getFinancingType);
            }
            List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(wrapper);
            if (CollectionUtil.isNotEmpty(repayBaseInfoList)) {
                List<FundReceiptRepayCashFlow> cashFlowList = this.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .in(FundReceiptRepayCashFlow::getReceiptRepayId, repayBaseInfoList.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList())));
                return cashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId));
            }
        }
        return Collections.emptyMap();
    }

}