import { Button, Select, Table, Tabs } from '@zswl/components'
import { Empty, Radio, Tag } from 'antd'
import Store from './store'
import { observer } from '@zswl/admin'
import PaymentOffModal from './PaymentOffModal'
import DetailModal from './DetailModal'
import ReceiptOffModal from './ReceiptOffModal'
import { getSearchColumns, getTableColumns, options } from '@/utils'
import ALL_COLUMNS from '../Columns'
import { MatchOptionColumn } from '@/components/Format'
import { useMemo } from 'react'
import CheckLetter from './CheckLetter'
import PageListDown from '@/components/PageListDown'
import { saveServer } from '@/utils'

const { collectionWriteOffStatusLocalEnum, paymentWriteOffStatusEnum } = options

const Index = ({ getCount }) => {
  const store = useMemo(() => new Store({ getCount }), [])
  const selectOptions = [
    { label: '付款', value: 'payment' },
    { label: '收款', value: 'receipt' },
  ]
  const isPayment = store.radioValue === 'payment'
  const writeOffOptions = isPayment ? paymentWriteOffStatusEnum : collectionWriteOffStatusLocalEnum
  const writeOffStatusColumn = MatchOptionColumn({
    title: '核销状态',
    dataIndex: 'writeOffStatus',
    matchOption: writeOffOptions,
    mode: 'multiple',
    search: {
      element: <Select options={writeOffOptions} mode="multiple" />,
      itemProps: {
        transform: (val) => ({ writeOffStatusList: val, writeOffStatus: undefined }),
      },
    },
    render: (val) => {
      const writeOffStatusEnum = isPayment
        ? paymentWriteOffStatusEnum
        : collectionWriteOffStatusLocalEnum
      const { label, color } = writeOffStatusEnum.find((v) => v.value === val) ?? {}
      return (
        <Tag style={{ border: 0 }} color={color}>
          {label}
        </Tag>
      )
    },
  })
  //付款：核销状态、客⼾名称、合同编号、期项、现⾦流项⽬、业务部⻔、应收⾦额（元）、本⾦（元）、利息（元）、已收⾦额（元）、应收⽇期、最近收款⽇、现⾦流编号、项⽬名称
  const cashFlowOptions = isPayment ? 'paymentFlowItemEnum' : 'cashFlowItemEnum'
  const columns = getTableColumns(
    ALL_COLUMNS,
    [
      writeOffStatusColumn,
      '客户名称',
      '合同编号',
      !isPayment && '期项',
      {
        title: '现⾦流项⽬',
        dataIndex: 'cashFlowItem',
        matchOption: cashFlowOptions,
      },
      '业务部⻔',

      !isPayment && '应收⾦额（元）',
      !isPayment && '本⾦（元）',
      !isPayment && '利息（元）',
      !isPayment && '已收⾦额（元）',
      !isPayment && '应收⽇期',

      isPayment && '应付⾦额（元）',
      isPayment && '已付⾦额（元）',
      isPayment && '应付⽇期',

      isPayment && '最近付款⽇',
      !isPayment && '最近收款⽇',
      { title: '现⾦流编号', dataIndex: isPayment ? 'paymentCode' : 'code' },
      '项⽬名称',
    ].filter(Boolean)
  )
  const { rows } = store.table.getSelected()
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    isPayment ? '应付⽇期' : '应收⽇期',
    '客户名称',
    writeOffStatusColumn,
    '合同编号',
    '业务部⻔',
    {
      title: '现⾦流项⽬',
      dataIndex: 'cashFlowItem',
      search: {
        element: <Select options={cashFlowOptions} mode="multiple" />,
        itemProps: {
          transform: (val) => ({ cashFlowItemList: val, cashFlowItem: undefined }),
        },
      },
    },
  ])

  return (
    <div>
      <Radio.Group
        options={selectOptions}
        onChange={store.radioChange}
        optionType="button"
        buttonStyle="solid"
        value={store.radioValue}
        style={{ marginBottom: 8 }}
      />
      <Table
        columnsFilter="flowCenter_ProjectSide_1"
                onFilter={(key,val) => saveServer('flowCenter_ProjectSide_1',val)}
        
        store={store.table}
        columns={columns}
        rowKey={isPayment ? 'paymentId' : 'collectionId'}
        actions={[
          <Button type="primary" onClick={store.handleOff} disabled={!rows.length}>
            手工核销
          </Button>,
          <Button onClick={store.openDetail} disabled={!rows.length}>
            结算明细
          </Button>,
          <Button onClick={store.$checkLetter.open}>打印收据</Button>,
        ]}
        extra={<PageListDown table={store.table} module={isPayment ? 'projPay' : 'projCollect'} />}
        editable={false}
        selectable={{
          type: 'radio',
        }}
        // columnsFilter
        columnWidth={120}
        searchbar={{
          labelCol: { span: 6 },
          items: searchItem,
          defaultExpanded: true,
        }}
        scroll={{ x: 1000 }}
      />
      <PaymentOffModal store={store} />
      <ReceiptOffModal store={store} />
      <DetailModal store={store} />
      <CheckLetter store={store} />
    </div>
  )
}
export default observer(Index)
