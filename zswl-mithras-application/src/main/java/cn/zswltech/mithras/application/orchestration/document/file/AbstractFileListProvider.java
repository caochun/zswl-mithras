package cn.zswltech.mithras.application.orchestration.document.file;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.application.orchestration.document.convert.FileConvert;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.document.enums.FileTypeEnum;
import cn.zswltech.mithras.document.enums.ImageTypeEnum;
import cn.zswltech.mithras.document.enums.PreviewTypeEnum;
import cn.zswltech.mithras.document.enums.VideoTypeEnum;
import cn.zswltech.mithras.document.file.bo.FileListExtQuery;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.document.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.document.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.client.authority.ClientAuthorityUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件列表提供
 *
 * @author wangchuanhao
 * @date 2023/2/6 10:49 AM
 */
public abstract class AbstractFileListProvider {

    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    protected MaterialsListLibService materialsListLibService;
    @Resource
    protected Id2NameService id2NameService;
    @Resource
    protected FileConvert fileConvert;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientAuthorityUtil authorityUtil;

    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        return listGroup(req, new FileListExtQuery());
    }

    /**
     * 给分组查询列表
     *
     * @param req
     * @param extQuery
     * @return
     */
    protected List<MaterialsList> listForGroup(FileListREQ req, FileListExtQuery extQuery) {
        List<MaterialsList> dataList = null;
        if (StringUtils.isBlank(req.getVersion())) {
            dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, getBusinessModule().name())
                    .eq(MaterialsList::getBelongId, req.getMainId())
                    .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsList::getMaterialsType, req.getMaterialsTypes())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
                    .eq(extQuery.getClientId() != null, MaterialsList::getSourceBusinessKey, extQuery.getClientId())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsSubTypes()), MaterialsList::getMaterialSubType, extQuery.getMaterialsSubTypes())
            );
        } else {
            dataList = materialsListLibService.getBaseMapper().selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getVersion, req.getVersion())
                    .eq(MaterialsListLib::getBusinessType, getBusinessModule().name())
                    .eq(MaterialsListLib::getBelongId, req.getMainId())
                    .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsListLib::getMaterialsType, req.getMaterialsTypes())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
                    .eq(extQuery.getClientId() != null, MaterialsList::getSourceBusinessKey, extQuery.getClientId())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsSubTypes()), MaterialsList::getMaterialSubType, extQuery.getMaterialsSubTypes())
            ).stream().map(fileConvert::actualLib2Entity).collect(Collectors.toList());
        }
        // 隐藏其他业务模块上传的归属于该客户的资料
        if (CollectionUtil.isNotEmpty(dataList) && StrUtil.equals(getBusinessModule().name(), BusinessModuleEnum.CLIENT.name())) {
            dataList.removeIf(e -> StrUtil.isNotBlank(e.getSourceBusinessKey()));
        }
        return dataList;
    }

    protected List<MaterialsList> listForClientGroup(FileListREQ req, FileListExtQuery extQuery) {
        Long userId = null;
        if (req.getExt() != null && req.getExt().containsKey("processInstanceId")) {
            Map<String, Object> ext = req.getExt();
            String processInstanceId = (String) ext.get("processInstanceId");
            cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = getClientProcessByIds(processInstanceId);
            if (processRespPage == null || processRespPage.getContents().isEmpty()) {
                return new ArrayList<>();
            }
            userId = Long.valueOf(processRespPage.getContents().get(0).getStartUserId());
        } else {
            if (!authorityUtil.isIntraGroupCollaboration(req.getMainId())) {
                userId = authorityUtil.ensureNoProcessViewWhichUserData(req.getMainId());
                if (Objects.isNull(userId)) {
                    // 说明没有可看的数据
                    return new ArrayList<>();
                }
            }
        }
        List<MaterialsList> dataList = null;
        if (StringUtils.isBlank(req.getVersion())) {
            dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, getBusinessModule().name())
                    .eq(MaterialsList::getBelongId, req.getMainId())
                    .eq(userId != null, MaterialsList::getCreateBy, userId)
                    .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsList::getMaterialsType, req.getMaterialsTypes())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
                    .eq(extQuery.getClientId() != null, MaterialsList::getSourceBusinessKey, extQuery.getClientId())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsSubTypes()), MaterialsList::getMaterialSubType, extQuery.getMaterialsSubTypes())
            );
        } else {
            dataList = materialsListLibService.getBaseMapper().selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getVersion, req.getVersion())
                    .eq(MaterialsListLib::getBusinessType, getBusinessModule().name())
                    .eq(MaterialsListLib::getBelongId, req.getMainId())
                    .eq(userId != null, MaterialsList::getCreateBy, userId)
                    .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsListLib::getMaterialsType, req.getMaterialsTypes())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
                    .eq(extQuery.getClientId() != null, MaterialsList::getSourceBusinessKey, extQuery.getClientId())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsSubTypes()), MaterialsList::getMaterialSubType, extQuery.getMaterialsSubTypes())
            ).stream().map(fileConvert::actualLib2Entity).collect(Collectors.toList());
        }
        // 隐藏其他业务模块上传的归属于该客户的资料
        if (CollectionUtil.isNotEmpty(dataList)) {
            dataList.removeIf(e -> StrUtil.isNotBlank(e.getSourceBusinessKey()));
        }
        return dataList;
    }

    public cn.zswltech.flow.core.util.Page<ProcessResp> getClientProcessByIds(String processInstanceId) {
        ProcessPageReq processReq = new ProcessPageReq();
        List<String> modelKeyList = ListUtil.toList(ProcessModelTypeEnum.ClientModifyFlow.name(), ProcessModelTypeEnum.ClientAuthorityCreateFlow.name(), ProcessModelTypeEnum.ClientAuthorityModifyFlow.name());
        processReq.setModelKeyList(modelKeyList);
        processReq.setProcessInstanceIdList(ListUtil.toList(processInstanceId));
        processReq.setPageIndex(1);
        processReq.setPageSize(Integer.MAX_VALUE);
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processReq);
        return processRespPage;
    }


    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req, FileListExtQuery extQuery) {
        List<MaterialsList> dataList = null;
        if ("CLIENT".equalsIgnoreCase(req.getModuleType())) {
            Client client = clientMapper.selectById(req.getMainId());
            if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
                // 自然人走老的
                dataList = listForGroup(req, extQuery);
            } else {
                dataList = listForClientGroup(req, extQuery);
            }
        } else {
            dataList = listForGroup(req, extQuery);
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
        fileConvert.fillName(rspList);
        rspList.sort(new CommonFileSortComparator());
        List<Pair<String, List<FileListRSP>>> resList = new ArrayList<>();
        if (BusinessModuleEnum.CREDIT_REPORT_SELECT.name().equals(req.getModuleType())) {
            if(CollectionUtils.isEmpty(rspList)) {
                return resList;
            }
            List<List<FileListRSP>> groupRspList = rspList.stream()
                    .collect(Collectors.groupingBy(FileListRSP::getMaterialSubType))
                    .values().stream()
                    .collect(Collectors.toList());
            // 分组排序
            sortGroup(groupRspList);
            for (List<FileListRSP> groupRsp : groupRspList) {
                resList.add(new Pair<>(groupRsp.get(0).getMaterialSubType(), groupRsp));
            }
        } else {
            List<List<FileListRSP>> groupRspList = rspList.stream()
                    .collect(Collectors.groupingBy(FileListRSP::getMaterialsType))
                    .values().stream()
                    .collect(Collectors.toList());
            // 分组排序
            sortGroup(groupRspList);
            for (List<FileListRSP> groupRsp : groupRspList) {
                {
                    resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
                }

            }
        }

        return resList;
    }

    public PageR<FileListRSP> list(FileListREQ req) {
        return list(req, new FileListExtQuery());
    }

    public PageR<FileListRSP> list(FileListREQ req, FileListExtQuery extQuery) {
        PageR<FileListRSP> dataPage = null;
        List<Long> fileIdList = null;
        if (StringUtils.isBlank(req.getVersion())) {
            if (req.getExt() != null) {
                Map<String, Object> ext = req.getExt();
                fileIdList = (List<Long>) ext.get("fileId");
            }
            Page<MaterialsList> pageData = materialsListService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<MaterialsList>lambdaQuery()
                            .eq(MaterialsList::getBusinessType, getBusinessModule().name())
                            .eq(MaterialsList::getBelongId, req.getMainId())
                            .eq(StrUtil.isNotBlank(req.getMaterialsType()), MaterialsList::getMaterialsType, req.getMaterialsType())
                            .in(CollectionUtils.isNotEmpty(fileIdList), MaterialsList::getId, fileIdList)
                            .eq(!Objects.isNull(req.getUserId()), MaterialsList::getCreateBy, req.getUserId())
//                            .and(innerQuery -> innerQuery.eq(MaterialsList::getSourceBusinessKey, "").or().isNull(MaterialsList::getSourceBusinessKey))
                            .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsList::getMaterialsType, req.getMaterialsTypes())
                            .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
            );
            if (CollectionUtils.isEmpty(pageData.getRecords())) {
                return PageR.of(new ArrayList<>(), pageData.getTotal(), pageData.getPages(), pageData.getCurrent(), pageData.getSize());
            }
            List<FileListRSP> resList = pageData.getRecords().stream().map(e -> {
                FileListRSP rsp = fileConvert.entity2RSP(e);
                if (Objects.nonNull(req.getNeedPreviewUrl()) && req.getNeedPreviewUrl()) {
                    FileTypeEnum fileTypeEnum = FileTypeEnum.getByExName(e.getSuffix());
                    if (fileTypeEnum == FileTypeEnum.UNKNOWN) {
                        ImageTypeEnum imageTypeEnum = ImageTypeEnum.getByExName(e.getSuffix());
                        if (imageTypeEnum == ImageTypeEnum.UNKNOWN) {
                            VideoTypeEnum videoTypeEnum = VideoTypeEnum.getByExName(e.getSuffix());
                            if (videoTypeEnum == VideoTypeEnum.UNKNOWN) {
                                rsp.setPreviewType(PreviewTypeEnum.UNKNOWN.name());
                            } else {
                                rsp.setPreviewType(PreviewTypeEnum.VIDEO.name());
                                rsp.setPreviewUrl(materialsListService.getPreviewUrl(e.getOssFilename(), 60 * 60 * 24));
                            }
                        } else {
                            rsp.setPreviewType(PreviewTypeEnum.IMAGE.name());
                            rsp.setPreviewUrl(materialsListService.getPreviewUrl(e.getOssFilename(), 60 * 60 * 24));
                        }
                    } else {
                        rsp.setPreviewType(PreviewTypeEnum.ONLYOFFICE.name());
                    }
                }
                return rsp;
            }).sorted(new CommonFileSortComparator()).collect(Collectors.toList());
            dataPage = PageR.of(resList, pageData.getTotal(), pageData.getPages(), pageData.getCurrent(), pageData.getSize());
        } else {
            Page<MaterialsListLib> pageData = materialsListLibService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<MaterialsListLib>lambdaQuery()
                            .eq(MaterialsListLib::getVersion, req.getVersion())
                            .eq(MaterialsListLib::getBusinessType, getBusinessModule().name())
                            .eq(MaterialsListLib::getBelongId, req.getMainId())
                            .eq(StrUtil.isNotBlank(req.getMaterialsType()), MaterialsList::getMaterialsType, req.getMaterialsType())
                            .in(CollectionUtils.isNotEmpty(fileIdList), MaterialsListLib::getOriginId, fileIdList)
                            .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsListLib::getMaterialsType, req.getMaterialsTypes())
                            .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
            );
            if (CollectionUtils.isEmpty(pageData.getRecords())) {
                return PageR.of(new ArrayList<>(), pageData.getTotal(), pageData.getPages(), pageData.getCurrent(), pageData.getSize());
            }
            List<FileListRSP> resList = pageData.getRecords().stream().map(fileConvert::actualLib2Entity).map(fileConvert::entity2RSP).collect(Collectors.toList());
            resList.sort(new CommonFileSortComparator());
            dataPage = PageR.of(resList, pageData.getTotal(), pageData.getPages(), pageData.getCurrent(), pageData.getSize());
        }
        fileConvert.fillName(dataPage.getList());
        return dataPage;
    }

    public abstract BusinessModuleEnum getBusinessModule();

    /**
     * 获取文件分组顺序
     *
     * @param rsp
     * @return
     */
    protected int getGroupFileSort(FileListRSP rsp) {
        return 0;
    }

    /**
     * 文件分组排序
     *
     * @param groupRspList
     */
    protected void sortGroup(List<List<FileListRSP>> groupRspList) {
        groupRspList.sort(Comparator.comparing(rsp -> getGroupFileSort(rsp.get(0))));
    }

    public void externalSortGroup(List<List<FileListRSP>> groupRspList){
        sortGroup(groupRspList);
    }

}
