package cn.zswltech.mithras.service.service.newftp.convert;

import cn.zswltech.mithras.dto.newftp.NewFtpBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpBaseInfoListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpDescriptionTextListRsp;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpDescriptionTextDraft;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description ftp主表
 * @date 2023-05-21
 */
@Mapper(componentModel = "spring")
public interface NewFtpBaseInfoConverter {

    List<NewFtpBaseInfoListRSP> entity2ListRsp(List<NewFtpBaseInfo> records);

    NewFtpBaseInfoListRSP entity2ListRsp(NewFtpBaseInfo record);


    List<NewFtpDescriptionTextListRsp> descEntity2ListRsp(List<NewFtpDescriptionTextDraft> entities);

    NewFtpDescriptionTextListRsp descEntity2ListRsp(NewFtpDescriptionTextDraft entity);

    NewFtpBaseInfoDetailRSP entity2DetailRsp(NewFtpBaseInfo byId);
}
