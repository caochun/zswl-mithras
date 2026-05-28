// 基于XLSX的前端导出Excel实现

import moment from 'moment'
import * as XLSX from 'xlsx'
import _ from 'lodash'

/**
 * eg: .columns = [
 *    { header: 'Id', key: 'id', wpx: 10 },
 *    { header: 'Name', key: 'name', wch: 32 },
 *    { header: 'D.O.B.', key: 'dob', width: 10, hidden: true }
 * ]
 * data: [{id: 1, name: 'John Doe', dob: new Date(1970,1,1)}]
 * @param columns 定义列属性数组
 * @param data  数据
 * @param name  文件名
 */
export const generateExcel = (columns = [], data = [], name = '') => {
  const headers = columns.map((item) => item.title)
  // https://docs.sheetjs.com/docs/csf/features/#row-and-column-properties
  const otherConfigs = columns.map(({ dataIndex, title, ...item }) => item)

  const dataList = data.map((item) => {
    let obj = {}
    columns.forEach(({ excelRender, title, dataIndex }) => {
      if (_.isFunction(excelRender)) {
        obj[title] = excelRender(item[dataIndex], item)
      } else {
        obj[title] = item[dataIndex]
      }
    })
    return obj
  })

  const workbook = XLSX.utils.book_new()
  workbook.SheetNames.push(name)
  const worksheet = XLSX.utils.json_to_sheet(dataList, {
    header: headers,
  })
  worksheet['!cols'] = otherConfigs
  workbook.Sheets[name] = worksheet

  // 生成Blob数据
  const excelData = XLSX.write(workbook, { type: 'array', bookType: 'xlsx' })
  const blobData = new Blob([excelData], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  })

  // 创建Blob URL
  const blobUrl = URL.createObjectURL(blobData)
  // 创建一个隐藏的<a>标签并设置href属性为Blob URL
  const link = document.createElement('a')
  link.href = blobUrl
  link.target = '_blank'
  link.download = `${name}-${moment().format('YYYY-MM-DD_hh.mm.ss_a')}.xlsx`
  // 触发点击操作，开始下载文件
  link.click()
  // 释放Blob URL
  URL.revokeObjectURL(blobUrl)
}

export default function awaitRequest(limit = 5) {
  let awaitTask = []
  let currentTaskNum = 0

  function run(event, ...args) {
    return new Promise((resolve, reject) => {
      function callbackEvent() {
        currentTaskNum++
        event(...args)
          .then((res) => {
            if (awaitTask.length) {
              const nextTask = awaitTask.shift()
              nextTask()
            }
            resolve(res)
          })
          .catch((e) => {
            console.error(e)
            reject(e)
          })
          .finally(() => {
            currentTaskNum--
          })
      }
      if (currentTaskNum >= limit) {
        awaitTask.push(callbackEvent)
      } else {
        callbackEvent()
      }
    })
  }
  Object.defineProperties(run, {
    clear: {
      value: () => {
        awaitTask = []
      },
    },
  })
  return run
}

/**
 * 循环分页请求，获取全部数据
 * @param {Function} request 请求
 * @param {Number} size 页大小
 * @param {Object} params 其余参数
 * @param {String} listLabel.pageLabel 当前页字段名。默认page
 * @param {String} listLabel.sizeLabel 页大小字段名。默认page_size
 * @param {String} listLabel.totalLabel 总条数字段名。默认total
 * @param {String} listLabel.itemsLabel 数据列表字段名。默认list
 * @returns
 */
export async function loopRequest(
  request,
  size,
  params,
  listLabel = {
    totalLabel: 'total',
    pageLabel: 'page',
    sizeLabel: 'pageSize',
    itemsLabel: 'list',
  }
) {
  const {
    totalLabel = 'total',
    pageLabel = 'page',
    sizeLabel = 'pageSize',
    itemsLabel = 'list',
  } = listLabel
  try {
    const firstRes = await request({
      ...params,
      [sizeLabel]: size,
      [pageLabel]: 1,
    })
    let list = firstRes.data[itemsLabel] || []
    const total = firstRes.data[totalLabel]
    if (total > size) {
      const limit = awaitRequest()
      const restRequest = Array.from({
        length: Math.floor(total / size),
      }).map((_, index) =>
        limit(() =>
          request({
            ...params,
            [sizeLabel]: size,
            [pageLabel]: index + 2,
          })
        )
      )
      const restRes = await Promise.all(restRequest)
      restRes.forEach((res) => {
        if (res.code === 0 && res.data[itemsLabel]) {
          list.push(...res.data[itemsLabel])
        }
      })
    }
    return list
  } catch (e) {
    console.error(e)
    return []
  }
}
