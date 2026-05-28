import { useState } from 'react'
import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import Summary from '@/components/Table/Summary'
import { AmountColumn } from '@/components/Format'

const PutProgress = ({ store }) => {
  const [departments, setDepartments] = useState([])
  const [searchDept, setSearchDept] = useState('')

  // 动态生成表头
  const generateColumns = () => {
    const baseColumns = [
      {
        title: '日期',
        dataIndex: 'date',
        width: 100,
        fixed: 'left',
      },
    ]

    // 为每个部门生成子列
    const departmentColumns = departments.map((dept) => ({
      title: dept.name,
      children: [
        AmountColumn({
          title: '公用',
          dataIndex: `${dept.code}_public`,
          width: 120,
          align: 'right',
        }),
        AmountColumn({
          title: '产业',
          dataIndex: `${dept.code}_industry`,
          width: 120,
          align: 'right',
        }),
        AmountColumn({
          title: '国有产业',
          dataIndex: `${dept.code}_stateIndustry`,
          width: 120,
          align: 'right',
        }),
      ],
    }))

    return [...baseColumns, ...departmentColumns]
  }

  return (
    <>
      <Table
        columns={generateColumns()}
        store={store.putProgressList}
        scroll={{ x: 'max-content' }}
        searchbar={[
          {
            label: '部门',
            key: 'department',
            type: 'input',
          },
          {
            label: '日期',
            key: 'date',
            type: 'dateRange',
          },
        ]}
        summary={(data) => (
          <Summary columns={generateColumns()} sumData={store?.summary} startIndex={1} />
        )}
      />
    </>
  )
}

export default observer(PutProgress)
