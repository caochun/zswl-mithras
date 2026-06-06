package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.service.resp.CreditReportObtainResultPDFResp;

import java.util.List;

public interface CreditReportResultApplicationService {

    boolean importByXJCreditReportJsonDTO(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportBaseId, Long creditReportClientId);

    boolean importByXJCreditReportObtainResultPDFResp(CreditReportObtainResultPDFResp creditReportObtainResultPDFResp, Long creditClientId);

    void modifyClientItemStatus(List<Long> clientItemIds, String selectStatus);
}
