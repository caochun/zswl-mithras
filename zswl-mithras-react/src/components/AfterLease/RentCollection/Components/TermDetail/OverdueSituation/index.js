import { amountFormat } from '@/utils'
import { Table } from '@zswl/components'
import { Descriptions, Form, Input, InputNumber } from 'antd'
import { useEffect, useMemo } from 'react'
import styles from '../index.less'
import { observer } from '@zswl/admin'
import NoOverdue from '../../NoOverdue'
import { saveServer } from '@/utils'

const OverdueCollection = ({ store }) => {
  const [form] = Form.useForm()

  const overdue = store.initData.overdue || {}

  const descriptionsColumns = useMemo(() => {
    const { sumAmount, writeOffAmount, lastAmount } = overdue
    return [
      {
        label: '系统计算罚息(元)',
        value: amountFormat(sumAmount / 10000) || '-',
      },
      {
        label: '核销罚息(元)',
        value: amountFormat(writeOffAmount / 10000) || '-',
      },
      {
        label: '罚息余额(元)',
        value: amountFormat(lastAmount / 10000) || '-',
      },
    ]
  }, [])
  const columns = [
    {
      title: '记录日期',
      width: 80,
      dataIndex: 'recordDate',
    },
    {
      title: '逾期金额(元)',
      width: 100,
      dataIndex: 'overdueAmount',
      render: (val) => amountFormat(val / 10000),
    },
    {
      title: '单日产生罚息(元)',
      width: 100,
      dataIndex: 'dayPenaltyInterest',
      render: (val) => amountFormat(val / 10000),
    },
    {
      title: '罚息余额(元)',
      width: 100,
      dataIndex: 'lastPenaltyInterest',
      render: (val) => amountFormat(val / 10000),
    },
  ]
  useEffect(() => {
    const { dailyRate, penaltyInterestAmount, creditAmount } = overdue

    form.setFieldsValue({
      dailyRate: dailyRate / 10000,
      penaltyInterestAmount: amountFormat(penaltyInterestAmount / 10000),
      creditAmount: amountFormat(creditAmount / 10000),
    })
  }, [overdue])
  if (!overdue.sumAmount) {
    return <NoOverdue />
  }
  return (
    <div>
      <Form form={form}>
        <Descriptions column={2} className={styles.desc1}>
          <Descriptions.Item label="罚息日利率(%)">
            <Form.Item name="dailyRate">
              <Input disabled style={{ width: 180 }} placeholder="请输入罚息日率！" />
            </Form.Item>
          </Descriptions.Item>
          {/* <Descriptions.Item label="状态">{overdue.status}</Descriptions.Item> */}
          <Descriptions.Item label="罚息金额(元)">
            <Form.Item name="penaltyInterestAmount">
              <InputNumber disabled style={{ width: 180 }} placeholder="请输入罚息金额！" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label="罚息减免金额(元)">
            <Form.Item name="creditAmount">
              <InputNumber disabled style={{ width: 180 }} placeholder="请输入罚息金额！" />
            </Form.Item>
          </Descriptions.Item>
        </Descriptions>
      </Form>
      <div className={styles.modalTitle}>罚息汇总：</div>
      <Descriptions
        title=""
        bordered
        column={3}
        style={{
          marginBottom: 20,
        }}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.desSmell}
      >
        {descriptionsColumns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
      <div className={styles.modalTitle}>罚息记录：</div>
      <Table
        onFilter={(key, val) => saveServer('TermDetail_OverdueSituation_1', val)}
        columnsFilter={'TermDetail_OverdueSituation_1'}
        scroll={false}
        store={store.penaltyInterestModalTable}
        columns={columns}
      />
    </div>
  )
}
export default observer(OverdueCollection)
