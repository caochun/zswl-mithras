package cn.zswltech.mithras.report.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 查询渠道
 *
 * @author wangchuanhao
 * @date 2023/1/12 9:50 AM
 */
@Getter
@AllArgsConstructor
public enum QueryChannel {

    /**
     * 编辑区
     */
    EDIT,

    /**
     * 流程里
     */
    PROC,

    /**
     * 流程产生的批次号
     */
    PROC_BATCH,

    /**
     * 生效区（借据维度穿透）
     */
    EFFECT,
    ;

    private static Map<String, QueryChannel> map;

    static {
        map = Stream.of(QueryChannel.values()).collect(Collectors.toMap(QueryChannel::name, Function.identity(), (k1, k2)->k1));
    }

    public static QueryChannel of(String name) {
        return map.get(name);
    }


}
