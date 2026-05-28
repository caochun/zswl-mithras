package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.service.others.MithrasException;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/27 09:30
 */
public class MissingFactorException extends RuntimeException {

    private static final long serialVersionUID = -127923673133155609L;

    public MissingFactorException(String msg) {
        super(msg);
    }

    public static MithrasException newException(String message) {
        return new MithrasException(message);
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
