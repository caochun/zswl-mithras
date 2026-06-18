import { Input } from 'antd'
import { Form, Modal } from '@zswl/components'
import { NoEnumFileTable } from '@/components/Table'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'

const { Item } = Form
const { TextArea } = Input

const params = {
  moduleType: 'RISK_OPINION',
}

const Index = ({ store }) => {
  const { $handleModal, curRecord } = store
  const [form] = Form.useForm()
  const [id, setId] = useState()
  // 处理中、已处理
  const alreadyHandled = ['HANDLE_ING', 'HANDLED'].includes(curRecord?.handleStatus)

  useEffect(() => {
    if ($handleModal.visible) {
      const initialValue = $handleModal.getInitialValues()
      setId(initialValue.id)
    }
  }, [$handleModal.visible])

  return (
    <Modal
      title={'处置'}
      store={$handleModal}
      okText={'确定'}
      destroyOnClose
      width={1000}
      propsBy={(data) => {
        const title = `${alreadyHandled ? '查看' : ''}处置`
        return {
          title,
          footer: alreadyHandled ? null : undefined,
        }
      }}
    >
      <Form form={form} labelCol={{ span: 6 }} preserve={false} layout="vertical">
        <Item label="处置意见" name="advisement" rules={[{ required: true, message: '请输入' }]}>
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
export default observer(Index)
