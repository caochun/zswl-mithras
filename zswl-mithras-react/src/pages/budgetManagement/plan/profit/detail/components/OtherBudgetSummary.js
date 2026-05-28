import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { generateColumns, generateOtherColumns } from '../utils'
import { OrgSelect } from '@/components'

const OtherBudgetSummary = ({ store }) => {
  const [newColumns, setColumns] = useState([])

  useEffect(() => {
    setColumns(generateOtherColumns(store.otherColumns.columns, store.otherColumns.rowSpan))
  }, [JSON.stringify(store.otherColumns)])

  return (
    <>
      <Table
        bordered
        columns={newColumns}
        store={store.otherBudgetSummaryList}
        editable={false}
        scroll={{ x: 'max-content' }}
        columnWidth={140}
        searchbar={[
          {
            label: '部门',
            dataIndex: 'belongDeptId',
            element: <OrgSelect functionCode="selectorgs-buggetNotmonth" />,
          },

          // {
          //   label: '日期',
          //   dataIndex: 'date',
          //   element: <DatePicker.RangePicker />,
          //   transform: (value) => {
          //     console.log('value: ', value)
          //     return value.map((item) => item.format('YYYY-MM-DD'))
          //   },
          // },
        ]}
      />
    </>
  )
}

export default observer(OtherBudgetSummary)
