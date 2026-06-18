import React, { useMemo, useState } from 'react'
import { observer } from '@zswl/admin'
import { Table, Modal, Tabs, Page, App } from '@zswl/components'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { EditDescription } from '@/components/Table'
import { Col, Row } from 'antd'
import Store from './store'

const INIT_FORMAT = 1
const CreditInfo = observer(({ creditReportClientId, params }) => {
  const store = useMemo(() => new Store({ creditReportClientId }), [creditReportClientId])
  const { unsettledSummaryList } = store
  /**
   * 获取信息概要表列配置
   */
  const getInfoSummaryColumns = () => [
    { title: '首次有信贷交易的年份', dataIndex: 'firstCredityear', width: 160 },
    { title: '发生信贷交易的机构数', dataIndex: 'creditOrganizationNumber', width: 180 },
    {
      title: '当前有未结清信贷交易的机构数',
      dataIndex: 'unsettledCreditOrganizationNumber',
      width: 220,
    },
    {
      title: '首次有相关还款责任的年份',
      dataIndex: 'firstRepaymentResponsibilityYear',
      width: 200,
    },
  ]

  const { creditReportQualityClassificationEnum } = App.getData().optionsType
  /**
   * 获取未结清信贷及授信信息概要表列配置
   */
  const getUnsettledSummaryColumns = () => [
    { title: '款项模块', dataIndex: 'paymentTypeName' },
    ...creditReportQualityClassificationEnum.map((item) => ({
      title: item.label,
      children: [
        { title: '账户数', dataIndex: `${item.value}_AccountNumber` },
        AmountColumn({
          title: '余额',
          dataIndex: `${item.value}_AccountAmount`,
          initFormat: INIT_FORMAT,
        }),
      ],
    })),
  ]
  const creditSummaryColumns = [
    {
      title: '非循环信用额度',
      dataIndex: 'fundClassification',
      children: [
        AmountColumn({
          title: '总额',
          dataIndex: 'totalAmount',
          initFormat: INIT_FORMAT,
        }),
        AmountColumn({
          title: '已用额度',
          dataIndex: 'usedAmount',
          initFormat: INIT_FORMAT,
        }),
        AmountColumn({
          title: '剩余可用额度',
          dataIndex: 'remainingAvailableAmount',
          initFormat: INIT_FORMAT,
        }),
      ],
      width: 120,
    },
    {
      title: '循环信用额度',
      dataIndex: 'fundClassification',
      children: [
        AmountColumn({
          title: '总额',
          dataIndex: 'cycleTotalAmount',
          initFormat: INIT_FORMAT,
        }),
        AmountColumn({
          title: '已用额度',
          dataIndex: 'cycleUsedAmount',
          initFormat: INIT_FORMAT,
        }),
        AmountColumn({
          title: '剩余可用额度',
          dataIndex: 'cycleRemainingAvailableAmount',
          initFormat: INIT_FORMAT,
        }),
      ],
      width: 120,
    },
  ]

  /**
   * 获取相关还款责任信息概要表列配置
   */
  const getResponsibilitySummaryColumns = () => [
    MatchOptionColumn({
      title: '责任类型',
      dataIndex: 'responsibilityType',
      width: 160,
      matchOption: 'creditReportRepaymentLiabilityEnum',
    }),
    {
      title: '被追偿业务',
      dataIndex: 'recoverable',
      width: 160,
      children: [
        AmountColumn({
          title: '还款责任金额',
          dataIndex: 'recoverableRepaymentResponsibilityAmount',
          initFormat: INIT_FORMAT,
        }),
        { title: '账户数', dataIndex: 'recoverableAccountNumber', width: 160 },
        AmountColumn({
          title: '余额',
          dataIndex: 'recoverableBalance',
          initFormat: INIT_FORMAT,
        }),
      ],
    },
    {
      title: '其他借贷交易',
      dataIndex: 'other',
      width: 160,
      children: [
        AmountColumn({
          title: '还款责任金额',
          dataIndex: 'otherRepaymentResponsibilityAmount',
          initFormat: INIT_FORMAT,
        }),
        { title: '账户数', dataIndex: 'otherAccountNumber', width: 160 },
        AmountColumn({
          title: '余额',
          dataIndex: 'otherBalance',
          initFormat: INIT_FORMAT,
        }),
        AmountColumn({
          title: '关注类余额',
          dataIndex: 'otherFocusBalance',
          initFormat: INIT_FORMAT,
        }),
        AmountColumn({
          title: '不良类余额',
          dataIndex: 'otherBadBalance',
          initFormat: INIT_FORMAT,
        }),
      ],
    },
  ]
  const payableLoansColumns = [
    /**/
    { title: '账户编号', dataIndex: 'accountNumber', width: 160 },
    { title: '授信机构', dataIndex: 'creditorInstitution', width: 160 },
    MatchOptionColumn({
      title: '业务种类',
      dataIndex: 'businessType',
      width: 160,
      matchOption: 'creditReportRecordBusinessTypeEnum',
    }),
    { title: '开立日期', dataIndex: 'openingDate', width: 160 },
    { title: '到期日', dataIndex: 'expirationDate', width: 160 },
    { title: '币种', dataIndex: 'currency', width: 160 },
    AmountColumn({
      title: '借款金额',
      dataIndex: 'loanAmount',
      width: 160,
      initFormat: INIT_FORMAT,
    }),
    MatchOptionColumn({
      title: '发放形式',
      dataIndex: 'distributionMethod',
      width: 160,
      matchOption: 'creditReportDistributionMethodTypeEnum',
    }),
    MatchOptionColumn({
      title: '担保方式',
      dataIndex: 'guaranteeMethod',
      width: 160,
      matchOption: 'creditReportGuaranteeMethodEnum',
    }),
    AmountColumn({ title: '余额', dataIndex: 'balance', width: 160, initFormat: INIT_FORMAT }),
    MatchOptionColumn({
      title: '五级分类',
      dataIndex: 'fiveClassification',
      width: 160,
      matchOption: 'creditReportFiveClassificationEnum',
    }),
    AmountColumn({
      title: '逾期总额',
      dataIndex: 'totalOverdueAmount',
      width: 160,
      initFormat: INIT_FORMAT,
    }),
    AmountColumn({
      title: '逾期本金',
      dataIndex: 'overduePrincipal',
      width: 160,
      initFormat: INIT_FORMAT,
    }),
    { title: '逾期月数', dataIndex: 'overdueMonth', width: 160 },
    { title: '最近一次还款日期', dataIndex: 'lastRepaymentDate', width: 160 },
    AmountColumn({
      title: '最近一次还款总额',
      dataIndex: 'lastRepaymentAmount',
      width: 160,
      initFormat: INIT_FORMAT,
    }),
    MatchOptionColumn({
      title: '最近一次还款形式',
      dataIndex: 'lastRepaymentType',
      matchOption: 'creditReportLastRepaymentTypeEnum',
      width: 160,
    }),
    { title: '特定交易提示', dataIndex: 'specificTransactionPrompts', width: 160 },
    { title: '授信协议编号', dataIndex: 'creditAgreementNumber', width: 160 },
    { title: '信息报告日期', dataIndex: 'informationReportDate', width: 160 },
  ]
  return (
    <Page noStyle store={store} params={params}>
      <div style={{ padding: 12 }}>
        <h3 style={{ fontWeight: 600, marginBottom: 8 }}>信息概要</h3>
        <Table
          store={store.infoSummaryHeaderTable}
          columns={getInfoSummaryColumns()}
          pagination={false}
          columnWidth={160}
          size="small"
        />
        <EditDescription
          hiddenButton
          title={
            <Row>
              <Col
                span={12}
                style={{
                  fontWeight: 600,
                  textAlign: 'center',
                  lineHeight: '40px',
                  backgroundColor: '#f5f6fa',
                  borderRight: '1px solid #f0f0f0',
                }}
              >
                借贷交易
              </Col>
              <Col
                span={12}
                style={{
                  fontWeight: 600,
                  lineHeight: '40px',
                  backgroundColor: '#f5f6fa',
                  textAlign: 'center',
                }}
              >
                担保交易
              </Col>
            </Row>
          }
          detail={store.infoSummaryDetail}
          contentStyle={{ marginTop: -15 }}
          columns={[
            AmountColumn({
              title: '余额',
              dataIndex: 'loanTransactionBalance',
              width: 160,
              initFormat: INIT_FORMAT,
            }),
            AmountColumn({
              title: '余额',
              dataIndex: 'guaranteeTransactionBalance',
              width: 160,
              initFormat: INIT_FORMAT,
            }),
            {
              title: '其中: 被迫偿余额',
              dataIndex: 'loanTransactionRecoveryBalance',
              width: 160,
            },
            AmountColumn({
              title: '其中: 关注类余额',
              dataIndex: 'guaranteeTransactionFocusBalance',
              width: 160,
              initFormat: INIT_FORMAT,
            }),
            AmountColumn({
              title: '关注类余额',
              dataIndex: 'loanTransactionFocusBalance',
              width: 160,
              initFormat: INIT_FORMAT,
            }),
            AmountColumn({
              title: '不良类余额',
              dataIndex: 'guaranteeTransactionBadBalance',
              width: 160,
              initFormat: INIT_FORMAT,
            }),
            AmountColumn({
              title: '不良类余额',
              dataIndex: 'loanTransactionBadBalance',
              width: 160,
              initFormat: INIT_FORMAT,
            }),
          ]}
        />
        <Table
          store={store.infoSummaryStatTable}
          columns={[
            {
              title: '非信贷交易账户数',
              dataIndex: 'nonCreditTransactionNumber',
              width: 160,
            },
            { title: '欠税记录条数', dataIndex: 'taxArrearsRecordsNumber', width: 140 },
            {
              title: '民事判决记录条数',
              dataIndex: 'civilJudgmentRecordsNumber',
              width: 160,
            },
            {
              title: '强制执行记录条数',
              dataIndex: 'mandatoryExecutionRecordsNumber',
              width: 160,
            },
            {
              title: '行政处罚记录条数',
              dataIndex: 'administrativePenaltyRecordsNumber',
              width: 180,
            },
          ]}
          pagination={false}
          size="small"
        />
        <h3 style={{ fontWeight: 600, margin: '12px 0 8px' }}>未结清信贷及授信信息概要</h3>
        {unsettledSummaryList.map((item, idx) => {
          console.log('item: ', item)

          return (
            <Table
              rowKey={'id'}
              columns={getUnsettledSummaryColumns()}
              pagination={false}
              dataSource={item}
              columnWidth={140}
              size="small"
            />
          )
        })}
        <Table
          store={store.creditSummaryTable}
          columns={creditSummaryColumns}
          pagination={false}
          columnWidth={140}
          size="small"
        />
        <h3 style={{ fontWeight: 600, margin: '12px 0 8px' }}>相关还款责任信息概要</h3>
        <Table
          store={store.responsibilitySummaryTable}
          columns={getResponsibilitySummaryColumns()}
          pagination={false}
          columnWidth={160}
          size="small"
        />
        <h3 style={{ fontWeight: 600, margin: '12px 0 8px' }}>信贷记录明细</h3>
        <Table
          store={store.payableLoansTable}
          columns={payableLoansColumns}
          pagination={false}
          columnWidth={160}
          size="small"
        />
      </div>
    </Page>
  )
})
/**
 * 客户征信报告弹窗内容
 * 展示信息概要、未结清信贷及授信信息概要、相关还款责任信息概要
 */
const ReportModal = observer(({ store }) => {
  const { clientInfos, id, creditCode } = store.page.getData()
  const defaultId = String(clientInfos[0]?.id)
  const [activeKey, setActiveKey] = useState(defaultId)

  return (
    <Modal title="客户征信报告" store={store.creditReportModal} destroyOnClose width={1200}>
      <Tabs
        activeKey={activeKey}
        defaultActiveKey={defaultId}
        onChange={(key) => {
          setActiveKey(key)
        }}
        items={clientInfos.map((c) => ({
          key: String(c.id),
          label: c.clientName,
          children: (
            <CreditInfo
              creditReportClientId={c.id}
              params={{ creditReportId: id, creditCode, creditReportClientId: c.id }}
            />
          ),
        }))}
      />
    </Modal>
  )
})

export default ReportModal
