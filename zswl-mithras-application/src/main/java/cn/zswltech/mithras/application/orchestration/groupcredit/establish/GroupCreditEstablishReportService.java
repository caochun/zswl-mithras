package cn.zswltech.mithras.application.orchestration.groupcredit.establish;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportRemoveREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportUploadREQ;
import cn.zswltech.mithras.credit.application.groupcredit.establish.impl.GroupCreditEstablishService;
import cn.zswltech.mithras.credit.application.groupcredit.establish.impl.GroupCreditEstablishUpdateAdvice;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.credit.groupcredit.establish.enums.GroupCreditEstablishMaterialsEnum;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.model.GroupCreditEstablishBaseInfo;
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
 * 立项报告文件
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:50 AM
 */
@Service
public class GroupCreditEstablishReportService implements GroupCreditEstablishUpdateAdvice {

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private GroupCreditEstablishService groupCreditEstablishService;

    @Transactional(rollbackFor = Exception.class)
    public void upload(MultipartFile file, GroupCreditEstablishReportUploadREQ req) {
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(req.getGroupCreditEstablishId());
        authCheck(baseInfo);
        materialsListService.add(file, req.getGroupCreditEstablishId(), req.getMaterialsType(), BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name());
        recordStatus(req.getGroupCreditEstablishId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(GroupCreditEstablishReportRemoveREQ req) {
        MaterialsList materialsList = materialsListService.getById(req.getId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name().equals(materialsList.getBusinessType())) {
            throw new MithrasException("不可操作其他模块业务数据");
        }
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(baseInfo);
        materialsListService.remove(Collections.singletonList(req.getId()));
        recordStatus(baseInfo.getId());
    }

    public FileListRSP download(Long recordId) {
        return materialsListService.download(recordId);
    }

    public List<Pair<String, List<GroupCreditEstablishReportListRSP>>> list(GroupCreditEstablishReportListREQ req) {
        Page<MaterialsList> page = materialsListMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<MaterialsList>lambdaQuery()
                        .in(MaterialsList::getMaterialsType, new ArrayList<>(GroupCreditEstablishMaterialsEnum.listAll()))
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name())
                        .eq(MaterialsList::getBelongId, req.getId())
                        .orderByDesc(MaterialsList::getCreateTime)
        );
        List<Long> creatorIdList = page.getRecords().stream().map(MaterialsList::getCreateBy).distinct().collect(Collectors.toList());
        Map<Long, String> creatorNameMap = id2NameService.sysUserId2Name(creatorIdList);
        List<GroupCreditEstablishReportListRSP> rspList = page.getRecords().stream().map(m -> {
            GroupCreditEstablishReportListRSP rsp = new GroupCreditEstablishReportListRSP();
            rsp.setId(m.getId());
            rsp.setMaterialsType(m.getMaterialsType());
            rsp.setTypeName(Optional.ofNullable(m.getMaterialsType()).map(GroupCreditEstablishMaterialsEnum::getByName).map(pem -> pem.getDisplay()).orElse(""));
            rsp.setSort(Optional.ofNullable(m.getMaterialsType()).map(GroupCreditEstablishMaterialsEnum::getByName).map(pem -> pem.getSort()).orElse(null));
            rsp.setFileName(m.getFilename());
            rsp.setCreateTime(Optional.ofNullable(m.getCreateTime()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            rsp.setCreator(creatorNameMap.get(m.getCreateBy()));
            return rsp;
        }).collect(Collectors.toList());

        List<List<GroupCreditEstablishReportListRSP>> groupRspList = rspList.stream()
                .collect(Collectors.groupingBy(GroupCreditEstablishReportListRSP::getMaterialsType))
                .values().stream()
                .sorted(Comparator.comparing(rs -> Optional.ofNullable(GroupCreditEstablishMaterialsEnum.getByName(rs.get(0).getMaterialsType())).map(e -> e.getSort()).orElse(Integer.MAX_VALUE)))
                .collect(Collectors.toList());
        List<Pair<String, List<GroupCreditEstablishReportListRSP>>> resList = new ArrayList<>();
        for (List<GroupCreditEstablishReportListRSP> groupRsp : groupRspList) {
            resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
        }
        return resList;
    }

    private void authCheck(GroupCreditEstablishBaseInfo baseInfo) {
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = groupCreditEstablishService.findRelatedProcess(baseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }

}
