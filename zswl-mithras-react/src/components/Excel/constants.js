/**
 * Excel 导出常量定义
 */

// 默认的列宽
export const DEFAULT_COLUMN_WIDTH = 20

// 默认行高
export const DEFAULT_ROW_HEIGHT = 20

// 标题头背景颜色
export const DEFAULT_HEADER_FG_COLOR = 'c0c0c0'

// 计算宽度时的采样行数，避免对全量数据进行 O(n*m) 的扫描
export const WIDTH_SAMPLE_SIZE = 200

// 默认分块大小
export const DEFAULT_CHUNK_SIZE = 200