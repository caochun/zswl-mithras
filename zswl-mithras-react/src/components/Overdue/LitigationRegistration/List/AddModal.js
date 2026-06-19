import { observer } from '@zswl/admin'
import { Modal, Form, Select, Table, TableStore } from '@zswl/components'
import { AmountColumn } from '@/components/Format'
import { useEffect, useMemo, useState } from 'react'
import litigationRegistrationApi from '@/api/overdue/litigationRegistrationApi'
import { ClientSelect } from '@/components/Select'
import { Col, Row } from 'antd'
import { saveServer } from '@/utils'

const { Item } = Form

const Index = ({ store }) => {
  const table = useMemo(() => new TableStore({}), [])
  const [form] = Form.useForm()
  const columns = [
    { title: '客户名称', dataIndex: 'clientName' },
    AmountColumn({ title: '风险敞口', dataIndex: 'riskExposure' }),
    AmountColumn({ title: '逾期租金', dataIndex: 'overdueAmount' }),
    { title: '当前最大逾期天数', dataIndex: 'overdueDays' },
  ]

  const clientChange = async (clientId) => {
    if (!clientId) return
    const res = await litigationRegistrationApi.getClientOverdueinfo({ clientId })
    form.setFieldsValue({ clientName: res.clientName })
    table.setList([res])
  }
  return (
    <Modal title={'诉讼登记'} width={800} store={store.createModal} destroyOnClose>
      <Form labelCol={{ span: 6 }} form={form}>
        <Row>
          <Col span={12}>
            <Item label="诉讼客户" name="clientId" required>
              <ClientSelect onChange={clientChange} canJump={false} allowClear={false} />
            </Item>
          </Col>
        </Row>

        <Item name="clientName" hidden />

        <Table
          columns={columns}
          store={table}
          editable={false}
          pagination={false}
          scroll={{ x: 'auto' }}
          resizable
          columnsFilter="overdueAdd"
          onFilter={(key,val) => saveServer('overdueAdd',val)}
        />
      </Form>
    </Modal>
  )
}

export default observer(Index)
