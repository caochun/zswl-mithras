import { Access, Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { Card } from 'antd'
import lprPricingApi from '@/api/budget/pricing/ftp/lprPricingApi'
import moment from 'moment'
import { saveServer } from '@/utils'

function Index() {
  const hasValuation = Access.validate('newFtpLprPricingModify')
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '时间',
        dataIndex: 'lprDate',
        render: (value) => {
          return value ? moment(value).format('YYYY-MM') : '-'
        },
      },
      '一年期LPR(%)',
      '一年期 LPR 计价值',
      '五年期LPR(%)',
      '五年期 LPR 计价值',
    ]

    return [...getTableColumns(ALL_COLUMNS, nameColumns)].filter(Boolean)
  }, [hasValuation])
  const table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        return await lprPricingApi.postPricingList(params)
      },
    })
  }, [])

  return (
    <Card title="LPR" style={{ marginTop: 12 }}>
      <Table
        columnsFilter="pricing_baseData_LPRTable"
                onFilter={(key,val) => saveServer('pricing_baseData_LPRTable',val)}

        store={table}
        editable={false}
        scroll={{
          x: 1200,
        }}
        columns={columns}
      />
    </Card>
  )
}

export default observer(Index)
