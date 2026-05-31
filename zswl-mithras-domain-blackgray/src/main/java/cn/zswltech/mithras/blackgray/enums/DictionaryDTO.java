package cn.zswltech.mithras.blackgray.enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: xinhao.hu
 * @date: 2022/7/25 14:07
 * @description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DictionaryDTO {
    /**
     * 字典值
     */
    private Object value;

    /**
     * 字典显示名
     */
    private String label;

    /**
     * 字典英文显示名
     */
    private String enLable;
}
