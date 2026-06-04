package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author junke
 */
public enum ProjSourceType implements PullDown {

    clfd("存量翻单"), qdjs("渠道介绍"), zzkf("自主开发");


    ProjSourceType(String display) {
        this.display = display;
    }

    public final String display;

    private static Map<String, ProjSourceType> map;

    static {
        map = Stream.of(ProjSourceType.values()).collect(Collectors.toMap(ProjSourceType::name, e -> e));
    }

    public static ProjSourceType of(String bizType) {
        return map.get(bizType);
    }

    @Override
    public String display() {
        return display;
    }
}
