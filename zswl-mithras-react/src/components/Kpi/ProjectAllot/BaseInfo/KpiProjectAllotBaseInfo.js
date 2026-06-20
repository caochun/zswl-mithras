import { EditDescription } from '@/components/Table'
import { observer } from '@zswl/admin'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'

const Index = ({ canEdit, editRef, store }) => {
  const nameColumns = [
    '合同编号',
    '项目名称',
    {
      title: '项目类别',
      editable: false,
    },
    {
      title: '项目来源',
      editable: false,
    },
    '合同起始时间',
    '合同终止时间',
    {
      title: '业务部门',
      rename: '利润所属部门',
    },
    '团队长人员',
    '项目交接备注',
  ].filter(Boolean)
  const baseInfo_columns = getDescColumns(ALL_COLUMNS(), nameColumns)

  return (
    <div>
      <EditDescription
        ref={editRef}
        title="基本信息"
        saveData={store.saveData}
        detail={store.page.getData()}
        canEdit={canEdit}
        initEdit={false}
        columns={baseInfo_columns}
      />
    </div>
  )
}

export default observer(Index)
