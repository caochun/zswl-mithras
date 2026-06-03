package cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.dto;

import lombok.Data;

/**
 * 租金催收首页
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:38 PM
 */
@Data
public class RentCollectionIndexListDTO {

   /* *//**
     * 付款id
     *//*
    private Long paymentId;*/

    /**
     *  借据id
     */
    private Long receiptId;

    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 收款主表id以,分隔
     */
    private String collectionIds;

    /**
     * 客户id
     */
    private Long clientId;

    /**
     * 付款申请编号
     */
    private String receiptCode;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 合同状态
     */
    private String contractStatus;

    /**
     * 主办id
     */
    private Long projSponsorUserId;

    /**
     * 业务部门id
     */
    private Long bizDeptId;

}
