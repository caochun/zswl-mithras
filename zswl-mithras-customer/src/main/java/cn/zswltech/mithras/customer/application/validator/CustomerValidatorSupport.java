package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.service.others.LackDataException;

final class CustomerValidatorSupport {

    private CustomerValidatorSupport() {
    }

    static void errLackData(boolean condition, String msg) {
        if (condition) {
            throw new LackDataException(msg);
        }
    }
}
