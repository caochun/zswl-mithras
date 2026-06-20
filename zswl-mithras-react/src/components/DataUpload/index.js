import { Upload, Space, Tooltip, Typography, message } from 'antd'
import { UploadOutlined } from '@ant-design/icons'
import { http } from '@zswl/admin'
import { isPlainObject, isEmpty } from 'lodash'
import { Button } from '@zswl/components'
import { getFileType } from '@/utils'
import FileList from '@/components/FileList'
import { useMemo, useState } from 'react'

const Dragger = Upload.Dragger

/**
 * 文件上传组件：历史文件在当前目录下的history里
 * 推荐写法是尽量统一上传下载的api，然后就算不统一，也把api传进来，api可以为一个函数也可以为一个字符串url，
 * 然后把参数写进去，再通过入参判断是否显示预览、下载、删除按钮等。
 * 这个地方改他主要是为了解决进度条的问题，原理就是给他加上handleCustomRequest的onUploadProgress，让他显示进度条，
 * 主要改动就是在原先的写法上，把beforeUpload和onChange里的方法改成api：（）=>{}，api也是一个回调函数，具体写法看下方代码
 */
export function transformToFileObj(item, uid = 0) {
  if (typeof item === 'string') {
    return { url: item, uid, name: item, status: 'done' }
  }
  if (isPlainObject(item) && !isEmpty(item)) {
    return {
      status: 'done',
      name: item.url || item.fileName,
      url: item.url || item.filePath,
      uid,
      ...item,
    }
  }
  return null
}
export function optimizeValue(value) {
  if (!value) {
    return []
  }
  if (typeof value === 'string') {
    try {
      const res = JSON.parse(value)
      return optimizeValue(res)
    } catch {
      return []
    }
  }
  if (isPlainObject(value)) {
    return [transformToFileObj(value)]
  }
  if (!Array.isArray(value)) {
    return []
  }
  return value.map(transformToFileObj).filter(Boolean)
}
const DataUpload = ({
  value,
  accept = '.doc, .docx, .pdf',
  type,
  maxCount = 4,
  onChange,
  onRemove = async () => {},
  api,
  multiple = true,
  extra,
  control,
  children,
  apiParams,
  buttonText = '文件上传',
  showCustomRender = true,
  ...props
}) => {
  const fileList = useMemo(() => optimizeValue(value), [JSON.stringify(value)])
  const handleChange = ({ fileList: list }) => {
    const resList = list.map((item) => {
      const { response, ...other } = item
      return { ...other, ...response }
    })
    return onChange?.(resList.filter((item) => item.status !== 'error'))
  }
  const otherProps = control
    ? {
        fileList,
        onChange: handleChange,
      }
    : {}

  const remove = async (file) => {
    await onRemove(file)
    onChange?.(fileList.filter((item) => item.uid !== file.uid))
  }

  const beforeUpload = () => {
    if (fileList.length >= maxCount) {
      message.error('最多只能上传' + maxCount + '个文件')
      return false
    }
    // todo:其他判断
    return true
  }
  const [loading, setLoading] = useState(false)
  const handleCustomRequest = (e) => {
    const { file, onProgress, onError, onSuccess } = e

    if (typeof api === 'string') {
      http
        .post(
          api,
          { file, ...apiParams },
          {
            timeout: 0,
            type: 'upload',
            onUploadProgress({ loaded, total }) {
              onProgress({ percent: (loaded / total) * 100 }) //显示进度条
            },
          }
        )
        .then((res) => {
          props.getFileRes(res)
          const { filePath, fileName, key, ...rest } = res[0]
          onSuccess({
            url: filePath,
            name: fileName,
            fileName,
            filePath,
            key, // 用来下载文件用的
            ...rest,
          })
          message.success('导入成功！')
        })
    } else if (typeof api === 'function') {
      setLoading(true)
      api(file, {
        timeout: 0,
        type: 'upload',
        onUploadProgress({ loaded, total }) {
          onProgress({ percent: (loaded / total) * 100 })
        },
      })
        .then((res) => {
          const { filePath, fileName, key, ...rest } = res[0]
          onSuccess({
            url: filePath,
            name: fileName,
            fileName,
            filePath,
            key, // 用来下载文件用的
            ...rest,
          })
        })
        .catch(onError)
        .finally(() => setLoading(false))
    }
  }
  const buttonStyle = { padding: 0 }

  return (
    <div>
      <Upload
        {...props}
        {...otherProps}
        multiple={multiple}
        accept={accept || type}
        maxCount={maxCount}
        fileList={fileList}
        beforeUpload={beforeUpload}
        onChange={handleChange}
        showUploadList={{
          showRemoveIcon: false,
        }}
        customRequest={handleCustomRequest}
        itemRender={(originNode, file) => {
          if (!showCustomRender) return null
          const { status, name, filename } = file
          const nameText = name ?? filename
          const { Icon } = getFileType(file)
          return (
            <Space size={4} key={file.id}>
              <Icon style={{ fontSize: 16 }} />
              <Tooltip title={nameText}>
                {nameText?.length > 15 ? nameText.substring(0, 15) + '...' : nameText}
              </Tooltip>

              <Button
                style={buttonStyle}
                type={'link'}
                onClick={() => remove(file)}
                disabled={props.disabled}
              >
                删除
              </Button>
            </Space>
          )
        }}
      >
        <>
          {children || (
            <Button
              icon={<UploadOutlined />}
              disabled={props.disabled}
              type="primary"
              loading={loading}
            >
              {buttonText}
            </Button>
          )}
          <div>{extra}</div>
        </>
      </Upload>
    </div>
  )
}
DataUpload.classify = (list) => {
  if (!list) {
    return { fileList: [], originalList: [] }
  }
  if (!Array.isArray(list)) {
    list = [list]
  }
  const fileList = []
  const originalList = []
  list.forEach((item) => {
    if (item.originFileObj || item instanceof File) {
      fileList.push(item.originFileObj || item)
    }
    if (item.fileName) {
      const { fileName, filePath, key, fileType, id, fileId } = item
      originalList.push({ fileName, filePath, key, fileType, id, fileId })
    }
  })
  return { fileList, originalList }
}
export function wrapFileInfo(data) {
  if (isPlainObject(data)) {
    const { fileUrl, filename, belongId } = data
    return { url: fileUrl, name: filename, uid: belongId || fileUrl, ...data }
  }
  if (Array.isArray(data)) {
    return data.map((item) => wrapFileInfo(item))
  }
  return {}
}
DataUpload.queryFileData = async (ids) => {
  if (ids) {
    if (!Array.isArray(ids)) {
      ids = [ids]
    }
    if (!ids.length) return []
    const res = await http?.get(
      '/materials/download/query',
      { params: { ids } }
      // { functionCode: 'materialsDownloadQuery' }
    )
    return wrapFileInfo(res)
  }
  return []
}
const DraggerFunction = ({
  value,
  accept = '.doc, .docx, .pdf',
  type,
  maxCount = 4,
  onChange,
  api,
  multiple = true,
  extra,
  children,
  ...props
}) => {
  const fileList = useMemo(() => optimizeValue(value), [value])
  const handleChange = ({ fileList: list }) => {
    const resList = list.map((item) => {
      const { response, ...other } = item
      return {
        ...other,
        ...response,
      }
    })
    return onChange?.(resList.filter((item) => item.status !== 'error'))
  }
  const remove = (file) => {
    onChange?.(fileList.filter((item) => item.uid !== file.uid))
  }

  const beforeUpload = () => {
    // todo:其他判断
    return true
  }
  const handleCustomRequest = (e) => {
    const { file, onProgress, onError, onSuccess } = e
    if (api) {
      api(file, {
        timeout: 0,
        type: 'upload',
        onUploadProgress({ loaded, total }) {
          onProgress({ percent: (loaded / total) * 100 })
        },
      })
        .then((res) => {
          onSuccess()
        })
        .catch()
    }
  }
  return (
    <Dragger
      multiple={multiple}
      accept={accept || type}
      maxCount={maxCount}
      // fileList={fileList}
      beforeUpload={beforeUpload}
      onChange={handleChange}
      customRequest={handleCustomRequest}
      {...props}
    >
      {children}
    </Dragger>
  )
}
DataUpload.Dragger = DraggerFunction
DataUpload.List = FileList
export default DataUpload
