import ActualTable from '@/components/Contract/ActualTable'
import PaymentApply from '../PaymentApply'
import { bizTypeMapText } from '../bizTypeConfig'
import CantractDetailBaoJia from '../Detail/BaoJia'
import CantractDetailBaseInfo from '../BaseInfo'
import { formScrollToField, timeFormat } from '@/utils'
import { getQuery, history, observer } from '@zswl/admin'
import { Button, Form, Page } from '@zswl/components'
import { DatePicker, InputNumber, Modal, Space, message } from 'antd'
import moment from 'moment'
import { useRef, useState } from 'react'
import ZiLiao from '../StartRentMaterials'
import Api from './api'
import styles from './index.less'
import { validateModal } from '@/utils/modal'
import ContractIRR from '../ContractIRR'

const { Item } = Form

export const checkIrr = async (params, needDoubleConfirm = true) => {
  const res = await Api.checkIrr({
    operation: 'START_RENT',
    ...params
  })
  await validateModal({ title: '合同加权平均IRR低于最低IRR要求，请注意！' }, !res)
  needDoubleConfirm && await validateModal({ title: '确认执行吗' })
  return res
}

function Index ({
  params: { id },
  query: { canEditFlags = 'true', businessVersion, taskActivityId, taskStatus, modelKey },
}) {
  const [contractInfo, setContractInfo] = useState({})
  let canEditFlagsFormAuth = canEditFlags === 'true'
  const isFormApproval = getQuery('typeId') == 'approval'
  const tab = getQuery('tab')
  // 合同起租-系统自动发起
  // ContractStartRentAutoFlow和ContractAddNewReceiptAutoFlow两个流程在发起人节点和项目主办节点都有导入实际租金表的按钮
  if (modelKey === 'ContractStartRentAutoFlow') {
    // 我收到的
    canEditFlagsFormAuth =
      tab === 'query'
        ? false
        : ['userTask_startUser', 'userTask_projectSponsor'].includes(taskActivityId)
  }

  const [form] = Form.useForm()
  const $tableRef = useRef(null)
  const $ziLiaoRef = useRef(null)

  const onSubmit = async () => {
    await form
      .validateFields()
      .then(async (values) => {
        await checkIrr({ contractId: id })

        await Api.submitStart({
          actualLeaseDate: timeFormat(values.actualLeaseDate),
          contractId: id,
        })
        message.success('提交成功')
        $tableRef.current.refresh()
      })
      .catch((e) => {
        formScrollToField(e, form)
      })
  }

  const cancelFlow = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.cancelFlow({
          contractId: id,
        })
        message.info('操作成功')
        setTimeout(() => {
          history.push(`/contract/list`)
        }, 500)
      },
    })
  }

  const onDateChange = async (value) => {
    if (value) {
      let actualLeaseDate = timeFormat(value)
      await Api.updateDate({
        id,
        actualLeaseDate,
      })
      message.success('更新成功')
      $tableRef.current.refresh()
      $ziLiaoRef.current?.autoGenerate()

      form.setFieldsValue({
        receiptStartDate: moment(actualLeaseDate),
      })
    }
  }

  const onCantractInfoData = (data) => {
    setContractInfo(data)
  }

  return (
    <Page header={null}>
      <div className={styles.page}>
        <div className={styles.header}>
          <div className={styles.title}>合同起租</div>
          {!isFormApproval && canEditFlagsFormAuth && (
            <Space>
              <Button onClick={cancelFlow}>取消操作</Button>
              <Button type="primary" onClick={onSubmit}>
                提交审批
              </Button>
            </Space>
          )}
        </div>
        <div style={{ marginTop: 12 }}>
          <CantractDetailBaseInfo
            contractId={id}
            businessVersion={businessVersion}
            canEditFlag={false}
            isDetail
            onEmitData={onCantractInfoData}
          />
        </div>
        <div style={{ marginTop: 12 }}>
          <CantractDetailBaoJia
            contractId={id}
            businessVersion={businessVersion}
            canEditFlag={false}
          />
        </div>

        <div style={{ marginTop: 12 }}>
          <PaymentApply
            contractId={id}
            businessVersion={businessVersion}
            taskActivityId={taskActivityId}
            canEdit={false}
            taskStatus={taskStatus}
          ></PaymentApply>
        </div>

        <div className={styles.wrap}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <h3 style={{ marginTop: 20 }}>{`实际${bizTypeMapText[contractInfo.bizType]?.rentTitle
              }`}</h3>
          </div>
          <ActualTable
            extra={(item, index) => {
              return (
                <Form
                  form={form}
                  layout="inline"
                  initialValues={{
                    receiptStartDate: item.actualStartDate
                      ? moment(item.actualStartDate)
                      : undefined,
                    actualLeaseDate: item.actualStartDate
                      ? moment(item.actualStartDate)
                      : undefined,
                  }}
                >
                  <Item label={'借据起租日期'} name={'receiptStartDate'}>
                    <DatePicker placeholder={'请选择'} disabled />
                  </Item>
                  <Item
                    label={`合同实际${bizTypeMapText[contractInfo.bizType]?.dateText}`}
                    name={'actualLeaseDate'}
                    rules={[{ required: true, message: '请选择日期' }]}
                  >
                    <DatePicker
                      placeholder={'请选择'}
                      style={{ width: 200 }}
                      disabled={!canEditFlagsFormAuth}
                      onChange={onDateChange}
                    />
                  </Item>
                </Form>
              )
            }}
            ref={$tableRef}
            businessVersion={businessVersion}
            bizType={contractInfo.bizType}
            contractId={id}
            showImportBtn={canEditFlagsFormAuth}
            showSubTitle={false}
            showIRRTips={'START_RENT'}
            // 债券转让、经营性租赁不显示,
            showTax={contractInfo.bizType !== 'ZR' && contractInfo.leaseType !== 'jyx_zu'}
            onImportSuccess={() => $ziLiaoRef.current?.autoGenerate()}
          ></ActualTable>
        </div>

        <ZiLiao
          ref={$ziLiaoRef}
          contractId={id}
          businessVersion={businessVersion}
          title="起租材料"
          canEdit={canEditFlagsFormAuth}
        ></ZiLiao>
      </div>
    </Page>
  )
}

export default observer(Index)
