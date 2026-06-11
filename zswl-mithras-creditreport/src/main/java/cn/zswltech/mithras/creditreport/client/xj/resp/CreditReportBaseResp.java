package cn.zswltech.mithras.creditreport.client.xj.resp;

import lombok.Data;

/**
 * @ClassName CreditReportBaseReq
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportBaseResp {
    //结果状态 0 成功, 1失败
    private Integer status;

    //结果状态码
    private String statuscode;

    //出错原因
    private String error;
}
