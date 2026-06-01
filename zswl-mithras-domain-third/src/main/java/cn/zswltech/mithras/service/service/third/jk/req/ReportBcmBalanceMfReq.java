package cn.zswltech.mithras.service.service.third.jk.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportBcmBalanceMfReq extends JinKongBasicReq {
    private String org_code;
    private String dt;
    private String year_period;
    private Integer page_size;
    private Integer page_num;
}
