import { Button, Page, Select, Table } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { getTableColumns, getFormColumns, hasValue, getSearchColumns } from '@/utils'
import {
  FinancialFundColumns as ALL_COLUMNS,
  FinancialFundListChangeModal as ChangeModal,
  FinancialFundListCreateModal as CreateModal,
} from '@/components/Financial/FundListEntries'
import Store from './store'
import { PageListDown } from '@/components'
import { InputEditable, MatchOptionColumn } from '@/components/Format'
import { Summary } from '@/components/Table'
import { saveServer } from '@/utils'
import Api from '@/api/financial/fundApi'
import { message } from 'antd'

const nameColumns = [
  {
    title: '融资编号',
    actions: ({ financingCode: name, id }) => [{ name, to: `/financial/fund/detail/${id}` }],
  },
  '业务类型',
  '融资机构',
  '融资金额(元)',
  '剩余本金(元)',
  '综合融资成本(%)',
  '担保融资金额(元)',
  '信用融资金额(元)',
  '借款年利率',
  '利率类型',
  {
    title: '借款日期(列表)',
    rename: '贷款日',
  },
  {
    title: '到期日期(列表)',
    rename: '到期日期',
  },
  '融资状态',
  '审批状态',
  '质押资产',
  '创建人',
  {
    title: '创建日期(列表)',
    rename: '创建日期',
  },
  '更新日期',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)

function Index() {
  const [isClear, setClear] = useState(false)
  const store = useMemo(() => {
    return new Store()
  }, [])

  const { sumData } = store
  const tableStore = Table.useStore({
    request: async (searchData) => {
      let params = {
        financingStatus: ['NEW','EFFECT','CARRY_INTEREST'],
        ...searchData,
      }
      if(isClear){
        params = {
          ...searchData
        }
      }
      const data = await Api.postList(params)
      store.sumData = data.sum || {}
      if(data.records.list === 0){
        message.warn('当前暂无新建/生效/起息状态的融资产品.可通过筛选条件查询历史数据。')
      }
      return data.records
    },
  }, [isClear])
  store.$table = tableStore
  const { rows } = tableStore.getSelected()
  const approvalStatus = rows[0]?.approvalStatus || ''
  const FINANCING_STATUS = rows[0]?.financingStatus || ''
  // const FINANCING_PROCESS_STATUS = rows[0]?.approvalStatus || ''
  const inFlowStatus = ['NEW_UNDER_APPROVAL', 'CHANGING_UNDER_APPROVAL'].includes(approvalStatus)

  // 需融资状态=“生效”，且不在任何流程中
  const canEffectBtn = ['EFFECT'].includes(FINANCING_STATUS) && !inFlowStatus

  // 需融资状态=“起息”，且不在任何流程中
  const canChangeBtn = ['CARRY_INTEREST'].includes(FINANCING_STATUS) && !inFlowStatus

  const openModal = getQuery('openModal')
  useEffect(() => {
    openModal === 'true' && store.$createModal.open()
  }, [openModal])

  const formNameColumns = [
    {
      title: '业务类型',
      dataIndex: 'businessTypeList',
      matchOption: 'fundFinancingBizTypeEnum',
      search: {
        element: (
          <Select
            options={'fundFinancingBizTypeEnum'}
            allowClear
            getPopupContainer={() => document.body}
            mode="multiple"
          />
        ),
      },
    },
    {
      title: '融资编号',
      editable: InputEditable({ disabled: false }),
    },
    '融资机构',
    '融资金额',
    MatchOptionColumn({
      title: '融资状态',
      dataIndex: 'financingStatus',
      width: 150,
      matchOption: 'fundFinancingStatusEnum',
      search: {
        element: (
          <Select
            options={'fundFinancingStatusEnum'}
            allowClear
            getPopupContainer={() => document.body}
            mode={'multiple'}
            defaultValue={['NEW','EFFECT','CARRY_INTEREST']}
            onClear={()=>{
              setClear(true)
            }}
            onChange={vals => vals.length === 0 && setClear(true)}
          />
        ),
      },
    }),
    '资金经理',
    '起息日',
    '到期日',
    '质押资产',
  ]
  const formColumns = getSearchColumns(ALL_COLUMNS, formNameColumns)
  return (
    <Page>
      <Table
        columnsFilter="financialFund"
        onFilter={(key, val) => saveServer('columnsFilter', val)}
        store={tableStore}
        editable={false}
        selectable={{
          type: 'radio',
        }}
        onChange={(a,b)=>console.log(a,b)}
        onValuesChange={(a,b)=>console.log(a,b)}
        columnWidth={200}
        searchbar={{
          labelCol: { span: 6 },
          items: formColumns,
        }}
        extra={[<PageListDown key="1" module="fund" table={tableStore} />]}
        actions={[
          <Button.Add onClick={store.$createModal.open} key="add">
            新增融资
          </Button.Add>,
          <Button onClick={store.onEffect} key="effect" disabled={!canEffectBtn}>
            融资生效
          </Button>,
          <Button onClick={store.onChange} key="change" disabled={!canChangeBtn}>
            贷后变更
          </Button>,
        ]}
        scroll={{ x: 1000 }}
        columns={[
          ...columns,
          {
            title: '操作',
            fixed: 'right',
            width: 120,
            actions(record) {
              return [
                {
                  name: '作废',
                  onClick: () => store.delete(record),
                  disabled: !['EFFECT', 'NEW'].includes(record.financingStatus),
                },
                {
                  name: '删除',
                  onClick: () => store.remove(record),
                  disabled:
                    record.financingStatus !== 'CLOSE' ||
                    !['NEW_UN_SUBMIT'].includes(record.approvalStatus),
                },
              ].filter(Boolean)
            },
          },
        ]}
        summary={() => {
          return (
            <Summary
              columns={tableStore.getOptimizedColumns()}
              sumData={sumData}
              startIndex={0}
            ></Summary>
          )
        }}
      />
      <CreateModal store={store} />
      <ChangeModal store={store} />
    </Page>
  )
}

export default observer(Index)
