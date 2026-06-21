import { Page, Table, Button } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import { useMemo } from 'react'
import Store from './store'
import ALL_COLUMNS from '../Columns'
import AddModal from './AddModal'
import {
  getTableColumns,
  getUserInfo,
  isLegalmanager,
  isDept,
  isProjmanager,
} from '@/utils'
import { saveServer } from '@/utils'

/**
 * 白名单管理页面
 * 提供白名单的查询、新增、编辑、删除等功能
 */
const nameColumns = [
  {
    title: '评估机构名称',
    fixed: 'left',
    width: 230,
    dataIndex: 'companyName',
    search: true,
    actions: ({ companyName: name }) => [
      {
        name,
        onClick: (record) => {
          history.push(`/whiteList/detail/${record.id}`)
        },
      },
    ],
  },
  '社会统一信用代码',
  '状态',
  '流程状态',
  '生效日',
  '到期日',
  '创建部门',
  '最近更新人',
  '更新时间',
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns, true)

/**
 * 白名单管理主组件
 * @returns {JSX.Element} 白名单管理页面
 */
function WhiteListList() {
  const store = useMemo(() => {
    return new Store({})
  }, [])
  //  【变更】：作为创建人的项目经理可操作，状态为「生效」审批流状态为「新建审批通过」或「变更审批通过」数据可勾选单个评估机构，后点击【变更】按钮发起变更流程。
  // 【出库】：法务经办可操作，状态为「生效」审批流状态为「新建审批通过」或「变更审批通过」数据可勾选单个评估机构，点击出库可发起评估机构出库流程。
  // 【删除】：作为创建人的项目经理可操作，状态为「生效」审批流状态为「新建审批通过」或「变更审批通过」数据可勾选单个评估机构，点击删除可删除评估机构。
  const { rows } = store.$table.getSelected()
  const userInfo = getUserInfo()
  const changeDisable =
    rows.length === 1 &&
    rows.every(
      ({ recordStatus, processStatus, createBy, deptId }) =>
        ['TAKE_EFFECT'].includes(recordStatus) &&
        [
          'NEW_APPROVAL_PASS',
          'CHANGE_APPROVAL_PASS',
          'CHANGE_UN_SUBMIT',
          'CHANGE_REJECT',
          'CANCEL_CHANGE',
        ].includes(processStatus) &&
        isDept(deptId)
    )

  const outDisable =
    (isLegalmanager() || (isProjmanager() && rows.every(({ deptId }) => isDept(deptId)))) &&
    rows.length === 1 &&
    rows.every(({ recordStatus, processStatus, createBy }) => {
      return (
        ['TAKE_EFFECT'].includes(recordStatus) &&
        [
          'NEW_APPROVAL_PASS',
          'CHANGE_APPROVAL_PASS',
          'OUT_UN_SUBMIT',
          'OUT_REJECT',
          'CANCEL_OUT',
        ].includes(processStatus)
      )
    })
  // 添加操作列
  const operationColumn = {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 100,
    actions: (record) => {
      const canEdit = isDept(record.deptId)
      return [
        {
          name: '删除',
          onClick: () => store.handleDelete(record),
          confirm: canEdit,
          disabled: !canEdit,
        },
      ]
    },
  }
  // 将操作列添加到 columns 数组中
  const finalColumns = [...columns, operationColumn]
  return (
    <Page store={store}>
      <Table
        columnWidth={180}
        selectable={{
          type: 'radio',
        }}
        store={store.$table}
        editable={false}
        actions={[
          <Button type="primary" onClick={() => store.handleCreate()}>
            新增评估机构
          </Button>,
          <Button onClick={store.handleChange} disabled={!changeDisable}>
            变更
          </Button>,
          <Button onClick={store.handleOutSubmit} disabled={!outDisable}>
            出库
          </Button>,
        ]}
        scroll={{
          x: 1500,
        }}
        columns={finalColumns}
        columnsFilter={'whiteList_list'}
        onFilter={(key, val) => saveServer('whiteList_list', val)}
      />
      <AddModal store={store} />
    </Page>
  )
}

export default observer(WhiteListList)
