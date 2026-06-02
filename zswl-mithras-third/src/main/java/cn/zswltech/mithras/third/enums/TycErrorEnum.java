package cn.zswltech.mithras.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 天眼查错误code枚举
 *
 * @author wangchuanhao
 * @date 2022/6/20 4:04 PM
 */
@AllArgsConstructor
@Getter
public enum TycErrorEnum {

    /**
     *
     */
    SUCCESS(0, "请求成功"),
    NO_DATA(300000, "⽆数据"),
    FAIL(300001, "请求失败"),
    INVALID_ACCOUNT(300002, "账号失效"),
    EXPIRED_ACCOUNT(300003, "账号过期"),
    RATE_LIMIT(300004, "访问频率过快"),
    NO_PERMISSION(300005, "⽆权限访问此api"),
    LESS_BALANCE(300006, "余额不足"),
    LESS_INVOKE_TIMES(300007, "剩余次数不⾜"),
    ILLEGAL_PARAM(300008, "缺少必要参数"),
    ERROR_ACCOUNT(300009, "账号信息有误"),
    URL_NOT_FOUND(300010, "URL不存在"),
    ILLEGAL_IP(300011, "此IP⽆权限访问此api"),
    REPORT_GENERATING(300012, "报告⽣成中"),
    NOT_MOCK(400001, "没有mock数据"),
    ;

    private Integer code;
    private String message;

    public static boolean isError(Integer errorCode) {
        return !SUCCESS.getCode().equals(errorCode) && !NO_DATA.getCode().equals(errorCode);
    }

}
