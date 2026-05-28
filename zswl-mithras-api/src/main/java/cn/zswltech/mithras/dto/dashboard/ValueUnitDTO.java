package cn.zswltech.mithras.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueUnitDTO {
    /**
     * 数值
     */
    private String value;
    /**
     * 单位
     */
    private String unit;
}