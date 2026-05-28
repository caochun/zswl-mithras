package cn.zswltech.mithras.service.service.creditreport;

import cn.zswltech.mithras.service.mapper.dto.credit.XJCreditReportJsonDTO;

//征信报告解析接口
public interface CreditReportParseInterface {
    void execute(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId);

    void clear(Long creditReportClientId);
}
