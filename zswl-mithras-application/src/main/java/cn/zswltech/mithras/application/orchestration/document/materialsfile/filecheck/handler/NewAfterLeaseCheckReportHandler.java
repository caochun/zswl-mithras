package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.afterlease.application.auth.AfterLeaseCheckReportModifyMainChecker;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.document.convert.FileConvert;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportMaterialsEnum;
import cn.zswltech.mithras.afterlease.enums.NewAfterLeaseCheckMaterialsEnum;
import cn.zswltech.mithras.customer.mobile.enums.VisitPhaseStatus;
import cn.zswltech.mithras.customer.mobile.enums.VisitRecordStatus;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.customer.mobile.persistence.mapper.VisitRecordMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.mobile.persistence.model.VisitRecord;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 租后调整
 * @author: jackerhe
 * @date: 2023/8/30 5:29 下午
 **/
@Component
public class NewAfterLeaseCheckReportHandler extends FileModuleCheck {

    @Resource
    private AfterLeaseCheckReportModifyMainChecker leaseCheckReportModifyMainChecker;
    @Resource
    private NewAfterLeaseCheckPlanClientMapper checkPlanClientMapper;
    @Resource
    private VisitRecordMapper visitRecordMapper;
    @Resource
    protected FileConvert fileConvert;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
    }


    @Override
    public List<Pair<String, List<FileListRSP>>> afterList(Long mainId, String moduleKey, FileListREQ req) {
        if (BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name().equalsIgnoreCase(moduleKey)) {
            // 检查是否需要过滤材料类型
            if (CollectionUtil.isNotEmpty(req.getMaterialsTypes())) {
                boolean containsKhh = req.getMaterialsTypes().contains(AfterLeaseCheckReportMaterialsEnum.KHHY.name());
                boolean containsCheckReport = req.getMaterialsTypes().contains(NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_PUBLIC_FINANCE.name());
                // 如果只请求了 CHECK_REPORT_PUBLIC_FINANCE，不返回 KHHY 数据
                if (containsCheckReport && !containsKhh) {
                    return null;
                }
            }
            NewAfterLeaseCheckPlanClient checkPlanClient = checkPlanClientMapper.selectById(mainId);
            if (checkPlanClient != null && checkPlanClient.getPlanId() != null) {
                List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                        Wrappers.<VisitRecord>lambdaQuery()
                                .in(VisitRecord::getCheckPlanId, checkPlanClient.getPlanId())
                                .eq(VisitRecord::getClientId, checkPlanClient.getClientId())
                                .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                                .eq(VisitRecord::getDeleted, 0)
                                .orderByDesc(VisitRecord::getCheckInDate));
                if (!visitRecordList.isEmpty()) {
                    Set<Long> visitIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
                    List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                            .eq(MaterialsList::getBusinessType, "VISIT_RECORD")
                            .in(MaterialsList::getBelongId, visitIds)
                            .eq(MaterialsList::getMaterialsType, VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.name())
                    );
                    for (VisitRecord visitRecord : visitRecordList) {
                        for (MaterialsList materialsList : dataList) {
                            if (visitRecord.getId().equals(materialsList.getBelongId())) {
                                materialsList.setLocation(visitRecord.getCheckInLocation());
                            }
                        }
                    }
                    if(CollectionUtil.isEmpty(dataList)){
                        return null;
                    }
                    materialsListService.updateBatchById(dataList);
                    /*if (CollectionUtil.isNotEmpty(dataList)) {
                        dataList.removeIf(e -> StrUtil.isNotBlank(e.getSourceBusinessKey()));
                    }*/
                    List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
                    fileConvert.fillName(rspList);
                    rspList.sort(new CommonFileSortComparator());
                    List<List<FileListRSP>> groupRspList = rspList.stream()
                            .collect(Collectors.groupingBy(FileListRSP::getMaterialsType))
                            .values().stream()
                            .collect(Collectors.toList());
                    // 分组排序
                    sortGroup(groupRspList);
                    List<Pair<String, List<FileListRSP>>> resList = new ArrayList<>();
                    for (List<FileListRSP> groupRsp : groupRspList) {
                        resList.add(new Pair<>(AfterLeaseCheckReportMaterialsEnum.KHHY.name(), groupRsp));
                    }
                    return resList;
                }
            }
        }
        return null;
    }

    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        checkJob(moduleKey, Collections.singletonList(fileId));
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds){
        // 如果是租后检查的删除，上传人操作自己的文件即可
        List<MaterialsList> materialsLists = materialsListService.listByIds(fileIds);
        for (MaterialsList materialsList : materialsLists) {
            if (!Objects.equals(materialsList.getCreateBy(), AccountUtil.getLoginInfo().getId())) {
                throw new AuthCheckException("非数据上传人，不能操作");
            }
        }
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
    }

    private void checkJob(String moduleKey, List<Long> fileIds){
        if(ObjectUtil.isEmpty(fileIds)){
            return;
        }
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        Set<Long> belongSet = materialsListService.getByIds(fileIds).stream().map(MaterialsList::getBelongId).collect(Collectors.toSet());
        belongSet.forEach(mainId -> leaseCheckReportModifyMainChecker.check(moduleEnum, NewAfterLeaseCheckPlanClientMapper.class, mainId, null));
    }

    protected int getGroupFileSort(FileListRSP rsp) {
        return 0;
    }

    /**
     * 文件分组排序
     * @param groupRspList
     */
    protected void sortGroup(List<List<FileListRSP>> groupRspList) {
        groupRspList.sort(Comparator.comparing(rsp -> getGroupFileSort(rsp.get(0))));
    }
}
