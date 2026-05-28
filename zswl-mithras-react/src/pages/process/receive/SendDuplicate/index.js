import { observer, history } from '@zswl/admin'
import store from './store'
import { Table, App, SearchBar } from '@zswl/components'
import ApprovalHistoryModal from '../../components/ApprovalHistoryModal'
import { Tag } from 'antd'
import { ClientSelect } from '@/components/Select'
import { useEffect, useState } from 'react'
import ProcessTypeTree from '@/pages/process/components/ProcessTypeTree'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function Index({ curTab }) {
  const [show, setShow] = useState(false)
  const [ids, setId] = useState('')
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  const approvalHistory = ({ processInstanceId }) => {
    console.log(3333)
    setId(processInstanceId)
    setShow(true)
  }
  return (
    <>
      <Table
        columnsFilter="processReceiveSendDuplicate"
        onFilter={(key,val) => saveServer('processReceiveSendDuplicate',val)}
        columnWidth={180}
        resizable
        scroll={{ x: 1500 }}
        store={store.table}
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
            {
              label: '读取状态',
              name: 'readFlag',
              options: [
                { label: '已读', value: '1' },
                { label: '未读', value: '0' },
              ],
              allowClear: true,
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
            width: 160,
            fixed: 'left',
            actions(value) {
              return [
                {
                  name: (
                    <div>
                      {value.processInstanceId}
                      <span style={{ paddingLeft: '6px' }}>
                        {value.readFlag == 0 && <Tag color="red">未读</Tag>}
                      </span>
                    </div>
                  ),
                  to: `/process/receive/detail/${value.processInstanceId}?typeId=approval&businessKey=${value.businessKey}&diff=processInstanceId&curTab=${curTab}`,
                },
              ]
            },
          },
          {
            title: '审批状态',
            dataIndex: 'processStatus',
            width: 100,
            render: (v) => {
              return App.matchOption('processStatus', v).label
            },
          },
          {
            title: '流程类型',
            dataIndex: 'modelName',
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
            title: '发起人',
            dataIndex: 'startUserName',
          },
          {
            title: '申请部门',
            dataIndex: 'startUserDeptName',
          },
          {
            title: '申请时间',
            dataIndex: 'startTime',
            width: 210,
            dateFormat: 'yyyy-MM-DD HH:mm:ss',
          },
          {
            title: '操作',
            width: 100,
            dataIndex: 'updateTime',
            fixed: 'right',
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

export default observer(Index)
