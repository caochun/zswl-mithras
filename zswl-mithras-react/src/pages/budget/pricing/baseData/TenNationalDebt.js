import { Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { Card, Col, Row } from 'antd'
import newFtpTreasuryBondYieldApi from '@/api/budget/pricing/ftp/newFtpTreasuryBondYieldApi'
import { ImportAction } from '@/components/Actions'
import moment from 'moment'
import { FiledFormat } from '@/components/Format'
import { saveServer } from '@/utils'

const leftColumns = getTableColumns(ALL_COLUMNS, [
  { title: '时间', dataIndex: 'date' },
  '当前值(%)',
])
const rightColumns = getTableColumns(ALL_COLUMNS, [
  {
    title: '时间',
    render: (val) => <FiledFormat value={moment(val).format('yyyy-MM')} />,
    width: 80,
  },
  '波动幅度(%)',
  'FTP 计价值',
])
function Index({ path }) {
  const leftTable = useMemo(() => {
    return new TableStore({
      pagination: {
        showSizeChanger: false,
        showQuickJumper: false,
        simple: true,
        size: 'small',
      },
      request: async (params) => {
        const res = await newFtpTreasuryBondYieldApi.postYieldList(params)
        return res
      },
    })
  }, [])
  const rightTable = useMemo(() => {
    return new TableStore({
      pagination: {
        showSizeChanger: false,
        showQuickJumper: false,
        simple: true,
        size: 'small',
      },
      request: async (params) => {
        const res = await newFtpTreasuryBondYieldApi.postYieldListpricing(params)
        return res
      },
    })
  }, [])
  const reload = () => {
    leftTable.search()
    rightTable.search()
  }
  return (
    <Card
      title="十年期国债收益率"
      extra={
        [
          // <ImportAction
          //   key="import"
          //   upload={newFtpTreasuryBondYieldApi.postYieldImport}
          //   beforeUpload={reload}
          //   access={'newftptreasurybondyieldimport'}
          // />,
        ]
      }
    >
      <Row gutter={12}>
        <Col span={12}>
          <Table onFilter={(key,val) => saveServer('baseData_TenNationalDebt_1',val)} columnsFilter="baseData_TenNationalDebt_1" store={leftTable} editable={false} columns={leftColumns} scroll={{ x: false }} />
        </Col>
        <Col span={12}>
          <Table onFilter={(key,val) => saveServer('baseData_TenNationalDebt_2',val)} columnsFilter="baseData_TenNationalDebt_2" store={rightTable} editable={false} columns={rightColumns} scroll={{ x: false }} />
        </Col>
      </Row>
    </Card>
  )
}

export default observer(Index)
