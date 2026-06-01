/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.service.mapper.dto.credit;

import lombok.Data;

/**
 * 借贷交易汇总信息单元
 */
@Data
public class EB02 {

    private EB02A EB02A;
    private EB02B EB02B;
    private EB02C EB02C;

}