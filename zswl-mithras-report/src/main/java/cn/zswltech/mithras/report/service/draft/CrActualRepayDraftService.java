package cn.zswltech.mithras.report.service.draft;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.ReportDataRepository;
import cn.zswltech.mithras.report.mapper.draft.CrActualRepayDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 征信报送-实际还款表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Slf4j
@Service
public class CrActualRepayDraftService extends ServiceImpl<CrActualRepayDraftMapper, CrActualRepayDraft> implements IService<CrActualRepayDraft> {
    @Resource
    private CrRepayPlanDraftService repayPlanDraftService;
    @Resource
    protected ReportDataRepository reportDataRepository;

    public void handlerRepay(CollectionRecordInfo operateRecord, CollectionBaseInfo baseInfo,
                             PaymentBaseInfo paymentBaseInfo, List<CrRepayPlanDraft> crRepayPlanDrafts, LocalDateTime dealTime) {

        //按照业务是不可能为空的，但是还是要保证健壮性
        if (CollUtil.isEmpty(crRepayPlanDrafts)) {
            return;
        }

        //需要更新计划表的展示标志列表
        List<CrRepayPlanDraft> changePlanList = new LinkedList<>();
        List<CrActualRepayDraft> needInsertList = new ArrayList<>();
        Long collectionAmount = operateRecord.getCollectionAmount();
        Long principal = operateRecord.getPrincipal();

        for (int i = 0; i < crRepayPlanDrafts.size(); i++) {
            CrRepayPlanDraft planDraft = crRepayPlanDrafts.get(i);
            //调整还款表内数据推送逻辑，对于已到还款日但是未进行还款的客户，每日推送该期次应收数据至待报送-还款表内，
            //当该期租金客户已还款且已完成核销，或该期租金已在逾期表内存在，则停止更新
            //获取是否在逾期表中存在，是否逾期字段写入列表
//            boolean isOverdue = reportDataRepository.getPlanIsOverdue(planDraft);
//            if(baseInfo.getWriteOffStatus().equals(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())||isOverdue){
//                return;
//            }
//            //对其项下各期次的计划收款日期<=当前日期&核销状态=未核销/部分核销/核销完毕（且在待报送还款表内不存在）&该期次数据在逾期表内不存在的数据更新至待报送-还款表内
//            if(dealTime.isBefore(planDraft.getCashFlowDate().plusDays(1).atStartOfDay())){
//                return;
//            }
            List<CrActualRepayDraft> list = this.list(Wrappers.<CrActualRepayDraft>lambdaQuery()
                    .eq(CrActualRepayDraft::getPaymentApplyCode, planDraft.getPaymentApplyCode())
                    .eq(CrActualRepayDraft::getPhase, baseInfo.getPhase()));

            //没有钱了直接退出
            if (collectionAmount <= 0) {
                return;
            }

            //如果没有还过钱，就遍历按照比例还钱
            if (CollUtil.isEmpty(list)) {
                //不够还当前期项，但是可以还一部分且没还过
                if (collectionAmount <= planDraft.getRent()) {
                    CrActualRepayDraft crActualRepayDraft = buildActualRepay(operateRecord, baseInfo, paymentBaseInfo, planDraft, collectionAmount, principal);
                    needInsertList.add(crActualRepayDraft);
                    planDraft.setIsShow(YesOrNoNumberEnum.YES.getCode());
                    changePlanList.add(BeanUtil.copyProperties(planDraft, CrRepayPlanDraft.class));
                    break;
                }

                if (collectionAmount > planDraft.getRent()) {
                    CrActualRepayDraft crActualRepayDraft = buildActualRepay(operateRecord, baseInfo, paymentBaseInfo, planDraft, planDraft.getRent(), planDraft.getPrincipal());
                    collectionAmount -= crActualRepayDraft.getCollectionAmount();
                    principal -= crActualRepayDraft.getCollectionPrincipal();
                    planDraft.setIsShow(YesOrNoNumberEnum.YES.getCode());
                    changePlanList.add(BeanUtil.copyProperties(planDraft, CrRepayPlanDraft.class));
                    needInsertList.add(crActualRepayDraft);
                    continue;
                }
            }

            //计算当前账户当前期项已经还的所有金额

            long accountCollectionAmount = list.stream().mapToLong(CrActualRepayDraft::getCollectionAmount).sum();
            long accountPrincipal = list.stream().mapToLong(CrActualRepayDraft::getCollectionPrincipal).sum();

            //不够还当前账户，但是可以还一部分且没还过
            if (collectionAmount <= planDraft.getRent() && planDraft.getRent() > accountCollectionAmount) {
                CrActualRepayDraft crActualRepayDraft = buildActualRepay(operateRecord, baseInfo, paymentBaseInfo, planDraft, collectionAmount, principal);
                needInsertList.add(crActualRepayDraft);
                planDraft.setIsShow(YesOrNoNumberEnum.YES.getCode());
                changePlanList.add(BeanUtil.copyProperties(planDraft, CrRepayPlanDraft.class));
                break;
            }
            //够还但是之前全部还了
            if (collectionAmount >= planDraft.getRent() && accountCollectionAmount >= planDraft.getRent()) {
                if(accountCollectionAmount > planDraft.getRent()){
                    log.info("多次跑同一天的任务，导致还款重复而超过原本租金，合同ID为：{}, 期项：{}", planDraft.getContractId(), planDraft.getPhase());
                }
                continue;
            }
            //够还但是之前只还了一部分
            if (collectionAmount > planDraft.getRent() - accountCollectionAmount && accountCollectionAmount < planDraft.getRent()) {
                CrActualRepayDraft crActualRepayDraft = buildActualRepay(operateRecord, baseInfo, paymentBaseInfo, planDraft,
                        planDraft.getRent() - accountCollectionAmount, planDraft.getPrincipal() - accountPrincipal);
                collectionAmount -= crActualRepayDraft.getCollectionAmount();
                principal -= crActualRepayDraft.getCollectionPrincipal();
                planDraft.setIsShow(YesOrNoNumberEnum.YES.getCode());
                changePlanList.add(BeanUtil.copyProperties(planDraft, CrRepayPlanDraft.class));
                needInsertList.add(crActualRepayDraft);
                continue;
            }
            //不够还，但是之前还了一部分
            if (collectionAmount <= planDraft.getRent() - accountCollectionAmount && accountCollectionAmount < planDraft.getRent()) {
                CrActualRepayDraft crActualRepayDraft = buildActualRepay(operateRecord, baseInfo, paymentBaseInfo, planDraft, collectionAmount, principal);
                planDraft.setIsShow(YesOrNoNumberEnum.YES.getCode());
                changePlanList.add(BeanUtil.copyProperties(planDraft, CrRepayPlanDraft.class));
                needInsertList.add(crActualRepayDraft);
                break;
            }
        }
        if (CollUtil.isNotEmpty(needInsertList)) {
            SpringContextHolder.getBean(CrActualRepayDraftService.class).saveBatch(needInsertList);
            //需要将数据从审批流中剔除
            for (CrActualRepayDraft draft : needInsertList) {
                // 更新还款计划表时 需要把实际还款表待报送的数据、该实际还款表对应的实际付款表 一起从审批流里剔除掉
                // 还款计划表
                LambdaUpdateWrapper<CrRepayPlanDraft> repayPlanUpdateWrapper = new LambdaUpdateWrapper<>();
                repayPlanUpdateWrapper.eq(CrRepayPlanDraft::getPaymentId, draft.getPaymentId());
                repayPlanUpdateWrapper.eq(CrRepayPlanDraft::getPhase, draft.getPhase());
                repayPlanUpdateWrapper.eq(CrRepayPlanDraft::getReportState, ReportState.TO_BE_REPORT.name());
                repayPlanUpdateWrapper.set(CrRepayPlanDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name());
                repayPlanUpdateWrapper.set(CrRepayPlanDraft::getProcBusinessKey, null);
                repayPlanUpdateWrapper.set(CrRepayPlanDraft::getIsShow, YesOrNoNumberEnum.YES.getCode());
                repayPlanDraftService.update(null, repayPlanUpdateWrapper);
                // 实际还款表
                LambdaUpdateWrapper<CrActualRepayDraft> actualRepayUpdateWrapper = new LambdaUpdateWrapper<>();
                actualRepayUpdateWrapper.eq(CrActualRepayDraft::getPaymentId, draft.getPaymentId());
                actualRepayUpdateWrapper.eq(CrActualRepayDraft::getPhase, draft.getPhase());
                actualRepayUpdateWrapper.eq(CrActualRepayDraft::getReportState, ReportState.TO_BE_REPORT.name());
                actualRepayUpdateWrapper.set(CrActualRepayDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name());
                actualRepayUpdateWrapper.set(CrActualRepayDraft::getProcBusinessKey, null);
                SpringContextHolder.getBean(CrActualRepayDraftService.class).update(null, actualRepayUpdateWrapper);
            }
            needInsertList.clear();
        }
        if(CollUtil.isNotEmpty(changePlanList)){
            SpringContextHolder.getBean(CrRepayPlanDraftService.class).updateBatchById(changePlanList);
            changePlanList.clear();
        }
    }

    private CrActualRepayDraft buildActualRepay(CollectionRecordInfo operateRecord, CollectionBaseInfo baseInfo, PaymentBaseInfo paymentBaseInfo,
                                                CrRepayPlanDraft planDraft, Long collectionAmount, Long principal) {
        CrActualRepayDraft repayDraft = CrActualRepayDraft.builder().build();
        repayDraft.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setProcBusinessKey(null)
                .setPaymentApplyCode(planDraft.getPaymentApplyCode())
                .setPaymentId(paymentBaseInfo.getId())
                .setPhase(baseInfo.getPhase())
                .setPayDate(operateRecord.getCollectionDate())
                .setCollectionAmount(collectionAmount)
                .setCollectionPrincipal(principal);
        repayDraft.setBusinessKey(repayDraft.genBusinessKey(operateRecord.getId(), operateRecord.getCollectionDate()));
        repayDraft.setContractId(baseInfo.getContractId());
        return repayDraft;
    }

    /**
     * 还罚息处理
     */
    public void handlerInterestRepay(CollectionRecordInfo operateRecord, List<CrRepayPlanDraft> crRepayPlanDrafts) {

        //需要处理的归还罚息列表
        List<CrRepayPlanDraft> interestList = new LinkedList<>();

        //定义变量此次总的归还罚息
        long allInterest = operateRecord.getPenaltyInterest();
        for (int i = 0; i < crRepayPlanDrafts.size(); i++) {
            CrRepayPlanDraft planDraft = crRepayPlanDrafts.get(i);

            //没有钱了直接退出
            if (allInterest <= 0) {
                return;
            }

            long needPay = planDraft.getPlanPenaltyInterest() - planDraft.getAlreadyPenaltyInterest();
            //够还
            if (allInterest >= needPay) {
                planDraft.setAlreadyPenaltyInterest(Util.mithrasLongDecimalTwo(planDraft.getAlreadyPenaltyInterest() + needPay));
                interestList.add(planDraft);
                allInterest -= needPay;
                continue;
            }

            //不够还
            planDraft.setAlreadyPenaltyInterest(Util.mithrasLongDecimalTwo(planDraft.getAlreadyPenaltyInterest() + allInterest));
            allInterest = 0;
            interestList.add(planDraft);

        }

        if (CollUtil.isNotEmpty(interestList)) {
            repayPlanDraftService.saveOrUpdateBatch(interestList);
        }
    }
}
