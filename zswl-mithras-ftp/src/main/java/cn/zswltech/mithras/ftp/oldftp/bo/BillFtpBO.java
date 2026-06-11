package cn.zswltech.mithras.ftp.oldftp.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2023/5/30
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillFtpBO {
    // 卖出价
    private Integer billFtpSell;

    // 买入价
    private String billFtpBuy;
}
