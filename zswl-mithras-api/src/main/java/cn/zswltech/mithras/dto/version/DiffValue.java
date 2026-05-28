package cn.zswltech.mithras.dto.version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 值差异
 *
 * @author wangchuanhao
 * @date 2022/6/30 9:27 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiffValue {

    private Object beforeValue;

    private Object value;

    private Boolean isChange;

    private List<DiffValue> diffValueList;

    /**
     * 变动类型
     * @see cn.zswltech.mithras.service.enums.InfoOperation
     */
    private String changeType;
}