package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;

//征信报告解析接口
public interface CreditReportParseInterface {
    void execute(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId);

    void clear(Long creditReportClientId);
}
