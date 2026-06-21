import { Table, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import { COMMON_COLUMNS } from '../../../CreditTableColumns'
import { getFormColumns } from '@/utils'
import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import statusRender from '../../../CreditTableStatusRender'
import { saveServer } from '@/utils'

const formColumns = getFormColumns(COMMON_COLUMNS, ['编号', '客户名称'])

function CreditTableFinishAccount({ store }) {
  const columns = [
    InputColumn({
      title: '编号',
      dataIndex: 'paymentApplyCode',
      fixed: 'left',
      render: statusRender,
    }),

    InputColumn({ title: '客户名称', width: 260, dataIndex: 'clientName' }),
    MatchOptionColumn({
      title: '业务类型',
      dataIndex: 'bizType',
      matchOption: 'crAccountBizType',
    }),
    MatchOptionColumn({
      title: '租金计算方式',
      width: 200,
      dataIndex: 'rentalCalcType',
      matchOption: 'crRepayCalcType',
    }),
    MatchOptionColumn({
      title: '还款频率',
      dataIndex: 'repayRate',
      matchOption: 'crAccountRepayRate',
    }),
    AmountColumn({ title: '借款金额(元)', dataIndex: 'paymentAmount' }),
    AmountColumn({ title: '保证金(元)', dataIndex: 'earnestMoney' }),
    InputColumn({ title: '借款期限(月)', dataIndex: 'projLeaseMonthCount' }),
    DateColumn({ title: '放款日期', dataIndex: 'lendingDate' }),
    DateColumn({ title: '结清日期', width: 160, dataIndex: 'closedDate' }),
    DateColumn({ title: '到期日期', width: 160, dataIndex: 'expirationDate' }),
  ]
  return (
    <div>
      <Table
        columnsFilter={'View_Account_1'}
        onFilter={(key, val) => saveServer('View_Account_1', val)}
        resizable
        columnWidth={180}
        onRow={(record) => {
          return {
            onClick: () => store.onRowClick({ record }),
          }
        }}
        editable={false}
        store={store.$accountTable}
        searchbar={{
          labelCol: { span: 6 },
          items: formColumns,
        }}
        columns={columns}
      />
    </div>
  )
}

export default observer(CreditTableFinishAccount)
