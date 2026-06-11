/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.dto.credit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 借贷交易相关还款责任汇总信息
 * @author: jackerhe 
 * @date: 2025/11/17 16:58
 **/
@Data
public class EB05AH {

    private Integer EB05AD01;
    //被追偿业务-还款责任金额
    private BigDecimal EB05AJ01;
    //被追偿账户数
    private BigDecimal EB05AS02;
    //被追偿账户余额
    private BigDecimal EB05AJ02;
    //其他借贷交易的还款责任金额
    private BigDecimal EB05AJ03;
    //其他借贷交易账户数
    private BigDecimal EB05AS03;
    //其他借贷交易账户余额
    private BigDecimal EB05AJ04;
    //其他借贷交易账户关注类余额
    private BigDecimal EB05AJ05;
    //其他借贷交易账户不良类余额
    private BigDecimal EB05AJ06;
    
}