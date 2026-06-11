package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.foundation.exception.LackDataException;

final class CustomerValidatorSupport {

    private CustomerValidatorSupport() {
    }

    static void errLackData(boolean condition, String msg) {
        if (condition) {
            throw new LackDataException(msg);
        }
    }
}
