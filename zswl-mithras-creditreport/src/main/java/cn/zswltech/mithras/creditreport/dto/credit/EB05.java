/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.dto.credit;

import lombok.Data;
/**
 * 相关还款责任汇总信息单元
 * @author: jackerhe 
 * @date: 2025/11/17 16:43
 **/
@Data
public class EB05 {

    private EB05A EB05A;
    private EB05B EB05B;

}