import { observer, getQuery } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { Table, TableStore, Button, App } from '@zswl/components'
import TableExport from '@/components/Actions/TableExport'
import api from '../../api'
import { AmountColumn, AmountEditable } from '@/components/Format'
import { message, Tooltip } from 'antd'

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
          editable: false,
        }),
        AmountColumn({
          dataIndex: `${belongDeptId}_currentYear`,
          title: '本年累计',
          editable: !isCompany,
          editable:
            !isCompany &&
            ((val, index) => {
              if (!val.state) return false
              return AmountEditable(val, `${belongDeptId}_currentYear`, {
                disabled: false,
                index,
                initFormat: 10000,
                inputConfig: {
                  min: -Infinity,
                },
              })
            }),
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
  const rowCount = deptList[0]?.currentMonthList?.length
  const dataSource = []
  for (let i = 0; i < rowCount; i++) {
    const row = {}
    deptList.forEach((dept) => {
      row[`${dept.belongDeptId}_currentMonth`] = dept.currentMonthList?.[i]?.fieldValue
      row[`${dept.belongDeptId}_currentYear`] = dept.currentYearList?.[i]?.fieldValue
      row[`${dept.belongDeptId}_currentMonth_id`] = dept.currentMonthList?.[i]?.id
      row[`${dept.belongDeptId}_currentYear_id`] = dept.currentYearList?.[i]?.id
      const match = App.matchOption(enumType, dept.currentMonthList?.[i]?.fieldName) || {}
      row.fieldName = match.label
      row.fieldNameId = match.value
      row.fieldLevel = match.level
      row.state = match.state
    })
    dataSource.push(row)
  }

  return { columns, dataSource }
}

const apiMap = {
  delivery: {
    list: api.getExamineBenefitList,
    add: api.getExamineBenefitAdd,
    modify: api.getExamineBenefitModify,
    remove: api.getExamineBenefitRemove,
    fieldName: '效益考核表',
  },
}
const Data = ({ canEdit, type = 'delivery', id }, ref) => {
  const [baseEdit, setBaseEdit] = useState(false)
  const { list, add, modify, remove, fieldName } = apiMap[type]
  const [newColumns, setNewColumns] = useState([])
  const [deptList, setDeptList] = useState([])

  const tableStore = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async (params) => {
          const res = await list({ ...params, budgetExamineId: id })
          const { columns, dataSource } = transformDataToTable(res, 'budgetExamineBenefitEnum')
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
    const listFilter = list.filter((item) => item.state)
    const deptListFilter = deptList.filter((item) => item.belongDeptId !== 10000396)
    deptListFilter.forEach(({ belongDeptId }) => {
      listFilter.forEach((item) => {
        // newList.push({
        //   id: item[`${belongDeptId}_currentMonth_id`],
        //   fieldValue: item[`${belongDeptId}_currentMonth`]
        //     ? item[`${belongDeptId}_currentMonth`] * 10000
        //     : null,
        // })
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
      scroll={{ y: 500 }}
      actions={[<div>单位：元</div>]}
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
