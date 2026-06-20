import React, { useState } from 'react'
import { observer } from '@zswl/admin'
import { Button, Popover, List, Badge, Progress, Space, Typography } from 'antd'
import {
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons'
import store from './store'
import './index.less'

const { Text } = Typography

/**
 * 悬浮任务按钮组件
 * 用于展示和管理长时任务的状态
 */
const TaskFloat = observer(() => {
  const [visible, setVisible] = useState(false)
  const { taskList, runningTasksCount } = store

  /**
   * 获取任务状态图标
   * @param {string} status - 任务状态
   * @returns {JSX.Element} 状态图标
   */
  const getStatusIcon = (status) => {
    switch (status) {
      case 'running':
        return <ClockCircleOutlined style={{ color: '#1890ff' }} />
      case 'completed':
        return <CheckCircleOutlined style={{ color: '#52c41a' }} />
      case 'failed':
        return <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} />
      default:
        return <ClockCircleOutlined style={{ color: '#d9d9d9' }} />
    }
  }

  /**
   * 获取任务状态文本
   * @param {string} status - 任务状态
   * @returns {string} 状态文本
   */
  const getStatusText = (status) => {
    switch (status) {
      case 'running':
        return '进行中'
      case 'completed':
        return '已完成'
      case 'failed':
        return '失败'
      default:
        return '等待中'
    }
  }

  /**
   * 渲染任务列表项
   * @param {Object} task - 任务对象
   * @returns {JSX.Element} 任务列表项
   */
  const renderTaskItem = (task) => (
    <List.Item key={task.id}>
      <Space direction="vertical" style={{ width: '100%' }}>
        <Space>
          {getStatusIcon(task.status)}
          <Text strong>{task.name}</Text>
          <Text type="secondary">{getStatusText(task.status)}</Text>
        </Space>
        {task.description && (
          <Text type="secondary" style={{ fontSize: '12px' }}>
            {task.description}
          </Text>
        )}

        {task.createTime && (
          <Text type="secondary" style={{ fontSize: '11px' }}>
            创建时间: {task.createTime}
          </Text>
        )}
      </Space>
    </List.Item>
  )

  /**
   * 任务列表内容
   */
  const taskContent = (
    <div className="task-float-content">
      <div className="task-float-header">
        <Text strong>长时任务</Text>
        <Text type="secondary">({taskList.length})</Text>
      </div>
      {taskList.length > 0 ? (
        <List
          size="small"
          dataSource={taskList}
          renderItem={renderTaskItem}
          style={{ maxHeight: '400px', overflowY: 'auto' }}
        />
      ) : (
        <div className="task-float-empty">
          <Text type="secondary">暂无任务</Text>
        </div>
      )}
    </div>
  )

  return (
    <div className="task-float-wrapper">
      <Popover
        content={taskContent}
        title={null}
        trigger="click"
        placement="topRight"
        visible={visible}
        onVisibleChange={setVisible}
        overlayClassName="task-float-popover"
      >
        <Badge count={runningTasksCount} size="small">
          <ClockCircleOutlined className={'ocr'} />
          {/* <Button
            type="primary"
            shape="circle"
            icon={}
            size="large"
            className="task-float-button"
          /> */}
        </Badge>
      </Popover>
    </div>
  )
})

export default TaskFloat
