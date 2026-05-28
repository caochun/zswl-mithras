package cn.zswltech.mithras.service.service.creditreport;

import cn.zswltech.mithras.service.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.service.service.creditreport.resp.CreditReportObtainResultPDFResp;
import org.springframework.stereotype.Service;

/**
* @description 三方接口对接
* @author vico
* @date 2025-11-14
*/
@Service
public interface CreditReportApiService {

    String addArchive(Long creditReportId);

    String queryReport(Long creditReportId);

    XJCreditReportJsonDTO resultJSON(Long creditReportId);

    CreditReportObtainResultPDFResp resultPDF(Long creditReportId);

}