package cn.zswltech.mithras.api.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 通用返回对象
 *
 * @author shuchi
 */
@Getter
@Setter
@ToString
public class R<T> {
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 状态码
     */
    private long code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 数据封装
     */
    private T data;

    /**
     * 异常时返回的异常信息
     */
    private String description;

    /**
     * 不阻断操作流程的toast提示
     */
    private String toast;

    protected R() {
    }

    protected R(long code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        if (code == ResultCode.SUCCESS.getCode()) {
            this.success = true;
        } else {
            this.success = false;
        }
    }


    protected R(long code, String msg, T data, String description) {
        this.description = description;
        this.code = code;
        this.msg = msg;
        this.data = data;
        if (code == ResultCode.SUCCESS.getCode()) {
            this.success = true;
        } else {
            this.success = false;
        }
    }


    /**
     * 成功返回结果
     */
    public static <T> R<T> ok() {
        return new R<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> R<T> ok(T data) {
        return new R<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param msg  提示信息
     */
    public static <T> R<T> ok(T data, String msg) {
        return new R<T>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> R<T> fail(ResultCode errorCode) {
        return new R<T>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param msg       错误信息
     */
    public static <T> R<T> fail(ResultCode errorCode, String msg) {
        return new R<T>(errorCode.getCode(), msg, null);
    }


    /**
     * 失败返回结果
     */
    public static <T> R<T> failWithDesc(ResultCode errorCode, String description) {
        return new R<T>(errorCode.getCode(), errorCode.getMsg(), null, description);
    }


    /**
     * 失败返回结果
     *
     * @param msg 提示信息
     */
    public static <T> R<T> fail(String msg) {
        return new R<T>(ResultCode.FAILURE.getCode(), msg, null);
    }

    /**
     * 失败返回结果
     *
     * @param msg 提示信息
     */
    public static <T> R<T> customFail(Integer code, String msg) {
        return new R<T>(code, msg, null);
    }

    /**
     * 失败返回结果
     *
     * @param msg  提示信息
     * @param code 编码
     */
    public static <T> R<T> fail(Integer code, String msg) {
        return new R<T>(code, msg, null);
    }

    /**
     * 正常返回，但是带有错误码
     *
     * @param msg  提示信息
     * @param code 编码
     */
    public static <T> R<T> ok(Integer code, String msg) {
        return new R<T>(code, msg, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> R<T> fail() {
        return fail(ResultCode.FAILURE);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> R<T> validateFail() {
        return fail(ResultCode.PARAM_VALID_ERROR);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param msg 提示信息
     */
    public static <T> R<T> validateFail(String msg) {
        return new R<T>(ResultCode.PARAM_VALID_ERROR.getCode(), msg, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> R<T> unauthorized(T data) {
        return new R<T>(ResultCode.UN_AUTHORIZED.getCode(), ResultCode.UN_AUTHORIZED.getMsg(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> R<T> forbidden(T data) {
        return new R<T>(ResultCode.REQ_REJECT.getCode(), ResultCode.REQ_REJECT.getMsg(), data);
    }

}
