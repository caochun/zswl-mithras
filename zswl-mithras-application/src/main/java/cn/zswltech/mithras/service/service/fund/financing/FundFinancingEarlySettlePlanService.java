package cn.zswltech.mithras.service.service.fund.financing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanRSP;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanSaveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingChangeSubTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingEarlySettlePlanMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingEarlySettlePlan;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingEarlySettlePlanLib;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.fund.application.FundOrganizationService;
import cn.zswltech.mithras.fund.application.financing.fms.FundFinancingBaseInfoStateMachine;
import cn.zswltech.mithras.fund.application.financing.fms.FundFinancingContext;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingEvent;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingEarlySettlePlanLibService;
import cn.zswltech.mithras.fund.application.lib.financing.handler.impl.FundFinancingEarlySettlePlanLibHandler;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

@Slf4j
@Service
public class FundFinancingEarlySettlePlanService extends ServiceImpl<FundFinancingEarlySettlePlanMapper, FundFinancingEarlySettlePlan> {

    private static final String REPAY_CASH_FLOW_ITEM = "REPAY";

    @Resource
    private FundFinancingEarlySettlePlanLibService financingEarlySettlePlanLibService;
    @Resource
    private FundFinancingEarlySettlePlanLibHandler financingEarlySettlePlanLibHandler;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoStateMachine stateMachine;
    @Resource
    private FundOrganizationService organizationService;

    public FundFinancingEarlySettlePlanRSP getEarlySettlePlan(SingleFinancingIdREQ req) {
        FundFinancingEarlySettlePlan earlySettlePlan;
        if (StrUtil.isBlank(req.getVersion())) {
            earlySettlePlan = this.getOneByFinancingId(req.getFinancingId());
        } else {
            FundFinancingEarlySettlePlanLib earlySettlePlanLib = financingEarlySettlePlanLibService.getOneByFinancingIdVersion(req.getFinancingId(), req.getVersion());
            Assert.notNull(earlySettlePlanLib, () -> MithrasException.newException("对应版本的提前结清方案数据c不存在"));
            earlySettlePlan = financingEarlySettlePlanLibHandler.actualLib2Entity(earlySettlePlanLib);
        }
        return this.convertToRSP(earlySettlePlan, req.getFinancingId());
    }

    public FundFinancingEarlySettlePlanRSP convertToRSP(FundFinancingEarlySettlePlan fundFinancingEarlySettlePlan, Long financingId) {
        FundFinancingEarlySettlePlanRSP resp = BeanUtil.copyProperties(fundFinancingEarlySettlePlan, FundFinancingEarlySettlePlanRSP.class);
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(financingId);
        if (Objects.isNull(financingBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<FundOrganization> organizationList = organizationService.getByFinancingId(financingId);
        resp.setFinancingId(financingBaseInfo.getId());
        resp.setOrganizationId(organizationList.stream().map(FundOrganization::getId).collect(Collectors.toList()));
        resp.setOrganizationName(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
        resp.setFinancingAmount(financingBaseInfo.getFinancingAmount());
        FundReceiptRepayCashFlow repayCashFlow = getBean(FundReceiptRepayCashFlowMapper.class).selectOne(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .eq(FundReceiptRepayCashFlow::getFinancingId, financingBaseInfo.getId())
                .orderByDesc(FundReceiptRepayCashFlow::getPhase)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.nonNull(repayCashFlow)) {
            String format = String.format("%s~%s", financingBaseInfo.getActualLoanDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    repayCashFlow.getRepayDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            resp.setFinancingTerm(format);
        }
        if (ObjectUtil.isNotNull(fundFinancingEarlySettlePlan) && ObjectUtil.isNull(fundFinancingEarlySettlePlan.getLastPrincipal()) ||
                ObjectUtil.isNull(fundFinancingEarlySettlePlan)) {
            FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = getBean(FundReceiptRepayBaseInfoMapper.class).selectOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .isNull(FundReceiptRepayBaseInfo::getFinancingType)
                    .eq(FundReceiptRepayBaseInfo::getFinancingId, financingBaseInfo.getId())
                    .last(StringUtil.mysqlLimitOne()));
            List<FundReceiptFlowDetail> fundReceiptFlowDetails = getBean(FundReceiptFlowDetailMapper.class).selectList(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                    .eq(FundReceiptFlowDetail::getReceiptRepayId, fundReceiptRepayBaseInfo.getId())
                    .eq(FundReceiptFlowDetail::getCashFlowItem, REPAY_CASH_FLOW_ITEM));
            if (CollUtil.isNotEmpty(fundReceiptFlowDetails)) {
                long allPrincipal = fundReceiptFlowDetails.stream().mapToLong(FundReceiptFlowDetail::getPrincipalAmount).summaryStatistics().getSum();
                resp.setLastPrincipal(LongUtil.null2zero(financingBaseInfo.getFinancingAmount()) - allPrincipal);
            } else {
                resp.setLastPrincipal(LongUtil.null2zero(financingBaseInfo.getFinancingAmount()));
            }
        } else {
            //剩余本金
            resp.setLastPrincipal(fundFinancingEarlySettlePlan.getLastPrincipal());
        }
        return resp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveEarlySettlePlan(FundFinancingEarlySettlePlanSaveREQ req) {
        FundFinancingBaseInfo baseInfo = financingBaseInfoService.getById(req.getFinancingId());
        Assert.notNull(baseInfo, () -> MithrasException.newException("融资数据不存在"));
        FundFinancingEarlySettlePlan earlySettlePlan = BeanUtil.copyProperties(req, FundFinancingEarlySettlePlan.class);
        if (Objects.isNull(earlySettlePlan.getId())) {
            this.save(earlySettlePlan);
        } else {
            this.getBaseMapper().updateAnnotationIncludeNullById(earlySettlePlan);
        }
        // 变更状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.MODIFY_SAVE, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
    }

    public FundFinancingEarlySettlePlan getOneByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundFinancingEarlySettlePlan> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingEarlySettlePlan::getFinancingId, financingId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}
