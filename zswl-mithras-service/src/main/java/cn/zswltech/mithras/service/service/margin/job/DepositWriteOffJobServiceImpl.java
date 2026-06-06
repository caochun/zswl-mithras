package cn.zswltech.mithras.service.service.margin.job;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.RentCollectionIndexPaymentState;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRetreatInfo;
import cn.zswltech.mithras.margin.application.job.DepositWriteOffJobService;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.system.service.BizProcessDataService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractDeductRentInfoService;
import cn.zswltech.mithras.contract.core.application.ContractRetreatInfoService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/10/27
 * @description 保证金抵扣/退还流程生成待办
 */
@Slf4j
@Component
public class DepositWriteOffJobServiceImpl implements DepositWriteOffJobService {
    @Resource
    private ContractRetreatInfoService contractRetreatInfoService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private ContractDeductRentInfoService contractDeductRentInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BizProcessDataService bizProcessDataService;

    @Override
    public void sendNoticeJob() {
        // 1、找到所有的已经通过的，退回金额>0 、推送标记不为1的信息
        LambdaQueryWrapper<ContractRetreatInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.APPROVAL_PASS.name());
        query.gt(ContractRetreatInfo::getReturnedAmount, 0);
        query.and(q -> q.isNull(ContractRetreatInfo::getSendedFlag)
                .or().eq(ContractRetreatInfo::getSendedFlag, "0"));
        List<ContractRetreatInfo> contractRetreatInfoList = contractRetreatInfoService.list(query);
        if (ObjectUtil.isEmpty(contractRetreatInfoList)) {
            log.info("没有需要推送的保证金退回信息");
            return;
        }

        for (ContractRetreatInfo contractRetreatInfo : contractRetreatInfoList) {
            contractDeductRentInfoService.checkRentAndSend(contractRetreatInfo);
        }
    }

    @Override
    public void depositWriteOffJob() {
        /* 1、找到不同合同保证金余额， 看余额能覆盖最后几个期次，从期次编号最小的 开始的计划收款日期前的 30天 */
        LocalDate applyPayDateLeft = LocalDate.now().plusDays(29);
        LocalDate applyPayDateRight = LocalDate.now().plusDays(31);
        LambdaQueryWrapper<MarginBaseInfo> query = Wrappers.lambdaQuery();
        query.gt(MarginBaseInfo::getCollectionAmount, 0); // 保证金余额 > 0
        List<MarginBaseInfo> marginBaseInfoList = marginBaseInfoService.list(query);

        /* 2、找到所有的(正常结清、提前结清、合同变更)流程中的合同信息  并从合同清单中摒弃掉*/
        ProcessPageReq req = new ProcessPageReq();
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        req.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ContractEarlySettleFlow.name(),
                ProcessModelTypeEnum.ContractNormalSettleFlow.name(),
                ProcessModelTypeEnum.ContractLPRChangeFlow.name(),
                ProcessModelTypeEnum.ContractExtensionFlow.name(),
                ProcessModelTypeEnum.ContractModifyFlow.name(),
                ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name()
        ));
        List<ProcessResp> processPage = flowTaskApiService.queryProcess(req).getContents();
        Set<String> contractIdSet = processPage.stream().map(ProcessResp::getBusinessKey).collect(Collectors.toSet());

        /*3、找到所有的保证金退抵流程， 在流程中或者待提交状态的数据*/
        LambdaQueryWrapper<ContractRetreatInfo> retreaWrapper = Wrappers.lambdaQuery();
        retreaWrapper.in(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.UN_SUBMIT.name(), FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
        List<ContractRetreatInfo> contractRetreatInfoList = contractRetreatInfoService.list(retreaWrapper);
        Set<String> contractIdRetreaSet = contractRetreatInfoList.stream().filter(e -> FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name().equals(e.getProcessStatus()))
                .map(ContractRetreatInfo::getContractId).collect(Collectors.toSet());
        contractIdSet.addAll(contractIdRetreaSet);

        for (MarginBaseInfo marginBaseInfo : marginBaseInfoList) {
            if (contractIdSet.contains(marginBaseInfo.getContractId().toString())) {
                continue;
            }

            Long collectionAmount = LongUtil.null2zero(marginBaseInfo.getCollectionAmount()); //保证金余额
            if (collectionAmount <= 0) {
                continue;
            }

            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(marginBaseInfo.getContractId());

            /*非起租状态的合同无自动发起流程*/
            if (!RentCollectionIndexPaymentState.START_RENT.name().equals(contractBaseInfo.getContractStatus())) {
                continue;
            }

            /*4、根据合同取找未核销的收款计划，  从后往前找，当保证金余额不足以抵扣的时候，判断该期次计划收款日期，如果在30天内，则生成待提交*/
            LambdaQueryWrapper<CollectionBaseInfo> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(CollectionBaseInfo::getContractId, marginBaseInfo.getContractId());
            wrapper.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()); // 租金
            wrapper.ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED); // 未核销完成
            wrapper.orderByDesc(CollectionBaseInfo::getPlanCollectionDate); // 计划收款日期 倒序查询
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(wrapper);

            if (ObjectUtil.isEmpty(collectionBaseInfoList)) { // 没有待核销租金收款计划
                continue;
            }

            boolean autoPrepare = false; // 自动生成待提交任务 标记
            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                if (collectionAmount > collectionBaseInfo.getPlanCollectionAmount()) { // 剩余金额大于本期计划收款金额
                    collectionAmount = collectionAmount - collectionBaseInfo.getPlanCollectionAmount();
                } else {
                    // 该期次的计划收款日期在今天后第30天
                    if (applyPayDateLeft.isBefore(collectionBaseInfo.getPlanCollectionDate()) && collectionBaseInfo.getPlanCollectionDate().isBefore(applyPayDateRight)) {
                        autoPrepare = true; // 自动生成待提交任务
                    }
                    break;
                }
            }

            /*5、 满足条件的生成待提交 */
            if (autoPrepare) {
                /*获取 合同退抵信息*/
                LambdaQueryWrapper<ContractRetreatInfo> infoWrapper = Wrappers.lambdaQuery();
                infoWrapper.eq(ContractRetreatInfo::getContractId, marginBaseInfo.getContractId().toString());
                infoWrapper.eq(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.UN_SUBMIT.name());
                infoWrapper.last(StringUtil.mysqlLimitOne());
                ContractRetreatInfo info = contractRetreatInfoService.getOne(infoWrapper);
                if (info == null) {
                    // 生成 合同退抵信息
                    info = new ContractRetreatInfo();
                    info.setContractId(contractBaseInfo.getId().toString());
                    info.setContractCode(contractBaseInfo.getContractCode());
                    info.setProcessStatus(FilingMaterialsProcessStatusEnum.UN_SUBMIT.name());
                    info.setCreateBy(contractBaseInfo.getCreateBy());
                    info.setUpdateBy(contractBaseInfo.getCreateBy());
                    contractRetreatInfoService.saveOrUpdate(info);

                    LambdaQueryWrapper<ContractRetreatInfo> queryWrapper = Wrappers.lambdaQuery();
                    queryWrapper.eq(ContractRetreatInfo::getContractId, marginBaseInfo.getContractId().toString());
                    queryWrapper.eq(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.UN_SUBMIT.name());
                    queryWrapper.last(StringUtil.mysqlLimitOne());
                    info = contractRetreatInfoService.getOne(infoWrapper);
                }
                autoStartProcess(info, contractBaseInfo); // 生成待处理
            }
        }
    }

    /**
     * 自动发起流程，生成待处理
     */

    private void autoStartProcess(ContractRetreatInfo contractRetreatInfo, ContractBaseInfo contractBaseInfo) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.MarginFlowAuto.name()); // 流程类型
        startProcessReq.setProcessInstanceName(contractBaseInfo.getContractCode()); // 流程名称
        Map<String, Object> varMap = new HashMap<>();

        AccountVO accountVO = new AccountVO();
        accountVO.setId(contractBaseInfo.getProjSponsorUserId());
        List<OrgDO> userDeptList = sysUserService.getUserDeptList(accountVO);
        if (ObjectUtil.isNotEmpty(userDeptList)) {
            contractDeductRentInfoService.getHeadUserIds(userDeptList.get(0), varMap);
        }

        //        varMap.put("userTask_deptHand", new LinkedList<>());    //业务部门负责人
//        varMap.put("userTask_leader", new LinkedList<>());      //分管领导
        varMap.put("userTask_financeManagerUser", new LinkedList<>());


        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(contractRetreatInfo.getId()));  // 绑定业务主键
        startProcessReq.setStartUserId(String.valueOf(contractBaseInfo.getProjSponsorUserId())); // 流程创建人
        startProcessReq.setStartUserDeptId(String.valueOf(userDeptList.get(0).getId())); // 流程创建人部门
        String processInstanceId = processApiService.start(startProcessReq);// 启动流程

        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());

        contractRetreatInfo.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        contractRetreatInfoService.updateById(contractRetreatInfo);

        contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.RETREAT_COMMIT.name());
        contractBaseInfoService.updateById(contractBaseInfo);
    }
}
