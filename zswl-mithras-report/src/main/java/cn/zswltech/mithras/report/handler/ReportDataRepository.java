package cn.zswltech.mithras.report.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.biz.TableTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.mapper.AccountReportRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.draft.CrOverdueRecordDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrRepayPlanDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.CrOverdueRecordMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrOverdueRecord;
import cn.zswltech.mithras.report.mapper.model.AccountReportRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.util.StreamUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * 数据提取
 *
 * @author wangchuanhao
 * @date 2022/10/14 2:28 PM
 */
@Component
public class ReportDataRepository {

    @Resource
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private AccountReportRecordMapper accountReportRecordMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CrOverdueRecordDraftMapper crOverdueRecordDraftMapper;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;
    @Resource
    private CrOverdueRecordMapper crOverdueRecordMapper;
    @Resource
    private CrRepayPlanDraftMapper crRepayPlanDraftMapper;

    /**
     * 判断合同是否需要上报（不考虑付款模块）仅从合同模块考虑（只报租赁、转租赁、保理，租赁转租赁主承租人需上报征信，保理第一债权人需上报征信）
     *
     * @param contractIdList
     * @return
     */
    public Map<Long, Boolean> contractReportMap(Collection<Long> contractIdList) {
        Map<Long, Boolean> map = new HashMap<>();
        contractIdList.forEach(c -> map.put(c, contractReport(c)));
        return map;
    }

    /**
     * （租赁、转租赁）合同主承租人是否上报征信
     * （保理）合同第一债权人是否上报征信
     *
     * @param contractBaseInfoLib
     * @return
     */
    public boolean mainTenantryReport(ContractBaseInfoLib contractBaseInfoLib) {
        if (Objects.isNull(contractBaseInfoLib)) {
            return false;
        }
        if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) || ProjectBizType.ZZ.name().equals(contractBaseInfoLib.getBizType())) {
            // 找到一个上报征信的主承租人
            ContractTenantryLib contractTenantryLib = contractTenantryLibMapper.selectOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                    .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
                    .eq(ContractTenantryLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                    .last(StringUtil.mysqlLimitOne())
            );
            return Objects.nonNull(contractTenantryLib);
        } else if (ProjectBizType.BL.name().equals(contractBaseInfoLib.getBizType())) {
            // 找到第一债权人
            ContractTenantryLib contractTenantryLib = contractTenantryLibMapper.selectOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                    .eq(ContractTenantryLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractTenantryLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractTenantryLib::getLesseeType, CreditorDebtorTypeEnum.CREDITOR.name())
                    .last(StringUtil.mysqlLimitOne())
            );
            return Objects.nonNull(contractTenantryLib) && Objects.equals(YesOrNoNumberEnum.YES.getCode(), contractTenantryLib.getIsReport());
        }
        return false;
    }

    /**
     * 判断合同是否需要上报（不考虑付款模块）仅从合同模块考虑（只报租赁、转租赁、保理，租赁转租赁主承租人需上报征信，保理第一债权人需上报征信）
     *
     * @param contractId
     * @return
     */
    public boolean contractReport(Long contractId) {
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                .eq(ContractBaseInfoLib::getOriginId, contractId)
                .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(ContractBaseInfoLib::getVersion)
                .last(StringUtil.mysqlLimitOne())
        );
        return contractReport(contractBaseInfoLib);
    }

    public boolean contractReport(ContractBaseInfoLib contractBaseInfoLib) {
        if (Objects.isNull(contractBaseInfoLib)) {
            return false;
        }
        if (ProjectBizType.ZR.name().equals(contractBaseInfoLib.getBizType())) {
            return false;
        }
        return mainTenantryReport(contractBaseInfoLib);
    }

    /**
     * 搜索这段时间范围内有变动的合同数据 并去重
     * 过滤掉业务类型为债权转让的数据
     * 没过滤承租人/债权人 上报征信逻辑
     *
     * @param dealTime
     * @param lastDealTime
     * @return
     */
    public Map<String, List<ContractBaseInfoLib>> listChangeContractBaseInfo(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                .gt(ContractBaseInfoLib::getCreateTime, lastDealTime)
                .le(ContractBaseInfoLib::getCreateTime, dealTime)
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.INVALID.name())
                .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                .ne(ContractBaseInfoLib::getBizType, ProjectBizType.ZR.name())
        );
        if (CollUtil.isEmpty(contractBaseInfoLibList)) {
            return Collections.emptyMap();
        }
        contractBaseInfoLibList = contractBaseInfoLibList.stream()
                .sorted(Comparator.comparing(ContractBaseInfoLib::getVersion).reversed())
                .filter(StreamUtil.distinctByKey(ContractBaseInfoLib::getOriginId))
                .collect(Collectors.toList());

        //把非直租的数据单独分出来
        Map<String, List<ContractBaseInfoLib>> listMap = new HashMap<>(2);
        List<ContractBaseInfoLib> notZhiZuList = new LinkedList<>();
        List<ContractBaseInfoLib> zhiZuList = new LinkedList<>();

        for (ContractBaseInfoLib baseInfoLib : contractBaseInfoLibList) {
            if (LeaseType.zhi_zu.name().equals(baseInfoLib.getLeaseType())) {
                zhiZuList.add(baseInfoLib);
            } else {
                notZhiZuList.add(baseInfoLib);
            }
        }
        listMap.put(DataTypeEnum.ZHI_ZU.name(), zhiZuList);
        listMap.put(DataTypeEnum.NOT_ZHI_ZU.name(), notZhiZuList);
        return listMap;
    }

    /**
     * 搜索这段时间范围内有变动的付款数据 并过滤合同主承租人逻辑
     *
     * @param dealTime
     * @param lastDealTime
     * @return
     */
    public Map<String, List<PaymentBaseInfo>> listNeedReportChangePaymentBaseInfo(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        // 收缩这段时间内存在投放的付款申请
        List<PaymentActualDetail> actualDetailList = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .between(PaymentActualDetail::getCreateTime, lastDealTime, dealTime));
        if (CollUtil.isEmpty(actualDetailList)) {
            return Collections.emptyMap();
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectBatchIds(actualDetailList.stream().map(PaymentActualDetail::getPaymentId).collect(Collectors.toList()));
        // 过滤一下没有实际租金表的数据
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyMap();
        }
        // 合同上报过滤逻辑
        Map<Long, Boolean> contractReportMap = this.contractReportMap(paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet()));
        paymentBaseInfoList = paymentBaseInfoList.stream().filter(p -> Boolean.TRUE.equals(contractReportMap.get(p.getContractId()))).collect(Collectors.toList());

        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyMap();
        }
        //按照直租和非直租分组
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet()));
        Map<String, List<PaymentBaseInfo>> paymentMap = new HashMap<>(2);
        Set<Long> zhiZuContractIds = contractBaseInfos.stream().filter(a -> LeaseType.zhi_zu.name().equals(a.getLeaseType())).map(ContractBaseInfo::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(zhiZuContractIds)) {
            paymentMap.put(DataTypeEnum.NOT_ZHI_ZU.name(), paymentBaseInfoList);
            return paymentMap;
        }
        List<PaymentBaseInfo> zhiZuPaymentBaseInfoList = paymentBaseInfoList.stream().filter(a -> zhiZuContractIds.contains(a.getContractId())).collect(Collectors.toList());
        List<PaymentBaseInfo> notZhiZuPaymentBaseInfoList = paymentBaseInfoList.stream().filter(a -> !zhiZuContractIds.contains(a.getContractId())).collect(Collectors.toList());
        paymentMap.put(DataTypeEnum.ZHI_ZU.name(), zhiZuPaymentBaseInfoList.stream().filter(this::receiptPayment).collect(Collectors.toList()));
        paymentMap.put(DataTypeEnum.NOT_ZHI_ZU.name(), notZhiZuPaymentBaseInfoList);
        return paymentMap;
    }

    /**
     * 搜索这段时间范围内有变动的付款数据 并过滤合同主承租人逻辑
     *
     * @param dealTime
     * @param lastDealTime
     * @return
     */
    public List<PaymentBaseInfo> listNeedReportChangePaymentBaseInfoNew(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        // 收缩这段时间内存在投放的付款申请
        List<PaymentActualDetail> actualDetailList = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .between(PaymentActualDetail::getCreateTime, lastDealTime, dealTime));
        if (CollUtil.isEmpty(actualDetailList)) {
            return Collections.emptyList();
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectBatchIds(actualDetailList.stream().map(PaymentActualDetail::getPaymentId).collect(Collectors.toList()));
        // 过滤一下没有实际租金表的数据
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }

        // 合同上报过滤逻辑
        Map<Long, Boolean> contractReportMap = this.contractReportMap(paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet()));
        paymentBaseInfoList = paymentBaseInfoList.stream().filter(p -> Boolean.TRUE.equals(contractReportMap.get(p.getContractId()))).collect(Collectors.toList());

        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }
        //按照直租和非直租分组
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet()));
        Set<Long> zhiZuContractIds = contractBaseInfos.stream().filter(a -> LeaseType.zhi_zu.name().equals(a.getLeaseType())).map(ContractBaseInfo::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(zhiZuContractIds)) {
            return paymentBaseInfoList;
        }
        return paymentBaseInfoList.stream().filter(a -> !zhiZuContractIds.contains(a.getContractId())).collect(Collectors.toList());
    }

    /**
     * 搜索这段时间范围内有变动的付款数据 并过滤合同主承租人逻辑
     * 子表 过滤 单借据多付款申请是否上报的逻辑
     *
     * @param dealTime
     * @param lastDealTime
     * @return
     */
    public Map<String, List<PaymentBaseInfo>> listNeedReportChangePaymentBaseInfoSubTable(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<PaymentBaseInfo>> stringListMap = listNeedReportChangePaymentBaseInfo(dealTime, lastDealTime);
        stringListMap.forEach((k, v) -> {
            Map<Long, Boolean> receiptMultiPaymentNeedReportSubTableMap = receiptMultiPaymentNeedReportSubTableMap(v.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()));
            v = v.stream().filter(p -> Boolean.TRUE.equals(receiptMultiPaymentNeedReportSubTableMap.get(p.getId()))).collect(Collectors.toList());
            stringListMap.put(k, v);
        });
        return stringListMap;
    }

    public List<PaymentBaseInfo> listNeedReportChangePaymentBaseInfoSubTableNew(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<PaymentBaseInfo> stringListMap = listNeedReportChangePaymentBaseInfoNew(dealTime, lastDealTime);
        Map<Long, Boolean> receiptMultiPaymentNeedReportSubTableMap = receiptMultiPaymentNeedReportSubTableMap(stringListMap.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()));
        stringListMap = stringListMap.stream().filter(p -> Boolean.TRUE.equals(receiptMultiPaymentNeedReportSubTableMap.get(p.getId()))).collect(Collectors.toList());
        return stringListMap;
    }

    /**
     * 列出一段时间内合同变动的数据 需要删除的合同id列表
     * 由于 合同主承租人征信报送不为是 导致的数据不报送 对历史数据进行清除
     *
     * @return
     */
    public List<Long> listDeleteCrDataContractId(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> listMap = listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> tempList = new ArrayList<>();
        listMap.values().forEach(tempList::addAll);
        List<Long> needDeleteContractIdList = new ArrayList<>();
        tempList.forEach(c -> {
            if (!mainTenantryReport(c)) {
                needDeleteContractIdList.add(c.getOriginId());
            }
        });
        return needDeleteContractIdList;
    }

    /**
     * 根据id查找客户
     *
     * @param clientIdList
     * @return
     */
    public Map<Long, Client> clientMap(Collection<Long> clientIdList) {
        if (CollectionUtils.isEmpty(clientIdList)) {
            return new HashMap<>();
        }
        return clientMapper.selectBatchIds(clientIdList).stream().collect(Collectors.toMap(Client::getId, Function.identity(), (k1, k2)->k1));
    }

    /**
     * 判断是否是有效的付款（存在实际租金表的）关联到借据的
     *
     * @param paymentBaseInfo
     * @return
     */
    public boolean receiptPayment(PaymentBaseInfo paymentBaseInfo) {
        return Objects.nonNull(paymentBaseInfo.getReceiptIdFinal());
    }

    /**
     * 子表是否报送
     * 只有子表对应的paymentId报送了，子表才报送
     *
     * @param paymentId
     * @return
     */
    public boolean receiptMultiPaymentNeedReportSubTable(Long paymentId) {
        return receiptMultiPaymentNeedReportSubTableMap(ListUtil.toList(paymentId)).get(paymentId);
    }

    public Map<Long, Boolean> receiptMultiPaymentNeedReportSubTableMap(Collection<Long> paymentIdList) {
        Map<Long, Boolean> resultMap = new HashMap<>(8);
        if (CollectionUtils.isEmpty(paymentIdList)) {
            return resultMap;
        }
        List<AccountReportRecord> accountReportRecordList = accountReportRecordMapper.selectList(Wrappers.<AccountReportRecord>lambdaQuery()
                .in(AccountReportRecord::getPaymentId, paymentIdList));
        for (AccountReportRecord accountReportRecord : accountReportRecordList) {
            resultMap.put(accountReportRecord.getPaymentId(), true);
        }
        for (Long paymentId : paymentIdList) {
            if (!resultMap.containsKey(paymentId)) {
                resultMap.put(paymentId, false);
            }
        }
        return resultMap;
    }

    /**
     * 传进来一个paymentList，过滤返回其中需要报送的
     * 1、生效、核销完毕
     * 2、不归属于特殊合同
     * 3、关联到借据
     * 4、单借据多付款情况下 找到报送过的那个付款 过滤掉其他付款
     *
     * @param paymentBaseInfoList
     * @return
     */
    public List<PaymentBaseInfo> filterNeedReportPaymentListSubTable(List<PaymentBaseInfo> paymentBaseInfoList) {
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return paymentBaseInfoList;
        }
        // 过滤 1、2、3
        paymentBaseInfoList = paymentBaseInfoList.stream()
                .filter(p -> PaymentStatusEnum.TAKE_EFFECT.name().equals(p.getPaymentStatus()) && CharSequenceUtil.equalsAny(p.getWriteOffStatus(), PaymentWriteOffStatus.PART_WRITTEN_OFF.name(), PaymentWriteOffStatus.WRITTEN_OFF.name()))
                .filter(this::receiptPayment)
                .collect(Collectors.toList());
        Map<Long, Boolean> receiptMultiPaymentNeedReportSubTableMap = receiptMultiPaymentNeedReportSubTableMap(paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()));
        // 过滤4
        paymentBaseInfoList = paymentBaseInfoList.stream().filter(p -> Boolean.TRUE.equals(receiptMultiPaymentNeedReportSubTableMap.get(p.getId()))).collect(Collectors.toList());
        return paymentBaseInfoList;
    }

    /**
     * 传进来一个paymentList，过滤返回其中需要报送的
     * 1、生效、核销完毕、部分核销、待核销
     * 2、不归属于特殊合同
     * 3、关联到借据
     * 4、单借据多付款情况下 找到报送过的那个付款 过滤掉其他付款
     *
     * @param paymentBaseInfoList
     * @return
     */
    public List<PaymentBaseInfo> filterRepayPlanNeedReportPaymentListSubTable(List<PaymentBaseInfo> paymentBaseInfoList) {
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return paymentBaseInfoList;
        }
        // 过滤 1、2、3
        paymentBaseInfoList = paymentBaseInfoList.stream()
                .filter(p -> PaymentStatusEnum.TAKE_EFFECT.name().equals(p.getPaymentStatus()) && CharSequenceUtil.equalsAny(p.getWriteOffStatus(), PaymentWriteOffStatus.PART_WRITTEN_OFF.name(), PaymentWriteOffStatus.WRITTEN_OFF.name(), PaymentWriteOffStatus.TO_BE_WRITE_OFF.name()))
                .filter(this::receiptPayment)
                .collect(Collectors.toList());
        Map<Long, Boolean> receiptMultiPaymentNeedReportSubTableMap = receiptMultiPaymentNeedReportSubTableMap(paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()));
        // 过滤4
        paymentBaseInfoList = paymentBaseInfoList.stream().filter(p -> Boolean.TRUE.equals(receiptMultiPaymentNeedReportSubTableMap.get(p.getId()))).collect(Collectors.toList());
        return paymentBaseInfoList;
    }

    /**
     * 传进来一个crRepayPlanDraft，判断是否更新至还款表
     * @param crRepayPlanDraft
     * @return
     */
    public boolean filterNeedReportList(CrRepayPlanDraft crRepayPlanDraft, LocalDateTime dealTime){
        //首次跑批全量插入
        boolean isFirst = crRepayPlanDraftMapper.selectCount(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                .eq(CrRepayPlanDraft::getPaymentApplyCode, crRepayPlanDraft.getPaymentApplyCode())) == 0;
        if (isFirst) {
            return true;
        }
        //收款日期<=当前日期
        if(dealTime.isBefore(crRepayPlanDraft.getCashFlowDate().plusDays(1).atStartOfDay())){
            return false;
        }
        //该期次数据在逾期表内是否存在
        if(getPlanIsOverdue(crRepayPlanDraft)){
            //该期次数据在逾期表内存在
            return false;
        }
        return true;
    }

    /**
     * 传进来一个crRepayPlanDraft，判断是否在逾期表存在
     * @param crRepayPlanDraft
     * @return
     */
    public boolean getPlanIsOverdue(CrRepayPlanDraft crRepayPlanDraft){
        //该期次数据在逾期表内是否存在
        List<CrOverdueRecordDraft> crOverdueRecordDrafts = crOverdueRecordDraftMapper.selectList(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .eq(CrOverdueRecordDraft::getContractId, crRepayPlanDraft.getContractId())
                .eq(CrOverdueRecordDraft::getPaymentApplyCode, crRepayPlanDraft.getPaymentApplyCode())
                .eq(CrOverdueRecordDraft::getPaymentId, crRepayPlanDraft.getPaymentId())
                .eq(CrOverdueRecordDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrOverdueRecordDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name()));
        for(CrOverdueRecordDraft crOverdueRecordDraft : crOverdueRecordDrafts){
            String phase = crOverdueRecordDraft.getPhase();
            if(StringUtils.isNotEmpty(phase)){
                String[] array = phase.replace(" ","").replace("[", "").replace("]", "").split(",");
                for (String str : array) {
                    int overduePhase = Integer.parseInt(str);
                    if(overduePhase == crRepayPlanDraft.getPhase()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 传进来一个crRepayPlanDraft，判断是否更新
     * @param crRepayPlanDraft
     * @return
     */
    public boolean getPlanIsUpate(CrRepayPlanDraft crRepayPlanDraft,Long receiptId,LocalDateTime dealTime,boolean isUpdate){
        //计划收款日期>当前日期
        if(dealTime.isBefore(crRepayPlanDraft.getCashFlowDate().plusDays(1).atStartOfDay())){
            return false;
        }
//        //新增时核销状态!=未核销/部分核销/核销完毕返回false不更新
//        List<String> array = Arrays.asList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(),CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(), CollectionWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
//        //更新时核销完毕（且在待报送还款表内不存在）才可更新
//        //更新时核销状态!=未核销/部分核销返回false不更新
//        if(isUpdate){
//            array = Arrays.asList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(), CollectionWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
//        }
        //待核销/部分核销可更新数据
        CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .eq(CollectionBaseInfo::getPhase, crRepayPlanDraft.getPhase())
                .eq(CollectionBaseInfo::getReceiptId, receiptId)
                .eq(CollectionBaseInfo::getContractId, crRepayPlanDraft.getContractId())
                .in(CollectionBaseInfo::getWriteOffStatus,Arrays.asList(CollectionWriteOffStatusEnum.UNCOLLECTION.name(), CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(), CollectionWriteOffStatusEnum.TO_BE_WRITE_OFF.name())));
        if(Objects.isNull(collectionBaseInfo)){
            return false;
        }
        //该期次数据在逾期表内是否存在
        if(getPlanIsOverdue(crRepayPlanDraft)){
            //该期次数据在逾期表内存在
            return false;
        }
        return true;
    }


    /**
     * 传进来一个CrOverdueRecordDraft，判断还款表是否有值
     * @param recordDraft
     * @return
     */
    public void deletePlanForOverdue(CrOverdueRecordDraft recordDraft){
        //当合同某一期次逾期，在逾期表内生成时，同时判断若该期次数据在还款表内存在，则在还款表内将该期次数据删除（物理删除）
        //该期次数据还款表内是否存在
        if(Objects.nonNull(recordDraft)&&Objects.nonNull(recordDraft.getPhase())){
            String[] array = recordDraft.getPhase().replace(" ","").replace("[", "").replace("]", "").split(",");
            List<CrRepayPlanDraft> crRepayPlanDrafts = crRepayPlanDraftMapper.selectList(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                    .eq(CrRepayPlanDraft::getContractId, recordDraft.getContractId())
                    .eq(CrRepayPlanDraft::getPaymentApplyCode, recordDraft.getPaymentApplyCode())
                    .eq(CrRepayPlanDraft::getPaymentId, recordDraft.getPaymentId())
                    .eq(CrRepayPlanDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .eq(CrRepayPlanDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .in(CrRepayPlanDraft::getPhase, array));
            List<Long> ids = new ArrayList<>();
            for(CrRepayPlanDraft crRepayPlanDraft : crRepayPlanDrafts){
                ids.add(crRepayPlanDraft.getId());
            }
            if(CollUtil.isNotEmpty(ids)){
                LambdaUpdateWrapper<CrRepayPlanDraft> repayPlanUpdateWrapper = new LambdaUpdateWrapper<>();
                repayPlanUpdateWrapper.in(CrRepayPlanDraft::getId, ids);
                repayPlanUpdateWrapper.set(CrRepayPlanDraft::getIsShow, YesOrNoNumberEnum.NO.getCode());
                crRepayPlanDraftMapper.update(null, repayPlanUpdateWrapper);
                //crRepayPlanDraftMapper.deleteBatchIds(ids);
            }
        }
    }


    /**
     * 传进来一个crRepayPlanDraft，判断是否在已报送逾期表存在
     * @param crRepayPlanDraft
     * @return
     */
    public boolean getPlanIsOverdueRecord(CrRepayPlanDraft crRepayPlanDraft){
        //该期次数据在逾期表内是否存在
        List<CrOverdueRecord> crOverdueRecords = crOverdueRecordMapper.selectList(Wrappers.<CrOverdueRecord>lambdaQuery()
                .eq(CrOverdueRecord::getContractId, crRepayPlanDraft.getContractId())
                .eq(CrOverdueRecord::getPaymentApplyCode, crRepayPlanDraft.getPaymentApplyCode())
                .eq(CrOverdueRecord::getPaymentId, crRepayPlanDraft.getPaymentId()));
        for(CrOverdueRecord crOverdueRecord : crOverdueRecords){
            String phase = crOverdueRecord.getPhase();
            if(StringUtils.isNotEmpty(phase)){
                String[] array = phase.replace(" ","").replace("[", "").replace("]", "").split(",");
                for (String str : array) {
                    int overduePhase = Integer.parseInt(str);
                    if(overduePhase == crRepayPlanDraft.getPhase()){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 判断是否在逾期表存在
     * @param
     * @return
     */
    public boolean getPlanIsOverdue(Long contractId, String paymentApplyCode, int conPhase){
        //该期次数据在逾期表内是否存在
        List<CrOverdueRecordDraft> crOverdueRecordDrafts = crOverdueRecordDraftMapper.selectList(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .eq(CrOverdueRecordDraft::getContractId, contractId)
                .eq(CrOverdueRecordDraft::getPaymentApplyCode, paymentApplyCode)
                .eq(CrOverdueRecordDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrOverdueRecordDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name()));
        for(CrOverdueRecordDraft crOverdueRecordDraft : crOverdueRecordDrafts){
            String phase = crOverdueRecordDraft.getPhase();
            if(StringUtils.isNotEmpty(phase)){
                String[] array = phase.replace(" ","").replace("[", "").replace("]", "").split(",");
                for (String str : array) {
                    int overduePhase = Integer.parseInt(str);
                    if(overduePhase == conPhase){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 传进来一个crRepayPlanDraft，获取宽限期
     * @param crRepayPlanDraft
     * @return
     */
    public String getGracePeriod(CrRepayPlanDraft crRepayPlanDraft){
        // 理论上不会为null， 但是为了防止数据错误，这里做空判断
        String gracePeriod = "0";
        if(Objects.nonNull(crRepayPlanDraft)){
            gracePeriod = crRepayPlanDraft.getGracePeriod();
        }else{
            return gracePeriod;
        }
        //获取是否存在有效的修改数据
        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, crRepayPlanDraft.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.REPAY_PLAN.name())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CrModifyDataSnap::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotNull(modifyDataSnap)) {
            Object object = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrRepayPlanDraft.class);
            Object value = ReflectUtil.getFieldValue(object, "gracePeriod");
            if (ObjectUtil.isNotNull(value)) {
                gracePeriod = value.toString();
            }
        }
        return gracePeriod;
    }



    /**
     * 传进来一个paymentList，过滤返回其中需要报送的
     * 1、生效、核销完毕
     * 2、不归属于特殊合同
     * 3、关联到借据
     * 4、单借据多付款情况下 找到报送过的那个付款 过滤掉其他付款
     *
     * @param paymentBaseInfoList
     * @return
     */
    public List<PaymentBaseInfo> filterNeedReportPaymentListSubTableNew(List<PaymentBaseInfo> paymentBaseInfoList) {
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return paymentBaseInfoList;
        }
        // 过滤 1、2、3
        paymentBaseInfoList = paymentBaseInfoList.stream()
                .filter(p -> PaymentStatusEnum.FINISHED.name().equals(p.getPaymentStatus()))
                .filter(p -> (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(p.getWriteOffStatus()) ||
                        PaymentWriteOffStatus.PART_WRITTEN_OFF.name().equals(p.getWriteOffStatus())))
                .collect(Collectors.toList());
        return paymentBaseInfoList;
    }

}
