import { App, Button, Page, Table, TableStore } from '@zswl/components'
import { http, observer } from '@zswl/admin'
import { useMemo } from 'react'
import { MatchOptionColumn, AmountColumn } from '@/components/Format'
import { Tag, Space } from 'antd'
import { hasValue } from '@/utils'
import { saveServer } from '@/utils'

function Index({ store }) {
  const { lesseeypeEnum = [], clientRole = [] } = App.getData().optionsType

  const goProcess = (record) => {
    const { clientId, clientName } = record
    const search = JSON.stringify({
      clientId,
      clientName,
    })
    window.open(`/process/query?search=${search}`)
  }

  const transactionStructureType = [...lesseeypeEnum, ...clientRole]
  const nameColumns = [
    {
      title: '客户名称',
      dataIndex: 'clientName',
      render: (value, record) => {
        return (
          <Space>
            <span>{value}</span>
            {record.count > 0 ? (
              <Tag color="#f50" onClick={() => goProcess(record)}>
                <a>{record.count}条舆情未完成处理</a>
              </Tag>
            ) : null}
          </Space>
        )
      },
    },
    //交易结构类型,客户类型
    MatchOptionColumn({
      title: '交易结构类型',
      dataIndex: 'transactionStructureType',
      matchOption: transactionStructureType,
    }),
    MatchOptionColumn({ title: '客户类型', dataIndex: 'clientType', matchOption: 'clientType' }),
    AmountColumn({
      title: '存量风险敞口(元)',
      dataIndex: 'stockRiskExposure',
    }),
  ]

  return (
    <>
      <div className="z-sub-title">交易结构信息</div>
      <Table
              columnsFilter={'detail_Components_TransactionInfo'}
              onFilter={(key,val) => saveServer('detail_Components_TransactionInfo',val)}
        store={store.transactionTable}
        columns={nameColumns}
        pagination={false}
        editable={false}
        columnWidth={120}
        scroll={{ x: 'auto' }}
      />
    </>
  )
}

export default observer(Index)
