import { observer } from '@zswl/admin'
import { App, Button } from '@zswl/components'
import { message, Modal } from 'antd'
import commonAuditActionApi from '@/api/blackGray/commonAuditActionApi'

/**
 * 撤回操作，渲染一个撤回按钮
 * @param store 如果是表格中的撤回操作，那么就需要传表格的store，如果不是则可不传
 * @param taskIds 当不传store时，需要传递taskIds，这种情况一般是撤回按钮不在表格中
 * @param fieldNames 指定审核状态，等字段映射,只有store存在时有效
 * @param needPre 是否需要判断当前处理人为当前用户的下一位审批岗的才能撤回
 */
function BlackGrayWithdrawAction({ store, taskIds, fieldNames, needPre = true, access }) {
  const filed = {
    approvalStatus: 'approvalStatus',
    preOperator: 'preOperator',
    ...fieldNames,
  }
  const { account } = App.useData().user
  const { rows = [], keys = [] } = store ? store.getSelected() : {}
  // 审批中且当前处理人为当前用户的下一位审批岗的才能撤回
  const canWithdraw = store
    ? rows.length >= 1 &&
      rows.every(
        (item) =>
          item[filed.approvalStatus] === 1 && (needPre ? item[filed.preOperator] === account : true)
      )
    : true
  const handleClick = () => {
    Modal.confirm({
      title: '确定撤回吗？',
      onOk: async () => {
        let ids = taskIds
        if (!ids) {
          // 判断其中是否有taskId，有就用taskId
          ids = rows.some((item) => item.taskId) ? rows.map((item) => item.taskId) : keys
        }
        await commonAuditActionApi.postWithdraw({ taskIds: ids })
        message.success('已成功撤回')
        if (store) {
          store.search()
        }
      },
    })
  }
  return (
    <Button.Withdraw
      type={'primary'}
      disabled={!canWithdraw}
      onClick={handleClick}
      access={access}
    />
  )
}

export default observer(BlackGrayWithdrawAction)
