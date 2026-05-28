import { AmountColumn } from '@/components/Format'
import { uniqueId } from 'lodash'

// 动态生成表头
const generateColumns = (departments) => {
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
    title: dept.deptName,
    dataIndex: `dept_${dept.deptId}`,
    children: [
      AmountColumn({ title: '公用事业类（元）', dataIndex: `${dept.deptId}_publicUtilities` }),
      AmountColumn({ title: '民生消费类（元）', dataIndex: `${dept.deptId}_civilConsumption` }),
      AmountColumn({ title: '国有产业类（元）', dataIndex: `${dept.deptId}_stateOwnedIndustry` }),
      AmountColumn({
        title: '其他产业类（元）',
        dataIndex: `${dept.deptId}_otherIndustry`,
      }),
    ],
  }))

  return [...baseColumns, ...departmentColumns]
}

const formatSummary = (data) => {
  const result = []
  data.forEach((dept) => {
    dept.dataList.forEach((item) => {
      result.push({ belongDeptId: dept.belongDeptId, belongDeptName: dept.belongDeptName, ...item })
    })
  })
  return result
}
const formatPutTable = (data) => {
  if (!data?.length) return { columns: [], dataSource: [] }
  const firstItem = data[0]
  if (!firstItem?.deptDataList?.length) return { columns: [], dataSource: [] }

  // 获取第一条数据的部门数据作为列定义
  const columns = firstItem.deptDataList

  // 转换数据格式
  const tableData = data.map((item) => {
    const row = {
      date: item.date,
      id: uniqueId(),
    }
    item.deptDataList.forEach((dept) => {
      row[`${dept.deptId}_publicUtilities`] = dept.publicUtilities
      row[`${dept.deptId}_civilConsumption`] = dept.civilConsumption
      row[`${dept.deptId}_stateOwnedIndustry`] = dept.stateOwnedIndustry
      row[`${dept.deptId}_otherIndustry`] = dept.otherIndustry
    })
    return row
  })

  return {
    columns,
    dataSource: tableData,
  }
}

const formatOtherSummary = (data) => {
  if (!data?.length) return { columns: { columns: [], rowSpan: 9 }, dataSource: [] }
  const firstItem = data[0]
  if (!firstItem?.deptDataList?.length) {
    return { columns: { columns: [], rowSpan: 9 }, dataSource: [] }
  }
  // 统计每年的指标个数
  const yearMetricCount = data.reduce((acc, item) => {
    if (!acc[item.year]) {
      acc[item.year] = 0
    }
    acc[item.year]++
    return acc
  }, {})
  // 获取任意一个年份的指标数量
  const count = Object.values(yearMetricCount)[0]

  const columns = { columns: firstItem.deptDataList, rowSpan: count }
  const dataSource = data.map((item) => {
    // 转换数据格式
    const row = {
      metricName: item.metricName,
      year: item.year,
      id: uniqueId(),
    }
    item?.deptDataList?.forEach((dept) => {
      row[`${dept.belongDeptId}_publicUtilities`] = dept.publicUtilities
      row[`${dept.belongDeptId}_civilConsumption`] = dept.civilConsumption
      row[`${dept.belongDeptId}_stateOwnedIndustry`] = dept.stateOwnedIndustry
      row[`${dept.belongDeptId}_otherIndustry`] = dept.otherIndustry
      row[`${dept.belongDeptId}_sum`] = dept.sum
    })
    return row
  })
  return { columns, dataSource }
}
// 动态生成表头
const generateOtherColumns = (departments, rowSpan) => {
  const baseColumns = [
    {
      title: '年份',
      dataIndex: 'year',
      onCell: (record, index) => {
        if (index % rowSpan === 0) return { rowSpan }
        return {
          rowSpan: 0,
        }
      },
    },
    { title: '指标', dataIndex: 'metricName', width: 200 },
  ]

  // 为每个部门生成子列
  const departmentColumns = departments.map((dept) => ({
    title: dept.belongDeptName,
    dataIndex: `dept_${dept.belongDeptId}`,
    children: [
      AmountColumn({
        title: '公用事业类（元）',
        dataIndex: `${dept.belongDeptId}_publicUtilities`,
      }),
      AmountColumn({
        title: '民生消费类（元）',
        dataIndex: `${dept.belongDeptId}_civilConsumption`,
      }),
      AmountColumn({
        title: '国有产业类（元）',
        dataIndex: `${dept.belongDeptId}_stateOwnedIndustry`,
      }),
      AmountColumn({
        title: '其他产业类（元）',
        dataIndex: `${dept.belongDeptId}_otherIndustry`,
      }),
      AmountColumn({ title: '部门小计（元）', dataIndex: `${dept.belongDeptId}_sum` }),
    ],
  }))

  return [...baseColumns, ...departmentColumns]
}

export { generateColumns, formatPutTable, formatSummary, formatOtherSummary, generateOtherColumns }
