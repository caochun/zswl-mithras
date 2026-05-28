/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.service.mapper.dto.credit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2025-11-12 18:31:28
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class EB04 {

    //非循环信用额度合计
    private BigDecimal EB040J01;
    //已使用的非循环信用额度合计
    private BigDecimal EB040J02;
    //剩余可用的非循环额度合计
    private BigDecimal EB040J03;
    //循环信用额度合计
    private BigDecimal EB040J04;
    //已使用的循环信用额度合计
    private BigDecimal EB040J05;
    //剩余可用的循环额度合计
    private BigDecimal EB040J06;
    private String EB040D01;

}