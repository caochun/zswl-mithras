import { FounderSelect, OrgSelect } from '@/components/Select'
import { ClientSelect } from '@/components/Select'
import {
  ProcessApprovalHistoryModal as ApprovalHistoryModal,
  ProcessTypeTree,
} from '@/components/Process/ProcessEntries'
import { saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import { SearchBar, Table } from '@zswl/components'
import { Space, Tag } from 'antd'
import { useState } from 'react'
import styles from './index.less'
import store from './store'

const { Item } = SearchBar

function Index({ curTab, enterpriseName }) {
  const [show, setShow] = useState(false)
  const [ids, setId] = useState('')

  const approvalHistory = ({ processInstanceId }) => {
    setId(processInstanceId)
    setShow(true)
  }

  return (
    <>
      <Table
        scroll={{ x: 1500 }}
        rowKey={'taskId'}
        actions={[
          {
            name: '一键审批',
            type: 'primary',
            onClick: store.batchPass,
            access: 'flowexecutionbatchpass',
            fallback: null,
            disabled: store.table.getSelected().keys?.length === 0,
          },
        ]}
        selectable={{
          type: 'checkbox',
        }}
        columnsFilter="processReceivePending"
        onFilter={(key, val) => saveServer('processReceivePending', val)}
        columnWidth={180}
        resizable
        store={store.table}
        rowClassName={(record) => {
          return record.overtimeFlag == 1 ? styles.overtime : undefined
        }}
        searchbar={{
          limit: 6,
          labelCol: { span: 6 },
          items: [
            {
              label: '流程ID',
              name: 'processInstanceId',
            },
            {
              label: '表单名称',
              allowClear: true,
              name: 'processName',
            },
            <Item name="modelKeyList" label="流程类型" key="modelKeyList">
              <ProcessTypeTree></ProcessTypeTree>
            </Item>,
            {
              label: '项目名称',
              name: 'projName',
            },
            {
              label: '项目编号',
              name: 'projCode',
            },
            {
              label: '合同编号',
              name: 'contractCode',
            },
            <Item label="客户所属部门" name="belongDeptId" key="belongDeptId">
              <OrgSelect functionCode="selectorgs-3"></OrgSelect>
            </Item>,
            <Item label="发起人" name="startUserId" key="startUserId">
              <FounderSelect functionCode="selectfounder-3" params={{ job: undefined }}></FounderSelect>
            </Item>,
            <Item label="客户名称" name="clientId" key="clientId">
              <ClientSelect enterpriseName={enterpriseName} functionCode={'clientlist-flow'} canJump={false}></ClientSelect>
            </Item>,
          ],
        }}
        columns={[
          {
            title: '流程ID',
            dataIndex: 'processInstanceId',
            width: 160,
            fixed: 'left',
            tooltip: false,
            actions(value) {
              return [
                {
                  name: (
                    <Space>
                      {value.processInstanceId}
                      {value.overtimeFlag === 1 && <Tag color="red">已超时</Tag>}
                    </Space>
                  ),
                  to: `/process/receive/detail/${value.taskId}?typeId=approval&businessKey=${value.businessKey}&diff=taskId&curTab=${curTab}`,
                },
              ]
            },
          },
          {
            title: '流程类型',
            dataIndex: 'modelName',
            width: 200,
          },
          {
            title: '表单名称',
            dataIndex: 'processName',
            width: 350,
            render: (val, record) => {
              return <span style={{ color: record.overtimeFlag == 1 ? 'red' : undefined }}>{val}</span>
            },
          },

          {
            title: '项目名称',
            dataIndex: 'projName',
            width: 220,
          },
          {
            title: '项目编号',
            dataIndex: 'projCode',
            width: 140,
          },
          {
            title: '合同编号',
            dataIndex: 'contractCode',
            width: 280,
          },
          {
            title: '客户名称',
            dataIndex: 'clientName',
            width: 220,
            resizable: true,
            actions({ clientName, clientId }) {
              if (!clientName) return ''
              return [
                {
                  name: clientName || '-',
                  to: `/customer/maintain/detail/${clientId}?clientType=CORPORATION&flag=info&typeId=create`,
                  disabled: !clientName,
                  className: 'z-single-line',
                  // style: { width: 180 },
                },
              ]
            },
          },

          {
            title: '发起人',
            dataIndex: 'startUserName',
          },
          {
            title: '当前节点',
            dataIndex: 'curTaskNames',
          },
          {
            title: '当前审批人',
            dataIndex: 'curAssigneeNames',
          },
          {
            title: '客户所属部门',
            dataIndex: 'belongDeptName',
          },
          {
            title: '申请部门',
            dataIndex: 'startUserDeptName',
          },
          {
            title: '申请时间',
            dataIndex: 'processStartTime',
          },
          {
            title: '操作',
            width: 100,
            fixed: 'right',
            isAction: true,
            dataIndex: 'updateTime',
            actions(value) {
              return [
                {
                  name: '审批历史',
                  onClick: () => {
                    approvalHistory(value)
                  },
                },
              ]
            },
          },
        ]}
      />
      <ApprovalHistoryModal
        visible={show}
        processInstanceId={ids}
        callBack={() => {
          setShow(false)
        }}
      />
    </>
  )
}

export default observer(Index)
