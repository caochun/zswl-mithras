/**
 * Excel 导出合并单元格相关函数
 */

import { getMaxLevel, countLeafNodes } from './headerUtils'

/**
 * 递归合并多层级表头
 */
export function mergeMultiLevelHeaders(worksheet, columns, startRow) {
  const maxLevel = getMaxLevel(columns)

  // 水平合并：合并相同标题的单元格
  for (let level = 0; level < maxLevel; level++) {
    const currentRow = startRow + level
    let startCol = 1

    function mergeHorizontal(nodes, currentLevel, colIndex) {
      let currentCol = colIndex

      nodes.forEach((node) => {
        if (Array.isArray(node.children)) {
          const childCount = countLeafNodes(node.children)

          if (currentLevel === level && childCount > 1) {
            // 水平合并相同标题
            try {
              worksheet.mergeCells(currentRow, currentCol, currentRow, currentCol + childCount - 1)
              const cell = worksheet.getCell(currentRow, currentCol)
              cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true }
            } catch (error) {
              console.warn('水平合并失败:', error.message)
            }
          }

          currentCol = mergeHorizontal(node.children, currentLevel + 1, currentCol)
        } else {
          currentCol++
        }
      })

      return currentCol
    }

    mergeHorizontal(columns, 0, startCol)
  }

  // 垂直合并：合并只有一个子节点的节点，以及独立的叶子节点
  let colIndex = 1
  function mergeVertical(nodes, currentLevel, colIndex) {
    let currentCol = colIndex

    nodes.forEach((node) => {
      if (Array.isArray(node.children)) {
        const childCount = countLeafNodes(node.children)

        // ✅ 正确的垂直合并逻辑：只有当子节点只有一个时才需要垂直合并
        if (childCount === 1 && currentLevel < maxLevel - 1) {
          try {
            worksheet.mergeCells(
              startRow + currentLevel,
              currentCol,
              startRow + maxLevel - 1,
              currentCol
            )
            const cell = worksheet.getCell(startRow + currentLevel, currentCol)
            cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true }
          } catch (error) {
            console.warn('垂直合并失败:', error.message)
          }
        }

        currentCol = mergeVertical(node.children, currentLevel + 1, currentCol)
      } else {
        // 叶子节点：如果当前层级不是最后一层，需要垂直合并
        if (currentLevel < maxLevel - 1) {
          try {
            worksheet.mergeCells(
              startRow + currentLevel,
              currentCol,
              startRow + maxLevel - 1,
              currentCol
            )
            const cell = worksheet.getCell(startRow + currentLevel, currentCol)
            cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true }
          } catch (error) {
            console.warn('垂直合并失败:', error.message)
          }
        }
        currentCol++
      }
    })

    return currentCol
  }

  mergeVertical(columns, 0, 1)
}