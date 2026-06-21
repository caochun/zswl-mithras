import { observer } from '@zswl/admin'
import Api from '@/api/common/actionApi'
import { Button } from '@zswl/components'
import { message, Modal } from 'antd'

/**
 * 删除按钮
 * @param store 表格的store,必传
 * @param rest 剩余属性
 * @param  fieldNames
 * @param api 接口,当是字符串时表示接口地址，函数时就会直接调用
 * 如果不勾选，可以点击进入批量审批，
 * 审批状态均为待提交/已驳回/已撤回数据才能删除
 */
function BlackGrayDeleteAction({ store, fieldNames, api, ...rest }) {
  const { rows, keys } = store.getSelected()
  const can = canDelete({ rows, fieldNames })
  const handleClick = () => {
    Modal.confirm({
      title: '确定删除吗？',
      onOk: async () => {
        if (typeof api === 'function') {
          await api({ ids: keys })
        } else if (typeof api === 'string') {
          await Api.postDelete(api, { ids: keys })
        }
        message.success('删除成功')
        store.search()
      },
    })
  }
  return (
    <Button.Delete danger disabled={!can} onClick={handleClick} {...rest}>
      删除
    </Button.Delete>
  )
}
function canDelete({ rows, fieldNames, ...info }) {
  const filed = {
    status: ['approvalStatus', 'auditStatus', 'approveStatus', 'status'], // 审批状态,依次寻找字段
    ...fieldNames,
  }
  let statusName = filed.status
  if (!rows) {
    if (Array.isArray(statusName)) {
      statusName = statusName.find((name) => info[name] !== undefined)
    }
    return [0, 2, 3].includes(info[statusName])
  }
  if (rows.length > 0) {
    if (Array.isArray(statusName)) {
      statusName = statusName.find((name) => rows[0][name] !== undefined)
    }
  }
  if (!statusName) {
    return false
  }
  return (
    rows.length > 0 &&
    rows.every((item) => {
      return [0, 2, 3].includes(item[statusName])
    })
  )
}
const Delete = observer(BlackGrayDeleteAction)

Delete.useBoolean = (data) => {
  if (!data) {
    return false
  }
  const { rows, fieldNames = {}, ...info } = data
  if (rows) {
    return canDelete({ rows, fieldNames })
  }
  return canDelete({ fieldNames, ...info })
}
export default Delete
