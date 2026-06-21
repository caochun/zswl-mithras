import { observer } from '@zswl/admin'
import { Table, Page, Tabs, Button, SearchBar, Select } from '@zswl/components'
import { getTableColumns, dateRangeTransformV2, rangePresets } from '@/utils'
import ALL_COLUMNS from '../../ProvisioningImpairmentColumns/BudgetProvisioningImpairmentColumns'
import Store from './store'
import { DownOutlined } from '@ant-design/icons'
import AddModal from '../../ProvisioningDataAddModal/BudgetProvisioningDataAddModal'
import { downLoadExcel } from '@/components/Excel'
import { DatePicker, Dropdown, Menu } from 'antd'
import { AmountColumn } from '@/components/Format'
import { useMemo } from 'react'

/**
 * 数据查询页面列定义
 * 根据eclRecordApi接口字段进行映射
 */
const nameColumns = [
  '测算时间',
  '合同号',
  '客户名称',
  '合同到期日',
  '评估主体名称',
  '借据编号',
  '内评级别',
  '内评违约概率',
  '外评级别',
  '违约概率PD',
  '所属分组',
  '五级分类',
  '逾期天数',
  '租赁物类型',
  AmountColumn({ title: '剩余本金', dataIndex: 'remainingPrincipal', initFormat: 1 }),
  AmountColumn({ title: '应计利息', dataIndex: 'accruedInterest', initFormat: 1 }),
  AmountColumn({ title: '保证金', dataIndex: 'earnestBalance', initFormat: 1 }),
  'EAD',
  '债项阶段',
  '上迁债项阶段',
  '期限调整系数T',
  '前瞻因子Z',
  '情景权重',
  '基准PDforward',
  '乐观PDforward',
  '悲观PDforward',
  '基准PDIFRS9',
  '乐观PDIFRS9',
  '悲观PDIFRS9',
  '基准ECL',
  '乐观ECL',
  '悲观ECL',
  'ECL',
  AmountColumn({ title: '下期租金', dataIndex: 'nextRentAmount', initFormat: 1 }),
  '备注',
]

/**
 * 数据查询页面组件
 * @param {Object} props - 组件属性
 * @param {string} props.pathname - 路径名
 */
const BudgetProvisioningDataSearchList = ({ pathname }) => {
  const store = useMemo(() => new Store({}), [])
  // 获取表格列配置
  const columns = getTableColumns(ALL_COLUMNS({ pathname }), nameColumns)
  // 获取表单列配置
  const { enums } = store.page.getData() || {}
  const formColumns = [
    { title: '合同号', dataIndex: 'contractCode' },
    { title: '客户名称', dataIndex: 'clientName' },
    {
      title: '测算时间',
      dataIndex: 'createTime',
      element: (
        <DatePicker.RangePicker renderExtraFooter={(panelNode) => {}} ranges={rangePresets} />
      ),
      transform: (val) => dateRangeTransformV2(val, 'createTime'),
    },
    {
      title: '内评级别',
      dataIndex: 'innerMdLevel',
      element: (
        <Select allowClear options={enums?.innerLevelEnum ?? []} placeholder="请选择内评级别" />
      ),
    },
    {
      title: '所属分组',
      dataIndex: 'group',
      element: (
        <Select
          allowClear
          options={enums?.kpiRatingModelGroupEnum ?? []}
          placeholder="请选择所属分组"
        />
      ),
    },
    {
      title: '五级分类',
      dataIndex: 'classify',
      element: (
        <Select
          allowClear
          options={enums?.assetClassifyResultEnum ?? []}
          placeholder="请选择五级分类"
        />
      ),
    },
    {
      title: '债项阶段',
      dataIndex: 'eclStep',
      // 暂无枚举来源，使用输入框便于模糊查询
    },
    {
      title: '租赁物类型',
      dataIndex: 'leaseType',
      element: (
        <Select allowClear options={enums?.leaseTypeEnum ?? []} placeholder="请选择租赁物类型" />
      ),
    },
  ]

  /**
   * 全量数据查询组件
   */
  const DataSearch = observer(() => {
    return (
      <Table
        store={store.$table}
        columnsFilter="budget_provisioning_search"
        columnWidth={180}
        editable={false}
        // scroll={{ x: '2000' }}
        columns={[
          ...columns,
          {
            title: '操作',
            dataIndex: 'action',
            width: 120,
            fixed: 'right',
            actions: (record) => [
              { name: '删除', onClick: () => store.itemDelete(record) },
              {
                name: '编辑',
                onClick: () => store.openAddModal(record),
                disabled: record.sourceType === 0,
              },
            ],
          },
        ]}
      />
    )
  })

  /**
   * 变动数据查询组件
   */
  const ChangeSearch = observer(() => {
    return (
      <Table
        store={store.$exportTable}
        columnsFilter="budget_provisioning_export_search"
        columnWidth={180}
        editable={false}
        columns={[
          ...columns,
          {
            title: '操作',
            dataIndex: 'action',
            width: 120,
            fixed: 'right',
            actions: (record) => [
              { name: '删除', onClick: () => store.compareItemDelete(record) },
              {
                name: '编辑',
                onClick: () => store.openAddModal(record),
                disabled: record.sourceType === 0,
              },
            ],
          },
        ]}
      />
    )
  })
  const { exportLoading, setExportLoading } = store
  // 下载全量数据
  const handleDownload = async (e) => {
    const fastFlag = e.key !== '1'
    const params = store.$table.getParams()
    const postParams = {
      ...params,
      pageSize: 99999,
      page: 1,
      fastFlag,
    }
    setExportLoading(true)
    const res = await store.$table.request(postParams)
    const dataSource = Array.isArray(res?.list) ? res.list : res || []
    await downLoadExcel({
      columns,
      dataSource,
      fileName: fastFlag ? '资产减值记录-最新' : '资产减值记录-全量',
      format: 'csv',
    })
    setExportLoading(false)
  }

  return (
    <Page store={store} params={{ pathname }}>
      <SearchBar items={formColumns} store={store.searchBar} />
      <Tabs
        items={[
          {
            key: '1',
            label: '全量数据',
            children: <DataSearch />,
          },
          {
            key: '2',
            label: '变动数据',
            children: <ChangeSearch />,
          },
        ]}
        tabBarExtraContent={[
          <Button type="primary" onClick={store.calc}>
            测算
          </Button>,
          <Dropdown
            overlay={
              <Menu
                onClick={handleDownload}
                items={[
                  { label: `下载全量数据`, key: '1', disabled: exportLoading },
                  { label: '下载最新数据', key: '2', disabled: exportLoading },
                ]}
              />
            }
          >
            <Button loading={exportLoading}>
              导出
              <DownOutlined />
            </Button>
          </Dropdown>,
          {
            name: '新增数据',
            type: 'primary',
            onClick: () => store.openAddModal(),
          },
        ]}
      />
      <AddModal store={store} />
    </Page>
  )
}
export default observer(BudgetProvisioningDataSearchList)
