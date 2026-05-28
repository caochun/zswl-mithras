import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { Form, Modal, Select } from '@zswl/components'
import IconFont from '@/components/Icon'
import styles from '../index.less'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={`导入合同相关材料`} store={store.$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'合同类型'}
          name={'contractType'}
          rules={[{ required: true, message: '请选择合同类型！' }]}
        >
          <Select
            placeholder="请选择"
            options={[
              { label: '质押合同', value: 'PLEDGE_CONTRACT' },
              { label: '其它', value: 'OTHER_CONTRACT' },
            ]}
          />
        </Item>
        <Item label={'合同文件'} name={'file'} rules={[{ required: true, message: '请上传文件!' }]}>
          <DataUpload accept="*" maxCount={1}></DataUpload>
        </Item>
      </Form>
      <div className={styles.tip}>
        <div className={styles.title}>
          <IconFont className={styles.icon} type="icon-icon_info_filled" />
          合同创建时，只允许上传质押合同和其他；剩余合同为标准化模板，只允许通过系统生成！
        </div>
      </div>
    </Modal>
  )
}

export default observer(Index)
