package cn.zswltech.mithras.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * 禁止特殊字符
 *
 * @author wangchuanhao
 * @date 2022/12/21 4:06 PM
 */
public class BanSpecialCharValidator implements ConstraintValidator<BanSpecialChar, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Objects.isNull(value) || "".equals(value)) {
            return true;
        }
        return value.trim().replaceAll("\n|\r|\t", "").length() == value.length();
    }

}
