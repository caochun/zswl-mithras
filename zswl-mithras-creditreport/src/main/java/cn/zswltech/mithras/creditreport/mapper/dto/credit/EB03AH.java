/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.mapper.dto.credit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 未结清担保交易汇总信息
 * @author: jackerhe 
 * @date: 2025/11/17 15:35
 **/
@Data
public class EB03AH {

    private Integer EB03AD01;
    private Integer EB03AD02;
    private int EB03AS02;
    private BigDecimal EB03AJ01;

}