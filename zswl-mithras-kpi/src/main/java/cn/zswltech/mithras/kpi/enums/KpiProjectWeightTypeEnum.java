package cn.zswltech.mithras.kpi.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@AllArgsConstructor
@Getter
public enum KpiProjectWeightTypeEnum implements PullDown {
    BUSINESS_DEPT("业务部门"),
    PROJECT_SPONSOR("项目主办"),
    PROJECT_COSPONSOR("项目协办"),
    OTHER_DEPT_RECOMMEND("跨部门推荐人");

    private final String display;

    public static KpiProjectWeightTypeEnum find(String name) {
        for (KpiProjectWeightTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
