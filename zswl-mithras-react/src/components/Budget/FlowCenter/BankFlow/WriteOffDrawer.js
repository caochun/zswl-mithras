import { FormAmount } from '@/components/Form'
import { amountFormat, formatPercent } from '@/utils'
import { observer } from '@zswl/admin'
import { Button, Drawer, Form, Select, Table } from '@zswl/components'
import { Col, Row, Space, Tag } from 'antd'
import CashFlowModal from './CashFlowModal'
import SpecialCashFlowModal from './SpecialCashFlowModal'
import SpecialPayFlowListModal, { AbsModal } from './SpecialPayFlowListModal'
import SpecialWriteOffResultModal from './SpecialWriteOffResultModal'
import { useMemo } from 'react'
import styles from './index.less'
import { AmountColumn, DateColumn, InputColumn } from '@/components/Format'
import { options } from '@/utils'
import { formatListJson } from './utils'
import { saveServer } from '@/utils'

const { financingFlowWriteOffStatusEnum } = options
const { Summary } = Table

export const calcThisAmount = (r, writeOffType) => {
  const writtenAmountTotal = r.writeOffedAmountList.reduce(
    (pre, cur) => pre + cur.writeOffAmount,
    0
  )
  const paymentAmount = r.paymentAmount ? r.paymentAmount - writtenAmountTotal : 0
  const collectionAmount = r.collectionAmount ? r.collectionAmount - writtenAmountTotal : 0
  const value =
    writeOffType === 'PAYMENT' ? paymentAmount - collectionAmount : collectionAmount - paymentAmount
  return value
}
export const SummaryRender = (data = []) => {
  const repeatMap = {}
  const { shouldPayAmountTotal, noPayAmountTotal, thisWriteOffAmountTotal } = formatListJson(
    data,
    false
  ).reduce(
    (previousValue, currentValue) => {
      const key = `${currentValue.receiptCode}-${currentValue.cashFlowItemName}-${currentValue.phase}`
      previousValue.thisWriteOffAmountTotal += +currentValue.thisWriteOffAmount || 0
      if (!repeatMap[key]) {
        previousValue.shouldPayAmountTotal += +currentValue.shouldPayAmount || 0
        previousValue.noPayAmountTotal += +currentValue.noPayAmount || 0
        repeatMap[key] = true
      }
      return previousValue
    },
    {
      shouldPayAmountTotal: 0,
      noPayAmountTotal: 0,
      thisWriteOffAmountTotal: 0,
    }
  )
  return (
    <Summary.Row>
      <Summary.Cell>合计</Summary.Cell>
      {[1, 2, 3, 4, 5, 6].map((_) => (
        <Summary.Cell>-</Summary.Cell>
      ))}

      <Summary.Cell>
        <div style={{ textAlign: 'right' }}>
          {amountFormat(formatPercent(thisWriteOffAmountTotal))}
        </div>
      </Summary.Cell>
    </Summary.Row>
  )
}
const BudgetFlowCenterBankFlowWriteOffDrawer = ({ store }) => {
  const { sideType, writeOffType, setSideType, type } = store
  const isSide = ['PROCESSED_PROJ_SIDE', 'PROCESSED_FUNDS_END'].includes(type)
  // 对方户名交易时问 收款金额（元）付款金额（元》 本次模销金额（元》摘要操作
  const columns = [
    InputColumn({ title: '对方户名', dataIndex: 'otherName', width: 200 }),
    DateColumn({ title: '交易时间', dataIndex: 'transactionDate' }),
    AmountColumn({ title: '收款金额（元）', dataIndex: 'collectionAmount', width: 150 }),
    AmountColumn({ title: '付款金额（元）', dataIndex: 'paymentAmount', width: 150 }),
    AmountColumn({
      title: '本次核销金额（元）',
      dataIndex: 'amountWrittenOffThisTime',
      min: -Infinity,
      render: (v, r) => {
        const value = calcThisAmount(r, writeOffType)
        return <FormAmount.Format value={value} initFormat={10000} />
      },
    }),
    InputColumn({ title: '摘要', dataIndex: 'mainInfo' }),

    {
      title: '操作',
      width: 280,
      actions: (record) => {
        return [
          <a
            href="#"
            onClick={() => {
              store.bankTableDelete(record.id)
            }}
          >
            删除
          </a>,
          <div>
            {record.writeOffedAmountList.map(({ writeOffAmount, writeOffTime }) => {
              const title = `${writeOffTime}  已核销 ${amountFormat(
                formatPercent(writeOffAmount)
              )} 元`
              return (
                <div>
                  <Tag color="green">{title}</Tag>
                </div>
              )
            })}
          </div>,
        ]
      },
    },
  ]
  const isProj = store.sideType === 'PROJ_SIDE'
  const cashFlowColumns = useMemo(
    () => [
      isProj && { title: '客户名称', dataIndex: 'clientName', width: 200 },
      isProj && { title: '合同编号', dataIndex: 'contractCode', width: 260 },
      !isProj && {
        title: '机构名称',
        dataIndex: 'orgName',
        width: 260,
        render: (val, { orgNames }) => val ?? orgNames?.join('、'),
      },
      !isProj && { title: '借据编号', dataIndex: 'receiptRepayBaseCode', width: 260 },

      { title: '现金流项目', dataIndex: 'cashFlowItemName' },
      { title: '现金流编号', dataIndex: 'cashFlowCode', width: 200 },
      AmountColumn({
        title: writeOffType === 'PAYMENT' ? '应付金额（元）' : '应收金额（元）',
        dataIndex: 'shouldPayAmount',
        width: 180,
      }),
      DateColumn({
        title: writeOffType === 'PAYMENT' ? '应付时间' : '应收时间',
        dataIndex: 'shouldPayTime',
      }),
      AmountColumn({
        dataIndex: 'noPayAmount',
        title: writeOffType === 'PAYMENT' ? '未付金额（元）' : '未收金额（元）',
        width: 180,
      }),
      AmountColumn({
        title: '本次核销金额（元）',
        dataIndex: 'thisWriteOffAmount',
        width: 180,
      }),
      {
        title: '操作',
        dataIndex: 'add',
        width: 150,
        editable: (record, index) => {
          if (record.isRemote)
            return (
              <Button
                type="link"
                onClick={() => store.reverse(record)}
                confirm={!record.isConfirm}
                disabled={record.isConfirm}
              >
                反核销
              </Button>
            )
          return [
            <Button type="link" onClick={() => store.handleEdit(record)}>
              编辑
            </Button>,
            <Button type="link" onClick={() => store.deleteRow(record, index)}>
              删除
            </Button>,
          ].filter(Boolean)
        },
      },
      {
        title: '核销类型',
        dataIndex: 'status',
        render: (val, record) => {
          let value = val
          if (!record.isRemote) {
            if (+record.thisWriteOffAmount === 0) {
              value = 'NO_WRITE_OFF'
            } else if (+record.thisWriteOffAmount === record.noPayAmount) {
              value = 'COMPLETE_WRITE_OFF'
            } else if (record.thisWriteOffAmount < record.noPayAmount) {
              value = 'PART_WRITE_OFF'
            }
          }
          const { label, color } =
            financingFlowWriteOffStatusEnum.find((v) => v.value === value) ?? {}
          return (
            <Tag style={{ border: 0 }} color={color}>
              {label}
            </Tag>
          )
        },
      },
    ],
    [writeOffType, sideType]
  )

  return (
    <div>
      <Drawer
        destroyOnClose
        onClose={() => {
          store.offDrawer.close()
          store.table.search()
          store.clearSelected()
        }}
        title={isSide ? '核销明细' : '手工核销'}
        width={'70%'}
        store={store.offDrawer}
        extra={
          <Space>
            <Button
              onClick={() => {
                store.form.resetFields()
                store.offDrawer.close()
                store.clearSelected()
              }}
            >
              取消
            </Button>
            <Button type="primary" onClick={store.handleSubmit}>
              确定
            </Button>
          </Space>
        }
      >
        <Form store={store.form} labelCol={{ span: 12 }}>
          <div className={styles.title}> 银行流水信息</div>
          <Table
        columnsFilter={'flowCenter_BankFlow_WriteOffDrawer_1'}
        onFilter={(key,val) => saveServer('flowCenter_BankFlow_WriteOffDrawer_1',val)}

            columns={columns}
            editable={false}
            scroll={{ x: true }}
            pagination={false}
            resizable
            store={store.bankFlowTable}
          />
          <Row style={{ marginTop: 12 }}>
            <Col span={10}>
              <Form.Item label="本次核销金额合计(元）" name="amount">
                <FormAmount disabled />
              </Form.Item>
            </Col>
            <Col span={10}>
              <Form.Item label="收付款类型" name="writeOffType">
                <Select
                  options={'bankFlowPaymentCollectionTypeEnum'}
                  disabled
                  style={{ width: 200 }}
                />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={10}>
              <Form.Item label="核销类型" name={'sideType'}>
                <Select
                  onChange={setSideType}
                  style={{ width: 200 }}
                  disabled={isSide}
                  options={'bankFlowWriteOffTypeEnum'}
                />
              </Form.Item>
            </Col>
          </Row>
          {['PROJ_SIDE', 'FUNDS_END'].includes(sideType) && (
            <Table
        columnsFilter={'flowCenter_BankFlow_WriteOffDrawer_2'}
        onFilter={(key,val) => saveServer('flowCenter_BankFlow_WriteOffDrawer_2',val)}

              resizable
              store={store.cashFlowTable}
              columns={cashFlowColumns}
              columnWidth={120}
              rowClassName={(record, rowIndex) => {
                return !!record?.isConfirm ? 'table-row-remove' : ''
              }}
              actions={[
                !isSide && <Button.Add onClick={store.addCashFlow}>新增现金流项目</Button.Add>,
              ]}
              scroll={{ x: 1000 }}
              pagination={false}
              summary={SummaryRender}
              rowKey={'uuid'}
            />
          )}
        </Form>
        <CashFlowModal writeOffType={writeOffType} store={store} sideType={sideType} />
      </Drawer>
      <SpecialCashFlowModal store={store} />
      <SpecialPayFlowListModal store={store} />
      <SpecialWriteOffResultModal store={store} />
      <AbsModal absModal={store.absModal} absTable={store.absTable} />
    </div>
  )
}

export default observer(BudgetFlowCenterBankFlowWriteOffDrawer)
