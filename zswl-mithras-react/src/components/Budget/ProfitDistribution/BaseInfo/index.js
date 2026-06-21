
import { observer } from '@zswl/admin'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'

const BudgetProfitDistributionBaseInfo = ({ canEdit = false, editRef, store }) => {
  const nameColumns = [
    '合同编号',
    '剩余可用额度(元)',
    '项目名称',
    '项目编号',
    '业务类型',
    '租赁类型',
    '风控行业分类',
    '项目分类',
    '项目来源',
    '资金用途',
    '项目背景',
    '备注',
    '项目主办',
    '项目协办',
    '业务部门',
    '业务部门负责人',
    '业务分管领导',
  ]
  const baseInfo_columns = getDescColumns(ALL_COLUMNS(), nameColumns)

  return (
    <div>
      <EditDescription
        ref={editRef}
        title="基本信息"
        detail={store.page.getData()}
        canEdit={false}
        initEdit={false}
        columns={baseInfo_columns}
      />
    </div>
  )
}

export default observer(BudgetProfitDistributionBaseInfo)
