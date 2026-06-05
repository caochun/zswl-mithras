package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportAddCmd;
import cn.zswltech.mithras.dto.creditreport.CreditReportClientAddDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportDetailDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportSaveCmd;
import cn.zswltech.mithras.dto.creditreport.CreditReportSubmitCmd;
import cn.zswltech.mithras.dto.creditreport.CreditReportSubmitDTO;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportBaseInfo;

import javax.servlet.ServletOutputStream;
import java.util.List;

public interface CreditReportQueryService {

    void add(CreditReportAddCmd req);

    void modify(CreditReportSaveCmd req);

    PageR<CreditReportListDTO> list(CreditReportListREQ req);

    void remove(Long id);

    CreditReportDetailDTO detail(Long id);

    CreditReportBaseInfo getById(Long id);

    List<CreditReportSubmitDTO> submit(CreditReportSubmitCmd cmd);

    List<ClientInfo> getClientInfo(String clientName, Long creditReportId);

    CreditReportClientAddDTO showCreditReportByClientId(Long clientId);

    void export(ServletOutputStream outputStream, CreditReportListREQ req);
}
