package cn.zswltech.mithras.credit.domain.groupcredit.establish.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 项目立项资料清单
 *
 * @author wangchuanhao
 * @date 2022/9/21 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum GroupCreditEstablishMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    REPORT("立项审批单", 1),
    PROJ_INFORMATION("业务申请书", 2),
    OTHER("其它", 3),

    ;

    private final String display;

    /**
     * 排序优先级
     */
    private Integer sort;

    private static final Map<String, GroupCreditEstablishMaterialsEnum> map;

    static {
        map = Stream.of(GroupCreditEstablishMaterialsEnum.values()).collect(Collectors.toMap(GroupCreditEstablishMaterialsEnum::name, e -> e));
    }

    public static GroupCreditEstablishMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "GROUP_CREDIT_ESTABLISH";
    }

    @Override
    public String display() {
        return display;
    }
}
