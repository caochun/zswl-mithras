/**
 * Excel 导出样式相关函数
 */

import { DEFAULT_HEADER_FG_COLOR } from './constants'

/**
 * 设置单元格样式
 */
export function addCellStyle(cell, attr) {
  const { color, fontSize, horizontal, bold } = attr || {}
  cell.fill = {
    type: 'pattern',
    pattern: 'solid',
    fgColor: { argb: color },
  }
  cell.font = {
    bold: bold ?? true,
    size: fontSize ?? 11,
    // italic: true,
    // name: '微软雅黑',
    color: { argb: '000' },
  }
  cell.border = {
    top: { style: 'thin' },
    left: { style: 'thin' },
    bottom: { style: 'thin' },
    right: { style: 'thin' },
  }
  cell.alignment = { vertical: 'middle', wrapText: true, horizontal: horizontal ?? 'center' }
}

/**
 * 设置头部样式
 */
export function addHeaderStyle(row, attr) {
  row.eachCell((cell) => addCellStyle(cell, attr))
}

/**
 * 设置工作表样式
 */
export function setWorksheetStyles(worksheet, isMultiHeader) {
  // 设置单元格样式
  worksheet.eachRow({ includeEmpty: true }, function (row, index) {
    row.eachCell({ includeEmpty: true }, function (cell) {
      addCellStyle(cell, { bold: false })
    })
  })

  // 性能优化：仅为表头添加样式，避免对 5000*列数 的单元格逐一设置样式

  // 添加表头样式
  if (isMultiHeader) {
    addHeaderStyle(worksheet.getRow(1), { color: DEFAULT_HEADER_FG_COLOR, bold: true })
    addHeaderStyle(worksheet.getRow(2), { color: DEFAULT_HEADER_FG_COLOR, bold: true })
  } else {
    addHeaderStyle(worksheet.getRow(1), { color: DEFAULT_HEADER_FG_COLOR, bold: true })
  }
}