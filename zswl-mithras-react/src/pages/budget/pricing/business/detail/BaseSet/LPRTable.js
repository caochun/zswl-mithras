import { Access, Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import { Card, Col, Row } from 'antd'
import { InputColumn } from '@/components/Format'
import { uniqueId } from 'lodash'
import { saveServer } from '@/utils'

function Index({ loadApi }) {
  const reload = async () => {
    table.search()
    rightTable.search()
  }

  const leftColumns = [
    InputColumn({ title: '时间', dataIndex: 'lprDate' }),
    InputColumn({ title: '一年期', dataIndex: 'oneYear' }),
    InputColumn({ title: '五年期', dataIndex: 'fiveYear' }),
  ]
  const rightColumns = [
    InputColumn({ title: '时间', dataIndex: 'lprDate' }),
    InputColumn({ title: '一年期季度均值', dataIndex: 'oneYear' }),
    InputColumn({ title: '五年期季度均值', dataIndex: 'fiveYear' }),
  ]
  const table = useMemo(() => {
    return new TableStore({
      pagination: false,
      request: async (params) => {
        const { list, ...restRes } = await loadApi({ ...params, frequency: 'MONTH' })
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
  return (
    <Card title="LPR" style={{ marginTop: 12 }}>
      <Row gutter={12}>
        <Col span={12}>
          <Table onFilter={(key,val) => saveServer('BaseSet_LPRTable_1',val)} columnsFilter="BaseSet_LPRTable_1" store={table} editable={false} scroll={{ x: 'auto' }} columns={leftColumns} />
        </Col>
        <Col span={12}>
          <Table
          columnsFilter="BaseSet_LPRTable_2"
          onFilter={(key,val) => saveServer('BaseSet_LPRTable_2',val)}
            store={rightTable}
            editable={false}
            scroll={{ x: 'auto' }}
            columns={rightColumns}
          />
        </Col>
      </Row>
    </Card>
  )
}

export default observer(Index)
