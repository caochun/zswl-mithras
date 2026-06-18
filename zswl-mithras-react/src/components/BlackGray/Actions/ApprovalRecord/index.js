import ApprovalHistory from './History'
import { Button } from '@zswl/components'
import { useState } from 'react'
import { Drawer } from 'antd'
import { ContainerOutlined } from '@ant-design/icons'

/**
 * 查看审批记录的按钮操作
 * 渲染一个 查看审批记录 的按钮
 * 点击按钮回弹出审批历史的抽屉
 * @param request 获取审批历史
 * @param params 相关参数，bizId，bizCode，taskId
 * @param children 按钮children
 * @param hidden 是否隐藏
 */
export default function ApprovalRecord({ request, params, children, hidden, ...rest }) {
  const [visible, setVisible] = useState(false)
  if (hidden) {
    return null
  }
  return (
    <>
      <span onClick={() => setVisible(true)}>
        {children || (
          <Button icon={<ContainerOutlined />} type={'primary'} {...rest}>
            查看审批记录
          </Button>
        )}
      </span>

      <Drawer
        closable={false}
        bodyStyle={{ padding: 0 }}
        width={550}
        open={visible}
        extra={null}
        onClose={() => setVisible(false)}
      >
        <ApprovalHistory visible={visible} request={request} params={params} />
      </Drawer>
    </>
  )
}
