package cn.zswltech.mithras.afterlease.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@Getter
@AllArgsConstructor
public enum AfterLeaseCheckPlanLibModelEnum {
    BASE("租后检查计划基本信息"),
    @Deprecated
    PROJECT("租后检查计划需检项目"),
    CLIENT("租后检查计划需检客户");

    private final String display;
}
