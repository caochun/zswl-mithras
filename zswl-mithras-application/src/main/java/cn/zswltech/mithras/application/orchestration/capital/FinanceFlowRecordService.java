package cn.zswltech.mithras.application.orchestration.capital;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.enums.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.capital.rule.CapitalBankFlowNoHandleRuleService;
import cn.zswltech.mithras.capital.rule.CapitalBankFlowSaveRuleService;
import cn.zswltech.mithras.capital.rule.CapitalBankFlowSyncRuleService;
import cn.zswltech.mithras.capital.rule.CapitalBankFlowWriteOffRuleService;
import cn.zswltech.mithras.capital.service.FinanceFlowWriteOffDetailService;
import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowSaveDecision;
import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowSnapshot;
import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowSyncDiff;
import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowSyncSnapshot;
import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowWriteOffState;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowTempRecord;
import cn.zswltech.mithras.third.financialshare.persistence.mapper.FinanceFlowRecordMapper;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandleFactory;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.basedata.service.BaseDataBankAccountService;
import cn.zswltech.mithras.third.baorong.application.BrFlowRecordService;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowRecordTempService;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2FlowQueryReq;
import cn.zswltech.mithras.third.financialshare.client.resp.CQ2FlowQueryRsp;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author vico
 * @description 财资平台流水记录
 * @date 2024-05-15
 */
@Slf4j
@Service
public class FinanceFlowRecordService extends ServiceImpl<FinanceFlowRecordMapper, FinanceFlowRecord> {

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Resource
    private FinanceFlowRecordTempService financeFlowRecordTempService;
    @Resource
    private FinanceFlowWriteOffDetailService financeFlowWriteOffDetailService;
    @Resource
    private CapitalBankFlowNoHandleRuleService capitalBankFlowNoHandleRuleService;
    @Resource
    private CapitalBankFlowSaveRuleService capitalBankFlowSaveRuleService;
    @Resource
    private CapitalBankFlowSyncRuleService capitalBankFlowSyncRuleService;
    @Resource
    private CapitalBankFlowWriteOffRuleService capitalBankFlowWriteOffRuleService;
    @Value("${mithras.remote.authOrg}")
    private String orgCode;

    private final static LocalDateTime BEGAN_DATE = LocalDateTime.of(2024, 7, 1, 0, 0, 0);


    //这里保存流水需要去重
    @Transactional(rollbackFor = Throwable.class)
    public void saveFlowRecord(List<FinanceFlowRecord> records) {
        if (ObjectUtil.isEmpty(records)) {
            return;
        }
        Set<Long> ids = records.stream().map(FinanceFlowRecord::getId).collect(Collectors.toSet());
        List<Long> hasIds = Collections.emptyList();
        if (CollectionUtil.isNotEmpty(ids)) {
            List<FinanceFlowRecord> financeFlowRecords = baseMapper.selectBatchIds(ids);
            if (CollectionUtil.isNotEmpty(financeFlowRecords)) {
                hasIds = financeFlowRecords.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList());
            }
        }
        CapitalBankFlowSaveDecision saveDecision = capitalBankFlowSaveRuleService.resolveNewFlowIds(ids, hasIds);
        //构建流水信息 这里暂时不做更新，流水更新后续业务也会受影响，故先不支持
        List<FinanceFlowRecord> addList = new ArrayList<FinanceFlowRecord>();
        records.forEach(record -> {
            if (saveDecision.getNewFlowIds().contains(record.getId())) {
                FinanceFlowRecord flowRecord = BeanUtil.copyProperties(record, FinanceFlowRecord.class);
                flowRecord.setShowInList(saveDecision.getDefaultShowInList());
                addList.add(flowRecord);
            }
        });
        if (!addList.isEmpty()) {
            getBean(FinanceFlowRecordService.class).saveBatch(addList);
            //自动核销流水
            try {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        getBean(FinanceFlowRecordService.class).move2NoHandle(addList);
                    }
                });
            } catch (Exception e) {
                // 事务回滚
                log.error("save FlowRecord after autoWriteOff error", e);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void move2NoHandle(List<FinanceFlowRecord> addList) {
        if (CollUtil.isEmpty(addList)) {
            return;
        }

        List<CapitalBankFlowSnapshot> flowSnapshots = addList.stream()
                .map(record -> new CapitalBankFlowSnapshot(record.getId(), record.getOppunit(),
                        record.getDescription(), record.getOppbanknumber()))
                .collect(Collectors.toList());
        List<String> userNames = getBean(UserDOMapper.class).selectAll().stream().map(UserDO::getUserName).collect(Collectors.toList());
        List<BaseDataBankAccount> list = getBean(BaseDataBankAccountService.class).list();
        List<String> accountNumbers = CollUtil.isEmpty(list) ? Collections.emptyList() : list.stream()
                .map(BaseDataBankAccount::getAccountNumber)
                .collect(Collectors.toList());
        List<Long> financeFlowIds = capitalBankFlowNoHandleRuleService.resolveNoHandleFlowIds(
                flowSnapshots, userNames, accountNumbers);
        if (CollUtil.isNotEmpty(financeFlowIds)) {
            getBean(FinanceFlowRecordService.class).lambdaUpdate()
                    .in(FinanceFlowRecord::getId, financeFlowIds)
                    .set(FinanceFlowRecord::getFinancingFlowType, BankFlowCenterTypeEnum.NO_PROCESSING_REQUIRE.name())
                    .update();

            log.info("本次拉取流水转入【无需处理】的流水ID列表：[{}]", financeFlowIds);
        }
    }

    //全量同步
    @Transactional(rollbackFor = Throwable.class)
    public List<String> fullSync(LocalDateTime beganTime, LocalDateTime endTime) {
        PlatformApiHandler<CQ2FlowQueryReq, CQ2FlowQueryRsp> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_FLOW_QUERY);
        CQ2FlowQueryReq req = new CQ2FlowQueryReq();
        if (ObjectUtil.isNotEmpty(beganTime)) {
            req.setBegintime(beganTime);
        } else {
            req.setBegintime(BEGAN_DATE);
        }
        if (ObjectUtil.isNotEmpty(endTime)) {
            req.setEndtime(endTime);
        } else {
            req.setEndtime(LocalDateTime.now());
        }
        //todo 待补充 机构
        req.setCompanynumber(orgCode);
        CQ2FlowQueryRsp rsp = platformApiHandler.execute(req);
        if (ObjectUtil.isEmpty(rsp)) {
            return Collections.emptyList();
        }
        List<FinanceFlowTempRecord> tempList = financeFlowRecordTempService.list(Wrappers.<FinanceFlowTempRecord>lambdaQuery()
                .eq(FinanceFlowTempRecord::getBatchId, rsp.getBatchId()));
        if (ObjectUtil.isEmpty(tempList)) {
            return Collections.emptyList();
        }
        //查询批次数据 这里要比对 判断出新增和删除
        List<FinanceFlowRecord> financeFlowRecords = this.baseMapper.selectList(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .between(FinanceFlowRecord::getBiztime, req.getBegintime(), req.getEndtime())
                .eq(FinanceFlowRecord::getLogicDeleteFlag, YesOrNoNumberEnum.NO.getCode()));
        List<FinanceFlowRecord> tempRecordList = BeanUtil.copyToList(tempList, FinanceFlowRecord.class);
        if (ObjectUtil.isEmpty(financeFlowRecords)) {
            //未保存过，全部保存
            SpringContextHolder.getBean(FinanceFlowRecordService.class).saveFlowRecord(tempRecordList);
            return Collections.emptyList();
        }
        CapitalBankFlowSyncDiff syncDiff = capitalBankFlowSyncRuleService.diffByBillNo(
                toSyncSnapshots(financeFlowRecords), toSyncSnapshots(tempRecordList));
        Set<String> addBillNos = syncDiff.getAddedFlows().stream()
                .map(CapitalBankFlowSyncSnapshot::getBillNo)
                .collect(Collectors.toSet());
        List<FinanceFlowRecord> addList = tempRecordList.stream()
                .filter(record -> addBillNos.contains(record.getBillno()))
                .collect(Collectors.toList());
        List<Long> deleteIds = syncDiff.getDeletedFlowIds();

        List<String> deleteBillNo = new ArrayList<>();
        if (!addList.isEmpty()) {
            SpringContextHolder.getBean(FinanceFlowRecordService.class).saveFlowRecord(addList);
        }
        if (!deleteIds.isEmpty()) {
            SpringContextHolder.getBean(FinanceFlowRecordService.class).update(Wrappers.<FinanceFlowRecord>lambdaUpdate()
                    .in(FinanceFlowRecord::getId, deleteIds)
                    .set(FinanceFlowRecord::getLogicDeleteFlag, YesOrNoNumberEnum.YES.getCode()));
            List<FinanceFlowRecord> financeFlowRecordList = getBean(FinanceFlowRecordService.class).listByIds(deleteIds);
            if (CollUtil.isNotEmpty(financeFlowRecordList)) {
                deleteBillNo = financeFlowRecordList.stream().map(FinanceFlowRecord::getBillno)
                        .distinct().collect(Collectors.toList());
            }
        }
        //同步宝融
        SpringContextHolder.getBean(BrFlowRecordService.class).doSyncCqFlow();
        return deleteBillNo;
    }

    private List<CapitalBankFlowSyncSnapshot> toSyncSnapshots(List<FinanceFlowRecord> records) {
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream()
                .map(record -> new CapitalBankFlowSyncSnapshot(record.getId(), record.getBillno()))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifySendFlag(List<String> billno, YesOrNoNumberEnum code) {
        if (ObjectUtil.isEmpty(code)) {
            code = YesOrNoNumberEnum.YES;
        }
        this.update(Wrappers.<FinanceFlowRecord>lambdaUpdate()
                .in(FinanceFlowRecord::getBillno, billno)
                .set(FinanceFlowRecord::getSendCqFlag, code.getCode()));
    }

    /**
     * 流水表mainID，金额
     **/
    //反核销付款数据
    @Transactional(rollbackFor = Throwable.class)
    public void withdrawBankFlow(Long mainId, String recordMainTable, Long amount) {
        Long financeFlowId = financeFlowWriteOffDetailService.logicalDeleteFirstByMainTableAndMainId(recordMainTable, mainId);
        if (ObjectUtil.isEmpty(financeFlowId)) {
            return;
        }

        FinanceFlowRecord financeFlowRecord = baseMapper.selectById(financeFlowId);
        financeFlowRecord.setSendCqFlag(YesOrNoNumberEnum.NO.getCode());
        financeFlowRecord.setSurplusAmount(LongUtil.null2zero(financeFlowRecord.getSurplusAmount()) + LongUtil.null2zero(amount));
        CapitalBankFlowWriteOffState writeOffState = capitalBankFlowWriteOffRuleService.resolveStateAfterWithdraw(
                financeFlowRecord.getSurplusAmount(), financeFlowRecord.getDebitamount(), financeFlowRecord.getCreditamount());
        financeFlowRecord.setWriteOffStatus(writeOffState.getWriteOffStatus());
        if (writeOffState.getFinancingFlowType() != null) {
            financeFlowRecord.setFinancingFlowType(writeOffState.getFinancingFlowType());
        }
        baseMapper.updateById(financeFlowRecord);
    }

}
