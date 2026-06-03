package cn.zswltech.mithras.service.service.contract;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListREQ;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListRSP;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractEvaluationAgencyEffectMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.TycAppraisalCompanyBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractEvaluationAgencyEffect;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemAppraisalRelation;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.TycAppraisalCompanyBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseAppraisalService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractEvaluationAgencyDraft;
import cn.zswltech.mithras.contract.mapper.contract.ContractEvaluationAgencyDraftMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @description 针对表【contract_evaluation_agency(合同租赁物评估机构关联草稿表)】的数据库操作Service实现
 * @createDate 2025-03-21 14:38:07
 */
@Slf4j
@Service
public class ContractEvaluationAgencyDraftService extends ServiceImpl<ContractEvaluationAgencyDraftMapper, ContractEvaluationAgencyDraft>
        implements IService<ContractEvaluationAgencyDraft> {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractEvaluationAgencyDraftService draftService;
    @Resource
    private TycAppraisalCompanyBaseInfoMapper companyBaseInfoMapper;
    @Resource
    private LeaseAppraisalService leaseAppraisalService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private ContractEvaluationAgencyEffectMapper effectMapper;

    @Transactional(rollbackFor = Throwable.class)
    public List<ContractEvaluationAgencyListRSP> queryList(ContractEvaluationAgencyListREQ req) {
        // 查询合同流状态，不通的状态需要查询不同的数据
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(req.getContractId());
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));

        ContractProcessStatusEnum currentStatus = ContractProcessStatusEnum.of(contractBaseInfo.getContractProcessStatus());
        if (Objects.isNull(currentStatus)) {
            log.error(String.format("合同编号：【%s】合同流程状态异常", contractBaseInfo.getContractCode()));
            return Collections.emptyList();
        }
        if (CharSequenceUtil.equalsAny(currentStatus.name(), ContractProcessStatusEnum.NEW_UNCOMMIT.name(),
                ContractProcessStatusEnum.NEW_COMMIT.name(), ContractProcessStatusEnum.NEW_CANCEL.name(),
                ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractProcessStatusEnum.CHANGE_COMMIT.name())) {
            // 取消新建状态比较特殊，查得到就查的到，查不到就算了
            if (CharSequenceUtil.equalsAny(currentStatus.name(), ContractProcessStatusEnum.NEW_COMMIT.name(),
                    ContractProcessStatusEnum.CHANGE_COMMIT.name())) {
                // 查看当前的审批节点是不是项目经理节点，是的话需要更新数据
                ProcessPageReq processPageReq = new ProcessPageReq();
                processPageReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name()));
                processPageReq.setBusinessKey(req.getContractId().toString());
                processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
                List<ProcessResp> contents = flowTaskApiService.queryProcess(processPageReq).getContents();
                if (ObjectUtil.isEmpty(contents)) {
                    return Collections.emptyList();
                }
                log.info("查询到合同：【{}】的创建或变更流程列表：{}", contractBaseInfo.getContractCode(), contents);
                ProcessResp processResp = contents.get(0);
                if (processResp.getCurTaskActivityIds().contains("userTask_startUser")) {
                    draftService.copyLastest2Edit(contractBaseInfo.getId());
                }
            } else {
                // 直接拷贝最新的数据到编辑区即可
                draftService.copyLastest2Edit(contractBaseInfo.getId());
            }
            // 以上状态说明合同还在变更中，需要查询草稿数据
            List<ContractEvaluationAgencyDraft> contractEvaluationAgencies = baseMapper.selectList(Wrappers.<ContractEvaluationAgencyDraft>lambdaQuery()
                    .eq(ContractEvaluationAgencyDraft::getContractId, req.getContractId()));
            if (ObjectUtil.isEmpty(contractEvaluationAgencies)) {
                return Collections.emptyList();
            }
            List<ContractEvaluationAgencyListRSP> resultList = new ArrayList<>();
            List<Long> companyIdList = contractEvaluationAgencies.stream().map(ContractEvaluationAgencyDraft::getCompanyId).collect(Collectors.toList());
            List<TycAppraisalCompanyBaseInfo> companyBaseInfoList = companyBaseInfoMapper.selectBatchIds(companyIdList);
            Map<Long, TycAppraisalCompanyBaseInfo> companyBaseInfoMap = new HashMap<>();
            if (ObjectUtil.isNotNull(companyBaseInfoList)) {
                companyBaseInfoMap = companyBaseInfoList.stream().collect(Collectors.toMap(TycAppraisalCompanyBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
            }
            for (ContractEvaluationAgencyDraft item : contractEvaluationAgencies) {
                ContractEvaluationAgencyListRSP rsp = new ContractEvaluationAgencyListRSP();
                rsp.setCompanyId(item.getCompanyId());
                rsp.setPurpose(item.getPurpose());
                TycAppraisalCompanyBaseInfo tycAppraisalCompanyBaseInfo = companyBaseInfoMap.get(item.getCompanyId());
                rsp.setCompanyName(Optional.ofNullable(tycAppraisalCompanyBaseInfo).map(TycAppraisalCompanyBaseInfo::getCompanyName).orElse(null));
                rsp.setSelectType(item.getSelectType());
                resultList.add(rsp);
            }
            return resultList;
        } else {
            // 其余直接查询版本数据
            List<ContractEvaluationAgencyEffect> contractEvaluationAgencyEffectList = effectMapper.selectList(Wrappers.<ContractEvaluationAgencyEffect>lambdaQuery()
                    .eq(ContractEvaluationAgencyEffect::getContractId, req.getContractId()));
            if (ObjectUtil.isEmpty(contractEvaluationAgencyEffectList)) {
                return Collections.emptyList();
            }
            // 构建数据返回
            List<Long> companyIdList = contractEvaluationAgencyEffectList.stream().map(ContractEvaluationAgencyEffect::getCompanyId).collect(Collectors.toList());
            List<TycAppraisalCompanyBaseInfo> companyBaseInfoList = companyBaseInfoMapper.selectBatchIds(companyIdList);
            Map<Long, TycAppraisalCompanyBaseInfo> companyBaseInfoMap = new HashMap<>();
            if (ObjectUtil.isNotNull(companyBaseInfoList)) {
                companyBaseInfoMap = companyBaseInfoList.stream().collect(Collectors.toMap(TycAppraisalCompanyBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
            }
            List<ContractEvaluationAgencyListRSP> resultList = new ArrayList<>();
            for (ContractEvaluationAgencyEffect item : contractEvaluationAgencyEffectList) {
                ContractEvaluationAgencyListRSP rsp = new ContractEvaluationAgencyListRSP();
                rsp.setCompanyId(item.getCompanyId());
                TycAppraisalCompanyBaseInfo companyBaseInfo = companyBaseInfoMap.get(item.getCompanyId());
                rsp.setCompanyName(Optional.ofNullable(companyBaseInfo).map(TycAppraisalCompanyBaseInfo::getCompanyName).orElse(null));
                rsp.setPurpose(item.getPurpose());
                rsp.setSelectType(item.getSelectType());
                resultList.add(rsp);
            }
            return resultList;
        }
    }

    /**
     * 抄送草稿数据到版本表
     *
     * @param contractId 合同ID
     */
    public void copyDraft2Effect(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));
        // 查询草稿数据
        List<ContractEvaluationAgencyDraft> contractEvaluationAgencies = baseMapper.selectList(Wrappers.<ContractEvaluationAgencyDraft>lambdaQuery()
                .eq(ContractEvaluationAgencyDraft::getContractId, contractId));
        // 首先删掉旧数据
        effectMapper.delete(Wrappers.<ContractEvaluationAgencyEffect>lambdaQuery()
                .eq(ContractEvaluationAgencyEffect::getContractId, contractId));
        if (ObjectUtil.isEmpty(contractEvaluationAgencies)) {
            log.info("合同编号：【{}】没有关联的租赁物评级机构", contractBaseInfo.getContractCode());
            return;
        }
        // 插入最新生效数据
        contractEvaluationAgencies.forEach(item -> {
            ContractEvaluationAgencyEffect effect = new ContractEvaluationAgencyEffect();
            effect.setContractId(item.getContractId());
            effect.setCompanyId(item.getCompanyId());
            effect.setPurpose(item.getPurpose());
            effect.setSelectType(item.getSelectType());
            effectMapper.insert(effect);
        });

    }

    /**
     * 找到最新关联的租赁物评估机构放到编辑区
     * 应该在流程提交/退回发起人时调用
     *
     * @param contractId 合同ID
     */
    public void copyLastest2Edit(Long contractId) {
        List<LeaseItemAppraisalRelation> relationList = leaseAppraisalService.queryLastestListByContractId(contractId);
        // 删除草稿数据
        baseMapper.delete(Wrappers.<ContractEvaluationAgencyDraft>lambdaQuery()
                .eq(ContractEvaluationAgencyDraft::getContractId, contractId));
        if (ObjectUtil.isNotEmpty(relationList)) {
            relationList.forEach(item -> {
                ContractEvaluationAgencyDraft draft = new ContractEvaluationAgencyDraft();
                draft.setContractId(contractId);
                draft.setCompanyId(item.getCompanyId());
                draft.setLeaseItemId(item.getLeaseItemId());
                draft.setPurpose(item.getPurpose());
                draft.setSelectType(item.getSelectType());
                baseMapper.insert(draft);
            });
        }
    }
}




