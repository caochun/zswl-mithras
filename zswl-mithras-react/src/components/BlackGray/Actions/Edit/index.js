import { observer } from '@zswl/admin'
import { App, Button } from '@zswl/components'

/**
 * 编辑按钮
 * @param store 表格的store,必传
 * @param rest 剩余属性
 * @param  fieldNames
 * 如果不勾选，可以点击进入批量审批，
 * 审批状态均为待提交/已驳回/已撤回数据 并且当前处理人是当前登录用户才能编辑
 */
function Index({ store, fieldNames, ...rest }) {
  const { rows } = store.getSelected()
  const can = canEdit({ rows, fieldNames })
  return (
    <Button.Edit type={'primary'} disabled={!can} {...rest}>
      编辑
    </Button.Edit>
  )
}
function canEdit({ rows, fieldNames, ...info }) {
  const filed = {
    status: ['approvalStatus', 'auditStatus', 'deliveryStatus', 'status', 'approveStatus'], // 审批状态,依次寻找字段
    current: ['currentOperator', 'currentProcessor', 'current', 'curUser', 'curAudit'], // 当前处理人
    ...fieldNames,
  }
  const { account } = App.getData().user
  let statusName = filed.status
  let currentName = filed.current
  if (!rows) {
    if (Array.isArray(statusName)) {
      statusName = statusName.find((name) => info[name] !== undefined)
    }
    if (Array.isArray(currentName)) {
      currentName = currentName.find((name) => info[name] !== undefined)
    }
    return [0, 2, 3].includes(info[statusName]) && info[currentName] === account
  }
  if (rows.length > 0) {
    if (Array.isArray(statusName)) {
      statusName = statusName.find((name) => rows[0][name] !== undefined)
    }
    if (Array.isArray(currentName)) {
      currentName = currentName.find((name) => rows[0][name] !== undefined)
    }
  }
  if (!statusName || !currentName) {
    return false
  }
  return (
    rows.length === 1 &&
    rows.every((item) => {
      return [0, 2, 3].includes(item[statusName]) && item[currentName] === account
    })
  )
}
const Edit = observer(Index)

Edit.useBoolean = (data) => {
  if (!data) {
    return false
  }
  const { rows, fieldNames = {}, ...info } = data
  if (rows) {
    return canEdit({ rows, fieldNames })
  }
  return canEdit({ fieldNames, ...info })
}
export default Edit
