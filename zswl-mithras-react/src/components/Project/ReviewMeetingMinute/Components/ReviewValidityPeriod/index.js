import { FormItemContent, StarDom } from '@/components/Form'
import { observer } from '@zswl/admin'
import { Descriptions, Form, Radio,Input  } from 'antd'
import dayjs from 'dayjs';
import styles from './index.less'
import { App } from '@zswl/components'

function ProjectReviewMeetingMinuteReviewValidityPeriod({ showValue,detail }) {
    const options = App.getData().optionsType
    // const creditTimeInit = (val) =>{
    //     // 判断加几个月
    //     const newDate = dayjs(detail['reportIssuanceTime']).add(val === 'SIX_MONTHS' ? 6:12, 'month').format('YYYY-MM-DD');
    //     // return newDate
    //     this.form.setFieldsValue({creditExpirationDate:newDate})
    // }
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
  return (
    <Descriptions className={styles.des} title="" bordered column={2} labelStyle={{ background: '#F5F6FA' }} size={'small'} >
        <Descriptions.Item span={2} label={<StarDom name={'总经理/董事会审批结束:'} />}  labelStyle={labelRed(getDetailChange('votingPeriodValidity'))}>
            <FormItemContent
            formDataShow
                isChange={getDetailChange('votingPeriodValidity')}
                formContent={
                    <Form.Item
                        name="votingPeriodValidity"
                        rules={[{ required: true,message:"请选择!" }]}
                    >
                        <Radio.Group
                            options={options.votingPeriodValidityEnum}
                        ></Radio.Group>
                    </Form.Item>
                }
                value={getKeyOptionsLabelMap('votingPeriodValidityEnum')[getDetailValue('votingPeriodValidity')]}
                showValue={showValue}
            />
        </Descriptions.Item>
        <Descriptions.Item span={2} label={'授信到期日'} labelStyle={labelRed(getDetailChange('projName'))}>
            <span>{ getDetailValue('creditExpirationDate')?getDetailValue('creditExpirationDate'):'-'}</span> 
        </Descriptions.Item>
    </Descriptions>
  )
}

export default observer(ProjectReviewMeetingMinuteReviewValidityPeriod)
