package cn.zswltech.mithras.archives.application;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.archives.ArchiveTemplateAddREQ;
import cn.zswltech.mithras.dto.archives.ArchiveTemplateInfoREQ;
import cn.zswltech.mithras.dto.archives.ArchiveTemplateInfoRSP;
import cn.zswltech.mithras.dto.archives.ArchiveTemplateListREQ;
import cn.zswltech.mithras.dto.archives.ArchiveTemplateListRSP;
import cn.zswltech.mithras.dto.archives.ArchiveTemplateUpdateREQ;
import cn.zswltech.mithras.archives.enums.ArchiveTemplateStatusEnum;
import cn.zswltech.mithras.archives.persistence.mapper.ArchiveTemplateMapper;
import cn.zswltech.mithras.archives.persistence.mapper.ArchiveTypeGroupMapper;
import cn.zswltech.mithras.archives.persistence.model.ArchiveFileType;
import cn.zswltech.mithras.archives.persistence.model.ArchiveTemplate;
import cn.zswltech.mithras.archives.persistence.model.ArchiveTypeGroup;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wwj
 * @create: 2023-02-27
 **/
@Slf4j
@Service
public class ArchiveTemplateService {

    @Resource
    private ArchiveTemplateMapper archiveTemplateMapper;

    @Resource
    private ArchiveTypeGroupMapper archiveTypeGroupMapper;

    @Resource
    private ArchiveFileTypeService archiveFileTypeService;

    public PageR<ArchiveTemplateListRSP> list(ArchiveTemplateListREQ req){
        Page<ArchiveTemplate> pageList = archiveTemplateMapper.pageList(new Page<>(req.getPage(), req.getPageSize()), req);
        List<ArchiveTemplateListRSP> rsps = new ArrayList<>();
        for (ArchiveTemplate archiveTemplate : pageList.getRecords()){
            ArchiveTemplateListRSP rsp = new ArchiveTemplateListRSP();
            rsp.setTemplateId(archiveTemplate.getId());
            rsp.setTemplateName(archiveTemplate.getTemplateName());
            rsp.setBizType(ListUtil.of(archiveTemplate.getTemplateType().split(",")));
            rsp.setStatus(archiveTemplate.getStatus());
            rsp.setCreateTime(archiveTemplate.getCreateTime());
            rsp.setUpdateTime(archiveTemplate.getUpdateTime());
            rsps.add(rsp);
        }
        return PageR.of(rsps, pageList.getTotal(),
                pageList.getPages(),
                pageList.getCurrent(),
                pageList.getSize());

    }

    @Transactional(rollbackFor = Exception.class)
    public Void addTemplate(ArchiveTemplateAddREQ req){
        checkNameOrType(req.getTemplateName(),ArchiveTemplateStatusEnum.ENABLE.name().equals(req.getStatus())?req.getBizType() : null);
        List<ArchiveTemplateAddREQ.Group> groups = req.getGroups();
        if (CollectionUtil.isEmpty(groups)){
            throw new MithrasException("无效模版");
        }
        Set<String> stringSet = new HashSet<>();
        for (ArchiveTemplateAddREQ.Group group : groups) {
            if (stringSet.contains(group.getGroupName())){
                throw new MithrasException("重复资料类型："+group.getGroupName());
            }
            stringSet.add(group.getGroupName());
            List<ArchiveTemplateAddREQ.Item> items = group.getItems();
            if (CollectionUtil.isEmpty(items)){
                throw new MithrasException("无效资料类型："+group.getGroupName());
            }
            Set<String> fileSet = new HashSet<>();
            for (ArchiveTemplateAddREQ.Item item : items) {
                if (fileSet.contains(item.getFileType())){
                    throw new MithrasException("重复文件类型："+item.getFileType());
                }
                fileSet.add(item.getFileType());
            }
        }

        ArchiveTemplate template = new ArchiveTemplate();
        template.setTemplateName(req.getTemplateName());
        template.setTemplateType(String.join(",",req.getBizType()));
        template.setStatus(req.getStatus());
        archiveTemplateMapper.insert(template);
        int sortgroup = 0;
        for (ArchiveTemplateAddREQ.Group group : groups){
            ArchiveTypeGroup typeGroup = new ArchiveTypeGroup();
            typeGroup.setGroupName(group.getGroupName());
            typeGroup.setTemplateId(template.getId());
            typeGroup.setSort(++sortgroup);
            archiveTypeGroupMapper.insert(typeGroup);
            List<ArchiveFileType> fileTypes = new ArrayList<>();
            int sorttype = 0;
            for (ArchiveTemplateAddREQ.Item item : group.getItems()){
                ArchiveFileType fileType = new ArchiveFileType();
                fileType.setTypeName(item.getFileType());
                fileType.setNeed(item.getNeed());
                fileType.setGroupId(typeGroup.getId());
                fileType.setSort(++sorttype);
                fileTypes.add(fileType);
            }
            if (CollectionUtil.isNotEmpty(fileTypes)) {
                archiveFileTypeService.saveBatch(fileTypes);
            }
        }
        return null;
    }

    public ArchiveTemplateInfoRSP info(ArchiveTemplateInfoREQ req) {
        ArchiveTemplate template = archiveTemplateMapper.selectById(req.getTemplateId());
        if (template == null){
            throw new MithrasException("无效模版！");
        }
        ArchiveTemplateInfoRSP rsp = new ArchiveTemplateInfoRSP();
        rsp.setTemplateId(template.getId());
        rsp.setTemplateName(template.getTemplateName());
        rsp.setStatus(template.getStatus());
        rsp.setBizType(ListUtil.toList(template.getTemplateType().split(",")));
        List<ArchiveTypeGroup> archiveTypeGroups = archiveTypeGroupMapper.selectList(Wrappers.<ArchiveTypeGroup>lambdaQuery().eq(ArchiveTypeGroup::getTemplateId, req.getTemplateId()).orderByAsc(ArchiveTypeGroup::getSort));
        if (CollectionUtil.isEmpty(archiveTypeGroups)){
            return rsp;
        }
        List<Long> groupIds = archiveTypeGroups.stream().map(ArchiveTypeGroup::getId).collect(Collectors.toList());
        List<ArchiveFileType> fileTypes = archiveFileTypeService.getBaseMapper().selectList(Wrappers.<ArchiveFileType>lambdaQuery().in(ArchiveFileType::getGroupId, groupIds).orderByAsc(ArchiveFileType::getGroupId,ArchiveFileType::getSort));
        Map<Long, List<ArchiveFileType>> map = fileTypes.stream().collect(Collectors.groupingBy(ArchiveFileType::getGroupId));
        List<ArchiveTemplateInfoRSP.Group> groupList = new ArrayList<>();
        for (ArchiveTypeGroup group : archiveTypeGroups){
            ArchiveTemplateInfoRSP.Group aGroup = new ArchiveTemplateInfoRSP.Group();
            aGroup.setGroupId(group.getId());
            aGroup.setGroupName(group.getGroupName());
            List<ArchiveTemplateInfoRSP.Item> items = new ArrayList<>();
            for (ArchiveFileType fileType : map.get(group.getId())){
                ArchiveTemplateInfoRSP.Item item = new ArchiveTemplateInfoRSP.Item();
                item.setFileTypeId(fileType.getId());
                item.setFileType(fileType.getTypeName());
                item.setNeed(fileType.getNeed());
                items.add(item);
            }
            aGroup.setItems(items);
            groupList.add(aGroup);
        }
        rsp.setGroups(groupList);
        return rsp;
    }

    @Transactional(rollbackFor = Exception.class)
    public Void updateStatus(ArchiveTemplateUpdateREQ req){
        checkNameOrType(null,ArchiveTemplateStatusEnum.ENABLE.name().equals(req.getStatus())?req.getBizType() : null);
        ArchiveTemplate template = new ArchiveTemplate();
        template.setId(req.getTemplateId());
        template.setStatus(req.getStatus());
        archiveTemplateMapper.updateById(template);
        return null;
    }

    public void checkNameOrType(String name,List<String> bizType) {
        if (StrUtil.isNotEmpty(name)) {
            ArchiveTemplate archiveTemplate = archiveTemplateMapper.selectOne(Wrappers.<ArchiveTemplate>lambdaQuery().eq(StrUtil.isNotEmpty(name), ArchiveTemplate::getTemplateName, name).last(StringUtil.mysqlLimitOne()));
            if (archiveTemplate != null) {
                throw new MithrasException("该模版名称已存在，请修改后保存。");
            }
        }
        if (CollectionUtil.isNotEmpty(bizType)) {
            QueryWrapper<ArchiveTemplate> objectQueryWrapper = new QueryWrapper<>();
            for (String s : bizType) {
                objectQueryWrapper.apply("FIND_IN_SET(\"" + s + "\"," + "template_type)").or();
            }
            List<ArchiveTemplate> archiveTemplates = archiveTemplateMapper.selectList(objectQueryWrapper);
            if (CollectionUtil.isNotEmpty(archiveTemplates)) {
                List<ArchiveTemplate> enable = archiveTemplates.stream().filter(o -> ArchiveTemplateStatusEnum.ENABLE.name().equals(o.getStatus())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(enable)) {
                    throw new MithrasException("该业务类型已有启用的模版，请前往有关模版进行调整。");
                }
            }
        }
    }
}
