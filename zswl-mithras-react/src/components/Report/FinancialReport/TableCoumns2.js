import { AmountEditable } from '@/components/Format'
import {
  AmountColumn,
  DateColumn,
  InputColumn,
  MatchOptionColumn,
} from '@/components/Format'

const initFormat = 1
const parentFormat = 1 / 100
const ItemRender = (text, { itemLevel }) => {
  return (
    <div style={{ fontWeight: itemLevel === 0 ? 'bold' : 'normal', marginLeft: itemLevel * 20 }}>
      {text}
    </div>
  )
}

const wrapItemProps = {
  inputConfig: {
    min: -Infinity,
  },
}
// 融资租赁公司最大十家客户（含集团）集中度统计表
export const topTenColumns = [
  // InputColumn({ title: '序号', dataIndex: 'onum', width: 80 }),
  InputColumn({ title: '客户姓名', dataIndex: 'custName', editable: true }),
  {
    title: '表内业务',
    children: [
      AmountColumn({
        title: '前十大客户租赁余额',
        dataIndex: 'onblToptCustLeasBal',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
      AmountColumn({
        title: '占净资产比例',
        dataIndex: 'onblOnar',
        suffix: '%',
        initFormat: parentFormat,
        editable: true,
        wrapItemProps,
      }),
    ],
  },
  {
    title: '表外业务',
    children: [
      AmountColumn({
        title: '担保',
        dataIndex: 'ofblGuar',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
      AmountColumn({
        title: '其他',
        dataIndex: 'ofblOth',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
    ],
  },
  {
    title: '扣减项',
    children: [
      AmountColumn({
        title: '合格质物',
        dataIndex: 'deitQulfSbim',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
      AmountColumn({
        title: '合格保证',
        dataIndex: 'deitQulfAsue',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
      AmountColumn({
        title: '其他',
        dataIndex: 'deitOth',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
    ],
  },
  AmountColumn({
    title: '信用风险敞口',
    dataIndex: 'credExps',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
]

// 融资租赁公司资产负债表
export const balanceColumns = [
  InputColumn({
    title: '项目',
    dataIndex: 'item',
    editable: false,
    render: (text, { itemLevel = 1 }) => {
      return (
        <div
          style={{ fontWeight: itemLevel === 0 ? 'bold' : 'normal', marginLeft: itemLevel * 20 }}
        >
          {text}
        </div>
      )
    },
  }),
  { title: '行数', dataIndex: 'order', width: 80, editable: false },
  AmountColumn({
    title: '期末数',
    dataIndex: 'endValue',
    initFormat,
    editable: true,
    disabledFunc: (record) => record.itemLevel === 0,
    wrapItemProps,
  }),
  AmountColumn({
    title: '年初数',
    dataIndex: 'startValue',
    initFormat,
    editable: true,
    disabledFunc: (record) => record.itemLevel === 0,
    wrapItemProps,
  }),
  InputColumn({
    title: '项目',
    dataIndex: 'item2',
    editable: false,
    render: (text, { itemLevel2 = 1 }) => {
      return (
        <div
          style={{ fontWeight: itemLevel2 === 0 ? 'bold' : 'normal', marginLeft: itemLevel2 * 20 }}
        >
          {text}
        </div>
      )
    },
  }),
  { title: '行数', dataIndex: 'order2', width: 80, editable: false },
  AmountColumn({
    title: '期末数',
    dataIndex: 'endValue2',
    initFormat,
    editable: true,
    disabledFunc: (record) => record.itemLevel2 === 0,
    wrapItemProps,
  }),
  AmountColumn({
    title: '年初数',
    dataIndex: 'startValue2',
    initFormat,
    editable: true,
    disabledFunc: (record) => record.itemLevel2 === 0,
    wrapItemProps,
  }),
]
