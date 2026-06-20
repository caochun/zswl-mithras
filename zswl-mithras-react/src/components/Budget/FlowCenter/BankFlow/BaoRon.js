import { AmountColumn, DateColumn, InputColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Button, Table } from '@zswl/components'
import Store from './baoRonStore.js'
import { useMemo } from 'react'
import { FormAmount } from '@/components/Form'
import { saveServer } from '@/utils'

const Index = ({ getCount }) => {
  const store = useMemo(() => new Store({ getCount }), [])
  const { rows, keys } = store.table.getSelected()
  const columns = [
    InputColumn({ title: '银行帐号', dataIndex: 'accountnumber', width: 220 }),
    InputColumn({ title: '开户银行', dataIndex: 'bankName' }),
    DateColumn({ title: '交易时间', dataIndex: 'tradetime', width: 120 }),
    InputColumn({ title: '摘要', dataIndex: 'comments' }),
    AmountColumn({
      title: '收款金额',
      dataIndex: 'amount',
      width: 140,
      render: (val, { moneyway }) => {
        const amount = moneyway == 2 ? val : undefined
        return <FormAmount.Format value={amount} />
      },
    }),
    AmountColumn({
      title: '付款金额',
      dataIndex: 'amount',
      width: 140,
      render: (val, { moneyway }) => {
        const amount = moneyway == 1 ? val : undefined
        return <FormAmount.Format value={amount} />
      },
    }),
    AmountColumn({ title: '余额', dataIndex: 'currentbalance', width: 120 }),
    InputColumn({ title: '对方户名', dataIndex: 'oppositeaccountname', search: true }),
    InputColumn({
      title: '对方账号',
      dataIndex: 'oppositeaccountnumber',
      search: true,
      width: 180,
    }),
    InputColumn({ title: '对方开户行', dataIndex: 'oppositebank', width: 200 }),
    InputColumn({ title: '保融流水Id', dataIndex: 'bruid', search: true, width: 200 }),
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      actions: (val) => [
        !val.ignoreFlag && {
          name: '忽略',
          confirm: true,
          onClick: () => {
            store.ignore(val.id)
          },
        },
      ],
    },
  ]

  return (
    <>
      <Table
        columnsFilter={'flowCenter_BankFlow_BaoRon'}
                onFilter={(key,val) => saveServer('flowCenter_BankFlow_BaoRon',val)}

        store={store.table}
        columns={columns}
        columnWidth={120}
        resizable
        serial
        editable={false}
        selectable
        rowClassName={(record, rowIndex) => {
          return !!record?.ignoreFlag ? 'table-row-remove' : ''
        }}
        scroll={{ x: 1000 }}
      />
    </>
  )
}

export default observer(Index)
