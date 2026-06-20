import { Button, Table } from '@zswl/components'
import { Radio, Tag } from 'antd'
import Store from './store'
import { observer } from '@zswl/admin'
import PaymentOffModal from './PaymentOffModal'
import { getSearchColumns, getTableColumns, options } from '@/utils'
import ALL_COLUMNS from '../Columns'
import { MatchOptionColumn } from '@/components/Format'
import { useMemo } from 'react'
import DetailModal from './DetailModal'
import PageListDown from '@/components/PageListDown'
import { saveServer } from '@/utils'
import { StoreExportAction as ExportAction } from '@/components/Actions'
import { getFinancialUrl } from '@/components/Financial/FinancingUrlEntries'

const { fundReceiptRepayCashFlowState } = options

const selectOptions = [
  { label: '付款', value: 'PAY' },
  { label: '收款', value: 'COLLECT' },
]
export { getFinancialUrl }
const Index = ({ getCount }) => {
  const store = useMemo(() => new Store({ getCount }), [])
  const writeOffStatusColumn = MatchOptionColumn({
    title: '核销状态',
    dataIndex: 'writeOffStatusList',
    matchOption: fundReceiptRepayCashFlowState,
    mode: 'multiple',
    search: true,
    render: (val, record) => {
      const { label, color } =
        fundReceiptRepayCashFlowState.find((v) => v.value === record.writeOffStatus) ?? {}
      return (
        <Tag style={{ border: 0 }} color={color}>
          {label}
        </Tag>
      )
    },
  })

  const isPay = store.flowType === 'PAY'
  const columns = getTableColumns(
    ALL_COLUMNS,
    [
      writeOffStatusColumn,
      { title: '融资渠道', rename: '融资机构' },
      {
        title: '融资编号',
        dataIndex: 'financingCode',
        actions: ({ financingCode: name, financingId }) => [
          { name, to: getFinancialUrl(name, financingId) },
        ],
      },
      isPay && '期项',

      {
        title: '现⾦流项⽬',
        dataIndex: 'cashFlowItem',
        matchOption: 'businessFlowFinanceCashFlowItemType',
      },
      { title: '日期', rename: isPay ? '应付日期' : '应收日期' },
      isPay && { title: '⾦额（元）', rename: '应付金额（元）' },
      isPay && '应付本金（元）',
      isPay && '应付利息（元）',
      { title: '已付金额（元）', rename: isPay ? undefined : '已收金额（元）' },
      isPay && '已付本金（元）',
      isPay && '已付利息（元）',
      { title: '最近付款日', rename: isPay ? undefined : '最近收款日' },
    ].filter(Boolean)
  )
  const { rows } = store.fundamentalsTable.getSelected()
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    { title: '融资渠道', rename: '融资机构' },
    writeOffStatusColumn,
    '日期',
    '融资编号',
  ])

  return (
    <div>
      <Radio.Group
        options={selectOptions}
        onChange={store.radioChange}
        optionType="button"
        buttonStyle="solid"
        value={store.flowType}
        style={{ marginBottom: 8 }}
      />
      <Table
        columnsFilter="flowCenter_Fundamentals_1"
                onFilter={(key,val) => saveServer('flowCenter_Fundamentals_1',val)}

        store={store.fundamentalsTable}
        columns={columns}
        editable={false}
        rowKey={'idKey'}
        selectable={{ type: 'checkbox' }}
        // columnsFilter
        actions={[
          <Button type="primary" onClick={store.handleOff} disabled={rows.length !== 1}>
            手工核销
          </Button>,
          <Button onClick={store.openDetail} disabled={rows.length !== 1}>
            结算明细
          </Button>,
          <ExportAction store={store.fundamentalsTable} key="export" api={store.exportFile}/>
          // <ExportAction store={store.fundamentalsTable} key="export" api='/business/flow/finance/list/export' type='post'/>
        ]}
        extra={
          <PageListDown
            table={store.fundamentalsTable}
            module={isPay ? 'flowFinancialPay' : 'flowFinancialCollect'}
            extraParams={{ flowType: store.flowType }}
          />
        }
        columnWidth={120}
        searchbar={{
          labelCol: { span: 6 },
          items: searchItem,
        }}
        scroll={{ x: 1000 }}
      />
      <DetailModal store={store} />
      <PaymentOffModal store={store} />
    </div>
  )
}
export default observer(Index)
