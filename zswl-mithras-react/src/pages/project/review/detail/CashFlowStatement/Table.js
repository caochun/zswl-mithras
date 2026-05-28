import { getQuery } from '@zswl/admin'
import { Table } from '@zswl/components'
import { AmountColumn, InputColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const CashFlowStatementTable = ({ tableStore }) => {
  const { bizType, clientType } = getQuery()
  const bizTypeTitle = {
    BL: '应收保理款(元)',
    ZR: '回收款(元)',
  }[bizType || clientType]

  const columns = [
    InputColumn({
      title: '日期',
      dataIndex: 'date',
    }),
    InputColumn({
      title: '期项',
      dataIndex: 'phase',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>{bizTypeTitle || '租金(元)'}</div>,
      dataIndex: 'rent',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>本金(元)</div>,
      dataIndex: 'principal',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>利息(元)</div>,
      dataIndex: 'interest',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>剩余本金(元)</div>,
      dataIndex: 'remainingPrincipal',
    }),
  ]
  return <Table         columnsFilter={'CashFlowStatement_Table_1'}
  onFilter={(key,val) => saveServer('CashFlowStatement_Table_1',val)} store={tableStore} columns={columns} />
}

export default CashFlowStatementTable
