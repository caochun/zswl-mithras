import { ApiSelect, FounderSelect } from '@/components/Select'
import { AmountColumn, AmountEditable, FiledFormat, MatchOptionColumn } from '@/components/Format'
import { isProjmanager, rules, saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import { Select, Table, TableStore } from '@zswl/components'
import { Tooltip } from 'antd'
import { useMemo } from 'react'
import Api from '@/api/customer/maintainApi'

export const childColumns = (hasProjCodeContractCode) => [
  {
    title: '项目名称',
    dataIndex: 'projectName',
    width: 280,
    render: (val) => <FiledFormat title={val} />,
    editable: false,
  },
  {
    title: '项目编号',
    dataIndex: 'projCode',
    width: 200,
    render: (val) => <FiledFormat title={val} />,
    editable: false,
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    render: (val) => <FiledFormat title={val} />,
    width: 280,
    editable: false,
  },
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
    editable: false,
  }),
  {
    title: '新所属主办',
    dataIndex: 'toSponsorId',
    requiredMark: true,
    render: (val, { toSponsorName }) => <FiledFormat title={toSponsorName} />,
    editable: {
      element: <FounderSelect />,
      rules: [rules.required()],
      functionCode: 'selectfounder',
    },
  },
  {
    title: '新所属协办',
    dataIndex: 'toCosponsorIds',
    render: (val, { toCosponsorNames }) => <FiledFormat title={toCosponsorNames} />,
    editable: () => {
      if (!hasProjCodeContractCode) return false
      return {
        element: <FounderSelect mode="multiple" />,
        functionCode: 'selectfounder',
      }
    },
  },
  {
    title: '新所属部门',
    dataIndex: 'toBelongDeptId',
    requiredMark: true,
    render: (val, { toBelongDeptName }) => <FiledFormat title={toBelongDeptName} />,
    editable: {
      element: (
        <ApiSelect
          api={Api.postNewOrgs}
          transformResult={(res) => {
            return res.map((item) => {
              return {
                label: item.label,
                value: +item.value,
              }
            })
          }}
        ></ApiSelect>
      ),
      rules: [rules.required()],
    },
  },
  {
    title: '项目流状态',
    dataIndex: 'projStatus',
    editable: false,
  },
  MatchOptionColumn({
    title: '项目资料归属状态',
    dataIndex: 'projArchiveStatus',
    matchOption: 'clientProjArchive',
    requiredMark: hasProjCodeContractCode,
    editable: (record) => {
      if (!hasProjCodeContractCode) return false
      return {
        element: <Select options={'clientProjArchive'} />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  }),
  AmountColumn({
    title: '风险移交比例',
    dataIndex: 'riskTransferValue',
    suffix: '%',
    requiredMark: hasProjCodeContractCode,
    editable: (record) => {
      if (!hasProjCodeContractCode) return false
      return AmountEditable(record, 'riskTransferValue', {
        required: true,
        disabled: false,
        inputConfig: {
          addonAfter: '%',
        },
      })
    },
  }),
  AmountColumn({
    title: '收益移交比例',
    dataIndex: 'incomeTransferValue',
    suffix: '%',
    requiredMark: hasProjCodeContractCode,
    editable: (record) => {
      if (!hasProjCodeContractCode) return false
      return AmountEditable(record, 'incomeTransferValue', {
        required: true,
        disabled: false,
        inputConfig: {
          addonAfter: '%',
        },
      })
    },
  }),
]

// 将子表格提取为独立组件
const ChildTable = observer(({ record, store, canEdit }) => {
  const { clientProjRSPList } = record
  // 使用 useMemo 缓存 TableStore，避免每次渲染重建
  const childTableStore = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async (params) => {
          return clientProjRSPList
        },
      }),
    [clientProjRSPList]
  )

  return (
    <div style={{ paddingLeft: 60 }}>
      <Table
        columnsFilter={'maintain_handover_RelateProject_2'}
        onFilter={(key, val) => saveServer('maintain_handover_RelateProject_2', val)}
        editable={false}
        scroll={{ x: 2000 }}
        rowKey={(childRecord, index) => `${record.id}_${childRecord.id}_${index}`}
        rowSelection={{
          selectedRowKeys: store.selectedProjectKeys.slice(),
          onSelect: (childRecord, selected) => {
            const index = clientProjRSPList.indexOf(childRecord)
            store.toggleProjectSelection(
              { ...childRecord, id: record.id },
              selected,
              `${record.id}_${childRecord.id}_${index}`
            )
          },
          onSelectAll: (selected, selectedRows, changeRows) => {
            const items = changeRows.map((r) => {
              const index = clientProjRSPList.indexOf(r)
              return {
                record: { ...r, id: record.id },
                uniqueKey: `${record.id}_${r.id}_${index}`,
              }
            })
            store.toggleProjectsSelection(items, selected)
          },
          getCheckboxProps: (childRecord) => ({
            disabled: record.inProcess || childRecord.inProcess,
          }),
        }}
        columns={[
          ...childColumns(),
          {
            title: '操作',
            fixed: 'right',
            width: 120,
            actions(childrenRecord) {
              return [
                {
                  name: '查看',
                  key: 'view',
                  onClick: () => {
                    store.setEditStatus(false)
                    store.editSponsorModal.open({
                      ...childrenRecord,
                      toBelongDeptId: '7',
                      id: record.id,
                    })
                  },
                },
                !record.inProcess &&
                  canEdit &&
                  isProjmanager() && {
                    name: '编辑',
                    key: 'edit',
                    onClick: () => {
                      store.setEditStatus(true)
                      store.editSponsorModal.open({
                        ...childrenRecord,
                        id: record.id,
                      })
                    },
                  },
              ]
            },
          },
        ]}
        store={childTableStore}
      />
    </div>
  )
})

const Index = ({ store, canEdit }) => {
  const { isFormApproval } = store.page.getParams()
  const { expandKeys, setExpandKeys } = store
  const selectedCount = store.selectedProjectKeys.length

  return (
    <Table
      columnsFilter={'maintain_handover_RelateProject_1'}
      onFilter={(key, val) => saveServer('maintain_handover_RelateProject_1', val)}
      rowKey={({ id }) => `${id}`}
      extra={[
        {
          name: '导出',
          type: 'primary',
          onClick: store.export,
        },
        isProjmanager() &&
          canEdit && {
            name: '编辑',
            type: 'primary',
            onClick: store.openBatchEdit,
          },
      ]}
      selectable={{
        getCheckboxProps: (record) => {
          return {
            disabled: record.inProcess,
          }
        },
        onSelect: (record, selected) => store.toggleClientSelection(record, selected),
        onSelectAll: (selected, selectedRows, changeRows) => {
          changeRows.forEach((record) => store.toggleClientSelection(record, selected))
        },
      }}
      scroll={{ x: 900 }}
      columnWidth={180}
      title={() => <>{<div>已选中： {selectedCount} 条记录</div>}</>}
      store={store.table}
      pagination={false}
      columns={[
        {
          title: '客户名称',
          dataIndex: 'clientName',
          width: 280,
          render: (val) => <FiledFormat title={val} />,
        },
        MatchOptionColumn({
          title: '客户分类',
          dataIndex: 'clientType',
          matchOption: 'clientType',
        }),
        MatchOptionColumn({
          title: '客户资产五级分类',
          dataIndex: 'assertClassifyResult',
          matchOption: 'assetClassifyResultEnum',
        }),
        {
          title: '当前所属主办',
          dataIndex: 'belongSponsorName',
          render: (val) => <FiledFormat title={val} />,
        },
        {
          title: '当前所属部门',
          dataIndex: 'belongDeptName',
          render: (val) => <FiledFormat title={val} />,
        },
        {
          title: '当前状态',
          dataIndex: 'inProcess',
          render: (inProcess, index) => {
            const text = inProcess ? '处于流程中，不可移交' : '可移交'
            return <Tooltip title={text}>{text}</Tooltip>
          },
        },
      ]}
      expandable={{
        expandedRowKeys: expandKeys,
        onExpand: (expanded, record) => {
          if (expanded) {
            setExpandKeys([...expandKeys, `${record.id}`])
          } else {
            setExpandKeys(expandKeys.filter((item) => item !== `${record.id}`))
          }
        },
        defaultExpandAllRows: true,
        expandedRowRender: (record) => (
          <ChildTable key={record.id} record={record} store={store} canEdit={canEdit} />
        ),
      }}
    />
  )
}
export default observer(Index)
