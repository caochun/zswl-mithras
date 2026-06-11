package cn.zswltech.mithras.application.orchestration.contract.impl;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractDeductRentInfoMapper;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractDeductRentInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRetreatInfo;
import cn.zswltech.mithras.workflow.mapper.model.CommonProcessPrepare;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractDeductRentInfoService;
import cn.zswltech.mithras.contract.core.ContractRetreatInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author vico
 * @description 合同抵扣租金信息
 * @date 2022-08-12
 */
@Service
public class ContractDeductRentInfoServiceImpl extends ServiceImpl<ContractDeductRentInfoMapper, ContractDeductRentInfo> implements ContractDeductRentInfoService {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractRetreatInfoService contractRetreatInfoService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private MaterialsListService materialsListService;


    @Override
    public void lockByCode(String code) {
        /*需要找到对应的付款计划并加锁*/
        LambdaQueryWrapper<CollectionBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CollectionBaseInfo::getCode, code);
        wrapper.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        wrapper.last(StringUtil.mysqlLimitOne());
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(wrapper);
        collectionBaseInfo.setRetreatLock("1");
        collectionBaseInfoService.updateById(collectionBaseInfo);
    }

    @Override
    public void unlockByCode(String code) {
        /*需要找到对应的付款计划并加锁*/
        LambdaQueryWrapper<CollectionBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CollectionBaseInfo::getCode, code);
        wrapper.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        wrapper.last(StringUtil.mysqlLimitOne());
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(wrapper);
        collectionBaseInfo.setRetreatLock("0");
        collectionBaseInfoService.updateById(collectionBaseInfo);
    }

    @Override
    public boolean checkByContractId(String contractId) {
        /*找到所有的保证金退抵流程， 在流程中或者待提交状态的数据*/
        LambdaQueryWrapper<ContractRetreatInfo> retreaWrapper = Wrappers.lambdaQuery();
        retreaWrapper.eq(ContractRetreatInfo::getContractId, contractId);
        retreaWrapper.in(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
        List<ContractRetreatInfo> contractRetreatInfoList = contractRetreatInfoService.list(retreaWrapper);
        if (ObjectUtils.isEmpty(contractRetreatInfoList)) {
            return true;
        }
        return false;
    }

    @Override
    public void checkRentAndSend(ContractRetreatInfo contractRetreatInfo) {
        // 1查询合同下所有租金信息  全部核销完毕 则发送待办
        LambdaQueryWrapper<CollectionBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        wrapper.eq(CollectionBaseInfo::getContractId, contractRetreatInfo.getContractId());
        wrapper.ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(wrapper);
        if (ObjectUtils.isEmpty(collectionBaseInfoList)) {
            send(contractRetreatInfo);
            contractRetreatInfo.setSendedFlag("1");
            contractRetreatInfoService.updateById(contractRetreatInfo);
        }
    }

    /*给出纳发送消息待办*/
    private void send(ContractRetreatInfo contractRetreatInfo) {
        /*根据角色查询所有用户*/
        Set<Long> userIdSet = sysUserService.getUserIdsByRole("CWGL_3");
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractRetreatInfo.getContractId());
        // 生成待办
        LocalDateTime now = LocalDateTime.now();
        CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                .processType(ProcessModelTypeEnum.MarginBackNotice.name()) // 保证金抵扣/退还
                .formName(contractRetreatInfo.getContractCode() + "保证金退款通知")
                .currentNode("保证金退款通知")
                .currentAssignee(JSON.toJSONString(userIdSet))
                .clientName(contractRetreatInfo.getClientName())
                .projName(contractBaseInfo.getProjName())
                .projCode(contractBaseInfo.getProjCode())
                .applyTime(now)
                .businessId(contractRetreatInfo.getId().toString()) // 合同退抵信息Id
                .businessData(contractRetreatInfo.getContractId().toString())
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();

        commonProcessPrepareService.save(commonProcessPrepare);
    }

    public void getHeadUserIds(OrgDO userDept, Map<String, Object> varMap) {
        /*业务部门负责人*/
        List<String> leaderIds = new LinkedList<>();
        /*分管领导*/
        List<String> businessHeaderIds = new LinkedList<>();

        // 部门负责人
        List<UserDO> businessHeaderList = sysUserService.listSpecificOrgJobUser(userDept.getId(), JobEnum.businesshead.name());
        if (CollectionUtil.isNotEmpty(businessHeaderList)) {
            for (UserDO userDO : businessHeaderList) {
                businessHeaderIds.add(String.valueOf(userDO.getId()));
            }
        }
        // 分管领导
        List<UserDO> leaderList = sysUserService.listSpecificOrgJobUser(userDept.getId(), JobEnum.leaderincharge.name());
        if (CollectionUtil.isNotEmpty(leaderList)) {
            for (UserDO userDO : leaderList) {
                leaderIds.add(String.valueOf(userDO.getId()));
            }
        }

        varMap.put("userTask_deptHand", businessHeaderIds.stream().distinct().collect(Collectors.toList()));
        varMap.put("userTask_leader", leaderIds.stream().distinct().collect(Collectors.toList()));
    }

    public void sendFileToSettle(Long contractId, String operation) {
        if (ContractOperationEnum.CHANGE_REPAYMENT_IN_ADVANCE.name().equals(operation)) {
            operation = ContractLibModelEnum.CHANGE.name(); // 合同变更 -提前还款
        }else if(ContractOperationEnum.SETTLE_NORMAL.name().equals(operation)){
            operation = ContractExtraFileTypeEnum.CONTRACT_SETTLE.name();
        }
        LambdaQueryWrapper<MaterialsList> fileWrapper = Wrappers.lambdaQuery();
        fileWrapper.in(MaterialsList::getBelongId, contractId);
        fileWrapper.eq(MaterialsList::getMaterialsType, operation);
        List<MaterialsList> fileLists = materialsListService.list(fileWrapper);
        if (ObjectUtils.isNotEmpty(fileLists)) { // 已有附件 代表已经初始化过
            return;
        }

        /*1、查询合同下所有的  审批通过的退抵信息*/
        LambdaQueryWrapper<ContractRetreatInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ContractRetreatInfo::getContractId, String.valueOf(contractId));
        wrapper.eq(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.APPROVAL_PASS.name());
        List<ContractRetreatInfo> retreatInfos = contractRetreatInfoService.list(wrapper);
        if (ObjectUtils.isEmpty(retreatInfos)) {
            return;
        }
        /*2、查询已有的退抵关联附件信息*/
        List<Long> ids = retreatInfos.stream().map(ContractRetreatInfo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<MaterialsList> materialsWrapper = Wrappers.lambdaQuery();
        materialsWrapper.in(MaterialsList::getBelongId, ids);
        materialsWrapper.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTARCT_DEPOSIT.name());
        List<MaterialsList> materialsLists = materialsListService.list(materialsWrapper);
        if (ObjectUtils.isEmpty(materialsLists)) {
            return;
        }

        for (MaterialsList materialsList : materialsLists) {
            materialsList.setId(null);
            materialsList.setMainId(null);
            materialsList.setBelongId(contractId);
            materialsList.setBusinessType(BusinessModuleEnum.CONTRACT.name());
            materialsList.setMaterialsType(operation); // 附件类型  正常结清
            materialsList.setIsEdit(-1);
            if (ContractLibModelEnum.CHANGE.name().equals(operation)) {
                materialsList.setMaterialSubType(ContractChangeTypeEnum.EARLY_REPAYMENT.name()); //
            }
        }
        materialsListService.saveBatch(materialsLists);
    }
}