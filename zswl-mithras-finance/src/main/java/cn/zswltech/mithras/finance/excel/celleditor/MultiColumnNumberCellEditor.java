package cn.zswltech.mithras.finance.excel.celleditor;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.excel.celleditor.NumberToBigDecimalCellEditor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 最终版：基于BigDecimal处理数字格式化（参考示例逻辑）
 * 构造方法接收 <列名, 四舍五入规则> 的Map，数字先转BigDecimal再处理精度
 */
public class MultiColumnNumberCellEditor extends NumberToBigDecimalCellEditor {
    // 特殊列配置：Key=列名，Value=该列的四舍五入规则
    private final Map<String, RoundingRule> specialColumnRuleMap;
    // 默认规则：未配置的列保留2位小数，HALF_UP四舍五入
    private static final RoundingRule DEFAULT_RULE = new RoundingRule(2, RoundingMode.HALF_UP);

    /**
     * 四舍五入规则封装类（语义化管理小数位数和舍入模式）
     */
    @Getter
    public static class RoundingRule {
        // getter
        // 保留小数位数
        private final int scale;
        // 四舍五入模式
        private final RoundingMode roundingMode;

        /**
         * 构造规则（明确指定舍入模式）
         * @param scale 保留小数位数（≥0）
         * @param roundingMode 四舍五入模式
         */
        public RoundingRule(int scale, RoundingMode roundingMode) {
            if (scale < 0) {
                throw new IllegalArgumentException("保留小数位数不能为负数");
            }
            if (roundingMode == null) {
                throw new IllegalArgumentException("四舍五入模式不能为空");
            }
            this.scale = scale;
            this.roundingMode = roundingMode;
        }

        /**
         * 简化构造规则（默认使用HALF_UP四舍五入）
         * @param scale 保留小数位数（≥0）
         */
        public RoundingRule(int scale) {
            this(scale, RoundingMode.HALF_UP);
        }

    }

    /**
     * 核心构造方法：直接接收 <列名, 四舍五入规则> 的Map
     * @param columnRuleMap Key=列名，Value=该列的四舍五入规则（RoundingRule）
     */
    public MultiColumnNumberCellEditor(Map<String, RoundingRule> columnRuleMap) {
        // 初始化特殊列规则（防御性拷贝，避免外部修改影响内部）
        this.specialColumnRuleMap = columnRuleMap == null
                ? new HashMap<>()
                : new HashMap<>(columnRuleMap);
    }

    /**
     * 核心方法：重写Hutool的CellEditor.edit方法（参考示例逻辑，基于BigDecimal处理）
     * @param cell  Excel单元格对象
     * @param value Hutool解析后的单元格原始值
     * @return 格式化后的值（数字转BigDecimal并按规则舍入，空字符串返回null）
     */
    @Override
    public Object edit(Cell cell, Object value) {
        // 1. 空单元格/空值直接返回null（对齐示例逻辑）
        if (value == null || cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        // 2. 处理字符串类型：空字符串返回null，非空返回原值
        if (value instanceof String) {
            String strValue = value.toString().trim();
            return StrUtil.isBlank(strValue) ? null : strValue;
        }

        // 3. 处理数字类型：统一转为BigDecimal后按规则设置小数位数
        if (value instanceof Number) {
            BigDecimal bigDecimal;
            try {
                // 将所有数字类型（Double/Float/Integer/Long/BigDecimal）转为BigDecimal
                if (value instanceof BigDecimal) {
                    bigDecimal = (BigDecimal) value;
                } else {
                    // 优先用字符串构造，避免浮点数精度丢失（如0.1d的二进制精度问题）
                    bigDecimal = new BigDecimal(value.toString());
                }

                // 获取当前列的规则（特殊列用自定义规则，否则用默认规则）
                RoundingRule rule = getColumnRule(cell);
                // 按规则设置小数位数和舍入模式
                return bigDecimal.setScale(rule.getScale(), rule.getRoundingMode());
            } catch (Exception e) {
                // 异常时返回原值（避免解析失败导致程序中断）
                return value;
            }
        }

        // 4. 非数字/非字符串类型：返回原值
        return value;
    }

    /**
     * 获取当前单元格对应列的四舍五入规则
     * @param cell 单元格对象
     * @return 列对应的规则（自定义规则/默认规则）
     */
    private RoundingRule getColumnRule(Cell cell) {
        // 获取列名（表头行默认第0行，可根据实际Excel调整）
        String columnName = getColumnName(cell);
        // 优先返回自定义规则，无则返回默认规则
        return specialColumnRuleMap.getOrDefault(columnName, DEFAULT_RULE);
    }

    /**
     * 获取单元格对应的列名（表头行默认第0行）
     */
    private String getColumnName(Cell cell) {
        if (cell.getRow().getSheet() == null) {
            return "";
        }
        // 获取表头行的对应列单元格
        Cell headerCell = cell.getRow().getSheet().getRow(0).getCell(cell.getColumnIndex());
        if (headerCell == null) {
            return "";
        }
        // 用父类方法解析表头值，保证一致性
        Object headerValue = super.edit(headerCell, headerCell);
        return headerValue == null ? "" : headerValue.toString().trim();
    }

}
