import { Modal, Form, Button } from '@zswl/components'
import { InputNumber, Space } from 'antd'
import { rules } from '@/utils'

const { Item } = Form

const Index = ({ store }) => {
  return (
    <Modal
      title={'结束投放'}
      store={store.collectionDayModal}
      okText={'确定'}
      destroyOnClose
      width={400}
      footer={
        <Space>
          <Button onClick={store.collectionDayModal.close}>取消</Button>
          <Button type="primary" onClick={store.updateCollectionDay}>
            保存收款日
          </Button>
          <Button type="primary" onClick={store.paymentFinish}>
            结束投放
          </Button>
        </Space>
      }
    >
      <Form preserve={false}>
        <Item label={'收款日'} name={'defaultCollectionDay'} rules={[rules.required()]}>
          <InputNumber style={{ width: '100%' }} />
        </Item>
      </Form>
    </Modal>
  )
}
export default Index
