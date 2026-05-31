package cn.zswltech.mithras.service.others;

/**
 * @author junke
 */
public class MithrasException extends RuntimeException {

    private static final long serialVersionUID = -127923673133155609L;
    private Integer code = 400;

    public MithrasException(String msg) {
        super(msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static MithrasException newException(String message) {
        return new MithrasException(message);
    }

    public static MithrasException customException(Integer code, String message) {
        MithrasException mithrasException = new MithrasException(message);
        mithrasException.code = code;
        return mithrasException;
    }

    public static void err(String message) {
        throw new MithrasException(message);
    }

    public static void err(boolean condition, String message) {
        if (condition) {
            throw new MithrasException(message);
        }
    }


}
