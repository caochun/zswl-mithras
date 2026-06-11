package cn.zswltech.mithras.third.financialshare.client.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FinancialBaseRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/10/26 2:17 下午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialBaseRSP {
    private String state;

    private Boolean success;

    private String message;

    private String errorCode;

    private Boolean status;

    public static FinancialBaseRSP fail(String message){
     return new FinancialBaseRSP("false", false, message,"-1", false);
    }
}
