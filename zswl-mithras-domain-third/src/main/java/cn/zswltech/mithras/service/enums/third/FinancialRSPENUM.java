package cn.zswltech.mithras.service.enums.third;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FinancialRSPENUM {
    SUCCESS("success", "请求成功");

    private String result;
    private String message;

}
