/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.dto.credit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2025-11-12 18:31:28
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class EB01A {

    //首次有信贷交易的年份
    private String EB01AR01;
    //首次有相关还款责任的年份
    private String EB01AR02;
    //发生信贷交易的机构数
    private int EB01AS01;
    //当前有未结清 信贷交易的机构数
    private int EB01AS02;
    //借贷交易余额
    private BigDecimal EB01AJ01;
    //借贷交易-被追偿余额
    private BigDecimal EB01AJ02;
    //借贷交易-关注类余额
    private BigDecimal EB01AJ03;
    //借贷交易-不良类余额
    private BigDecimal EB01AJ04;
    //担保交易-余额
    private BigDecimal EB01AJ05;
    //担保交易-关注类余额
    private BigDecimal EB01AJ06;
    //担保交易-不良类余额
    private BigDecimal EB01AJ07;

}