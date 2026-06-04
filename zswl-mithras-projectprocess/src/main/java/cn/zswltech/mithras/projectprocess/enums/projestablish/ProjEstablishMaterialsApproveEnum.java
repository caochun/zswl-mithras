package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
public enum ProjEstablishMaterialsApproveEnum implements PullDown {

    MEETING_MINUTES("立项会会议纪要", 1),
    OTHER("其他",2),
    ;

    private final String display;

    /**
     * 排序优先级
     */
    private final Integer sort;

    private static final Map<String, ProjEstablishMaterialsApproveEnum> map;

    static {
        map = Stream.of(ProjEstablishMaterialsApproveEnum.values()).collect(Collectors.toMap(ProjEstablishMaterialsApproveEnum::name, Function.identity(),(m1,m2)->m1));
    }

    public static ProjEstablishMaterialsApproveEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String display() {
        return display;
    }
}
