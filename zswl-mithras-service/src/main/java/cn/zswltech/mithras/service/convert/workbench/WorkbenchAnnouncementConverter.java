package cn.zswltech.mithras.service.convert.workbench;

import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementAddReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementDetailRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchAnnouncementModifyReq;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchAnnouncement;
import org.mapstruct.Mapper;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Mapper(componentModel = "spring")
public interface WorkbenchAnnouncementConverter {

    WorkbenchAnnouncement addReq2Entity(WorkbenchAnnouncementAddReq req);

    WorkbenchAnnouncement modifyReq2Entity(WorkbenchAnnouncementModifyReq req);

    WorkbenchAnnouncementListRsp entity2ListRsp(WorkbenchAnnouncement entity);

    WorkbenchAnnouncementDetailRsp entity2DetailRsp(WorkbenchAnnouncement byId);
    
}
