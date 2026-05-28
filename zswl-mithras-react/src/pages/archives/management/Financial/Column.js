import { CreditOrgSelect, FounderSelect } from '@/components'
import { MatchOptionColumn } from '@/components/Format'
import { Select } from 'antd'
import store from './store'

const ALL_COLUMNS = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    fixed: 'left',
    render: (val, record, index) => {
      const params = store.table.getParams?.() || {}
      const page = params.pageNum ?? params.page ?? 1
      const pageSize = params.pageSize ?? 20
      return (page - 1) * pageSize + index + 1
    },
  },
  {
    title: '融资编号',
    dataIndex: 'financingCode',
    width: 180,
    render: (val) => val || '-',
  },
  MatchOptionColumn({
    title: '归档类型',
    dataIndex: 'filingType',
    matchOption: 'filingMaterialsFilingTypeEnum',
    width: 120,
    render: (val) => val || '-',
  }),
  MatchOptionColumn({
    title: '项目类别',
    dataIndex: 'projClassify',
    matchOption: 'directFinancingType',
    width: 150,
    search: true,
    render: (val) => val || '-',
  }),
  {
    title: '项目类别/业务类型',
    dataIndex: 'projClassifyOrBizType',
    width: 250,
    render: (val) => val || '-',
  },
  {
    title: '产品名称/融资机构',
    dataIndex: 'productNameOrOrganizationName',
    width: 350,
    render: (val) => val || '-',
  },
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'fundFinancingBizTypeEnum',
    width: 150,
    search: true,
  }),
  {
    title: '产品名称',
    dataIndex: 'productName',
    width: 200,
    render: (val) => val || '-',
  },
  {
    title: '融资机构',
    dataIndex: 'organizationName',
    width: 250,
    search: {
      dataIndex: 'organizationId',
      itemProps: {
        transform: (val) => ({ financingOrgName: undefined, organizationId: val }),
      },
      element: <CreditOrgSelect params={{ pageSize: 1000 }} />,
    },
  },
  MatchOptionColumn({
    title: '融资状态',
    dataIndex: 'financingStatus',
    width: 150,
    matchOption: 'fundFinancingStatusEnum',
    search: true,
  }),
  {
    title: '起息日',
    dataIndex: 'carryInterestTime',
    width: 150,
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          carryInterestTime: undefined,
          carryInterestTimeFrom: startDataTime?.format('yyyy-MM-DD'),
          carryInterestTimeTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  {
    title: '到期日',
    dataIndex: 'endDate',
    width: 150,
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          endDate: undefined,
          endDateFrom: startDataTime?.format('yyyy-MM-DD'),
          endDateTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  {
    title: '资金经理',
    dataIndex: 'fundManagerName',
    width: 120,
    search: {
      dataIndex: 'fundManagerId',
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
  },
  {
    title: '是否完成归档',
    dataIndex: 'archiveFlag',
    width: 120,
    search: {
      element: (
        <Select
          allowClear
          placeholder="请选择"
          options={[
            { label: '是', value: true },
            { label: '否', value: false },
          ]}
        />
      ),
    },
    render: (val) => {
      if (val === true) return '是'
      if (val === false) return '否'
      return '-'
    },
  },
  {
    title: '归档时间',
    dataIndex: 'archiveDate',
    width: 180,
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          archiveDate: undefined,
          archiveDateFrom: startDataTime?.format('yyyy-MM-DD'),
          archiveDateTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  {
    title: '操作',
    dataIndex: 'operation',
    width: 100,
    fixed: 'right',
    actions({ id, archiveFlag, filingMaterialsId }) {
      if (archiveFlag) {
        return [
          {
            name: '详情',
            to: `/archives/management/detail/${filingMaterialsId}`,
          },
        ]
      }
      return []
    },
  },
]

export default ALL_COLUMNS
