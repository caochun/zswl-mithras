/**
 * Excel 导出通用辅助函数
 */

import { isValidElement } from 'react'

/**
 * 闲时调度（requestIdleCallback）及降级
 */
export function requestIdle(cb, options) {
  try {
    if (typeof window !== 'undefined' && typeof window.requestIdleCallback === 'function') {
      return window.requestIdleCallback(cb, options)
    }
  } catch (e) {}
  // 降级到 setTimeout
  return setTimeout(() => cb({ timeRemaining: () => 0, didTimeout: true }), 16)
}

/**
 * 当前日期时间字符串
 */
export function getCurrentDateString() {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const day = now.getDate()
  const hours = now.getHours()
  const minutes = now.getMinutes()
  const seconds = now.getSeconds()
  return `${year}${month.toString().padStart(2, '0')}${day.toString().padStart(2, '0')}${hours
    .toString()
    .padStart(2, '0')}${minutes.toString().padStart(2, '0')}${seconds.toString().padStart(2, '0')}`
}

/**
 * 计算宽度
 */
export function autoWidthAction(val, width = 10) {
  if (val == null) {
    width = 10
  } else if (val.toString().charCodeAt(0) > 255) {
    width = val.toString().length * 2
  } else {
    width = val.toString().length
  }
  return width
}

/**
 * 获取render 函数值
 */
export function getValueFromRender(renderResult) {
  // 递归从 ReactElement 中提取纯文本，避免使用 DOM 渲染
  const extractText = (node) => {
    if (node == null) return ''
    if (typeof node === 'string' || typeof node === 'number') return String(node)
    if (Array.isArray(node)) return node.map(extractText).join('')
    if (isValidElement(node)) {
      const children = node.props?.children ?? node.props?.value
      return extractText(children)
    }
    if (typeof node === 'object') {
      // 处理特殊的value对象
      if (node.value !== undefined) {
        return extractText(node.value)
      }
      const children = node?.props?.children ?? node.children
      return extractText(children)
    }
    return ''
  }
  return extractText(renderResult)
}

/**
 * columns 数据扁平化
 */
export function flattenArray(columns) {
  let flattenedArray = []
  columns.forEach((item) => {
    if (Array.isArray(item.children)) {
      flattenedArray = flattenedArray.concat(flattenArray(item.children))
    } else {
      flattenedArray.push(item)
    }
  })
  return flattenedArray
}

/**
 * 获取自适应宽度
 */
export function getAutoWidth({ item, dataSource }) {
  const maxArr = [autoWidthAction(item.label)]
  const sample = Array.isArray(dataSource) ? dataSource.slice(0, 200) : []
  sample.forEach((ite) => {
    const str = ite[item.dataIndex] || ''
    if (str) {
      maxArr.push(autoWidthAction(str))
    }
  })
  const width = Math.max(...maxArr) + 5

  return width
}

/**
 * 需要的列数，四舍五入
 */
export function getColumnNumber(width) {
  // 保留函数但不再用于数据行的多列扩展，避免指数级增长
  return Math.max(1, Math.round(width / 20))
}
