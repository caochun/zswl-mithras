package cn.zswltech.mithras.service.service.leaseholdproperty;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.AppraisalCompanyWhitelistProcessStatusEnum;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.AppraisalCompanyWhitelistMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.TycAppraisalCompanyBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.leaseholdproperty.application.AppraisalCompanyWhitelistLibService;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.leaseholdproperty.application.impl.LeaseItemAppraisalRelationService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.third.tianyancha.application.TycService;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasBaseInfo;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/9/2
 * @description
 */
@Slf4j
@Service
public class AppraisalCompanyWhitelistService extends ServiceImpl<AppraisalCompanyWhitelistMapper, AppraisalCompanyWhitelist> {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AppraisalCompanyWhitelistLibService appraisalCompanyWhitelistLibService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private TycService tycService;
    @Resource
    private TycAppraisalCompanyBaseInfoMapper tycAppraisalCompanyBaseInfoMapper;
    @Resource
    private LeaseItemAppraisalRelationService leaseItemAppraisalRelationService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;

    public PageR<AppraisalCompanyWhitelistPageRSP> pageList(AppraisalCompanyWhitelistPageREQ req) {
        Page<AppraisalCompanyWhitelist> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<AppraisalCompanyWhitelist> conditionQuery = this.buildQuery(req);
        Page<AppraisalCompanyWhitelist> dbResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<AppraisalCompanyWhitelistPageRSP> list = BeanUtil.copyToList(dbResult.getRecords(), AppraisalCompanyWhitelistPageRSP.class);
        // 填充额外数据
        Set<Long> userIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        for (AppraisalCompanyWhitelistPageRSP rsp : list) {
            userIds.add(rsp.getLastOperatorId());
            userIds.add(rsp.getCreateBy());
            deptIds.add(rsp.getDeptId());
        }
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIds);
        Set<String> uscCodes = dbResult.getRecords().stream().map(AppraisalCompanyWhitelist::getUscCode).collect(Collectors.toSet());
        Map<String, Long> map = tycAppraisalCompanyBaseInfoMapper.selectList(Wrappers.<TycAppraisalCompanyBaseInfo>lambdaQuery().in(TycAppraisalCompanyBaseInfo::getCreditCode, uscCodes)).stream().collect(Collectors.toMap(TycAppraisalCompanyBaseInfo::getCreditCode, TycAppraisalCompanyBaseInfo::getId, (a, b) -> b));
        for (AppraisalCompanyWhitelistPageRSP rsp : list) {
            if (Objects.nonNull(rsp.getCreateBy())) {
                rsp.setCreateByName(userNameMap.get(rsp.getCreateBy()));
            }
            if (Objects.nonNull(rsp.getLastOperatorId())) {
                rsp.setLastOperatorName(userNameMap.get(rsp.getLastOperatorId()));
            }
            if (Objects.nonNull(rsp.getDeptId())) {
                rsp.setDeptName(deptNameMap.get(rsp.getDeptId()));
            }
            rsp.setCompanyId(map.get(rsp.getUscCode()));
        }
        return PageR.of(list, dbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long add(AppraisalCompanyWhitelistAddREQ req) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        OrgDO org = sysUserService.getBizDeptByUserId(currentUserId);
        if (Objects.isNull(org)) {
            throw new MithrasException("没有找到当前用户所在的业务部门");
        }
        // 调用天眼查查询
        MithrasBaseInfo tycBaseInfo = tycService.baseInfo(req.getUscCode());
        if (Objects.isNull(tycBaseInfo)) {
            throw new MithrasException("没有从天眼查获取到工商信息");
        }
        // 查询已有数据
        List<AppraisalCompanyWhitelist> existList = this.list(
                Wrappers.<AppraisalCompanyWhitelist>lambdaQuery().eq(AppraisalCompanyWhitelist::getUscCode, req.getUscCode().trim())
        );
        if (CollectionUtil.isNotEmpty(existList)) {
            for (AppraisalCompanyWhitelist record : existList) {
                if (StrUtil.equals(record.getRecordStatus(), RecordStatus.EXPIRE.name())) {
                    continue;
                }
                if (Objects.equals(record.getDeptId(), org.getId())) {
                    return record.getId();
                }
            }
        }
        // 判断是否有流程
        if (this.isRunningProcess(req.getUscCode().trim())) {
            throw new MithrasException("该评估机构已存在审批中的数据，请勿重复提交");
        }
        // 尝试往评估机构池放数据
        LeaseAppraisalAddREQ leaseAppraisalAddREQ = new LeaseAppraisalAddREQ();
        leaseAppraisalAddREQ.setCreditCode(req.getUscCode().trim());
        SpringUtil.getBean(LeaseAppraisalService.class).appraisalAdd(leaseAppraisalAddREQ);
        // 保存白名单数据
        AppraisalCompanyWhitelist appraisalCompanyWhitelist = new AppraisalCompanyWhitelist();
        appraisalCompanyWhitelist.setUscCode(req.getUscCode());
        this.refreshCommerceInfo(appraisalCompanyWhitelist, tycBaseInfo);
        appraisalCompanyWhitelist.setDeptId(org.getId());
        appraisalCompanyWhitelist.setLastOperatorId(currentUserId);
        appraisalCompanyWhitelist.setRecordStatus(RecordStatus.NEW.name());
        appraisalCompanyWhitelist.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.NEW_UN_SUBMIT.name());
        this.save(appraisalCompanyWhitelist);
        return appraisalCompanyWhitelist.getId();
    }

    public AppraisalCompanyDetailRSP detail(SinglePkREQ req) {
        AppraisalCompanyWhitelist dbModel;
        if (StrUtil.isBlank(req.getVersion())) {
            dbModel = this.getById(req.getId());
        } else {
            AppraisalCompanyWhitelistLib libModel = appraisalCompanyWhitelistLibService.getByIdAndVersion(req.getId(), req.getVersion());
            if (Objects.isNull(libModel)) {
                throw new MithrasException("版本数据不存在");
            }
            dbModel = BeanUtil.copyProperties(libModel, AppraisalCompanyWhitelist.class);
            dbModel.setId(libModel.getOriginId());
            dbModel.setCreateBy(libModel.getDataCreateBy());
            dbModel.setCreateTime(libModel.getDataCreateTime());
            dbModel.setUpdateBy(libModel.getDataUpdateBy());
            dbModel.setUpdateTime(libModel.getDataUpdateTime());
        }
        AppraisalCompanyDetailRSP rsp = BeanUtil.copyProperties(dbModel, AppraisalCompanyDetailRSP.class);
        // 填充评估机构池中的id
        TycAppraisalCompanyBaseInfo tycAppraisalCompanyBaseInfo = tycAppraisalCompanyBaseInfoMapper.selectOne(Wrappers.<TycAppraisalCompanyBaseInfo>lambdaQuery().eq(TycAppraisalCompanyBaseInfo::getCreditCode, dbModel.getUscCode()));
        rsp.setCompanyId(tycAppraisalCompanyBaseInfo.getId());
        // 查询关联项目信息
        List<LeaseItemAppraisalRelation> relations = leaseItemAppraisalRelationService.list(Wrappers.<LeaseItemAppraisalRelation>lambdaQuery().eq(LeaseItemAppraisalRelation::getCompanyId, tycAppraisalCompanyBaseInfo.getId()));
        if (CollectionUtil.isNotEmpty(relations)) {
            LambdaQueryWrapper<LeaseItemInfo> leaseItemInfoQuery = Wrappers.lambdaQuery();
            leaseItemInfoQuery.in(LeaseItemInfo::getId, relations.stream().map(LeaseItemAppraisalRelation::getLeaseItemId).collect(Collectors.toSet()));
            leaseItemInfoQuery.eq(LeaseItemInfo::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name());
            List<LeaseItemInfo> itemInfoList = leaseItemInfoService.list(leaseItemInfoQuery);
            if (CollectionUtil.isNotEmpty(itemInfoList)) {
                rsp.setRelatedProjectList(itemInfoList.stream().map(e -> new Pair<>(e.getProjReviewId(), e.getProjName())).collect(Collectors.toList()));
            }
        }
        return rsp;
    }

    public void deleteById(Long id) {
        AppraisalCompanyWhitelist record = this.getById(id);
        if (Objects.isNull(record)) {
            throw new MithrasException("主数据不存在");
        }
        OrgDO org = sysUserService.currentUserBizDept();
        if (Objects.isNull(org) || !Objects.equals(org.getId(), record.getDeptId())) {
            throw new MithrasException("仅创建部门用户可操作");
        }
        if (!StrUtil.equals(record.getRecordStatus(), RecordStatus.NEW.name()) || !StrUtil.equals(record.getProcessStatus(), AppraisalCompanyWhitelistProcessStatusEnum.NEW_UN_SUBMIT.name())) {
            throw new MithrasException("当前状态不允许删除");
        }
        this.removeById(id);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void refreshCommerce(SinglePkREQ req) {
        AppraisalCompanyWhitelist appraisalCompanyWhitelist = this.getById(req.getId());
        if (Objects.isNull(appraisalCompanyWhitelist)) {
            throw new MithrasException("主数据不存在");
        }
        if (StrUtil.isBlank(appraisalCompanyWhitelist.getUscCode())) {
            throw new MithrasException("统一社会信用代码为空");
        }
        if (StrUtil.equals(appraisalCompanyWhitelist.getRecordStatus(), RecordStatus.EXPIRE.name())) {
            throw new MithrasException("数据已失效");
        }
        // 调用天眼查查询
        MithrasBaseInfo mithrasBaseInfo = tycService.baseInfo(appraisalCompanyWhitelist.getUscCode());
        if (Objects.isNull(mithrasBaseInfo)) {
            throw new MithrasException("没有从天眼查获取到工商信息");
        }
        // 更新评估机构池数据
        LeaseAppraisalLastedREQ leaseAppraisalLastedREQ = new LeaseAppraisalLastedREQ();
        leaseAppraisalLastedREQ.setCreditCode(appraisalCompanyWhitelist.getUscCode());
        SpringUtil.getBean(LeaseAppraisalService.class).appraisalLasted(leaseAppraisalLastedREQ);
        // 更新本地数据
        this.refreshCommerceInfo(appraisalCompanyWhitelist, mithrasBaseInfo);
        if (StrUtil.equals(appraisalCompanyWhitelist.getRecordStatus(), RecordStatus.TAKE_EFFECT.name()) && !StrUtil.equals(appraisalCompanyWhitelist.getProcessStatus(), AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_UNDER_APPROVAL.name())) {
            // 审批状态变为变更未提交
            appraisalCompanyWhitelist.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_UN_SUBMIT.name());
        }
        this.updateById(appraisalCompanyWhitelist);
    }

    @Transactional(rollbackFor = Throwable.class)
    public String submit(SinglePkREQ req) {
        Long id = req.getId();
        AppraisalCompanyWhitelist appraisalCompanyWhitelist = this.getById(id);
        if (Objects.isNull(appraisalCompanyWhitelist)) {
            throw new MithrasException("主数据不存在");
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        // 取创建人所在部门及部门负责人
        OrgDO orgDO = sysUserService.currentUserBizDept();
        if (Objects.isNull(orgDO)) {
            throw new MithrasException("没有找到当前登陆人所在的业务部门");
        }
        if (!Objects.equals(appraisalCompanyWhitelist.getDeptId(), orgDO.getId())) {
            throw new MithrasException("仅创建部门的用户可操作");
        }
        // 判断是否已经有生效的评估机构
        AppraisalCompanyWhitelist existEffectOne = this.findEffectByUscCode(appraisalCompanyWhitelist.getUscCode());
        if (Objects.nonNull(existEffectOne) && !Objects.equals(existEffectOne.getId(), id)) {
            throw new MithrasException("已存在生效的评估机构");
        }
        // 判断是否有流程
        if (this.isRunningProcess(appraisalCompanyWhitelist)) {
            throw new MithrasException("该评估机构已存在审批中的数据，请勿重复提交");
        }
        Long deptMasterId = sysUserService.getUserIdByOrgJob(orgDO.getId(), JobEnum.businesshead.name());
        if (Objects.isNull(deptMasterId)) {
            throw new MithrasException("没有找到当前登陆人所在业务部门的部门负责人");
        }
        // 校验资料
        int count = SpringUtil.getBean(MaterialsListService.class).count(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST.name())
                .eq(MaterialsList::getBelongId, id)
        );
        if (count == 0) {
            throw new MithrasException("资料清单不能为空");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        if (StrUtil.equals(appraisalCompanyWhitelist.getRecordStatus(), RecordStatus.TAKE_EFFECT.name())) {
            if (!StrUtil.equals(appraisalCompanyWhitelist.getProcessStatus(), AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_UN_SUBMIT.name())) {
                throw new MithrasException("数据未发生变更，无需发起流程");
            }
            appraisalCompanyWhitelist.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_UNDER_APPROVAL.name());
            startProcessReq.setModelKey(ProcessModelTypeEnum.AppraisalCompanyWhitelistModifyFlow.name());
            startProcessReq.setProcessInstanceName(ProcessModelTypeEnum.AppraisalCompanyWhitelistModifyFlow.getDisplay() + "-" + appraisalCompanyWhitelist.getCompanyName());
        } else {
            appraisalCompanyWhitelist.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.NEW_UNDER_APPROVAL.name());
            startProcessReq.setModelKey(ProcessModelTypeEnum.AppraisalCompanyWhitelistCreateFlow.name());
            startProcessReq.setProcessInstanceName(ProcessModelTypeEnum.AppraisalCompanyWhitelistCreateFlow.getDisplay() + "-" + appraisalCompanyWhitelist.getCompanyName());
        }
        startProcessReq.setBusinessKey(id.toString());
        startProcessReq.setStartUserId(currentUserId.toString());
        startProcessReq.setStartUserDeptId(orgDO.getId().toString());
        startProcessReq.setVariables(MapUtil.of("bizDeptLeader", Collections.singletonList(deptMasterId.toString())));
        // 变更业务数据
        this.updateById(appraisalCompanyWhitelist);
        // 创建流程
        return flowProcessApiService.start(startProcessReq);
    }

    public void modify(AppraisalCompanyWhitelistModifyREQ req) {
        AppraisalCompanyWhitelist appraisalCompanyWhitelist = this.getById(req.getId());
        if (Objects.isNull(appraisalCompanyWhitelist)) {
            throw new MithrasException("主数据不存在");
        }
        // 判断是否有流程
        if (this.isRunningProcess(appraisalCompanyWhitelist)) {
            throw new MithrasException("该评估机构已存在审批中的数据，请勿重复提交");
        }
        AppraisalCompanyWhitelist update = BeanUtil.copyProperties(req, AppraisalCompanyWhitelist.class);
        update.setLastOperatorId(AccountUtil.getLoginInfo().getId());
        if (!StrUtil.equals(appraisalCompanyWhitelist.getProcessStatus(), AppraisalCompanyWhitelistProcessStatusEnum.OUT_UNDER_APPROVAL.name())) {
            update.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.OUT_UN_SUBMIT.name());
        }
        this.updateById(update);
    }

    @Transactional(rollbackFor = Throwable.class)
    public String submitOut(SinglePkREQ req) {
        AppraisalCompanyWhitelist appraisalCompanyWhitelist = this.getById(req.getId());
        if (Objects.isNull(appraisalCompanyWhitelist)) {
            throw new MithrasException("主数据不存在");
        }
        if (!StrUtil.equals(appraisalCompanyWhitelist.getRecordStatus(), RecordStatus.TAKE_EFFECT.name())) {
            throw new MithrasException("仅生效数据允许出库");
        }
        if (StrUtil.isBlank(appraisalCompanyWhitelist.getOutReason())) {
            throw new MithrasException("出库原因不能为空");
        }
        // 判断是否有流程
        if (this.isRunningProcess(appraisalCompanyWhitelist)) {
            throw new MithrasException("该评估机构已存在审批中的数据，请勿重复提交");
        }
        // 判断操作条件
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        OrgDO bizOrg = sysUserService.getBizDeptByUserId(currentUserId);
        boolean isLegal = sysUserService.userIsSpecificJob(currentUserId, JobEnum.legalmanager.name());
        boolean sameDept = Objects.nonNull(bizOrg) && Objects.equals(bizOrg.getId(), appraisalCompanyWhitelist.getDeptId());
        if (!isLegal && !sameDept) {
            throw new MithrasException("仅允许法务经理或者创建部门用户操作");
        }
        // 变更业务数据
        appraisalCompanyWhitelist.setLastOperatorId(currentUserId);
        appraisalCompanyWhitelist.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.OUT_UNDER_APPROVAL.name());
        this.updateById(appraisalCompanyWhitelist);
        // 创建流程实例
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.AppraisalCompanyWhitelistOutFlow.name());
        startProcessReq.setProcessInstanceName(ProcessModelTypeEnum.AppraisalCompanyWhitelistOutFlow.getDisplay() + "-" + appraisalCompanyWhitelist.getCompanyName());
        startProcessReq.setBusinessKey(req.getId().toString());
        startProcessReq.setStartUserId(currentUserId.toString());
        if (isLegal) {
            // 法务经理发起
            startProcessReq.setVariables(MapUtil.of("starUserIsLegalManager", true));
            List<OrgDO> orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.legalmanager.name());
            if (CollectionUtil.isNotEmpty(orgList)) {
                startProcessReq.setStartUserDeptId(orgList.get(0).getId().toString());
            }
        } else {
            startProcessReq.setVariables(MapUtil.of("starUserIsLegalManager", false));
            startProcessReq.setStartUserDeptId(bizOrg.getId().toString());
        }
        return flowProcessApiService.start(startProcessReq);
    }

    public AppraisalCompanyWhitelist findEffectByUscCode(String uscCode) {
        LambdaQueryWrapper<AppraisalCompanyWhitelist> query = Wrappers.lambdaQuery();
        query.eq(AppraisalCompanyWhitelist::getUscCode, uscCode);
        query.eq(AppraisalCompanyWhitelist::getRecordStatus, RecordStatus.TAKE_EFFECT.name());
        query.orderByDesc(AppraisalCompanyWhitelist::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<AppraisalCompanyWhitelist> listByUscCode(String uscCode) {
        LambdaQueryWrapper<AppraisalCompanyWhitelist> query = Wrappers.lambdaQuery();
        query.eq(AppraisalCompanyWhitelist::getUscCode, uscCode);
        return this.list(query);
    }

    private LambdaQueryWrapper<AppraisalCompanyWhitelist> buildQuery(AppraisalCompanyWhitelistPageREQ req) {
        LambdaQueryWrapper<AppraisalCompanyWhitelist> conditionQuery = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(req.getCompanyName())) {
            conditionQuery.like(AppraisalCompanyWhitelist::getCompanyName, req.getCompanyName());
        }
        if (StrUtil.isNotBlank(req.getRecordStatus())) {
            conditionQuery.eq(AppraisalCompanyWhitelist::getRecordStatus, req.getRecordStatus());
        }
        if (Objects.nonNull(req.getRecordExpireDateFrom())) {
            conditionQuery.ge(AppraisalCompanyWhitelist::getRecordExpireDate, req.getRecordExpireDateFrom());
        }
        if (Objects.nonNull(req.getRecordExpireDateTo())) {
            conditionQuery.le(AppraisalCompanyWhitelist::getRecordExpireDate, req.getRecordExpireDateTo());
        }
        if (Objects.nonNull(req.getDeptId())) {
            conditionQuery.eq(AppraisalCompanyWhitelist::getDeptId, req.getDeptId());
        }
        if (Objects.nonNull(req.getCreateBy())) {
            conditionQuery.eq(BaseModel::getCreateBy, req.getCreateBy());
        }
        if (Objects.nonNull(req.getProcessStatus())) {
            conditionQuery.eq(AppraisalCompanyWhitelist::getProcessStatus, req.getProcessStatus());
        }
        conditionQuery.orderByDesc(BaseModel::getCreateTime);
        return conditionQuery;
    }

    private void refreshCommerceInfo(AppraisalCompanyWhitelist appraisalCompanyWhitelist, MithrasBaseInfo tycCommerceInfo) {
        appraisalCompanyWhitelist.setCompanyName(tycCommerceInfo.getTycName());
        appraisalCompanyWhitelist.setEstablishDate(tycCommerceInfo.getEstablishDate());
        if (Objects.nonNull(tycCommerceInfo.getBizLicenceLongTerm())) {
            appraisalCompanyWhitelist.setLicenseIsLongTerm(tycCommerceInfo.getBizLicenceLongTerm() ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
        }
        appraisalCompanyWhitelist.setBusinessScope(tycCommerceInfo.getBizScope());
        appraisalCompanyWhitelist.setLicenseExpireDate(tycCommerceInfo.getBizLicenseEndDate());
    }

    private boolean isRunningProcess(AppraisalCompanyWhitelist current) {
        return this.isRunningProcess(current.getUscCode());
    }

    private boolean isRunningProcess(String uscCode) {
        List<AppraisalCompanyWhitelist> list = this.listByUscCode(uscCode);
        if (CollectionUtil.isEmpty(list)) {
            return false;
        }
        // 去掉失效的
        list.removeIf(e -> StrUtil.equals(e.getRecordStatus(), RecordStatus.EXPIRE.name()));
        for (AppraisalCompanyWhitelist record : list) {
            // 判断是否有流程
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setModelKeyList(BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST.getModelKeyList());
            processPageReq.setBusinessKey(record.getId().toString());
            processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
            Long flowCount = flowTaskApiService.queryProcessCount(processPageReq);
            if (Objects.nonNull(flowCount) && flowCount > 0) {
                return true;
            }
        }
        return false;
    }
}
