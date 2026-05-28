import { founderSelect, clientSelect, orgSelect } from '@/pages/dashboard/workbench/Column'
import { InputColumn, AmountColumn } from '@/components/Format'

export const All_COLUMNS = [
  clientSelect(),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '当前阶段停留天数(工作日)',
    dataIndex: 'lengthOfStay',
  }),
  InputColumn({
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassifyDisplay',
  }),
  AmountColumn({
    title: '授信金额(元)',
    dataIndex: 'creditAmount',
    initFormat: 1,
    sorter: {
      compare: (a, b) => a.creditAmount?.value - b.creditAmount?.value,
    },
  }),
  InputColumn({
    title: '国际行业分类',
    dataIndex: 'industryTypeDisplay',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
  }),
  InputColumn({
    title: '承租人',
    dataIndex: 'clientName',
  }),
  InputColumn({
    title: '担保人',
    dataIndex: 'guarantorNames',
  }),
  orgSelect(),
  founderSelect(),
  InputColumn({
    title: '项目协办',
    dataIndex: 'projCosponsorUserNames',
  }),
  InputColumn({
    title: '申请时间',
    dataIndex: 'applyTime',
  }),
]
