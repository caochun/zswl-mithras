package cn.zswltech.mithras.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FinancialRSPENUM {
    SUCCESS("success", "请求成功");

    private String result;
    private String message;

}
