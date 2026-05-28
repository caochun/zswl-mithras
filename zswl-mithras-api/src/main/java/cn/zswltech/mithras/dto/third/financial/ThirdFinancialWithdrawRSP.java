package cn.zswltech.mithras.dto.third.financial;

import lombok.Data;


/**
 * @create: 2022-08-17
 **/
@Data
public class ThirdFinancialWithdrawRSP {

    private String state;

    private Boolean success;

    private String message;

    private Boolean status;

    private String data;

}
