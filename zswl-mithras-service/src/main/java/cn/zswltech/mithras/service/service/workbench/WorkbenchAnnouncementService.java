package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.workbench.application.convert.WorkbenchAnnouncementConverter;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.workbench.domain.enums.AnnouncementMetricMaterialEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchAnnouncement;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchAnnouncementMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Service
public class WorkbenchAnnouncementService extends ServiceImpl<WorkbenchAnnouncementMapper, WorkbenchAnnouncement> {
    @Resource
    private WorkbenchAnnouncementConverter baseConverter;
    @Resource
    private MaterialsListService materialsListService;

    @Transactional(rollbackFor = Throwable.class)
    public Long add(WorkbenchAnnouncementAddReq req) {
        WorkbenchAnnouncement info = baseConverter.addReq2Entity(req);
        baseMapper.insert(info);
        return info.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(WorkbenchAnnouncementModifyReq req) {
        WorkbenchAnnouncement originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        WorkbenchAnnouncement info = baseConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
    }

    public PageR<WorkbenchAnnouncementListRsp> list(WorkbenchAnnouncementListReq req) {
        LocalDate now = LocalDate.now();
        Page<WorkbenchAnnouncement> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<WorkbenchAnnouncement>lambdaQuery().orderByDesc(WorkbenchAnnouncement::getTop)
                        .le(YesOrNoNumberEnum.YES.getCode().equals(LongUtil.null2zero(req.getExceptionFlag())), WorkbenchAnnouncement::getExpirationFrom, now)
                        .ge(YesOrNoNumberEnum.YES.getCode().equals(LongUtil.null2zero(req.getExceptionFlag())), WorkbenchAnnouncement::getExpirationTo, now)
                        .orderByDesc(BaseModel::getUpdateTime));
        Set<Long> belongIds = page.getRecords().stream().map(WorkbenchAnnouncement::getId)
                .collect(Collectors.toSet());
        Map<Long, List<MaterialsList>> imageMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(belongIds)) {
            imageMap = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                            .in(MaterialsList::getBelongId, belongIds)
                            .eq(MaterialsList::getBusinessType, AnnouncementMetricMaterialEnum.IMAGE.businessModule())
                            .eq(MaterialsList::getMaterialsType, AnnouncementMetricMaterialEnum.IMAGE.name())).stream()
                    .collect(Collectors.groupingBy(MaterialsList::getBelongId));
        }
        Map<Long, List<MaterialsList>> finalImageMap = imageMap;
        List<WorkbenchAnnouncementListRsp> rspList = page.getRecords().stream().map(workbenchAnnouncement -> {
            WorkbenchAnnouncementListRsp rsp = baseConverter.entity2ListRsp(workbenchAnnouncement);
            List<MaterialsList> materialsLists = finalImageMap.get(rsp.getId());
            if (ObjectUtil.isNotEmpty(materialsLists)) {
                List<MaterialsList> sortedList = materialsLists.stream().sorted(Comparator.comparing(MaterialsList::getId))
                        .collect(Collectors.toList());
                rsp.setImage(materialsListService.getPreviewUrl(sortedList.get(0).getOssFilename(), 86400));
            }
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(page, rspList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(WorkbenchAnnouncementSingleReq req) {
        WorkbenchAnnouncement originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, req.getId())
                .eq(MaterialsList::getBusinessType, AnnouncementMetricMaterialEnum.IMAGE.businessModule())
                .eq(MaterialsList::getMaterialsType, AnnouncementMetricMaterialEnum.IMAGE.name()));
        baseMapper.deleteById(req.getId());
    }

    public WorkbenchAnnouncementDetailRsp detail(WorkbenchAnnouncementSingleReq req) {
        WorkbenchAnnouncement byId = getById(req.getId());
        if (ObjectUtil.isEmpty(byId)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        WorkbenchAnnouncementDetailRsp rsp = baseConverter.entity2DetailRsp(byId);
        List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, req.getId())
                .eq(MaterialsList::getBusinessType, AnnouncementMetricMaterialEnum.IMAGE.businessModule())
                .eq(MaterialsList::getMaterialsType, AnnouncementMetricMaterialEnum.IMAGE.name()));
        List<WorkbenchAnnouncementDetailRsp.ImageFileRsp> imageFileRsps = new ArrayList<>();
        for (MaterialsList material : materialsLists) {
            WorkbenchAnnouncementDetailRsp.ImageFileRsp imageFileRsp = new WorkbenchAnnouncementDetailRsp.ImageFileRsp();
            imageFileRsp.setId(material.getId());
            imageFileRsp.setPreUrl(materialsListService.getPreviewUrl(material.getOssFilename(), 86400));
            imageFileRsps.add(imageFileRsp);
        }
        rsp.setImages(imageFileRsps);
        return rsp;
    }

    public void top(WorkbenchAnnouncementSingleReq req) {
        WorkbenchAnnouncement originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (originalInfo.getTop() == null || originalInfo.getTop() == 0) {
            originalInfo.setTop(1);
        } else {
            originalInfo.setTop(0);
        }
        baseMapper.updateById(originalInfo);
    }
}