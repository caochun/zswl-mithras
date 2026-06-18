import { observer } from '@zswl/admin'
import { App, Button } from '@zswl/components'

/**
 * 表格中审批操作，渲染一个审批按钮
 * @param store 表格的store,必传
 * @param rest 剩余属性
 * @param single 是否单条审批
 * @param  fieldNames
 * 如果不勾选，可以点击进入批量审批，
 * 勾选之后判断如果是审批中且当前处理人为当前登录用户才能审批
 */
function Index({ store, fieldNames, single, ...rest }) {
  const { rows } = store.getSelected()
  const can = canAudit({ rows, fieldNames, single })
  return (
    <Button.Edit type={'primary'} disabled={!can} {...rest}>
      审批
    </Button.Edit>
  )
}
function canAudit({ rows, fieldNames, single, ...info }) {
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
    return info[statusName] === 1 && info[currentName] === account
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
  if (single && rows.length !== 1) {
    return false
  }
  return (
    rows.length === 0 ||
    rows.every((item) => {
      return item[statusName] === 1 && item[currentName] === account
    })
  )
}
const Audit = observer(Index)

Audit.useBoolean = (data) => {
  if (!data) {
    return false
  }
  const { rows, fieldNames = {}, ...info } = data
  if (rows) {
    return canAudit({ rows, fieldNames })
  }
  return canAudit({ fieldNames, ...info })
}
export default Audit
