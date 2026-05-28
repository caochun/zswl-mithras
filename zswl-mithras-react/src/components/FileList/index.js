import { Space, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { Button } from '@zswl/components'
import fileListApi from '@/api/common/fileList'
import { downFile, toHump, getFileType } from '@/utils'
import _ from 'lodash'

const { Text } = Typography

function FileList({
  value = [],
  maxWidth = 150,
  tooltip = true,
  downloadApi,
  functionCodeList,
  params,
}) {
  const [list, setList] = useState([])
  const download = async (id, record) => {
    const ids = _.isArray(id) ? [...id] : [id]
    if (_.isFunction(downloadApi)) {
      return await downloadApi({ ids }, record)
    } else {
      const functionCode = functionCodeList?.download || `${toHump(params.moduleType)}FileDownload`
      const res = await fileListApi.getFileDownload({ ...params, fileId: id }, functionCode)
      downFile(res)
    }
  }
  useEffect(() => {
    setList(value)
  }, [value])
  const preview = async ({ id }) => {
    window.open(`/preview/reportPreview/${id}`)
  }
  if (!list.length) {
    return null
  }
  return (
    <>
      <Space direction={'vertical'} size={4} style={{ minWidth: 270 }}>
        {list.map((item, index) => {
          const { fileName, fileType, filename, name } = item
          const theFileName = fileName ?? filename ?? name
          const { Icon, type } = getFileType(fileType)
          const display = theFileName?.substring(theFileName.indexOf('-') + 1)
          return (
            display && (
              <Space key={index} size={4}>
                <Icon style={{ fontSize: 16 }} />
                <Text style={{ maxWidth }} ellipsis={{ tooltip: tooltip && display }}>
                  <span>{display}</span>
                </Text>
                <Button type={'link'} style={{ padding: 0 }} onClick={() => preview(item)}>
                  预览
                </Button>
                <Button
                  icon={false}
                  type={'link'}
                  style={{ padding: 0 }}
                  onClick={() => download(item.id, item)}
                >
                  下载
                </Button>
              </Space>
            )
          )
        })}
      </Space>
    </>
  )
}

export default FileList
