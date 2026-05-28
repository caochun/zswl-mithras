package cn.zswltech.mithras.dto.process.prepare;

import cn.zswltech.mithras.dto.version.DiffValue;
import lombok.Data;

/**
 * @author luyi
 */
@Data
public class RentCollectionMonthDetailListRSP {


    private Long id;

    /**
     * 预备表id
     */
    private Long prepareId;

    /**
     * 客户id
     */
    private Long clientId;
    private String clientName;

    private Long deptId;
    private String deptName;

    /**
     * 主办id
     */
    private Long sponsorId;
    private String sponsorName;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 期项
     */
    private Integer phase;

    /**
     * 租金支付日
     */
    private DiffValue repayDate;
    /**
     * 租金
     */
    private DiffValue rent;
    /**
     * 本金
     */
    private DiffValue principal;
    /**
     * 利息
     */
    private DiffValue interest;
    /**
     * 银行开户账户名
     */
    private DiffValue bankAccountName;
    /**
     * 银行开户账户号
     */
    private DiffValue bankAccountNumber;
    /**
     * 开户行名称
     */
    private DiffValue bankName;

    private Integer month;
}

