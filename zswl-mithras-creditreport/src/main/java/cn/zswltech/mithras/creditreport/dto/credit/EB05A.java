/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.dto.credit;
import lombok.Data;

import java.util.List;

/**
 * 借贷交易相关还款责任汇总信息段
 */
@Data
public class EB05A {

    private int EB05AS01;
    private List<EB05AH> EB05AH;
}