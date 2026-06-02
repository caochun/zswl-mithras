package cn.zswltech.mithras.ftp.newftp.convert;

import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftModifyREQ;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceExtDraft;
import org.mapstruct.Mapper;

/**
 * @author zhaozhengkang
 * @description ftp指导报价扩展表（下半部分）
 * @date 2023-05-21
 */
@Mapper(componentModel = "spring")
public interface NewFtpMonthlyGuidanceExtDraftConverter {

    NewFtpMonthlyGuidanceExtDraft modifReq2Entity(NewFtpMonthlyGuidanceExtDraftModifyREQ req);

    NewFtpMonthlyGuidanceExtDraftDetailRSP entity2DetailRsp(NewFtpMonthlyGuidanceExtDraft one);
}
