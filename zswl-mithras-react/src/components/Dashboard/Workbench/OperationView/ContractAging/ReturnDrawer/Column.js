import { MatchOptionColumn, InputColumn, AmountColumn, DateColumn } from '@/components/Format'
import { founderSelect, orgSelect, clientSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'

export const ALL_COLUMNS = [
  InputColumn({
    title: '流程ID',
    dataIndex: 'flowId',
    actions({ flowId }) {
      return [
        {
          name: flowId,
          onClick: () => {
            const search = { processInstanceIdList: [flowId] }
            window.open(`/process/query?search=${JSON.stringify(search)}`)
          },
        },
      ]
    },
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
  }),
  InputColumn({
    title: '项目编号',
    dataIndex: 'projCode',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
  }),
  clientSelect(),
  founderSelect(),
  orgSelect({
    mode: 'multiple',
  }),
  MatchOptionColumn({
    title: '业务分类',
    dataIndex: 'businessGroup',
    matchOption: 'businessGroupEnum',
    mode: 'multiple',
  }),
  MatchOptionColumn({
    title: '业务模式',
    dataIndex: 'businessModel',
    matchOption: 'contractBusinessModelEnum',
    mode: 'multiple',
  }),
  InputColumn({
    title: '申请时间',
    dataIndex: 'applyTime',
  }),
  DateColumn({
    title: '结束时间',
    dataIndex: 'endTime',
    search: true,
  }),
  InputColumn({
    title: '运营退回次数',
    dataIndex: 'yunYingReturnCount',
  }),
  founderSelect({
    params: { job: 'yunYingGuanLi,operationManagement' },
    title: '运营经办',
    dataIndex: 'yunYingGuanLiId',
    render: (value, { yunYingGuanLiName }) => yunYingGuanLiName ?? '-',
  }),
  founderSelect({
    params: { job: 'yunYingGuanLiReview,operationManagementReview' },
    title: '运营复核',
    dataIndex: 'yunYingGuanLiReviewId',
    render: (value, { yunYingGuanLiReviewName }) => yunYingGuanLiReviewName ?? '-',
  }),

  InputColumn({
    title: '退回原因',
    dataIndex: 'reasonReturn',
  }),
]
