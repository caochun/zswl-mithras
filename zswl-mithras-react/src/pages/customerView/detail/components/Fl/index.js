import { observer, http } from '@zswl/admin'
import { Anchor, Row, Col, Typography, Space } from 'antd'
import moment from 'moment' // 添加 moment 导入
import CustomCard from '../shared/Card/index'
import styles from './styles.less'
import Second from './svgs/Second.svg'
import SafetyCertificateOutlined from './svgs/SafetyCertificateOutlined.svg'
import JudicialAid from './svgs/JudicialAid.svg'
import FileTextOutlined from './svgs/FileTextOutlined.svg'
import Impose from './svgs/Impose.svg'
import store from './store'
import { Table } from '@zswl/components'
import { JudicialAidModule, InstrumentModule, ImitHighModule } from './Modal'
import { useEffect } from 'react'
import { FilterOutlined } from '@ant-design/icons'
import { saveServer } from '@/utils'

const { Link } = Anchor
const { Title } = Typography

const sections = [
  { id: 'caseInfo', title: '1.立案信息', dataKey: 'caseInformations' },
  { id: 'enforcementInfo', title: '2.被执行信息', dataKey: 'executedInformations' },
  { id: 'judicialAssistance', title: '3.司法协助', dataKey: 'judicialAssistances' },
  { id: 'judgmentDocuments', title: '4.裁判文书', dataKey: 'judgmentDocuments' },
  { id: 'consumptionRestriction', title: '5.限制高消费', dataKey: 'restrictionHighConsumptions' },
]

const RenderTable = observer(({ dataKey }) => {
  const table = store[dataKey]
  const data = store.allData?.[dataKey] ?? []
  // 去重 并转化成 数组
  const filters = data
    .map((item) => ({ text: item.caseReason, value: item.caseReason }))
    .filter((item, index, self) => self.findIndex((t) => t.value === item.value) === index)
    .filter((item) => item.value)

  const columnsMap = {
    caseInformations: [
      {
        title: '案号',
        dataIndex: 'caseNumber',
        render: (text) => text || '-',
      },
      {
        title: '案件状态',
        dataIndex: 'caseStatus',
        render: (text) => text || '-',
      },
      {
        title: '案由',
        dataIndex: 'caseReason',
        render: (text) => text || '-',
        filterIcon: (
          <Space>
            <FilterOutlined />
            <span style={{ fontSize: 12, color: 'blue' }}>筛选</span>
          </Space>
        ),
        filterSearch: true,
        onFilter: (value, record) => {
          return record.caseReason?.indexOf(value) === 0
        },
        filters,
      },
      {
        title: '立案日期',
        dataIndex: 'caseDate',
        render: (text) => {
          if (!text) return '-'
          const timestamp = String(text).length === 10 ? text * 1000 : text
          const date = moment(Number(timestamp))
          return date.isValid() ? date.format('YYYY-MM-DD') : '-'
        },
      },
    ],
    executedInformations: [
      {
        title: '案号',
        dataIndex: 'caseNo',
        render: (text) => text || '-',
      },
      {
        title: '执行标的(元)',
        dataIndex: 'target',
        render: (text) => text || '-',
      },
      {
        title: '立案时间',
        dataIndex: 'caseDate',
        render: (text) => text || '-',
      },
    ],
    judicialAssistances: [
      {
        title: '通知文书号',
        dataIndex: 'noticeDocumentNumber',
        render: (text) => text || '-',
      },
      {
        title: '股权数额',
        dataIndex: 'stockAmount',
        render: (text) => text || '-',
      },
      {
        title: '状态',
        dataIndex: 'status',
        render: (text) => text || '-',
      },
      {
        title: '公示日期',
        dataIndex: 'publicationDate',
        render: (text) => text || '-',
      },
      {
        title: '更新时间',
        dataIndex: 'updateTime',
        render: (text) => text || '-',
      },
      {
        title: '是否历史',
        dataIndex: 'ifHistory',
        render: (text) => text || '-',
      },
      {
        title: '操作',
        render: (e) => <a onClick={() => store.JudicialAidModuleStore.open(e?.id)}>详情</a>,
      },
    ],
    judgmentDocuments: [
      {
        title: '案号',
        dataIndex: 'caseNo',
        render: (text) => text || '-',
      },
      {
        title: '案由',
        dataIndex: 'caseReason',
        render: (text) => text || '-',
        filterSearch: true,
        filterIcon: (
          <Space>
            <FilterOutlined />
            <span style={{ fontSize: 12, color: 'blue' }}>筛选</span>
          </Space>
        ),
        onFilter: (value, record) => record.caseReason && record.caseReason?.indexOf(value) === 0,
        filters,
      },
      {
        title: '当事人类型',
        dataIndex: 'partyType',
        render: (text) => {
          if (!text) return '-'
          switch (text) {
            case 1:
              return '原告'
            case 2:
              return '被告'
            case 3:
              return '当事人'
            case 4:
              return '上诉人'
            case 5:
              return '被上诉人'
            case 6:
              return '申请人'
            case 7:
              return '被申请人'
            case 8:
              return '被执行人'
            case 99:
              return '其他'
            default:
              return '-'
          }
        },
      },
      {
        title: '胜败诉',
        dataIndex: 'verdict',
        render: (text) => text || '-',
      },
      {
        title: '审判结果',
        dataIndex: 'judgmentResult',
        render: (text) => text || '-',
      },
      {
        title: '审判程序',
        dataIndex: 'caseProgram',
        render: (text) => text || '-',
      },
      {
        title: '操作',
        render: (e) => <a onClick={() => store.InstrumentModuleStore.open(e?.id)}>详情</a>,
      },
    ],
    restrictionHighConsumptions: [
      {
        title: '被执行人姓名',
        dataIndex: 'executedName',
        render: (text) => text || '-',
      },
      {
        title: '被执行人类型',
        dataIndex: 'executedType',
        render: (text) => text || '-',
      },
      {
        title: '案号',
        dataIndex: 'caseNo',
        render: (text, record) => {
          if (!text) return '-'
        },
      },
      {
        title: '案由',
        dataIndex: 'caseReason',
        render: (text) => text || '-',
      },
      {
        title: '立案时间',
        dataIndex: 'caseDate',
        render: (text) => text || '-',
      },
      {
        title: '操作',
        render: (e) => <a onClick={() => store.ImitHighModuleStore.open(e?.id)}>详情</a>,
      },
    ],
  }
  const columns = columnsMap[dataKey] || []
  return (
    <Table         columnsFilter={'components_Fl_1'}
            onFilter={(key,val) => saveServer('components_Fl_1',val)} resizable store={table} columns={columns} rowKey="uuid" pagination={false} bordered />
  )
})
// 卡片
const CardContainer = observer(({ data }) => {
  // const load = async () => {
  //   await http.post('/customer/view/detail/queryCustomerViewInfo', {
  //     enterpriseName: '恒生银行（中国）有限公司',
  //   })
  // }
  // useEffect(() => {
  //   load()
  // })
  return (
    <>
      <div
        style={{
          display: 'flex',
          gap: '16px',
          padding: '0',
          justifyContent: `space-between`,
          height: 110,
          marginBottom: 10,
          overflowY: 'auto',
        }}
      >
        <CustomCard
          onlyTotal={true}
          icon={<Second />}
          title="立案信息"
          total={data?.caseInformations}
        />
        <CustomCard
          onlyTotal={true}
          icon={<SafetyCertificateOutlined />}
          title="被执行信息"
          total={data?.executedInformations}
        />
        <CustomCard
          onlyTotal={true}
          icon={<JudicialAid />}
          modal={<JudicialAidModule />}
          title="司法协助"
          total={data?.judicialAssistances}
        />
        <CustomCard
          onlyTotal={true}
          icon={<FileTextOutlined />}
          modal={<InstrumentModule />}
          title="裁判文书"
          total={data?.judgmentDocuments}
        />
        <CustomCard
          onlyTotal={true}
          icon={<Impose />}
          modal={<ImitHighModule />}
          title="限制高消费"
          total={data?.restrictionHighConsumptions}
        />
      </div>
    </>
  )
})

function Index() {
  return (
    <Row gutter={20} wrap={false}>
      <Col>
        <Anchor offsetTop={150}>
          {sections.map((section) => (
            <Link key={section.id} href={`#${section.id}`} title={section.title} />
          ))}
        </Anchor>
      </Col>
      <Col flex={1} style={{ paddingLeft: 37 }}>
        <CardContainer data={store.CardContainer} />
        {sections.map((section) => (
          <div id={section.id} key={section.id} style={{ marginBottom: 30 }}>
            <>
              <Title level={5}>
                {section.title} <span className={styles.titleSpans}>数据来源：聚源</span>
              </Title>
            </>
            <RenderTable key={section.id} dataKey={section.dataKey} />
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
