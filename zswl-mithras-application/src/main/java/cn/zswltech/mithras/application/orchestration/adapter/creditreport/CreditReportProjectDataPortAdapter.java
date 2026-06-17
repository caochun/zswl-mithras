package cn.zswltech.mithras.application.orchestration.adapter.creditreport;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.creditreport.service.CreditReportProjectDataPort;
import cn.zswltech.mithras.creditreport.service.CreditReportProjectSnapshot;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishTradeStructureMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewTradeStructureMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishTradeStructure;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewTradeStructure;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CreditReportProjectDataPortAdapter implements CreditReportProjectDataPort {

    private static final String PROJ_ESTABLISH = "PROJ_ESTABLISH";
    private static final String PROJ_REVIEW = "PROJ_REVIEW";
    private static final String GROUP_CREDIT_ESTABLISH = "GROUP_CREDIT_ESTABLISH";
    private static final String GROUP_CREDIT_REVIEW = "GROUP_CREDIT_REVIEW";

    @Resource
    private ProjEstablishTradeStructureMapper projEstablishTradeStructureMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjReviewTradeStructureMapper projReviewTradeStructureMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;

    @Override
    public CreditReportProjectSnapshot getProjectSnapshot(String bizType, Long projectId) {
        if (StrUtil.equals(bizType, PROJ_ESTABLISH)) {
            return getProjEstablishSnapshot(projectId);
        }
        if (StrUtil.equals(bizType, PROJ_REVIEW)) {
            return getProjReviewSnapshot(projectId);
        }
        if (StrUtil.equals(bizType, GROUP_CREDIT_ESTABLISH)) {
            return getGroupCreditEstablishSnapshot(projectId);
        }
        if (StrUtil.equals(bizType, GROUP_CREDIT_REVIEW)) {
            return getGroupCreditReviewSnapshot(projectId);
        }
        return null;
    }

    @Override
    public List<Long> listAvailableProjReviewIds(String projIdDataType, Long projId) {
        return projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .notIn(ProjReviewBaseInfo::getProjReviewStatus, "CLOSED", "INVALID", "EXPIRE")
                        .eq(projId != null, ProjReviewBaseInfo::getProjEstablishId, projId)
                        .eq(StrUtil.isNotBlank(projIdDataType), ProjReviewBaseInfo::getRelationDataType, projIdDataType))
                .stream()
                .map(ProjReviewBaseInfo::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> listClientIds(String bizType, Long projectId) {
        CreditReportProjectSnapshot snapshot = getProjectSnapshot(bizType, projectId);
        return snapshot == null ? Collections.emptyList() : snapshot.getClientIds();
    }

    @Override
    public List<CreditReportProjectSnapshot> listProjectSnapshotsByClientId(Long clientId) {
        List<CreditReportProjectSnapshot> snapshots = new ArrayList<>();
        snapshots.addAll(listProjEstablishSnapshotsByClientId(clientId));
        snapshots.addAll(listGroupCreditEstablishSnapshotsByClientId(clientId));
        return snapshots;
    }

    private CreditReportProjectSnapshot getProjEstablishSnapshot(Long projectId) {
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(projectId);
        if (baseInfo == null) {
            return null;
        }
        CreditReportProjectSnapshot snapshot = new CreditReportProjectSnapshot();
        snapshot.setProjId(baseInfo.getId());
        snapshot.setProjIdDataType(PROJ_ESTABLISH);
        snapshot.setProjCode(baseInfo.getProjCode());
        snapshot.setProjName(baseInfo.getProjName());
        snapshot.setClientIds(projEstablishTradeStructureMapper.selectList(Wrappers.<ProjEstablishTradeStructure>lambdaQuery()
                        .eq(ProjEstablishTradeStructure::getProjEstablishId, projectId))
                .stream()
                .map(ProjEstablishTradeStructure::getClientId)
                .collect(Collectors.toList()));
        return snapshot;
    }

    private CreditReportProjectSnapshot getProjReviewSnapshot(Long projectId) {
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(projectId);
        if (baseInfo == null) {
            return null;
        }
        CreditReportProjectSnapshot snapshot = new CreditReportProjectSnapshot();
        snapshot.setProjId(baseInfo.getId());
        snapshot.setProjIdDataType(PROJ_REVIEW);
        snapshot.setProjCode(baseInfo.getProjCode());
        snapshot.setProjName(baseInfo.getProjName());
        snapshot.setClientIds(projReviewTradeStructureMapper.selectList(Wrappers.<ProjReviewTradeStructure>lambdaQuery()
                        .eq(ProjReviewTradeStructure::getProjReviewId, projectId))
                .stream()
                .map(ProjReviewTradeStructure::getClientId)
                .collect(Collectors.toList()));
        return snapshot;
    }

    private CreditReportProjectSnapshot getGroupCreditEstablishSnapshot(Long projectId) {
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(projectId);
        if (baseInfo == null) {
            return null;
        }
        CreditReportProjectSnapshot snapshot = new CreditReportProjectSnapshot();
        snapshot.setProjId(baseInfo.getId());
        snapshot.setProjIdDataType(GROUP_CREDIT_ESTABLISH);
        snapshot.setProjCode(baseInfo.getProjCode());
        snapshot.setProjName(baseInfo.getProjName());
        snapshot.setClientIds(Collections.singletonList(baseInfo.getClientId()));
        return snapshot;
    }

    private CreditReportProjectSnapshot getGroupCreditReviewSnapshot(Long projectId) {
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(projectId);
        if (baseInfo == null) {
            return null;
        }
        CreditReportProjectSnapshot snapshot = new CreditReportProjectSnapshot();
        snapshot.setProjId(baseInfo.getId());
        snapshot.setProjIdDataType(GROUP_CREDIT_REVIEW);
        snapshot.setProjCode(baseInfo.getProjCode());
        snapshot.setProjName(baseInfo.getProjName());
        snapshot.setClientIds(Collections.singletonList(baseInfo.getClientId()));
        return snapshot;
    }

    private List<CreditReportProjectSnapshot> listProjEstablishSnapshotsByClientId(Long clientId) {
        Set<Long> projEstablishIds = projEstablishTradeStructureMapper.selectList(Wrappers.<ProjEstablishTradeStructure>lambdaQuery()
                        .eq(ProjEstablishTradeStructure::getClientId, clientId))
                .stream()
                .map(ProjEstablishTradeStructure::getProjEstablishId)
                .collect(Collectors.toSet());
        if (projEstablishIds.isEmpty()) {
            return Collections.emptyList();
        }
        return projEstablishBaseInfoMapper.selectBatchIds(projEstablishIds)
                .stream()
                .map(e -> {
                    CreditReportProjectSnapshot snapshot = new CreditReportProjectSnapshot();
                    snapshot.setProjId(e.getId());
                    snapshot.setProjIdDataType(PROJ_ESTABLISH);
                    snapshot.setProjCode(e.getProjCode());
                    snapshot.setProjName(e.getProjName());
                    snapshot.setClientIds(Collections.singletonList(clientId));
                    return snapshot;
                })
                .collect(Collectors.toList());
    }

    private List<CreditReportProjectSnapshot> listGroupCreditEstablishSnapshotsByClientId(Long clientId) {
        List<GroupCreditEstablishBaseInfo> establishList = groupCreditEstablishBaseInfoMapper.selectList(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery()
                .eq(GroupCreditEstablishBaseInfo::getClientId, clientId));
        if (establishList.isEmpty()) {
            return Collections.emptyList();
        }
        return establishList.stream()
                .map(e -> {
                    CreditReportProjectSnapshot snapshot = new CreditReportProjectSnapshot();
                    snapshot.setProjId(e.getId());
                    snapshot.setProjIdDataType(GROUP_CREDIT_REVIEW);
                    snapshot.setProjCode(e.getProjCode());
                    snapshot.setProjName(e.getProjName());
                    snapshot.setClientIds(Collections.singletonList(e.getClientId()));
                    return snapshot;
                })
                .collect(Collectors.toList());
    }
}
