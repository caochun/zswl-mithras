import { observer } from '@zswl/admin'
import PayMentDetailsModal from './ApplicationModal'
import { Table, Button, App } from '@zswl/components'
import { Space } from 'antd'
import { useMemo } from 'react'
import BankAccount from '@/components/Form/BankAccount'
import { amountFormat } from '@/utils'
import styles from '../index.less'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const { applicationEditStatus, page } = store
  const detail = page.getData()

  const columns = useMemo(() => {
    const defaultColumns = [
      {
        title: '对方账户',
        dataIndex: 'oppositeAccount',
        width: 280,
        render: (value) => {
          return <BankAccount.Format value={value}></BankAccount.Format>
        },
      },
      {
        title: '对方账户名',
        dataIndex: 'oppositeAccountName',
      },
      {
        title: '对方账户开户行',
        dataIndex: 'oppositeAccountBank',
      },
      {
        title: '支付方式',
        dataIndex: 'paymentMethod',
        render: (val) => App.matchOption('paymentMethod', val).label,
      },
      {
        title: '支付金额',
        dataIndex: 'paymentAmount',
        render: (val) => {
          return amountFormat(val / 10000)
        },
      },
      {
        title: '附言',
        dataIndex: 'postscript',
      },
    ]

    if (applicationEditStatus) {
      defaultColumns.push({
        title: '操作',
        dataIndex: 'contractCode',
        width: 160,
        fixed: 'right',
        render: (_, record) => {
          return (
            <Space>
              <Button
                type="link"
                onClick={() => {
                  const openData = {
                    ...record,
                    paymentAmount: record.paymentAmount / 10000,
                    type: 'edit',
                  }
                  store.createApplicationModal.open(openData)
                }}
              >
                编辑
              </Button>
              <Button type="link" onClick={() => store.paymentPlanTable.deleteRow(record)}>
                删除
              </Button>
            </Space>
          )
        },
      })
    }
    return defaultColumns
  }, [applicationEditStatus, detail])

  return (
    <>
      <div className={styles.nav} style={{ marginTop: 12, marginBottom: 12 }}>
        <div className={styles.subTitle}>本次支付明细</div>
        <div className={styles.btnWrap}>
          {store.applicationEditStatus && (
            <Button type="primary" onClick={() => store.createApplicationModal.open()}>
              新增
            </Button>
          )}
        </div>
      </div>
      <Table
        columnsFilter={'Applicat_PaymentDetail_1'}
        onFilter={(key,val) => saveServer('Applicat_PaymentDetail_1',val)}
        resizable
        store={store.paymentPlanTable}
        rowKey="editId"
        columns={columns}
        scroll={{ x: 1200 }}
      />
      <PayMentDetailsModal store={store} />
    </>
  )
}

export default observer(Index)
