import { observer } from '@zswl/admin'
import store from './store'
import { Table, App, SearchBar } from '@zswl/components'
import ApprovalHistoryModal from '../../ApprovalHistoryModal/ProcessApprovalHistoryModal'
import ProcessTypeTree from '../../ProcessTypeTree/ProcessTypeTree'
import { ClientSelect } from '@/components/Select'
import { saveServer } from '@/utils'

import { useEffect, useState } from 'react'
import { PageListDownloadAction as PageListDown } from '@/components/Actions'
const { Item } = SearchBar

function ProcessReceiveApproval({ curTab }) {
  const [show, setShow] = useState(false)
  const [ids, setId] = useState('')
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  const approvalHistory = ({ processInstanceId }) => {
    setId(processInstanceId)
    setShow(true)
  }
  // render: (val, record) => {
  //   return <span style={{ color: 'red' }}>{val}</span>
  // },
  return (
    <>
      <Table
        columnsFilter="processReceiveApproval"
        onFilter={(key,val) => saveServer('processReceiveApproval',val)}
        resizable
        columnWidth={180}
        store={store.table}
        scroll={{
          x: 2000,
        }}
        extra={[<PageListDown key="1" module="receiveApproval" table={store.table} />]}
        searchbar={{
          labelCol: { span: 6 },
          limit: 6,
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
            <Item label="客户名称" name="clientId" key="clientId">
              <ClientSelect functionCode={'clientlist-flow'} canJump={false}></ClientSelect>
            </Item>,
          ],
        }}
        columns={[
          {
            title: '流程ID',
            dataIndex: 'processInstanceId',
            width: 120,
            fixed: 'left',
            actions(value) {
              return [
                {
                  name: value.processInstanceId,
                  to: `/process/receive/detail/${value.taskId}?typeId=approval&businessKey=${value.businessKey}&diff=taskId&curTab=${curTab}`,
                },
              ]
            },
          },
          {
            title: '流程状态',
            dataIndex: 'processStatus',
            width: 100,
            render: (v) => {
              return App.matchOption('processStatus', v).label
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
            width: 300,
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
            title: '当前节点',
            dataIndex: 'curTaskNames',
            width: 150,
          },
          {
            title: '当前审批人',
            dataIndex: 'curAssigneeNames',
          },
          {
            title: '发起人',
            dataIndex: 'startUserName',
            width: 160,
          },
          {
            title: '申请部门',
            dataIndex: 'startUserDeptName',
            width: 160,
          },
          {
            title: '申请时间',
            dataIndex: 'processStartTime',
          },
          {
            title: '处理时间',
            dataIndex: 'taskEndTime',
          },
          {
            title: '操作',
            width: 100,
            fixed: 'right',
            dataIndex: 'updateTime',
            isAction: true,
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

export default observer(ProcessReceiveApproval)
