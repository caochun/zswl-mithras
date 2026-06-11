package cn.zswltech.mithras.customer.enums.app;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;

/**
 * @author dingqi
 * @date 2025/9/18
 * @description
 */
@AllArgsConstructor
public enum VisitDownloadTaskStatusEnum implements PullDown {
    DOING("执行中"),
    SUCCESS("成功"),
    FAILURE("失败");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
