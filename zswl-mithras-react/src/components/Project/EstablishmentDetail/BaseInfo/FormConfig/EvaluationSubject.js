import { Descriptions, Form, Select } from 'antd'
import { observer } from '@zswl/admin'
import FormItemContent from '@/components/FormItemContent'
import StarDom from '@/components/StarDom'
import { BlackInfo } from '@/components/BlackGray/BlackInfo'
import { uniqBy } from 'lodash'
import { labelRed } from './utils'

const EvaluationSubject = ({
  showValue,
  onEvaluateMainChange,
  getDetailValue,
  getDetailChange,
}) => {
  return (
    <FormItemContent
      isChange={getDetailChange('evaluationSubjectId')}
      formContent={
        <Form.Item noStyle dependencies={['creditorInfo', 'lesseeInfo', 'guaranteeInfo']}>
          {({ getFieldValue, setFieldValue }) => {
            const lesseeInfoValue = getFieldValue('lesseeInfo')
            const creditorInfo = getFieldValue('creditorInfo')
            const guaranteeInfoValue = getFieldValue('guaranteeInfo')
            const result = [lesseeInfoValue, guaranteeInfoValue, creditorInfo]
              .flat()
              .map((item) => {
                if (item?.clientId) {
                  return { ...item.clientId }
                }
              })
            const selectOpt = uniqBy(result.filter(Boolean), 'value')
            const evaluateMainValue = getFieldValue('evaluationSubjectId')
            if (
              evaluateMainValue &&
              (selectOpt?.length === 0 ||
                !selectOpt?.find((item) => item.value == evaluateMainValue.value))
            ) {
              setFieldValue('evaluationSubjectId', undefined)
            }
            return (
              <Form.Item
                name="evaluationSubjectId"
                rules={[{ required: true, message: '请选择!' }]}
              >
                <Select
                  allowClear
                  labelInValue
                  onChange={onEvaluateMainChange}
                  options={selectOpt}
                  placeholder="请选择"
                  notFoundContent="请选择债权人或担保人"
                />
              </Form.Item>
            )
          }}
        </Form.Item>
      }
      value={
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          {getDetailValue('evaluationSubjectName')}
          <BlackInfo params={{ clientId: getDetailValue('evaluationSubjectId')?.value }} />
        </div>
      }
      showValue={showValue}
    />
  )
}

export default observer(EvaluationSubject)
