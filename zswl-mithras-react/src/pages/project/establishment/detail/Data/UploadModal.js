import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import store from './store'
import { Button, Input, Upload } from 'antd'
import { rules } from '@/utils'
import { useState } from 'react'
import useGetMap from '@/utils/hooks/useGetMap'

const { Item } = Form
function UploadModal() {
  const { options } = useGetMap()
  const [fileList, setFileList] = useState([])

  const uploadProps = {
    name: 'file',
    beforeUpload: (file) => {
      setFileList([file])
      return false
    },
    onRemove: () => {
      setFileList([])
    },
    fileList,
  }

  const getFile = (e) => {
    console.log('Upload event:', e)

    if (Array.isArray(e)) {
      return e
    }
    return e && e.fileList
  }

  return (
    <Modal title={'立项资料文件上传'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'材料类型'}
          name={'materialsType'}
          rules={[{ required: true, message: '请选择材料类型！' }]}
        >
          <Select options={options.materialsType} />
        </Item>
        <Item
          getValueFromEvent={getFile}
          label={'文件'}
          name={'file'}
          rules={[{ required: true, message: '请上传文件！' }]}
        >
          <Upload {...uploadProps}>
            <Button>文件上传</Button>
          </Upload>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(UploadModal)
