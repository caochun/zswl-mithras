package cn.zswltech.mithras.dto.capital;

import lombok.Data;

/**x
 * @author yangxiong
 * @date 2024/5/19/11:06
 * @description
 */
@Data
public class BankFlowProcessingCenterReceiptListRSP {

    /**
     * 借据ID
     */
    private Long receiptId;

    /**
     * 借据编号
     */
    private String receiptCode;
}
