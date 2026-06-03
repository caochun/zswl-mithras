package cn.zswltech.mithras.filingmaterials.domain.enums;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 资料归档审批状态
 * @author: lllin
 * @date: 2025-12-04
 */
public enum FilingMaterialsProcessStatusEnum {
    /**
     * 审批中
     */
    UNDER_APPROVAL("审批中"),
    UN_SUBMIT("待提交"),
    APPROVAL_PASS("审批通过"),
    APPROVAL_REJECT("审批拒绝"),
    CANCEL("已关闭"),
    ABOLISH("作废");

    FilingMaterialsProcessStatusEnum(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    private static final Map<String, FilingMaterialsProcessStatusEnum> map;

    static {
        map = Stream.of(FilingMaterialsProcessStatusEnum.values()).collect(Collectors.toMap(FilingMaterialsProcessStatusEnum::name, e -> e));
    }
    public final String display;

    public static FilingMaterialsProcessStatusEnum of(String code) {
        for (FilingMaterialsProcessStatusEnum value : FilingMaterialsProcessStatusEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static List<FilingMaterialsProcessStatusEnum> listAll() {
        return new ArrayList<>(map.values());
    }

}
