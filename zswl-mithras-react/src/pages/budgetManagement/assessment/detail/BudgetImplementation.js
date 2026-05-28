import { observer, getQuery } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { Table, TableStore, Button, App } from '@zswl/components'
import TableExport from '@/components/Actions/TableExport'
import api from '../api'
import { AmountColumn, AmountEditable } from '@/components/Format'
import { message, Tooltip } from 'antd'
import FormAmount from '@/components/Form/FormAmount'

const render = (val, record) => {
  if (['备注'].includes(record.fieldName)) {
    return <div style={{ textAlign: 'left' }}>{val ?? '-'}</div>
  }
  return <FormAmount.Format value={val} initFormat={10000} />
}
const commonCell = (_, index) => {
  const isRemark = ['备注'].includes(_.fieldName)
  return {
    colSpan: isRemark ? 0 : 1,
  }
}
const generateColumns = (columns) => {
  const newColumns = [
    {
      dataIndex: 'fieldName',
      title: '指标',
      fixed: 'left',
      editable: false,
      render: (text, record) => {
        return (
          <Tooltip title={text}>
            <div
              style={{
                fontWeight: record.fieldLevel === 2 ? 'bold' : 'normal',
                paddingLeft: record.fieldLevel > 2 ? (record.fieldLevel - 2) * 10 : 0,
              }}
            >
              {text}
            </div>
          </Tooltip>
        )
      },
    },
  ]
  columns.forEach(({ belongDeptId, belongDeptName }) => {
    const isCompany = belongDeptName === '公司'
    newColumns.push({
      dataIndex: `${belongDeptId}_current`,
      title: belongDeptName,
      children: [
        AmountColumn({
          dataIndex: `${belongDeptId}_currentMonth`,
          title: '本月数',
          render,
          onCell: (_, index) => {
            const isRemark = ['备注'].includes(_.fieldName)
            return {
              colSpan: isRemark ? 8 : 1,
            }
          },
        }),
        AmountColumn({
          dataIndex: `${belongDeptId}_totalYear`,
          title: '本年累计',
          onCell: commonCell,
        }),
        AmountColumn({
          dataIndex: `${belongDeptId}_lastYearPeriod`,
          title: '上年同期',
          onCell: commonCell,
        }),
        AmountColumn({
          dataIndex: `${belongDeptId}_onYear`,
          title: '同比(%)',
          onCell: commonCell,
        }),
        AmountColumn({
          dataIndex: `${belongDeptId}_annualBudgetTarget`,
          title: '全年预算目标',
          onCell: commonCell,
        }),
        AmountColumn({
          dataIndex: `${belongDeptId}_progressBudgetTarget`,
          title: '进度预算目标',
          onCell: commonCell,
        }),

        AmountColumn({
          dataIndex: `${belongDeptId}_progressBudgetCompletionRate`,
          title: '进度预算完成率（%）',
          onCell: commonCell,
        }),
        AmountColumn({
          dataIndex: `${belongDeptId}_annualBudgetCompletionRate`,
          title: '全年预算完成率（%）',
          onCell: commonCell,
        }),
      ],
    })
  })
  return newColumns
}
function transformDataToTable(deptList, enumType = 'budgetExamineBenefitEnum') {
  // 生成表头
  const columns = deptList.map((dept) => ({
    belongDeptId: dept.belongDeptId,
    belongDeptName: dept.belongDeptName,
  }))

  // 假设每个 currentMonthList 长度一致
  const rowCount = deptList[0]?.rowList?.length
  const dataSource = []
  const remarkRow = {
    fieldName: '备注',
    fieldNameId: 'remark',
    fieldLevel: 1,
    state: false,
  }
  for (let i = 0; i < rowCount; i++) {
    const row = {}
    deptList.forEach((dept) => {
      row[`${dept.belongDeptId}_currentMonth`] = dept.rowList?.[i]?.currentMonth
      row[`${dept.belongDeptId}_totalYear`] = dept.rowList?.[i]?.totalYear
      row[`${dept.belongDeptId}_lastYearPeriod`] = dept.rowList?.[i]?.lastYearPeriod
      row[`${dept.belongDeptId}_onYear`] = dept.rowList?.[i]?.onYear
      row[`${dept.belongDeptId}_annualBudgetTarget`] = dept.rowList?.[i]?.annualBudgetTarget
      row[`${dept.belongDeptId}_progressBudgetTarget`] = dept.rowList?.[i]?.progressBudgetTarget
      row[`${dept.belongDeptId}_progressBudgetCompletionRate`] =
        dept.rowList?.[i]?.progressBudgetCompletionRate
      row[`${dept.belongDeptId}_annualBudgetCompletionRate`] =
        dept.rowList?.[i]?.annualBudgetCompletionRate
      const match = App.matchOption(enumType, dept.rowList?.[i]?.fieldName) ?? {}
      row.fieldName = match.label
      row.fieldNameId = match.value
      row.fieldLevel = match.level
      row.state = match.state
      remarkRow[`${dept.belongDeptId}_currentMonth`] = dept.remark
    })
    dataSource.push(row)
  }
  dataSource.push(remarkRow)
  return { columns, dataSource, remarkRow }
}

const apiMap = {
  budget: {
    list: api.getExamineExecuteList,
    add: api.getExamineExecuteAdd,
    modify: api.getExamineExecuteModify,
    remove: api.getExamineExecuteRemove,
    fieldName: '预算执行情况表',
  },
}
const Data = ({ canEdit, type = 'budget', id }, ref) => {
  const [baseEdit, setBaseEdit] = useState(false)

  const { list, add, modify, remove, fieldName } = apiMap[type]
  const [newColumns, setNewColumns] = useState([])
  const [deptList, setDeptList] = useState([])

  const tableStore = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async (params) => {
          const enumType =
            type === 'delivery' ? 'budgetExamineBenefitEnum' : 'budgetExamineBudgetExecuteEnum'
          const res = await list({ ...params, budgetExamineId: id })
          const { columns, dataSource } = transformDataToTable(res, enumType)
          setNewColumns(generateColumns(columns))
          setDeptList(columns)
          return dataSource
        },
      }),
    []
  )

  const saveBase = async () => {
    const { list, values } = await tableStore.submit()

    const newList = []
    const listFilter = list.filter((item) => !item.state)
    // const deptListFilter = deptList.filter((item) => item.belongDeptId !== 10000396)
    deptList.forEach(({ belongDeptId }) => {
      listFilter.forEach((item) => {
        newList.push({
          id: item[`${belongDeptId}_currentMonth_id`],
          fieldValue: item[`${belongDeptId}_currentMonth`]
            ? item[`${belongDeptId}_currentMonth`] * 10000
            : null,
        })
        newList.push({
          id: item[`${belongDeptId}_currentYear_id`],
          fieldValue: item[`${belongDeptId}_currentYear`]
            ? item[`${belongDeptId}_currentYear`] * 10000
            : null,
        })
      })
    })

    await modify(newList.filter((item) => item.fieldValue !== null))
    message.success('保存成功')
    setBaseEdit(false)
    tableStore?.search()
  }

  return (
    <Table
      bordered
      columns={newColumns}
      store={tableStore}
      editable={baseEdit}
      columnWidth={180}
      rowKey="fieldNameId"
      actions={[<div>单位：元</div>]}
      scroll={{ y: 500 }}
      extra={[
        canEdit && baseEdit && (
          <>
            <Button key="cancel" onClick={() => setBaseEdit(false)}>
              取消
            </Button>
            <Button type="primary" key="save" onClick={saveBase}>
              保存
            </Button>
          </>
        ),
        canEdit && !baseEdit && (
          <Button type="primary" key="edit" onClick={() => setBaseEdit(true)}>
            编辑
          </Button>
        ),
        <TableExport table={tableStore} otherExcelProps={{ fileName: fieldName }} />,
      ]}
    />
  )
}

export default observer(Data)
