package cn.zswltech.mithras.document.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 生成文档使用的变量
 *
 * @author wangchuanhao
 * @date 2022/7/11 9:44 PM
 */
@Getter
@AllArgsConstructor
public enum GenDocVarEnum {

    /**
     * 测试占位符
     */
    DEMO("demo", "demoName", GenDocVarTypeEnum.TEXT),

    ;

    private String module;

    private String varName;

    private GenDocVarTypeEnum type;

}
