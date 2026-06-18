import { observer, history } from '@zswl/admin'
import store from './store'
import { Table, App, Page, SearchBar } from '@zswl/components'
import { ClientSelect } from '@/components/Select'
import { useEffect, useState } from 'react'
import ApprovalHistoryModal from '@/components/Process/ApprovalHistoryModal'
import ProcessTypeTree from '@/components/Process/ProcessTypeTree'
import { saveServer } from '@/utils'
import customeApi from '@/api/customer/maintainApi'

const { Item } = SearchBar

function Index() {
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
  return (
    <>
      <Table
        scroll={{
          x: 2000,
        }}
        columnWidth={180}
        resizable
        columnsFilter="processApplicationApply"
        onFilter={(key, val) => saveServer('processApplicationApply', val)}
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
                  to: `/process/application/detail/${value.processInstanceId}?typeId=approval&businessKey=${value.businessKey}&diff=processInstanceId&tab=apply`,
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
            width: 200,
            render:(val, { clientId }) => <a onClick={async() => {
              if (!val) return ''
              try {
                const result = await customeApi.postClientApplyOccupy({clientId})
                if(result.msg !== '所选客户为空'){
                  history.push(`/customer/maintain/detail/${clientId}?clientType=CORPORATION&flag=info&typeId=create`)
                }
              } catch (error) {
                
              }
            }}>{val || '-'}</a>
            // actions: ({ clientName, clientId }) => {
            //   if (!clientName) return ''
            //   // const result =  customeApi.postClientApplyOccupy({clientId})
            //   // if(result.msg === '所选客户为空'){
            //   return [
            //     {
            //       name: clientName || '-',
            //       to: `/customer/maintain/detail/${clientId}?clientType=CORPORATION&flag=info&typeId=create`,
            //       disabled: !clientName,
            //       className: 'z-single-line',
            //       // style: { width: 160 },
            //     },
            //   ]
            // },
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
            title: '申请时间',
            dataIndex: 'startTime',
          },
          {
            title: '操作',
            width: 200,
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
                {
                  name: '撤回',
                  onClick: () => {
                    store.withdrawToStartUse(value)
                  },
                },
                {
                  name: '关闭流程',
                  onClick: () => {
                    store.cancelProcess(value)
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
