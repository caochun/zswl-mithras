import { useEffect, useState } from 'react'
import { Upload, Button } from 'antd'
import IconFont from '../Icon'
import { observer } from '@zswl/admin'

//这个地方没必要加进度条，因为他只是本地上传没有接口
const FormUpload = (props) => {
  const { onChange, disabled, value, maxCount, delFile, accept, onRemove, ...rest } = props
  const [fileList, setFileList] = useState([])

  useEffect(() => {
    setFileList(value || [])
  }, [value])
  const uploadProps = {
    name: 'file',
    // accept: type ? type : '.pdf',
    accept,
    maxCount: maxCount || 1,
    onRemove: (val) => {
      const currentData = [...fileList].filter((item) => item.id !== val.id)
      setFileList(currentData)
      onRemove(val)
      onChange(currentData)
    },
    beforeUpload: (file, fileLists) => {
      try {
        const tempFile = [...fileList]
        tempFile.push(file)
        onChange(tempFile)
        setFileList(tempFile)
      } catch (e) {
      }

      return false
    },
    onPreview: (file) => {
      file.id && window.open(`/preview/reportPreview/${file.id}`)
    },
    fileList,
    ...rest,
  }

  return (
    <Upload {...uploadProps}>
      <Button disabled={disabled}>
        <IconFont type="icon-icon_upload" />
        文件上传
      </Button>
    </Upload>
  )
}

export default observer(FormUpload)
