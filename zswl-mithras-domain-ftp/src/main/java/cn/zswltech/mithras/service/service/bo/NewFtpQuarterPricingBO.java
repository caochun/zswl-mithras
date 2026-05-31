package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/11/20
 * @description
 */
@Data
public class NewFtpQuarterPricingBO {
    private LocalDate targetDate;
    private Long ftpId;
    private List<NewFtpMonthlyGuidanceDraft> ftpMonthlyGuidanceList;
}
