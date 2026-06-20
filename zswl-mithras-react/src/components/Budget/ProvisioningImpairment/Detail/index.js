import { observer } from '@zswl/admin'
import { Table, Page, Button } from '@zswl/components'
import { getFormColumns, getTableColumns, saveServer } from '@/utils'
import AddModal from '../../ProvisioningDataAddModal'
import ModalStore from '../../ProvisioningDataAddModal/store'
import ALL_COLUMNS from '../../ProvisioningImpairmentColumns/BudgetProvisioningImpairmentColumns'
import PageListDown from '@/components/PageListDown'
import Api from '@/api/budget/provisioning/provisioning'
import Store from './store'
import { useMemo } from 'react'

const formNameColumns = ['业务部门', '客户名称', '合同编号']
const nameColumns = [
  '业务类型',
  '项目类型',
  '业务部门',
  '客户名称',
  '合同编号',
  '借据编号',
  '到期日',
  '剩余期限(年)',
  '剩余本金',
  '保证金',
  '敞口',
  '风险等级',
  '本月风险金余额',
  '上月风险金余额',
  '本月风险金计提/转回',
  '应计利息',
  '下期租金',
  '备注',
]

const Index = ({ params: { id } }) => {
  const store = useMemo(() => new Store({ id }), [id])
  const modalStore = useMemo(() => new ModalStore({ afterSubmit: () => store.$table.search() }), [])
  const { editIndex } = store
  const columns = getTableColumns(ALL_COLUMNS({ editIndex }), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS({ type: 'searchForm' }), formNameColumns)

  return (
    <Page store={modalStore.page}>
      <Table
        columnsFilter="provisioning_detail_idjs"
        onFilter={(key, val) => saveServer('provisioning_detail_idjs', val)}
        extra={[
          <Button type="primary" onClick={() => modalStore.openAddModal({ provisionId: id })}>
            新增
          </Button>,
          {
            name: '确认',
            type: 'primary',
            onClick: store.confirm,
          },
          {
            name: '刷新',
            type: 'primary',
            onClick: store.refresh,
          },
          <PageListDown
            key="11"
            module="provisioning"
            table={store.$table}
            extraParams={{ id }}
            api={Api.exportDetail}
          />,
        ]}
        actions={[
          {
            name: <div>五级分类专项风险拨备计提表（截止到{store.provisionDate}） 货币单位：元</div>,
            style: {
              border: 0,
              cursor: 'default',
            },
          },
        ]}
        store={store.$table}
        columnWidth={160}
        searchbar={{
          items: formColumns,
        }}
        scroll={{
          x: 2000,
        }}
        columns={[
          ...columns,
          {
            title: '操作',
            width: 120,
            fixed: 'right',
            actions(record, rowIndex) {
              return [
                editIndex !== rowIndex && {
                  name: '编辑',
                  key: 'edit',
                  onClick: () => store.editItem({ rowIndex }),
                },
                editIndex === rowIndex && {
                  name: '取消',
                  key: 'cancel',
                  onClick: () => store.cancelEdit({ rowIndex }),
                },
                editIndex === rowIndex && {
                  name: '确定',
                  key: 'confirm',
                  onClick: () => store.confirmEdit({ record, rowIndex }),
                },
              ].filter(Boolean)
            },
          },
        ]}
      />
      <AddModal store={modalStore} />
    </Page>
  )
}
export default observer(Index)
