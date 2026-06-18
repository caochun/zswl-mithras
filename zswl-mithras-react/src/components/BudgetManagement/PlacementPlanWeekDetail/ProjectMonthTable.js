import { observer } from '@zswl/admin'
import {
  AmountColumn,
  AmountEditable,
  DateColumn,
  DatePickerEditable,
  MatchOptionColumn,
  TextAreaColumn,
  TextAreaEditable,
} from '@/components/Format'
import { Select, Table } from '@zswl/components'
import { FounderSelect, OrgSelect } from '@/components/Select'

const INIT_FORMAT = 10000 * 10000
const PARENT_FORMAT = 10000

const ProjectTable = observer(({ store, canEdit }) => {
  const { editIndex, rowIndex } = store
  const AmountEditableRow = ({ title, dataIndex, initFormat, suffix, width }) => {
    return AmountColumn({
      title,
      dataIndex,
      initFormat,
      suffix,
      width,
      editable: (record, rowIndex) =>
        editIndex === rowIndex
          ? AmountEditable(record, dataIndex, {
              disabled: false,
              initFormat,
            })
          : false,
    })
  }
  const DateEditableRow = ({ title, dataIndex, search }) => {
    return DateColumn({
      title,
      dataIndex,
      search,
      editable: (val, rowIndex) =>
        editIndex === rowIndex ? DatePickerEditable(val, dataIndex) : false,
    })
  }
  const MatchOptionEditableRow = ({ title, dataIndex, matchOption, required, width }) => {
    return MatchOptionColumn({
      title,
      dataIndex,
      matchOption,
      width,
      editable: (record, rowIndex) =>
        editIndex === rowIndex
          ? {
              element: <Select options={matchOption} />,
              rules: required && [rules.required('请选择')],
              requiredMark: required,
            }
          : false,
    })
  }
  const columns = [
    {
      title: '部门',
      dataIndex: 'belongDeptName',
      width: 160,
      fixed: 'left',
    },
    { title: '项目编号/合同编号', dataIndex: 'projCode', width: 200, fixed: 'left' },
    { title: '客户名称', dataIndex: 'clientName', width: 200, fixed: 'left' },
    MatchOptionEditableRow({
      title: '业务类型',
      dataIndex: 'leaseType',
      matchOption: 'leaseType',
      width: 160,
    }),
    {
      title: '业务来源',
      dataIndex: 'projSource',
      matchOption: 'projSourceType',
    },
    {
      title: '项目地区',
      dataIndex: 'provinceName',
      width: 200,
      render: (text, record) =>
        `${record.provinceName || ''} ${record.cityName || ''} ${record.districtName || ''}`,
    },
    MatchOptionColumn({
      title: 'FTP行业分类',
      dataIndex: 'ftpIndustryCategory',
      matchOption: 'ftpIndustryCategoryEnum',
      search: true,
    }),
    AmountColumn({ title: '授信金额(万元)', dataIndex: 'creditAmount', initFormat: INIT_FORMAT }),
    DateEditableRow({ title: '投放日', dataIndex: 'planPayDate', search: true }),
    AmountColumn({
      title: '已投放金额(万元)',
      dataIndex: 'paidAmount',
      initFormat: INIT_FORMAT,
      width: 160,
    }),
    AmountEditableRow({
      title: '拟投放金额(万元)',
      dataIndex: 'planPayAmount',
      initFormat: INIT_FORMAT,
      width: 160,
    }),
    AmountEditableRow({
      title: '租赁期限(月)',
      dataIndex: 'termMonth',
      initFormat: 1,
    }),
    AmountEditableRow({
      title: '项目保证金(万元)',
      dataIndex: 'deposit',
      initFormat: INIT_FORMAT,
      width: 160,
    }),
    AmountEditableRow({
      title: '项目咨询费(万元)',
      dataIndex: 'consultingFee',
      initFormat: INIT_FORMAT,
      width: 160,
    }),
    AmountEditableRow({
      title: '合同利率(%)',
      dataIndex: 'contractInterestRate',
      initFormat: PARENT_FORMAT,
      suffix: '%',
    }),
    AmountEditableRow({
      title: 'IRR(%)',
      dataIndex: 'irr',
      initFormat: PARENT_FORMAT,
      suffix: '%',
    }),
    AmountEditableRow({
      title: 'FTP(%)',
      dataIndex: 'ftp',
      initFormat: PARENT_FORMAT,
      suffix: '%',
    }),
    AmountColumn({
      title: '价差',
      dataIndex: 'irrFtpDiff',
      initFormat: PARENT_FORMAT,
      suffix: '%',
    }),
    MatchOptionEditableRow({
      title: '本周项目计划节点',
      dataIndex: 'planActionThisWeek',
      matchOption: 'budgetPlanPayProjectStageEnum',
      width: 160,
    }),
    MatchOptionEditableRow({
      title: '本周项目实际节点',
      dataIndex: 'actualActionThisWeek',
      matchOption: 'budgetPlanPayProjectStageEnum',
      width: 160,
    }),
    MatchOptionEditableRow({
      title: '下周项目计划节点',
      dataIndex: 'planActionNextWeek',
      matchOption: 'budgetPlanPayProjectStageEnum',
      width: 160,
    }),
    MatchOptionEditableRow({
      title: '项目进展状态',
      dataIndex: 'projectProgress',
      matchOption: 'budgetPlanPayProjectStageEnum',
    }),
    TextAreaColumn({
      title: '存在问题及建议',
      dataIndex: 'suggestion',
      width: 200,
      editable: (record, rowIndex) =>
        editIndex === rowIndex ? TextAreaEditable(record, 'suggestion') : false,
    }),
    MatchOptionEditableRow({
      title: '系统流程状态',
      dataIndex: 'rzyProcessStage',
      matchOption: 'allProcessTypeEnum',
      width: 200,
    }),
    MatchOptionEditableRow({
      title: '运营进度反馈',
      dataIndex: 'yunyingFeedback',
      matchOption: 'budgetPlanPayYunYingFeedbackEnum',
      width: 180,
    }),
    MatchOptionEditableRow({
      title: '运营优先级',
      dataIndex: 'yunyingPriority',
      matchOption: 'yunyingPriorityEnum',
    }),
    MatchOptionEditableRow({
      title: '是否纳入资金计划',
      dataIndex: 'bringIntoFundPlan',
      matchOption: 'budgetPlanPayFundPlanEnum',
      width: 180,
    }),
  ]
  return (
    <>
      <Table
        selectable
        columnWidth={140}
        columnsFilter="placement_plan_week"
        onFilter={(key, val) => saveServer('placement_plan_week', val)}
        // autoRequest={false}
        expandable={{
          onExpand: store.expandRow,
        }}
        searchbar={{
          items: [
            {
              label: '项目主办',
              dataIndex: 'sponsorUserId',
              element: <FounderSelect functionCode="selectfounder-buggetNotmonth" />,
            },
            {
              label: '业务类型',
              dataIndex: 'leaseType',
              element: <Select options={'leaseType'} />,
            },
          ],
        }}
        columns={[
          ...columns,
          canEdit && {
            title: '操作',
            dataIndex: 'action',
            width: 120,
            fixed: 'right',
            actions: (record, rowIndex) => {
              if (record.level === 1) return []
              return [
                editIndex !== rowIndex && {
                  name: '调整',
                  key: 'edit',
                  onClick: () => store.editItem({ rowIndex }),
                },
                editIndex === rowIndex && {
                  name: '取消',
                  key: 'cancel',
                  onClick: () => store.cancelEdit({ rowIndex }),
                },
                editIndex === rowIndex && {
                  name: '确定',
                  key: 'confirm',
                  onClick: () => store.confirmEdit({ record, rowIndex }),
                },
              ]
            },
          },
        ]}
        store={store?.projectTable}
        scroll={{ x: 2000, y: 500 }}
        rowKey="id"
      />
    </>
  )
})

export default ProjectTable
