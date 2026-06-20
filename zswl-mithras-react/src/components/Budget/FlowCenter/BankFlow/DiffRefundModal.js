import { observer } from '@zswl/admin'
import { Button, Form, Modal, Select } from '@zswl/components'
import { FormAmount } from '@/components/Form'

const { Item } = Form
function Index({ store }) {
  return (
    <Modal
      title={`软差退款`}
      store={store.diffRefundModal}
      okText={'确定'}
      width={400}
      destroyOnClose
    >
      <Form labelCol={{ span: 8 }}>
        <FormAmount.Item
          label={'退款金额(元)'}
          name={'refundAmount'}
          isRequired
          style={{ width: '100%' }}
        />
      </Form>
    </Modal>
  )
}

export default observer(Index)
