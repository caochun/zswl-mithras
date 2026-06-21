import { Input } from 'antd'
import { Form, Modal } from '@zswl/components'
import { NoEnumFileTable } from '@/components/Table'
import { observer } from '@zswl/admin'

const { Item } = Form
const { TextArea } = Input

const params = {
  moduleType: 'RISK_OPINION',
}

const RiskPublicMonitorCloseModal = ({ store }) => {
  const { $closeModal } = store
  const { handleStatus, id } = $closeModal?.getInitialValues() ?? {}
  const [form] = Form.useForm()
  // 处理中、已处理
  const alreadyHandled = ['HANDLE_ING', 'HANDLED'].includes(handleStatus)

  return (
    <Modal title={'关闭'} store={$closeModal} okText={'确定'} destroyOnClose width={1000}>
      <Form form={form} labelCol={{ span: 6 }} preserve={false} layout="vertical">
        <Item label="关闭原因" name="closeReason" rules={[{ required: true, message: '请输入' }]}>
          <TextArea disabled={alreadyHandled}></TextArea>
        </Item>
        <Item name="id" hidden>
          <Input></Input>
        </Item>
      </Form>
      <NoEnumFileTable
        title={'附件'}
        canEdit={!alreadyHandled}
        params={{
          ...params,
          mainId: id,
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </Modal>
  )
}
export default observer(RiskPublicMonitorCloseModal)
