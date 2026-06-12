package cn.zswltech.mithras.application.orchestration.groupcredit.review;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportRemoveREQ;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportUploadREQ;
import cn.zswltech.mithras.credit.application.groupcredit.review.GroupCreditReviewReportApplicationService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.credit.groupcredit.review.enums.GroupCreditReviewMaterialsEnum;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ONLY_BIZ_DEPT_DO;


/**
 * 评审报告文件
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:50 AM
 */
@Service
public class GroupCreditReviewReportService implements GroupCreditReviewReportApplicationService, GroupCreditReviewUpdateAdvice {

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(MultipartFile file, GroupCreditReviewReportUploadREQ req) {
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(req.getGroupCreditReviewId());
        authCheck(baseInfo);
        materialsListService.add(file, req.getGroupCreditReviewId(), req.getMaterialsType(), BusinessModuleEnum.GROUP_CREDIT_REVIEW.name());
        recordStatus(req.getGroupCreditReviewId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(GroupCreditReviewReportRemoveREQ req) {
        MaterialsList materialsList = materialsListService.getById(req.getId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!BusinessModuleEnum.GROUP_CREDIT_REVIEW.name().equals(materialsList.getBusinessType())) {
            throw new MithrasException("不可操作其他模块业务数据");
        }
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(baseInfo);
        materialsListService.remove(Collections.singletonList(req.getId()));
        recordStatus(baseInfo.getId());
    }

    @Override
    public FileListRSP download(Long recordId) {
        return materialsListService.download(recordId);
    }

    @Override
    public List<Pair<String, List<GroupCreditReviewReportListRSP>>> list(GroupCreditReviewReportListREQ req) {
        Page<MaterialsList> page = materialsListMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<MaterialsList>lambdaQuery()
                        .in(MaterialsList::getMaterialsType, new ArrayList<>(GroupCreditReviewMaterialsEnum.listAll()))
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW.name())
                        .eq(MaterialsList::getBelongId, req.getId())
                        .orderByDesc(MaterialsList::getCreateTime)
        );
        List<Long> creatorIdList = page.getRecords().stream().map(MaterialsList::getCreateBy).distinct().collect(Collectors.toList());
        Map<Long, String> creatorNameMap = id2NameService.sysUserId2Name(creatorIdList);
        List<GroupCreditReviewReportListRSP> rspList = page.getRecords().stream().map(m -> {
            GroupCreditReviewReportListRSP rsp = new GroupCreditReviewReportListRSP();
            rsp.setId(m.getId());
            rsp.setMaterialsType(m.getMaterialsType());
            rsp.setTypeName(Optional.ofNullable(m.getMaterialsType()).map(GroupCreditReviewMaterialsEnum::getByName).map(pem -> pem.getDisplay()).orElse(""));
            rsp.setSort(Optional.ofNullable(m.getMaterialsType()).map(GroupCreditReviewMaterialsEnum::getByName).map(pem -> pem.getSort()).orElse(null));
            rsp.setFileName(m.getFilename());
            rsp.setCreateTime(Optional.ofNullable(m.getCreateTime()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            rsp.setCreator(creatorNameMap.get(m.getCreateBy()));
            return rsp;
        }).collect(Collectors.toList());

        List<List<GroupCreditReviewReportListRSP>> groupRspList = rspList.stream()
                .collect(Collectors.groupingBy(GroupCreditReviewReportListRSP::getMaterialsType))
                .values().stream()
                .sorted(Comparator.comparing(rs -> Optional.ofNullable(GroupCreditReviewMaterialsEnum.getByName(rs.get(0).getMaterialsType())).map(e -> e.getSort()).orElse(Integer.MAX_VALUE)))
                .collect(Collectors.toList());
        List<Pair<String, List<GroupCreditReviewReportListRSP>>> resList = new ArrayList<>();
        for (List<GroupCreditReviewReportListRSP> groupRsp : groupRspList) {
            resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
        }
        return resList;
    }

    private void authCheck(GroupCreditReviewBaseInfo baseInfo) {
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = groupCreditReviewService.findRelatedProcess(baseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }

}
