import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { ApiSelect } from '@/components/Select'
import policyManageApi from '@/api/afterLease/policyManageApi'
import { useState } from 'react'

const { Item } = Form
function AddModal({ store }) {
  const [form] = Form.useForm()
  const [contractList, setContractList] = useState([])
  const projChange = async (value) => {
    const res = await policyManageApi.getContractList({ projId: value })
    setContractList(res)
    form.setFieldValue('paymentId', null)
  }
  return (
    <Modal title={'新增保单'} store={store.addModal} okText={'确认'} destroyOnClose width={450}>
      <Form form={form} labelCol={{ span: 6 }}>
        <Item label={'项目名称'} name="projectId" rules={[{ required: true, message: '请选择' }]}>
          <ApiSelect api={policyManageApi.getProjList} onChange={projChange} />
        </Item>
        <Item label={'合同编号'} name="paymentId" rules={[{ required: true, message: '请选择' }]}>
          <Select options={contractList} />
        </Item>
        {/* {paymentId && (
          <Base id={paymentId} isFormApproval={false} canEditFlag={true} store={store} />
        )} */}
      </Form>
    </Modal>
  )
}

export default observer(AddModal)
