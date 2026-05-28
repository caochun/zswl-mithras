package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author yupengfei
 * @date 2024/5/8 20:21
 */
@AllArgsConstructor
@Getter
public enum LeaseOperateEnum implements PullDown {
    REPLACE("替换"),
    DELETE_CHANGE_RECORD("删除变更记录"),
    DELETE_HOME_PAGE("删除首页"),
    RE_UPLOAD("重新上传"),
    RETEST("重新验真"),
    PREVIEW("预览"),
    DELETE("删除"),
    UPDATE("编辑"),
    NEW("新建");
    private final String display;

    public static LeaseOperateEnum of(String name) {
        for (LeaseOperateEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
