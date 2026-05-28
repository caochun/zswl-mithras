import { Upload, Space, Tooltip, Typography } from 'antd'
import { UploadOutlined } from '@ant-design/icons'
import { isPlainObject, isEmpty } from 'lodash'
import { Button } from '@zswl/components'
import { getFileType } from '@/utils'

const { Text } = Typography
const Dragger = Upload.Dragger

/**
 * 文件上传
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
  api,
  multiple = true,
  extra,
  control,
  children,
  ...props
}) => {
  const fileList = optimizeValue(value)
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
  const otherProps = control
    ? {
        fileList,
        onChange: handleChange,
      }
    : {}
  const remove = (file) => {
    onChange?.(fileList.filter((item) => item.uid !== file.uid))
  }

  const beforeUpload = () => {
    // todo:其他判断
    return true
  }
  const handleCustomRequest = (e) => {
    console.log(123, e)
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
        customRequest={api ? handleCustomRequest : undefined}
        itemRender={(originNode, file) => {
          const { status, name } = file
          const { Icon } = getFileType(file)
          if (status === 'done') {
            const display = name.substring(name.indexOf('-') + 1)
            return (
              <Space size={4}>
                <Icon style={{ fontSize: 16 }} />
                {/* <Tooltip title={name}>
                {name.length > 20 ? name.substring(0, 20) + '...' : name}
              </Tooltip> */}
                <Text style={{ width: 140 }} ellipsis={{ tooltip: display }}>
                  {display}
                </Text>
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
          }
          return originNode
        }}
      >
        <>
          {children || (
            <Button icon={<UploadOutlined />} disabled={props.disabled}>
              文件上传
            </Button>
          )}
          <div>{extra}</div>
        </>
      </Upload>
      {/* <div style={{ fontSize: 14, color: '#5F6267CF' }}>支持扩展名：{accept || type}</div> */}
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
      const { fileName, filePath, key, fileType } = item
      originalList.push({ fileName, filePath, key, fileType })
    }
  })
  return { fileList, originalList }
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
  const fileList = optimizeValue(value)
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
    console.log('api2:', api)

    if (api) {
      api(file, {
        timeout: 0,
        type: 'upload',
        onUploadProgress({ loaded, total }) {
          // console.log('loaded:', loaded)
          // console.log('fileList2:', fileList)
          onProgress({ percent: (loaded / total) * 100 })
        },
      })
        .then((res) => {
          onSuccess()
          // onSuccess({
          //   uid: '2',
          //   name: '3123',
          //   status: 'done',
          //   url: '21314423',
          // })
        })
        .catch(onError)
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

export default DataUpload
