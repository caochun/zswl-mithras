package cn.zswltech.mithras.dto.capital;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/18/13:53
 * @description
 */
@Data
public class BankFlowProcessingCenterListREQ extends PageReq {

    /**
     * id
     */
    private Long id;

    private List<Long> financingFlowIdList;

    /**
     * tab 类型枚举 name
     */
    @NotBlank(message = "tab类型不能为空")
    private String tabType;

    /**
     * 交易明细编号
     */
    private String transactionDetailsNumber;

    /**
     * 对方户名
     */
    private String otherName;

    /**
     * 交易时间开始
     */
    private String transactionDateFrom;

    /**
     * 交易时间结束
     */
    private String transactionDateTo;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 收付款类型
     */
    private String collectionPaymentType;

    /**
     * 对方账号
     */
    private String otherBankAccount;

    /**
     * 对方开户行
     */
    private String otherBankName;


    /**
     * 摘要
     */
    private String mainInfo;
}
