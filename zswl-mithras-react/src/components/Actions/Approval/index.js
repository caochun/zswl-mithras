import { observer, http } from '@zswl/admin'
import { Button, Form, Modal, ModalStore } from '@zswl/components'
import { Input, message } from 'antd'
import { useMemo, forwardRef, useImperativeHandle, useState } from 'react'

/**
 * 表格中导出操作，渲染一个导出按钮
 * @param store 表格的store，必传
 * @param api 导出的api
 * @param fileName 导出的文件名
 */
function ApprovalAction({ onClick, params, isEffect, children, beforeClick, text, ...rest }, ref) {
  const [msg, setMsg] = useState('')
  const isReconsider = params?.remarkType === 'RECONSIDER'
  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: async () => {
          return {}
          // const { remarkJson, id } = await processModifyRemarkApi.postRemarkDetail(params)
          // return { ...remarkJson, id }
        },
        onFinish: async ({ id, ...remarkJson }) => {
          await onClick?.({
            remarkAddREQ: {
              ...params,
              remarkJson,
            },
          })
          modal.close()
        },
      }),
    []
  )
  const handleClick = async (e) => {
    e.stopPropagation()
    if (isEffect) {
      const res = await beforeClick?.()
      setMsg(res)
      modal.open()
    } else {
      await onClick?.()
    }
  }
  useImperativeHandle(ref, () => ({
    modal,
  }))

  return (
    <>
      {children ? (
        <div onClick={handleClick}>{children}</div>
      ) : (
        <Button type={'primary'} onClick={handleClick} {...rest}>
          {text || (isReconsider ? '复议' : '提交审批')}
        </Button>
      )}
      <Modal store={modal} title={isReconsider ? '复议' : '提交审批'} destroyOnClose>
        <div style={{display:msg ? 'block' : 'none', color:'red',marginBottom:7,paddingLeft:32}}>{msg}，请填写说明原因</div>
        <Form labelCol={{ span: 6 }}>
          <Form.Item name="id" hidden>
            <Input />
          </Form.Item>
          <Form.Item
            label={isReconsider ? '复议内容' : '变更原因'}
            name="reason"
            rules={[{ required: true, message: `请输入${isReconsider ? '复议内容' : '变更原因'}` }]}
          >
            <Input.TextArea />
          </Form.Item>
          <Form.Item
            label={isReconsider ? '原内容' : '变更原内容'}
            name="originalContent"
            rules={[{ required: true, message: `请输入${isReconsider ? '原内容' : '变更原内容'}` }]}
          >
            <Input.TextArea />
          </Form.Item>
          <Form.Item
            label={isReconsider ? '调整后内容' : '变更后内容'}
            name="toBeContent"
            rules={[
              { required: true, message: `请输入${isReconsider ? '调整后内容' : '变更后内容'}` },
            ]}
          >
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default forwardRef(ApprovalAction)
