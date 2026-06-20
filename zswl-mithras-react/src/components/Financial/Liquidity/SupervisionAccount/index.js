import { observer } from '@zswl/admin'
import styles from './style.less'
import { Form, Block, Page } from '@zswl/components'
import { DatePicker } from 'antd'
import LineCharts from './Component/LineCharts'
import ZInput from '@/components/ZInput'
import { ApiSelect } from '@/components/Select'
import BankInfoTable from './Component/BankInfoTable'
import FundTransferDaily from './Component/FundTransferDaily'
import DailyInfoModal from './Component/DailyInfoModal'
import BankInfoModal from './Component/BankInfoModal'
import store from './store'
import fundTransferApi from '@/api/financial/fundTransfer'

const SupervisionAccount = ({ detail }) => {
  console.log('detail: ', detail)
  const [form] = Form.useForm()

  return (
    <Page store={store.blockStore}>
      <div className={styles.container}>
        <div className={styles.header}>
          <h3 className={'z-sub-title'}>监管户待转资金汇总</h3>
          <div className={styles['update-time']}>
            账户余额更新时间：{detail.accountBalanceUpdateTime}
          </div>
        </div>
        <div className={styles['date-range']}>
          <Form
            onValuesChange={store.onValuesChange}
            layout="inline"
            form={form}
            initialValues={{
              timeRange: store.commonQueryParams.timeRange,
            }}
          >
            <Form.Item label="时间范围" name="timeRange">
              <DatePicker.RangePicker allowClear={false} />
            </Form.Item>
            <Form.Item label="银行名称" name="accountBank">
              <ApiSelect
                debounceSearch
                style={{ width: 250 }}
                api={fundTransferApi.postBankAccountList}
                searchField="bankName"
                fieldNames={{
                  label: 'accountBank',
                  value: 'accountBank',
                }}
              ></ApiSelect>
            </Form.Item>
          </Form>
        </div>
        <LineCharts
          dataSource={store.sumChartsData}
          handleClick={store.dailyChartsModal.open}
          indicators={[{ key: 'pendingBalanceAmount', name: '监管户待转资金汇总' }]}
        ></LineCharts>
        {/* 开户行 */}
        <BankInfoTable store={store}></BankInfoTable>
        {/* 监管户待转资金当日 */}
        <FundTransferDaily></FundTransferDaily>
        {/* 汇总每日情况 */}
        <DailyInfoModal store={store}></DailyInfoModal>
        {/* 账号每日情况 */}
        <BankInfoModal store={store}></BankInfoModal>
      </div>
    </Page>
  )
}

export default observer(SupervisionAccount)
