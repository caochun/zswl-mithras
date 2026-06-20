import { FormItemContent } from '@/components/Form'
import { App } from '@zswl/components'
import { Descriptions, Form, Input } from 'antd'
import FormListItem from './FormListItem'
import styles from './index.less'
import { observer } from '@zswl/admin'
const RentalPlan = ({ showValue, form, detail, isLog = true, businessKey }) => {
  const options = App.getData().optionsType
  const getDetailValue = (key) => {
    return detail[key]
  }
  const getDetailChange = (key) => {
    return false
  }
  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }
  return (
    <Descriptions dataSource={detail} title="" bordered column={2} labelStyle={{ background: '#F5F6FA' }} size={'small'} className={styles.des} >
      <Descriptions.Item
        label={'担保措施'}
        span={2}
        labelStyle={labelRed(getDetailChange('guaranteeMeasureDetails'))}
      >
        <FormItemContent
        formDataShow
          isChange={getDetailChange('guaranteeMeasureDetails')}
          formContent={
            <>
              <span className={styles.formTitle}>{options['guaranteeMeasuresTypeEnum'][0].label}</span>
              <Form.List  name="guaranteeMeasureDetails">
                {(fields,{ add, remove },index) => {
                      return (
                        <FormListItem detail={detail} businessKey={businessKey} enumType={options['guaranteeMeasuresTypeEnum'][0]} type={options['guaranteeMeasuresTypeEnum'][0].value} fields={fields} add={add} form={form} required remove={remove} scene="main" addText="添加"   fieldKey={'guaranteeMeasureDetails'} noClientType />
                      )
                }}
                </Form.List>
                <span className={styles.formTitle}>{options['guaranteeMeasuresTypeEnum'][1].label}</span>
                <Form.List name="guaranteeMeasureDetails">
                {(fields,{ add, remove },index) => {
                      return (
                        <FormListItem detail={detail}  businessKey={businessKey}  enumType={options['guaranteeMeasuresTypeEnum'][1]} type={options['guaranteeMeasuresTypeEnum'][1].value} fields={fields} add={add} form={form} required remove={remove} scene="main" addText="添加"  fieldKey={'guaranteeMeasureDetails'} noClientType />
                      )
                }}
                </Form.List>
                <Form.List name="guaranteeMeasureDetails">
                {(fields,{ add, remove }) => {
                      return (
                        <Form.Item key={0} style={{margin:' 5px 0 20px 0'}} label={'其他'} name={[0,'otherMessage']} >
                          <Input placeholder="请输入" maxLength={2500} />
                      </Form.Item>
                       )
                }} 
                </Form.List>
              </>
          }
          value={getDetailValue('guaranteeMeasureDetails')?<FormListItem.Detail detail={detail} enumTypeList={options['guaranteeMeasuresTypeEnum']} fieldKey={'guaranteeMeasureDetails'} values={getDetailValue('guaranteeMeasureDetails')} />:'-'}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'抵押标的'}
        span={2}
        labelStyle={labelRed(getDetailChange('pledgeMeasuresDetails'))}
      >
        <FormItemContent
        formDataShow
          isChange={getDetailChange('pledgeMeasuresDetails')}
          formContent={
            <>
              <span className={styles.formTitle}>{options['pledgeMeasuresTypeEnum'][0].label}</span>
              <Form.List  name="pledgeMeasuresDetails">
                {(fields,{ add, remove }) => {
                      return (
                        <FormListItem detail={detail}  businessKey={businessKey} keyId={'1'} type={options['pledgeMeasuresTypeEnum'][0].value} enumType={options['pledgeMeasuresTypeEnum'][0]} fields={fields} add={add} form={form} required remove={remove} scene="main" addText="添加"  fieldKey={'pledgeMeasuresDetails'} noClientType />
                      )
                }}
                </Form.List>
                <span className={styles.formTitle}>{options['pledgeMeasuresTypeEnum'][1].label}</span>
                <Form.List name="pledgeMeasuresDetails">
                {(fields,{ add, remove }) => {
                      return (
                        <FormListItem detail={detail}  businessKey={businessKey} keyId={'2'} type={options['pledgeMeasuresTypeEnum'][1].value} enumType={options['pledgeMeasuresTypeEnum'][1]} fields={fields} add={add} form={form} required remove={remove} scene="main" addText="添加"  fieldKey={'pledgeMeasuresDetails'} noClientType />
                      )
                }}
                </Form.List>
                <span className={styles.formTitle}>{options['pledgeMeasuresTypeEnum'][2].label}</span>
                <Form.List name="pledgeMeasuresDetails">
                {(fields,{ add, remove }) => {
                      return (
                        <FormListItem detail={detail}  businessKey={businessKey} keyId={'3'} type={options['pledgeMeasuresTypeEnum'][2].value} enumType={options['pledgeMeasuresTypeEnum'][2]} fields={fields} add={add} form={form} required remove={remove} scene="main" addText="添加"  fieldKey={'pledgeMeasuresDetails'} noClientType />
                      )
                }}
                </Form.List>
                <Form.List name="pledgeMeasuresDetails">
                {(fields,{ add, remove }) => {
                      return (
                        <Form.Item key={0} style={{margin:' 5px 0 20px 0'}} label={'其他'} name={[0,'otherMessage']} >
                          <Input placeholder="请输入" maxLength={2500} />
                      </Form.Item>
                      )
                }}
                </Form.List>
            </>
          }
          value={getDetailValue('pledgeMeasuresDetails')? <FormListItem.Detail detail={detail}  enumTypeList={options['pledgeMeasuresTypeEnum']} fieldKey={'pledgeMeasuresDetails'} values={getDetailValue('pledgeMeasuresDetails')} />:'-'}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        span={2}
        label={'其他风险缓释措施'}
        labelStyle={labelRed(getDetailChange('otherRiskMitigationMeasures'))}
      >
        <FormItemContent
        formDataShow
          isChange={getDetailChange('otherRiskMitigationMeasures')}
          formContent={
            <Form.Item name="otherRiskMitigationMeasures">
              <Input placeholder="请输入" maxLength={2500} />
            </Form.Item>
          }
          value={getDetailValue('otherRiskMitigationMeasures')}
          showValue={showValue}
        />
      </Descriptions.Item>
    </Descriptions>
  )
}
export default observer(RentalPlan)