package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.service.resp.CreditReportObtainResultPDFResp;
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