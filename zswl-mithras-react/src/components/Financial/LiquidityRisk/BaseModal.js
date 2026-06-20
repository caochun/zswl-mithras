import { observer } from '@zswl/admin'
import { App, Form, Modal } from '@zswl/components'
import { FormAmount } from '@/components/Form'
import { ReadOnly } from '@/components/Form'
import { FormTable } from '@/components/Form'
import ALL_COLUMNS from './Column'
import { getTableColumns } from '@/utils'
import styles from './index.less'
import moment from 'moment'

const nameColumns = [
  { title: '融资金额', dataIndex: 'amount' },
  { title: '融资日期', dataIndex: 'date', width: 120 },
  '备注',
]
const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
}

const INIT_FORMAT = 10000 * 10000
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const columns2 = getTableColumns(ALL_COLUMNS, [
  { title: '融资日期', rename: '投放日期' },
  { title: '融资金额', rename: '项目金额' },
])

function BaseModal({ store, time = {} }) {
  const { days } = time
  const [form] = Form.useForm()
  const getCount = (v) => {
    const [start, end] = store.getInitialValues().time.split('~')
    if (!v.date) return 0
    return moment(v.date).isBetween(start, moment(end).add(1, 'd'), null, '[]') ? +v.amount : 0
  }
  return (
    <Modal title={'数据设置'} store={store} okText={'确定'} draggable width={800} destroyOnClose>
      <Form preserve={true} form={form}>
        <div className={styles.zSubTitle}>
          基础数据 <span className={styles.unit}>（单位：万元）</span>
        </div>
        <Form.Item label={'期初现金流余额'} name={'beginCashflowAmount'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} />
        </Form.Item>
        <Form.Item label={'其他收入'} name={'otherIncome'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} />
        </Form.Item>
        <Form.Item label={'其他支出'} name={'otherExpenses'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} />
        </Form.Item>

        <div className={styles.zSubTitle}>
          现金流流入 <span className={styles.unit}>（单位：万元）</span>
        </div>
        <Form.Item label={`近${days}天租金/利息回笼`} name={'rentInterestReturn'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} disabled />
        </Form.Item>
        <Form.Item label={'保证金/手续费收入'} name={'earnestMoneyRevenue'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} disabled />
        </Form.Item>
        <Form.Item dependencies={['inDetail']}>
          {({ getFieldValue, setFieldValue }) => {
            const val = getFieldValue('inDetail')
            const amount = val?.reduce((pre, v) => pre + getCount(v), 0) ?? 0
            setFieldValue(['financingSum'], amount)
            return (
              <Form.Item label={'融资总额'} name={'financingSum'} {...layout}>
                <FormAmount initFormat={INIT_FORMAT} disabled />
              </Form.Item>
            )
          }}
        </Form.Item>

        <Form.Item label={'融资明细'} name={'inDetail'} {...layout}>
          <FormTable columns={columns} />
        </Form.Item>
        <div className={styles.zSubTitle}>
          现金流流出 <span className={styles.unit}>（单位：万元）</span>
        </div>
        <Form.Item label={`近${days}天归还融资本金`} name={'returnFinancingPrincipal'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} disabled />
        </Form.Item>
        <Form.Item label={`归还融资利息`} name={'returnFinancingInterest'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} disabled />
        </Form.Item>

        <Form.Item label={'项目保证金'} name={'projEarnestMoney'} {...layout}>
          <FormAmount initFormat={INIT_FORMAT} disabled />
        </Form.Item>

        <Form.Item dependencies={['outDetail']}>
          {({ getFieldValue, setFieldValue }) => {
            const val = getFieldValue('outDetail')
            const amount = val?.reduce((pre, v) => pre + getCount(v), 0) ?? 0
            setFieldValue(['projOutSum'], amount)
            return (
              <Form.Item label={'项目投放总额'} name={'projOutSum'} {...layout}>
                <FormAmount initFormat={INIT_FORMAT} disabled />
              </Form.Item>
            )
          }}
        </Form.Item>
        <Form.Item label={'项目投放明细'} name={'outDetail'} {...layout}>
          <FormTable columns={columns2} />
        </Form.Item>
        <div className={styles.zSubTitle}>
          现金流小计 <span className={styles.unit}>（单位：万元）</span>
        </div>
        <Form.Item label={'时间区间'} name={'time'} {...layout}>
          <ReadOnly />
        </Form.Item>

        <Form.Item
          dependencies={['rentInterestReturn', 'earnestMoneyRevenue', 'financingSum', 'inDetail']}
        >
          {({ getFieldValue, setFieldValue }) => {
            const rentInterestReturn = getFieldValue('rentInterestReturn')
            const earnestMoneyRevenue = getFieldValue('earnestMoneyRevenue')
            const financingSum = getFieldValue('financingSum')

            const sumAmountIn = +rentInterestReturn + +earnestMoneyRevenue + +financingSum
            setFieldValue('sumAmountIn', sumAmountIn)
            return (
              <Form.Item label={'总计资金流入量'} name={'sumAmountIn'} {...layout}>
                <FormAmount initFormat={INIT_FORMAT} disabled />
              </Form.Item>
            )
          }}
        </Form.Item>
        <Form.Item
          dependencies={[
            'returnFinancingPrincipal',
            'returnFinancingInterest',
            'projEarnestMoney',
            'projOutSum',
            'outDetail',
          ]}
        >
          {({ getFieldValue, setFieldValue }) => {
            const returnFinancingPrincipal = getFieldValue('returnFinancingPrincipal')
            const returnFinancingInterest = getFieldValue('returnFinancingInterest')
            const projEarnestMoney = getFieldValue('projEarnestMoney')
            const projOutSum = getFieldValue('projOutSum')
            const sumAmountOut =
              +returnFinancingPrincipal + +returnFinancingInterest + +projEarnestMoney + +projOutSum
            setFieldValue(['sumAmountOut'], sumAmountOut)
            return (
              <Form.Item label={'总计资金流出量'} name={'sumAmountOut'} {...layout} {...layout}>
                <FormAmount initFormat={INIT_FORMAT} disabled />
              </Form.Item>
            )
          }}
        </Form.Item>
        <Form.Item dependencies={['inDetail', 'outDetail']}>
          {({ getFieldValue, setFieldValue }) => {
            const sumAmountIn = getFieldValue('sumAmountIn')
            const sumAmountOut = getFieldValue('sumAmountOut')
            const totalFundingSurplus = +sumAmountIn - +sumAmountOut
            setFieldValue(['totalFundingSurplus'], totalFundingSurplus)
            return (
              <Form.Item label={'总资金盈缺'} name={'totalFundingSurplus'} {...layout} {...layout}>
                <FormAmount initFormat={INIT_FORMAT} disabled />
              </Form.Item>
            )
          }}
        </Form.Item>
        <Form.Item
          dependencies={[
            'beginCashflowAmount',
            'inDetail',
            'outDetail',
            'otherIncome',
            'otherExpenses',
          ]}
        >
          {({ getFieldValue, setFieldValue }) => {
            const totalFundingSurplus = getFieldValue('totalFundingSurplus')
            const beginCashflowAmount = getFieldValue('beginCashflowAmount')
            const otherIncome = getFieldValue('otherIncome')
            const otherExpenses = getFieldValue('otherExpenses')
            const periodCashBalance =
              +totalFundingSurplus + +beginCashflowAmount + +otherIncome - +otherExpenses
            setFieldValue(['periodCashBalance'], periodCashBalance)
            return (
              <Form.Item label={'期间现金余额'} name={'periodCashBalance'} {...layout}>
                <FormAmount initFormat={INIT_FORMAT} disabled />
              </Form.Item>
            )
          }}
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(BaseModal)
