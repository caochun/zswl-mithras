import { Amount } from '@/components/Format'
import { FounderSelect } from '@/components/Select'
import { FormAmount } from '@/components/Form'
import { CpmPaymentApplicationPublicInformation as PublicInformation } from '@/components/Cpm/PaymentApplicationPublicInfoEntries'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import { rules } from '@/utils'
import { observer } from '@zswl/admin'
import { Form, Select } from '@zswl/components'
import { DatePicker, InputNumber, Radio } from 'antd'
import moment from 'moment'
import numeral from 'numeral'
import { useRef } from 'react'
import styles from '../index.less'
import { shouldShowCreditWithdrawalForm } from '../utils'
import ContractIRR from './ContractIRR'

const { Item } = Form
const disabledDate = (current) => {
  return current && moment(current).isBefore(moment(), 'day')
}

const Index = ({ onAssetClassifyQualitativeAdjust, assetClassifyDisabled, store }) => {

  const publicInfoRef = useRef()
  store.publicInfoRef = publicInfoRef
  const { detailData } = useFlowData()
  const { dynamicFormKeyList, dynamicFormData, businessKey, taskActivityId, processStatus } = detailData
  const dynamicFormKeysForRender = dynamicFormKeyList?.filter(
    (key) => key !== 'projReview_setCreditWithdrawal' || shouldShowCreditWithdrawalForm(dynamicFormData)
  )

  const form = Form.useFormInstance()
  const renderDynamicFormItem = (list) => {
    const result = list?.map((key) => {
      return {
        // 合同印花税财务经理节点
        contract_updateStampDuty: (
          <Form.List name="dutyFormRSPList">
            {(fields) => {
              return fields?.map(({ name, key }, index) => {
                return (
                  <div key={key} className={styles.formList}>
                    <Item label={'借据编号'}>{dynamicFormData.contract_updateStampDuty?.dutyFormRSPList?.[index]?.receiptCode}</Item>
                    <Item name={[name, 'stampDuty']} label={`印花税`} rules={[rules.required()]}>
                      <Amount>
                        <InputNumber addonAfter="元" precision={2}></InputNumber>
                      </Amount>
                    </Item>
                  </div>
                )
              })
            }}
          </Form.List>
        ),
        projReview_setApprovedAmount: <FormAmount.Item name={'approvedAmount'} label={'项目批复金额(元)'} disabled></FormAmount.Item>,
        group_projReview_setApprovedAmount: <FormAmount.Item name={'approvedAmount'} label={'项目批复金额(元)'}></FormAmount.Item>,
        risk_opinion_handle_type: (
          <Item label={'是否处置'} name={'handleResult'}>
            <Radio.Group>
              <Radio value={1}>处理</Radio>
              <Radio value={0}>关闭</Radio>
            </Radio.Group>
          </Item>
        ),
        // 付款申请最低irr
        payment_updateIRR: (
          <>
            <Item name={'lowestIrr'} label={'最低irr'} rules={[{ required: true, message: '请输入' }]}>
              <Amount>
                <InputNumber
                  addonAfter="%"
                  formatter={(value) => {
                    return value && numeral(value).format('0,0.[00]')
                  }}
                ></InputNumber>
              </Amount>
            </Item>
            <div style={{ position: 'relative' }}>
              <Item name={'publicInfoConfirm'} label={'公开信息查询确认'} rules={[{ required: true, message: '请输入' }]}>
                <Select options={[{ value: 1, label: '已确认' }]} style={{ width: 220 }} />
              </Item>
              <PublicInformation
                ref={publicInfoRef}
                paymentId={businessKey}
                taskActivityId="userTask_projectmanager"
                style={{ position: 'absolute', left: 'calc(16% + 220px)', top: 0 }}
              />
            </div>
          </>
        ),
        // 合同变更-提前还款流程财务确认节点增加动态表单
        contract_early_repay_financial_confirm: (
          <Form.Item name="isPass" label={'审批通过'} rules={[rules.required('请选择')]}>
            <Select style={{ width: '100%' }} options={'yesOrNo'} placeholder="请选择" />
          </Form.Item>
        ),
        asset_classify_qualitative_adjust: (
          <Form.Item name="asset_classify_qualitative_adjust" label={'定性调整'} rules={[rules.required('请选择')]}>
            <Radio.Group onChange={onAssetClassifyQualitativeAdjust}>
              <Radio value={0}>否</Radio>
              <Radio value={1}>是</Radio>
            </Radio.Group>
          </Form.Item>
        ),
        // 舆情
        risk_opinion_asset_management: (
          <Form.Item name="warnLevel" label={'确认预警信号'} rules={[rules.required('请选择')]}>
            <Select style={{ width: '100%' }} options={'warnLevelEnum'} placeholder="请选择" />
          </Form.Item>
        ),
        asset_classify_result: (
          <Form.Item name="asset_classify_result" label={'确认分类'} rules={[rules.required('请选择')]}>
            <Select disabled={assetClassifyDisabled} style={{ width: '100%' }} options={'assetClassifyResultEnum'} placeholder="请选择" />
          </Form.Item>
        ),
        projReview_chooseJudges: (
          <Form.Item name="userId" label={'项目评审委员'}>
            <FounderSelect params={{ job: 'expertlibrary,Jury' }} mode="multiple" labelInValue functionCode="selectfounder-3" />
          </Form.Item>
        ),
        projReview_showDirectors: (
          <Form.Item name="userId" disabled label={'董事会成员'}>
            <Select mode={'multiple'} style={{ width: '100%' }} disabled placeholder="请选择" />
          </Form.Item>
        ),
        projReview_pricingChooseAdjudicator: (
          <Form.Item name="adjudicator" label={'终审人'}>
            <Select style={{ width: '100%' }} placeholder="请选择" options={'pricingFinalAdjudicator'} />
          </Form.Item>
        ),
        ftp_chooseJudges: (
          <Form.Item name="userId" label={'定价委员会'}>
            <FounderSelect params={{ job: 'pricingcommitteemember' }} mode="multiple" labelInValue />
          </Form.Item>
        ),
        projReview_setReviewMeetingPlanDate: (
          <Form.Item name="reviewMeetingPlanDate" label={'评审会预计召开时间'} required>
            <DatePicker style={{ width: '100%' }} placeholder="请选择" disabledDate={disabledDate} />
          </Form.Item>
        ),
        projReview_setDirectorMeetingPlanDate: (
          <Form.Item name="directorMeetingPlanDate" label={'董事会预计召开时间'} required>
            <DatePicker style={{ width: '100%' }} placeholder="请选择" />
          </Form.Item>
        ),
        projReview_setNeedBorad: (
          <Form.Item name="needBorad" label={'是否需要董事会'} required>
            <Select options={'trueOrFalse'} placeholder="请选择" />
          </Form.Item>
        ),
        projReview_pricingChooseApproveAuth: (
          <div>
            <Form.Item name="approveAuth" label={'审批权限'} rules={[rules.required('请选择')]}>
              <Select placeholder="请选择" options={'pricingApproveAuthEnum'} disabled={!dynamicFormData.projReview_pricingChooseApproveAuth?.canChoose} />
            </Form.Item>
            <Form.Item dependencies={['approveAuth']} noStyle>
              {({ getFieldValue, setFieldValue }) => {
                if (getFieldValue('approveAuth') === 'priceCommittee') {
                  setFieldValue(
                    'approvalUserIdList',
                    dynamicFormData.projReview_pricingChooseApproveAuth?.approvalUserIdList?.map((userId) => {
                      return +userId
                    })
                  )
                  return (
                    <Form.Item initialValue={[]} name="approvalUserIdList" label={'定价委员会人员'} rules={[rules.required('请选择')]}>
                      <FounderSelect params={{ job: 'pricingcommitteemember' }} mode="multiple" labelInValue />
                    </Form.Item>
                  )
                }
                return null
              }}
            </Form.Item>
          </div>
        ),
        projReview_lawManagerReview: (
          <Form.Item name="userId" label={'法务经理复核'} required>
            <FounderSelect params={{ job: 'legalmanager' }} labelInValue functionCode="selectfounder-3" />
          </Form.Item>
        ),
        afterLeaseCheckReport_assetManager: (
          <Form.Item name="userId" label={'资产管理复核'} required>
            <FounderSelect params={{ job: 'assetmanagement' }} labelInValue functionCode="selectfounder-3" />
          </Form.Item>
        ),
        contract_changeIrrForm: (
          <Form.Item name="contractIrr" label={'IRR'} required>
            <ContractIRR />
          </Form.Item>
        ),

        early_warning_monitor_after_loan_assetManager: (
          <Form.Item name="userId" label={'资产管理复核'} required>
            <FounderSelect params={{ job: 'assetmanagement' }} labelInValue functionCode="selectfounder-3" />
          </Form.Item>
        ),
        risk_opinion_dispose_after_loan_assetManager: (
          <Form.Item name="userId" label={'资产管理复核'} required>
            <FounderSelect params={{ job: 'assetmanagement' }} labelInValue functionCode="selectfounder-3" />
          </Form.Item>
        ),
        risk_control_payment_assetManager: (
          <Form.Item name="userId" label={'资产管理复核'} required>
            <FounderSelect params={{ job: 'assetmanagement' }} labelInValue functionCode="selectfounder-3" />
          </Form.Item>
        ),
        projReview_setCreditWithdrawal: (
          <Form.Item
            className={styles.creditWithdrawalFormItem}
            name={taskActivityId?.includes('lawManager') ? 'groupCreditWithdrawalLaw' : 'groupCreditWithdrawalRisk'}
            label={'是否符合集团授信提款条件'}
            rules={[rules.required('请选择')]}
          >
            <Select
              style={{ width: '100%' }}
              placeholder="请选择"
              disabled={['userTask_riskManager_back', 'userTask_lawManager_back', 'userTask_lawManager_back_review'].includes(taskActivityId)}
              options={[
                { value: '1', label: '符合' },
                { value: '0', label: '不符合（需提交评审会审批）' },
              ]}
            />
          </Form.Item>
        ),
        // follow_up_rental_inspection_form: (
        //   <div>
        //     <Form.Item
        //       name="nextCheckWay"
        //       label={'下次租后检查形式'}
        //       rules={
        //         ['userTask_assetManager', 'assetManagementReview'].includes(taskActivityId) &&
        //         ['1'].includes(processStatus) && [rules.required('请选择')]
        //       }
        //     >
        //       <Select
        //         placeholder="请选择"
        //         options={'afterLeaseCheckWayEnum'}
        //         onChange={(value) => {
        //           if (value === 'WITHOUT_CHECK') {
        //             form.setFieldValue('nextDeadline', null)
        //           }
        //         }}
        //         disabled={
        //           !(
        //             ['userTask_assetManager', 'assetManagementReview'].includes(taskActivityId) &&
        //             ['1'].includes(processStatus)
        //           )
        //         }
        //       />
        //     </Form.Item>
        //     <Form.Item dependencies={['nextCheckWay']} noStyle>
        //       {({ getFieldValue, setFieldValue }) => {
        //         const disabled =
        //           getFieldValue('nextCheckWay') === 'WITHOUT_CHECK' ||
        //           !(
        //             ['userTask_assetManager', 'assetManagementReview'].includes(taskActivityId) &&
        //             ['1'].includes(processStatus)
        //           )
        //         return (
        //           <Form.Item
        //             name="nextDeadline"
        //             label={'下次租后检查截止日'}
        //             rules={!disabled ? [rules.required('请选择')] : []}
        //           >
        //             <DatePicker disabled={disabled} />
        //           </Form.Item>
        //         )
        //       }}
        //     </Form.Item>
        //   </div>
        // ),
      }[key]
    })
    return result?.filter(Boolean)
  }
  return <div>{dynamicFormKeysForRender?.length > 0 ? renderDynamicFormItem([...dynamicFormKeysForRender]) : null}</div>
}

export default observer(Index)
