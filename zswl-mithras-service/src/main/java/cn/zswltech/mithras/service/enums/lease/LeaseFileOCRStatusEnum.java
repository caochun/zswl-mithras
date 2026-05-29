package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 租赁物文件ocr识别状态
 *
 * @author yupengfei
 * @date 2024/5/8 18:13
 */
@AllArgsConstructor
@Getter
public enum LeaseFileOCRStatusEnum implements PullDown {
    CHECK_FAILURE("核验失败"),
    VERIFICATION_PASSED("验真通过"),
    VERIFICATION_NOT_PASSED("验真不通过"),
    IDENTIFY_FAILED("识别失败"),
    IDENTIFY_SUCCESS("识别成功"),
    VERIFICATION_FAILED("发票超过五年无法验真"),
    ;

    private final String display;

    public static LeaseFileOCRStatusEnum of(String name) {
        for (LeaseFileOCRStatusEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
