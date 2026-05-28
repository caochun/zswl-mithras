import { Switch, Modal, Tooltip } from 'antd'
import { Table } from '@zswl/components'
import _ from 'lodash'
import { useState } from 'react'
import { saveServer } from '@/utils'

function AsyncModal({ tableStore, id, isEyeChange, visible, ...rest }) {
  const columns = [
    {
      title: '字段名称',
      dataIndex: 'name',
      editable: false,
      width: 100,
    },
    {
      title: '天眼查数据',
      dataIndex: 'data',
      editable: false,
      render: (val, record) => {
        return _.isFunction(record.itemRender) ? (
          record?.itemRender(val)
        ) : (
          <Tooltip title={val}>{val}</Tooltip>
        )
      },
      width: 200,
    },
    {
      title: '是否以天眼查为准',
      dataIndex: 'isEye',
      width: 80,
      fixed: 'right',

      editable: (val) => {
        return {
          element: (
            <Switch
              checkedChildren="是"
              unCheckedChildren="否"
              defaultChecked={false}
              width={80}
              onChange={() => isEyeChange(val)}
            />
          ),
          valuePropName: 'checked',
        }
      },
    },
  ]
  return (
    <Modal
      title={'相关信息同步'}
      open={visible}
      okText={'确定'}
      footer={null}
      width={800}
      {...rest}
    >
      <p style={{ fontSize: 16, fontWeight: 800 }}>基本信息</p>
      <Table         columnsFilter="detail_AsyncModal_1"
              onFilter={(key,val) => saveServer('detail_AsyncModal_1',val)} columns={columns} store={tableStore} scroll={false} editable></Table>
    </Modal>
  )
}

export default AsyncModal
