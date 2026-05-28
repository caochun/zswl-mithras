import { Descriptions, Input, InputNumber } from 'antd'
import { Form } from '@zswl/components'
import { observer } from '@zswl/admin'
import FormItemContent from '@/components/FormItemContent'
import StarDom from '@/components/StarDom'
import { FounderSelect } from '@/components'
import Api from '@/pages/afterLease/adjust/api'
import { amountStrToNumber, getInputNumberValueFromEvent, getInputNumberMonthProps } from '@/utils'
import mathjs from '@/utils/math'
import styles from '../../index.less'

const bizTypeMapExtTitle = {
  ZL: '新租赁期限(月)',
  ZZ: '新租赁期限(月)',
  BL: '新保理额度有效期(月)',
  ZR: '新转让额度有效期(月)',
}

const Index = ({ showValue, detail = {}, isLog, compareChangeList = [] }) => {
  const getDetailValue = (key) => {
    if (isLog) {
      return detail[key]?.value
    }
    return detail[key]
  }

  const getDetailChange = (key) => {
    if (isLog) {
      return detail[key]?.isChange
    }
    if (compareChangeList.length > 0) {
      return compareChangeList.indexOf(key) > -1
    }
    return false
  }

  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }

  return (
    <Descriptions
      title=""
      bordered
      column={2}
      labelStyle={{ background: '#F5F6FA' }}
      size={'small'}
      className={styles.des}
    >
      <Descriptions.Item label={<StarDom name="展期月数"></StarDom>}>
        <FormItemContent
          formContent={
            <div>
              <Form.Item
                name="extensionmonth"
                rules={[
                  {
                    required: true,
                    message: '请输入展期月数！',
                  },
                ]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  {...getInputNumberMonthProps()}
                  placeholder="请输入"
                />
              </Form.Item>
            </div>
          }
          value={getDetailValue('extensionmonth')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={bizTypeMapExtTitle[detail.bizType]}>
        <Form.Item dependencies={['extensionmonth']} noStyle>
          {({ getFieldValue }) => {
            const caclFn = (key, isCurrent) => {
              return isCurrent
                ? amountStrToNumber(getFieldValue(key)) || 0
                : amountStrToNumber(getDetailValue(key)) || 0
            }
            return (
              <FormItemContent
                formContent={
                  <div>
                    <Form.Item
                      style={{ width: '100%' }}
                      getValueFromEvent={getInputNumberValueFromEvent}
                    >
                      <Input
                        disabled
                        style={{ width: '100%' }}
                        placeholder="请输入"
                        value={mathjs.format(
                          mathjs.add(
                            caclFn('leaseMonthCount', true),
                            caclFn('extensionmonth', true)
                          )
                        )}
                      />
                    </Form.Item>
                  </div>
                }
                value={mathjs.format(
                  mathjs.add(caclFn('leaseMonthCount'), caclFn('extensionmonth'))
                )}
                showValue={showValue}
              />
            )
          }}
        </Form.Item>
      </Descriptions.Item>
      <Descriptions.Item
        label={'展期说明'}
        span={2}
        labelStyle={labelRed(getDetailChange('adjustExplain'))}
      >
        <FormItemContent
          isChange={getDetailChange('adjustExplain')}
          formContent={
            <Form.Item name="adjustExplain">
              <Input.TextArea />
            </Form.Item>
          }
          value={<p className={styles.textArea}>{getDetailValue('adjustExplain') || '-'}</p>}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'项目主办'}
        labelStyle={labelRed(getDetailChange('projSponsorUserName'))}
      >
        <FormItemContent
          isChange={getDetailChange('projSponsorUserName')}
          formContent={
            <Form.Item name="projSponsorUserName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('projSponsorUserName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'项目协办'}
        labelStyle={labelRed(getDetailChange('projCosponsorUserNames'))}
      >
        <FormItemContent
          isChange={getDetailChange('projCosponsorUserNames')}
          formContent={
            <Form.Item name="projCosponsorUserNames">
              <Input disabled></Input>
            </Form.Item>
          }
          value={getDetailValue('projCosponsorUserNames')?.join(',')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'业务部门'} labelStyle={labelRed(getDetailChange('bizDeptName'))}>
        <FormItemContent
          isChange={getDetailChange('bizDeptName')}
          formContent={
            <Form.Item name="bizDeptName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('bizDeptName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'业务部门负责人'}
        labelStyle={labelRed(getDetailChange('bizDeptLeaderName'))}
      >
        <FormItemContent
          isChange={getDetailChange('bizDeptLeaderName')}
          formContent={
            <Form.Item name="bizDeptLeaderName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('bizDeptLeaderName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'业务分管领导'}
        labelStyle={labelRed(getDetailChange('bizDivisionLeaderName'))}
      >
        <FormItemContent
          isChange={getDetailChange('bizDivisionLeaderName')}
          formContent={
            <Form.Item name="bizDivisionLeaderName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('bizDivisionLeaderName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name="风控经理" />}
        labelStyle={labelRed(getDetailChange('riskControlManagerId'))}
      >
        <FormItemContent
          isChange={getDetailChange('riskControlManagerId')}
          formContent={
            <Form.Item
              name="riskControlManagerId"
              rules={[{ required: true, message: '请选择风控经理!' }]}
            >
              <FounderSelect
                key="riskControlManagerId"
                functionCode="selectfounder-adjust"
                params={{ job: 'riskmanager' }}
              ></FounderSelect>
            </Form.Item>
          }
          value={getDetailValue('riskControlManagerName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name="法务经理" />}
        labelStyle={labelRed(getDetailChange('legalManagerUserId'))}
      >
        <FormItemContent
          isChange={getDetailChange('legalManagerUserId')}
          formContent={
            <Form.Item
              name="legalManagerUserId"
              rules={[{ required: true, message: '请选择法务经理!' }]}
            >
              <FounderSelect
                key="legalManagerUserId"
                functionCode="selectfounder-adjust"
                params={{ job: 'legalmanager' }}
              ></FounderSelect>
            </Form.Item>
          }
          // value={getDetailValue('legalManagerUserId')?.label}
          value={getDetailValue('legalManagerName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item></Descriptions.Item>
    </Descriptions>
  )
}

export default observer(Index)
