package cn.zswltech.mithras.creditreport.service.resp;

import cn.zswltech.mithras.creditreport.service.req.CreditReportBaseReq;
import lombok.Data;

/**
 * @ClassName CreditReportBaseReq
 * @Description 4.1新增档案信息
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportObtainResultJSONResp extends CreditReportBaseResp {

    //结构化数据JSON
    private String json;

}
