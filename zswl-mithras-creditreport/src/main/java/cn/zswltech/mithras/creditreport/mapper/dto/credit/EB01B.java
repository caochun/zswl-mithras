/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.mapper.dto.credit;

import lombok.Data;

@Data
public class EB01B {

    //非信贷交易账户数
    private int EB01BS01;
    //欠税记录条数
    private int EB01BS02;
    //民事判决记录条数
    private int EB01BS03;
    private int EB01BS04;
    private int EB01BS05;

}