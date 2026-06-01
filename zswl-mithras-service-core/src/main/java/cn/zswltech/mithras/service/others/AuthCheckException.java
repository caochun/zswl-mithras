package cn.zswltech.mithras.service.others;

/**
 * @author dingqi
 * @date 2022/7/28
 * @description
 */
public class AuthCheckException extends RuntimeException {
    private static final long serialVersionUID = -1815890927868419509L;

    public AuthCheckException(String msg) {super(msg);}

    public static AuthCheckException newException(String msg) {
        return new AuthCheckException(msg);
    }
}
