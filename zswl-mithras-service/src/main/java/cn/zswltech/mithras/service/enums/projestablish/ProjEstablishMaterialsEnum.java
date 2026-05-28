package cn.zswltech.mithras.service.enums.projestablish;

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
public enum ProjEstablishMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    REPORT("立项审批单", 1),
    PROJ_INFORMATION("业务申请书", 2),
    PROJ_CREDIT_LETTER("征信授权书", 3),
    OTHER("其他",4),
    ;

    private final String display;

    /**
     * 排序优先级
     */
    private Integer sort;

    private static final Map<String, ProjEstablishMaterialsEnum> map;

    static {
        map = Stream.of(ProjEstablishMaterialsEnum.values()).collect(Collectors.toMap(ProjEstablishMaterialsEnum::name, e -> e));
    }

    public static ProjEstablishMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "PROJ_ESTABLISH";
    }

    @Override
    public String display() {
        return display;
    }
}
