package cn.zswltech.mithras.service.service.creditreport.resp;

import lombok.Data;

/**
 * @ClassName CreditReportBaseReq
 * @Description 单笔查询返回
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportQueryReportResp extends CreditReportBaseResp {

    //应用交易流水号
    private String serialnumber;

}
