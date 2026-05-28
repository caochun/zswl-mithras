package cn.zswltech.mithras.dto.flow.form;

import lombok.Data;

/**
 * @author dingqi
 * @date 2023/9/5
 * @description
 */
@Data
public class SetPaymentFtpFormREQ {
    private Long paymentId;
    private Integer cashFtp;
    private Integer billFtp;
}
