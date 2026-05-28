import { getQuery, observer } from '@zswl/admin'
import {
  AmountColumn,
  AmountEditable,
  DateColumn,
  DatePickerEditable,
  FounderColumn,
  MatchOptionColumn,
  TextAreaColumn,
  TextAreaEditable,
} from '@/components/Format'
import { Select, Table } from '@zswl/components'
import { rules, saveServer, amountFormat } from '@/utils'
import { highPrecisionMultiply, formatPercent } from '@/utils/base'
import './index.less'

const INIT_FORMAT = 10000 * 10000
const PARENT_FORMAT = 10000

const ProjectTable = observer(({ store, canEdit, taskActivityId, isFinalPlanEventFlow }) => {
  // 运营负责人
  const isYunyingManager = ['userTask_headofyyglb'].includes(taskActivityId)
  // 资金负责人
  const isZiJinManager = ['userTask_headofzj', 'userTask_fundManager'].includes(taskActivityId)
  // 财务经理 、财务负责人
  const isCaiWuManager = ['userTask_financialmanager'].includes(taskActivityId)

  const baseCanEdit = isFinalPlanEventFlow ? ['userTask_financeManager', 'userTask_startUser'].includes(taskActivityId) : canEdit
  const { editIndex } = store

  const AmountEditableRow = ({ title, dataIndex, initFormat, suffix, required, search, canEdit, onChange, zijinCode = false }) => {
    return AmountColumn({
      title,
      dataIndex,
      initFormat,
      suffix,
      required,
      search,
      editable: (record, rowIndex) => {
        return (record.level !== 1 && editIndex === rowIndex && canEdit) || zijinCode
          ? AmountEditable(record, dataIndex, {
              disabled: false,
              required,
              initFormat,
              inputConfig: { onChange, rowIndex, checkPlanPayAmount: (val) => store.checkPlanPayAmount(record.id, val) },
            })
          : false
      },
    })
  }
  const DateEditableRow = ({ title, dataIndex, search, canEdit }) => {
    return DateColumn({
      title,
      dataIndex,
      search,
      editable: (record, rowIndex) => (record.level !== 1 && editIndex === rowIndex && canEdit ? DatePickerEditable(record, dataIndex) : false),
    })
  }
  const MatchOptionEditableRow = ({ title, dataIndex, matchOption, required, search, canEdit, width = 140, yunyingCode = false, zijinCode = false, onChange }) => {
    return MatchOptionColumn({
      title,
      dataIndex,
      matchOption,
      width,
      search,
      editable: (record, rowIndex) =>
        (record.level !== 1 && editIndex === rowIndex && canEdit) || yunyingCode || zijinCode
          ? {
              element: (
                <Select
                  options={matchOption}
                  onChange={(val) => {
                    ;(yunyingCode || zijinCode) && onValuesChange({ id: record.id, [dataIndex]: val })
                    onChange && onChange(val, record)
                  }}
                />
              ),
              rules: required && [rules.required('请选择')],
              requiredMark: required,
            }
          : false,
    })
  }
  const isFormApproval = getQuery('typeId') == 'approval'

  const columns = [
    { title: '部门名称', dataIndex: 'belongDeptName', fixed: 'left' },
    { title: '项目编号/合同编号', dataIndex: 'projCode', width: 200, fixed: 'left' },
    { title: '客户名称', dataIndex: 'clientName', fixed: 'left' },

    MatchOptionEditableRow({
      title: '业务类型',
      dataIndex: 'leaseType',
      matchOption: 'leaseType',
      required: true,
      search: isFormApproval,
      canEdit: baseCanEdit,
      width: 160,
    }),
    {
      title: '项目地区',
      dataIndex: 'provinceName',
      width: 180,
      render: (text, record) => `${record.provinceName || ''} ${record.cityName || ''} ${record.districtName || ''}`,
    },
    MatchOptionColumn({
      title: '行业分类',
      dataIndex: 'projectClassify',
      matchOption: 'projectClassify',
      search: isFormApproval,
      canEdit: baseCanEdit,
    }),
    MatchOptionColumn({
      title: 'FTP行业分类',
      dataIndex: 'ftpIndustryCategory',
      matchOption: 'ftpIndustryCategoryEnum',
      search: isFormApproval,
      canEdit: baseCanEdit,
    }),
    MatchOptionEditableRow({
      title: '是否3A评级',
      dataIndex: 'arate',
      matchOption: 'yesOrNoNumberEnum',
      required: true,
      canEdit: baseCanEdit,
      width: 120,
    }),
    MatchOptionEditableRow({
      title: '管理层级',
      dataIndex: 'manageLevel',
      matchOption: 'budgetPlanManageLevelPlanEnum',
      required: true,
      canEdit: baseCanEdit,
      width: 130,
    }),
    {
      title: '业务来源',
      dataIndex: 'projSource',
      matchOption: 'projSourceType',
    },
    FounderColumn({
      title: '项目主办',
      dataIndex: 'sponsorUserId',
      search: isFormApproval,
      renderField: 'sponsorUserName',
      editable: false,
    }),
    AmountColumn({ title: '授信金额(万元)', dataIndex: 'creditAmount', initFormat: INIT_FORMAT }),
    AmountColumn({ title: '已投放金额(万元)', dataIndex: 'paidAmount', initFormat: INIT_FORMAT }),
    AmountEditableRow({
      title: '拟投放金额(万元)',
      dataIndex: 'planPayAmount',
      initFormat: INIT_FORMAT,
      required: true,
      canEdit: baseCanEdit,
      onChange: (val, { dataIndex, rowIndex }) => {
        const list = store?.projectTable.getList().map((item, i) => {
          if (i === rowIndex) {
            return {
              ...item,
              fundPlanPayAmount: val.replace(/,/g, ''),
            }
          }
          return item
        })
        store.projectTable.setList(list)
      },
    }),
    DateEditableRow({
      title: '投放日',
      dataIndex: 'planPayDate',
      search: isFormApproval,
      canEdit: baseCanEdit,
    }),
    AmountEditableRow({
      title: '租赁期限(月)',
      dataIndex: 'termMonth',
      initFormat: 1,
      required: true,
      canEdit: baseCanEdit,
    }),
    AmountEditableRow({
      title: '项目保证金(万元)',
      dataIndex: 'deposit',
      initFormat: INIT_FORMAT,
      required: true,
      canEdit: baseCanEdit,
    }),
    AmountEditableRow({
      title: '项目咨询费(万元)',
      dataIndex: 'consultingFee',
      initFormat: INIT_FORMAT,
      required: true,
      canEdit: baseCanEdit,
    }),
    AmountEditableRow({
      title: '合同利率(%)',
      dataIndex: 'contractInterestRate',
      initFormat: PARENT_FORMAT,
      suffix: '%',
      required: true,
      canEdit: baseCanEdit,
    }),
    AmountEditableRow({
      title: 'IRR(%)',
      dataIndex: 'irr',
      initFormat: PARENT_FORMAT,
      required: true,
      canEdit: baseCanEdit,
    }),
    AmountEditableRow({
      title: 'FTP(%)',
      dataIndex: 'ftp',
      initFormat: PARENT_FORMAT,
      required: isCaiWuManager,
      canEdit: baseCanEdit,
    }),
    AmountColumn({
      title: '价差',
      dataIndex: 'irrFtpDiff',
      initFormat: PARENT_FORMAT,
      required: true,
    }),
    MatchOptionEditableRow({
      title: '本周项目计划节点',
      dataIndex: 'planActionThisWeek',
      matchOption: 'budgetPlanPayProjectStageEnum',
      required: true,
      canEdit: baseCanEdit,
    }),
    MatchOptionEditableRow({
      title: '本周项目实际节点',
      dataIndex: 'actualActionThisWeek',
      matchOption: 'budgetPlanPayProjectStageEnum',
      required: true,
      canEdit: baseCanEdit,
    }),
    MatchOptionEditableRow({
      title: '下周项目计划节点',
      dataIndex: 'planActionNextWeek',
      matchOption: 'budgetPlanPayProjectStageEnum',
      required: true,
      canEdit: baseCanEdit,
    }),
    MatchOptionEditableRow({
      title: '项目进展状态',
      dataIndex: 'projectProgress',
      matchOption: 'budgetPlanPayProjectStageEnum',
      required: true,
      canEdit: baseCanEdit,
    }),
    TextAreaColumn({
      title: '存在问题及建议',
      dataIndex: 'suggestion',
      width: 200,
      editable: (record, rowIndex) => (editIndex === rowIndex && canEdit ? TextAreaEditable(record, 'suggestion') : false),
    }),
    MatchOptionEditableRow({
      title: '系统流程状态',
      dataIndex: 'rzyProcessStage',
      matchOption: 'allProcessTypeEnum',
      width: 200,
      required: false,
      canEdit: baseCanEdit,
    }),
    MatchOptionEditableRow({
      title: '运营进度反馈',
      dataIndex: 'yunyingFeedback',
      matchOption: 'budgetPlanPayYunYingFeedbackEnum',
      width: 180,
      required: isYunyingManager,
      canEdit: isYunyingManager,
      yunyingCode: isYunyingManager,
    }),
    MatchOptionEditableRow({
      title: '运营优先级',
      dataIndex: 'yunyingPriority',
      matchOption: 'yunyingPriorityEnum',
      width: 200,
      required: isYunyingManager,
      canEdit: isYunyingManager,
      yunyingCode: isYunyingManager,
    }),
    MatchOptionEditableRow({
      title: '是否纳入资金计划',
      dataIndex: 'bringIntoFundPlan',
      matchOption: 'budgetPlanPayFundPlanEnum',
      required: isZiJinManager,
      // canEdit: isZiJinManager || isCaiWuManager,
      canEdit: isZiJinManager,
      zijinCode: isZiJinManager,
      onChange: (val, record) => {
        if (['BACKUP', 'NOT_BRING_INTO'].includes(val)) {
          store.projectTable.setEditorValue(record.id, 'fundPlanPayAmount', '0.00')
          store.saveYunyingData({ id: record.id, fundPlanPayAmount: 0 })
        }
      },
    }),
    isZiJinManager
      ? AmountEditableRow({
          title: '资金拟投放金额(万元)',
          dataIndex: 'fundPlanPayAmount',
          initFormat: PARENT_FORMAT,
          required: isZiJinManager,
          // canEdit: isZiJinManager || isCaiWuManager
          canEdit: isZiJinManager,
          zijinCode: isZiJinManager,
          onChange: (val, { dataSource, dataIndex }) => {
            store.saveYunyingData({ id: dataSource.id, [dataIndex]: highPrecisionMultiply(val.replace(/,/g, '')).split('.')[0] })
          },
        })
      : {
          title: '资金拟投放金额（万元）',
          dataIndex: 'fundPlanPayAmount',
          canEdit: baseCanEdit,
          required: isZiJinManager,
          canEdit: isZiJinManager,
          render: (text, record) => {
            return amountFormat(record.fundPlanPayAmount)
          },
        },
  ]

  const onValuesChange = (value) => {
    store.saveYunyingData(value)
  }

  return (
    <div className="ro-mothplan">
      <Table
        selectable
        // resizable
        columnsFilter="placement_plan"
        onFilter={(key, val) => saveServer('placement_plan', val)}
        expandable={{
          onExpand: store.expandRow,
        }}
        columns={[
          ...columns,
          !isYunyingManager &&
            !isZiJinManager &&
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
        columnWidth={140}
        store={store?.projectTable}
        scroll={{ x: 2000, y: true }}
        rowKey="id"
      />
    </div>
  )
})

export default ProjectTable
