package cn.zswltech.mithras.third.enums;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * V0 0%
 * V10 1%
 * V13 13%
 * v15 5%征收率减按1.5%
 * V2 2%
 * V3 3%
 * V5 5%
 * V6 6%
 * V9 9%
 **/
@Getter
public enum CQTaxRateENUM {
    V0("V0", BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
    V10("V10", BigDecimal.valueOf(10), BigDecimal.valueOf(0.01)),
    V13("V13", BigDecimal.valueOf(13), BigDecimal.valueOf(0.13)),
    v15("v15", BigDecimal.valueOf(15), BigDecimal.valueOf(0.15)),
    V2("V2", BigDecimal.valueOf(2), BigDecimal.valueOf(0.02)),
    V3("V3", BigDecimal.valueOf(3), BigDecimal.valueOf(0.03)),
    V5("V5", BigDecimal.valueOf(5), BigDecimal.valueOf(0.05)),
    V6("V6", BigDecimal.valueOf(6), BigDecimal.valueOf(0.06)),
    V9("V9", BigDecimal.valueOf(9), BigDecimal.valueOf(0.09)),
    ;

    private final String code;
    private final BigDecimal display;
    private final BigDecimal display1;

    CQTaxRateENUM(String code, BigDecimal display, BigDecimal display1) {
        this.code = code;
        this.display = display;
        this.display1 = display1;
    }

    public static CQTaxRateENUM getCqBusinessType(BigDecimal rate){
        for(CQTaxRateENUM value: CQTaxRateENUM.values()){
            if(value.display.compareTo(rate) == 0 || value.getDisplay1().compareTo(rate) == 0){
                return value;
            }
        }
        //默认v6
        return V6;
    }

}
