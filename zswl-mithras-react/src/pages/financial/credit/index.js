import { useEffect } from 'react'
import { getQuery, observer } from '@zswl/admin'
import { Button, Page, Table } from '@zswl/components'
import CreditModal from './CreditModal'
import { getTableColumns, getFormColumns } from '@/utils'
import { PageListDown } from '@/components'
import { CreditOrgSelect } from '@/components/Financial/SelectEntries'
import ALL_COLUMNS from './Column'
import { PureAmountFormat } from '@/components/Format'
import store from './store'
import { saveServer } from '@/utils'

const { Summary } = Table
const { Row, Cell } = Summary

const RenderSum = (value) => {
  return <div style={{ textAlign: 'right' }}>{PureAmountFormat(value)} </div>
}

const nameColumns = [
  {
    title: '授信编号',
    actions: ({ creditCode: name, id }) => [{ name, to: `/financial/credit/detail/${id}` }],
    access: 'fundcreditdetail',
  },
  { title: '授信机构', dataIndex: 'organizationName' },
  '额度是否可循环',
  { title: '生效状态' },
  {
    title: '授信总额',
    children: [
      {
        title: '授信总额-总额度(元)',
        rename: '总额度(元)',
      },
      {
        title: '授信总额-担保额度(元)',
        rename: '担保额度(元)',
      },
      {
        title: '授信总额-信用额度(元)',
        rename: '信用额度(元)',
      },
    ],
  },
  {
    title: '已使用额度',
    children: [
      {
        title: '已使用额度-总额度(元)',
        rename: '总额度(元)',
      },
      {
        title: '已使用额度-担保额度(元)',
        rename: '担保额度(元)',
      },
      {
        title: '已使用额度-信用额度(元)',
        rename: '信用额度(元)',
      },
    ],
  },
  {
    title: '剩余授信额度',
    children: [
      {
        title: '剩余授信额度-总额度(元)',
        rename: '总额度(元)',
      },
      {
        title: '剩余授信额度-担保额度(元)',
        rename: '担保额度(元)',
      },
      {
        title: '剩余授信额度-信用额度(元)',
        rename: '信用额度(元)',
      },
    ],
  },
  '剩余本金（元）',
  '管理人',
  '授信到期日',
]
const formNameColumns = [
  {
    title: '授信机构',
    editable: {
      element: <CreditOrgSelect />,
    },
  },
  '生效状态',
  '授信金额',
  '授信日期',
  '机构类型',
  { title: '管理人', rename: '创建人' },
  '创建日期',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function Index() {
  const { rows, keys } = store.table.getSelected()
  const canDelete = keys.length > 0
  const openModal = getQuery('openModal')
  useEffect(() => {
    openModal === 'true' && store.createModal.open()
  }, [openModal])

  const { sumData } = store
  const canInvalid = keys.length === 1 && rows.every((v) => v.effective)
  return (
    <Page>
      <div>
        <Table
          columnsFilter={'financial_credit_1'}
          onFilter={(key,val) => saveServer('financial_credit_1',val)}
          store={store.table}
          selectable
          editable={false}
          columnWidth={180}
          searchbar={{
            labelCol: { span: 6 },
            items: formColumns,
            initialValues: {
              effective: true,
            },
          }}
          actions={[
            <Button.Add onClick={store.createModal.open} key="add" access={'fundcreditadd'}>
              新增授信
            </Button.Add>,
            <Button.Delete
              onClick={store.delete}
              key="delete"
              disabled={!canDelete}
              access="fundcreditremove"
            >
              删除授信
            </Button.Delete>,
            <Button onClick={store.invalid} disabled={!canInvalid} confirm>
              失效授信
            </Button>,
          ]}
          extra={[<PageListDown key="1" module="credit" table={store.table} />]}
          scroll={{
            x: 2200,
          }}
          columns={columns}
          summary={() => {
            return (
              <Summary fixed>
                <Row>
                  <Cell colSpan={1}> </Cell>
                  <Cell colSpan={2}>合计值</Cell>
                  <Cell colSpan={2}> </Cell>
                  <Cell colSpan={1}>{RenderSum(sumData.totalCreditLimit)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.guaranteeAmount)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.creditLimit)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.usedTotalCreditAmount)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.usedGuaranteeAmount)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.usedCreditAmount)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.remainingLimit)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.remainingGuaranteeAmount)}</Cell>
                  <Cell colSpan={1}> {RenderSum(sumData.remainingCreditAmount)}</Cell>
                </Row>
              </Summary>
            )
          }}
        />
        <CreditModal />
      </div>
    </Page>
  )
}

export default observer(Index)
