import { ClientSelect, FounderSelect, OrgSelect } from '@/components/Select'
import AmountRange from '@/components/AmountRange'
import {
  AmountEditable,
  AmountFormat,
  FiledFormat,
  InputEditable,
  MatchOptionColumn,
} from '@/components/Format'
import IconFont from '@/components/Icon'
import { rules, formatPercent, amountFormat } from '@/utils'
import { history } from '@zswl/admin'
import { App, Button, Select } from '@zswl/components'
import { Input, InputNumber, message, Tooltip } from 'antd'

const { TextArea } = Input
const toDetail = (clientId, clientType) => {
  if (!clientId || clientType === 'NO-CORPORATION') {
    message.info('请选择授信主体')
    return
  }
  history.push(
    `/customer/maintain/detail/${
      clientId?.value ?? clientId
    }?clientType=CORPORATION&flag=info&typeId=create`
  )
}

const ALL_COLUMNS = [
  {
    title: '授信名称',
    dataIndex: 'projName',
    // fixed: 'left',
    actions: ({ projName: name, id, projEstablishStatus, projSponsorUserId }) => [
      {
        name,
        to: `/credit/establish/detail/${id}?canEditFlag=${projEstablishStatus !== 'CLOSED'}`,
        className: 'z-single-line',
        // style: { width: 500 },
      },
    ],
    editable: InputEditable,
    render: (val) => <FiledFormat title={val}></FiledFormat>,
  },

  { title: '授信编号', width: 130, dataIndex: 'projCode', editable: InputEditable },
  { title: '业务类型', width: 100, dataIndex: 'bizType', matchOption: 'projEstablishBizType' },
  {
    title: '业务部门',
    dataIndex: 'bizDeptId',
    requiredMark: true,
    render: (val, { bizDeptName }) => <FiledFormat title={bizDeptName} />,
    width: 140,
    editable: {
      element: <OrgSelect />,
      rules: [rules.required()],
      functionCode: 'selectorgs-groupCreditReview',
    },
  },
  {
    title: '主办',
    dataIndex: 'projSponsorUserId',
    render: (val, { projSponsorUserName }) => <FiledFormat title={projSponsorUserName} />,
    requiredMark: true,
    width: 130,
    editable: {
      element: <FounderSelect />,
      rules: [rules.required()],
      functionCode: 'selectfounder-groupCreditReview',
    },
  },
  {
    title: '协办',
    width: 140,
    dataIndex: 'projCosponsorUserIds',
    render: (val, { projCosponsorUserNames }) => projCosponsorUserNames?.join(',') || '-',
    mode: 'multiple',
    editable: {
      element: <FounderSelect mode="multiple" />,
      functionCode: 'selectfounder-groupCreditReview',
    },
  },
  { title: '客户名称', width: 140, dataIndex: 'clientName' },
  // {
  //   title: '立项状态',
  //   dataIndex: 'projEstablishStatus',
  //   matchOption: 'projEstablishStatus',
  //   width: 140,
  // },
  {
    title: '审批类型',
    dataIndex: 'approvalType',
    matchOption: 'projEstablishApprovalType',
    render: (val) => App.matchOption('projEstablishApprovalType', val).label,
    width: 120,
    editable: {
      element: <Select options={'projEstablishApprovalType'} disabled />,
    },
  },
  {
    title: '审批状态',
    dataIndex: 'groupCreditEstablishProcessStatus',
    matchOption: 'groupCreditEstablishProcessStatus',
    width: 120,
  },
  {
    title: '创建时间',
    width: 180,
    dataIndex: 'createTime',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          createTime: undefined,
          createFrom: startDataTime?.format('yyyy-MM-DD'),
          createTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  { title: '更新时间', width: 180, dataIndex: 'updateTime' },
  {
    title: '授信主体',
    dataIndex: 'clientId',
    requiredMark: true,
    render: (val, { clientName, clientId, clientType }) => (
      <Tooltip title={clientName}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          {clientName}
          <IconFont
            type="icon-icon_link"
            onClick={() => toDetail(clientId, clientType)}
            className={'z-icon'}
          />
        </div>
      </Tooltip>
    ),
    editable: {
      element: <ClientSelect canJump={false} disabled />,
      functionCode: 'clientlist-groupCreditReview',
    },
    width: 280,
  },
  {
    title: '授信主体评级',
    dataIndex: 'clientRatingScore',
    render: (val, { clientRatingScore, clientRatingScoreId }) => {
      const goRat = () => {
        history.push(`/customer/customerRat/detail/${clientRatingScoreId}?canEditFlags=false`)
      }
      return (
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            width: '100%',
          }}
        >
          <span>{clientRatingScore}</span>
          {clientRatingScoreId && (
            <Button type="link" onClick={goRat}>
              评级报告
            </Button>
          )}
        </div>
      )
    },
    editable: false,
  },
  {
    title: '存量风险敞口(元)',
    dataIndex: 'clientRiskExposure',
    align: 'right',
    editable: false,
    render: (val) => <AmountFormat value={val} />,
    // editable: (val) =>
    //   AmountEditable(val, 'clientRiskExposure', { required: false, disabled: true }),
  },
  {
    title: '授信额度(元)',
    dataIndex: 'applyCreditAmount',
    requiredMark: true,
    align: 'right',
    render: (val) => <AmountFormat value={val} />,
    editable: (val) =>
      AmountEditable(val, 'applyCreditAmount', { required: true, disabled: false }),
    width: 160,
  },
  {
    title: '项目批复金额(元)',
    dataIndex: 'approvedAmount',
    align: 'right',
    editable: false,
    render: (val) => <AmountFormat value={val} />,
  },
  {
    title: '额度是否可循环',
    dataIndex: 'creditAmountLoop',
    matchOption: 'yesOrNo',
    requiredMark: true,
    editable: {
      options: 'yesOrNo',
      required: true,
      rules: [rules.required()],
    },
  },
  {
    title: '额度有效期(月)',
    dataIndex: 'validMonthCount',
    requiredMark: true,
    editable: {
      element: <InputNumber style={{ width: '100%' }} min={1} />,
      rules: [rules.required()],
    },
  },
  {
    title: '授信说明',
    dataIndex: 'projBackground',
    span: 2,
    render: (val) => val || '-',
    editable: (value) => ({
      element: <TextArea autoSize={{ minRows: 4, maxRows: 20 }} />,
    }),
  },
  {
    title: '业务部门负责人',
    requiredMark: true,
    dataIndex: 'bizDeptLeaderId',
    render: (val, { bizDeptLeaderName }) => <FiledFormat title={bizDeptLeaderName} />,
    editable: {
      element: <FounderSelect params={{ job: 'businesshead' }} />,
      rules: [rules.required()],
    },
  },
  {
    title: '业务分管领导',
    requiredMark: true,
    dataIndex: 'bizDivisionLeaderId',
    render: (val, { bizDivisionLeaderName }) => <FiledFormat title={bizDivisionLeaderName} />,
    editable: {
      element: <FounderSelect params={{ job: 'leaderincharge' }} />,
      rules: [rules.required()],
      functionCode: 'selectfounder-groupCreditReview',
    },
  },
  {
    title: '风控经理',
    requiredMark: true,
    dataIndex: 'riskControlManagerId',
    render: (val, { riskControlManagerName }) => <FiledFormat title={riskControlManagerName} />,
    editable: {
      element: <FounderSelect params={{ job: 'riskmanager' }} />,
      rules: [rules.required()],
    },
  },
  {
    title: '法务经理',
    requiredMark: true,
    span: 1,
    dataIndex: 'legalManagerUserId',
    render: (val, { legalManagerName }) => <FiledFormat title={legalManagerName} />,
    editable: {
      element: <FounderSelect params={{ job: 'legalmanager' }} />,
      rules: [rules.required()],
      functionCode: 'selectfounder-groupCreditReview',
    },
  },
  {
    title: '授信金额',
    dataIndex: 'applyCreditAmount',
    requiredMark: true,
    align: 'right',
    width: 160,
    itemProps: {
      transform: (val) => {
        const [start, end] = val || []
        return {
          applyCreditAmount: undefined,
          creditAmountFrom: start && start * 10000,
          creditAmountTo: end && end * 10000,
        }
      },
    },
    render: (val) => amountFormat(formatPercent(val)),
    editable: {
      element: <AmountRange />,
    },
  },
  {
    title: '风控行业分类',
    dataIndex: 'riskControlIndustryClassify',
    span: 1,
    editable: false,
    render: (val) => (
      <FiledFormat title={App.matchOption('riskControlIndustryClassify', val).label} />
    ),
  },
  {
    title: '立项状态',
    dataIndex: 'groupCreditEstablishStatus',
    matchOption: 'recordStatus',
  },
  {
    title: '评审状态',
    dataIndex: 'groupCreditReviewStatus',
    matchOption: 'recordStatus',
  },
]

export default ALL_COLUMNS
