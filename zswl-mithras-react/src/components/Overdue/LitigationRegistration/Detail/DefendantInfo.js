import { observer } from '@zswl/admin'
import { Table, Button, Modal, Form, Select, Input } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import litigationRegistrationApi from '@/api/overdue/litigationRegistrationApi'
import { saveServer } from '@/utils'

const AddModal = observer(({ store }) => {
  const { contractList } = store
  const [customerList, setCustomerList] = useState([])
  const getCustomer = async () => {
    if (contractList.length === 0) return []
    const contractIds = contractList.map((item) => item.value)
    const res = await litigationRegistrationApi.postContractClient({ contractIds })
    setCustomerList(res)
  }

  useEffect(() => {
    getCustomer()
  }, [contractList])
  return (
    <Modal store={store.defendantModal} title="新增被告">
      <Form>
        <Form.Item name={'customerId'} label={'被告名称'} rules={[{ required: true }]}>
          <Select options={customerList} fieldNames={{ label: 'name', value: 'clientId' }}></Select>
        </Form.Item>
        <Form.Item dependencies={['customerId']}>
          {({ getFieldValue, setFieldsValue, setFields }) => {
            const customerId = getFieldValue('customerId')
            if (!customerId) return null
            const { certificateNumber, certificateType, role, name } = customerList.find(
              (item) => item.clientId === customerId
            )

            setFields([
              { name: 'certificateNumber', value: certificateNumber },
              { name: 'certificateType', value: certificateType },
              { name: 'role', value: role },
              { name: 'name', value: name },
            ])
            return (
              <>
                <Form.Item name={'name'} hidden>
                  <Input disabled />
                </Form.Item>
                <Form.Item name={'role'} label={'合同地位'}>
                  <Input disabled />
                </Form.Item>
                <Form.Item name={'certificateType'} label={'证件类型'}>
                  <Input disabled />
                </Form.Item>
                <Form.Item name={'certificateNumber'} label={'证件号码'}>
                  <Input disabled />
                </Form.Item>
              </>
            )
          }}
        </Form.Item>
      </Form>
    </Modal>
  )
})
const DefendantInfo = ({ store, canEdit }) => {
  const columns = [
    { title: '被告名称', dataIndex: 'name' },
    { title: '合同地位', dataIndex: 'role' },
    { title: '证件类型', dataIndex: 'certificateType' },
    { title: '证件号码', dataIndex: 'certificateNumber' },
  ]
  const { rows } = store.defendantTable.getSelected()
  const disabled = !rows.length

  return (
    <>
      <h2>被告信息 </h2>
      <Table
        columnsFilter={'litigationRegistration_detail_DefendantInfo'}
        onFilter={(key, val) => saveServer('litigationRegistration_detail_DefendantInfo', val)}
        store={store.defendantTable}
        columns={columns}
        editable={false}
        selectable
        actions={[
          <Button.Add
            key="add"
            type="primary"
            onClick={() => store.defendantModal.open()}
            disabled={!canEdit}
          >
            新增被告
          </Button.Add>,
          <Button.Delete
            key="delete"
            type="primary"
            onClick={store.deleteDefendant}
            disabled={disabled}
          >
            删除
          </Button.Delete>,
        ]}
      />
      <AddModal store={store} />
    </>
  )
}

export default observer(DefendantInfo)
