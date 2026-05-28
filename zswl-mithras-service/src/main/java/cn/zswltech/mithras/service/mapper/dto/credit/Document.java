/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.service.mapper.dto.credit;

import lombok.Data;

/**
 * 信加征信解析根节点
 * @author: jackerhe
 * @date: 2025/11/14 09:23
 **/
@Data
public class Document {

    //信用提示信息
    private EBA EBA;

    //借贷交易汇总信息
    private EBB EBB;



    private EAA EAA;
    private EBC EBC;
    private EBD EBD;
    private EBE EBE;
    private ECA ECA;
    private EDA EDA;
    private EDB EDB;
    private EDC EDC;
    private EDD EDD;
    private EEA EEA;
    private String EFA;
    private EFB EFB;
    private EFC EFC;
    private EFD EFD;
    private EFE EFE;
    private EFF EFF;
    private String EFG;
    private EGA EGA;
    private EHA EHA;
    private String EIA;

}