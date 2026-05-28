package cn.zswltech.mithras.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result Code Enum
 *
 * @author shuchi
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    LOGIN_TIMEOUT(98, "登录超时"),

    RETRY_LOGIN(101, "密码已修改, 请重新登录"),

    FAILURE(400, "操作失败"),

    ILLEGAL_REQUEST(400, "非法请求"),

    PARAM_MISS(400, "缺少必填参数"),

    PARAM_TYPE_ERROR(400, "参数类型错误"),

    PARAM_BIND_ERROR(400, "参数绑定错误"),

    PARAM_VALID_ERROR(400, "参数验证错误"),

    UN_AUTHORIZED(401, "没有访问权限"),

    REQ_REJECT(403, "请求拒绝"),

    NOT_FOUND(404, "404 未找到"),

    METHOD_NOT_SUPPORTED(405, "请求方法不支持"),

    INTERNAL_SERVER_ERROR(500, "系统错误"),

    // ..........  业务异常 (前端约定code>10000).............//

    /**
     * 用户session过期
     */
    USER_SESSION_EXPIRE(999, "用户session过期"),

    /**
     * 数据不存在
     */
    RECORD_NOT_FOUND_ERROR(10001, "Record Not Found"),

    /**
     * 员工没有菜单权限
     */
    USER_NO_MENU_AUTH(10002, "用户没有菜单权限"),
    ;


    // ..........  业务异常 (前端约定code>10000) .............//


    final int code;

    final String msg;
}
