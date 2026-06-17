import { FiledFormat, AmountColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import { App } from '@zswl/components'
import FormAmount from '@/components/Form/FormAmount'

const ALL_COLUMNS = () => {
  return [
    InputColumn({
      title: '合同编号',
      dataIndex: 'contractCode',
      width: 280,
    }),
    InputColumn({
      title: '借据编号',
      dataIndex: 'receiptCode',
      width: 200,
    }),
    InputColumn({
      title: '项目名称',
      dataIndex: 'projName',
      width: 280,
    }),
    MatchOptionColumn({
      title: '项目来源',
      dataIndex: 'projSource',
      matchOption: 'kpiProjectSourceDistributionEnum',
    }),
    MatchOptionColumn({
      title: '项目类别',
      dataIndex: 'projClassify',
      matchOption: 'kpiProjectClassifyEnum',
    }),
    InputColumn({
      title: '项目类型',
      dataIndex: 'bizType',
      render: (_, { leaseType, factoringType, zrType }) => {
        // projEstablishBizType
        // leaseType：contractBusinessModelEnum
        // leaseType：contractBizTypeEnum
        // factoringType：factoringType
        // zrType：zrType
        return (
          App.matchOption('contractBusinessModelEnum', leaseType)?.label ||
          App.matchOption('factoringType', factoringType)?.label ||
          App.matchOption('zrType', zrType)?.label
        )
      },
    }),
    InputColumn({
      title: '首次投放日',
      dataIndex: 'contractStartDate',
    }),
    InputColumn({
      title: '考核部门',
      dataIndex: 'belongDeptName',
    }),
    // 列表
    AmountColumn({
      title: '项目利润-当期值(元)',
      dataIndex: 'profitCurrent',
    }),
    AmountColumn({
      title: '项目利润-累计值(元)',
      dataIndex: 'profitTotal',
    }),
    AmountColumn({
      title: '合同利润奖金-当期值(元)',
      dataIndex: 'bonusCurrent',
    }),
    AmountColumn({
      title: '合同利润奖金-调整值(元)',
      dataIndex: 'profitAdjust',
    }),
    AmountColumn({
      title: '合同利润奖金-累计值(元)',
      dataIndex: 'bonusTotal',
    }),
    AmountColumn({
      title: '合同投放奖金-当期值(元)',
      dataIndex: 'paymentCurrent',
    }),
    AmountColumn({
      title: '合同投放奖金-累计值(元)',
      dataIndex: 'paymentTotal',
    }),
    // 详情
    InputColumn({
      title: '月份',
      dataIndex: 'calculateDate',
    }),
    AmountColumn({
      title: '项目利润-当期值(元)',
      dataIndex: 'detailList-profitCurrent',
      render: (_, { profitCurrent }) => <FormAmount.Format value={profitCurrent} />,
    }),
    AmountColumn({
      title: '项目利润-累积值(元)',
      dataIndex: 'detailList-profitTotal',
      render: (_, { profitTotal }) => <FormAmount.Format value={profitTotal} />,
    }),

    AmountColumn({
      title: '项目本年累计投放金额(元)',
      dataIndex: 'projPaymentYearAmount',
    }),
    AmountColumn({
      title: '合同本月投放额(元)',
      dataIndex: 'contractPaymentMonthAmount',
    }),
    InputColumn({
      title: '基础提奖比例',
      dataIndex: 'projectRadioConfig',
    }),
    InputColumn({
      title: '项目类型系数',
      dataIndex: 'typeRadioConfig',
    }),
    InputColumn({
      title: '项目规模系数',
      dataIndex: 'scaleRadioConfig',
    }),
    InputColumn({
      title: '提奖比例',
      dataIndex: 'awardRatio',
    }),

    // children
    InputColumn({
      title: '人员/部门',
      dataIndex: 'weightInfo',
      width: 250,
      render: (val, { divideType, divideTypeName, divideTargetName }) => {
        return (
          <div>
            <span>【{divideTypeName}】</span>
            <span>{divideTargetName}</span>
            {/* <Tag color={TagColor[divideType]}>{divideTypeName}</Tag>】 */}
          </div>
        )
      },
    }),
    AmountColumn({
      title: '占比',
      dataIndex: 'weightValue',
      suffix: '%',
    }),
    AmountColumn({
      title: '利润提奖-当期值(元)',
      dataIndex: 'profitCurrent',
    }),
    AmountColumn({
      title: '利润提奖-调整值(元)',
      dataIndex: 'profitAdjust',
    }),
    AmountColumn({
      title: '利润提奖-累计值(元)',
      dataIndex: 'profitTotal',
    }),

    AmountColumn({
      title: '投放提奖-当期值(元)',
      dataIndex: 'paymentAwardCurrent',
    }),
    AmountColumn({
      title: '投放提奖-累计值(元)',
      dataIndex: 'paymentAwardTotal',
    }),

    AmountColumn({
      title: '利润奖金-当期值(元)',
      dataIndex: 'bonusCurrent',
    }),
    AmountColumn({
      title: '利润奖金-调整值(元)',
      dataIndex: 'bonusAdjust',
    }),
    AmountColumn({
      title: '利润奖金-累计值(元)',
      dataIndex: 'bonusTotal',
    }),
    AmountColumn({
      title: '投放奖金-当期值(元)',
      dataIndex: 'paymentCurrent',
    }),
    AmountColumn({
      title: '投放奖金-累计值(元)',
      dataIndex: 'paymentTotal',
    }),
  ]
}

export default ALL_COLUMNS
