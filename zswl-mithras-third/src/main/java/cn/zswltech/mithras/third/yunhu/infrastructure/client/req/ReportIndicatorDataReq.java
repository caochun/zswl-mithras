package cn.zswltech.mithras.third.yunhu.infrastructure.client.req;

import cn.zswltech.mithras.third.jinkong.infrastructure.client.req.JinKongBasicReq;
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
