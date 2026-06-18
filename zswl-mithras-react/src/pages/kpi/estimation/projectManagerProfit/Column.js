import { AmountColumn, InputColumn } from '@/components/Format'
import { FormAmount } from '@/components/Form'

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
      title: '本年存量利润-总计主办利润(元)',
      dataIndex: 'bonusCurrent',
    }),
    AmountColumn({
      title: '本年存量利润-总计主办投放(元)',
      dataIndex: 'paymentCurrent',
    }),
    AmountColumn({
      title: '本年存量利润-总计协办利润(元)',
      dataIndex: 'bonusCurrentDeputy',
    }),
    AmountColumn({
      title: '本年存量利润-总计协办投放(元)',
      dataIndex: 'paymentCurrentDeputy',
    }),

    AmountColumn({
      title: '本年新增利润-总计主办利润(元)',
      dataIndex: 'bonusCurrentAdd',
    }),
    AmountColumn({
      title: '本年新增利润-总计主办投放(元)',
      dataIndex: 'paymentCurrentAdd',
    }),
    AmountColumn({
      title: '本年新增利润-总计协办利润(元)',
      dataIndex: 'bonusCurrentDeputyAdd',
    }),
    AmountColumn({
      title: '本年新增利润-总计协办投放(元)',
      dataIndex: 'paymentCurrentDeputyAdd',
    }),

    // 详情
    InputColumn({
      title: '考核部门',
      dataIndex: 'deptName',
    }),
    // 存量
    AmountColumn({
      title: '本年存量-主办利润-产业(元)',
      dataIndex: 'industryStock-bonusCurrent',
      render: (_, { industryStock }) => <FormAmount.Format value={industryStock?.bonusCurrent} />,
    }),
    AmountColumn({
      title: '本年存量-主办利润-公用事业(元)',
      dataIndex: 'publicStock-bonusCurrent',
      render: (_, { publicStock }) => <FormAmount.Format value={publicStock?.bonusCurrent} />,
    }),
    AmountColumn({
      title: '本年存量-主办投放额-产业(元)',
      dataIndex: 'industryStock-paymentCurrent',
      render: (_, { industryStock }) => <FormAmount.Format value={industryStock?.paymentCurrent} />,
    }),
    AmountColumn({
      title: '本年存量-主办投放额-公用事业(元)',
      dataIndex: 'publicStock-paymentCurrent',
      render: (_, { publicStock }) => <FormAmount.Format value={publicStock?.paymentCurrent} />,
    }),
    AmountColumn({
      title: '本年存量-协办利润-产业(元)',
      dataIndex: 'industryStock-bonusCurrentDeputy',
      render: (_, { industryStock }) => (
        <FormAmount.Format value={industryStock?.bonusCurrentDeputy} />
      ),
    }),
    AmountColumn({
      title: '本年存量-协办利润-公用事业(元)',
      dataIndex: 'publicStock-bonusCurrentDeputy',
      render: (_, { publicStock }) => <FormAmount.Format value={publicStock?.bonusCurrentDeputy} />,
    }),
    AmountColumn({
      title: '本年存量-协办投放额-产业(元)',
      dataIndex: 'industryStock-paymentCurrentDeputy',
      render: (_, { industryStock }) => (
        <FormAmount.Format value={industryStock?.paymentCurrentDeputy} />
      ),
    }),
    AmountColumn({
      title: '本年存量-协办投放额-公用事业(元)',
      dataIndex: 'publicStock-paymentCurrentDeputy',
      render: (_, { publicStock }) => (
        <FormAmount.Format value={publicStock?.paymentCurrentDeputy} />
      ),
    }),
    AmountColumn({
      title: '本年存量-推荐人利润-产业(元)',
      dataIndex: 'industryStock-bonusCurrentReference',
      render: (_, { industryStock }) => (
        <FormAmount.Format value={industryStock?.bonusCurrentReference} />
      ),
    }),
    AmountColumn({
      title: '本年存量-推荐人利润-公用事业(元)',
      dataIndex: 'publicStock-industryStock',
      render: (_, { publicStock }) => <FormAmount.Format value={publicStock?.industryStock} />,
    }),
    AmountColumn({
      title: '本年存量-推荐人投放-产业(元)',
      dataIndex: 'industryStock-paymentCurrentReference',
      render: (_, { industryStock }) => (
        <FormAmount.Format value={industryStock?.paymentCurrentReference} />
      ),
    }),
    AmountColumn({
      title: '本年存量-推荐人投放-公用事业(元)',
      dataIndex: 'publicStock-paymentCurrentReference',
      render: (_, { publicStock }) => (
        <FormAmount.Format value={publicStock?.paymentCurrentReference} />
      ),
    }),
    AmountColumn({
      title: '本年存量-利润合计(元)',
      dataIndex: 'bonusAmountStock',
    }),
    AmountColumn({
      title: '本年存量-投放合计(元)',
      dataIndex: 'paymentAmountStock',
    }),

    // 新增
    AmountColumn({
      title: '本年新增-主办利润-产业(元)',
      dataIndex: 'industryAdd-bonusCurrent',
      render: (_, { industryAdd }) => <FormAmount.Format value={industryAdd?.bonusCurrent} />,
    }),
    AmountColumn({
      title: '本年新增-主办利润-公用事业(元)',
      dataIndex: 'publicAdd-bonusCurrent',
      render: (_, { publicAdd }) => <FormAmount.Format value={publicAdd?.bonusCurrent} />,
    }),
    AmountColumn({
      title: '本年新增-主办投放额-产业(元)',
      dataIndex: 'industryAdd-paymentCurrent',
      render: (_, { industryAdd }) => <FormAmount.Format value={industryAdd?.paymentCurrent} />,
    }),
    AmountColumn({
      title: '本年新增-主办投放额-公用事业(元)',
      dataIndex: 'publicAdd-paymentCurrent',
      render: (_, { publicAdd }) => <FormAmount.Format value={publicAdd?.paymentCurrent} />,
    }),
    AmountColumn({
      title: '本年新增-协办利润-产业(元)',
      dataIndex: 'industryAdd-bonusCurrentDeputy',
      render: (_, { industryAdd }) => <FormAmount.Format value={industryAdd?.bonusCurrentDeputy} />,
    }),
    AmountColumn({
      title: '本年新增-协办利润-公用事业(元)',
      dataIndex: 'publicAdd-bonusCurrentDeputy',
      render: (_, { publicAdd }) => <FormAmount.Format value={publicAdd?.bonusCurrentDeputy} />,
    }),
    AmountColumn({
      title: '本年新增-协办投放额-产业(元)',
      dataIndex: 'industryAdd-paymentCurrentDeputy',
      render: (_, { industryAdd }) => (
        <FormAmount.Format value={industryAdd?.paymentCurrentDeputy} />
      ),
    }),
    AmountColumn({
      title: '本年新增-协办投放额-公用事业(元)',
      dataIndex: 'publicAdd-paymentCurrentDeputy',
      render: (_, { publicAdd }) => <FormAmount.Format value={publicAdd?.paymentCurrentDeputy} />,
    }),
    AmountColumn({
      title: '本年新增-推荐人利润-产业(元)',
      dataIndex: 'industryAdd-bonusCurrentReference',
      render: (_, { industryAdd }) => (
        <FormAmount.Format value={industryAdd?.bonusCurrentReference} />
      ),
    }),
    AmountColumn({
      title: '本年新增-推荐人利润-公用事业(元)',
      dataIndex: 'publicAdd-industryStock',
      render: (_, { publicAdd }) => <FormAmount.Format value={publicAdd?.industryStock} />,
    }),
    AmountColumn({
      title: '本年新增-推荐人投放-产业(元)',
      dataIndex: 'industryAdd-paymentCurrentReference',
      render: (_, { industryAdd }) => (
        <FormAmount.Format value={industryAdd?.paymentCurrentReference} />
      ),
    }),
    AmountColumn({
      title: '本年新增-推荐人投放-公用事业(元)',
      dataIndex: 'publicAdd-paymentCurrentReference',
      render: (_, { publicAdd }) => (
        <FormAmount.Format value={publicAdd?.paymentCurrentReference} />
      ),
    }),
    AmountColumn({
      title: '本年新增-利润合计(元)',
      dataIndex: 'bonusAmountAdd',
    }),
    AmountColumn({
      title: '本年新增-投放合计(元)',
      dataIndex: 'paymentAmountAdd',
    }),

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
  ]
}

export default ALL_COLUMNS
