import { observer } from '@zswl/admin'
import { Modal as ZModal } from '@zswl/components'
import store from './store'
import Modal from '../shared/Modal'
import moment from 'moment'

const Timer = ({ value }) => {
  return <div style={{ marginTop: 5 }}>{moment(value).format('YYYY-MM-DD HH:mm:ss')}</div>
}

// 重大税收违法详情
const JudicialAidModule = observer(() => {
  const modalListProps = {
    column: 2,
    width: 800,
    title: '重大税收违法详情',
    layout: 'horizontal',
    labelCol: { span: 8 },
    disabled: true,
    items: [
      { title: '纳税人识别号', name: 'taxnumber', element: { type: 'input', placeholder: ' ' } },
      { title: '经营地点', name: 'businessplace', element: { type: 'input', placeholder: ' ' } },
      {
        title: '移送公安情况',
        name: 'policetransfer',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '所属税务机关', name: 'taxorg', element: { type: 'input', placeholder: ' ' } },
      { title: '检查机关', name: 'checkorg', element: { type: 'input', placeholder: ' ' } },
      { title: '公示税务机关', name: 'publictaxorg', element: { type: 'input', placeholder: ' ' } },
      { title: '发生日期', name: 'occurdate', element: { type: 'input', placeholder: ' ' } },
      {
        title: '违法事实开始时间',
        name: 'illefactstartdate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '违法事实结束时间',
        name: 'illefactenddate',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '发布时间', name: 'inserttime', element: { type: 'input', placeholder: ' ' } },
      { title: '更新时间', name: 'updatetime', element: { type: 'input', placeholder: ' ' } },
      {
        title: '案件性质',
        name: 'casenature',
        element: { type: 'textArea', placeholder: ' ' },
        col: 24,
        labelCol: { span: 4 },
        wrapperCol: { span: 24 },
      },
      {
        title: '违法事实',
        name: 'illegalfact',
        element: { type: 'textArea', placeholder: ' ' },
        col: 24,
        labelCol: { span: 4 },
        wrapperCol: { span: 24 },
      },
      {
        title: '法律依据及处罚',
        name: 'legalbasispunishment',
        element: { type: 'textArea', placeholder: ' ' },
        col: 24,
        labelCol: { span: 4 },
        wrapperCol: { span: 24 },
      },
      { name: 'id', hidden: true },
    ],
  }
  return <Modal modal={store.JudicialAidModuleStore} {...modalListProps} />
})

// 担保事件详情
const InstrumentModule = observer(() => {
  const modalListProps = {
    column: 2,
    width: 800,
    title: '担保事件详情',
    layout: 'horizontal',
    labelCol: { span: 8 },
    disabled: true,
    items: [
      {
        title: '涉及对象类型',
        name: 'eventobjecttype',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '涉及对象名称',
        name: 'eventobjectname',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '涉及对象角色',
        name: 'eventobjectrole',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '与本公司关系',
        name: 'objectassociation',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '信息来源', name: 'InfoSource', element: { type: 'input', placeholder: ' ' } },
      {
        title: '担保业务类型',
        name: 'guarantbusitype',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '事件类型', name: 'eventtype', element: { type: 'input', placeholder: ' ' } },
      {
        title: '担保行为',
        name: 'guaranteebehavior',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '事件内容', name: 'eventcontent', element: { type: 'input', placeholder: ' ' } },
      { title: '事件进程', name: 'eventprocedure', element: { type: 'input', placeholder: ' ' } },
      { title: '货币单位', name: 'currencyunit', element: { type: 'input', placeholder: ' ' } },
      { title: '担保原因', name: 'guaranteereason', element: { type: 'input', placeholder: ' ' } },
      {
        title: '担保余额(元)',
        name: 'guaranteebalance',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '最新担保余额(元)',
        name: 'latestguaranteesum',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '担保方式', name: 'cguaranteemethod', element: { type: 'input', placeholder: ' ' } },
      {
        title: '担保截止日',
        name: 'cguaranteenddate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '借款方性质',
        name: 'lenderattribute',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '借款方式', name: 'lender', element: { type: 'input', placeholder: ' ' } },
      { title: '解除日期', name: 'relievedate', element: { type: 'input', placeholder: ' ' } },
      { title: '解除方式', name: 'relievemethod', element: { type: 'input', placeholder: ' ' } },
      { title: '是否通知', name: 'ifoverdue', element: { type: 'input', placeholder: ' ' } },
      { title: '逾期时间(月)', name: 'overduetime', element: { type: 'input', placeholder: ' ' } },
      {
        title: '担保逾期金额(元)',
        name: 'overduesum',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '逾期描述', name: 'overduetimedesc', element: { type: 'input', placeholder: ' ' } },
      { title: '是否违约', name: 'ifviolation', element: { type: 'input', placeholder: ' ' } },
      { title: '发布时间', name: 'inserttime', element: { type: 'input', placeholder: ' ' } },
      { title: '更新时间', name: 'updatetime', element: { type: 'input', placeholder: ' ' } },
      { name: 'id', hidden: true },
    ],
  }
  return <Modal modal={store.InstrumentModuleStore} {...modalListProps} />
})

// 动产抵押详情
const ImitHighModule = observer(() => {
  const modalListProps = {
    column: 2,
    width: 1000,
    title: '动产抵押详情',
    layout: 'horizontal',
    labelCol: { span: 8 },
    disabled: true,
    items: [
      {
        title: '动产抵押登记编号',
        name: 'impawnRegNumber',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '登记日期', name: 'mortgageRegDate', element: { type: 'input', placeholder: ' ' } },
      { title: '登记机关', name: 'regOrg', element: { type: 'input', placeholder: ' ' } },
      {
        title: '公示日期',
        name: 'mortgagePublDate',
        element: { type: 'datePicker', placeholder: ' ' },
      },
      { title: '被担保债券种类', name: 'type', element: { type: 'input', placeholder: ' ' } },
      {
        title: '被担保债券数额(万)',
        name: 'securedPrincipalClaimsBalance',
        element: { type: 'input', placeholder: ' ' },
      },

      { title: '注销日期', name: 'cancelDate', element: { type: 'input', placeholder: ' ' } },
      { title: '注销原因', name: 'cancelReason', element: { type: 'input', placeholder: ' ' } },
      {
        title: '履行期限起始日',
        name: 'performanceStartDate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '履行期限截止日',
        name: 'performanceEndDate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '债务人履行债务的期限',
        name: 'pefper_to',
        element: { type: 'input', placeholder: ' ' },
      },

      {
        title: '担保范围',
        name: 'overviewScope',
        element: { type: 'textArea', placeholder: ' ' },
        col: 24,
        labelCol: { span: 4 },
        wrapperCol: { span: 24 },
      },
      {
        title: '被担保主体债权备注',
        name: 'overviewRemark',
        element: { type: 'textArea', placeholder: ' ' },
        col: 24,
        labelCol: { span: 4 },
        wrapperCol: { span: 24 },
      },
      { name: 'id', hidden: true },
    ],
  }
  return <Modal modal={store.ImitHighModuleStore} {...modalListProps} />
})

export { JudicialAidModule, InstrumentModule, ImitHighModule }
