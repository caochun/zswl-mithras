package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.application.orchestration.document.convert.FileConvert;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.mobile.enums.VisitPhaseStatus;
import cn.zswltech.mithras.customer.mobile.enums.VisitRecordStatus;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.customer.mobile.mapper.VisitRecordMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.customer.mobile.model.VisitRecord;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.EXPIRE;


/**
 * @ClassName AfterLeaseCollectionCheck
 * 项目评审文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:51 上午
 * @Version 1.0
 **/
@Component
public class ProjReviewCheckHandler extends FileModuleCheck {

    private static final Set<String> SPONSOR_COULD_REMOVE_TYPE_SET = new HashSet<>();

    static {
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjReviewMaterialsEnum.DUE_DILIGENCE_REPORT.name());
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjReviewMaterialsEnum.OTHER.name());
    }

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private VisitRecordMapper visitRecordMapper;
    @Resource
    protected FileConvert fileConvert;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.PROJ_REVIEW.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(mainId);
        authCheck(projReviewBaseInfo);
        // 确定是否为发起人
        if (!Objects.equals(AccountUtil.getLoginInfo().getId(), projReviewBaseInfo.getProjSponsorUserId())) {
            // 上传尽调报告 或 其他文件
            throw new AuthCheckException("详情页只允许发起人上传，其他岗位请在审批流中上传相应文件报告");
        }
    }


    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        // 查询项目评审主数据
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(projReviewBaseInfo);
        if (!SPONSOR_COULD_REMOVE_TYPE_SET.contains(materialsList.getMaterialsType())) {
            throw new MithrasException("只能删除尽调报告/业务定价审批表/其他");
        }
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        //下载权限同查看暂不限制
        super.checkDownload(moduleKey, mainId, fileId);
    }

    private void authCheck(ProjReviewBaseInfo projReviewBaseInfo) {
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("项目评审信息不存在");
        }
        if (CLOSED.name().equals(projReviewBaseInfo.getProjReviewStatus()) || EXPIRE.name().equals(projReviewBaseInfo.getProjReviewStatus())) {
            throw new MithrasException("该评审已关闭，不允许再修改有关信息");
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = projReviewService.findRelatedProcess(projReviewBaseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> afterList(Long mainId, String moduleKey, FileListREQ req) {
        if (BusinessModuleEnum.PROJ_REVIEW.name().equalsIgnoreCase(moduleKey)) {
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(mainId);
            List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                    Wrappers.<VisitRecord>lambdaQuery()
                            .in(VisitRecord::getProjCode, projReviewBaseInfo.getProjCode())
                            .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                            .eq(VisitRecord::getDeleted, 0)
                            .orderByDesc(VisitRecord::getCheckInDate));
            if (!visitRecordList.isEmpty()) {
                Set<Long> visitIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
                List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, "VISIT_RECORD")
                        .in(MaterialsList::getBelongId, visitIds)
                        .eq(MaterialsList::getMaterialsType, VisitPhaseStatus.ON_SITE_DUE_DILIGENCE.name())
                );
                for (VisitRecord visitRecord : visitRecordList) {
                    for (MaterialsList materialsList : dataList) {
                        if (visitRecord.getId().equals(materialsList.getBelongId())) {
                            materialsList.setLocation(visitRecord.getCheckInLocation());
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(dataList)) {
                    materialsListService.updateBatchById(dataList);
                }
                /*if (CollectionUtil.isNotEmpty(dataList)) {
                    dataList.removeIf(e -> StrUtil.isNotBlank(e.getSourceBusinessKey()));
                }*/
                List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
                for (FileListRSP fileListRSP : rspList) {
                    for (VisitRecord visitRecord : visitRecordList) {
                        if (fileListRSP.getBelongId().equals(visitRecord.getId())) {
                            fileListRSP.setLocation(visitRecord.getCheckInLocation());
                        }
                    }
                }
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
                    resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
                }
                return resList;
            }
        }
        return null;
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
