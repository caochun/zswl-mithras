package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/4/3/13:57
 * @description
 */
@Getter
@AllArgsConstructor
public enum DataShowTypeEnum implements PullDown {
    /**
     * 数据展示标签类型
     */
    NORMAL(""),
    ADD("新增"),
    REMOVE("删除"),
    MODIFY("修改"),
    CANCEL_REMOVE("取消删除"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
