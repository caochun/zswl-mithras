package cn.zswltech.mithras.third.jinkong.infrastructure.client.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportBcmAssetMfReq extends JinKongBasicReq {
    private Integer page_size;
    private Integer page_num;
    private String fyear;
    private String fperiod;
    private String forgnumber;
}
