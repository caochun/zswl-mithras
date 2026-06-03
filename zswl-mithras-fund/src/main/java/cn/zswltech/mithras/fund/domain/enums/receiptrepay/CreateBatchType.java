package cn.zswltech.mithras.fund.domain.enums.receiptrepay;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 进入批量审批的界面
 * 创建批次的类型
 *
 * @author wangchuanhao
 * @date 2023/2/20 11:40 AM
 */
@AllArgsConstructor
@Getter
public enum CreateBatchType {

    /**
     * 页面批量勾选 创建批次
     */
    BATCH,

    /**
     * 自动创建批次 包含未付款状态的所有数据
     */
    AUTO,

    ;

    private static Map<String, CreateBatchType> map;
    static {
        map = Stream.of(CreateBatchType.values()).collect(Collectors.toMap(CreateBatchType::name, e -> e));
    }

    public static CreateBatchType of(String name) {
        return map.get(name);
    }

}
