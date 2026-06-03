package cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums;

import lombok.Getter;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/16 22:15
 */
public enum Region {
//    /**
//     *
//     */
//    ZHEJIANG("330000"), JIANGSU("320000"), SHANXI("140000"), SHANDONG("370000"),
//    HENAN("410000"), ANHUI("340000"), JIANGXI("360000"), QINGHAI("630000"),
//    FUJIAN("350000"), GUANGDONG("440000"), HEBEI("130000"), HUBEI("420000"),
//    SICHUAN("510000"), HEILONGJIANG("230000"), GANSU("620000"), JILIN("220000"),
//    GUIZHOU("520000"), HUNAN("430000"), HAINAN("460000"), LIAONING("210000"),
//    YUNNAN("530000"), SHANXI2("610000"), TAIWAN("710000"),
//
//    XIANGGANG("810000"), AOMEN("820000"),
//    GUANGXI("450000"), NEIMENGGU("150000"), XINJIANG("650000"), XIZANG("540000"), NINGXIA("640000"),
//    BEIJING("110000"), TIANJIN("120000"), SHANGHAI("310000"), CHONGQING("500000"),
//    ;
//    private final String code;
//
//    Region(String code) {
//        this.code = code;
//    }
//
//    public String code() {
//        return code;
//    }
ZHEJIANG("330000", "浙江"),
    JIANGSU("320000", "江苏"),
    SHANXI("140000", "山西"),
    SHANDONG("370000", "山东"),
    HENAN("410000", "河南"),
    ANHUI("340000", "安徽"),
    JIANGXI("360000", "江西"),
    QINGHAI("630000", "青海"),
    FUJIAN("350000", "福建"),
    GUANGDONG("440000", "广东"),
    HEBEI("130000", "河北"),
    HUBEI("420000", "湖北"),
    SICHUAN("510000", "四川"),
    HEILONGJIANG("230000", "黑龙江"),
    GANSU("620000", "甘肃"),
    JILIN("220000", "吉林"),
    GUIZHOU("520000", "贵州"),
    HUNAN("430000", "湖南"),
    HAINAN("460000", "海南"),
    LIAONING("210000", "辽宁"),
    YUNNAN("530000", "云南"),
    SHANXI2("610000", "陕西"),
    TAIWAN("710000", "台湾"),
    XIANGGANG("810000", "香港"),
    AOMEN("820000", "澳门"),
    GUANGXI("450000", "广西"),
    NEIMENGGU("150000", "内蒙古"),
    XINJIANG("650000", "新疆"),
    XIZANG("540000", "西藏"),
    NINGXIA("640000", "宁夏"),
    BEIJING("110000", "北京"),
    TIANJIN("120000", "天津"),
    SHANGHAI("310000", "上海"),
    CHONGQING("500000", "重庆");

    private final String code;
    @Getter
    private final String name;

    Region(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String code() {
        return code;
    }
}
