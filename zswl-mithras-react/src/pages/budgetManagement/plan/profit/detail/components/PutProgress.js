import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import { DatePicker, Table } from '@zswl/components'
import { generateColumns } from '../utils'
import { OrgSelect } from '@/components/Select'
import { dateRangeTransformV2, rangePresets } from '@/utils'

const PutProgress = ({ store }) => {
  const [newColumns, setColumns] = useState([])
  useEffect(() => {
    setColumns(generateColumns(store.putColumns))
  }, [JSON.stringify(store.putColumns)])
  return (
    <>
      <Table
        bordered
        columns={newColumns}
        store={store.putProgressList}
        editable={false}
        scroll={{ x: 'max-content' }}
        columnWidth={140}
        searchbar={[
          {
            label: '部门',
            dataIndex: 'belongDeptId',
            element: <OrgSelect functionCode="selectorgs-buggetNotmonth" />,
          },
          {
            label: '日期',
            dataIndex: 'queryDate',
            element: <DatePicker.RangePicker ranges={rangePresets} />,
            itemProps: {
              transform: (val) => dateRangeTransformV2(val, 'queryDate', 'yyyy-MM-DD'),
            },
          },
        ]}
      />
    </>
  )
}

export default observer(PutProgress)
