package cn.zswltech.mithras.ftp.common.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ProjectClassify implements PullDown {
    ENCOURAGEMENT("鼓励介入类"),
    MODERATE_SUPPORT("适度支持类"),
    CAUTIOUS("谨慎支持类"),
    CONSTRUCTION_MACHINERY("工程机械类（厂商担保模式）"),
    INTRA_GROUP_COLLABORATION("集团内协同业务");

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

    public static ProjectClassify find(String name) {
        return map.get(name);
    }

    public static ProjectClassify of(String name) {
        return map.get(name);
    }
}
