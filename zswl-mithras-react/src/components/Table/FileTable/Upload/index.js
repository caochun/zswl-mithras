import { Upload, Space, Tooltip, Empty, message } from 'antd'
import { Modal, App } from '@zswl/components'
import { getFileType } from '@/utils'
import DataUpload from '@/components/DataUpload'
import { observer } from '@zswl/admin'

const { Dragger } = DataUpload

function UploadModal({
  modalStore,
  tableStore,
  uploadApi,
  enumList,
  uploadTips,
  uploadProps,
  ...rest
}) {
  const initialValues = modalStore.getInitialValues() ?? {}
  const upload = async (params) => {
    console.log('params: ', params)
    const { fileType, file, config } = params
    const { code, msg } = await uploadApi(
      {
        ...initialValues,
        fileType,
        materialsType: fileType,
        file,
      },
      config
    )
    if (code === 200) {
      message?.success('上传成功')
      tableStore?.search()
      return true
    } else {
      message.info(msg)
      return false
    }
  }

  const getCorporationUploadParams = (fileType) => {
    const { beforeUpload, ...restUploadProps } = uploadProps
    return {
      name: 'file',
      multiple: true,
      // beforeUpload(info) {
      beforeUpload: async (info, config) => {
        try {
          await beforeUpload?.(info, config)
          const code = await upload({
            file: info,
            fileType,
            config,
          })
          console.log('code: ', code)
          return code ? false : Upload.LIST_IGNORE
        } catch (error) {
          return Upload.LIST_IGNORE
        }
      },
      accept: '*',
      ...restUploadProps,
    }
  }

  return (
    <Modal
      title={'附件上传'}
      footer={null}
      store={modalStore}
      okText={'确定'}
      destroyOnClose
      {...rest}
    >
      {uploadTips && <div style={{ marginBottom: 10 }}>{uploadTips}</div>}
      {enumList?.length > 0 ? (
        enumList?.map((item, index) => {
          return (
            <Dragger
              key={item.label}
              {...getCorporationUploadParams(item.value)}
              style={{ marginTop: index !== 0 ? '20px' : '0px' }}
            >
              <p className="ant-upload-text">{item.label}</p>
              <p className="ant-upload-hint">点击/将文件拖拽到这里上传</p>
            </Dragger>
          )
        })
      ) : (
        <Empty />
      )}
    </Modal>
  )
}

export default observer(UploadModal)
