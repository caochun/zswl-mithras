package cn.zswltech.mithras.third.service.financial.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName FinancialCollectionVO
 * @Description 用于传输普通付款至苍穹
 * @Author jackerhe
 * @Date 2023/4/3 1:01 下午
 * @Version 1.0
 **/
@Data
@Accessors(chain = true)
public class FinancialPaymentVO {

    //付款编号
    private String paymentCode;

    //合同编号
    private String contractCode;

    //付款类型（区分保证金退款，付款)
    private String paymentType;

    //申请支付金额
    private Long applyPaymentAmount;

    //申请支付时间
    private LocalDateTime applyPaymentDate;

    //是否包含首期租金
    private Integer isInitialRent;

    //首期租金金额
    private Long initialRentAmount;

    //内扣质保金
    private Long retentionMoney;


}
