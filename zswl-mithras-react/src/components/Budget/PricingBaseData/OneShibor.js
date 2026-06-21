import { Button, Modal, Page, Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { Card, Col, Row } from 'antd'
import newFtpShiborInterestRateApi from '@/api/budget/pricing/ftp/newFtpShiborInterestRateApi'
import { ImportAction } from '@/components/Actions'
import { FiledFormat } from '@/components/Format'
import moment from 'moment'
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
  'FTP 计价值',
])
function BudgetPricingBaseDataOneShibor({ path }) {
  const leftTable = useMemo(() => {
    return new TableStore({
      pagination: {
        showSizeChanger: false,
        showQuickJumper: false,
        simple: true,
        size: 'small',
      },
      request: (params) => {
        return newFtpShiborInterestRateApi.postRateList(params)
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
      request: (params) => {
        return newFtpShiborInterestRateApi.postRateListprincing(params)
      },
    })
  }, [])
  const reload = () => {
    leftTable.search()
    rightTable.search()
  }
  return (
    <Card
      title="一年期SHIBOR利率"
      extra={
        [
          // <ImportAction
          //   key="import"
          //   upload={newFtpShiborInterestRateApi.postRateImport}
          //   beforeUpload={reload}
          //   access={'newftpshiborinterestrateimport'}
          // />,
        ]
      }
    >
      <Row gutter={12}>
        <Col span={12}>
          <Table
        columnsFilter="baseData_OneShibor_1"
                onFilter={(key,val) => saveServer('baseData_OneShibor_1',val)}

            store={leftTable}
            editable={false}
            columns={leftColumns}
            columnWidth={50}
            scroll={{ x: false }}
          />
        </Col>
        <Col span={12}>
          <Table
        columnsFilter="baseData_OneShibor_2"
                onFilter={(key,val) => saveServer('baseData_OneShibor_2',val)}

            store={rightTable}
            editable={false}
            columns={rightColumns}
            columnWidth={50}
            scroll={{ x: false }}
          />
        </Col>
      </Row>
    </Card>
  )
}

export default observer(BudgetPricingBaseDataOneShibor)
