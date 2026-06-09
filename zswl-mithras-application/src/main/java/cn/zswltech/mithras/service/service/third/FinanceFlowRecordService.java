package cn.zswltech.mithras.service.service.third;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.domain.enums.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.capital.domain.enums.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FinanceFlowWriteOffDetailMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FinanceFlowWriteOffDetail;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowTempRecord;
import cn.zswltech.mithras.third.mapper.FinanceFlowRecordMapper;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.basedata.service.BaseDataBankAccountService;
import cn.zswltech.mithras.third.baorong.application.BrFlowRecordService;
import cn.zswltech.mithras.third.service.FinanceFlowRecordTempService;
import cn.zswltech.mithras.service.service.capital.FinanceFlowAutoWriteOffService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQ2FlowQueryReq;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.CQ2FlowQueryRsp;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

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
    private FinanceFlowWriteOffDetailMapper financeFlowWriteOffDetailMapper;
    @Resource
    private TransactionDefinition transactionDefinition;
    @Resource
    private DataSourceTransactionManager transactionManager;
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
        Set<Long> hasIds = new HashSet<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            List<FinanceFlowRecord> financeFlowRecords = baseMapper.selectBatchIds(ids);
            if (CollectionUtil.isNotEmpty(financeFlowRecords)) {
                financeFlowRecords.forEach(record -> hasIds.add(record.getId()));
            }
        }
        //构建流水信息 这里暂时不做更新，流水更新后续业务也会受影响，故先不支持
        List<FinanceFlowRecord> addList = new ArrayList<FinanceFlowRecord>();
        records.forEach(record -> {
            if (!hasIds.contains(record.getId())) {
                FinanceFlowRecord flowRecord = BeanUtil.copyProperties(record, FinanceFlowRecord.class);
                flowRecord.setShowInList(YesOrNoNumberEnum.YES.getCode());
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

        List<Long> financeFlowIds = new ArrayList<>(16);
        //过滤条件1： （付款方向 and 对方户名=员工姓名） or 对方户名含“待报解” -》去除付款方向
        //找到当前所有用户
        List<String> userNames = getBean(UserDOMapper.class).selectAll().stream().map(UserDO::getUserName).collect(Collectors.toList());
        String[] userNamesArray = userNames.toArray(new String[userNames.size()]);
        List<Long> idsBySysName = addList.stream()
                .filter(obj -> CharSequenceUtil.containsAny(obj.getOppunit(), userNamesArray))
                .map(FinanceFlowRecord::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(idsBySysName)) {
            financeFlowIds.addAll(idsBySysName);
        }

        List<Long> idsByOppUnit = addList.stream().filter(a -> Objects.nonNull(a.getOppunit()))
                .filter(obj -> obj.getOppunit().contains("待报解"))
                .map(FinanceFlowRecord::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(idsByOppUnit)) {
            financeFlowIds.addAll(idsByOppUnit);
        }

        //过滤条件2： 摘要中含“报销” or “工资” or “奖金”
        List<Long> idsByDesc = addList.stream().filter(o -> CharSequenceUtil.isNotBlank(o.getDescription()))
                .filter(o -> CharSequenceUtil.containsAny(o.getDescription(), "报销", "工资", "奖金"))
                .map(FinanceFlowRecord::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(idsByDesc)) {
            financeFlowIds.addAll(idsByDesc);
        }

        //过滤条件3：对方账号=【财务管理-我方账户】中的账号
        List<BaseDataBankAccount> list = getBean(BaseDataBankAccountService.class).list();
        if (CollUtil.isNotEmpty(list)) {
            String[] accountNumArray = list.stream().map(BaseDataBankAccount::getAccountNumber)
                    .map(e -> e.replace(" ", "")).toArray(String[]::new);
            List<Long> idsByAccount = addList.stream().filter(o -> CharSequenceUtil.isNotBlank(o.getOppbanknumber()))
                    .filter(o -> CharSequenceUtil.equalsAny(o.getOppbanknumber().replace(" ", ""), accountNumArray))
                    .map(FinanceFlowRecord::getId)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(idsByAccount)) {
                financeFlowIds.addAll(idsByAccount);
            }
        }

        financeFlowIds = financeFlowIds.stream().distinct().collect(Collectors.toList());
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
        //比对
        List<FinanceFlowRecord> addList = new ArrayList<>();
        List<Long> deleteIds = new ArrayList<>();
        Set<String> oldIds = financeFlowRecords.stream().map(FinanceFlowRecord::getBillno).collect(Collectors.toSet());
        Set<String> tempIds = tempRecordList.stream().map(FinanceFlowRecord::getBillno).collect(Collectors.toSet());
        //添加的
        tempRecordList.forEach(temp -> {
            if (!oldIds.contains(temp.getBillno())) {
                addList.add(temp);
            }
        });

        List<String> deleteBillNo = new ArrayList<>();
        //删除的
        financeFlowRecords.forEach(record -> {
            if (!tempIds.contains(record.getBillno())) {
                deleteIds.add(record.getId());
            }
        });
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
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = financeFlowWriteOffDetailMapper.selectOne(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, recordMainTable)
                .eq(FinanceFlowWriteOffDetail::getMainId, mainId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(financeFlowWriteOffDetail)) {
            return;
        }

        LambdaUpdateWrapper<FinanceFlowWriteOffDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FinanceFlowWriteOffDetail::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(FinanceFlowWriteOffDetail::getId, financeFlowWriteOffDetail.getId());
        financeFlowWriteOffDetailMapper.update(null, updateWrapper);
        FinanceFlowRecord financeFlowRecord = baseMapper.selectById(financeFlowWriteOffDetail.getFinanceFlowId());
        financeFlowRecord.setSendCqFlag(YesOrNoNumberEnum.NO.getCode());
        financeFlowRecord.setSurplusAmount(LongUtil.null2zero(financeFlowRecord.getSurplusAmount()) + LongUtil.null2zero(amount));
        //记录金额
        double debitamount = financeFlowRecord.getDebitamount() == null ? 0f : financeFlowRecord.getDebitamount();
        double creditamount = financeFlowRecord.getCreditamount() == null ? 0f : financeFlowRecord.getCreditamount();
        if (financeFlowRecord.getSurplusAmount() <= 0) {
            financeFlowRecord.setWriteOffStatus(FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name());
        } else if (financeFlowRecord.getSurplusAmount() >= (debitamount + creditamount) * 10000) {
            financeFlowRecord.setWriteOffStatus(FinancingFlowWriteOffStatusEnum.NO_WRITE_OFF.name());
            financeFlowRecord.setFinancingFlowType(BankFlowCenterTypeEnum.PROCESSING_CENTER.name());
        } else {
            financeFlowRecord.setWriteOffStatus(FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name());
            financeFlowRecord.setFinancingFlowType(BankFlowCenterTypeEnum.PROCESSING_CENTER.name());
        }
        baseMapper.updateById(financeFlowRecord);
    }

}
