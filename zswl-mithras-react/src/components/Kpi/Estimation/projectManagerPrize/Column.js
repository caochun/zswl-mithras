import { AmountColumn, InputColumn, FiledFormat } from '@/components/Format'

const ALL_COLUMNS = () => {
  return [
    // 列表
    InputColumn({
      title: '最新核算月份',
      dataIndex: 'calculateDate',
    }),
    AmountColumn({
      title: '项目利润-当期值(元)',
      dataIndex: 'profitCurrent',
    }),
    AmountColumn({
      title: '项目利润-累计值(元)',
      dataIndex: 'profitTotal',
    }),
    AmountColumn({
      title: '总计主办利润奖金(元)',
      dataIndex: 'bonusCurrent',
    }),
    AmountColumn({
      title: '总计主办投放奖金(元)',
      dataIndex: 'paymentCurrent',
    }),
    AmountColumn({
      title: '总计协办利润奖金(元)',
      dataIndex: 'bonusCurrentDeputy',
    }),
    AmountColumn({
      title: '总计协办投放奖金(元)',
      dataIndex: 'paymentCurrentDeputy',
    }),
    // 详情
    InputColumn({
      title: '考核部门',
      dataIndex: 'deptName',
    }),
    InputColumn({
      title: '人员/部门',
      dataIndex: 'divideTargetName',
      width: 250,
    }),
    AmountColumn({
      title: '主办利润奖金(元)',
      dataIndex: 'bonusCurrent',
    }),
    AmountColumn({
      title: '主办投放奖金(元)',
      dataIndex: 'paymentCurrent',
    }),
    AmountColumn({
      title: '协办利润奖金(元)',
      dataIndex: 'bonusCurrentDeputy',
    }),
    AmountColumn({
      title: '协办投放奖金(元)',
      dataIndex: 'paymentCurrentDeputy',
    }),
    AmountColumn({
      title: '推荐人利润奖金(元)',
      dataIndex: 'bonusCurrentReference',
    }),
    AmountColumn({
      title: '推荐人投放奖金(元)',
      dataIndex: 'paymentCurrentReference',
    }),
    AmountColumn({
      title: '合计(元)',
      dataIndex: 'amount',
    }),
  ]
}

export default ALL_COLUMNS
