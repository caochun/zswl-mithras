import { Upload, Space, Tooltip, Empty, message } from 'antd'
import { Modal, App } from '@zswl/components'
import { getFileType } from '@/utils'
import DataUpload from '@/components/DataUpload'
import { observer } from '@zswl/admin'
import _ from 'lodash'

const { Dragger } = DataUpload

function UploadModal({
  modalStore,
  tableStore,
  uploadApi,
  enumList,
  uploadTips,
  refreshApi,
  ...rest
}) {
  const initialValues = modalStore.getInitialValues() ?? {}
  const upload = async (params) => {
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
      if (_.isFunction(refreshApi)) {
        refreshApi()
      }
      tableStore?.search()
      return true
    } else {
      message.info(msg)
      return false
    }
  }
  const getCorporationUploadParams = (fileType) => {
    return {
      name: 'file',
      multiple: true,
      // beforeUpload(info) {
      api: async (info, config) => {
        const code = await upload({
          file: info,
          fileType,
          config,
        })
        // return false
        if (code === 200) {
          return false
        } else {
          return Upload.LIST_IGNORE
        }
      },
      accept: '*',
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
