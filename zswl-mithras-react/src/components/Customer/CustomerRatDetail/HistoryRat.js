import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/pages/customer/customerRat/Columns'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

const historyColumns = getDescColumns(ALL_COLUMNS, [
  '历史评级模型名称',
  '历史评级结果',
  '历史认定结果',
  '历史评级生效日期',
  '历史评级失效日期',
  '历史评级模型编号',
  '历史评级发起人',
])
function Index({ detail }) {
  return (
    <>
      <EditDescription
        title="历史评级信息"
        columns={historyColumns}
        canEdit={false}
        detail={detail}
      />
    </>
  )
}

export default observer(Index)
