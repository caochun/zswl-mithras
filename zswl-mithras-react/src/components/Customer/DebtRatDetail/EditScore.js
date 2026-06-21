import { observer } from '@zswl/admin'
import { Col, Collapse, Radio, Row } from 'antd'
import { useEffect, useState } from 'react'
import { DynamicFormItem } from '../RatingForm'
import { Form } from '@zswl/components'

const { Panel } = Collapse

const CustomerDebtRatDetailEditScore = ({ paramInfo, auth, title, handleAdjust }) => {
  const { info = {} } = paramInfo ?? {}
  const [infoList, setInfoList] = useState([])
  const [isNeedAdjust, setIsNeedAdjust] = useState({})
  const [activeKey, setActiveKey] = useState([])
  const formatInfo = (info) => {
    const result = []
    Object.entries(info[title] ?? {}).forEach(([key, value]) => {
      result.push({ groupName: key, list: value })
    })
    setActiveKey(result.map(({ groupName }) => groupName))
    setInfoList(result)
  }
  useEffect(() => {
    info && formatInfo(info)
  }, [JSON.stringify(info)])
  const adjustChange = (groupName, value) => {
    setIsNeedAdjust({ ...isNeedAdjust, [groupName]: value })

    if (value === false) {
      handleAdjust?.(groupName, value)
    }
  }
  return (
    <>
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>{title}</div>
      <Collapse activeKey={activeKey} onChange={setActiveKey}>
        {infoList.map(({ groupName, list }, index) => {
          const changeMap = {
            房地产调整项: 'isRealEstateAdjust',
            股权调整项: 'isStockRightsAdjust',
          }
          const changeName = changeMap[groupName]
          return (
            <Panel header={groupName} key={groupName}>
              {!!changeName && (
                <Row>
                  <Col span={12}>
                    <Form.Item
                      label={`是否需要进行${groupName}调整项`}
                      name={changeName}
                      rules={[{ required: true, message: '请选择' }]}
                    >
                      <Radio.Group onChange={(e) => adjustChange(groupName, e.target.value)}>
                        <Radio value={true}>是</Radio>
                        <Radio value={false}>否</Radio>
                      </Radio.Group>
                    </Form.Item>
                  </Col>
                </Row>
              )}
              <Form.Item dependencies={[changeName]} noStyle>
                {({ getFieldValue }) => {
                  const adjust = getFieldValue(changeName)
                  const hasAdjust = [null, undefined].includes(adjust)
                  return (
                    <Row
                      gutter={12}
                      style={{
                        display: !handleAdjust || !hasAdjust ? 'block' : 'none',
                      }}
                    >
                      {list.map((item) => (
                        <DynamicFormItem record={item} disabled={!auth || !adjust} key={item.id} />
                      ))}
                    </Row>
                  )
                }}
              </Form.Item>
            </Panel>
          )
        })}
      </Collapse>
    </>
  )
}

export default observer(CustomerDebtRatDetailEditScore)
