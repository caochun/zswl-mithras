const subjectQuarterType = {
  1: '一季报',
  2: '中报',
  3: '三季度',
  4: '年报',
}
const subjectReportType = {
  LOCAL: '本部',
  MERGED: '合并',
  ALL: '全部',
}

const SHEET_TABLE_NAME = {
  CAPITAL_BALANCE: '资产负债表',
  PROFIT: '利润表',
  CASH_FLOW: '现金流量表',
  BIZ_INDEX: '业务指标表',
  GOV_CAPITAL_BALANCE: '资产负债表',
  INCOME_EXPEND: '收入支出表',
}

const unitData = [
  { label: '元', value: '1' },
  { label: '万元', value: '10000' },
  { label: '亿元', value: '100000000' },
]
const precisionData = [
  { label: '0', value: '0' },
  { label: '0.0', value: '1' },
  { label: '0.00', value: '2' },
  { label: '0.000', value: '3' },
  { label: '0.0000', value: '4' },
]

const generalColumns = [
  {
    title: '报表类型',
    dataIndex: 'reportType',
    children: [
      {
        title: '报告期',
        children: [
          {
            title: '对应时间',
            children: [
              {
                title: '科目代码',
                dataIndex: 'subjectCode',
              },
              {
                title: '科目',
                dataIndex: 'subjectName',
              },
            ],
          },
        ],
      },
    ],
  },
]
export {
  generalColumns,
  subjectQuarterType,
  subjectReportType,
  unitData,
  precisionData,
  SHEET_TABLE_NAME,
}
