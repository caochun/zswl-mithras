import FormAmount from '@/components/Form/FormAmount'
import { amountFormat, formatPercent } from '@/utils'
import { observer } from '@zswl/admin'
import { Button, Drawer, Form, Select, Table } from '@zswl/components'
import { Col, Row, Space, Tag } from 'antd'
import { AmountColumn, DateColumn, InputColumn } from '@/components/Format'
import { useMemo } from 'react'
import styles from './index.less'
import { SummaryRender, calcThisAmount } from './WriteOffDrawer'
import { options } from '@/utils'
import { saveServer } from '@/utils'

const { financingFlowWriteOffStatusEnum } = options

const Index = ({ store }) => {
  // 对方户名交易时问 收款金额（元）付款金额（元》 本次模销金额（元》摘要操作
  const columns = [
    InputColumn({ title: '开户银行', dataIndex: 'bankName', width: 200 }),
    DateColumn({ title: '交易时间', dataIndex: 'transactionDate' }),
    AmountColumn({ title: '收款金额（元）', dataIndex: 'collectionAmount', width: 150 }),
    AmountColumn({ title: '付款金额（元）', dataIndex: 'paymentAmount', width: 150 }),
    InputColumn({ title: '摘要', dataIndex: 'mainInfo' }),
  ]

  const cashFlowColumns = useMemo(
    () => [
      {
        title: '机构名称',
        dataIndex: 'orgName',
        width: 260,
        render: (val, { orgNames }) => val ?? orgNames?.join('、'),
      },
      { title: '借据编号', dataIndex: 'receiptRepayBaseCode', width: 260 },
      { title: '现金流项目', dataIndex: 'cashFlowItemName' },
      { title: '现金流编号', dataIndex: 'cashFlowCode', width: 200 },
      AmountColumn({
        title: '应付金额（元）',
        dataIndex: 'shouldPayAmount',
        width: 180,
      }),
      DateColumn({
        title: '应付时间',
        dataIndex: 'shouldPayTime',
      }),
      AmountColumn({
        dataIndex: 'noPayAmount',
        title: '未付金额（元）',
        width: 180,
      }),
      AmountColumn({
        title: '本次核销金额（元）',
        dataIndex: 'thisWriteOffAmount',
        width: 180,
      }),
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
    []
  )

  return (
    <Drawer
      title="核销结果"
      width={'70%'}
      store={store.specialWriteOffResultModal}
      extra={null}
      destroyOnClose
      onClose={() => {
        store.specialWriteOffResultModal.close()
        store.table.search()
      }}
    >
      <Form store={store.form} labelCol={{ span: 12 }}>
        <div className={styles.title}> 银行流水信息</div>
        <Table
        columnsFilter={'flowCenter_BankFlow_SpecialWriteOffResultModal_1'}
                onFilter={(key,val) => saveServer('flowCenter_BankFlow_SpecialWriteOffResultModal_1',val)}
        
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
        </Row>
        <div className={styles.title}> 本次核销结果</div>
        <Table
        columnsFilter={'flowCenter_BankFlow_SpecialWriteOffResultModal_2'}
                onFilter={(key,val) => saveServer('flowCenter_BankFlow_SpecialWriteOffResultModal_2',val)}
        
          resizable
          store={store.specialWriteOffResultTable}
          columns={cashFlowColumns}
          scroll={{ x: true }}
          pagination={false}
          summary={SummaryRender}
          rowKey={'uuid'}
        />
      </Form>
    </Drawer>
  )
}

export default observer(Index)
