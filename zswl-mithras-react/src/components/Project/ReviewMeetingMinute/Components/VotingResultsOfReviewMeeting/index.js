import { FormItemContent, StarDom } from '@/components/Form'
import { observer } from '@zswl/admin'
import { Descriptions, Form, InputNumber, Radio, Input } from 'antd'
import { getInputNumberProps } from '@/utils'
import { App } from '@zswl/components'
import styles from './index.less'
import { isArray } from 'lodash'

function Index({ form, showValue, detail, isProjModify }) {
  const options = App.getData().optionsType
  const getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (options && options[key]) {
      options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  const getDetailChange = (key) => {
    return false
  }
  const getDetailValue = (key) => {
    return detail[key]
  }
  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }
  function validateDate(dateString) {
    const regex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/
    return regex.test(dateString)
  }
  function validateDate2(dateString) {
    const regex = /^\d{4}$/
    return regex.test(dateString)
  }
  const validateLTime2 = (r, value) => {
    if (!value) {
      return Promise.reject(`请输入!`)
    }
    if (!validateDate2(value)) {
      return Promise.reject(`日期格式有误 YYYY`)
    }
    return Promise.resolve()
  }
  const validateLTime = (r, value) => {
    if (!value) {
      return Promise.reject(`请输入!`)
    }
    if (!validateDate(value)) {
      return Promise.reject(`日期格式有误 YYYY-MM-DD`)
    }
    return Promise.resolve()
  }
  return (
    <Descriptions
      className={styles.des}
      title=""
      bordered
      column={2}
      labelStyle={{ background: '#F5F6FA' }}
      size={'small'}
    >
      <Descriptions.Item
        label={<StarDom name={'表决信息为:'} />}
        span={2}
        labelStyle={labelRed(getDetailChange('projName'))}
        style={{ lineHeight: '50px' }}
      >
        <FormItemContent
          formDataShow
          isChange={getDetailChange('projName')}
          formContent={
            <div className={styles.VotingResultsOfReviewMeeting}>
              根据《浙江浙商融资租赁有限公司项目评审委员会会议制度》，于
              <Form.Item
                name="reportIssuanceTime"
                style={{ display: 'inline-block', width: '120px' }}
                rules={[
                  {
                    required: true,
                    validator: validateLTime,
                  },
                ]}
              >
                <Input />
              </Form.Item>
              {/* {getDetailValue('reportIssuanceTime')} */}
              召开浙江浙商融资租赁有限公司评审委员会
              <Form.Item
                name="reportIssuanceYear"
                style={{ display: 'inline-block', width: '80px' }}
                rules={[
                  {
                    required: true,
                    validator: validateLTime2,
                  },
                ]}
              >
                <InputNumber {...getInputNumberProps()} />
              </Form.Item>
              {/* {getDetailValue('reportIssuanceYear')} */}
              年第
              <Form.Item
                name="reportIssuanceNumber"
                style={{ display: 'inline-block', width: '80px' }}
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber {...getInputNumberProps()} />
              </Form.Item>
              {/* {getDetailValue('reportIssuanceNumber')} */}次{isProjModify ? '临时' : ''}
              审议会议，集体审议了本项目{isProjModify ? '变更' : ''}。本次参会表决委员共
              <Form.Item
                name="reportNumberVoters"
                style={{ display: 'inline-block', width: '80px' }}
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber {...getInputNumberProps()} />
              </Form.Item>
              人， 其中
              <Form.Item
                style={{ display: 'inline-block', width: '80px' }}
                name="reportNumberAgree"
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber {...getInputNumberProps()} />
              </Form.Item>
              人同意；
              <Form.Item
                style={{ display: 'inline-block', width: '80px' }}
                name="reportNumberConditionalAgree"
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber {...getInputNumberProps()} />
              </Form.Item>
              人有条件同意；
              <Form.Item
                name="reportNumberAgainst"
                style={{ display: 'inline-block', width: '80px' }}
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber {...getInputNumberProps()} />
              </Form.Item>
              人反对。
            </div>
          }
          value={
            getDetailValue('reportIssuanceTime') ? (
              <span>
                根据《浙江浙商融资租赁有限公司项目评审委员会会议制度》，于
                {getDetailValue('reportIssuanceTime')}召开浙江浙商融资租赁有限公司评审委员会
                {getDetailValue('reportIssuanceYear')}年第{getDetailValue('reportIssuanceNumber')}次
                {isProjModify ? '临时' : ''}审议会议，集体审议了本项目{isProjModify ? '变更' : ''}
                。本次参会表决委员共
                {getDetailValue('reportNumberVoters')}人,其中{getDetailValue('reportNumberAgree')}
                人同意；{getDetailValue('reportNumberConditionalAgree')}人有条件同意；
                {getDetailValue('reportNumberAgainst')}人反对。
              </span>
            ) : (
              '-'
            )
          }
          showValue={showValue}
        />
      </Descriptions.Item>

      <Descriptions.Item
        span={2}
        label={'表决委员为:'}
        labelStyle={labelRed(getDetailChange('votingCommittee'))}
      >
        {getDetailValue('votingCommittee') &&
        Array.isArray(JSON.parse(getDetailValue('votingCommittee')))
          ? JSON.parse(getDetailValue('votingCommittee'))
              ?.map((item) => item)
              .join(',')
          : getDetailValue('votingCommittee') || '-'}
      </Descriptions.Item>
      <Descriptions.Item
        span={2}
        label={<StarDom name={'表决结果为:'} />}
        labelStyle={labelRed(getDetailChange('votingResult'))}
      >
        <FormItemContent
          formDataShow
          isChange={getDetailChange('votingResult')}
          formContent={
            <Form.Item name="votingResult" rules={[{ required: true, message: '请选择!' }]}>
              <Radio.Group mode="multiple" options={options.votingResultTypeEnum}></Radio.Group>
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('votingResultTypeEnum')[getDetailValue('votingResult')]}
          showValue={showValue}
        />
      </Descriptions.Item>
    </Descriptions>
  )
}

export default observer(Index)
