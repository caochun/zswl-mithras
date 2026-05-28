import { observer } from '@zswl/admin'
import Store from './store'
import { Table, App, Page, SearchBar, Access } from '@zswl/components'
import { useEffect, useState, useMemo } from 'react'
import SendDuplicateModal from '../Detail/ZTabs/Operation/Components/SendDuplicateModal'
import { ClientSelect, FounderSelect, OrgSelect } from '@/components/Select'
import BpmnFlowChooseChartModal from '@/components/BpmnFlowChooseChart/ModalOPenChart'
import ForWardModal from './ForWardModal'
import FastHandle from './FastHandle'
import ProcessTypeTree from '@/pages/process/components/ProcessTypeTree'
import { PageListDown } from '@/components'
import { ApiSelect } from '@/components'
import Api from './api'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function Index({ query }) {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const [show, setShow] = useState(false)
  const [visible, setVisible] = useState(false)

  const [ids, setId] = useState('')

  const sendDuplicate = ({ processInstanceId }) => {
    setId(processInstanceId)
    setShow(true)
  }
  const sendChart = ({ processInstanceId }) => {
    setId(processInstanceId)
    setVisible(true)
    store.table.search()
  }
  const { search } = query
  useEffect(() => {
    if (search) {
      const searchObj = JSON.parse(search)
      const { clientId, clientName, ...rest } = searchObj
      const searchForm = store.table.formStore

      if (clientId) {
        searchForm.setFieldsValue({
          clientId: {
            label: clientId,
            value: clientName,
          },
        })
      }
      store.table.setParams({
        ...rest,
        clientId,
      })
      store.table.search()
    } else {
      store.table.setParams()
      store.table.search()
    }
  }, [search])
  return (
    <Page>
      <Table
        columnWidth={180}
        autoRequest={false}
        columnsFilter="processQuery"
        onFilter={(key,val) => saveServer('processQuery',val)}
        resizable
        scroll={{ x: 1500 }}
        store={store.table}
        extra={[<PageListDown key="1" module="processQuery" table={store.table} />]}
        searchbar={{
          initialValues: {},
          labelCol: { span: 6 },
          items: [
            {
              label: '流程ID',
              name: 'processInstanceId',
              placeholder: '单个查询',
            },
            <Item name="processInstanceIdList" label="流程ID">
              <ApiSelect
                placeholder="多个查询"
                debounceSearch
                maxTagCount={10}
                mode="multiple"
                api={Api.getList}
                transformResult={(res) => res?.list}
                searchField={'processInstanceId'}
                fieldNames={{
                  label: 'processInstanceId',
                  value: 'processInstanceId',
                }}
              ></ApiSelect>
            </Item>,
            {
              label: '审批状态',
              name: 'processStatus',
              options: 'processStatus',
            },
            <Item name="modelKeyList" label="流程类型" key="modelKeyList">
              <ProcessTypeTree></ProcessTypeTree>
            </Item>,
            {
              label: '表单名称',
              allowClear: true,
              name: 'processName',
            },
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
            <Item label="发起人" name="startUserId" key="startUserId">
              <FounderSelect
                functionCode="selectfounder-3"
                params={{ job: undefined }}
              ></FounderSelect>
            </Item>,
            <Item label="申请部门" name="startUserDeptId" key="startUserDeptId">
              <OrgSelect functionCode="selectorgs-2"></OrgSelect>
            </Item>,
            <Item label="客户名称" name="clientId" key="clientId">
              <ClientSelect functionCode={'clientlist-flow'} canJump={false}></ClientSelect>
            </Item>,
          ],
        }}
        columns={[
          {
            title: '流程ID',
            dataIndex: 'processInstanceId',
            width: 100,
            fixed: 'left',
            render(value, record) {
              return (
                <a
                  target="_blank"
                  href={`/process/query/detail/${record.processInstanceId}?typeId=approval&businessKey=${record.businessKey}&diff=processInstanceId&nav=myquery`}
                >
                  {record.processInstanceId}
                </a>
              )
            },
          },
          {
            title: '审批状态',
            dataIndex: 'processStatus',
            width: 100,
            tooltip: true,
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
            width: 350,
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
            title: '最后审批人',
            dataIndex: 'finalAssigneeName',
          },
          {
            title: '当前节点',
            dataIndex: 'curTaskNames',
            render: (value) => value || '-',
          },
          {
            title: '当前审批人',
            dataIndex: 'curAssigneeNames',
            render: (value) => value || '-',
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
            width: Access.validate('flowexecutionjump') ? 280 : 120,
            fixed: 'right',
            dataIndex: 'updateTime',
            isAction: true,
            align: 'center',
            tooltip: false,
            actions(value) {
              // 只有审批中才能点击
              const disabled = value.processStatus !== '1'
              return [
                {
                  name: '抄送',
                  onClick: () => {
                    sendDuplicate(value)
                  },
                },
                {
                  name: '跳转',
                  access: 'flowexecutionjump',
                  disabled,
                  // fallback: <a disabled>跳转</a>,
                  fallback: null,
                  onClick: () => {
                    sendChart(value)
                  },
                },
                {
                  name: '转办',
                  disabled,
                  access: 'flowexecutiontransfer',
                  // fallback: <a disabled>转办</a>,
                  fallback: null,
                  onClick: () => {
                    store.forWardModalStore.open(value?.processInstanceId)
                  },
                },
                {
                  name: '一键通过',
                  disabled,
                  access: 'flowexecutionpassAll',
                  // fallback: <a disabled>一键通过</a>,
                  fallback: null,
                  onClick: () => {
                    store.fastHandleOpen(value?.processInstanceId, 'pass')
                    // store.pass(value?.processInstanceId)
                  },
                },
                {
                  name: '一键拒绝',
                  disabled,
                  access: 'flowexecutionrejectAll',
                  // fallback: <a disabled>一键拒绝</a>,
                  fallback: null,
                  onClick: () => {
                    store.fastHandleOpen(value?.processInstanceId, 'reject')
                    // store.reject(value?.processInstanceId)
                  },
                },
              ]
            },
          },
        ]}
      />
      <BpmnFlowChooseChartModal
        visible={visible}
        setVisible={setVisible}
        processInstanceId={ids}
        callBack={(value) => {
          // console.log('value输出: ', value);
          // console.log('processInstanceId输出: ', ids);
          setVisible(false)
          store.jump(value, ids)
        }}
      />
      <SendDuplicateModal
        visible={show}
        processInstanceId={ids}
        callBack={() => {
          setShow(false)
        }}
        detailData={{ ccTabReadOnlyFlag:'', ccUerList:'' }}
      />
      <ForWardModal store={store} />
      <FastHandle store={store} />
    </Page>
  )
}

export default observer(Index)
