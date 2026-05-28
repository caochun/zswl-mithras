/**
 * Excel 导出表头相关工具函数
 */

import { getValueFromRender, getAutoWidth } from './helpers'
import { WIDTH_SAMPLE_SIZE, DEFAULT_COLUMN_WIDTH } from './constants'

/**
 * 递归获取所有层级的表头数据
 */
export function getMultiLevelHeaders(columns) {
  const result = []
  const maxLevel = getMaxLevel(columns)

  function fillHeaders(nodes, level, startIndex) {
    let currentIndex = startIndex

    nodes.forEach((node) => {
      if (Array.isArray(node.children)) {
        // 非叶子节点
        const childLeafCount = countLeafNodes(node.children)

        // 在当前层级添加标题
        for (let i = 0; i < childLeafCount; i++) {
          result[level][currentIndex + i] = node.title
        }

        // 递归处理子节点
        fillHeaders(node.children, level + 1, currentIndex)
        currentIndex += childLeafCount
      } else {
        // 叶子节点，需要在所有层级添加标题（用于垂直合并）
        for (let i = level; i < maxLevel; i++) {
          result[i][currentIndex] = node.title
        }
        currentIndex++
      }
    })
  }

  // 初始化结果数组
  for (let i = 0; i < maxLevel; i++) {
    result[i] = []
  }

  fillHeaders(columns, 0, 0)
  return result
}

/**
 * 计算叶子节点数量
 */
export function countLeafNodes(columns) {
  let count = 0
  columns.forEach((item) => {
    if (Array.isArray(item.children)) {
      count += countLeafNodes(item.children)
    } else {
      count += 1
    }
  })
  return count
}

/**
 * 获取所有的 dataIndex（叶子节点）
 */
export function getAllDataIndexes(columns, result = []) {
  if (!Array.isArray(columns)) return result

  columns.forEach((item) => {
    if (Array.isArray(item.children) && item.children.length > 0) {
      // 递归处理子节点
      getAllDataIndexes(item.children, result)
    } else if (item.dataIndex) {
      // 叶子节点，有dataIndex才添加
      result.push(item.dataIndex)
    }
  })
  return result
}

/**
 * 根据 antd 的 column 生成 exceljs 的 column
 */
export function generateHeaders({ columns, dataSource }) {
  // 只处理叶子节点，跳过父节点
  const leafColumns = []

  function extractLeafColumns(nodes) {
    nodes.forEach((node) => {
      if (Array.isArray(node.children) && node.children.length > 0) {
        extractLeafColumns(node.children)
      } else if (node.dataIndex) {
        leafColumns.push(node)
      }
    })
  }

  extractLeafColumns(columns || [])

  const headers = leafColumns.map((col) => {
    const obj = {
      header: `${getValueFromRender(col.title)}`,
      key: col.dataIndex,
      width: getAutoWidth({ item: col, dataSource }),
    }
    return obj
  })

  return headers
}

/**
 * 递归查找列
 */
export function findColumnByKey({ columns, key }) {
  if (!key || !Array.isArray(columns)) return null

  // 递归查找函数
  function findColumn(nodes) {
    for (const node of nodes) {
      if (node.dataIndex === key) {
        return node
      }
      if (Array.isArray(node.children) && node.children.length > 0) {
        const found = findColumn(node.children)
        if (found) return found
      }
    }
    return null
  }

  return findColumn(columns)
}

/**
 * 获取最大层级深度
 */
export function getMaxLevel(columns) {
  if (!columns || columns.length === 0) return 0

  let maxLevel = 0

  function traverse(nodes, currentLevel) {
    maxLevel = Math.max(maxLevel, currentLevel)

    nodes.forEach((node) => {
      if (Array.isArray(node.children)) {
        traverse(node.children, currentLevel + 1)
      }
    })
  }

  traverse(columns, 1) // 从第1层开始
  return maxLevel
}