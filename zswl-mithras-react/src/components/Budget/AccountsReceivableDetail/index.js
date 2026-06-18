import { Button, Page, Table, Tabs } from '@zswl/components'
import { observer } from '@zswl/admin'
import { PlusOutlined } from '@ant-design/icons'
import { getTableColumns } from '@/utils'
import store from './store'
import ALL_COLUMNS from './Columns'
import EditModal from './DetailEditModal'
import OverdueEditModal from './OverdueEditModal'
import { TableExportAction as TableExport } from '@/components/Actions'
import { RentCollectionTermDetail as TermDetail } from '@/components/AfterLease/RentCollectionEntries'

/**
 * 应收账款详情页面组件
 */
const Index = observer(({ params, query }) => {
  const { id } = params
  const { processInstanceId } = query
  const collectionActions = ({ collectionCode: name, ...record }) => [
    {
      key: 'edit',
      name,
      onClick: () => store.openTerm(record),
    },
  ]
  // 应收逾期集成表显示的列名
  const detailNameColumns = [
    {
      title: '收款计划编号',
      actions: collectionActions,
      search: true,
    },
    '单据状态',
    '合同编号',
    '项目名称',
    '客户名称',
    '款项内容',
    '单据账龄起算日',
    '科目',
    '约定收款日期',
    '约定收款条件',
    '应收金额（元）',
    '行业政策收款周期',
    '审批状态',
    { title: '单据日期', search: false },
  ]

  // 应收逾期集成结算单表格显示的列名
  const overdueNameColumns = [
    {
      title: '收款计划编号',
      actions: collectionActions,
      search: true,
    },
    '单据状态',
    '合同编号',
    '项目名称',
    '客户名称',
    '单据日期',
    '结算日期',
    '结算记录的凭证记账日期',
    '结算关系',
    '结算金额（元）',
    '审批状态',
  ]

  // 获取列配置
  const overdueColumns = getTableColumns(ALL_COLUMNS, overdueNameColumns, true)
  const detailColumns = getTableColumns(ALL_COLUMNS, detailNameColumns, true)
  /**
   * 应收逾期集成结算单组件
   */
  const OverdueTable = ({ canEdit }) => (
    <Table
      store={store.overdueTable}
      columns={[
        ...overdueColumns,
        canEdit && {
          title: '操作',
          dataIndex: 'actions',
          width: 80,
          fixed: 'right',
          actions: [
            {
              key: 'edit',
              name: '编辑',
              onClick: store.editOverdue,
            },
          ],
        },
      ]}
      extra={[
        <TableExport
          table={store.overdueTable}
          otherExcelProps={{ fileName: '应收逾期集成结算表' }}
        />,
      ]}
      columnWidth={120}
      selectable={canEdit}
      actions={
        canEdit && [
          {
            key: 'add',
            name: '新增结算单',
            icon: <PlusOutlined />,
            type: 'primary',
            onClick: store.addOverdue,
          },
        ]
      }
      scroll={{ x: 2000 }}
    />
  )

  /**
   * 应收逾期集成单表格组件
   */
  const DetailTable = ({ canEdit }) => (
    <Table
      store={store.detailTable}
      columns={[
        ...detailColumns,
        canEdit && {
          title: '操作',
          dataIndex: 'actions',
          fixed: 'right',
          width: 80,
          actions: [
            {
              key: 'edit',
              name: '编辑',
              onClick: store.editDetail,
            },
          ],
        },
      ]}
      resizable
      selectable={canEdit}
      columnWidth={120}
      extra={[
        <TableExport table={store.detailTable} otherExcelProps={{ fileName: '应收逾期集成单' }} />,
      ]}
      scroll={{ x: 2000 }}
    />
  )
  const canEdit = !processInstanceId
  // Tabs配置
  const tabItems = [
    {
      label: '应收逾期集成单',
      key: 'detail',
      children: <DetailTable canEdit={canEdit} />,
    },
    {
      label: '应收逾期集成结算表',
      key: 'overdue',
      children: <OverdueTable canEdit={canEdit} />,
    },
  ]

  return (
    <Page params={{ id, processInstanceId }} store={store}>
      <Tabs
        items={tabItems}
        defaultActiveKey="detail"
        tabBarExtraContent={[
          !processInstanceId && (
            <Button type="primary" onClick={store.report}>
              推送苍穹
            </Button>
          ),
          !processInstanceId && (
            <Button type="primary" onClick={store.submit}>
              提交审批
            </Button>
          ),
        ]}
      />
      <EditModal store={store} />
      <OverdueEditModal store={store} />
      <TermDetail baseStore={store} collectionId={store.collectionId} />
    </Page>
  )
})

export default Index
