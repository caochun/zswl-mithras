import { observer } from '@zswl/admin'
import { Anchor, Row, Col, Typography } from 'antd'
import { Table } from '@zswl/components'
import CustomCard from '../shared/Card/index'
import styles from './styles.less'
import SecondTow from './svgs/SecondTow.svg'
import SafetyCertificateOutlinedTow from './svgs/SafetyCertificateOutlinedTow.svg'
import JudicialAidTow from './svgs/JudicialAidTow.svg'
import FileTextOutlinedTow from './svgs/FileTextOutlinedTow.svg'
import ImposeTow from './svgs/ImposeTow.svg'
import ImposeDy from './svgs/ImposeDy.svg'
import { saveServer } from '@/utils'

import store from './store'
import { JudicialAidModule, InstrumentModule, ImitHighModule } from './Modal'

const { Link } = Anchor
const { Title } = Typography

const sections = [
  { id: 'businessAbnormal', title: '1.经营异常', dataKey: 'businessAbnormal' },
  { id: 'administrativePunishment', title: '2.行政处罚', dataKey: 'administrativePunishment' },
  { id: 'seriousViolation', title: '3.严重违法', dataKey: 'seriousViolation' },
  { id: 'majorTaxViolation', title: '4.重大税收违法', dataKey: 'majorTaxViolation' },
  { id: 'guaranteeEvents', title: '5.担保事件', dataKey: 'guaranteeEvents' },
  { id: 'chattelMortgage', title: '6.动产抵押', dataKey: 'chattelMortgage' },
]

const RenderTable = observer(({ dataKey }) => {
  const columnsMap = {
    // 经营异常
    businessAbnormal: [
      { title: '列入异常原因', dataIndex: 'inclusionReason' },
      { title: '列入异常日期', dataIndex: 'inclusionDate' },
      { title: '移除异常原因', dataIndex: 'removalReason' },
      { title: '移除异常日期', dataIndex: 'removalDate' },
      { title: '更新时间', dataIndex: 'updateTime' },
    ],
    // 行政处罚
    administrativePunishment: [
      { title: '处罚文书号', dataIndex: 'documentNumber' },
      { title: '违法行为类型', dataIndex: 'illegalType' },
      { title: '处罚内容', dataIndex: 'content' },
      { title: '处罚决定日期', dataIndex: 'decisionDate' },
      { title: '更新时间', dataIndex: 'updateTime' },
      { title: '是否历史', dataIndex: 'isHistory' },
    ],
    // 严重违法
    seriousViolation: [
      { title: '列入原因', dataIndex: 'inclusionReason' },
      { title: '列入日期', dataIndex: 'inclusionDate' },
      { title: '移除原因', dataIndex: 'removalReason' },
      { title: '移除日期', dataIndex: 'removalDate' },
      { title: '类型', dataIndex: 'type' },
      { title: '更新时间', dataIndex: 'updateTime' },
    ],
    // 重大税收违法
    majorTaxViolation: [
      {
        title: '违法事实',
        dataIndex: 'fact',
        render: (val) => <a onClick={() => store.JudicialAidModuleStore.open()}>{val}</a>,
      },
      { title: '案件性质', dataIndex: 'caseNature' },
      { title: '发生日期', dataIndex: 'occurrenceDate' },
      { title: '更新时间', dataIndex: 'updateTime' },
      {
        title: '操作',
        render: () => <a onClick={() => store.JudicialAidModuleStore.open()}>详情</a>,
      },
    ],
    // 担保事件
    guaranteeEvents: [
      { title: '担保对象', dataIndex: 'guaranteeObject' },
      { title: '与本公司关系', dataIndex: 'relation' },
      { title: '担保业务类型', dataIndex: 'guaranteeBusinessType' },
      { title: '担保行为', dataIndex: 'guaranteeBehavior' },
      { title: '最新担保额(元)', dataIndex: 'latestGuaranteeAmount' },
      { title: '是否通知', dataIndex: 'notified' },
      { title: '更新时间', dataIndex: 'updateTime' },
      {
        title: '操作',
        render: () => <a onClick={() => store.InstrumentModuleStore.open()}>详情</a>,
      },
    ],
    // 动产抵押
    chattelMortgage: [
      { title: '动产抵押登记编号', dataIndex: 'registrationNumber' },
      { title: '被担保债权种类', dataIndex: 'debtType' },
      { title: '被担保债权范围', dataIndex: 'debtScope' },
      { title: '被担保债务数额(万元)', dataIndex: 'amount' },
      { title: '登记日期', dataIndex: 'registrationDate' },
      {
        title: '操作',
        render: () => <a onClick={() => store.ImitHighModuleStore.open()}>详情</a>,
      },
    ],
  }

  const columns = columnsMap[dataKey] || []

  return <Table         columnsFilter={'Jy_copy_1'}
  onFilter={(key,val) => saveServer('Jy_copy_1',val)} store={store[dataKey]} columns={columns} rowKey="id" pagination={false} bordered />
})

function CardContainer() {
  return (
    <>
      <div
        style={{
          display: 'flex',
          gap: '16px',
          padding: '0',
          justifyContent: `space-between`,
          marginBottom: 10,
        }}
      >
        <CustomCard icon={<SecondTow />} red={true} title="经营异常" current={1} total={3} />
        <CustomCard
          icon={<SafetyCertificateOutlinedTow />}
          title="行政处罚"
          current={4}
          total={20}
        />
        <CustomCard icon={<JudicialAidTow />} title="严重违法" current={0} total={1} />
        <CustomCard icon={<FileTextOutlinedTow />} title="重大税收违法" current={1} />
        <CustomCard icon={<ImposeTow />} title="担保事件" current={0} />
        <CustomCard icon={<ImposeDy />} title="动产抵押" current={0} />
      </div>
    </>
  )
}

function Index() {
  return (
    <Row gutter={20} wrap={false}>
      <Col>
        <Anchor>
          {sections.map((section) => (
            <Link key={section.id} href={`#${section.id}`} title={section.title} />
          ))}
        </Anchor>
      </Col>
      <Col flex={1} style={{ paddingLeft: 37 }}>
        <CardContainer />
        {sections.map((section) => (
          <div id={section.id} key={section.id} style={{ marginBottom: 30 }}>
            <Title level={5}>
              {section.title} <span className={styles.titleSpans}>数据来源：聚源</span>
            </Title>
            <RenderTable key={section.id} dataKey={section.dataKey} />
            {/* {renderTable(section.dataKey)} */}
          </div>
        ))}
      </Col>
      <JudicialAidModule />
      <InstrumentModule />
      <ImitHighModule />
    </Row>
  )
}

export default observer(Index)
