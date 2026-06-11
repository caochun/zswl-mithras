package cn.zswltech.mithras.document.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 生成文档使用的变量类型
 *
 * @author wangchuanhao
 * @date 2022/7/11 9:44 PM
 */
@Getter
@AllArgsConstructor
public enum GenDocVarTypeEnum {

    /**
     * 文本 {{}}
     */
    TEXT,

    /**
     * 表格 {{#}}
     */
    TABLE,
    ;

}
