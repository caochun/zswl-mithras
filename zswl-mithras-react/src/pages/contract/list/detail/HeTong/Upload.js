import { Upload, Space, Tooltip, Empty } from 'antd'
import { Modal, App } from '@zswl/components'
import { getFileType } from '@/utils'
import DataUpload from '@/components/DataUpload'

const { Dragger } = DataUpload

function UploadModal({ store }) {
  const { optionsType } = App.getData()
  const getCorporationUploadParams = (contractType) => {
    return {
      name: 'file',
      multiple: true,
      api: async (info, config) => {
        const code = await store.upload({
          file: info,
          contractType,
          config,
        })
        if (code === 200) {
          return false
        } else {
          return Upload.LIST_IGNORE
        }
      },
      // itemRender(originNode, file) {
      //   const { name } = file
      //   const { Icon } = getFileType(file)
      //   return (
      //     <Space size={4}>
      //       <Icon style={{ fontSize: 16 }} />
      //       <Tooltip title={name}>
      //         {name.length > 20 ? name.substring(0, 20) + '...' : name}
      //       </Tooltip>
      //     </Space>
      //   )
      // },
    }
  }

  return (
    <Modal
      title={'合同相关材料上传'}
      footer={null}
      store={store.uploadModal}
      okText={'确定'}
      destroyOnClose
    >
      {optionsType.contractTypeEnum?.length > 0 ? (
        optionsType.contractTypeEnum?.map((item, index) => {
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

export default UploadModal
