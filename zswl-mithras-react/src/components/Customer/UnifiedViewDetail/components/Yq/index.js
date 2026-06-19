import { Button, Page, Table, Tabs, SearchBar } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import { Input, Select, Badge, Checkbox, Form } from 'antd'
import CustomCard from '../shared/Card/index'
import PositivePublicOpinion from './svgs/PositivePublicOpinion.svg'
import NeutralPublicOpinion from './svgs/NeutralPublicOpinion.svg'
import NegativePublic from './svgs/NegativePublic.svg'
import { saveServer } from '@/utils'
import styles from './index.less'
import store from './store'

const colorMap = {
  已推送: { ys: 'yts', text: '已推送' },
  已反馈: { ys: 'yfk', text: '已反馈' },
  审批通过: { ys: 'sptg', text: '审批通过' },
  审批退回: { ys: 'spth', text: '审批退回' },
  未推送: { ys: 'wts', text: '未推送' },
}

const importanceDotColor = {
  FCC0000002QB: '#52C41A',
  FCC0000002QC: '#FADB15',
  FCC0000002QD: '#FB942D',
  FCC0000002QE: '#F5222D',
}

const CardContainer = observer(() => {
  return (
    <>
      <div style={{ textAlign: 'right', fontSize: 12, marginBottom: 10 }}>
        <span style={{ color: `rgba(196, 198, 207, 1)` }}>数据来源：聚源</span>
      </div>
      <div
        style={{
          display: 'flex',
          gap: '16px',
          padding: '0',
          marginBottom: 10,
        }}
      >
        <CustomCard
          icon={<NegativePublic />}
          red={true}
          title="负面舆情"
          onlyTotal={true}
          total={store.cardData?.negativeCount}
        />
        <CustomCard
          icon={<NeutralPublicOpinion />}
          onlyTotal={true}
          title="中性舆情"
          total={store.cardData?.positiveCount}
        />
        <CustomCard
          icon={<PositivePublicOpinion />}
          onlyTotal={true}
          title="正面舆情"
          total={store.cardData?.publicCount}
        />
      </div>
    </>
  )
})

function Index({ path, ...props }) {
  const [directions, setDirections] = React.useState(['正面', '中性', '负面'])
  const [selectedKeys, setSelectedKeys] = React.useState([
    'FCC0000002Q9', // 对应正面
    'FCC0000002QF', // 对应中性
    'FCC0000002QA', // 对应负面
  ])

  const directionKeyMap = {
    正面: 'FCC0000002Q9',
    中性: 'FCC0000002QF',
    负面: 'FCC0000002QA',
  }

  const handleCheckboxChange = (vals) => {
    setDirections(vals)
    const keys = vals.map((val) => directionKeyMap[val])
    setSelectedKeys(keys)
    store.warningList.setParams({ keys: keys })
    store.warningList.search()
  }

  const columns = [
    {
      title: '舆情标题',
      dataIndex: 'title',
      key: 'title',
      render: (text, record) => {
        if (!text) return '-'
        if (!record.linkAddress) return text
        return (
          <a
            onClick={() => {
              window.open(record.linkAddress, '_blank')
            }}
          >
            {text}
          </a>
        )
      },
    },
    {
      title: '舆情发生时间',
      dataIndex: 'infoPublDate',
      key: 'infoPublDate',
      render: (text) => text || '-',
    },
    {
      title: '情感重要度',
      dataIndex: 'emotionImportance',
      key: 'emotionImportance',
      render: (text) => {
        console.log(text, 'text')

        if (!text) return '-'
        /**
         * text 是code == FCC0000002QB — 零星、FCC0000002QC— 一星、FCC0000002QD — 二星、FCC0000002QE — 三星
         */
        const dotColor = importanceDotColor[text] || 'orange'

        const colorMap = {
          FCC0000002QB: { ys: 'negative', text: '零星' },
          FCC0000002QC: { ys: 'neutral', text: '一星' },
          FCC0000002QD: { ys: 'neutral', text: '二星' },
          FCC0000002QE: { ys: 'neutral', text: '三星' },
        }
        return (
          <>
            <span style={{ color: dotColor }}>● </span>
            {colorMap[text]?.text || '-'}
          </>
        )
      },
    },
    {
      title: '情感方向',
      dataIndex: 'emotionDirection',
      key: 'emotionDirection',
      render: (text) => {
        if (!text) return '-'
        const colorMap = {
          FCC0000002QA: { ys: 'negative', text: '负面' },
          FCC0000002QF: { ys: 'neutral', text: '中性' },
          FCC0000002Q9: { ys: 'positive', text: '正面' },
        }
        const tag = colorMap[text]
        return tag ? tag.text : '-'
      },
    },
  ]

  return (
    <Page noStyle>
      <div className={styles.container}>
        <div className={styles.content}>
          <CardContainer />
          <div style={{ padding: 10 }}>
            <Table
              columnsFilter={'detail_components_Yq_1'}
              onFilter={(key, val) => saveServer('detail_components_Yq_1', val)}
              resizable
              actions={[
                <span>
                  变更原内容:
                  <Checkbox.Group
                    options={['正面', '中性', '负面']} // 勾选框选项
                    value={directions} // 当前选中状态
                    onChange={handleCheckboxChange} // 处理变化
                  />
                </span>,
              ]}
              store={store.warningList}
              columns={columns}
            />
          </div>
        </div>
      </div>
    </Page>
  )
}

export default observer(Index)
