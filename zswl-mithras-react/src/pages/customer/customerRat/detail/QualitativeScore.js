import { observer } from '@zswl/admin'
import { Form } from '@zswl/components'
import { Collapse, Row } from 'antd'
import { useEffect, useState } from 'react'
import { DynamicFormItem, approvalInfoRender } from '@/components/Customer/RatingForm'

const { Panel } = Collapse
export { DynamicFormItem, approvalInfoRender }
const Index = ({ initialValues, paramInfo, auth, isFirst, store }) => {
  const { info = {} } = paramInfo ?? {}
  const [infoList, setInfoList] = useState([])
  const [activeKey, setActiveKey] = useState([])
  const formatInfo = (info) => {
    let result = []
    const keyOrder = [
      '持续经营能力',
      '客户背景/规模',
      '财务指标',
      '标的船舶'
    ];
    const keyOrder2 = [
      '持续经营能力（满分27分）',
      '客户背景/规模（30分）',
      '财务指标（15分）',
      '标的船舶（28分）'
    ];
    if(info?.定性指标){
      if(store.model === 'client_hymx'){
        result = keyOrder.map((key, i) => ({ list: info?.定性指标[key] || info?.定性指标[keyOrder2[i]], groupName: key }));
      }else{
        Object.entries(info?.定性指标 ?? {}).forEach(([key, value]) => {
          result.push({ groupName: key, list: value })
        })
      }
    }
    setActiveKey(result.map(({ groupName }) => groupName))
    setInfoList(result)
  }
  useEffect(() => {
    info && formatInfo(info)
  }, [JSON.stringify(info)])
  const clearFieldValue = (key) => {
    store.form.setFieldValue(key, '')
  }
  return (
    <>
      <div style={{ color: '#0058f9', padding: '12px 0' }}>{!!isFirst ? 2 : 1}. 请填写下列指标字段的数值</div>
      <Collapse activeKey={activeKey} onChange={setActiveKey}>
        {infoList.map(({ groupName, list }, index) => (
          <Panel header={groupName} key={groupName}>
            <Row gutter={12}>
              {list.map((item) => (
                <DynamicFormItem initialValues={initialValues} record={item} disabled={!auth} key={item.fieldName} clearFieldValue={clearFieldValue}/>
              ))}
            </Row>
          </Panel>
        ))}
      </Collapse>
    </>
  )
}

export default observer(Index)
