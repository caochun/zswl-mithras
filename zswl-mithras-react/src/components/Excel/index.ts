import * as ExcelJS from 'exceljs'
// const ExcelJS = require('exceljs')
import { saveAs } from 'file-saver'
import JsZip from 'jszip'
import {
  DownLoadExcelProps,
  DownLoadMoreExcelList,
  DownLoadZipProps,
  DownLoadExcelChunkedProps,
} from './type'
import { handleEachSheet, saveWorkbook } from './coreUtils'

// https://github.com/exceljs/exceljs/blob/master/README_zh.md

/**
 * Excel 导出工具函数库
 *
 * 提供完整的 Excel 导出功能，包括：
 * - 多级表头处理
 * - 数据提取和格式化
 * - 单元格合并
 * - 样式设置
 * - 文件下载
 */

// 常量定义
export * from './constants'

// 通用辅助函数
export * from './helpers'
// 表头相关函数
export * from './headerUtils'
// 数据处理相关函数
export * from './dataUtils'
// 样式相关函数
export * from './styleUtils'
// 合并单元格相关函数
export * from './mergeUtils'
// 核心功能函数
export * from './coreUtils'

// Excel导出，支持二级嵌套表头
export const downLoadExcel = async ({
  columns,
  dataSource,
  fileName,
  format = 'xlsx',
  sheetName = 'sheet1',
}: DownLoadExcelProps): Promise<void> => {
  // 创建工作簿
  const workbook = new ExcelJS.Workbook()

  await handleEachSheet({
    workbook,
    sheet: {
      sheetName,
      columns,
      dataSource,
    },
  })
  // 导出excel
  await saveWorkbook({
    workbook,
    fileName,
    format,
  })
}

export const downLoadMorExcel = async ({
  excelList = [],
  fileName,
  format = 'xlsx',
}: DownLoadMoreExcelList): Promise<void> => {
  // 创建工作簿
  const workbook = new ExcelJS.Workbook()

  // 验证数据
  const validExcelList = excelList.filter((item) => {
    if (!item.columns || !item.dataSource) {
      console.warn('Invalid excel item:', item)
      return false
    }
    return true
  })

  if (validExcelList.length === 0) {
    console.error('No valid excel data to export')
    return
  }

  for (let index = 0; index < validExcelList.length; index++) {
    const { columns, dataSource, sheetName } = validExcelList[index]
    await handleEachSheet({
      workbook,
      sheet: {
        sheetName: sheetName ?? `sheet-${index + 1}`,
        columns,
        dataSource,
      },
    })
  }
  // 导出excel
  await saveWorkbook({
    workbook,
    fileName,
    format,
  })
}

// 导出多个文件为zip压缩包
export async function downloadZip(params: DownLoadZipProps) {
  const zip = new JsZip()
  // 待每个文件都写入完之后再生成 zip 文件
  const promises = params?.files?.map(
    async (param) => await handleEachFile(param, zip, param.folderName)
  )
  await Promise.all(promises)
  zip.generateAsync({ type: 'blob' }).then((blob) => {
    saveAs(blob, `${params.zipName}.zip`)
  })
}

async function handleEachFile(param, zip, folderName) {
  // 创建工作簿
  const workbook = new ExcelJS.Workbook()
  if (Array.isArray(param?.sheets)) {
    for (const sheet of param.sheets) {
      await handleEachSheet({ workbook, sheet })
    }
  }
  // 生成 blob
  const data = await workbook.xlsx.writeBuffer()
  const blob = new Blob([data], { type: '' })
  if (folderName) {
    zip.folder(folderName)?.file(`${param.filename}.xlsx`, blob)
  } else {
    // 写入 zip 中一个文件
    zip.file(`${param.filename}.xlsx`, blob)
  }
}

// 大数据量时将数据按行分块，打包为 zip，降低单次内存占用
export const downLoadExcelChunked = async ({
  columns,
  dataSource,
  fileName,
  format = 'xlsx',
  sheetName = 'sheet1',
  chunkSize = 10000,
}: DownLoadExcelChunkedProps): Promise<void> => {
  if (!Array.isArray(dataSource) || dataSource.length === 0) {
    console.warn('No data to export')
    return
  }
  if (format === 'csv') {
    // 单文件 CSV 直接走常规逻辑
    return downLoadExcel({ columns, dataSource, fileName, format, sheetName })
  }
  const zip = new JsZip()
  const total = dataSource.length
  let part = 0
  for (let i = 0; i < total; i += chunkSize) {
    part += 1
    const workbook = new ExcelJS.Workbook()
    await handleEachSheet({
      workbook,
      sheet: {
        sheetName,
        columns,
        dataSource: dataSource.slice(i, Math.min(i + chunkSize, total)),
      },
    })
    const buffer = await workbook.xlsx.writeBuffer()
    const blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    })
    const base = fileName || new Date().toISOString().replace(/[-:.TZ]/g, '')
    zip.file(`${base}_part${part}.xlsx`, blob)
  }
  const zipBlob = await zip.generateAsync({ type: 'blob' })
  const base = fileName || new Date().toISOString().replace(/[-:.TZ]/g, '')
  saveAs(zipBlob, `${base}.zip`)
}
