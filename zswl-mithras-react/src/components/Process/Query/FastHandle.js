import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form
const { TextArea } = Input

function Index({ store }) {
  return (
    <Modal
      title={store.fastHandleData?.typeText}
      store={store.fastHandleModal}
      okText={'确定'}
      destroyOnClose
    >
      <Form preserve={false}>
        <Item label={'原因'} name={'message'}>
          <TextArea></TextArea>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
