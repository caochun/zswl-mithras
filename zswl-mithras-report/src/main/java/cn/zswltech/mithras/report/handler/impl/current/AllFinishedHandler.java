package cn.zswltech.mithras.report.handler.impl.current;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.base.model.CrAccountBase;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.draft.CrClientDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrClientDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/11/26 10:02
 * @description
 */
@Slf4j
@Component
public class AllFinishedHandler extends CrAbstractHandler<CrAccountDraft, CrAccount> {
    @Autowired
    private List<CrAbstractHandler> handlers;
    @Autowired
    private CrClientDraftMapper crClientDraftMapper;

    @Override
    public Integer sort() {
        return 9999;
    }

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.ALL_FINISHED;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        //do nothing
    }

    @Override
    public void afterModuleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        // 增加一个需求就是：已经报送过结清的账户不再抽数据到待报送
        // 查询这段时间内已经有结清日期的账户
        List<CrAccount> reportedAccountList = SpringUtil.getBean(CrAccountService.class).list(Wrappers.<CrAccount>lambdaQuery()
                .le(CrAccount::getUpdateTime, lastDealTime)
                .isNotNull(CrAccount::getClosedDate));
        if (CollUtil.isEmpty(reportedAccountList)) {
            return;
        }
        Map<String, CrAccount> accountMap = reportedAccountList.stream().collect(Collectors.toMap(CrAccountBase::getPaymentApplyCode, Function.identity(), (o1, o2) -> o1));

        // 存在已经结清的账户，将带报送里面数据置为已处理，不再展示
        List<CrAccountDraft> accountDrafts = SpringUtil.getBean(CrAccountDraftService.class).list(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountBase::getPaymentApplyCode, accountMap.keySet())
                .in(CrBaseModel::getContractId, reportedAccountList.stream().map(CrAccount::getContractId).collect(Collectors.toList())));
        // 这里需要将draft里面的数据重置为effect里面的数据
        List<CrAccountDraft> updateList = new LinkedList<>();
        for (CrAccountDraft draft : accountDrafts) {
            CrAccount account = accountMap.get(draft.getPaymentApplyCode());
            if (account == null) {
                log.error("已报送账户不存在：【{}】，需要开发检查数据！！！", draft.getPaymentApplyCode());
                continue;
            }
            // 为什么根据paymentApplyCode来判断，因为paymentApplyCode是唯一的，但是出现的问题是paymentApplyCode的生成规则有问题
            draft.setClosedDate(account.getClosedDate());
            draft.setPaymentAmount(account.getPaymentAmount());
            draft.setReportState(ReportState.REPORTED.name());
            draft.setBusinessKey(account.getBusinessKey());
            updateList.add(draft);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            log.info("本次过滤已经结清但是又出现在待报送的数据：【{}】", updateList);
            SpringUtil.getBean(CrAccountDraftService.class).updateBatchById(updateList);
            List<String> paymentApplyCodeCollection = updateList.stream().map(CrAccountBase::getPaymentApplyCode).collect(Collectors.toList());
            // 处理其他表
            for (CrAbstractHandler<?, ?> crHandler : handlers) {
                // 账户表不处理，当前类不处理
                if (crHandler.reportModule() == ReportModuleEnum.ACCOUNT
                        || crHandler.reportModule() == ReportModuleEnum.ALL_FINISHED
                        || crHandler.reportModule() == ReportModuleEnum.ACCOUNT_NEW
                        || crHandler.reportModule() == ReportModuleEnum.REPAY_PLAN_ALL) {
                    log.info("表{}不处理，当前类不处理", crHandler.reportModule().name());
                    continue;
                }
                // 客户表的处理逻辑比较特殊，需要判断当前用户是否存在在租合同，如果有则继续报送，否则隐藏
                if (crHandler.reportModule() == ReportModuleEnum.CLIENT) {
                    // 拿到当前待报送的所有客户
                    List<CrClientDraft> clientDrafts = crClientDraftMapper.selectList(Wrappers.<CrClientDraft>lambdaQuery()
                            .eq(CrClientDraft::getReportState, ReportState.TO_BE_REPORT.name()));
                    if (CollUtil.isNotEmpty(clientDrafts)) {
                        crHandler.handlerSettleRecord(clientDrafts.stream().map(CrClientDraft::getBusinessKey).collect(Collectors.toList()));
                    }
                    continue;
                }
                log.info("处理其他表：【{}】", crHandler.reportModule());
                crHandler.handlerSettleRecord(paymentApplyCodeCollection);
            }
        }
    }
}
