import { AmountColumn } from '@/components/Format'
import CommonTips from '@/components/CommonTips'

const ALL_COLUMNS = ({ isZhiZu, retentionMoney }, flag) => {
  return [
    AmountColumn({
      title: '首期租金(元)',
      dataIndex: 'downPayment',
      editable: true,
    }),
    AmountColumn({
      title: '客户保证金(元)',
      dataIndex: 'earnestMoney',
      editable: true,
    }),
    isZhiZu &&
      AmountColumn({ title: '厂商质保金(元)', dataIndex: 'retentionMoney', editable: true }),
    isZhiZu &&
    {
      title: '质保金收款方式',
      dataIndex: 'warrantyPayWay',
      editable: false,
      matchOption:[{label:'不内扣',value:1},{label:'内扣',value:0}]
    },
    isZhiZu &&
    {
      title: '厂商质保金退还日期',
      dataIndex: 'warrantyReturnDate',
      editable: false,
      requiredMark: retentionMoney && retentionMoney/10000 > 0,
    },
    AmountColumn({
      title: '服务费/咨询费(元)',
      dataIndex: 'consultingFee',
      formTooltip: CommonTips.fieldMapTip['consultingFee'],
      editable: true,
    }),
    AmountColumn({
      title: '手续费(元)',
      dataIndex: 'commission',
      formTooltip: CommonTips.fieldMapTip['commission'],
      editable: true,
    }),
    AmountColumn({
      title: '首期利息(元)',
      dataIndex: 'firstInstallmentInterest',
      formTooltip: CommonTips.fieldMapTip['firstInstallmentInterest'],
      editable: true,
    }),
  ]
}

export default ALL_COLUMNS
