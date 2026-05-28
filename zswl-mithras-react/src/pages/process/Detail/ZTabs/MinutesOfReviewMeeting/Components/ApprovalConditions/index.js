import { observer } from '@zswl/admin'
import { Button, Col, Descriptions, Form, Input, Row, Space } from 'antd'
import FormItemContent from '@/components/FormItemContent'
import styles from './index.less'
import IconFont from '@/components/Icon'
import AddModal from '@/pages/lease/tracking/AddModal'
import FormList from '../FormList'

function Index({ showValue, detail, businessKey, store, detailData, projName }) {
  const { onTrackEventList, trackEventText, onTrackEventClose } = store
  const getDetailChange = (key) => {
    return false
  }
  const getDetailValue = (key) => {
    return detail[key]
  }
  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }
  const TrackModalChange = (val) => {
    if (!val) {
      onTrackEventList(detail.id, detailData.businessKey, detailData.processInstanceId)
    }
  }
  const remove = (val) => {
    onTrackEventClose(val.id).then(() => {
      onTrackEventList(detail.id, detailData.businessKey, detailData.processInstanceId)
    })
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
        label={'特殊合同条款'}
        span={2}
        labelStyle={labelRed(getDetailChange('specialContractTerms'))}
      >
        <FormItemContent
          isChange={getDetailChange('specialContractTerms')}
          formContent={<FormList name="specialContractTerms" />}
          value={getDetailValue('specialContractTermsList')?.map((v) => <div>{v}</div>) || '-'}
          showValue={showValue}
        />
      </Descriptions.Item>

      <Descriptions.Item
        label={'放款前须落实条件'}
        span={2}
        labelStyle={labelRed(getDetailChange('conditionsBeforeDisbursement'))}
      >
        <FormItemContent
          isChange={getDetailChange('conditionsBeforeDisbursement')}
          formContent={<FormList name="conditionsBeforeDisbursement" />}
          value={
            getDetailValue('conditionsBeforeDisbursementList')?.map((v) => <div>{v}</div>) || '-'
          }
          showValue={showValue}
        />
      </Descriptions.Item>

      <Descriptions.Item
        label={'管理要求'}
        span={2}
        labelStyle={labelRed(getDetailChange('managementRequirement'))}
      >
        <FormItemContent
          isChange={getDetailChange('managementRequirement')}
          formContent={
            <>
              <Form.Item label={'租后要求'}>
                <div>
                  {
                    <div className={styles.trackModalP}>
                      <div
                        className={styles.icon3}
                        style={{
                          marginRight: 8,
                        }}
                      >
                        <IconFont type="icon-icon_add" />
                        <div className={styles.TrackModal}>
                          <AddModal
                            params={{ projName: projName }}
                            TrackModalChange={TrackModalChange}
                            projReviewMeetMinuteId={detail.id}
                            detail={detail}
                            canEdit={false}
                          ></AddModal>
                        </div>
                      </div>
                      <span>添加</span>
                    </div>
                  }
                  {store.trackEventText &&
                    store.trackEventText.map((v, i) => {
                      return (
                        <div className={styles.trackModalP}>
                          <span className={styles.text}>{v.taskContent}</span>
                          <IconFont
                            onClick={() => remove(v)}
                            className={styles.iconDelete}
                            type="icon-icon_delete"
                          />
                        </div>
                      )
                    })}
                </div>
              </Form.Item>
              <Form.Item label={'限额要求'} name="limitRequirement" style={{padding:'10px 0'}}>
                <Input placeholder="请输入" maxLength={2500}/>
              </Form.Item>
              <Form.Item label={'其他要求'} name="managementRequirement">
                <Input placeholder="请输入" maxLength={2500} />
              </Form.Item>
            </>
          }
          value={
            <>
              {store.trackEventText && store.trackEventText.length > 0 && (
                <>
                  <div>租后要求:</div>
                  {store.trackEventText.map((v, i) => {
                    return <div>{v.taskContent}</div>
                  })}
                </>
              )}
              {getDetailValue('limitRequirement') && (
                <div className={styles.padding5}>
                  {'限额要求: ' + getDetailValue('limitRequirement')}
                </div>
)              }
              {getDetailValue('managementRequirement') && (
                <div className={styles.padding5}>
                  {'其他要求: ' + getDetailValue('managementRequirement')}
                </div>
              )}
              {!store.trackEventText && !getDetailValue('managementRequirement') && '-'}
            </>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
    </Descriptions>
  )
}

export default observer(Index)
