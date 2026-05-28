import { InputColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  InputColumn({
    title: '业务类型',
    dataIndex: 'businessCategory',
  }),
  InputColumn({
    title: '租赁类型',
    dataIndex: 'leaseType',
  }),
  InputColumn({
    title: '全流程平均时效(工作日)',
    dataIndex: 'averageDuration',
    align: 'right',
  }),
  InputColumn({
    title: '运营经办平均时效(工作日)',
    dataIndex: 'averageDurationYYJB',
    align: 'right',
  }),
  InputColumn({
    title: '运营部平均时效(工作日)',
    dataIndex: 'averageDurationYYB',
    align: 'right',
  }),
  InputColumn({
    title: '法务部平均时效(工作日)',
    dataIndex: 'averageDurationFWB',
    align: 'right',
  }),
  InputColumn({
    title: '财务部平均时效(工作日)',
    dataIndex: 'averageDurationCWB',
    align: 'right',
  }),
]
