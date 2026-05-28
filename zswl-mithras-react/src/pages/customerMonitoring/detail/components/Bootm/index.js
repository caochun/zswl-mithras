import React from 'react'
import { Timeline, Tag, Card, Tooltip } from 'antd'
import styles from './style.less'
import Yq from './imgs/yq.png'
import Yj from './imgs/yj.png'
import { getQuery, history, observer } from '@zswl/admin'
import ListRed from '/public/assets/risk/monitoringAlertList/vector.svg'
import Frame from '/public/assets/risk/monitoringAlertList/Frame.svg'

const TimelineNode = ({ date, imgs }) => (
  <Timeline.Item
    className={styles['timeline-middle-item']}
    dot={<img src={imgs} alt="Node Icon" style={{ width: '74px', height: '74px' }} />}
  >
    <div style={{ marginLeft: 48 }}>
      <h3>{date}</h3>
    </div>
  </Timeline.Item>
)

const WarningIcon = () => <img src="/public/assets/risk/customerView/waring.svg" alt="warning" />

// 预警信号灯组件
const LightStatus = ({ level }) => {
  let icon = <Frame /> // 默认黄灯图标
  let text = '黄灯'
  let className = styles.yellowText

  if (level === '红灯') {
    icon = <ListRed /> // 红灯图标
    text = '红灯'
    className = styles.redText
  } else if (level === '黄灯') {
    text = '黄灯'
    className = styles.yellowText
  }

  return (
    <Tag style={{ marginLeft: 20 }} color={level === '红灯' ? 'red' : 'gold'}>
      <div className={styles['warning-tag']}>
        <div className={className}>
          {icon}
          <div style={{ marginLeft: 3 }}>{text}</div>
        </div>
      </div>
    </Tag>
  )
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return dateString.split(' ')[0]
}

const groupByDate = (data) => {
  const groups = {}
  data?.forEach((item) => {
    if (!item.date) return
    const date = formatDate(item.date)
    if (!groups[date]) {
      groups[date] = []
    }
    groups[date].push(item)
  })
  return groups
}

// 添加一个数字转中文的辅助函数
const numberToChinese = (num) => {
  const chineseNumbers = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
  return chineseNumbers[num]
}

// 添加星级颜色映射
const STAR_COLORS = {
  1: '#FADB15', // 一星 黄色
  2: '#FB942D', // 二星 橙色
  3: '#F5222D', // 三星 红色
}

const TimelineContent = ({ data, status, type = 'yq' }) => (
  <Timeline.Item
    dot={<div className={styles['middle-dot']} />}
    className={styles['timeline-middle-item']}
  >
    <div className={styles['custom-timeline-box']}>
      <div className={styles['custom-timeline-box-left']}>
        <div>
          {type === 'yq' ? (
            <Tooltip placement="topLeft" title={data?.title}>
              {data?.linkAddress ? (
                <a
                  href={data.linkAddress}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ color: '#333' }}
                  className={styles.titleLink}
                >
                  {data?.title}
                </a>
              ) : (
                <span>{data?.title}</span>
              )}
            </Tooltip>
          ) : (
            <Tooltip placement="topLeft" title={data?.warnTitle}>
              <span>{data?.warnTitle}</span>
            </Tooltip>
          )}
        </div>
      </div>
      <div className={styles['custom-timeline-box-right']}>
        {type === 'yq' ? (
          <>
            <span style={{ minWidth: '40px' }}>
              {data?.warnStar ? (
                <>
                  <span
                    style={{
                      color: STAR_COLORS[data?.warnStar] || '#FFB434',
                      margin: '0 7px 0 8px',
                    }}
                  >
                    ●
                  </span>
                  {`${numberToChinese(data?.warnStar)}星`}
                </>
              ) : (
                '无'
              )}
            </span>
            <div className={styles['warning-tag']} style={{ marginLeft: 20, minWidth: '52px' }}>
              <LightStatus
                level={
                  data?.warnLevel === 1
                    ? '绿灯'
                    : data?.warnLevel === 2
                    ? '黄灯'
                    : data?.warnLevel === 3
                    ? '红灯'
                    : '黄灯'
                }
              />
            </div>
          </>
        ) : (
          <>
            <span style={{ minWidth: '40px' }}>{data?.warnCode || '无'}</span>
            <LightStatus level={data?.warnLevel} />
          </>
        )}
        <span style={{ marginLeft: 20, minWidth: '52px' }}>
          {status.find(
            (item) => item.value === (type === 'yq' ? data?.opinionStatus : data?.warnStatus)
          )?.label || '　'}
        </span>
      </div>
    </div>
  </Timeline.Item>
)

const CustomTimeline = ({ status, yqDetail, yjDetail }) => {
  const { enterpriseName } = getQuery()
  const groupedYqData = groupByDate(yqDetail)
  const groupedYjData = groupByDate(yjDetail)
  const sortedYqDates = Object.keys(groupedYqData).sort((a, b) => new Date(b) - new Date(a))
  const sortedYjDates = Object.keys(groupedYjData).sort((a, b) => new Date(b) - new Date(a))

  return (
    <div className={styles['timeline-wrapper']} style={{ display: 'flex', gap: '20px' }}>
      <div className={styles['cart-yqmx']} style={{ flex: 1 }}>
        <Card
          title={<div className={styles['custom-card-title']}>舆情明细</div>}
          style={{ marginTop: 20 }}
          extra={
            <div
              type="link"
              className={styles.Btn}
              style={{ color: '#1677ff' }}
              onClick={() => {
                history.push(`/process/receive/?enterpriseName=${enterpriseName}&tag=2`)
              }}
            >
              去处理
            </div>
          }
          bordered={false}
        >
          <Timeline mode="left" className={`${styles['custom-timeline']} custom-timeline`}>
            {sortedYqDates.map((date) => (
              <React.Fragment key={date}>
                {date && <TimelineNode date={date} imgs={Yq} />}
                {groupedYqData[date].map((item, index) => (
                  <TimelineContent key={index} data={item} status={status} type="yq" />
                ))}
              </React.Fragment>
            ))}
          </Timeline>
        </Card>
      </div>

      <div className={styles['cart-yqmx']} style={{ flex: 1 }}>
        <Card
          title={<div className={styles['custom-card-title']}>预警明细</div>}
          style={{ marginTop: 20 }}
          extra={
            <div
              type="link"
              className={styles.Btn}
              style={{ color: '#1677ff' }}
              onClick={() => {
                history.push(`/process/receive/?enterpriseName=${enterpriseName}&tag=1`)
              }}
            >
              去处理
            </div>
          }
          bordered={false}
        >
          <Timeline mode="left" className={`${styles['custom-timeline']} custom-timeline`}>
            {sortedYjDates.map((date) => (
              <React.Fragment key={date}>
                {date && <TimelineNode date={date} imgs={Yj} />}
                {groupedYjData[date].map((item, index) => (
                  <TimelineContent key={index} data={item} status={status} type="yj" />
                ))}
              </React.Fragment>
            ))}
          </Timeline>
        </Card>
      </div>
    </div>
  )
}

export default observer(CustomTimeline)
