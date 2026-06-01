package cn.zswltech.mithras.service.enums.afterlease;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@AllArgsConstructor
@Getter
public enum AfterLeaseCheckReportAreaTypeEnum {
    CONTENT("检查内容"),
    SUMMARY("检查总结"),
    EXTRA_CONTENT("补充信息检查内容");

    private final String display;
}
