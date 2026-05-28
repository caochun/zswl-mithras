import { observer } from '@zswl/admin'
import { Button, Form } from '@zswl/components'
import { Col, Collapse, Row, Space, Tooltip } from 'antd'
import styles from './styles.less'
import { FileTextTwoTone, QuestionCircleOutlined } from '@ant-design/icons'
import { useEffect, useMemo, useState } from 'react'
import { approvalInfoRender, DynamicFormItem } from './QualitativeScore'
import { isEmpty } from '@/utils'

const { Panel } = Collapse
const Index = ({ store, paramInfo, auth }) => {
  const { executeData } = store
  const item = paramInfo?.info?.评级调整事项

  const [infoList, setInfoList] = useState([])
  const [activeKey, setActiveKey] = useState([])
  const formatInfo = () => {
    const result = []
    Object.entries(item ?? {}).forEach(([key, value]) => {
      result.push({ groupName: key, list: value })
    })
    setActiveKey(result.map(({ groupName }) => groupName))
    setInfoList(result)
  }
  useEffect(() => {
    item && formatInfo()
  }, [JSON.stringify(item)])
  const { approvalStatus: approvalStatusValue, approvalOpinion } = infoList?.[0] ?? {}
  return (
    <div>
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>
        <FileTextTwoTone style={{ marginRight: 8, fontSize: 14 }} />
        评级调整事项
      </div>

      {infoList.length ? (
        <div className={styles.rows}>
          <Collapse activeKey={activeKey} onChange={setActiveKey} style={{ width: '100%' }}>
            {infoList.map(({ groupName, list }, index) => (
              <Panel header={groupName} key={groupName}>
                <Row gutter={12}>
                  {list.map(({ approvalStatus, ...item }) => (
                    <DynamicFormItem record={item} disabled={!auth} key={item.id} required />
                  ))}
                </Row>
              </Panel>
            ))}
          </Collapse>
          {!isEmpty(approvalStatusValue) && (
            <div style={{ marginTop: 12, width: '40%', marginLeft: 12 }}>
              {approvalInfoRender({ approvalStatusValue, approvalOpinion })}
            </div>
          )}
        </div>
      ) : (
        <div style={{ backgroundColor: '#ffd0ce', padding: 12, color: '#5b90fa' }}>无</div>
      )}

      <Space style={{ marginTop: 12 }}>
        <Button type="primary" onClick={store.calc} disabled={!auth}>
          试算
        </Button>
        <Space>
          试算次数
          <span className={styles.gray} style={{ width: 120 }}>
            {executeData?.executeCount}/{executeData?.executeCountLimit}
          </span>
        </Space>
      </Space>

      <Row gutter={30} style={{ marginTop: 40 }}>
        <Col span={12}>
          <div>
            初评结果
            <Tooltip title="未填写【评级调整项】时系统测算的评级结果">
              <QuestionCircleOutlined style={{ marginLeft: 8 }} />
            </Tooltip>
          </div>
          <div className={styles.gray}>{executeData.firstScore}</div>
        </Col>
        <Col span={12}>
          <div>
            调整后评级
            <Tooltip title="填写【评级调整项】时系统测算的评级结果">
              <QuestionCircleOutlined style={{ marginLeft: 8 }} />
            </Tooltip>
          </div>
          <div className={styles.gray}>{executeData.score}</div>
        </Col>
      </Row>
    </div>
  )
}

export default observer(Index)
