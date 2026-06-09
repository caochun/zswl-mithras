package cn.zswltech.mithras.archives.application;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.archives.*;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.archives.domain.enums.ArchiveTemplateStatusEnum;
import cn.zswltech.mithras.archives.domain.enums.ArchivesFlowStatusEnum;
import cn.zswltech.mithras.archives.domain.enums.ArchivesStatusEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.*;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.archives.infrastructure.persistence.model.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.workflow.application.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto.ArchivesFlatTempalteDTO;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto.ArchivesManagementDTO;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto.ArchivesMastFileCountDTO;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto.ArchivesMastFileTypeCountDTO;
import cn.zswltech.mithras.system.user.Id2NameService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wwj
 * @create: 2023-02-28
 **/
@Slf4j
@Service
public class ArchivesManageService extends ServiceImpl<ArchivesManagementMapper, ArchivesManagement> {

    @Resource
    private FlowTaskApiService taskApiService;

    @Resource
    private ArchiveFileTypeMapper archiveFileTypeMapper;

    @Resource
    private ArchiveTypeGroupMapper archiveTypeGroupMapper;

    @Resource
    private ArchivesDownloadPermissionMapper archivesDownloadPermissionMapper;

    @Resource
    private ArchivesDownloadPermissionReasonMapper archivesDownloadPermissionReasonMapper;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;

    @Resource
    private ArchiveTemplateMapper archiveTemplateMapper;

    @Resource
    private ArchivesManagementMapper archivesManagementMapper;

    @Resource
    private FlowProcessApiService processApiService;

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BizProcessDataService bizProcessDataService;

    @Resource
    private ArchivesSupportPort archivesSupportPort;

    public cn.zswltech.mithras.api.common.R<List<ProjEstablishVagueListRSP>> vague(ProjEstablishVagueListREQ req) {
        Map<String, ProjEstablishVagueListRSP> vagueMap = archivesSupportPort.vagueQuery(req);
        if (cn.hutool.core.util.ObjectUtil.isEmpty(vagueMap)) {
            return cn.zswltech.mithras.api.common.R.ok();
        }
        return cn.zswltech.mithras.api.common.R.ok(new ArrayList<>(vagueMap.values()));
    }

    public PageR<ArchivesListRSP> list(ArchivesListREQ req){
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        req.setIsBizUser(isBizUser);
        req.setDeptIdList(canViewDeptIds);
        req.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        List<ArchivesFlatTempalteDTO> flatTemplates = null;
        if (StrUtil.isNotEmpty(req.getFileType())){
            flatTemplates = archiveTemplateMapper.flatTemplate(null, req.getFileType());
            if (CollectionUtil.isNotEmpty(flatTemplates)){
                req.setArchivesId(flatTemplates.stream().map(ArchivesFlatTempalteDTO::getArchiveId).distinct().collect(Collectors.toList()));
            }
            if (CollectionUtil.isEmpty(req.getArchivesId())){
                PageR.empty(req.getPage(), req.getPageSize());
            }
        }

        Page<ArchivesManagementDTO> pageList = archivesManagementMapper.pageList(new Page<>(req.getPage(), req.getPageSize()), req);
        List<ArchivesListRSP> rsps = new ArrayList<>();
        List<ArchivesManagementDTO> records = pageList.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            List<Long> templateIds = records.stream().map(ArchivesManagementDTO::getTemplateId).distinct().collect(Collectors.toList());
            List<Long> archivesIds = records.stream().map(ArchivesManagementDTO::getId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(flatTemplates)) {
                flatTemplates = flatTemplates.stream().filter(o -> templateIds.contains(o.getTemplateId())).collect(Collectors.toList());
            }else {
                flatTemplates = archiveTemplateMapper.flatTemplate(templateIds, req.getFileType());
            }
            List<ArchivesMastFileCountDTO> fileCounts = archiveTemplateMapper.mustFileCount(archivesIds);
            List<ArchivesMastFileTypeCountDTO> fileTypeCounts = archiveTemplateMapper.mustFileTypeCount(templateIds);
            Map<Long, List<ArchivesFlatTempalteDTO>> archivesMap = new HashMap<>();
            AccountVO loginInfo = AccountUtil.getLoginInfo();
            Map<Long, List<ArchivesDownloadPermission>> filePermission = new HashMap<>();
            if (CollectionUtil.isNotEmpty(flatTemplates)) {
                archivesMap = flatTemplates.stream().collect(Collectors.groupingBy(ArchivesFlatTempalteDTO::getArchiveId));
                List<Long> fileIds = flatTemplates.stream().map(ArchivesFlatTempalteDTO::getFileId).distinct().collect(Collectors.toList());
                List<ArchivesDownloadPermission> permissions = archivesDownloadPermissionMapper.selectList(Wrappers.<ArchivesDownloadPermission>lambdaQuery().in(ArchivesDownloadPermission::getMaterialsId, fileIds).ge(ArchivesDownloadPermission::getExpires, LocalDateTime.now()).eq(ArchivesDownloadPermission::getUserId,loginInfo.getId()).ne(ArchivesDownloadPermission::getStatus,2));
                if (CollectionUtil.isNotEmpty(permissions)){
                    filePermission = permissions.stream().collect(Collectors.groupingBy(ArchivesDownloadPermission::getMaterialsId));
                }
            }
            Map<Long, List<ArchivesMastFileCountDTO>> fileCountMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(fileCounts)){
                fileCountMap = fileCounts.stream().collect(Collectors.groupingBy(ArchivesMastFileCountDTO::getArchiveId));
            }
            Map<Long, List<ArchivesMastFileTypeCountDTO>> fileTypeCountMap = fileTypeCounts.stream().collect(Collectors.groupingBy(ArchivesMastFileTypeCountDTO::getTemplateId));
            Set<Long> sysUserIds = new HashSet<>();
            Set<Long> clientIds = new HashSet<>();
            Set<Long> deptIds = new HashSet<>();
            for (ArchivesManagementDTO dto : records) {
                sysUserIds.add(dto.getProjSponsorUserId());
                clientIds.add(dto.getClientId());
                deptIds.add(dto.getBizDeptId());
            }
            Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
            Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
            Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
            for (ArchivesManagementDTO dto : records) {
                ArchivesListRSP rsp = new ArchivesListRSP();
                rsp.setId(dto.getId());
                rsp.setProjName(dto.getProjName());
                rsp.setBizType(dto.getBizType());
                rsp.setBizDept(deptMap.get(dto.getBizDeptId()));
                rsp.setProjSponsorUserName(sysUserMap.get(dto.getProjSponsorUserId()));
                rsp.setClientName(clientMap.get(dto.getClientId()));
                rsp.setStatus(dto.getStatus());
                rsp.setFlowStatus(dto.getFlowStatus());
                rsp.setArchivesCode(dto.getArchivesCode());
                if (dto.getType().equals(0)) {
                    Long fileCount = 0L;
                    if (fileCountMap.get(dto.getId()) != null) {
                        fileCount = fileCountMap.get(dto.getId()).get(0).getCount();
                    }
                    Long fileTypeCount = 0L;
                    if (fileTypeCountMap.get(dto.getTemplateId()) != null) {
                        fileTypeCount = fileTypeCountMap.get(dto.getTemplateId()).get(0).getCount();
                    }
                    rsp.setRequiredSituation(fileCount.toString() + "/" + fileTypeCount.toString());
                }else {
                    rsp.setRequiredSituation("-");
                }
                rsp.setCreateTime(dto.getCreateTime());
                rsp.setUpdateTime(dto.getUpdateTime());
                if (ArchivesFlowStatusEnum.APPROVAL_PASS.name().equals(dto.getFlowStatus())) {
                    Map<Long, ArchivesListRSP.Archives> archiveMap = new HashMap<>();
                    List<ArchivesFlatTempalteDTO> tempalteDTOS = archivesMap.get(dto.getId());
                    if (CollectionUtil.isNotEmpty(tempalteDTOS)) {
                        for (ArchivesFlatTempalteDTO dto1 : tempalteDTOS) {
                            if (!archiveMap.containsKey(dto1.getGroupId())) {
                                ArchivesListRSP.Archives archive = new ArchivesListRSP.Archives();
                                archive.setGroupId(dto1.getGroupId());
                                archive.setGroupKey(dto.getId() + "_" + dto1.getGroupId());
                                archive.setGroupName(dto1.getGroupName());
                                archive.setFiles(Lists.newArrayList());
                                archive.setSort(dto1.getSort());
                                archiveMap.put(dto1.getGroupId(), archive);
                            }
                            ArchivesListRSP.Files files = new ArchivesListRSP.Files();
                            files.setFileId(dto1.getFileId());
                            files.setFileName(dto1.getTypeName() + "-" + dto1.getFileName());
                            List<ArchivesDownloadPermission> permissions = filePermission.get(dto1.getFileId());
                            Map<Integer, List<ArchivesDownloadPermission>> map = null;
                            if (CollectionUtil.isNotEmpty(permissions)){
                                map = permissions.stream().collect(Collectors.groupingBy(ArchivesDownloadPermission::getStatus));
                            }
                            String statusStr = null;
                            boolean canDownload = false;
                            if (map != null && CollectionUtil.isNotEmpty(map.get(1))) {
                                statusStr = "已借阅";
                                canDownload = true;
                            }else if (map != null && CollectionUtil.isNotEmpty(map.get(0))){
                                statusStr = "借阅审批中";
                            }else {
                                statusStr = "未借阅";
                            }
                            files.setStatus(statusStr);
                            files.setCanDownload(canDownload);
                            archiveMap.get(dto1.getGroupId()).getFiles().add(files);
                        }
                    }
                    rsp.setArchives(new ArrayList<>(CollectionUtil.sort(archiveMap.values(),Comparator.comparing(ArchivesListRSP.Archives::getSort))));
                }else {
                    rsp.setArchives(ListUtil.empty());
                }
                rsps.add(rsp);
            }
        }
        return PageR.of(rsps, pageList.getTotal(),
                pageList.getPages(),
                pageList.getCurrent(),
                pageList.getSize());

    }

    @Transactional(rollbackFor = Exception.class)
    public Void downloadEffect(ArchiveDownloadEffectREQ req){
        List<ArchivesManagement> archivesManagements = archivesManagementMapper.selectList(Wrappers.<ArchivesManagement>lambdaQuery().in(ArchivesManagement::getId, req.getArchivesIds()).ne(ArchivesManagement::getFlowStatus, ArchivesFlowStatusEnum.APPROVAL_PASS.name()));
        if (CollectionUtil.isNotEmpty(archivesManagements)){
            throw new MithrasException("未审批通过无法借阅");
        }
        ArchivesDownloadPermissionReason reason = new ArchivesDownloadPermissionReason();
        reason.setReason(req.getReason());
        archivesDownloadPermissionReasonMapper.insert(reason);
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        List<ArchivesDownloadPermission> downloadPermissions = new ArrayList<>();
        LocalDateTime plusYears = LocalDateTime.now().plusYears(15);
        String uuid = IdUtil.fastUUID();
        for (ArchiveDownloadEffectREQ.EffectFile effectFile: req.getFiles()){
            ArchivesDownloadPermission permission = new ArchivesDownloadPermission();
            permission.setMaterialsId(effectFile.getFileId());
            permission.setUserId(loginInfo.getId());
            permission.setBatch(uuid);
            permission.setStatus(0);
            permission.setReasonId(reason.getId());
            permission.setExpires(plusYears);
            downloadPermissions.add(permission);
        }
        archivesDownloadPermissionMapper.insertList(downloadPermissions);
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.ArchivesDownloadFlow.name());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(uuid);
        startProcessReq.setProcessInstanceName("借阅审批");
        processApiService.start(startProcessReq);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public ArchivesRSP addArchives(ArchivesAddREQ req){
        ArchivesManagement management1 = archivesManagementMapper.selectOne(Wrappers.<ArchivesManagement>lambdaQuery().eq(ArchivesManagement::getProjId, req.getProjId()).eq(ArchivesManagement::getFlowStatus, ArchivesFlowStatusEnum.NEW_UN_SUBMIT.name()).last(StringUtil.mysqlLimitOne()));
        if (management1 != null){
            throw new MithrasException("无法创建，此项目有归档任务未提交审批！");
        }
        ArchivesManagement management = new ArchivesManagement();
        management.setProjId(req.getProjId());
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(req.getProjId());
        ArchiveTemplate template = archiveTemplateMapper.selectOne(Wrappers.<ArchiveTemplate>lambdaQuery().eq(ArchiveTemplate::getStatus, ArchiveTemplateStatusEnum.ENABLE.name()).apply("FIND_IN_SET(\"" + baseInfo.getBizType() + "\"," + "template_type)").last(StringUtil.mysqlLimitOne()));
        if (template == null){
            throw new MithrasException("无可用模版！");
        }
        management.setTemplateId(template.getId());
        management.setStatus(ArchivesStatusEnum.WAITE.name());
        management.setFlowStatus(ArchivesFlowStatusEnum.NEW_UN_SUBMIT.name());
        management.setType(1);
        archivesManagementMapper.insert(management);
        ArchivesManagement update = new ArchivesManagement();
        update.setId(management.getId());
        LocalDate now = LocalDate.now();
        update.setArchivesCode(now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))+management.getId());
        archivesManagementMapper.updateById(update);
        ArchivesRSP rsp = new ArchivesRSP();
        rsp.setId(management.getId());
        return rsp;
    }

    public ArchivesInfoRSP archivesInfo(ArchivesInfoREQ req){
        ArchivesManagement management = archivesManagementMapper.selectById(req.getId());
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(management.getProjId());
        List<ArchivesMastFileCountDTO> fileCounts = archiveTemplateMapper.mustFileCount(CollectionUtil.newArrayList(req.getId()));
        List<ArchivesMastFileTypeCountDTO> fileTypeCounts = archiveTemplateMapper.mustFileTypeCount(CollectionUtil.newArrayList(management.getTemplateId()));

        List<ArchiveFileType> types = archiveTemplateMapper.noFileTypes(req.getId(), management.getTemplateId());
        ArchivesInfoRSP rsp = new ArchivesInfoRSP();
        rsp.setId(management.getId());
        rsp.setProjName(baseInfo.getProjName());
        rsp.setStatus(management.getStatus());
        rsp.setFlowStatus(management.getFlowStatus());
        rsp.setType(management.getType());
        if (management.getType().equals(0)) {
            String fileCount = "0";
            String fileTypeCount = "0";
            if (CollectionUtil.isNotEmpty(fileCounts) && fileCounts.get(0).getCount() != null){
                fileCount = fileCounts.get(0).getCount().toString();
            }
            if (CollectionUtil.isNotEmpty(fileTypeCounts) && fileTypeCounts.get(0).getCount() != null){
                fileTypeCount = fileTypeCounts.get(0).getCount().toString();
            }
            rsp.setRequiredSituation(fileCount + "/" + fileTypeCount);
        }else {
            rsp.setRequiredSituation("-");
        }
        if (CollectionUtil.isNotEmpty(types)){
            rsp.setFileTypes(types.stream().map(ArchiveFileType::getTypeName).collect(Collectors.toList()));
        }
        return rsp;
    }

    public List<ArchivesUploadSelectRsp> archivesUploadInfo(ArchivesInfoREQ req){
        ArchivesManagement management = archivesManagementMapper.selectById(req.getId());
        List<ArchiveTypeGroup> typeGroups = archiveTypeGroupMapper.selectList(Wrappers.<ArchiveTypeGroup>lambdaQuery().eq(ArchiveTypeGroup::getTemplateId, management.getTemplateId()).orderByAsc(ArchiveTypeGroup::getSort));
        List<Long> ids = typeGroups.stream().map(ArchiveTypeGroup::getId).collect(Collectors.toList());
        List<ArchiveFileType> fileTypes = archiveFileTypeMapper.selectList(Wrappers.<ArchiveFileType>lambdaQuery().in(ArchiveFileType::getGroupId, ids).orderByAsc(ArchiveFileType::getGroupId,ArchiveFileType::getSort));
        Map<Long, List<ArchiveFileType>> listMap = fileTypes.stream().collect(Collectors.groupingBy(ArchiveFileType::getGroupId));
        List<MaterialsList> materialsLists = archivesSupportPort.listMaterialsByBelongId(ArchivesSupportPort.BUSINESS_TYPE_ARCHIVES, management.getId());
        Map<String, List<MaterialsList>> fileMap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
        List<ArchivesUploadSelectRsp> uploadGroups = new ArrayList<>();
        for (ArchiveTypeGroup group : typeGroups){
            ArchivesUploadSelectRsp groups = new ArchivesUploadSelectRsp();
            groups.setTitle(group.getGroupName());
            List<ArchivesUploadSelectRsp.Select> selects = new ArrayList<>();
            for (ArchiveFileType fileType : listMap.get(group.getId())){
                ArchivesUploadSelectRsp.Select tmp = new ArchivesUploadSelectRsp.Select();
                tmp.setLabel(fileType.getTypeName());
                tmp.setValue(String.valueOf(fileType.getId()));
                if (management.getType().equals(0)) {
                    tmp.setRequired(fileType.getNeed());
                }else {
                    tmp.setRequired(0);
                }
                List<MaterialsList> materialsLists1 = fileMap.get(fileType.getId().toString());
                if (CollectionUtil.isNotEmpty(materialsLists1)) {
                    List<ArchivesUploadSelectRsp.FileInfo> files = new ArrayList<>();
                    for (MaterialsList materialsList : materialsLists1) {
                        ArchivesUploadSelectRsp.FileInfo file = new ArchivesUploadSelectRsp.FileInfo();
                        file.setFileId(materialsList.getId());
                        file.setFileName(materialsList.getFilename());
                        files.add(file);
                    }
                    tmp.setFiles(files);
                }else {
                    tmp.setFiles(ListUtil.empty());
                }
                selects.add(tmp);
            }
            groups.setChildren(selects);
            uploadGroups.add(groups);
        }
        return uploadGroups;
    }

    public List<ArchivesSearchRSP> archivesSearch(ArchivesSearchREQ req){
        ArchivesManagement management = archivesManagementMapper.selectById(req.getId());
        if (!ArchivesFlowStatusEnum.APPROVAL_PASS.name().equals(management.getFlowStatus())){
            return ListUtil.empty();
        }
        List<ArchivesFlatTempalteDTO> flatFiles = archiveTemplateMapper.archiveFileList(req.getId(),req.getContent(),req.getGroupName());
        if (CollectionUtil.isEmpty(flatFiles)){
            return ListUtil.empty();
        }
        List<Long> ids = flatFiles.stream().map(ArchivesFlatTempalteDTO::getUploadUser).distinct().collect(Collectors.toList());
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(ids);
        Map<Long, List<ArchivesDownloadPermission>> filePermission = new HashMap<>();
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        List<Long> fileIds = flatFiles.stream().map(ArchivesFlatTempalteDTO::getFileId).distinct().collect(Collectors.toList());
        List<ArchivesDownloadPermission> permissions = archivesDownloadPermissionMapper.selectList(Wrappers.<ArchivesDownloadPermission>lambdaQuery().in(ArchivesDownloadPermission::getMaterialsId, fileIds).ge(ArchivesDownloadPermission::getExpires, LocalDateTime.now()).eq(ArchivesDownloadPermission::getUserId,loginInfo.getId()).ne(ArchivesDownloadPermission::getStatus,2));
        if (CollectionUtil.isNotEmpty(permissions)){
            filePermission = permissions.stream().collect(Collectors.groupingBy(ArchivesDownloadPermission::getMaterialsId));
        }
        Map<Long,ArchivesSearchRSP> rsps = new HashMap<>();
        for (ArchivesFlatTempalteDTO dto : flatFiles){
            if (!rsps.containsKey(dto.getGroupId())){
                ArchivesSearchRSP archive = new ArchivesSearchRSP();
                archive.setGroupName(dto.getGroupName());
                archive.setFiles(Lists.newArrayList());
                archive.setSort(dto.getSort());
                rsps.put(dto.getGroupId(),archive);
            }
            ArchivesSearchRSP.FileInfo files = new ArchivesSearchRSP.FileInfo();
            files.setFileType(dto.getTypeName());
            files.setUploadUser(sysUserMap.get(dto.getUploadUser()));
            files.setUploadTime(dto.getUploadTime());
            files.setFileId(dto.getFileId());
            files.setFileName(dto.getFileName());
            List<ArchivesDownloadPermission> permissions1 = filePermission.get(dto.getFileId());
            Map<Integer, List<ArchivesDownloadPermission>> map = null;
            if (CollectionUtil.isNotEmpty(permissions1)){
                map = permissions1.stream().collect(Collectors.groupingBy(ArchivesDownloadPermission::getStatus));
            }
            String statusStr = null;
            boolean canDownload = false;
            if (map != null && CollectionUtil.isNotEmpty(map.get(1))) {
                statusStr = "已借阅";
                canDownload = true;
            }else if (map != null && CollectionUtil.isNotEmpty(map.get(0))){
                statusStr = "借阅审批中";
            }else {
                statusStr = "未借阅";
            }
            files.setStatus(statusStr);
            files.setCanDownload(canDownload);
            rsps.get(dto.getGroupId()).getFiles().add(files);
        }
        return ListUtil.toList(CollectionUtil.sort(rsps.values(),Comparator.comparing(ArchivesSearchRSP::getSort)));
    }

    public Void remind(ArchivesInfoREQ req){
        ArchivesManagement management = archivesManagementMapper.selectById(req.getId());
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(management.getProjId());
        MessageAddREQ message = new MessageAddREQ();
        message.setFrom("系统通知");
        Set<Long> to = new HashSet<>();
        if (baseInfo.getProjSponsorUserId() != null) {
            to.add(baseInfo.getProjSponsorUserId());
        }
        message.setTo(new ArrayList<>(to));
        message.setRelation(baseInfo.getProjName());
        message.setContent(baseInfo.getProjName());
        message.setNeedOa(false);
        message.setNoticeSource(NoticeSourceENUM.ARCHIVES.name());
        message.setMessageType(MessageTypeEnum.ARCHIVES.name());
        message.setPcurl(StringUtils.format(MessageUrlEnum.ARCHIVES.pcUrl,req.getId()));
        message.setBusinessId(String.valueOf(management.getId()));
        messageService.sendMessage(messageConvert.reqToMessage(message));
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Void archivesEffect(ArchivesInfoREQ req) {
        ArchivesManagement management = archivesManagementMapper.selectById(req.getId());
        if (management.getType().equals(0)) {
            List<ArchiveFileType> types = archiveTemplateMapper.noFileTypes(req.getId(), management.getTemplateId());
            if (CollectionUtil.isNotEmpty(types)) {
                List<Long> ids = types.stream().map(ArchiveFileType::getId).collect(Collectors.toList());
                List<ArchiveFileType> fileTypes = archiveFileTypeMapper.selectList(Wrappers.<ArchiveFileType>lambdaQuery().in(ArchiveFileType::getId, ids).eq(ArchiveFileType::getNeed, 1));
                if (CollectionUtil.isNotEmpty(fileTypes)) {
                    throw new MithrasException("请上传所有必传文件!");
                }
            }
        }else {
            List<MaterialsList> list = archivesSupportPort.listMaterialsByBelongId(ArchivesSupportPort.BUSINESS_TYPE_ARCHIVES, management.getId());
            if (CollectionUtil.isEmpty(list)){
                throw new MithrasException("无归档数据,无法审批");
            }

        }
        if(isInProcess(req.getId(), ArchivesSupportPort.ARCHIVES_MODEL_KEYS)){
            throw new MithrasException("该用户已处于流程中，无法提交数据");
        }

        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(management.getProjId());
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.ArchivesFlow.name());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>())
        ));
        startProcessReq.setBusinessKey(String.valueOf(management.getId()));
        startProcessReq.setProcessInstanceName(baseInfo.getProjName()+"归档审批");

        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId())
                .map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());
        ArchivesManagement update = new ArchivesManagement();
        update.setId(management.getId());
        update.setFlowStatus(ArchivesFlowStatusEnum.UNDER_APPROVAL.name());
        archivesManagementMapper.updateById(update);
        return null;
    }

    private boolean isInProcess(Long mainId, List<String> modelKeys) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(mainId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(modelKeys);
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = taskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        return !Objects.isNull(processResp);
    }

    public ArchiveDownloadFlowRSP downloadFlowInfo(ArchivesFlowInfoREQ req) {
        List<ArchivesDownloadPermission> downloadPermissions = archivesDownloadPermissionMapper.selectList(Wrappers.<ArchivesDownloadPermission>lambdaQuery().eq(ArchivesDownloadPermission::getBatch, req.getBatch()));
        ArchivesDownloadPermissionReason reason = archivesDownloadPermissionReasonMapper.selectById(downloadPermissions.get(0).getReasonId());
        ArchiveDownloadFlowRSP rsp = new ArchiveDownloadFlowRSP();
        rsp.setReason(reason.getReason());
        List<Long> fileIds = downloadPermissions.stream().map(ArchivesDownloadPermission::getMaterialsId).distinct().collect(Collectors.toList());
        List<MaterialsList> files = archivesSupportPort.getMaterialsByIds(fileIds);
        Map<String, List<MaterialsList>> fileMap = files.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
        List<Long> fileTypeIds = files.stream().map(o -> Long.parseLong(o.getMaterialsType())).distinct().collect(Collectors.toList());
        List<ArchiveFileType> fileTypes = archiveFileTypeMapper.selectList(Wrappers.<ArchiveFileType>lambdaQuery().in(ArchiveFileType::getId, fileTypeIds).orderByAsc(ArchiveFileType::getGroupId,ArchiveFileType::getSort));
        Map<Long, List<ArchiveFileType>> fileTypeMap = fileTypes.stream().collect(Collectors.groupingBy(ArchiveFileType::getGroupId));
        List<Long> archivesIds = files.stream().map(MaterialsList::getBelongId).distinct().collect(Collectors.toList());
        List<ArchivesManagement> archivesManagements = archivesManagementMapper.selectList(Wrappers.<ArchivesManagement>lambdaQuery().in(ArchivesManagement::getId, archivesIds));
        List<Long> projIds = archivesManagements.stream().map(ArchivesManagement::getProjId).distinct().collect(Collectors.toList());
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().in(ProjEstablishBaseInfo::getId, projIds));
        Map<Long, List<ProjEstablishBaseInfo>> projMap = projEstablishBaseInfos.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfo::getId));
        List<Long> templateIds = archivesManagements.stream().map(ArchivesManagement::getTemplateId).distinct().collect(Collectors.toList());
        List<ArchiveTypeGroup> archiveTypeGroups = archiveTypeGroupMapper.selectList(Wrappers.<ArchiveTypeGroup>lambdaQuery().in(ArchiveTypeGroup::getTemplateId, templateIds).orderByAsc(ArchiveTypeGroup::getTemplateId,ArchiveTypeGroup::getSort));
        Map<Long, List<ArchiveTypeGroup>> templateMap = archiveTypeGroups.stream().collect(Collectors.groupingBy(ArchiveTypeGroup::getTemplateId));
        List<ArchiveDownloadFlowRSP.MaterialsData> materialsData = new ArrayList<>();
        for (ArchivesManagement management : archivesManagements){
            List<ProjEstablishBaseInfo> projEstablishBaseInfos1 = projMap.get(management.getProjId());
            ArchiveDownloadFlowRSP.MaterialsData tmp = new ArchiveDownloadFlowRSP.MaterialsData();
            tmp.setProjName(projEstablishBaseInfos1.get(0).getProjName());
            tmp.setKey(projEstablishBaseInfos1.get(0).getId());
            List<ArchiveDownloadFlowRSP.Materials> grouplist = new ArrayList<>();
            List<ArchiveTypeGroup> groups = templateMap.get(management.getTemplateId());
            for (ArchiveTypeGroup group : groups){
                List<ArchiveFileType> fileTypes1 = fileTypeMap.get(group.getId());
                if (CollectionUtil.isNotEmpty(fileTypes1)) {
                    ArchiveDownloadFlowRSP.Materials materials = new ArchiveDownloadFlowRSP.Materials();
                    materials.setMaterialsName(group.getGroupName());
                    materials.setKey(projEstablishBaseInfos1.get(0).getId()+"_"+group.getId());
                    List<ArchiveDownloadFlowRSP.FileInfo> infos = new ArrayList<>();
                    for (ArchiveFileType o : fileTypes1){
                        List<MaterialsList> file = fileMap.get(o.getId().toString());
                        for (MaterialsList o2 : file){
                            ArchiveDownloadFlowRSP.FileInfo info = new ArchiveDownloadFlowRSP.FileInfo();
                            info.setFileId(o2.getId());
                            info.setFileName(o2.getFilename());
                            infos.add(info);
                        }
                    }
                    materials.setFiles(infos);
                    grouplist.add(materials);
                }
            }
            tmp.setMaterialsData(grouplist);
            materialsData.add(tmp);
        }
        rsp.setMaterials(materialsData);
        return rsp;
    }

    public ArchivesFlowRSP archivesFlow(ArchivesInfoREQ req) {
        ArchivesManagement management = archivesManagementMapper.selectById(req.getId());
        ArchiveTemplate template = archiveTemplateMapper.selectById(management.getTemplateId());
        List<ArchiveTypeGroup> groups = archiveTypeGroupMapper.selectList(Wrappers.<ArchiveTypeGroup>lambdaQuery().eq(ArchiveTypeGroup::getTemplateId,template.getId()).orderByAsc(ArchiveTypeGroup::getSort));

        ArchivesFlowRSP rsp = new ArchivesFlowRSP();
        rsp.setTemplateId(template.getId());
        rsp.setTemplateName(template.getTemplateName());
        List<MaterialsList> materialsLists = archivesSupportPort.listMaterialsByBelongId(ArchivesSupportPort.BUSINESS_TYPE_ARCHIVES, management.getId());
        Map<String, List<MaterialsList>> fileMap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
        List<Long> fileTypeIds = materialsLists.stream().map(o -> Long.parseLong(o.getMaterialsType())).distinct().collect(Collectors.toList());
        List<ArchiveFileType> fileTypes = archiveFileTypeMapper.selectList(Wrappers.<ArchiveFileType>lambdaQuery().in(ArchiveFileType::getId, fileTypeIds).orderByAsc(ArchiveFileType::getGroupId,ArchiveFileType::getSort));
        Map<Long, List<ArchiveFileType>> fileTypeMap = fileTypes.stream().collect(Collectors.groupingBy(ArchiveFileType::getGroupId));
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(management.getProjId());
        ArchivesFlowRSP.MaterialsData materialsData = new ArchivesFlowRSP.MaterialsData();
        materialsData.setProjName(baseInfo.getProjName());
        materialsData.setKey(baseInfo.getId());
        List<ArchivesFlowRSP.Materials> grouplist = new ArrayList<>();
        for (ArchiveTypeGroup group : groups){
            List<ArchiveFileType> fileTypes1 = fileTypeMap.get(group.getId());
                ArchivesFlowRSP.Materials materials = new ArchivesFlowRSP.Materials();
                materials.setMaterialsName(group.getGroupName());
                materials.setKey(baseInfo.getId()+"_"+group.getId());
                materials.setGrey(true);
                materials.setSize(0);
            List<ArchivesFlowRSP.FileTypes> types = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(fileTypes1)) {
                materials.setGrey(false);
                for (ArchiveFileType o : fileTypes1){
                    ArchivesFlowRSP.FileTypes typesTmp = new ArchivesFlowRSP.FileTypes();
                    typesTmp.setFileTypeName(o.getTypeName());
                    typesTmp.setKey(materials.getKey()+"_"+o.getId());
                    typesTmp.setGrey(true);
                    List<MaterialsList> file = fileMap.get(o.getId().toString());
                    List<ArchivesFlowRSP.FileInfo> infos = new ArrayList<>();
                    if (CollUtil.isNotEmpty(file)) {
                        typesTmp.setSize(file.size());
                        materials.setSize(materials.getSize()+file.size());
                        typesTmp.setGrey(false);
                        for (MaterialsList o2 : file) {
                            ArchivesFlowRSP.FileInfo info = new ArchivesFlowRSP.FileInfo();
                            info.setFileId(o2.getId());
                            info.setFileName(o2.getFilename());
                            infos.add(info);
                        }
                    }
                    typesTmp.setFiles(infos);
                    types.add(typesTmp);
                }
            }
            materials.setFileTypes(types);
            grouplist.add(materials);
        }
        materialsData.setMaterialsData(grouplist);
        rsp.setMaterials(materialsData);
        return rsp;
    }


}
