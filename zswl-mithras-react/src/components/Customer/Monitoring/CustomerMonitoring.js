import { Button, Page, Table, Tabs, SearchBar,Select,Form } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import styles from './index.less'
import TopSection from './components/TopSection'
import VisualizationTwoD from './components/VisualizationTwoD'
import { Checkbox } from 'antd'
import store from './store'
import ListRed from '/public/assets/risk/monitoringAlertList/vector.svg'
import Frame from '/public/assets/risk/monitoringAlertList/Frame.svg'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function Index({ path, ...props }) {
  const [directions, setDirections] = React.useState([])
  const onChange = (key) => {}
  const LightStatus = ({ level }) => {
    let icon = <Frame /> // 默认黄灯图标
    let text = '黄灯'
    let className = styles.yellowText
  
    if (level === 3) {
      icon = <ListRed /> // 红灯图标
      text = '红灯'
      className = styles.redText
    } else if (level === 1) {
      text = '绿灯'
      className = styles.greenText
    }
  
    return (
      <div className={className}>
        {
          level?
          <>
            {icon}
            <div style={{ marginLeft: 3 }}>{text}</div>
          </>
          :''
        }

      </div>
    )
  }
  const columnsYj = [
    { title: '客户名称', dataIndex: 'clientName', key: 'clientName' },
    { title: '统一社会信用代码', dataIndex: 'uscc', key: 'uscc' },
    { title: '所属部门', dataIndex: 'bizDeptName', key: 'bizDeptName' },
    {
      title: '红灯预警',
      dataIndex: 'redWarnCount',
      key: 'redWarnCount',
      render: (text) => <span style={{ color: text !== 0 ? 'red' : 'inherit' }}>{text}</span>,
    },
    {
      title: '黄灯预警',
      dataIndex: 'yellowWarnCount',
      key: 'yellowWarnCount',
      render: (text) => <span style={{ color: text !== 0 ? 'orange' : 'inherit' }}>{text}</span>,
    },
    {
      title: '舆情总数',
      dataIndex: 'opCount',
      key: 'opCount',
      render: (text) => <span style={{ color: text !== 0 ? 'blue' : 'inherit' }}>{text}</span>,
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <a
          className={styles.blueText}
          onClick={() => {
            history.push(
              `${path}/detail/${record.clientId}?enterpriseName=${record.clientName}&uscc=${record.uscc}`
            )
          }}
        >
          详情
        </a>
      ),
    },
  ]

  const columnsYjLB = [
    { title: '预警编号', dataIndex: 'warnCode', key: 'warnCode', width: 150 },
    { title: '预警标题', dataIndex: 'warnTitle', key: 'warnTitle', width: 350},
    { title: '客户名称', dataIndex: 'clientName', key: 'clientName', width: 300 },
    {
      title: '预警级别',
      dataIndex: 'warnLevel',
      key: 'warnLevel',
      width: 150,
      render: (level) => <LightStatus level={level} />,
    },
    {
      title: '统一社会信用代码',
      dataIndex: 'uscc',
      key: 'uscc',
      width: 200
    },
    {
      title: '预警时间',
      dataIndex: 'dataTime',
      key: 'dataTime',
      width: 200
    },
    {
      title: '处置状态',
      dataIndex: 'warnStatus',
      key: 'warnStatus',
      width: 150,
      render: (value) => {
        const label = store.riskControlOpinionHandleStatus.find(
          (item) => item.value === value
        )?.label
        return <span>{label}</span>
      },
    },
  ]


  const columnsYQ = [
    { title: '舆情标题', dataIndex: 'title', key: 'title',width: 300 },
    { title: '统一社会信用代码', dataIndex: 'uscc', key: 'uscc',width: 200 },
    { title: '客户名称', dataIndex: 'clientName', key: 'clientName', width: 300 },
    {
      title: '重要度',
      dataIndex: 'warnLevel',
      key: 'warnLevel',
      width: 100,
      render: (level) => {
        if (level) {
          const stars = Array(level).fill('★').join('')
          return <span style={{ color: '#faad14' }}>{stars}</span>
        } 
      },
    },
    {
      title: '预警级别',
      dataIndex: 'warnLevel',
      width: 150,
      key: 'warnLevel',
      render: (level) => <LightStatus level={level} />,
    },
    {
      title: '信息触发时间',
      dataIndex: 'dataTime',
      key: 'dataTime',
      width: 200,
    },
    {
      title: '处置状态',
      dataIndex: 'handleResult',
      key: 'handleResult',
      width: 150,
      render: (value) => {
        const label = store.riskControlOpinionHandleStatus.find(
          (item) => item.value === value
        )?.label
        return <span>{label}</span>
      },
    },
  ]
  const handleCheckboxChange = (vals) => {
    setDirections(vals)
    store.clientMonitorWarnlist.setParams({ warnLevels: vals })
    store.clientMonitorWarnlist.search()
  }
  return (
    //
    <Page store={store.page} className={styles.page}>
      <div className={styles.container}>
        <div className={styles.top}>
          <TopSection statistics={store?.statistics} />
          <VisualizationTwoD
            fxsl={store?.postQuantityChange}
            warnPieData={store?.pie?.warnPieData}
            opPieData={store?.pie?.opPieData}
          />
        </div>
        <div className={styles.content}>
          <Tabs
            defaultActiveKey="1"
            onChange={onChange}
            columnsFilter="afterLeaseCheckPlanCheckList"
            items={[
              {
                label: `监控客户列表`,
                key: '1',
                children: (
                  <div style={{ padding: 10 }}>
                    <Table
                            columnsFilter={'pages_customerMonitoring_1'}
                            onFilter={(key,val) => saveServer('pages_customerMonitoring_1',val)}
                      actions={[
                        <Checkbox.Group
                          options={['只看风险客户（预警或舆情大于0）']}
                          defaultValue={['']}
                          onChange={(checkedValues) => {
                            // 如果选中全选
                            if (checkedValues.includes('只看风险客户（预警或舆情大于0）')) {
                              // 选中所有数据
                              store.warningList.setParams({ onlyWarn: 1 })
                              store.warningList.search()
                              store.warningList.setSelectedRowKeys(
                                store.warningList.dataSource.map((item) => item.id)
                              )
                            } else {
                              store.warningList.setParams({ onlyWarn: '' })
                              store.warningList.search()
                            }
                          }}
                        />,
                      ]}
                      searchbar={{
                        labelCol: { span: 6 },
                        items: [{ label: '客户名称', name: 'clientName', allowClear: true }],
                      }}
                      className="newTableWarningList"
                      store={store.warningList}
                      columns={columnsYj}
                    />
                  </div>
                ),
              },
              {
                label: `预警列表`,
                key: '2',
                children: (
                  <div style={{ padding: 10 }}>
                    <Table
                                                columnsFilter={'pages_customerMonitoring_2'}
                                                onFilter={(key,val) => saveServer('pages_customerMonitoring_2',val)}
                      actions={[
                        <span>
                          预警级别：
                          <Checkbox.Group
                            options={[
                              { label: '红灯', value: '3' },
                              { label: '黄灯', value: '2' },
                            ]}
                            value={directions}
                            onChange={(vals) => {
                              handleCheckboxChange(vals)
                            }}
                          />
                        </span>,

                      ]}
                      searchbar={{
                        labelCol: { span: 6 },
                        items: [
                          { label: '预警编号', name: 'warnCode', allowClear: true },
                          { label: '客户名称', name: 'clientName', allowClear: true },
                          { label: '预警标题', name: 'warnTitle', allowClear: true },
                          {
                            title: '处置状态',
                            dataIndex: 'warnStatus',
                            element: (
                              <Select
                                options={store?.riskControlOpinionHandleStatus}
                                placeholder="处置状态"
                                debounceSearch
                              />
                            ),
                          },
                        ],
                      }}
                      className="newTableWarningList"
                      store={store.clientMonitorWarnlist}
                      columns={columnsYjLB}
                    />
                  </div>
                ),
              },
              {
                label: `舆情列表`,
                key: '3',
                children: (
                  <div style={{ padding: 10 }}>
                    <Table
                                                columnsFilter={'pages_customerMonitoring_3'}
                                                onFilter={(key,val) => saveServer('pages_customerMonitoring_3',val)}
                      searchbar={{
                        labelCol: { span: 6 },
                        items: [
                          { label: '客户名称', name: 'clientName', allowClear: true },
                          {
                            title: '处置状态',
                            dataIndex: 'opinionStatus',
                            element: (
                              <Select
                                options={store?.riskControlOpinionHandleStatus}
                                placeholder="处置状态"
                                debounceSearch
                              />
                            ),
                          },
                        ],
                      }}
                      className="newTableWarningList"
                      store={store.opinionlist}
                      columns={columnsYQ}
                    />
                  </div>
                ),
              },
            ]}
          />
        </div>
      </div>
    </Page>
  )
}

export default observer(Index)
