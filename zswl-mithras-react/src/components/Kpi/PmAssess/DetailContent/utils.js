import { InputNumber, Tooltip } from 'antd'
import { QuestionCircleOutlined } from '@ant-design/icons'
import { rules } from '@/utils'

const tipMap = {
  部门业绩完成率: (
    <div>
      <div>指标定义：取部门投放完成率*50%+部门利润完成率*50%得分。</div>
      <div>考核标准：100%及以上得满分，100%以下线性插值得分，0%以下得0分。</div>
    </div>
  ),
  称职条件: (
    <div>
      <div>指标定义：称职条件完成率。</div>
      <div>考核标准：100%及以上得满分，100%以下线性插值得分，0%以下得0分。</div>
    </div>
  ),

  营销渠道建设: (
    <div>
      <div>指标定义：客户拜访数：每季度拜访新客户不低于12户（包括电访），取四季度平均分。</div>
      <div>考核标准：指标不低于12户得满分，指标为0得0分，区间取线性插值得分。</div>
    </div>
  ),
  租后管理: (
    <div>
      <div>
        1.按公司制度规定的要求完成客户租后检查并形成报告（包括完成时间、完成质量等符合要求），完成时间延迟
        1 个月或以上的扣 2 分/客户，每缺一个客户扣 2 分，每缺一个实质性担保人/抵押物检查扣 1 分。
      </div>
      <div>
        2.按制度规定或有权决定机构出具的意见完成对风险预警、风险项目租后管理工作及相关报告，如未以上要求完成的，1
        次扣 2 分。
      </div>
      <div>
        3.项目投放后，全部材料齐全并按归档时间归档。按制度规定时间完成归档，如未完成，超 1 次扣 1
        分。4.可倒扣至-5 分。
      </div>
    </div>
  ),
  '关注类业务占比(期末)': (
    <div>
      <div>指标定义：关注类业务余额/部门全部业务余额。</div>
      <div>考核标准：占比为0得10分，占比≥5%得0分，区间线性插值得分。</div>
    </div>
  ),
  不良率: (
    <div>
      <div>
        指标定义：主要考核不良资产比率，不良资产比率=个人年末不良资产总额/个人年末资产总额。
      </div>
      <div> 考核标准：不良率＜1%得满分；不良率≥1% 得0分。</div>
    </div>
  ),
  逾期率: (
    <div>
      <div>
        指标定义：主要考核租金逾期率，租金逾期率=个人年末逾期租金总额/个人年末全部业务余额。
      </div>
      <div>考核标准：逾期率＜2.5%，按比例得10-0分逾期率≥2.5% 得0分。</div>
    </div>
  ),
}

export const TABLE_TITLE_DATA = [
  {
    id: 1111,
    userName: '指标名称',
    deptPerformanceScore: '部门业绩完成率',
    competentScore: '称职条件',
    marketingChannelScore: '营销渠道建设',
    afterLeaseScore: '租后管理',
    focusRadioScore: '关注类业务占比(期末)',
    overdueRateScore: '逾期率',
    defectRateScore: '不良率',
  },
  {
    id: 1112,
    userName: '满分分值',
    deptPerformanceScore: '35',
    competentScore: '10',
    marketingChannelScore: '15',
    afterLeaseScore: '10',
    focusRadioScore: '10',
    overdueRateScore: '10',
    defectRateScore: '10',
  },
]

const RenderTip = ({ title }) => {
  return (
    <Tooltip overlayStyle={{ maxWidth: 700 }} title={title}>
      <QuestionCircleOutlined style={{ color: '#BCBDC0' }} />
    </Tooltip>
  )
}

export const render = (value, record, rowIndex) => {
  if (rowIndex === 0) {
    return (
      <div>
        {value}
        &nbsp;
        <RenderTip title={tipMap[value]}></RenderTip>
      </div>
    )
  }
  return value
}

export const editable = (record, rowIndex, editIndex) => {
  return editIndex === rowIndex && rowIndex > 1
    ? {
        element: <InputNumber min={0} max={9999} precision={2} style={{ width: 180 }} />,
        required: true,
        rules: [rules.required('请输入')],
      }
    : false
}
