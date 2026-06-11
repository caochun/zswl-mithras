package cn.zswltech.mithras.application.orchestration.facade.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckClientApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.afterlease.application.auth.AfterLeaseCheckPlanModifyMainChecker;
import cn.zswltech.mithras.afterlease.application.auth.AfterLeaseCheckPlanModifySubChecker;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.afterlease.application.convert.AfterLeaseCheckPlanProjectConvert;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/10
 * @description
 */
@Slf4j
@Service
public class AfterLeaseCheckClientFacade implements AfterLeaseCheckClientApplicationService {
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private ClientService clientService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private OrgService orgService;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public R<AfterLeaseCheckClientInfoRSP> getInfoById(@Valid SinglePkREQ req) {
        return R.ok(afterLeaseCheckPlanClientService.getInfoById(req.getId(), req.getVersion()));
    }

    @Override
    public R<List<AfterLeaseCheckClientSelectRSP>> queryClient(@Valid AfterLeaseCheckClientSelectREQ req) {
        return R.ok(afterLeaseCheckPlanClientService.queryClient(req));
    }

    @Override
    public R<Void> submitApproval(@Valid SinglePkREQ req) {
        afterLeaseCheckPlanClientService.submitApproval(req.getId());
        return R.ok();
    }

//    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonModifyMainAuthCheckerNew.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_REPORT")
    @Override
    public R<AfterLeaseCheckReportVersionRSP> changeReportType(@Valid AfterLeaseCheckReportTypeChangeREQ req) {
        return R.ok(afterLeaseCheckPlanClientService.changeReportType(req.getId(), req.getReportType()));
    }

    @Override
    public R<List<AfterLeaseCheckClientDeptInfoRSP>> listAfterLeaseCanCheckGroupByDept(SinglePkREQ req) {
        return R.ok(afterLeaseCheckPlanClientService.listAfterLeaseCanCheckGroupByDept(req.getId()));
    }

    @Override
    public R<List<AfterLeaseCheckClientListRSP>> list(@Valid AfterLeaseCheckClientListREQ req) {
        return R.ok(afterLeaseCheckPlanClientService.listBy(req));
    }

    @DataAuthCheck(keyFieldName = "planId", checkerClass = AfterLeaseCheckPlanModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_PLAN")
    @Override
    public R<Void> save(@Valid AfterLeaseCheckClientSaveREQ req) {
        afterLeaseCheckPlanClientService.save(req);
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = AfterLeaseCheckPlanModifySubChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, mapperClass = NewAfterLeaseCheckPlanClientMapper.class, businessModule = "NEW_AFTER_LEASE_CHECK_PLAN")
    @Override
    public R<Void> remove(@Valid SinglePkREQ singlePkREQ) {
        afterLeaseCheckPlanClientService.removeProjectById(singlePkREQ.getId());
        return R.ok();
    }

    @Override
    public R<List<AfterLeaseCheckClientListRSP>> listNotQuarterPlanProject(@Valid SinglePkREQ req) {
        return R.ok(afterLeaseCheckPlanClientService.listByLoginUser(req.getId(), req.getVersion()));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public R<List<AfterLeaseCheckClientListGroupRSP>> listQuarterPlanProject(@Valid SinglePkREQ req) {
        // 季度计划需要预处理项目
        this.preHandleClient(req.getId());
        // 找到所有业务部门
        Example example = new Example(OrgDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", OrgConstants.BUSINESS_DEPT);
        List<OrgDO> orgList = orgService.selectByExample(example);
        if (CollectionUtil.isEmpty(orgList)) {
            return R.ok(Collections.emptyList());
        }
        // 找到当前用户可看到的检查项目列表
        List<AfterLeaseCheckClientListRSP> checkClientListRSPList = afterLeaseCheckPlanClientService.listByLoginUser(req.getId(), req.getVersion());
        // 如果没有业务部门，填充一个-1
        Map<Long, List<AfterLeaseCheckClientListRSP>> checkClientRSPMap = checkClientListRSPList.stream().peek(e -> {
            if (Objects.isNull(e.getBizDeptId())) {
                e.setBizDeptId(-1L);
                e.setBizDeptName("其他");
            }
        }).collect(Collectors.groupingBy(AfterLeaseCheckClientListRSP::getBizDeptId));
        // 根据用户是否业务部门来决定是否要补全所有部门数据
        List<AfterLeaseCheckClientListGroupRSP> result = new LinkedList<>();
        // 是否业务部门用户 如果有任何非业务部门，都不是业务部门 取高
        boolean bizDeptFlag = sysUserService.getUserDeptList().stream().allMatch(o -> Objects.equals(OrgConstants.BUSINESS_DEPT, o.getType()));
        if (bizDeptFlag) {
            for (Map.Entry<Long, List<AfterLeaseCheckClientListRSP>> entry : checkClientRSPMap.entrySet()) {
                result.add(AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckProjectListGroupRSP(entry.getValue()));
            }
        } else {
            for (OrgDO orgDO : orgList) {
                List<AfterLeaseCheckClientListRSP> list = checkClientRSPMap.get(orgDO.getId());
                if (CollectionUtil.isEmpty(list)) {
                    AfterLeaseCheckClientListGroupRSP rsp = new AfterLeaseCheckClientListGroupRSP();
                    rsp.setBizDeptId(orgDO.getId());
                    rsp.setBizDeptName(orgDO.getName());
                    rsp.setToCheckCount(0);
                    rsp.setFinishCount(0);
                    rsp.setClientList(Collections.emptyList());
                    result.add(rsp);
                } else {
                    result.add(AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckProjectListGroupRSP(list));
                }
            }
            // 补公海客户
            List<AfterLeaseCheckClientListRSP> openSeaClientList = checkClientRSPMap.get(-1L);
            if (CollectionUtil.isNotEmpty(openSeaClientList)) {
                result.add(AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckProjectListGroupRSP(openSeaClientList));
            }
        }
        return R.ok(result);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public R<Void> modifyPlanClient(AfterLeaseCheckClientModifyReq req) {
        NewAfterLeaseCheckPlanClient clientPlan = afterLeaseCheckPlanClientService.getById(req.getId());
        if (Objects.equals(req.getScene(), AfterLeaseCheckClientModifyReq.SCENE_RISK_MANAGER)) {
            clientPlan.setCheckTime(req.getCheckTime());
            clientPlan.setIsNotify(false);
        }
        if (Objects.equals(req.getScene(), AfterLeaseCheckClientModifyReq.SCENE_ASSET_MANAGER)) {
            // 判断检查方式
            if (Objects.equals(req.getCheckWay(), AfterLeaseCheckWayEnum.SITE.name())) {
                Assert.notNull(req.getRiskManagerId(), () -> MithrasException.newException("协查风控经理不能为空"));
            } else {
                req.setRiskManagerId(null);
            }
            clientPlan.setCheckWay(req.getCheckWay());
            if (Objects.nonNull(req.getRiskManagerId())) {
                // 选择的风控经理不是当前的风控经理
                if (!req.getRiskManagerId().equals(clientPlan.getRiskManagerId())) {
                    Map<Long, String> userId2Name = id2NameService.sysUserId2Name(Collections.singletonList(req.getRiskManagerId()));
                    clientPlan.setRiskManagerId(req.getRiskManagerId());
                    clientPlan.setRiskManagerName(userId2Name.get(req.getRiskManagerId()));
                    clientPlan.setCheckTime(null);
                    clientPlan.setIsNotify(false);
                }
            } else {
                clientPlan.setCheckTime(null);
                clientPlan.setRiskManagerId(null);
                clientPlan.setRiskManagerName(null);
                clientPlan.setIsNotify(false);
            }
        }
        afterLeaseCheckPlanClientService.updateAnnotationIncludeNullById(clientPlan);
        return R.ok();
    }

    @Override
    public R<List<SelectRSP>> listRiskManager() {
        List<UserDO> userList = new LinkedList<>();
        List<UserDO> userList1 = sysUserService.getUserByDeptCode("FXGLB_YWPS");
        if (CollectionUtil.isNotEmpty(userList1)) {
            userList.addAll(userList1);
        }
        List<UserDO> userList2 = sysUserService.getUserByDeptCode("FLHGB_ZCBQ");
        if (CollectionUtil.isNotEmpty(userList2)) {
            userList.addAll(userList2);
        }
        if (CollectionUtil.isEmpty(userList)) {
            return R.ok(Collections.emptyList());
        }
        Map<Long, UserDO> userMap = new HashMap<>(userList.size() * 2);
        for (UserDO userDO : userList) {
            userMap.putIfAbsent(userDO.getId(), userDO);
        }
        // 剔掉法务经理
        Example example = new Example(UserOrgJobDO.class);
        example.createCriteria().andIn("userId", userList.stream().map(UserDO::getId).collect(Collectors.toSet()));
        List<UserOrgJobDO> userOrgJobList = userOrgJobDOMapper.selectByExample(example);
        Map<Long, Set<String>> userJobMap = new HashMap<>();
        for (UserOrgJobDO userOrgJobDO : userOrgJobList) {
            userJobMap.putIfAbsent(userOrgJobDO.getUserId(), new HashSet<>());
            userJobMap.get(userOrgJobDO.getUserId()).add(userOrgJobDO.getJobCode());
        }
        List<SelectRSP> result = new LinkedList<>();
        for (Map.Entry<Long, UserDO> entry : userMap.entrySet()) {
            UserDO user = entry.getValue();
            Set<String> list = userJobMap.get(user.getId());
            if (CollectionUtil.isNotEmpty(list) && list.contains(JobEnum.legalmanager.name())) {
                continue;
            }
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(String.format("%s(%s)", user.getUserName(), user.getAccount()));
            rsp.setValue(user.getId().toString());
            result.add(rsp);
        }
        return R.ok(result);
    }

    @Override
    public R<PageR<AfterLeaseCheckLedgerListRSP>> queryCheckPlanLedgerList(AfterLeaseCheckLedgerListREQ req) {
        return R.ok(afterLeaseCheckPlanClientService.queryLedgerList(req));
    }

    private void preHandleClient(Long planId) {
        NewAfterLeaseCheckPlanBase plan = afterLeaseCheckPlanBaseService.getById(planId);
        if (Objects.isNull(plan)) {
            throw new MithrasException("检查计划不存在");
        }
        if (!Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.NEW.name())) {
            return;
        }
        List<NewAfterLeaseCheckPlanClient> exist = afterLeaseCheckPlanClientService.listBy(planId);
        Set<Long> existClientId = exist.stream().map(NewAfterLeaseCheckPlanClient::getClientId).collect(Collectors.toSet());
//        // 找到所有起租状态的合同
//        LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
//        contractQuery.eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name());
//        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractQuery);
        // 在租合同
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listInRentContract(null);
        Set<Long> candidateClientId = contractBaseInfoList.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toSet());
        // 如果已经添加过则移除
        candidateClientId.removeIf(existClientId::contains);
        if (CollectionUtil.isNotEmpty(candidateClientId)) {
            // 如果有不存在的则加到计划中
            List<Client> clientList = clientService.listByIds(candidateClientId);
            List<NewAfterLeaseCheckPlanClient> toInsertList = clientList.stream()
//                    .filter(item -> Objects.nonNull(item.getBelongDeptId()) && Objects.nonNull(item.getBelongSponsorId()))
                    .map(item -> AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckPlanClient(planId, item))
                    .collect(Collectors.toList());
            afterLeaseCheckPlanClientService.saveBatch(toInsertList);
        }
    }
}
