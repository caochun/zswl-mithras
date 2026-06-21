import { Button } from '@zswl/components'
import { message, Upload } from 'antd'
import IconFont from '@/components/Icon'
import { useState } from 'react'

const MODE_MAP = {
  import: {
    text: '导入',
    icon: 'icon-icon_inport',
  },
  upload: {
    text: '上传',
    icon: 'icon-icon_upload',
  },
}
function ImportAction({
  upload,
  beforeUpload,
  type = 'primary',
  mode = 'import',
  btnText,
  access,
  ...rest
}) {
  const { text, icon } = MODE_MAP[mode]
  const [uploadLoading, setUploadLoading] = useState(false)
  const getCorporationUploadParams = () => {
    return {
      name: 'file',
      multiple: true,
      beforeUpload: async (info) => {
        setUploadLoading(true)
        await upload({
          file: info,
        })
          .then((res) => {
            // type:upload 接口里需要加才能拿到 code transformResult: (res) => res.data,
            if (res?.code === 200) {
              message.success(`${text}成功`)
              beforeUpload?.(res)
              setUploadLoading(false)
            } else {
              message.error(`${res.msg}`)
              setUploadLoading(false)
            }
          })
          .catch((e) => {
            setUploadLoading(false)
          })
        return false
      },
      itemRender(originNode, file) {
        return <></>
      },
    }
  }
  return (
    <Upload {...getCorporationUploadParams()} {...rest}>
      <Button key="upload" loading={uploadLoading} type={type} access={access}>
        <IconFont type={icon} />
        {btnText ?? text}
      </Button>
    </Upload>
  )
}

export default ImportAction
