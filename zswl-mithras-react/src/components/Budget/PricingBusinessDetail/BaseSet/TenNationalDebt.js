import { Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { Card, Col, Row } from 'antd'
import { ImportAction } from '@/components/Actions'
import { uniqueId } from 'lodash'
import { saveServer } from '@/utils'

const leftColumns = getTableColumns(ALL_COLUMNS, ['时间', '每日值'])
const centerColumns = getTableColumns(ALL_COLUMNS, ['时间', { title: '每日值', rename: '月均值' }])
const rightColumns = getTableColumns(ALL_COLUMNS, ['时间', { title: '每日值', rename: '季度均值' }])
function BudgetPricingBusinessBaseSetTenNationalDebt({ path, loadApi, title, canEdit, uploadApi }) {
  const leftTable = useMemo(() => {
    return new TableStore({
      pagination: { showSizeChanger: false, showQuickJumper: false, simple: true, size: 'small' },
      request: async (params) => {
        const { list, ...restRes } = await loadApi({ ...params, frequency: 'DAY' })
        return {
          list: list.map(({ id, ...rest }) => ({ id: id?.value ?? id ?? uniqueId(), ...rest })),
          ...restRes,
        }
      },
    })
  }, [])
  const rightTable = useMemo(() => {
    return new TableStore({
      pagination: false,
      request: async (params) => {
        const { list, ...restRes } = await loadApi({ ...params, frequency: 'SEASON' })
        return {
          list: list.map(({ id, ...rest }) => ({ id: id?.value ?? id ?? uniqueId(), ...rest })),
          ...restRes,
        }
      },
    })
  }, [])
  const centerTable = useMemo(() => {
    return new TableStore({
      pagination: {
        showSizeChanger: false,
        showQuickJumper: false,
        simple: true,
        size: 'small',
      },
      request: async (params) => {
        const { list, ...restRes } = await loadApi({ ...params, frequency: 'MONTH' })
        return {
          list: list.map(({ id, ...rest }) => ({ id: id?.value ?? id ?? uniqueId(), ...rest })),
          ...restRes,
        }
      },
    })
  }, [])

  const reload = async () => {
    leftTable.search()
    centerTable.search()
    rightTable.search()
  }

  return (
    <Card
      title={title}
      extra={[canEdit && <ImportAction key="import" upload={uploadApi} beforeUpload={reload} />]}
    >
      <Row gutter={12}>
        <Col span={8}>
          <Table onFilter={(key,val) => saveServer('BaseSet_TenNationalDebt_1',val)} columnsFilter="BaseSet_TenNationalDebt_1" store={leftTable} editable={false} columns={leftColumns} scroll={{ x: false }} />
        </Col>
        <Col span={8}>
          <Table
          columnsFilter="BaseSet_TenNationalDebt_2"
          onFilter={(key,val) => saveServer('BaseSet_TenNationalDebt_2',val)}
            store={centerTable}
            editable={false}
            columns={centerColumns}
            scroll={{ x: false }}
          />
        </Col>
        <Col span={8}>
          <Table onFilter={(key,val) => saveServer('BaseSet_TenNationalDebt_3',val)} columnsFilter="BaseSet_TenNationalDebt_3" store={rightTable} editable={false} columns={rightColumns} scroll={{ x: false }} />
        </Col>
      </Row>
    </Card>
  )
}

export default observer(BudgetPricingBusinessBaseSetTenNationalDebt)
