import { observer } from '@zswl/admin'
import { Button, Modal, Select, Table, TableStore } from '@zswl/components'
import { Alert, Tag } from 'antd'
import { forwardRef, useImperativeHandle, useMemo, useRef, useState } from 'react'
import { MatchOptionColumn } from '@/components/Format'
import { getSearchColumns, getTableColumns, options } from '@/utils'
import ALL_COLUMNS from '../../Columns'
import flowCenterApi from '@/api/budget/flowCenter/flowCenterApi'
import { saveServer } from '@/utils'

const { collectionWriteOffStatusLocalEnum, paymentWriteOffStatusEnum } = options

const writeOffOptions = collectionWriteOffStatusLocalEnum

const BudgetFlowCenterBankFlowAutomaticMatchAddCashFlow = ({ modal, onFinish }) => {
  const [selectedRows, setSelectedRows] = useState([])
  const selectedRowKeys = selectedRows.map((item) => item?.collectionId)
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
      const writeOffStatusEnum = collectionWriteOffStatusLocalEnum
      const { label, color } = writeOffStatusEnum.find((v) => v.value === val) ?? {}
      return (
        <Tag style={{ border: 0 }} color={color}>
          {label}
        </Tag>
      )
    },
  })
  const columns = getTableColumns(
    ALL_COLUMNS,
    [
      writeOffStatusColumn,
      '客户名称',
      '合同编号',
      '期项',
      {
        title: '现⾦流项⽬',
        dataIndex: 'cashFlowItem',
        matchOption: 'cashFlowItemEnum',
      },
      '业务部⻔',
      '应收⾦额（元）',
      '本⾦（元）',
      '利息（元）',
      '已收⾦额（元）',
      '应收⽇期',
      '最近收款⽇',
      { title: '现⾦流编号', dataIndex: 'code' },
      '项⽬名称',
    ].filter(Boolean)
  )
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    '应收⽇期',
    '客户名称',
    writeOffStatusColumn,
    '合同编号',
    '业务部⻔',
    {
      title: '现⾦流项⽬',
      dataIndex: 'cashFlowItem',
      search: {
        element: <Select options={'cashFlowItemEnum'} mode="multiple" />,
        itemProps: {
          transform: (val) => ({ cashFlowItemList: val, cashFlowItem: undefined }),
        },
      },
    },
  ])

  const cashFlowTable = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          const res = await flowCenterApi.postCollectionList(params)
          setSelectedRows([])
          return res
        },
      }),
    []
  )

  const clearSelected = () => {
    cashFlowTable.clearSelected()
    setSelectedRows([])
  }

  const handleSelectChange = (selectedRowKeys, selectedRows) => {
    setSelectedRows(selectedRows)
  }

  return (
    <Modal
      store={modal}
      title="交易流水"
      width={1200}
      destroyOnClose
      onOk={async () => await onFinish(selectedRowKeys)}
    >
      <Table
        title={() => {
          return <Alert message={`已选中${selectedRows.length}条记录`}></Alert>
        }}
        store={cashFlowTable}
        columns={columns}
        rowKey={'collectionId'}
        columnWidth={200}
        scroll={{ x: true }}
        columnsFilter="BankFlow_AutomaticMatch_AddCashFlow"
                onFilter={(key,val) => saveServer('BankFlow_AutomaticMatch_AddCashFlow',val)}

        resizable
        serial
        extra={[
          {
            name: '清空选中',
            key: 'clearSelected',
            onClick: clearSelected,
          },
        ]}
        editable={false}
        selectable={{
          type: 'checkbox',
          selectedRowKeys: selectedRowKeys,
          preserveSelectedRowKeys: true,
          onChange: handleSelectChange,
        }}
        searchbar={{ labelCol: { span: 6 }, initialValues: {}, items: searchItem }}
      />
    </Modal>
  )
}

export default observer(BudgetFlowCenterBankFlowAutomaticMatchAddCashFlow)
