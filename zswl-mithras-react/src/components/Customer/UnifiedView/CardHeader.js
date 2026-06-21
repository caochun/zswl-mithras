import { history, observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import styles from './index.less'
import { LineChart } from '@/components/Chart/LineChartEntries'
import { useEffect, useState } from 'react'
import customerViewApi from '@/api/customerView/customerOverviewApi'
import dayjs from 'dayjs'

const cardList = [
  {
    title: '总客户数',
    key: 'totalCustomer',
    backgroundImage: '/public/assets/risk/customerView/totalCustomer.svg',
    backgroundColor: '27, 175, 255',
    color: '#1bafff',
    groupCode: 'CLIENT_ALL',
    link: '/customer/maintain',
  },
  {
    title: '存续客户数',
    key: 'survivalCustomer',
    backgroundImage: '/public/assets/risk/customerView/existingCustomer.svg',
    backgroundColor: '37, 88, 230',
    color: '#2558e64d',
    groupCode: 'CLIENT_SURVIVAL',
    link: '/lifeCycle/custom?type=EXISTING',
  },
  {
    title: '已结清客户数',
    key: 'settledCustomer',
    backgroundImage: '/public/assets/risk/customerView/setter.svg',
    backgroundColor: '8, 236, 236',
    color: '#08ecec',
    groupCode: 'CLIENT_SETTLE',
    link: '/lifeCycle/custom?type=SETTLED',
  },
  {
    title: '逾期客户数',
    key: 'overdueCustomer',
    backgroundImage: '/public/assets/risk/customerView/beOverdue.svg',
    backgroundColor: '255, 93, 93',
    color: '#ff5d5d',
    groupCode: 'CLIENT_OVERDUE',
    link: '/lifeCycle/custom?type=OVERDUE',
  },
]

const CustomerUnifiedViewCardHeader = () => {
  const [cardData, setCardData] = useState(cardList)
  const getCardData = async () => {
    const [statistics, trends] = await Promise.all([
      customerViewApi.postDashboardClientOverviewStatistics({}),
      customerViewApi.postCustomerTrends({}),
    ])

    const cardData = cardList.map((card) => {
      const trend = trends.find((t) => t.cordName === card.groupCode).items
      const info = statistics.find((t) => t.groupCode === card.groupCode)
      return {
        ...card,
        value: info?.quantity ?? 0,
        change: info?.incrementThisMonth ?? 0,
        trend: [
          {
            name: card.title,
            list: trend?.map((t) => ({
              name: dayjs(t.belongTime).format('MM月'),
              value: t.clientSum,
            })),
          },
        ],
      }
    })
    setCardData(cardData)
  }
  useEffect(() => {
    getCardData()
  }, [])
  return (
    <div className={styles.cards}>
      {cardData.map((card) => (
        <div
          key={card.title}
          className={styles.card}
          onClick={() => {
            history.push(card.link)
          }}
          style={{
            background: `linear-gradient(180deg, rgba(${card.backgroundColor}, 0.30) -45.23%, #fff 60.5%)`,
          }}
        >
          <div className={styles.cardContent}>
            <div className={styles.title}>{card.title}</div>
            <img src={card.backgroundImage} alt={card.title} className={styles.backgroundImage} />
            <div className="z-flex-jsb">
              <div className={styles.value}>{card.value ?? 0}</div>
              <div className={styles.change}>
                本月
                <span className={styles.changeValue}>{card.change}</span>
                <IconFont type={card.change >= 0 ? 'icon-arrow_up' : 'icon-arrow_down'} />
              </div>
            </div>
          </div>
          {/* 这里添加折线图组件 */}
          <LineChart
            chartBoxStyle={{ height: 100, width: '100%' }}
            unit="个"
            data={card.trend ?? []}
            colorWheel={[card.color]}
            options={{
              gird: {
                top: 0,
                left: 0,
              },
              legend: {
                show: false,
              },
              xAxis: {
                axisLine: {
                  show: false,
                },
              },
              yAxis: {
                show: false,
              },
            }}
          />
        </div>
      ))}
    </div>
  )
}
export default observer(CustomerUnifiedViewCardHeader)
