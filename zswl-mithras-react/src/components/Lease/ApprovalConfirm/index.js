import { observer, history } from '@zswl/admin'
import { Button, Modal } from '@zswl/components'
import leaseApi from '@/api/lease/maintainApi'
import flowList from '@/api/process/flowTaskApi'
import vatInvoiceApi from '@/api/lease/vatInvoiceApi'

function Index({ onClick, params, isEffect, children, beforeClick, text, ...rest }) {
  const checkProcessStatus = async () => {
    const { relevanceFlowId } = await leaseApi.postCheckLeaseDetail(params)
    if (!relevanceFlowId) {
      await onClick?.()
      return
    }
    const { processStatus } = await flowList.getProcessDetail({
      processInstanceId: relevanceFlowId,
    })

    if (['3', '4', '7'].includes(processStatus)) {
      Modal.confirm({
        title: '提示',
        content: '租赁物所在项目评审状态为已取消/审批拒绝，是否确认继续租赁物审核流程',
        onOk: async () => {
          await onClick?.()
        },
      })
    } else {
      await onClick?.()
    }
  }
  const handleClick = async (e) => {
    e.stopPropagation()
    if (isEffect) {
      const res = await beforeClick?.()
      const isProj = params.taskActivityId === 'projManager'
      if (isProj) {
        const check = await vatInvoiceApi.postVatInvoiceAmountCheckout({
          leaseholdId: params.id,
        })
        if (['大于', '小于'].includes(check)) {
          Modal.confirm({
            title: '提示',
            content: `租赁物清单账面原值（元）合计值${check}ocr识别发票金额（元）合计值`,
            cancelText: '查看详情',
            onCancel: () => {
              history.push(`/ocr/list?id=${params.id}`)
            },
            onOk: async () => {
              await checkProcessStatus()
            },
          })
        } else {
          await checkProcessStatus()
        }
      } else {
        await checkProcessStatus()
      }
    } else {
      onClick?.()
    }
  }

  return (
    <>
      {children ? (
        <div onClick={handleClick}>{children}</div>
      ) : (
        <Button type={'primary'} onClick={handleClick} {...rest}>
          {text}
        </Button>
      )}
    </>
  )
}

export default observer(Index)
