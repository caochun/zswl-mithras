import { App } from '@zswl/components'
import { FileTwoTone, FileImageTwoTone, FileWordTwoTone, FilePptTwoTone } from '@ant-design/icons'
import { message } from 'antd'
import _ from 'lodash'
import { baseURL, getQjtAc, getSalt } from './base'
import fileListApi from '@/utils/api/fileApi'
import { http } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'

export function download(blobContent, name) {
  const blob = new Blob([blobContent], { type: 'application/octet-stream;charset=utf-8' })
  const link = document.createElement('a')
  if (name) {
    link.download = name
  }
  link.href = window.URL.createObjectURL(blob)
  link.click()
  window.URL.revokeObjectURL(link.href)
}
export function downUrl(url, params) {
  let newValues = _.pickBy({
    ...params,
  })
  const keys = Object.keys(newValues)
  const values = Object.values(newValues)
  const token = JSON.stringify({
    _salt_: getSalt(),
    _qjt_ac_: getQjtAc(),
  })
  if (keys.length === 0) {
    return ''
  }
  let tmpUrl = ''
  for (let i = 0; i < keys.length; i++) {
    if (keys.length && values.length) {
      tmpUrl += `${i === 0 ? '' : '&'}${keys[i]}=${
        Array.isArray(values[i]) ? `${values[i].join('&' + keys[i] + '=')}` : values[i]
      }`
    }
  }
  const urls = baseURL() + `${url}?` + tmpUrl + `&token=` + encodeURI(token)
  return urls
}
export function previewFile(file) {
  if (!file) {
    return
  }
  const { userName } = App.getData().user
  const { fileName, fileType, key } = file
  window.open(
    `/public/preview.html?title=${fileName || ''}&type=${fileType || 'pdf'}&key=${
      key || ''
    }&watermark_txt=${userName || ''}`
  )
}

/**
 * 文件类型
 */
export function getFileType(file) {
  const defaultRes = {
    type: 'file',
    Icon: FileTwoTone,
  }
  if (!file) {
    return defaultRes
  }
  let name = file.name || file.fileName || file
  if (typeof name !== 'string') {
    return defaultRes
  }
  const suffix = name.substring(name.lastIndexOf('.') + 1).toLocaleLowerCase()
  if (['jpg', 'jpeg', 'png', 'gif', 'svg'].includes(suffix)) {
    return {
      type: 'image',
      Icon: FileImageTwoTone,
    }
  }
  if (['doc', 'docx'].includes(suffix)) {
    return {
      type: 'doc',
      Icon: FileWordTwoTone,
    }
  }
  if (['pdf'].includes(suffix)) {
    return {
      type: 'pdf',
      Icon: FilePptTwoTone,
    }
  }
  return defaultRes
}

// 下划线转换驼峰
export function toHump(name) {
  return name.toLowerCase().replace(/\_(\w)/g, (all, letter) => {
    return letter.toUpperCase()
  })
}
// 斜杠转换小t驼峰
export function toHump2(name) {
  return name.toLowerCase().replace(/\/(\w)/g, (all, letter) => {
    return letter
  })
}
// 大写下划线转驼峰
export function toHump3(name) {
  return name.toLowerCase().replace(/\_(\w)/g, (all, letter) => {
    return letter.toUpperCase()
  })
}
export const copyText = (text) => {
  if (navigator.clipboard) {
    // clipboard api 复制
    navigator.clipboard.writeText(text)
  } else {
    const textarea = document.createElement('textarea')
    document.body.appendChild(textarea)
    // 隐藏此输入框
    textarea.style.position = 'fixed'
    textarea.style.clip = 'rect(0 0 0 0)'
    textarea.style.top = '10px'
    // 赋值
    textarea.value = text
    // 选中
    textarea.select()
    // 复制
    document.execCommand('copy', true)
    // 移除输入框
    document.body.removeChild(textarea)
  }
}

export const blobToJson = (blob) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (event) => {
      try {
        const text = event.target.result
        const jsonData = JSON.parse(text)
        resolve(jsonData)
      } catch (error) {
        reject(error)
      }
    }
    reader.onerror = (event) => {
      reject(new Error('Failed to read Blob data.'))
    }
    reader.readAsText(blob)
  })
}

const uploadMinIO = async (urls, files) => {
  console.log('files: ', files)
  console.log('urls: ', urls)
  const uploadList = Object.entries(urls).map(async ([key, url]) => {
    const file = files.find((v) => v.name === key)
    return await fileListApi.postFileUpload()
    // return await http.put(url, file, {
    //   headers: {
    //     'Content-Type': file.type,
    //     token: null,
    //   },
    // })
  })
  return await Promise.all(uploadList)
}

export const saveFile = async (files = [], params) => {
  const functionCode = `${toHump(params.moduleType)}FileUploadPresigned`
  const { fileList } = DataUpload.classify(files)
  const fileNames = fileList.map((v) => v.name)
  const newParams = {
    materialsType: 'default',
    ...params,
    fileNames,
  }
  const uploadFunctionCode = `${toHump(params.moduleType)}FileUpload`
  const uploadList = fileList.map(async (file) => {
    return await fileListApi.postFileUpload({ file, ...newParams }, uploadFunctionCode)
  })
  return await Promise.all(uploadList)
  const { fileUrls } = await fileListApi.postFileUploadPresigned(newParams, functionCode)
  return await uploadMinIO(fileUrls, fileList)
}

export const compare = (beforeArr, afterArr) => {
  let resObj = {
      add: [],
      del: [],
      res: [],
    },
    cenObj = {}
  //把beforeArr数组去重放入cenObj
  for (let i = 0; i < beforeArr.length; i++) {
    cenObj[beforeArr[i]] = beforeArr[i]
  }
  //遍历afterArr，查看其元素是否在cenObj中
  for (let j = 0; j < afterArr.length; j++) {
    if (!cenObj[afterArr[j]]) {
      resObj.add.push(afterArr[j])
    } else {
      console.log('afterArr[j]: ', afterArr[j])
      resObj.res.push(afterArr[j])
      delete cenObj[afterArr[j]]
    }
  }
  for (let k in cenObj) {
    resObj.del.push(k)
  }
  console.log('resObj: ', resObj)
  return resObj
}
