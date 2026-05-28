package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.AppraisalCompanyDetailRSP;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.AppraisalCompanyWhitelistMaterialEnum;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.lease.AppraisalCompanyWhitelistProcessStatusEnum;
import cn.zswltech.mithras.service.enums.lease.LeaseAppraisalFileTypeEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.leaseholdproperty.AppraisalCompanyWhitelistService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/9/5
 * @description
 */
@Slf4j
@Component
public class AppraisalCompanyWhitelistFileHandler extends FileModuleCheck {
    @Resource
    private AppraisalCompanyWhitelistService appraisalCompanyWhitelistService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;

    @Override
    public void checkList(String moduleKey, Long mainId) {
        // 不校验
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
        // 不校验
    }

    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materialsList = materialsListService.getById(fileId);
        OrgDO currentUserOrg = sysUserService.currentUserBizDept();
        if (StrUtil.equals(materialsList.getMaterialsType(), AppraisalCompanyWhitelistMaterialEnum.NORMAL.name())) {
            // 仅同部门项目经理可删除
            OrgDO org = sysUserService.getBizDeptByUserId(materialsList.getCreateBy());
            if (Objects.isNull(org) || Objects.isNull(currentUserOrg) || !Objects.equals(org.getId(), currentUserOrg.getId())) {
                throw new MithrasException("仅创建部门用户允许删除");
            }
        }
        if (StrUtil.equals(materialsList.getMaterialsType(), AppraisalCompanyWhitelistMaterialEnum.EXTRA_OUT.name())) {
            // 仅同部门项目经理及法务可删除
            boolean isLegal = sysUserService.currentUserIsSpecificJob(JobEnum.legalmanager.name());
            AppraisalCompanyWhitelist record = appraisalCompanyWhitelistService.getById(materialsList.getBelongId());
            if (Objects.isNull(record)) {
                throw new MithrasException("业务数据不存在");
            }
            boolean sameDept = Objects.nonNull(currentUserOrg) && Objects.equals(currentUserOrg.getId(), record.getDeptId());
            if (!isLegal && !sameDept) {
                throw new MithrasException("仅法务经理/创建部门用户允许删除");
            }
        }
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        if (CollectionUtil.isEmpty(fileIds)) {
            return;
        }
        for (Long fileId : fileIds) {
            this.checkRemove(moduleKey, fileId);
        }
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType){
        AppraisalCompanyWhitelist record = appraisalCompanyWhitelistService.getById(mainId);
        if (StrUtil.equals(record.getRecordStatus(), RecordStatus.EXPIRE.name())) {
            throw new MithrasException("评估机构白名单已失效，不允许上传");
        }
        dataAuthProcessRule.check(BusinessModuleEnum.of(moduleKey), mainId);
        OrgDO org = sysUserService.currentUserBizDept();
        boolean sameDept = Objects.nonNull(org) && Objects.equals(record.getDeptId(), org.getId());
        if (StrUtil.equals(materialsType, AppraisalCompanyWhitelistMaterialEnum.EXTRA_OUT.name())) {
            boolean isLegal = sysUserService.currentUserIsSpecificJob(JobEnum.legalmanager.name());
            if (!isLegal && !sameDept) {
                throw new MithrasException("仅允许法务经理或创建部门用户上传");
            } else {
                return;
            }
        }
        if (!sameDept) {
            throw new MithrasException("仅允许创建部门用户上传");
        }
    }

    @Override
    public void afterUploadHandle(String moduleType, Long mainId, Long fileId, String sourceBusinessKey, Long userId){
        MaterialsList materialsList = materialsListService.getById(fileId);
        this.infoChange(mainId, materialsList.getMaterialsType());
        // 拷贝文件至评估机构模块（生成文件记录，文件地址用同一个）
        try {
            SinglePkREQ req = new SinglePkREQ();
            req.setId(mainId);
            AppraisalCompanyDetailRSP detail = appraisalCompanyWhitelistService.detail(req);
            MaterialsList copy = BeanUtil.copyProperties(materialsList, MaterialsList.class);
            copy.reset();
            copy.setBelongId(detail.getCompanyId());
            copy.setMaterialsType(LeaseAppraisalFileTypeEnum.APPRAISAL_DATA_LIST.name());
            copy.setBusinessType(BusinessModuleEnum.LEASE_APPRAISAL_DATA_LIST.name());
            copy.setSourceBusinessKey(BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST.name() + "@" + mainId);
            materialsListService.save(copy);
        } catch (Exception e) {
            log.error("同步上传文件至评估机构池发生异常[{}]", JSONUtil.toJsonStr(materialsList), e);
        }
    }

    @Override
    public void afterRemoveHandle(Long mainId, List<MaterialsList> fileList, Long userId){
        if (CollectionUtil.isEmpty(fileList)) {
            return;
        }
        // 上层业务保证批量删除是同一分类
        MaterialsList materialsList = fileList.get(0);
        Set<Long> fileIds = fileList.stream().map(MaterialsList::getId).collect(Collectors.toSet());
        this.infoChange(mainId, materialsList.getMaterialsType());
        try {
            SinglePkREQ req = new SinglePkREQ();
            req.setId(mainId);
            AppraisalCompanyDetailRSP detail = appraisalCompanyWhitelistService.detail(req);
            List<MaterialsList> list = materialsListService.listBy(BusinessModuleEnum.LEASE_APPRAISAL_DATA_LIST.name(), detail.getCompanyId());
            Set<Long> deleteIds = new HashSet<>();
            for (MaterialsList m : list) {
                if (StrUtil.isNotBlank(m.getSourceBusinessKey())) {
                    String[] array = m.getSourceBusinessKey().split("@");
                    if (StrUtil.equals(array[0], BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST.name()) && fileIds.contains(Long.parseLong(array[1]))) {
                        deleteIds.add(m.getId());
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(deleteIds)) {
                materialsListService.removeByIds(deleteIds);
            }
        } catch (Exception e) {
            log.error("同步删除评估机构池文件发生异常[{}]", JSONUtil.toJsonStr(fileIds), e);
        }
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST.name();
    }

    private void infoChange(Long mainId, String materialType) {
        AppraisalCompanyWhitelist record = appraisalCompanyWhitelistService.getById(mainId);
        record.setLastOperatorId(AccountUtil.getLoginInfo().getId());
        if (StrUtil.equals(record.getRecordStatus(), RecordStatus.TAKE_EFFECT.name()) && StrUtil.equals(materialType, AppraisalCompanyWhitelistMaterialEnum.EXTRA_OUT.name()) && !StrUtil.equals(record.getProcessStatus(), AppraisalCompanyWhitelistProcessStatusEnum.OUT_UNDER_APPROVAL.name())) {
            record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.OUT_UN_SUBMIT.name());
        }
        if (StrUtil.equals(record.getRecordStatus(), RecordStatus.TAKE_EFFECT.name()) && StrUtil.equals(materialType, AppraisalCompanyWhitelistMaterialEnum.NORMAL.name()) && !StrUtil.equals(record.getProcessStatus(), AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_UNDER_APPROVAL.name())) {
            record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_UN_SUBMIT.name());
        }
        appraisalCompanyWhitelistService.updateById(record);
    }
}
