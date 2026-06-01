package cn.zswltech.mithras.service.enums.kpi;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段名称前端已定义，枚举根据前端定义的来
 */
@AllArgsConstructor
@Getter
public enum WorkbenchProjectDataRange implements PullDown {

    all("全部项目"),
    own("我的项目");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

}
