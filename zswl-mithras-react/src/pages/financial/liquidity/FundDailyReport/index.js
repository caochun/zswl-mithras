import fundDayReportApi from '@/api/financial/liquidity/fundDayReportApi'
import { AmountColumn } from '@/components/Format'
import { getFinancialUrl } from '@/components/Financial/FinancingUrlEntries'
import { amountFormat, formatPercent, saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import { Page, Table } from '@zswl/components'
import { Col, List, Row } from 'antd'
import { forwardRef, useImperativeHandle } from 'react'
import styles from './style.less'

const SupervisedAccounts = ({ data, title, itemStyle }) => {
  return (
    <div className={styles['supervised-accounts']}>
      <div className="z-sub-title">{title}</div>
      <List
        dataSource={data}
        renderItem={(item) => (
          <List.Item className={styles['account-item']} style={itemStyle}>
            <div className={styles['account-info']}>
              <div className={styles['bank-name']}>{item.bankName}</div>
              <div className={styles['bank-type']}>{'银行'}</div>
            </div>
            <div className={styles['balance-info']}>
              <div className={styles['balance-amount']}>
                {amountFormat(formatPercent(item.accountBalance, 10000 * 10000))}
                <span className={styles.unit}>（万元）</span>
              </div>
              <div className={styles['balance-label']}>{'日初余额'}</div>
            </div>
          </List.Item>
        )}
      />
    </div>
  )
}
const DailyReport = forwardRef((props, ref) => {
  const pageStore = Page.useStore({
    request: async (params) => {
      const [detail, rent] = await Promise.all([
        fundDayReportApi.postIndicatorList(params),
        fundDayReportApi.postAccountBalance(params),
      ])
      return { detail, rent }
    },
  })
  const { detail, rent } = pageStore.getData()
  // 资金日报数据
  const cardList = [
    {
      label: '日初余额',
      value: detail?.dawnOfDayBalance,
      type: 'blue',
      icon: '/public/assets/liquidity/initial-balance.png',
    },
    {
      label: '日初非监管户余额',
      value: detail?.dawnOfDayNonSupervisionBalance,
      type: 'cyan',
      icon: '/public/assets/liquidity/initial-available.png',
    },
    {
      label: '日初非受限余额',
      value: detail?.dawnOfDayNonRestrictedBalance,
      type: 'pink',
      icon: '/public/assets/liquidity/initial-unrestricted.png',
    },
    {
      label: '今日计划租金流入',
      value: detail?.todayPlanRentIncome,
      type: 'purple',
      icon: '/public/assets/liquidity/planned-rent.png',
    },
    {
      label: '今日计划还本付息',
      value: detail?.todayPlanRepayPrincipalInterest,
      type: 'purple',
      icon: '/public/assets/liquidity/planned-repayment.png',
    },
    {
      span: 7,
    },

    {
      label: '日终余额',
      value: detail?.endOfDayBalance,
      type: 'blue',
      icon: '/public/assets/liquidity/final-balance.png',
    },
    {
      label: '日终非监管户余额',
      value: detail?.endOfDayNonSupervisionBalance,
      type: 'blue',
      icon: '/public/assets/liquidity/final-available.png',
    },
    {
      label: '日终非受限余额',
      value: detail?.endOfDayNonRestrictedBalance,
      type: 'orange',
      icon: '/public/assets/liquidity/final-unrestricted.png',
    },
  ]

  // 租金流入表格列配置
  const rentColumns = [
    { title: '承租人名称', dataIndex: 'tenantName', width: 150 },
    {
      title: '合同编号',
      dataIndex: 'contractCode',
      width: 180,
      actions: ({ contractCode: name, contractId }) => [
        { name, to: `/contract/list/detail/${contractId}` },
      ],
    },
    { title: '本期到期日', dataIndex: 'expireDate', width: 120 },
    AmountColumn({
      title: '本期应还金额',
      dataIndex: 'shouldPayAmount',
      width: 120,
      initFormat: 1,
    }),
    AmountColumn({ title: '已还金额', dataIndex: 'paidAmount', width: 120, initFormat: 1 }),
    AmountColumn({ title: '未还金额', dataIndex: 'unpaidAmount', width: 120, initFormat: 1 }),
  ]

  // 还本付息表格列配置
  const repayColumns = [
    { title: '融资机构', dataIndex: 'financingOrgName', width: 150 },
    {
      title: '融资编号',
      dataIndex: 'financingCode',
      width: 180,
      actions: ({ financingCode: name, financingId }) => [
        { name, to: getFinancialUrl(name, financingId) },
      ],
    },
    AmountColumn({ title: '融资金额', dataIndex: 'financingAmount', width: 120, initFormat: 1 }),
    { title: '本期到期日', dataIndex: 'expireDate', width: 120 },
    AmountColumn({
      title: '本期应还金额',
      dataIndex: 'shouldPayAmount',
      width: 120,
      initFormat: 1,
    }),
    AmountColumn({
      title: '还款本金',
      dataIndex: 'shouldPayPrincipal',
      width: 120,
      initFormat: 1,
    }),
    AmountColumn({
      title: '还款利息',
      dataIndex: 'shouldPayInterest',
      width: 120,
      initFormat: 1,
    }),
  ]

  const rentTable = Table.useStore({
    pagination: {
      pageSize: 10,
    },
    request: async (params) => {
      return fundDayReportApi.postRentIncome(params)
    },
  })
  const repaymentTable = Table.useStore({
    pagination: {
      pageSize: 10,
    },
    request: async (params) => {
      return fundDayReportApi.postRepayPrincipalInterest(params)
    },
  })

  useImperativeHandle(ref, () => ({
    setDisablePagination: async (disabled) => {
      if (disabled) {
        rentTable.setPagination(false)
        repaymentTable.setPagination(false)
        await Promise.all([rentTable.search(), repaymentTable.search()])
      } else {
        rentTable.setPagination(true)
        repaymentTable.setPagination(true)
        await new Promise((resolve) => setTimeout(resolve, 100))
        rentTable.setPagination({
          current: 1,
          pageSize: 10,
        })
        repaymentTable.setPagination({
          current: 1,
          pageSize: 10,
        })
        await Promise.all([
          rentTable.search({ page: 1, pageSize: 10 }),
          repaymentTable.search({ page: 1, pageSize: 10 }),
        ])
      }
    },
  }))
  return (
    <Page noStyle store={pageStore}>
      <div className={styles['daily-report']} id="fundDailyReport">
        {/* 资金日报卡片 */}
        <div className={styles['daily-summary']}>
          <div className={styles['icon-wrapper']}>
            <div className={styles.date}>资金日报</div>
            <div>{detail?.date}</div>
            <img src="/public/assets/liquidity/daily-report.png" alt="daily report" />
          </div>
          <Row gutter={[0, 16]}>
            {cardList.map((item, index) => {
              if (!item.label) {
                return <Col key={index} span={7} offset={item.offset} />
              }
              return (
                <Col
                  key={index}
                  span={7}
                  className={styles['amount-item-wrapper']}
                  offset={item.offset}
                >
                  <img src={item.icon} alt={item.label} />
                  <div className={`${styles['amount-item']} ${styles[item.type]}`}>
                    <div className={styles.label}>{item.label}</div>
                    <div className={styles.value}>
                      {amountFormat(formatPercent(item.value, 10000 * 10000))}
                      <span className={styles.unit}>（万元）</span>
                    </div>
                  </div>
                </Col>
              )
            })}
          </Row>
        </div>

        {/* 监管户余额 */}
        <Row gutter={16}>
          <Col span={12}>
            <SupervisedAccounts
              data={rent?.nonSupervisionAccountBalanceList || []}
              title="非监管户余额"
              itemStyle={{ backgroundColor: '#f1f6fd' }}
            />
          </Col>
          <Col span={12}>
            <SupervisedAccounts
              data={rent?.supervisionAccountBalanceList || []}
              title="监管户余额"
              itemStyle={{ backgroundColor: '#f5fffd' }}
            />
          </Col>
        </Row>

        {/* 租金流入 */}
        <div className="z-sub-title">租金流入</div>
        <Table
          columnsFilter={'liquidity_FundDailyReport_1'}
          onFilter={(key, val) => saveServer('liquidity_FundDailyReport_1', val)}
          columns={rentColumns}
          store={rentTable}
        />

        {/* 还本付息 */}
        <div className="z-sub-title">还本付息</div>
        <Table
          columnsFilter={'liquidity_FundDailyReport_2'}
          onFilter={(key, val) => saveServer('liquidity_FundDailyReport_2', val)}
          columns={repayColumns}
          store={repaymentTable}
        />
      </div>
    </Page>
  )
})

DailyReport.displayName = 'DailyReport'

export default observer(DailyReport)
