package cn.zswltech.mithras.workbench.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementAddReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementDetailRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementListReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementModifyReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementSingleReq;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.workbench.application.convert.WorkbenchAnnouncementConverter;
import cn.zswltech.mithras.workbench.application.port.WorkbenchAnnouncementMaterialPort;
import cn.zswltech.mithras.workbench.mapper.WorkbenchAnnouncementMapper;
import cn.zswltech.mithras.workbench.mapper.model.WorkbenchAnnouncement;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private WorkbenchAnnouncementMaterialPort materialPort;

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
        Map<Long, List<WorkbenchAnnouncementMaterial>> imageMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(belongIds)) {
            imageMap = materialPort.listImages(belongIds).stream()
                    .collect(Collectors.groupingBy(WorkbenchAnnouncementMaterial::getBelongId));
        }
        Map<Long, List<WorkbenchAnnouncementMaterial>> finalImageMap = imageMap;
        List<WorkbenchAnnouncementListRsp> rspList = page.getRecords().stream().map(workbenchAnnouncement -> {
            WorkbenchAnnouncementListRsp rsp = baseConverter.entity2ListRsp(workbenchAnnouncement);
            List<WorkbenchAnnouncementMaterial> materials = finalImageMap.get(rsp.getId());
            if (ObjectUtil.isNotEmpty(materials)) {
                List<WorkbenchAnnouncementMaterial> sortedList = materials.stream()
                        .sorted(Comparator.comparing(WorkbenchAnnouncementMaterial::getId))
                        .collect(Collectors.toList());
                rsp.setImage(materialPort.getPreviewUrl(sortedList.get(0).getOssFilename(), 86400));
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
        materialPort.removeImages(req.getId());
        baseMapper.deleteById(req.getId());
    }

    public WorkbenchAnnouncementDetailRsp detail(WorkbenchAnnouncementSingleReq req) {
        WorkbenchAnnouncement byId = getById(req.getId());
        if (ObjectUtil.isEmpty(byId)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        WorkbenchAnnouncementDetailRsp rsp = baseConverter.entity2DetailRsp(byId);
        List<WorkbenchAnnouncementDetailRsp.ImageFileRsp> imageFileRsps = new ArrayList<>();
        for (WorkbenchAnnouncementMaterial material : materialPort.listImages(req.getId())) {
            WorkbenchAnnouncementDetailRsp.ImageFileRsp imageFileRsp = new WorkbenchAnnouncementDetailRsp.ImageFileRsp();
            imageFileRsp.setId(material.getId());
            imageFileRsp.setPreUrl(material.getPreviewUrl());
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
