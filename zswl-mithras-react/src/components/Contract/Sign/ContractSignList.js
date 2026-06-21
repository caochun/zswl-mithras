import { Page, Table } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import Store from './Store'
import { useMemo } from 'react'
import { Tabs } from 'antd'
import { getTableColumns, saveServer } from '@/utils'
import { ALL_COLUMNS } from './Column'

const ToBeSignTable = ({ store, pathname }) => {
  const nameColumns = ['合同编号', '项目名称', '客户名称', '项目主办', '签约方式', '推送时间']
  const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)
  return (
    <Table
      columnsFilter={'contract_sign_1'}
      onFilter={(key, val) => saveServer('contract_sign_1', val)}
      scroll={{ x: true }}
      store={store.unSignedTable}
      columns={[
        ...columns,
        {
          title: '操作',
          actions: ({ id, contractId }) => {
            return [
              {
                name: '合同文本明细',
                onClick: () => {
                  history.push(
                    `${pathname}/detail/${id}?contractId=${contractId}&contractSignStatus=TO_BE_SIGN`
                  )
                },
              },
              {
                name: '打包下载',
                onClick: async () => store.downloadFile(contractId),
              },
            ]
          },
        },
      ]}
    ></Table>
  )
}

const SignedTable = ({ store, pathname }) => {
  const nameColumns = ['合同编号', '项目名称', '客户名称', '项目主办', '签约方式', '推送时间']
  const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)
  return (
    <Table
      columnsFilter={'contract_sign_2'}
      onFilter={(key, val) => saveServer('contract_sign_2', val)}
      scroll={{ x: true }}
      store={store.signedTable}
      columns={[
        ...columns,
        {
          title: '操作',
          actions: ({ id, contractId }) => {
            return [
              {
                name: '合同签约详情',
                onClick: () => {
                  history.push(
                    `${pathname}/detail/${id}?contractId=${contractId}&contractSignStatus=SIGNED`
                  )
                },
              },
            ]
          },
        },
      ]}
    ></Table>
  )
}

const ContractSignList = ({ pathname }) => {
  const store = useMemo(() => new Store(), [])

  return (
    <Page>
      <Tabs
        items={[
          {
            label: '待签约',
            key: 'TO_BE_SIGN',
            children: <ToBeSignTable store={store} pathname={pathname}></ToBeSignTable>,
          },
          {
            label: '已签约',
            key: 'SIGNED',
            children: <SignedTable store={store} pathname={pathname}></SignedTable>,
          },
        ]}
      ></Tabs>
    </Page>
  )
}

export default observer(ContractSignList)
