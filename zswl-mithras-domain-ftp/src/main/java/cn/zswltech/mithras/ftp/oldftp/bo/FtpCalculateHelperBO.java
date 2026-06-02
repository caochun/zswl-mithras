package cn.zswltech.mithras.ftp.oldftp.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2023/5/22
 * @description
 */
@AllArgsConstructor
@Data
public class FtpCalculateHelperBO {
    /**
     * 付款id（不一定有，比如把历史值当作整体进行计算的时候就没有）
     */
    private Long paymentId;

    /**
     * FTP（百分比扩大10000倍）
     */
    private Integer ftp;

    /**
     * 付款
     */
    private Long payAmount;

    /**
     * 收款
     */
    private Long collectAmount;
}
