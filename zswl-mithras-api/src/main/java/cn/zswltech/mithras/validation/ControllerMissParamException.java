package cn.zswltech.mithras.validation;

/**
 * @author junke
 */
public class ControllerMissParamException extends RuntimeException {
    private final String param;

    public ControllerMissParamException(String param) {
        super("lack " + param);
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
