/**
 * Excel 导出核心功能函数
 */

import { getCurrentDateString, getValueFromRender } from './helpers'
import { DEFAULT_ROW_HEIGHT, DEFAULT_COLUMN_WIDTH, DEFAULT_HEADER_FG_COLOR } from './constants'
import { getMultiLevelHeaders, generateHeaders, getAllDataIndexes } from './headerUtils'
import { addData2Table } from './dataUtils'
import { mergeMultiLevelHeaders } from './mergeUtils'
import { setWorksheetStyles, addHeaderStyle } from './styleUtils'

/**
 * 前端浏览器下载，format: xlsx、csv
 */
export async function saveWorkbook({ workbook, format, fileName }) {
  const uint8Array =
    format === 'xlsx' ? await workbook.xlsx.writeBuffer() : await workbook.csv.writeBuffer()
  const newFileName = fileName || getCurrentDateString()

  const blob = new Blob([uint8Array], { type: 'application/octet-binary' })
  if (window.navigator.msSaveOrOpenBlob) {
    // msSaveOrOpenBlob方法返回boolean值
    navigator.msSaveBlob(blob, newFileName + `.${format}`)
  } else {
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = newFileName + `.${format}`
    link.click()
    window.URL.revokeObjectURL(link.href)
  }
}

/**
 * 处理每个工作表
 */
export async function handleEachSheet({ workbook, sheet }) {
  const { sheetName, columns: _columns, dataSource } = sheet
  // 添加sheet
  const worksheet = workbook.addWorksheet(sheetName, {
    properties: {
      defaultRowHeight: DEFAULT_ROW_HEIGHT,
    },
  })
  // 不需要序号
  const columns = _columns.filter((v) => v.key !== 'z-table-serial-column')

  const headers = generateHeaders({ columns, dataSource })

  // 获取多层级表头数据
  const multiLevelHeaders = getMultiLevelHeaders(columns)
  // 用于匹配数据的 keys（只获取叶子节点的 dataIndex）
  const headerKeys = getAllDataIndexes(columns)
  // 添加表头行
  multiLevelHeaders.forEach((headerRow, index) => {
    const row = worksheet.addRow(headerRow.map((title) => getValueFromRender(title)))
    addHeaderStyle(row, { color: DEFAULT_HEADER_FG_COLOR, bold: true })
  })

  // 合并表头单元格
  mergeMultiLevelHeaders(worksheet, columns, 1)

  await addData2Table({ worksheet, headerKeys, headers, dataSource, columns })

  // 为列设置自适应宽度（采样计算），并添加合理的最小/最大边界
  worksheet.columns = headers.map((header) => ({
    key: header.key,
    width: Math.max(10, Math.min(80, header.width || DEFAULT_COLUMN_WIDTH)),
  }))

  // 设置工作表样式
  const isMultiHeader = headers?.some((item) => item.children)
  setWorksheetStyles(worksheet, isMultiHeader)
}
