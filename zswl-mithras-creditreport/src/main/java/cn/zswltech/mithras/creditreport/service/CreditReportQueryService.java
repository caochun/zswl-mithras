package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListREQ;

public interface CreditReportQueryService {

    PageR<CreditReportListDTO> list(CreditReportListREQ req);

    void remove(Long id);
}
