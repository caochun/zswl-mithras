import { observer, history } from '@zswl/admin'
import store from './store'
import { Table, App, Page, SearchBar } from '@zswl/components'
import ApprovalHistoryModal from '../../components/ApprovalHistoryModal'
import { ClientSelect } from '@/components/Select'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import { useEffect, useState } from 'react'
import ProcessTypeTree from '@/pages/process/components/ProcessTypeTree'
import { saveServer } from '@/utils'

const { Item } = SearchBar
function Index() {
  const [show, setShow] = useState(false)
  const [ids, setId] = useState('')
  const approvalHistory = ({ processInstanceId }) => {
    setId(processInstanceId)
    setShow(true)
  }
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  //审批退回
  return (
    <>
      <Table
        resizable
        columnsFilter="processApplicationFinish"
        onFilter={(key,val) => saveServer('processApplicationFinish',val)}
        columnWidth={180}
        scroll={{
          x: 2000,
        }}
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
                  to: `/process/application/detail/${value.processInstanceId}?typeId=approval&businessKey=${value.businessKey}&diff=processInstanceId&tab=finish&nav=myquery`,
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
            actions({ clientName, clientId }) {
              if (!clientName) return ''
              return [
                {
                  name: clientName || '-',
                  to: `/customer/maintain/detail/${clientId}?clientType=CORPORATION&flag=info&typeId=create`,
                  disabled: !clientName,
                  className: 'z-single-line',
                  // style: { width: 160 },
                },
              ]
            },
          },
          {
            title: '审批状态',
            dataIndex: 'processStatus',
            width: 120,
            tooltip: true,
            render: (v) => {
              return getKeyOptionsLabelMapPlus('processStatus')[v]
            },
          },

          {
            title: '申请时间',
            dataIndex: 'startTime',
          },
          {
            title: '结束时间',
            dataIndex: 'endTime',
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

export default observer(Index)
