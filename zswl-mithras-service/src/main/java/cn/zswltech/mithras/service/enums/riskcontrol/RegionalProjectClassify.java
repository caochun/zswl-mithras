package cn.zswltech.mithras.service.enums.riskcontrol;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 地区项目分类
 * @author: zhaozhengkang
 * @date: 2023/2/9 14:34
 * 现在使用
 */
public enum RegionalProjectClassify implements PullDown {
    /**
     * 鼓励支持类
     */
    ENCOURAGE_SUPPORT("鼓励支持类"),
    /**
     * 适度支持类
     */
    MODERATE_SUPPORT("适度支持类"),
    /**
     * 谨慎支持类
     */
    CAUTIOUS_SUPPORT("谨慎支持类"),
    ;

    private String display;

    RegionalProjectClassify(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

    private static Map<String, RegionalProjectClassify> map;

    static {
        map = Stream.of(RegionalProjectClassify.values())
                .collect(Collectors.toMap(RegionalProjectClassify::name, e -> e));
    }

    public static RegionalProjectClassify of(String name) {
        return map.get(name);
    }
}
