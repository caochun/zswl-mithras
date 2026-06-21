import { observer } from '@zswl/admin'
import { Select, Form, DatePicker } from '@zswl/components'
import { useEffect } from 'react'
import FileTable from './FileTable'
import { useFlowData, fileKeyEnum } from '@/utils/domains/process/ProcessFlowContext'
import store from './store'
import styles from './index.less'
import { rules } from '@/utils'

function ProcessCompleteOperation() {
  const { detailData: detail } = useFlowData()
  const {
    processInstanceId,
    taskId,
    dynamicFormData,
    dynamicFormKeyList,
    mainModule,
    businessKey,
    taskActivityId,
    processStatus,
  } = detail
  const { founderList } = store

  useEffect(() => {
    store.searchFounder('', '1')
  }, [])

  const fileKeyList = Object.keys(fileKeyEnum)
  const dynamicForm = (key) => {
    // 立项创建，审批节点有变动，其实projEstablish_setRiskManager已废弃，暂不删除了
    if (['projEstablish_setRiskManager'].includes(key)) {
      return (
        <Form.Item name="userId" label={'风控经理'} rules={[{ required: true }]}>
          <Select
            style={{ width: '100%' }}
            labelInValue
            mode="multiple"
            options={founderList}
            placeholder="请选择"
            onSearch={(e) => {
              store.searchFounder(e, '1')
            }}
          />
        </Form.Item>
      )
    } else if (['projReview_setProjectClassify'].includes(key)) {
      return (
        <Form.Item name="projectClassify" label={'项目分类'} rules={[{ required: true }]}>
          <Select
            style={{ width: '100%' }}
            // labelInValue
            options={'projectClassify'}
            placeholder="请选择"
          />
        </Form.Item>
      )
    } else if (['follow_up_rental_inspection_form'].includes(key)) {
      return (
        <div>
          <Form.Item
            name="nextCheckWay"
            label={'下次租后检查形式'}
            rules={
              ['userTask_assetManager', 'assetManagementReview'].includes(taskActivityId) &&
              ['1'].includes(processStatus) && [rules.required('请选择')]
            }
          >
            <Select
              placeholder="请选择"
              style={{ width: 200 }}
              options={'afterLeaseCheckWayEnum'}
              onChange={(value) => {
                if (value === 'WITHOUT_CHECK') {
                  store.completeOperation.setFieldValue('nextDeadline', null)
                }
              }}
              disabled={
                !(
                  ['userTask_assetManager', 'assetManagementReview'].includes(taskActivityId) &&
                  ['1'].includes(processStatus)
                )
              }
            />
          </Form.Item>
          <Form.Item dependencies={['nextCheckWay']} noStyle>
            {({ getFieldValue, setFieldValue }) => {
              const disabled =
                getFieldValue('nextCheckWay') === 'WITHOUT_CHECK' ||
                !(
                  ['userTask_assetManager', 'assetManagementReview'].includes(taskActivityId) &&
                  ['1'].includes(processStatus)
                )
              return (
                <Form.Item
                  name="nextDeadline"
                  label={'下次租后检查截止日'}
                  rules={!disabled ? [rules.required('请选择')] : []}
                >
                  <DatePicker disabled={disabled} />
                </Form.Item>
              )
            }}
          </Form.Item>
        </div>
      )
    } else if (fileKeyList.includes(key)) {
      const item = fileKeyEnum[key]
      const { label, materialsType, ...rest } = item
      return (
        <FileTable
          detail={detail}
          label={label}
          params={{
            processInstanceId,
            taskId,
            businessKey,
            materialsTypeList: [materialsType],
            moduleType: mainModule,
          }}
          materialsType={materialsType}
          {...rest}
        />
      )
    }
  }

  useEffect(() => {
    if (Object.keys(dynamicFormData ?? {}).length === 0) return
    if (dynamicFormKeyList.includes('projEstablish_setRiskManager')) {
      store.completeOperation.setFieldsValue({
        userId: dynamicFormData.projEstablish_setRiskManager,
      })
    }
    // 项目分类/项目评审流程: 秘书会票节点  这个节点增加项目分类
    if (dynamicFormKeyList.includes('projReview_setProjectClassify')) {
      store.completeOperation.setFieldsValue({
        projectClassify:
          dynamicFormData.projReview_setProjectClassify &&
          dynamicFormData.projReview_setProjectClassify?.projectClassify,
      })
    }
    if (dynamicFormKeyList.includes('follow_up_rental_inspection_form')) {
      store.completeOperation.setFieldsValue({
        nextCheckWay: dynamicFormData.follow_up_rental_inspection_form?.nextCheckWay,
        nextDeadline: dynamicFormData.follow_up_rental_inspection_form?.nextDeadline,
      })
    }
  }, [dynamicFormKeyList, dynamicFormData])

  return (
    <>
      <div className={styles.uploadList}>
        <Form preserve={false} store={store.completeOperation} cache="false" layout={'horizontal'}>
          {dynamicFormKeyList.length > 0 &&
            dynamicFormKeyList.map((v) => {
              return dynamicForm(v)
            })}
        </Form>
        {detail.collaborateFlag && (
          <FileTable
            detail={detail}
            label={'协同文件'}
            params={{
              processInstanceId,
              taskId,
              businessKey,
              materialsTypeList: ['OTHER'],
              moduleType: mainModule,
            }}
            materialsType={'OTHER'}
          />
        )}
      </div>
    </>
  )
}

export default observer(ProcessCompleteOperation)
