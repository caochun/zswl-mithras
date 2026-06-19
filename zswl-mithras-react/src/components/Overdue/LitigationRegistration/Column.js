import { DateColumn, MatchOptionColumn, TextAreaColumn } from '@/components/Format'

const ALL_COLUMNS = [
  { title: '客户名称', dataIndex: 'client', search: true },
  TextAreaColumn({ title: '合同编号', dataIndex: 'contractCodes', search: true, width: 280 }),
  MatchOptionColumn({
    title: '诉讼状态',
    dataIndex: 'status',
    matchOption: 'litigationStatus',
    search: true,
  }),
  { title: '诉讼登记编号', dataIndex: 'code', search: true },
  MatchOptionColumn({ title: '诉讼阶段', dataIndex: 'stage', matchOption: 'litigationStageEnum' }),
  { title: '记录人', dataIndex: 'processPerson' },
  { title: '创建人', dataIndex: 'createByName' },
  DateColumn({
    title: '记录时间',
    dataIndex: 'createTime',
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
    width: 200,
  }),
]

export default ALL_COLUMNS
