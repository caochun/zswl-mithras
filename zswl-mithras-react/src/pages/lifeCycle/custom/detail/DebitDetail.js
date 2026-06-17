import ProjectDetail from '@/components/AfterLease/RentCollection/Components/ProjectDetail'
import { observer } from '@zswl/admin'
import { Table, TableStore } from '@zswl/components'
import store from './store'
import { Card, Col, Row } from 'antd'
import { useMemo, useState } from 'react'
import customCycleApi from '@/api/lifeCycle/customCycleApi'
import FormAmount from '@/components/Form/FormAmount'
import { SelectOutlined } from '@ant-design/icons'
import { saveServer } from '@/utils'

const Detail = ({ id }) => {
  const [activeType, setActiveType] = useState({})
  const [overdueAmountData, setOverdueAmountData] = useState()
  const table = useMemo(() => {
    return new TableStore({
      pagination: {
        showSizeChanger: false,
        size: 'small',
      },
      request: async (params) => {
        const { overdueAmount, receiptPage } = await customCycleApi.postLifecycleReceipt({
          ...params,
          clientId: id,
        })
        setOverdueAmountData(overdueAmount)
        return receiptPage
      },
    })
  }, [])
  const openDrawer = (record) => {
    setActiveType(record)
    store.$projectDetailDrawer.open()
  }
  const columns = [
    {
      title: '借据编号',
      dataIndex: 'receiptCode',
      width: 180,
    },
    {
      title: '借据卡',
      dataIndex: 'contractId',
      render: (text, record) => {
        return (
          <a onClick={() => openDrawer(record)}>
            <SelectOutlined />
          </a>
        )
      },
    },
  ]
  return (
    <Card title="借据详情" style={{ height: '100%' }}>
      <Row>
        <Col span={24} style={{ display: 'flex', color: 'red', padding: '6px 0' }}>
          <label>逾期金额（万元）：</label>
          <FormAmount.Format initFormat={10000 * 10000} value={overdueAmountData} />
        </Col>
      </Row>
      <Table
        columnsFilter={'custom_detail_DebitDetail'}
        onFilter={(key, val) => saveServer('custom_detail_DebitDetail', val)}
        columns={columns}
        store={table}
        scroll={null}
        pagination={{ simple: false, pageSize: 5 }}
      />
      <ProjectDetail
        baseStore={store}
        isProcess={false}
        {...activeType}
        query={{ canEditFlags: 'false' }}
      />
    </Card>
  )
}

export default observer(Detail)
