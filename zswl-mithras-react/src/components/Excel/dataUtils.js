/**
 * Excel 导出数据处理相关函数
 */

import { requestIdle, getValueFromRender } from './helpers'
import { findColumnByKey } from './headerUtils'
import { DEFAULT_CHUNK_SIZE } from './constants'

/**
 * 处理每行的数据，render 函数值
 */
export function getRowsData({ data, column, key }) {
  const render = column?.excelRender ?? column?.render

  // 首先尝试使用dataIndex获取数据
  const dataValue = data[column.dataIndex]

  if (render && dataValue !== undefined && dataValue !== null) {
    const renderResult = render(dataValue, data)
    if (renderResult) {
      return getValueFromRender(renderResult)
    }
  }

  // 如果没有render函数或render返回空值，直接使用key获取数据
  return data[key]?.toString() ?? ''
}

/**
 * 添加数据到表格
 */
export async function addData2Table({ worksheet, headerKeys, headers, dataSource, columns }) {
  if (!Array.isArray(dataSource) || dataSource.length === 0) {
    return
  }

  // 预计算 key -> column 映射，避免每行都执行查找
  const columnsByKey = headerKeys.map((key) => findColumnByKey({ columns, key }))

  // 闲时分块插入，避免一次性构建全量 allRows 带来内存峰值
  const TOTAL = dataSource.length
  let i = 0
  await new Promise((resolve) => {
    function runChunk(deadline) {
      // 可根据剩余时间动态调整块大小（简单策略：时间少则减半）
      let chunkSize = DEFAULT_CHUNK_SIZE
      try {
        const remaining =
          typeof deadline?.timeRemaining === 'function' ? deadline.timeRemaining() : 0
        if (remaining < 10) chunkSize = Math.max(50, Math.floor(DEFAULT_CHUNK_SIZE / 2))
      } catch (e) {}

      const from = i
      const to = Math.min(i + chunkSize, TOTAL)
      const rows = []

      for (let idx = from; idx < to; idx++) {
        const data = dataSource[idx]
        const row = headerKeys.map((key, cIdx) => {
          const column = columnsByKey[cIdx]
          if (!column) {
            console.warn(`Column not found for key: ${key}`)
            return ''
          }
          const value = getRowsData({ data, column, key })
          return value
        })
        rows.push(row)
      }

      worksheet.addRows(rows)

      i = to
      if (i < TOTAL) {
        requestIdle(runChunk)
      } else {
        resolve(null)
      }
    }
    requestIdle(runChunk)
  })
}
