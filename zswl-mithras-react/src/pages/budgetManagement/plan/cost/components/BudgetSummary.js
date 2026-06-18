import { FC, useState } from 'react'
import { observer } from '@zswl/admin'
import { Button, Table } from '@zswl/components'
import { Input, Space } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import ALL_COLUMNS from '../Column'
import { getTableColumns } from '@/utils'
import { Summary } from '@/components/Table'

const nameColumns = [
  '部门',
  '行业分类',
  '投放额',
  '收入',
  '成本',
  '差价',
  '税费+设备',
  '利润总额',
  '收入',
  '成本',
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)

const BudgetSummary = ({ store }) => {
  const [department, setDepartment] = useState('')
  const [industry, setIndustry] = useState('')

  const handleSearch = () => {
    store.list.search({
      department,
      industry,
    })
  }

  const handleReset = () => {
    setDepartment('')
    setIndustry('')
    store.list.search()
  }

  return (
    <>
      <Table
        columns={columns}
        store={store.list}
        editable={false}
        columnWidth={120}
        searchbar={[
          {
            label: '部门',
            key: 'department',
            type: 'input',
          },
          {
            label: '行业分类',
            key: 'industry',
            type: 'input',
          },
        ]}
        summary={(data) => <Summary columns={columns} sumData={store?.summary} startIndex={1} />}
      />
    </>
  )
}

export default observer(BudgetSummary)
