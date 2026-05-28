package cn.zswltech.mithras.report.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 批次类型
 *
 * @author wangchuanhao
 * @date 2023/1/12 4:03 PM
 */
@AllArgsConstructor
@Getter
public enum BatchType {

    /**
     * 增量
     */
    INCRE,

    /**
     * 全量
     */
    FULL,
    ;
}
