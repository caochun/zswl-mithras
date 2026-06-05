package cn.zswltech.mithras.customer.application.validator;

import java.util.regex.Pattern;

/**
 * @author bigbear
 * @version 1.0
 * @description 中征码（机构信用代码）校验工具
 * @since 2025/8/19 10:32
 **/
public class InstitutionCreditCodeValidator {

    // 标准18位中征码正则（纯数字）
    private static final Pattern STANDARD_18_DIGIT_PATTERN =
            Pattern.compile("^\\d{18}$");

    // 临时16位码正则（允许数字和字母，按业务需求调整）
    private static final Pattern TEMP_16_DIGIT_PATTERN =
            Pattern.compile("^[A-Za-z0-9]{16}$");

    /**
     * 校验是否为有效的中征码（兼容18位标准和16位临时码）
     * @param code 待校验的代码
     * @return true-有效，false-无效
     */
    @Deprecated
    public static boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }

        // 优先校验18位标准代码
        if (code.length() == 18) {
            return isValidStandard18DigitCode(code);
        }
        // 兼容16位临时码（按业务需求开放）
        else if (code.length() == 16) {
            return isValidTemp16DigitCode(code);
        }

        return false;
    }

    /**
     * 校验标准18位中征码 （不能用！！！！）
     */
    private static boolean isValidStandard18DigitCode(String code) {
        // 1. 正则验证是否为18位纯数字
        if (!STANDARD_18_DIGIT_PATTERN.matcher(code).matches()) {
            return false;
        }

        // 2. 校验码验证（示例算法，实际需参考人民银行规范）
        // 假设校验规则：前17位的加权和模11等于最后一位
        try {
            int sum = 0;
            int[] weights = {1, 3, 5, 7, 9, 2, 4, 6, 8, 10, 12, 14, 16, 18, 3, 5, 7};

            for (int i = 0; i < 17; i++) {
                int digit = Character.getNumericValue(code.charAt(i));
                sum += digit * weights[i];
            }

            int expectedCheckDigit = sum % 11 % 10;
            int actualCheckDigit = Character.getNumericValue(code.charAt(17));

            return expectedCheckDigit == actualCheckDigit;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验16位临时码（基础格式验证）
     */
    private static boolean isValidTemp16DigitCode(String code) {
        // 仅做长度和字符类型校验（实际业务可能需对接平台API验证）
        return TEMP_16_DIGIT_PATTERN.matcher(code).matches();
    }


    public static void main(String[] args) {
        System.out.println(isValid("3303810003359051"));
        System.out.println(isValid("321391UTGEJUXM46"));
        System.out.println(isValid("331125000065406912"));
    }
}
