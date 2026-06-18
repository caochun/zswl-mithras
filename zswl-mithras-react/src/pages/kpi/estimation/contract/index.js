import { observer, history } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns } from '@/utils'
import {
  CalculateDateItem,
  DepartMentalItem,
  ContractCodeItem,
  ProjNameItem,
} from '@/components/Kpi/KpiEstimationEntries'
import moduleColumns from './Column'
import EstmationModal from './EstmationModal'
import moment from 'moment'
import store from './store'
import { saveServer } from '@/utils'

const Index = ({ pathname }) => {
  const nameColumns = [
    {
      title: '合同编号',
      dataIndex: 'contractCode',
      fixed: 'left',
      width: 260,
      actions({ contractId, contractCode }) {
        return [
          {
            name: contractCode,
            onClick: () => history.push(`${pathname}/contract/detail/${contractId}`),
          },
        ]
      },
    },
    {
      title: '月份',
      rename: '核算月份',
      render: (val, { calculateDateYear, calculateDateMonth }) =>
        moment(`${calculateDateYear}-${calculateDateMonth}`).format('yyyy年MM月'),
    },
    '借据编号',
    '考核部门',
    '项目名称',
    '项目利润-当期值(元)',
    '项目利润-累计值(元)',
    '合同利润奖金-当期值(元)',
    '合同利润奖金-调整值(元)',
    '合同利润奖金-累计值(元)',
    '合同投放奖金-当期值(元)',
    '合同投放奖金-累计值(元)',
  ]

  const columns = getTableColumns(moduleColumns(), nameColumns)

  return (
    <>
      <Table
        columnsFilter="项目绩效测算表_合同维度"
        onFilter={(key,val) => saveServer('项目绩效测算表_合同维度',val)}
        actions={[
          {
            name: '绩效测算',
            type: 'primary',
            onClick: store.calculationModal.open,
          },
          {
            name: '导出',
            type: 'primary',
            onClick: () => store.export({ columns }),
          },
        ]}
        store={store.table}
        columnWidth={190}
        editable={false}
        searchbar={{
          items: [
            <ContractCodeItem></ContractCodeItem>,
            <DepartMentalItem></DepartMentalItem>,
            <CalculateDateItem></CalculateDateItem>,
            <ProjNameItem></ProjNameItem>,
          ],
        }}
        scroll={{
          x: 1600,
        }}
        columns={columns}
      />
      <EstmationModal store={store}></EstmationModal>
    </>
  )
}
export default observer(Index)
