package cn.zswltech.mithras.projectprocess.enums.projreview;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 项目评审 数据来源类型
 *
 * @author wangchuanhao
 * @date 2022/11/11 3:48 PM
 */
public enum ReviewRelationDataType {

    /**
     * 普通立项
     */
    PROJ_ESTABLISH,

    /**
     * 集团授信
     */
    GROUP_CREDIT_REVIEW,
    ;

    private static Map<String, ReviewRelationDataType> map;

    static {
        map = Stream.of(ReviewRelationDataType.values()).collect(Collectors.toMap(ReviewRelationDataType::name, e -> e));
    }

    public static ReviewRelationDataType of(String name) {
        return map.get(name);
    }

}
