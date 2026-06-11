package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 地区分类
 * @date: 2023/5/19 11:26
 * 项目评审使用
 */
public enum ProjRegionalClassify implements PullDown {
    /**
     * 浙江地区
     */
    ZHEJIANG("浙江地区"),

    /**
     * 鼓励支持类地区（除浙江）
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

    private final String display;

    ProjRegionalClassify(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

    private static Map<String, ProjRegionalClassify> map;

    static {
        map = Stream.of(ProjRegionalClassify.values())
                .collect(Collectors.toMap(ProjRegionalClassify::name, e -> e));
    }

    public static ProjRegionalClassify of(String name) {
        return map.get(name);
    }

}
