package cn.zswltech.mithras.report.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.biz.TableTypeEnum;
import cn.zswltech.mithras.report.handler.ReportDataRepository;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/1/12/11:20
 * @description 通用数据集成，解决重复代码
 */
@Component
public class CommonInfoService {
    @Autowired
    private ContractBaseInfoService contractBaseInfoService;
    @Autowired
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ReportDataRepository reportDataRepository;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;
    @Resource
    private Id2NameService id2NameService;

    @NotNull
    public Map<Long, ContractBaseInfo> getLongContractBaseInfoMap(Set<Long> contractIds) {
        Map<Long, ContractBaseInfo> infoMap = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity(), (k1, k2) -> k1));
        return Optional.of(infoMap).orElse(Collections.emptyMap());
    }

    public Map<Long, PaymentBaseInfo> getPaymentBaseInfoMap(Set<Long> paymentIds) {
        if (CollUtil.isEmpty(paymentIds)) {
            return Collections.emptyMap();
        }
        return paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getId, paymentIds))
                .stream().collect(Collectors.toMap(PaymentBaseInfo::getId, Function.identity(), (k1, k2) -> k1));
    }

    public Map<Long, String> getClientId2NameMap(Set<Long> paymentIds) {
        if (CollUtil.isEmpty(paymentIds)) {
            return Collections.emptyMap();
        }
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getId, paymentIds));
        return id2NameService.clientId2Name(paymentBaseInfos.stream().map(PaymentBaseInfo::getClientId).collect(Collectors.toList()));
    }


    public List<ContractTenantryLib> getContractTenantryLibs(ContractBaseInfoLib contractBaseInfoLib) {
        List<ContractTenantryLib> contractTenantryLibList = new ArrayList<>();
        if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) || ProjectBizType.ZZ.name().equals(contractBaseInfoLib.getBizType())) {
            // 联合承租人
            contractTenantryLibList = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                    .eq(ContractTenantryLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.JOINT_LESSEE.name())
                    .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
            );
        } else if (ProjectBizType.BL.name().equals(contractBaseInfoLib.getBizType())) {
            // 债权人且非第一债权人
            contractTenantryLibList = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                    .eq(ContractTenantryLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractTenantryLib::getLesseeType, CreditorDebtorTypeEnum.CREDITOR.name())
                    .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
            );
        }
        return contractTenantryLibList;
    }

    public Map<Long, List<CrAccountDraft>> getPaymentIdAccountListMap(List<PaymentBaseInfo> paymentBaseInfoList) {
        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyMap();
        }
        //将账户信息全部查出并按照 paymentId 分组， 尽量少查数据库
        return crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                        .in(CrAccountDraft::getPaymentId, paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(CrAccountDraft::getPaymentId));
    }

    public List<PaymentBaseInfo> getPaymentBaseInfos(ContractBaseInfoLib contractBaseInfoLib) {
        if (Objects.isNull(contractBaseInfoLib)) {
            return Collections.emptyList();
        }
        // 找一找该合同有没有付款核销完毕或者部分核销的付款申请数据
        return paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getContractId, contractBaseInfoLib.getOriginId())
                .in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(RecordStatus.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()))
                .in(PaymentBaseInfo::getWriteOffStatus, Arrays.asList(PaymentWriteOffStatus.WRITTEN_OFF.name(), PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))
        );
    }

    public List<ContractBaseInfoLib> getContractBaseInfoLibs(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> contractBaseInfoLibList = stringListMap.get(DataTypeEnum.NOT_ZHI_ZU.name());
        if (CollUtil.isEmpty(contractBaseInfoLibList)) {
            contractBaseInfoLibList = Collections.emptyList();
        }
        return contractBaseInfoLibList;
    }


    public <T> List<Map<String, DiffValue>> buildDiffMapList(List<T> rspList, List<String> businessKeys, String procBusinessKey,
                                                             TableTypeEnum tableTypeEnum, Class<?> clazz, String batchNo, Integer isShow) {
        List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(Objects.nonNull(isShow), CrModifyDataSnap::getIsShow, isShow)
                .eq(CharSequenceUtil.isNotBlank(procBusinessKey), CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .eq(CharSequenceUtil.isNotBlank(batchNo), CrModifyDataSnap::getBatchNo, batchNo)
                .in(CollUtil.isNotEmpty(businessKeys), CrModifyDataSnap::getBusinessKey, businessKeys)
                .eq(CrModifyDataSnap::getTableType, tableTypeEnum.name()));
        Map<String, CrModifyDataSnap> dataSnapMap = new HashMap<>();
        if (CollUtil.isNotEmpty(modifyDataSnaps)) {
            dataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
        }
        List<Map<String, DiffValue>> mapList = new LinkedList<>();
        Map<String, CrModifyDataSnap> finalDataSnapMap = dataSnapMap;
        rspList.forEach(dto -> {
            Map<String, DiffValue> map = new HashMap<>(8);
            Field[] fields = ReflectUtil.getFields(dto.getClass());
            CrModifyDataSnap dataSnap = finalDataSnapMap.get(ReflectUtil.getFieldValue(dto, "businessKey"));
            for (Field field : fields) {
                DiffValue diffValue = new DiffValue();
                Object fieldValue = ReflectUtil.getFieldValue(dto, field);
                //处理账户表report_flag
                if ("reportFlag".equals(field.getName())) {
                    diffValue.setIsChange(Boolean.FALSE);
                    diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                    diffValue.setBeforeValue(ReflectUtil.getFieldValue(dto, field));
                    diffValue.setValue(diffValue.getBeforeValue());
                    map.put(field.getName(), diffValue);
                    continue;
                }
                if (ObjectUtil.isNull(dataSnap)) {
                    //没改过
                    if (!"label".equals(field.getName()) && !"reason".equals(field.getName())) {
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setBeforeValue(fieldValue);
                        diffValue.setValue(fieldValue);
                    }
                    if ("label".equals(field.getName())) {
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setIsChange(Boolean.FALSE);
                    }
                    if ("reason".equals(field.getName())) {
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setIsChange(Boolean.FALSE);
                    }
                } else {
                    Object object = JSONUtil.toBean(dataSnap.getDataMap(), clazz);
                    Object value = ReflectUtil.getFieldValue(object, field);
                    if (ObjectUtil.isNull(value)) {
                        //没改过
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setBeforeValue(fieldValue);
                        diffValue.setValue(fieldValue);
                    } else {
                        //改过
                        if (!DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel()) && !DataShowTypeEnum.REMOVE.name().equals(dataSnap.getLabel()) && !value.equals(fieldValue)) {
                            diffValue.setIsChange(Boolean.TRUE);
                            diffValue.setChangeType(dataSnap.getLabel());
                            diffValue.setBeforeValue(fieldValue);
                            diffValue.setValue(value);
                        }
                        if (!DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel()) && !DataShowTypeEnum.REMOVE.name().equals(dataSnap.getLabel()) && value.equals(fieldValue)) {
                            diffValue.setIsChange(Boolean.FALSE);
                            diffValue.setChangeType(dataSnap.getLabel());
                            diffValue.setBeforeValue(fieldValue);
                            diffValue.setValue(value);
                        }
                        if (DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                            diffValue.setIsChange(Boolean.TRUE);
                            diffValue.setChangeType(DataShowTypeEnum.ADD.name());
                            diffValue.setValue(value);
                        }
                        if (DataShowTypeEnum.REMOVE.name().equals(dataSnap.getLabel())) {
                            diffValue.setIsChange(Boolean.TRUE);
                            diffValue.setChangeType(DataShowTypeEnum.REMOVE.name());
                            diffValue.setBeforeValue(value);
                            diffValue.setValue(null);
                        }
                    }
                    Object repayPlanDraft = BeanUtil.copyProperties(dto, clazz, ReportConstants.IGNORE_ID);
                    ReflectUtil.setFieldValue(repayPlanDraft, "contractCode", ReflectUtil.getFieldValue(object, "contractCode"));
                    if ("label".equals(field.getName()) && repayPlanDraft.equals(object) && !DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                        diffValue.setChangeType(null);
                        diffValue.setValue(null);
                    }
                    if ("label".equals(field.getName()) && !repayPlanDraft.equals(object)) {
                        diffValue.setChangeType(dataSnap.getLabel());
                        diffValue.setIsChange(Boolean.TRUE);
                        diffValue.setValue(dataSnap.getLabel());
                    }
                    if ("label".equals(field.getName()) && DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                        diffValue.setChangeType(dataSnap.getLabel());
                        diffValue.setIsChange(Boolean.TRUE);
                        diffValue.setValue(dataSnap.getLabel());
                    }
                    if ("reason".equals(field.getName())) {
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setValue(dataSnap.getReason());
                        diffValue.setChangeType(dataSnap.getLabel());
                    }
                }
                map.put(field.getName(), diffValue);
            }
            mapList.add(map);
        });
        return mapList;
    }

    public <T> List<Map<String, DiffValue>> buildDiffMapEffectList(List<T> rspList, List<String> businessKeys, TableTypeEnum tableTypeEnum, Class<?> clazz) {
        List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .ne(CrModifyDataSnap::getLabel, DataShowTypeEnum.REMOVE.name())
                .eq(CrModifyDataSnap::getIsShow, YesOrNoNumberEnum.NO.getCode())
                .in(CollUtil.isNotEmpty(businessKeys), CrModifyDataSnap::getBusinessKey, businessKeys)
                .eq(CrModifyDataSnap::getTableType, tableTypeEnum.name()));
        Map<String, CrModifyDataSnap> dataSnapMap = new HashMap<>();
        if (CollUtil.isNotEmpty(modifyDataSnaps)) {
            dataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
        }
        List<Map<String, DiffValue>> mapList = new LinkedList<>();
        Map<String, CrModifyDataSnap> finalDataSnapMap = dataSnapMap;
        rspList.forEach(dto -> {
            Map<String, DiffValue> map = new HashMap<>(8);
            Field[] fields = ReflectUtil.getFields(dto.getClass());
            CrModifyDataSnap dataSnap = finalDataSnapMap.get(ReflectUtil.getFieldValue(dto, "businessKey"));
            for (Field field : fields) {
                DiffValue diffValue = new DiffValue();
                Object fieldValue = ReflectUtil.getFieldValue(dto, field);
                if (ObjectUtil.isNull(dataSnap)) {
                    //没改过
                    if (!"label".equals(field.getName()) && !"reason".equals(field.getName())) {
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setBeforeValue(fieldValue);
                        diffValue.setValue(fieldValue);
                    }
                    if ("label".equals(field.getName())) {
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setIsChange(Boolean.FALSE);
                    }
                    if ("reason".equals(field.getName())) {
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setIsChange(Boolean.FALSE);
                    }
                } else {
                    Object object = JSONUtil.toBean(dataSnap.getDataMap(), clazz);
                    Object value = ReflectUtil.getFieldValue(object, field);
                    if (ObjectUtil.isNull(value)) {
                        //没改过
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setValue(fieldValue);
                        diffValue.setBeforeValue(fieldValue);
                    } else {
                        //改过
                        if (!DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel()) && !value.equals(fieldValue)) {
                            diffValue.setIsChange(Boolean.TRUE);
                            diffValue.setChangeType(dataSnap.getLabel());
                            diffValue.setBeforeValue(fieldValue);
                            diffValue.setValue(value);
                        } else if (!DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel()) && value.equals(fieldValue)) {
                            diffValue.setIsChange(Boolean.FALSE);
                            diffValue.setBeforeValue(fieldValue);
                            diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                            diffValue.setValue(value);
                        } else if (DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                            diffValue.setIsChange(Boolean.TRUE);
                            diffValue.setChangeType(dataSnap.getLabel());
                            diffValue.setValue(value);
                        }
                    }
                    Object repayPlanDraft = BeanUtil.copyProperties(dto, clazz, ReportConstants.IGNORE_ID);
                    ReflectUtil.setFieldValue(repayPlanDraft, "contractCode", ReflectUtil.getFieldValue(object, "contractCode"));
                    if ("label".equals(field.getName()) && repayPlanDraft.equals(object) && !DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                        diffValue.setChangeType(null);
                        diffValue.setValue(null);
                    }
                    if ("label".equals(field.getName()) && !repayPlanDraft.equals(object)) {
                        diffValue.setChangeType(dataSnap.getLabel());
                        diffValue.setIsChange(Boolean.TRUE);
                        diffValue.setValue(dataSnap.getLabel());
                    }
                    if ("label".equals(field.getName()) && DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                        diffValue.setChangeType(dataSnap.getLabel());
                        diffValue.setIsChange(Boolean.TRUE);
                        diffValue.setValue(dataSnap.getLabel());
                    }
                    if ("reason".equals(field.getName())) {
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setValue(dataSnap.getReason());
                        diffValue.setChangeType(dataSnap.getLabel());
                    }
                }
                map.put(field.getName(), diffValue);
            }
            mapList.add(map);
        });
        return mapList;
    }

    @Autowired
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    /**
     * 填充结清时间
     */
    public void fillClosedDate(List<CrAccountDraft> reportDataList) {
        if (CollUtil.isEmpty(reportDataList)) {
            return;
        }
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectBatchIds(reportDataList.stream().map(CrAccountDraft::getPaymentId).collect(Collectors.toList()));
        Map<Long, Long> receiptIdMap = paymentBaseInfos.stream()
                .filter(a -> Objects.nonNull(a.getReceiptIdFinal()))
                .collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getReceiptIdFinal));
        reportDataList.forEach(c -> {
            //最后一笔租金 ===> 不再判断最后一期租金是否核销完毕，而是所有的租金和罚息都核销完毕
            List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getReceiptId, receiptIdMap.get(c.getPaymentId()))
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ne(CollectionBaseInfo::getPhase, 0)
            );

            if (CollUtil.isNotEmpty(collectionBaseInfos)) {
                // 计算是否核销完毕
                long totalRemain = collectionBaseInfos.stream().map(e -> {
                    long remainRent = LongUtil.null2zero(e.getPlanCollectionAmount()) - LongUtil.null2zero(e.getCollectionAmount());
                    long remainPenaltyInterest = LongUtil.null2zero(e.getPenaltyInterest()) - LongUtil.null2zero(e.getCollectionPenaltyInterest());
                    return remainRent + remainPenaltyInterest;
                }).reduce(Long::sum).orElse(9999999999L);

                boolean ok = totalRemain <= 0;
                if (ok) {
                    collectionBaseInfos.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate).reversed());
                    c.setClosedDate(collectionBaseInfos.get(0).getCollectionDate());
                }
            }
        });
    }
}
