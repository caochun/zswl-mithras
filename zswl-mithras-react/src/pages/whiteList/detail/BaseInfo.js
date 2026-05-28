import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'

/**
 * 白名单详情页基本信息组件
 * 按照 ContractInfo.js 的写法模式重构
 * @param {Object} props - 组件属性
 * @param {Object} props.dataSource - 数据源
 * @param {boolean} props.canEdit - 是否可编辑
 * @param {string} props.id - 组件ID，用于锚点定位
 */
const nameColumns = [
  '评估机构名称',
  '社会统一信用代码',
  '成立日期',
  '营业许可证到期日',
  '营业许可证是否为长期',
  '关联项目名称',
  '业务范围',
]

/**
 * 基本信息组件
 * @param {Object} props - 组件属性
 * @param {Object} props.dataSource - 数据源
 * @param {boolean} props.canEdit - 是否可编辑
 * @param {string} props.id - 组件ID，用于锚点定位
 */
function BaseInfo({ dataSource, canEdit = false, id, store }) {
  const columns = getDescColumns(ALL_COLUMNS, nameColumns)

  return (
    <EditDescription
      detail={dataSource}
      canEdit={false}
      columns={columns}
      extra={[
        canEdit && (
          <Button type="primary" onClick={store.handleCommerceRefresh}>
            更新工商信息
          </Button>
        ),
      ]}
      title="基本信息"
    />
  )
}

export default observer(BaseInfo)
