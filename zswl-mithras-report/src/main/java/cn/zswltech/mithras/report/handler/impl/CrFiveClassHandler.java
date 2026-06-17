package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.api.report.ReportAssetClassifyClientSnapshot;
import cn.zswltech.mithras.api.report.ReportAssetClassifyPort;
import cn.zswltech.mithras.report.enums.biz.FiveClassEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrFiveClassDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrFiveClass;
import cn.zswltech.mithras.report.service.draft.CrFiveClassDraftService;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-五级分类表
 * 需要在accountHandler之后执行
 *
 * @author wangchuanhao
 * @date 2022/10/8 2:52 PM
 */
@Component
@Slf4j
@Order(100)
public class CrFiveClassHandler extends CrAbstractHandler<CrFiveClassDraft, CrFiveClass> {

    @Resource
    private CrAccountDraftMapper crAccountDraftMapper;
    @Resource
    private CrFiveClassDraftService crFiveClassDraftService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ReportAssetClassifyPort reportAssetClassifyPort;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.FIVE_CLASS;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {

        // 如果account对应的paymentCode没有五级分类记录或者相对于上一次报送的记录状态发生变化，就要新增一条
        List<CrAccountDraft> all = crAccountDraftMapper.selectList(null);
        //过滤掉不是在租的合同
        if (CollectionUtils.isEmpty(all)) {
            return;
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(all.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet()));
        if (CollUtil.isEmpty(contractBaseInfos)) {
            return;
        }
        Set<Long> contractIds = contractBaseInfos.stream()
                .filter(a -> ContractStatus.START_RENT.name().equals(a.getContractStatus()) || ContractStatus.TAKE_EFFECT.name().equals(a.getContractStatus()))
                .map(ContractBaseInfo::getId).collect(Collectors.toSet());
        List<CrAccountDraft> accountDraftList = all.stream().filter(a -> contractIds.contains(a.getContractId())).collect(Collectors.toList());

        //第一次默认正常
        List<CrFiveClassDraft> classDrafts = crFiveClassDraftService.list(Wrappers.<CrFiveClassDraft>lambdaQuery()
                .in(CrFiveClassDraft::getContractId, contractIds));
        if (CollUtil.isNotEmpty(classDrafts)) {
            Set<String> longSet = classDrafts.stream().map(CrFiveClassDraft::getPaymentApplyCode).collect(Collectors.toSet());
            List<CrAccountDraft> collect = accountDraftList.stream().filter(a -> !longSet.contains(a.getPaymentApplyCode())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) {
                saveNewHandler(collect);
            }
        }

        if (CollUtil.isEmpty(classDrafts)) {
            saveNewHandler(accountDraftList);
        }
        List<ReportAssetClassifyClientSnapshot> classifyClientList =
                reportAssetClassifyPort.findFinishedClientClassifySnapshots(dealTime.toLocalDate());
        if (CollUtil.isEmpty(classifyClientList)) {
            return;
        }

        //根据客户ID分组并取出最新版本
        Map<Long, ReportAssetClassifyClientSnapshot> clientAuxiliaryLibMap = classifyClientList.stream()
                .collect(Collectors.toMap(ReportAssetClassifyClientSnapshot::getClientId, Function.identity(), (k1, k2) -> k1));

        Set<String> existFiveClassPaymentIdSet = new HashSet<>();
        if (CollUtil.isNotEmpty(classDrafts)) {
            existFiveClassPaymentIdSet = classDrafts.stream().map(CrFiveClassDraft::getPaymentApplyCode).collect(Collectors.toSet());
        }
        List<CrFiveClassDraft> needInsertList = new ArrayList<>();
        List<CrAccountDraft> tempAccountDraftList = new ArrayList<>();
        for (int i = 0; i < accountDraftList.size(); i++) {
            CrAccountDraft accountDraft = accountDraftList.get(i);
            if (!existFiveClassPaymentIdSet.contains(accountDraft.getPaymentApplyCode())) {
                //新增的不需要考虑比较的问题
                CrFiveClassDraft fiveClassDraft = new CrFiveClassDraft();
                fiveClassDraft.setReportState(ReportState.TO_BE_REPORT.name());
                fiveClassDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                fiveClassDraft.setPaymentId(accountDraft.getPaymentId());
                fiveClassDraft.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
                fiveClassDraft.setIdentificationDate(classifyClientList.get(0).getIdentificationDate());
                fiveClassDraft.setFiveClass(FiveClassEnum.NORMAL.getValue());
                fiveClassDraft.setContractId(accountDraft.getContractId());
                fiveClassDraft.setBusinessKey(fiveClassDraft.genBusinessKey());
                needInsertList.add(fiveClassDraft);
            }
            tempAccountDraftList.add(accountDraft);
        }

        List<CrFiveClassDraft> needUpdateList = new LinkedList<>();
        //处理需要更新的或者新插入一条的
        Set<Long> paymentIds = tempAccountDraftList.stream().map(CrAccountDraft::getPaymentId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(paymentIds)) {
            Map<String, CrFiveClassDraft> fiveClassDraftMap = new HashMap<>(8);
            Map<String, List<CrFiveClassDraft>> longListMap = crFiveClassDraftService.list(Wrappers.<CrFiveClassDraft>lambdaQuery().in(CrFiveClassDraft::getPaymentId, paymentIds))
                    .stream().collect(Collectors.groupingBy(CrFiveClassDraft::getPaymentApplyCode));
            longListMap.forEach((k, v) -> {
                if (CollUtil.isNotEmpty(v)) {
                    v.sort(Comparator.comparing(CrFiveClassDraft::getCreateTime).reversed());
                    fiveClassDraftMap.put(k, v.get(0));
                }
            });
            for (CrAccountDraft draft : tempAccountDraftList) {
                ReportAssetClassifyClientSnapshot clientLib = clientAuxiliaryLibMap.get(draft.getClientId());
                if (Objects.nonNull(clientLib)) {
                    CrFiveClassDraft classDraft = fiveClassDraftMap.get(draft.getPaymentApplyCode());
                    if (Objects.nonNull(classDraft) && !Objects.equals(FiveClassEnum.of(clientLib.getClassifyResult()).getValue(), classDraft.getFiveClass())) {
                        //查询征信五级分类表判断修改还是新增
                        if (ReportState.TO_BE_REPORT.name().equals(classDraft.getReportState())) {
                            //更新
                            CrFiveClassDraft build = new CrFiveClassDraft();
                            build.setFiveClass(FiveClassEnum.of(clientLib.getClassifyResult()).getValue());
                            build.setId(classDraft.getId());
                            build.setIdentificationDate(clientLib.getIdentificationDate());
                            needUpdateList.add(build);
                        } else {
                            //新增
                            CrFiveClassDraft fiveClassDraft = new CrFiveClassDraft();
                            fiveClassDraft.setReportState(ReportState.TO_BE_REPORT.name());
                            fiveClassDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                            fiveClassDraft.setPaymentId(draft.getPaymentId());
                            fiveClassDraft.setPaymentApplyCode(draft.getPaymentApplyCode());
                            fiveClassDraft.setIdentificationDate(clientLib.getIdentificationDate());
                            fiveClassDraft.setFiveClass(FiveClassEnum.of(clientLib.getClassifyResult()).getValue());
                            fiveClassDraft.setContractId(draft.getContractId());
                            fiveClassDraft.setBusinessKey(fiveClassDraft.genBusinessKey());
                            needInsertList.add(fiveClassDraft);
                        }
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(needUpdateList)) {
            draftService.saveOrUpdateBatch(needUpdateList);
        }
        if (CollUtil.isNotEmpty(needInsertList)) {
            draftService.saveBatch(needInsertList);
        }
    }

    private void saveNewHandler(List<CrAccountDraft> collect) {
        List<CrFiveClassDraft> temp = new LinkedList<>();
        for (CrAccountDraft accountDraft : collect) {
            CrFiveClassDraft fiveClassDraft = new CrFiveClassDraft();
            fiveClassDraft.setReportState(ReportState.TO_BE_REPORT.name());
            fiveClassDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
            fiveClassDraft.setPaymentId(accountDraft.getPaymentId());
            fiveClassDraft.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
            fiveClassDraft.setIdentificationDate(accountDraft.getLendingDate().atStartOfDay());
            fiveClassDraft.setFiveClass(FiveClassEnum.NORMAL.getValue());
            fiveClassDraft.setContractId(accountDraft.getContractId());
            fiveClassDraft.setBusinessKey(fiveClassDraft.genBusinessKey());
            temp.add(fiveClassDraft);
        }
        crFiveClassDraftService.saveBatch(temp);
    }

    @Override
    public Integer sort() {
        return 14;
    }
}
