import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from '../../CustomerRatColumns'
import { saveServer } from '@/utils'

function Index({ path, store }) {
  const canDelete = store.evaluationTable.selectedRowKeys.length > 0
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '客户编号',
        actions: ({ clientCode: name, id }) => [
          {
            name,
            to: `/customer/customerRat/detail/${id}`,
          },
        ],
      },
      { title: '客户名称' },
      { title: '模型名称' },
      { title: '评级得分' },
      { title: '评级认定结果' },
      { title: '生效日期' },
      { title: '失效日期' },
      { title: '评级状态' },
      { title: '发起人' },
      { title: '发起机构' },
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, false)
  }, [])
  return (
    <Table
            columnsFilter={'detail_EvaluationInfo_1'}
            onFilter={(key,val) => saveServer('detail_EvaluationInfo_1',val)}
      store={store.evaluationTable}
      editable={false}
      scroll={{
        x: 1200,
      }}
      columnWidth={140}
      columns={columns}
    />
  )
}

export default observer(Index)
