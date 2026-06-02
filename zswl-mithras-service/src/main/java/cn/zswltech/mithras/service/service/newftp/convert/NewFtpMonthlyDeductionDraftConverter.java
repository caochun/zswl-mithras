package cn.zswltech.mithras.service.service.newftp.convert;

import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionModifyREQ;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyDeductionDraft;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 月度计价指导
 * @date 2023-05-21
 */
@Mapper(componentModel = "spring")
public interface NewFtpMonthlyDeductionDraftConverter {

    List<NewFtpMonthlyDeductionListRSP> entity2ListRsp(List<NewFtpMonthlyDeductionDraft> list);

    NewFtpMonthlyDeductionDraft modifyReq2Entity(NewFtpMonthlyDeductionModifyREQ req);
}
