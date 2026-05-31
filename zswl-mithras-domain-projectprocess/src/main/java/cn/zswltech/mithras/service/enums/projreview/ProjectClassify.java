package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/29 11:48
 */
public enum ProjectClassify implements PullDown {
    ENCOURAGEMENT("鼓励介入类"),
    MODERATE_SUPPORT("适度支持类"),
    CAUTIOUS("谨慎支持类"),
    CONSTRUCTION_MACHINERY("工程机械类（厂商担保模式）"),
    INTRA_GROUP_COLLABORATION("集团内协同业务")
    ;

    public static ProjectClassify find(String name) {
        for (ProjectClassify item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public String display;

    ProjectClassify(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

    private static Map<String, ProjectClassify> map;

    static {
        map = Stream.of(ProjectClassify.values())
                .collect(Collectors.toMap(ProjectClassify::name, e -> e));
    }

    public static ProjectClassify of(String name) {
        return map.get(name);
    }
}
