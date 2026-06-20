import { useMemo, forwardRef, useImperativeHandle, useEffect } from 'react'
import { Table, Page } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from '../PublicMonitorColumns/RiskPublicMonitorColumns'
import Handle from './Handle'
import ChangeInfo from './Components/ChangeInfo'
import CourtAnnounce from './Components/CourtAnnounce'
import CourtSession from './Components/CourtSession'
import CaseInfo from './Components/CaseInfo'
import Store from './Store'
import CloseModal from './CloseModal'
import AddModal from './AddModal'
import { saveServer } from '@/utils'
import { isRiskManager, isAssetJon } from '@/utils/auth'

const nameColumns = [
  '标题',
  '客户名称',
  '统一社会信用代码',
  '状态',
  '处置意见',
  '预警星级',
  '预警信号',
  '主体机构代码',
  '来源类型',
  '创建人',
  '关联关系描述',
  '信息发布日期',
]
const formNameColumns = [
  'ID',
  '标题',
  '客户名称',
  {
    title: '统一社会信用代码',
    rename: (
      <div style={{ fontSize: 12 }}>
        <div>统一社会</div>
        <div>信用代码</div>
      </div>
    ),
  },
  '状态',
  '预警星级',
  '预警信号',
]

function Index(
  {
    showPageStyle = true,
    canEdit = true,
    chiName = '',
    query = {},
    customNameColumns,
    customFormNameColumns,
  },
  ref
) {
  const { recordId } = query
  const queryName = query.chiName || chiName

  const store = useMemo(() => {
    return new Store(queryName)
  }, [queryName])

  const { $table, $closeModal, $handleModal, seeFlowDetail, handleJump, handleOpenModal } = store

  const columns = getTableColumns(
    ALL_COLUMNS({
      canEdit: true,
    }),
    customNameColumns?.length >= 0 ? customNameColumns : nameColumns
  )
  const formColumns = getFormColumns(
    ALL_COLUMNS({
      canEdit: true,
    }),
    customFormNameColumns?.length >= 0 ? customFormNameColumns : formNameColumns
  )

  useImperativeHandle(ref, () => ({
    getData: () => {
      return $table.getList()
    },
  }))

  useEffect(() => {
    $table.setParams({ id: recordId, chiName: queryName })
    $table.search()
  }, [recordId, queryName])
  return (
    <Page noStyle={!showPageStyle}>
      <Table
        // columnsFilter={'风控管理_舆情监测'}
        autoRequest={false}
        scroll={{ x: 1600 }}
        store={$table}
        editable={false}
        searchbar={{
          items: formColumns,
        }}
        columnsFilter={'工作台_客户视图_客户舆情'}
        onFilter={(key, val) => saveServer('工作台_客户视图_客户舆情', val)}
        columns={[
          ...columns,
          {
            title: '操作',
            fixed: 'right',
            width: 160,
            actions(record) {
              const { id, handleStatus, riskType, linkAddress, dataSource, operableFlag, chiName, creditCode } = record
              const canHandle = canEdit && ['REJECTED', 'PEND_HANDLE'].includes(handleStatus)
              // 新的工商信息舆情
              if (riskType === 2) {
                return [
                  {
                    name: '查看',
                    onClick: () => dataSource && dataSource === 'XINSIGHT' ? handleJump(record) : handleOpenModal(record),
                  },
                  canHandle && {
                    name: '关闭',
                    onClick: () => $closeModal.open(record),
                  },
                  operableFlag && {
                    name: '编辑',
                    onClick: () => history.push(`/risk/publicMonitor/detail/${id}?clientName=${chiName}&creditCode=${creditCode}`),
                  },
                  operableFlag && {
                    name: '删除',
                    onClick: () => store.postMonitorDelete({id}),
                  }
                ]
              } else if (riskType === 1) {
                if (handleStatus === 'IGNORED') {
                  return [
                    linkAddress && {
                      name: '查看',
                      onClick: () => handleJump(record),
                    },
                  ]
                }
                return [
                  linkAddress && {
                    name: '查看',
                    onClick: () => handleJump(record),
                  },
                  canHandle && {
                    name: '处理',
                    onClick: () => $handleModal.open(record),
                  },
                  canHandle && {
                    name: '关闭',
                    onClick: () => $closeModal.open(record),
                  },
                  [('HANDLE_ING', 'HANDLED')].includes(handleStatus) && {
                    name: '查看处理',
                    onClick: () => seeFlowDetail(record),
                  },
                  operableFlag && {
                    name: '编辑',
                    onClick: () => history.push(`/risk/publicMonitor/detail/${id}?clientName=${chiName}&creditCode=${creditCode}`),
                  },
                  operableFlag && {
                    name: '删除',
                    onClick: () => store.postMonitorDelete({id}),
                  }
                ].filter(Boolean)
              }
            },
          },
        ]}
        rowKey={(record) => record.id+'_'+Math.random()}
        actions={[
          (isAssetJon() || isRiskManager()) && {
            name: '新增舆情',
            onClick: store.$addModal.open,
            type: 'primary',
          }
        ]}
      />
      <Handle store={store}></Handle>
      <CloseModal store={store}></CloseModal>
      <ChangeInfo store={store}></ChangeInfo>
      <CourtAnnounce store={store}></CourtAnnounce>
      <CourtSession store={store}></CourtSession>
      <CaseInfo store={store}></CaseInfo>
      <AddModal store={store}></AddModal>
    </Page>
  )
}

export default observer(forwardRef(Index))
