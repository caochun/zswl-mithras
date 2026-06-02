package cn.zswltech.mithras.third.service.jk.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportBcmFflexfiledAssistMfReq extends JinKongBasicReq {
    private Integer page_size;
    private Integer page_num;
}
