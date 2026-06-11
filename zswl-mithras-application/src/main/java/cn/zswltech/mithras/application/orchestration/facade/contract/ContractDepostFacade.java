package cn.zswltech.mithras.application.orchestration.facade.contract;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.contract.ContractDepostApplicationService;
import cn.zswltech.mithras.dto.contract.depost.*;
import cn.zswltech.mithras.dto.file.FileDownLoadREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.collection.convert.contract.ContractDeductRentInfoConverter;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.application.orchestration.document.file.impl.ContarctDepositProviderCheck;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractDeductRentInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRetreatInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractDeductRentInfoService;
import cn.zswltech.mithras.contract.core.ContractRetreatInfoService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.FileService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import org.springframework.stereotype.Service;

/**
 * 合同管理-首页，基本信息表
 *
 * @author vico
 * @date 2022-08-12
 */
@Service
@Slf4j
public class ContractDepostFacade implements ContractDepostApplicationService {

    @Resource
    private ContractRetreatInfoService contractRetreatInfoService;
    @Resource
    private ContractDeductRentInfoService contractDeductRentInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private ContractDeductRentInfoConverter contractDeductRentInfoConverter;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private FileTemplateService fileTemplateService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FileService fileService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private ClientService clientService;
    @Resource
    private BizProcessDataService bizProcessDataService;

    @Override
    public R check(ContractDepostREQ req) {
        AccountVO accountVO = AccountUtil.getLoginInfo();
        LambdaQueryWrapper<MarginBaseInfo> query = Wrappers.lambdaQuery();
        query.gt(MarginBaseInfo::getCollectionAmount, 0); // 保证金余额 >0
        query.eq(MarginBaseInfo::getContractId, req.getContractId());
        List<MarginBaseInfo> marginBaseInfoList = marginBaseInfoService.list(query);
        String contractId = null;
        if (ObjectUtils.isEmpty(marginBaseInfoList)) {
            throw new MithrasException("该合同保证金余额不足/存在在途保证金退抵/合同提前结清/合同结清/合同变更流程，请等待结束后操作！");
        }

        boolean hasCollectionAmount = false;  // 有保证金余额
        for (MarginBaseInfo marginBaseInfo : marginBaseInfoList) {
//            Long amount = LongUtil.null2zero(marginBaseInfo.getBackAmount()) + LongUtil.null2zero(marginBaseInfo.getDeductAmount()); // 退抵金额
            Long collectionAmount = LongUtil.null2zero(marginBaseInfo.getCollectionAmount()); //保证金余额
            if (collectionAmount > 0) {
                hasCollectionAmount = true;
                contractId = marginBaseInfo.getContractId().toString();
                break;
            }
        }
        if (!hasCollectionAmount) {
            throw new MithrasException("该合同保证金余额不足/存在在途保证金退抵/合同提前结清/合同结清/合同变更流程，请等待结束后操作！");
        }


        /*找到所有的(正常结清、提前结清、合同变更)流程中的合同信息  并从合同清单中摒弃掉*/
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(contractId);
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ContractEarlySettleFlow.name(),
                ProcessModelTypeEnum.ContractNormalSettleFlow.name(),
                ProcessModelTypeEnum.ContractLPRChangeFlow.name(),
                ProcessModelTypeEnum.ContractExtensionFlow.name(),
                ProcessModelTypeEnum.ContractModifyFlow.name(),
                ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name()
        ));
        List<ProcessResp> processPage = flowTaskApiService.queryProcess(processPageReq).getContents();
        if (ObjectUtils.isNotEmpty(processPage)) {
            throw new MithrasException("该合同保证金余额不足/存在在途保证金退抵/合同提前结清/合同结清/合同变更流程，请等待结束后操作！");
        }

        /*找到所有的保证金退抵流程， 在流程中或者待提交状态的数据*/
        LambdaQueryWrapper<ContractRetreatInfo> retreaWrapper = Wrappers.lambdaQuery();
        retreaWrapper.eq(ContractRetreatInfo::getContractId, req.getContractId());
        retreaWrapper.in(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.UN_SUBMIT.name(), FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
        List<ContractRetreatInfo> contractRetreatInfoList = contractRetreatInfoService.list(retreaWrapper);

        /*审批中和草稿都没有*/
        if (ObjectUtils.isEmpty(contractRetreatInfoList)) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
            contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.RETREAT_UNCOMIIT.name());
            contractBaseInfoService.updateById(contractBaseInfo);

            /*合同管理页面   保证金退抵校验通过自动生成*/
            ContractRetreatInfo contractRetreatInfo = new ContractRetreatInfo();
            contractRetreatInfo.setContractId(req.getContractId().toString());
            contractRetreatInfo.setProcessStatus(FilingMaterialsProcessStatusEnum.UN_SUBMIT.name());
            contractRetreatInfoService.save(contractRetreatInfo);

            LambdaQueryWrapper<ContractRetreatInfo> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ContractRetreatInfo::getContractId, req.getContractId());
            wrapper.eq(ContractRetreatInfo::getProcessStatus, FilingMaterialsProcessStatusEnum.UN_SUBMIT.name());
            ContractRetreatInfo info = contractRetreatInfoService.getOne(retreaWrapper);
            return R.ok(info.getId());
        }

        /*审批中和草稿逻辑上只有一种  如果是草稿直接返回id，  审批中直接校验不通过 */
        ContractRetreatInfo info = contractRetreatInfoList.get(0);
        if (FilingMaterialsProcessStatusEnum.UN_SUBMIT.name().equals(info.getProcessStatus())) {
            return R.ok(info.getId());
        } else {
            throw new MithrasException("该合同保证金余额不足/存在在途保证金退抵/合同提前结清/合同结清/合同变更流程，请等待结束后操作！");
        }

    }

    @Override
    public R depostInfo(Long id) {
        ContractRetreatInfo retreatInfo = contractRetreatInfoService.getById(id);

        /*保证金余额实时取自合同保证金*/
        LambdaQueryWrapper<MarginBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MarginBaseInfo::getContractId, retreatInfo.getContractId());
        wrapper.last(StringUtil.mysqlLimitOne());
        MarginBaseInfo marginBaseInfo = marginBaseInfoService.getOne(wrapper);
//        Long amount = LongUtil.null2zero(marginBaseInfo.getBackAmount()) + LongUtil.null2zero(marginBaseInfo.getDeductAmount()); // 退抵金额
        Long collectionAmount = LongUtil.null2zero(marginBaseInfo.getCollectionAmount()); //保证金余额

        retreatInfo.setCollectionAmount(collectionAmount);
        retreatInfo.setContractCode(marginBaseInfo.getContractCode());

        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(marginBaseInfo.getContractId());
        Client client = clientService.getById(contractBaseInfo.getClientId());
        retreatInfo.setClientName(client.getClientName());
        return R.ok(retreatInfo);
    }

    @Override
    public R rentList(ContractRetreatREQ req) {
        LambdaQueryWrapper<ContractDeductRentInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ContractDeductRentInfo::getRetreatInfoId, req.getRetreatInfoId());
        List<ContractDeductRentInfo> contractDeductRentInfoList = contractDeductRentInfoService.list(wrapper);
        return R.ok(contractDeductRentInfoList);
    }

    @Override
    public R rentAdd(ContractDeductRentInfoREQ req) {
        ContractDeductRentInfo contractDeductRentInfo = contractDeductRentInfoConverter.reviewToContractDeductRentInfo(req);
        contractDeductRentInfoService.save(contractDeductRentInfo);

        contractDeductRentInfoService.lockByCode(req.getCode());
        return R.ok("操作成功");
    }

//    @Override
//    public R rentUpdate(ContractDeductRentInfoREQ req) {
//        ContractDeductRentInfo contractDeductRentInfo = contractDeductRentInfoConverter.reviewToContractDeductRentInfo(req);
//        contractDeductRentInfoService.saveOrUpdate(contractDeductRentInfo);
//        return R.ok("操作成功");
//    }

    @Override
    public R rentDel(ContractDeductRentREQ req) {
        /*需要找到对应的付款计划并解锁*/
        ContractDeductRentInfo contractDeductRentInfo = contractDeductRentInfoService.getById(req.getId());
        contractDeductRentInfoService.unlockByCode(contractDeductRentInfo.getCode());
        contractDeductRentInfoService.removeById(req.getId());
        return R.ok();
    }

    @Override
    public R getByContractId(ContractDepostREQ req) {
        LambdaQueryWrapper<CollectionBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CollectionBaseInfo::getContractId, req.getContractId());
        wrapper.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()); //租金
        wrapper.ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()); //排除已核销
        wrapper.and(w -> w.eq(CollectionBaseInfo::getRetreatLock, "0") // 未锁定
                .or()
                .isNull(CollectionBaseInfo::getRetreatLock));
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(wrapper);
        Map<Long, String> codes = new HashMap<>();
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            codes.put(collectionBaseInfo.getId(), collectionBaseInfo.getCode());
        }
        return R.ok(codes);
    }

    @Override
    public R getByCollectionId(String collectionId) {
        LambdaQueryWrapper<CollectionBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CollectionBaseInfo::getId, collectionId);
        wrapper.and(w -> w.eq(CollectionBaseInfo::getRetreatLock, "0") // 未锁定
                .or()
                .isNull(CollectionBaseInfo::getRetreatLock));
        wrapper.last(StringUtil.mysqlLimitOne());
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(wrapper);

        if (ObjectUtil.isNull(collectionBaseInfo)) {
            throw new MithrasException("对应现金流编号收款信息不存在");
        }
        ContractDeductRentInfoRSP rsp = contractDeductRentInfoConverter.reviewToContractContractDeductRentInfoRSP(collectionBaseInfo);
        rsp.setRestAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()));
        return R.ok(rsp);
    }


    @Override
    public R downloadtemplete() {
        /*查询模板对应ID*/
        LambdaQueryWrapper<FileTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FileTemplate::getTemplateType, "保证金冲抵（退还）申请模版");
        wrapper.last(StringUtil.mysqlLimitOne());
        FileTemplate fileTemplate = fileTemplateService.getOne(wrapper);
        if (ObjectUtils.isEmpty(fileTemplate)) {
            throw new MithrasException("未找到保证金退抵申请模版");
        }

        /*查询模板附件对应ID*/
        LambdaQueryWrapper<MaterialsList> materialsWrapper = Wrappers.lambdaQuery();
        materialsWrapper.eq(MaterialsList::getBelongId, fileTemplate.getId());
        materialsWrapper.last(StringUtil.mysqlLimitOne());
        materialsWrapper.orderByDesc(MaterialsList::getCreateTime);
        MaterialsList materialsList = materialsListService.getOne(materialsWrapper);
        if (ObjectUtils.isEmpty(materialsList)) {
            throw new MithrasException("未找到保证金退抵申请模版");
        }

        /*下载模板文件*/
        FileDownLoadREQ req = new FileDownLoadREQ();
        req.setMainId(fileTemplate.getId());
        req.setFileId(materialsList.getId());
        req.setModuleType("FILE_TEMPLATE");

        return R.ok(fileService.download(req));
    }

    @Override
    public R depostSave(@Valid ContractRetreatSubmitREQ req) {
        if (ObjectUtils.isEmpty(req.getId())) {
            throw new RuntimeException("提交失败，请检查退抵信息");
        }
        if (LongUtil.null2zero(req.getCollectionAmount()) < LongUtil.null2zero(req.getDeductionAmount()) + LongUtil.null2zero(req.getReturnedAmount())) {
            throw new MithrasException("超额退抵，请检查！");
        }
        if (LongUtil.null2zero(req.getCollectionAmount()) <= 0) {
            throw new MithrasException("该合同保证金余额不足/存在在途保证金退抵/合同提前结清/合同结清/合同变更流程，请等待结束后操作！");
        }

        ContractRetreatInfo contractRetreatInfo = contractDeductRentInfoConverter.reviewToContractContractRetreatInfo(req);
        contractRetreatInfoService.updateById(contractRetreatInfo);
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "id", paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "CONTARCT_DEPOSIT", checkerClass = ContarctDepositProviderCheck.class)
    public R submit(@Valid ContractRetreatSubmitREQ req) {
        if (ObjectUtils.isEmpty(req.getId())) {
            throw new RuntimeException("提交失败，请检查退抵信息");
        }
        if (req.getCollectionAmount() < req.getDeductionAmount() + req.getReturnedAmount()) {
            throw new MithrasException("超额退抵，请检查！");
        }
        if (req.getCollectionAmount() <= 0) {
            throw new MithrasException("该合同保证金余额不足/存在在途保证金退抵/合同提前结清/合同结清流程，请等待结束后操作！");
        }
        if (req.getDeductionAmount() > 0) {
            LambdaQueryWrapper<ContractDeductRentInfo> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ContractDeductRentInfo::getRetreatInfoId, req.getId());
            List<ContractDeductRentInfo> contractDeductRentInfoList = contractDeductRentInfoService.list(wrapper);
            if (ObjectUtil.isEmpty(contractDeductRentInfoList)) {
                throw new MithrasException("抵扣租金与内扣金额不匹配，请检查！");
            }
        }

        ContractRetreatInfo contractRetreatInfo = contractRetreatInfoService.getById(req.getId());
        if (ProcessStatus.UNDER_APPROVAL.name().equals(contractRetreatInfo.getProcessStatus())) {
            throw new MithrasException("退抵租金流程已在审批中！");
        }

        LambdaQueryWrapper<MaterialsList> materialsWrapper = Wrappers.lambdaQuery();
        materialsWrapper.in(MaterialsList::getBelongId, req.getId());
        materialsWrapper.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTARCT_DEPOSIT.name());
        List<MaterialsList> materialsLists = materialsListService.list(materialsWrapper);
        if(ObjectUtils.isEmpty(materialsLists)){
            throw new MithrasException("请上传保证金退抵文件！");
        }

        AccountVO accountVO = AccountUtil.getLoginInfo();
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.MarginFlowManually.name()); // 流程类型

        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        startProcessReq.setProcessInstanceName(contractBaseInfo.getContractCode()); // 流程名称
        Map<String, Object> varMap = new HashMap<>();
        OrgDO userDept = sysUserService.getUserDept();
        if (Objects.isNull(userDept)) {
            throw new MithrasException("当前用户部门为空");
        }

        contractDeductRentInfoService.getHeadUserIds(userDept, varMap);
//        varMap.put("userTask_deptHand", new LinkedList<>());    //业务部门负责人
//        varMap.put("userTask_leader", new LinkedList<>());      //分管领导
        varMap.put("userTask_financeManagerUser", new LinkedList<>());      //财务经理（会计）
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(req.getId()));  // 绑定业务主键
        startProcessReq.setStartUserId(String.valueOf(accountVO.getId())); // 流程创建人
        startProcessReq.setStartUserDeptId(String.valueOf(userDept.getId())); // 流程创建人部门
        String processInstanceId = processApiService.start(startProcessReq);// 启动流程
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());

        contractRetreatInfo.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        contractRetreatInfo.setContractCode(contractBaseInfo.getContractCode());
        contractRetreatInfoService.updateById(contractRetreatInfo);

        contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.RETREAT_COMMIT.name());
        contractBaseInfoService.updateById(contractBaseInfo);

        return R.ok();
    }

    public R noticeCommit(String prepareId) {
        AccountVO accountVO = AccountUtil.getLoginInfo();
        CommonProcessPrepare commonProcessPrepare = commonProcessPrepareService.getById(Long.valueOf(prepareId));
        String currentAssignee = commonProcessPrepare.getCurrentAssignee();
        if (ObjectUtil.isNotEmpty(currentAssignee)) {
            Set<Long> userIdSet = JSON.parseObject(currentAssignee, new TypeReference<Set<Long>>() {
            });
            userIdSet.remove(accountVO.getId());

            if (ObjectUtil.isEmpty(userIdSet)) {  // 待办中没有人了
                commonProcessPrepareService.removeById(prepareId);
            } else {
                commonProcessPrepare.setCurrentAssignee(JSON.toJSONString(userIdSet));
                commonProcessPrepareService.updateById(commonProcessPrepare);
            }
        }
        return R.ok();
    }

}
