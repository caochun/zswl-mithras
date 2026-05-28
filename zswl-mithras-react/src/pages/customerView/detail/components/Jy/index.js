import { observer } from '@zswl/admin'
import { Anchor, Row, Col, Typography } from 'antd'
import { Table } from '@zswl/components'
import CustomCard from '../shared/Card/index'
import SecondTow from './svgs/SecondTow.svg'
import SafetyCertificateOutlinedTow from './svgs/SafetyCertificateOutlinedTow.svg'
import JudicialAidTow from './svgs/JudicialAidTow.svg'
import FileTextOutlinedTow from './svgs/FileTextOutlinedTow.svg'
import ImposeTow from './svgs/ImposeTow.svg'
import ImposeDy from './svgs/ImposeDy.svg'
import { JudicialAidModule, InstrumentModule, ImitHighModule } from './Modal'
import { saveServer } from '@/utils'

import store from './store'

const { Link } = Anchor
const { Title } = Typography

// 卡片配置
const sections = [
  {
    id: 'businessAbnormal',
    title: '1.经营异常',
    cartTitle: '经营异常',
    dataKey: 'bizException',
    icon: <SecondTow />,
    red: true,
  },
  {
    id: 'administrativePunishment',
    title: '2.行政处罚',
    cartTitle: '行政处罚',
    dataKey: 'case_info_list',
    icon: <SafetyCertificateOutlinedTow />,
  },
  {
    id: 'seriousViolation',
    title: '3.严重违法',
    cartTitle: '严重违法',
    dataKey: 'illegal_list',
    icon: <JudicialAidTow />,
  },
  {
    id: 'majorTaxViolation',
    title: '4.重大税收违法',
    cartTitle: '重大税收违法',
    dataKey: 'major_illegal_list',
    icon: <FileTextOutlinedTow />,
  },
  {
    id: 'guaranteeEvents',
    title: '5.担保事件',
    cartTitle: '担保事件',
    dataKey: 'guarant_event_list',
    icon: <ImposeTow />,
  },
  {
    id: 'chattelMortgage',

    title: '6.动产抵押',
    cartTitle: '动产抵押',
    dataKey: 'mortgage_list',
    icon: <ImposeDy />,
  },
]

// 经营异常表格
const BusinessAbnormalTable = observer(() => {
  const columns = [
    {
      title: '列入异常原因',
      dataIndex: 'exception_result_in',
      render: (text) => text || '-',
    },
    {
      title: '列入异常日期',
      dataIndex: 'exception_date_in',
      render: (text) => text || '-',
    },
    {
      title: '移除异常原因',
      dataIndex: 'exception_result_out',
      render: (text) => text || '-',
    },
    {
      title: '移除异常日期',
      dataIndex: 'exception_date_out',
      render: (text) => text || '-',
    },
    {
      title: '更新时间',
      dataIndex: 'create_time',
      render: (text) => text || '-',
    },
  ]
  return (
    <Table
    columnsFilter={'components_Jy_1'}
    onFilter={(key,val) => saveServer('components_Jy_1',val)}
      store={store.bizException}
      resizable
      columns={columns}
      rowKey="id"
      pagination={false}
      bordered
    />
  )
})

// 行政处罚表格
const AdministrativePunishmentTable = observer(() => {
  const columns = [
    {
      title: '处罚决定书号',
      dataIndex: 'penalty_document_no',
      render: (text) => text || '-',
    },
    {
      title: '违法行为类型',
      dataIndex: 'illegal_situation',
      render: (text) => text || '-',
    },
    {
      title: '处罚内容',
      dataIndex: 'penalty_result',
      render: (text) => text || '-',
    },
    {
      title: '处罚决定日期',
      dataIndex: 'penalty_date',
      render: (text) => text || '-',
    },
    {
      title: '更新时间',
      dataIndex: 'result_date',
      render: (text) => text || '-',
    },
  ]
  return (
    <Table
    columnsFilter={'components_Jy_2'}
    onFilter={(key,val) => saveServer('components_Jy_2',val)}
      store={store.caseInfoList}
      resizable
      columns={columns}
      rowKey="id"
      pagination={false}
      bordered
    />
  )
})

// 严重违法表格
const SeriousViolationTable = observer(() => {
  const columns = [
    {
      title: '列入原因',
      dataIndex: 'listreason',
      width: 350,
      render: (text) => text || '-',
    },
    {
      title: '列入日期',
      dataIndex: 'listdate',
      render: (text) => {
        if (!text) return '-'
        const date = text.split('T')[0]
        return date || '-'
      },
    },
    {
      title: '移除原因',
      dataIndex: 'removereason',
      render: (text) => text || '-',
    },
    {
      title: '移除日期',
      dataIndex: 'removedate',
      render: (text) => {
        if (!text) return '-'
        const date = text.split('T')[0]
        return date || '-'
      },
    },
    {
      title: '更新时间',
      dataIndex: 'create_time',
      render: (text) => text || '-',
    },
  ]
  return (
    <Table
    columnsFilter={'components_Jy_3'}
    onFilter={(key,val) => saveServer('components_Jy_3',val)}
      resizable
      store={store.illegalList}
      columns={columns}
      rowKey="id"
      pagination={false}
      bordered
    />
  )
})

// 重大税收违法表格
const MajorTaxViolationTable = observer(() => {
  const columns = [
    {
      title: '违法事实',
      dataIndex: 'illegalfact',
      width: 350,
      // 目前无链接占时置灰
      render: (text) => (text ? text : '-'),
    },
    {
      title: '案件性质',
      dataIndex: 'casenature',
      render: (text) => text || '-',
      width: 350,
    },
    {
      title: '发生日期',
      dataIndex: 'occurdate',
      render: (text) => {
        if (!text) return '-'
        const date = text.split('T')[0]
        return date || '-'
      },
    },
    {
      title: '更新时间',
      dataIndex: 'updatetime',
      render: (text) => {
        if (!text) return '-'
        const date = text.split('T')[0]
        return date || '-'
      },
    },
    {
      title: '操作',
      render: (e) => <a onClick={() => store.JudicialAidModuleStore.open(e?.id)}>详情</a>,
    },
  ]

  return (
    <Table
    columnsFilter={'components_Jy_4'}
    onFilter={(key,val) => saveServer('components_Jy_4',val)}
      resizable
      store={store.majorIllegalList}
      columns={columns}
      rowKey={(record) => record.id || Math.random()}
      pagination={false}
      bordered
    />
  )
})

// 担保事件表格
const GuaranteeEventsTable = observer(() => {
  const columns = [
    {
      title: '被担保对象',
      dataIndex: 'eventObjectName',
      render: (text) => text || '-',
      width: 350,
    },
    {
      title: '担保业务类型',
      dataIndex: 'guarantBusiType',
      render: (text) => {
        switch (text) {
          case 'FCC000001E60':
            return '常规担保'
          case 'FCC000001E61':
            return '购房担保'
          case 'FCC000001E62':
            return '主营担保'
          default:
            return '-'
        }
      },
    },
    {
      title: '担保行为',
      dataIndex: 'guaranteeBehavior',
      render: (text) => {
        switch (text) {
          case 'FCC000000HLR':
            return '提供担保'
          case 'FIC0000000RS':
            return '接受担保'
          case 'FCC000000YD4':
            return '提供担保协议(预计担保额度)'
          case 'FCC000000YD5':
            return '接受担保协议(预计担保额度)'
          case 'FCC000000HLS':
            return '提供反担保'
          case 'FCC000000YD6':
            return '接受反担保'
          case 'FCC000000YD7':
            return '互保协议(预计担保额度)'
          default:
            return '-'
        }
      },
    },
    {
      title: '担保金额',
      dataIndex: 'latestGuaranteeLimit',
      render: (text) => text || '-',
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      render: (text) => text || '-',
    },
    {
      title: '操作',
      render: (e) => <a onClick={() => store.InstrumentModuleStore.open(e?.id)}>详情</a>,
    },
  ]

  return (
    <Table
    columnsFilter={'components_Jy_5'}
    onFilter={(key,val) => saveServer('components_Jy_5',val)}
      resizable
      store={store.guarantEventList}
      columns={columns}
      rowKey="id"
      pagination={false}
      bordered
    />
  )
})

// 动产抵押表格
const ChattelMortgageTable = observer(() => {
  const columns = [
    {
      title: '动产抵押登记编号',
      dataIndex: 'impawnRegNumber',
      render: (text) => text || '-',
    },
    {
      title: '被担保债权种类',
      dataIndex: 'type',
      render: (text) => text || '-',
    },
    {
      title: '被担保债权范围',
      dataIndex: 'overviewScope',
      render: (text) => text || '-',
    },
    {
      title: '被担保债务数额(万元)',
      dataIndex: 'securedPrincipalClaimsBalance',
      render: (text) => text || '-',
    },
    {
      title: '登记日期',
      dataIndex: 'mortgageRegDate',
      render: (text) => text || '-',
    },
    {
      title: '操作',
      render: (e) => (
        <a
          onClick={() => {
            store.ImitHighModuleStore.open(e?.id)
          }}
        >
          详情
        </a>
      ),
    },
  ]
  return (
    <Table     columnsFilter={'components_Jy_6'}
    onFilter={(key,val) => saveServer('components_Jy_6',val)} resizable store={store.mortgageList} columns={columns} pagination={false} bordered />
  )
})

// 渲染卡片组件
const CardContainer = observer(() => {
  return (
    <div
      style={{
        display: 'flex',
        gap: '16px',
        padding: '0',
        justifyContent: 'space-between',
        overflow: 'auto',
        height: 110,
        marginBottom: 10,
      }}
    >
      {sections.map((section) => {
        const current = store.CardContainer[section.dataKey]?.current || 0
        const total = store.CardContainer[section.dataKey]?.total || 0
        return (
          <CustomCard
            key={section.id}
            icon={section.icon}
            red={section.red || false}
            onlyTotal={['major_illegal_list', 'case_info_list'].includes(section.dataKey)}
            title={section.cartTitle}
            current={current}
            total={total}
            dataKey={section.dataKey}
          />
        )
      })}
    </div>
  )
})

// 主页面组件
const Index = observer(() => {
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
        <CardContainer />
        <div id="businessAbnormal">
          <Title level={5}>1.经营异常</Title>
          <BusinessAbnormalTable />
        </div>
        <div id="administrativePunishment">
          <Title level={5}>2.行政处罚</Title>
          <AdministrativePunishmentTable />
        </div>
        <div id="seriousViolation">
          <Title level={5}>3.严重违法</Title>
          <SeriousViolationTable />
        </div>
        <div id="majorTaxViolation">
          <Title level={5}>4.重大税收违法</Title>
          <MajorTaxViolationTable />
        </div>
        <div id="guaranteeEvents">
          <Title level={5}>5.担保事件</Title>
          <GuaranteeEventsTable />
        </div>
        <div id="chattelMortgage">
          <Title level={5}>6.动产抵押</Title>
          <ChattelMortgageTable />
        </div>
      </Col>
      <JudicialAidModule />
      <InstrumentModule />
      <ImitHighModule />
    </Row>
  )
})

export default Index
