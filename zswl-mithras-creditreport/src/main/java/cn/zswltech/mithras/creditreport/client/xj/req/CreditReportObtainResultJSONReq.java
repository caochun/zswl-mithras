package cn.zswltech.mithras.creditreport.client.xj.req;

import lombok.Data;

/**
 * @ClassName CreditReportBaseReq
 * @Description 4.1新增档案信息
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportObtainResultJSONReq extends CreditReportBaseReq {

    //应用交易流水号
    private String serialnumber;

}
