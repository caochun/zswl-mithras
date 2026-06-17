import { observer, getQuery } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { Table, TableStore, Button, App } from '@zswl/components'
import TableExport from '@/components/Actions/TableExport'
import api from '@/api/budgetManagement/assessmentApi'
import { AmountColumn } from '@/components/Format'
import { Tooltip } from 'antd'

const defaultFields = [
  { fieldName: '资金计划偏离度（%）', fieldLevel: 1, key: 'deviationDegreePlan' },
  { fieldName: '项目准确度（%）', fieldLevel: 1, key: 'projectAccuracy' },
  {
    fieldName: '提报及时性-未及时提报次数-资金计划',
    fieldLevel: 1,
    key: 'failedReportFundingPlan',
  },
  { fieldName: '提报及时性-未及时提报次数-周报', fieldLevel: 1, key: 'failedReportWeek' },
  { fieldName: '提报及时性-延迟天数-资金计划', fieldLevel: 1, key: 'delayDaysFundingPlan' },
  { fieldName: '提报及时性-延迟天数-周报', fieldLevel: 1, key: 'delayDaysWeek' },
]

const generateColumns = (columns) => {
  const newColumns = [
    {
      dataIndex: 'fieldName',
      title: '指标',
      editable: false,
      fixed: 'left',
      render: (text, record) => {
        return (
          <Tooltip title={text}>
            <div style={{ fontWeight: record.fieldLevel === 2 ? 'bold' : 'normal' }}>{text}</div>
          </Tooltip>
        )
      },
    },
  ]
  columns.forEach(({ deptId, deptName }) => {
    newColumns.push({
      dataIndex: `${deptId}_current`,
      title: deptName,
      children: [
        AmountColumn({
          dataIndex: `${deptId}_currentMonth`,
          title: '本月数',
          editable: false,
          onCell: (_, index) => {
            return <div>{index}</div>
          },
        }),
        AmountColumn({
          dataIndex: `${deptId}_currentYear`,
          title: '本年累计',
          editable: false,
        }),
      ],
    })
  })
  return newColumns
}
function transformDataToTable(deptList = []) {
  // 生成表头
  const columns =
    deptList?.map((dept) => ({
      deptId: dept.deptId,
      deptName: dept.deptName,
    })) ?? []

  const dataSource = defaultFields
  for (let i = 0; i < defaultFields.length; i++) {
    const row = {}
    deptList?.forEach(({ deptId, monthDate, yearTotalDate }) => {
      row[`${deptId}_currentMonth`] = monthDate[defaultFields[i].key]
      row[`${deptId}_currentYear`] = yearTotalDate[defaultFields[i].key]
    })
    dataSource[i] = { ...dataSource[i], ...row }
  }

  return { columns, dataSource }
}

const Data = ({ id }, ref) => {
  const [newColumns, setNewColumns] = useState([])
  const [deptList, setDeptList] = useState([])

  const tableStore = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async (params) => {
          const res = await api.getExaminePayPlanExecuteList({ ...params, budgetExamineId: id })
          const { columns, dataSource } = transformDataToTable(res)
          setNewColumns(generateColumns(columns))
          setDeptList(columns)
          return dataSource
        },
      }),
    []
  )

  return (
    <Table
      bordered
      columns={newColumns}
      resizable
      store={tableStore}
      editable={false}
      columnWidth={180}
      rowKey="fieldNameId"
      extra={[
        <TableExport table={tableStore} otherExcelProps={{ fileName: '投放计划执行情况表' }} />,
      ]}
    />
  )
}

export default observer(Data)
