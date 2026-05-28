import { Button } from '@zswl/components'
import { Modal } from 'antd'
import { history, http } from '@zswl/admin'
import { forwardRef, useRef, useState, useImperativeHandle } from 'react'
import Next from './Next'
import Last from './Last'

/**
 * @param params：
 *   taskIds: 审批任务id必须，如果是批量,传数组
 *   modelKey: 业务模块标识
 *   注意params也可以是一个函数，用来动态获取参数，返回参数同上
 * @param form： 其他表单，传了就会先收集其他表单信息
 * @param onSubmit：最后提交时的回调函数，如果不传就回自动调用通用的审批接口
 * @param onSuccess：最后提交成功之后的回调函数
 * @param validator: 点击按钮处理前的验证，返回false将阻止后续的动作
 * 三个情况下弹框区分
 * 1、nextNodeSelect为true，且有lastNodeSelectIds 选择弹窗二
 *
 * --弹窗二两种分支 另一接口返回值包括（nextNodeSelect，nextActivityId，lastNodeAuditAccounts）
 * --a、当选择的节点下一节点为结束节点（nextNodeSelect为false），则不需要选择下一审批人
 * --b、否则需要下一审批人，下一审批人根据 下一节点id从nodeInfo中选择
 *
 * --显示最终审批人：xxx 判断 lastNodeAuditAccounts是否有值,有值话就下方显示：最终最终审批人：xx
 *
 * 2、nextNodeSelect为true，且没有lastNodeSelectIds 选择弹窗一
 * --弹窗一下拉列表选择，根据nextActivityId选择nodeInfo（类型为map）中selectUsers
 *
 * 3、nextNodeSelect为false，或为驳回情况。选择弹窗三
 */
function SubmitAudit(
  {
    form: suggestForm,
    params,
    onSubmit,
    goBack = true,
    onSuccess,
    children,
    access,
    validator,
    icon,
    ...rest
  },
  ref
) {
  const getParams = () => {
    const resParams = typeof params === 'function' ? params() : params
    const { taskIds, modelKey } = resParams || {}
    return {
      modelKey,
      taskIds: (Array.isArray(taskIds) ? taskIds : [taskIds]).filter(
        (val) => val !== null && val !== undefined
      ),
    }
  }
  const [visible, setVisible] = useState(0)
  const [info, setInfo] = useState()
  const saveRef = useRef({})
  const closeModal = () => {
    setVisible(0)
  }
  const [loading, setLoading] = useState(false)
  const onFinish = async (values) => {
    const resValues = { ...values, ...saveRef.current }
    setLoading(true)
    try {
      if (onSubmit) {
        await onSubmit(resValues)
      } else {
        const resParams = getParams()
        await http.post('/audit/common/exec/audit', {
          ...resValues,
          taskIds: resParams.taskIds,
        })
        if (goBack) {
          history.goBack()
        }
      }
      setLoading(false)
      closeModal()

      if (onSuccess) {
        onSuccess()
      }
    } catch (error) {
      setLoading(false)
    }
  }
  const handleClick = async () => {
    if (validator) {
      const res = await validator()
      if (res === false) {
        return
      }
    }
    if (suggestForm) {
      saveRef.current = (await suggestForm.submit()) || {}
    }
    const { audit } = saveRef.current
    if (audit === 2) {
      // 驳回弹出Confirm弹框
      Modal.confirm({
        title: '是否确定驳回？',
        content: '驳回后无法撤回',
        okButtonProps: { loading },
        onOk() {
          onFinish()
        },
      })
    } else {
      // nextNodeSelect为true，且有lastNodeSelectIds
      const resParams = getParams()
      const res = await http.get('/audit/common/select/auditUsers', {
        params: resParams,
      })
      setInfo(res)
      const { nextNodeSelect, lastNodeSelectIds } = res || {}
      if (nextNodeSelect) {
        if (lastNodeSelectIds?.length) {
          // 弹出Last弹框
          setVisible(2)
        } else {
          // 弹出Next弹框
          setVisible(1)
        }
      } else {
        Modal.confirm({
          title: '是否提交审批？',
          content: '提交后无法撤回',
          okButtonProps: { loading },
          onOk() {
            onFinish()
          },
        })
      }
    }
  }
  useImperativeHandle(ref, () => {
    return {
      click(extra) {
        if (extra) {
          saveRef.current = extra
        }
        handleClick()
      },
    }
  })
  return (
    <>
      <Button.Submit icon={icon} type={'primary'} access={access} {...rest} onClick={handleClick}>
        {children || '提交'}
      </Button.Submit>
      <Next
        visible={visible}
        info={info}
        onCancel={closeModal}
        onFinish={onFinish}
        loading={loading}
      />
      <Last
        visible={visible}
        getParams={getParams}
        info={info}
        onCancel={closeModal}
        onFinish={onFinish}
        loading={loading}
      />
    </>
  )
}

export default forwardRef(SubmitAudit)
