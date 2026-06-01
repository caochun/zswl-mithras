package cn.zswltech.mithras.service.service.third.yunhu.req;

import cn.zswltech.mithras.service.service.third.jk.req.JinKongBasicReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/10/9
 * @description 国资快报请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportIndicatorDataReq extends JinKongBasicReq {
    private Integer page_size;
    private Integer page_num;
    private String dt;
    private String org_number;
}
