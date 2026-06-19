import { observer } from '@zswl/admin'
import { getDescColumns } from '@/utils'
import ALL_COLUMNS from '../Column'
import { EditDescription } from '@/components/Table'

const labelStyle = {
  // color: 'red',
  background: '#F5F6FA',
}
const nameColumns = ['评分卡名称', '适用风控行业分类', '选择省内/省外', '状态', '适用年份', '说明']

const columns = getDescColumns(ALL_COLUMNS, nameColumns)
function Index({ store, detail }) {
  return (
    <EditDescription
      title={'评分卡模型基本信息'}
      bordered
      column={2}
      dataSource={detail}
      items={columns}
      className={'z-description'}
      saveData={store.saveData}
      labelStyle={labelStyle}
    />
  )
}

export default observer(Index)
