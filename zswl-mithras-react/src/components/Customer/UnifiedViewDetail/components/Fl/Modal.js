import { observer } from '@zswl/admin'
import { Modal as ZModal } from '@zswl/components'
import store from './store'
import Modal from '../shared/Modal'

// 司法协助
const JudicialAidModule = observer(() => {
  const modalListProps = {
    column: 2,
    width: 800,
    title: '司法协助',
    layout: 'horizontal',
    labelCol: { span: 12 },
    disabled: true,
    items: [
      {
        title: '冻结执行事项',
        name: 'judicialassistevent',
        element: { type: 'input', placeholder: ' ', color: 'red' },
      },
      {
        title: '冻结期限起始日期',
        name: 'freezestartdate',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '冻结期限', name: 'freezematurity', element: { type: 'input', placeholder: ' ' } },
      {
        title: '续行冻结期限截止日期',
        name: 'keepfreezeenddate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '解冻执行事项',
        name: 'cancelfreezeevent',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '解冻执行裁定书文号',
        name: 'cancelfreezejudgeno',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '冻结执行裁定书文号',
        name: 'freezeno',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '冻结期限截止日期',
        name: 'freezeenddate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '续行冻结期限起始日期',
        name: 'keepfreezestartdate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '解冻执行法院',
        name: 'cancelfreezecourt',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '解冻执行通知书文号',
        name: 'cancelfreezenoticeno',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '解除冻结日期',
        name: 'cancelfreezedate',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '解冻公示日期',
        name: 'cancelfreezenoticedate',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '失效原因', name: 'invalidreason', element: { type: 'input', placeholder: ' ' } },
      { title: '失效时间', name: 'invaliddate', element: { type: 'input', placeholder: ' ' } },
      { title: '更新时间', name: 'updateTime', element: { type: 'input', placeholder: ' ' } },
      { name: 'id', hidden: true },
    ],
  }
  return <Modal modal={store.JudicialAidModuleStore} {...modalListProps} />
})
// 裁决文书
const InstrumentModule = observer(() => {
  const modalListProps = {
    column: 2,
    width: 800,
    title: '裁判文书',
    layout: 'horizontal',
    labelCol: { span: 7 },
    disabled: true,
    items: [
      {
        title: '案号',
        name: 'casenumber',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '案由', name: 'subjectmatter', element: { type: 'input', placeholder: ' ' } },
      { title: '当事人类型', name: 'partykind', element: { type: 'input', placeholder: ' ' } },
      { title: '败诉', name: 'verdict', element: { type: 'input', placeholder: ' ' } },
      { title: '案件类型', name: 'casetype', element: { type: 'input', placeholder: ' ' } },
      { title: '发布日期', name: 'pubdate', element: { type: 'input', placeholder: ' ' } },
      { title: '判决书类型', name: 'verdicttype', element: { type: 'input', placeholder: ' ' } },
      { title: '审理程序', name: 'procedures', element: { type: 'input', placeholder: ' ' } },
      { title: '审判结果', name: 'judgment', element: { type: 'input', placeholder: ' ' } },
      { title: '判决时间', name: 'refereedate', element: { type: 'input', placeholder: ' ' } },
      { title: '法院', name: 'courtname', element: { type: 'input', placeholder: ' ' } },
      { title: '省份', name: 'state', element: { type: 'input', placeholder: ' ' } },
      {
        title: '执行标的(万元)',
        name: 'executetarget',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '发布时间', name: 'inserttime', element: { type: 'input', placeholder: ' ' } },

      {
        title: '标题',
        name: 'infotitle',
        style: { color: 'red' },
        element: { type: 'textArea', placeholder: ' ', style: { color: 'red' } },
      },
      { name: 'id', hidden: true },
    ],
  }
  return <Modal modal={store.InstrumentModuleStore} {...modalListProps} />
})
//限制高消费
const ImitHighModule = observer(() => {
  const modalListProps = {
    column: 2,
    width: 800,
    title: '限制高消费',
    layout: 'horizontal',
    labelCol: { span: 7 },
    disabled: true,
    items: [
      {
        title: '被执行人姓名',
        name: 'peopleenforced',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '被执行人类型',
        name: 'peopleenforcedtype',
        element: { type: 'input', placeholder: ' ' },
      },
      {
        title: '被执行人ID',
        name: 'peopleenforcedid',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '性别', name: 'gender', element: { type: 'input', placeholder: ' ' } },
      { title: '立案时间', name: 'filingtime', element: { type: 'input', placeholder: ' ' } },
      { title: '案号', name: 'casenumber', element: { type: 'input', placeholder: ' ' } },
      { title: '执行法院', name: 'courtname', element: { type: 'input', placeholder: ' ' } },
      {
        title: '申请执行人',
        name: 'executeapplyname',
        element: { type: 'input', placeholder: ' ' },
      },
      { title: '执行人ID', name: 'executeapplycode', element: { type: 'input', placeholder: ' ' } },
      { title: '案由', name: 'subjectmatter', element: { type: 'input', placeholder: ' ' } },
      { title: '信息发布日期', name: 'infopubldate', element: { type: 'input', placeholder: ' ' } },
      { title: '发布时间', name: 'inserttime', element: { type: 'input', placeholder: ' ' } },
      { title: '修改时间', name: 'updatetime', element: { type: 'input', placeholder: ' ' } },
      { title: '原文', name: 'content', element: { type: 'textArea', placeholder: ' ' } },
      { name: 'id', hidden: true },
    ],
  }
  return <Modal modal={store.ImitHighModuleStore} {...modalListProps} />
})
export { JudicialAidModule, InstrumentModule, ImitHighModule }
