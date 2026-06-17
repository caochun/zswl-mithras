import { useRef, useState, useEffect } from 'react'
import { observer, history, getQuery } from '@zswl/admin'
import { Button, Page, Form, ModalStore } from '@zswl/components'
import { DatePicker, Space, message, Modal, Tooltip } from 'antd'
import DataUpload from '@/components/DataUpload'
import IconFont from '@/components/Icon'
import moment from 'moment'
import { hasPermission, timeFormat } from '@/utils'
import ActualTable from '@/components/Contract/ActualTable'
import ContractIRR from '@/components/Contract/ContractIRR'
import { bizTypeMapText } from '@/components/Contract/bizTypeConfig'
import CantractDetailBaseInfo from '@/components/Contract/Detail/BaseInfo'
import CantractDetailBaoJia from '@/components/Contract/Detail/BaoJia'
import PaymentApply from '@/components/Contract/PaymentApply'
import ImportRent from './ImportRent'
import Api from './api'
import styles from './index.less'
import { validateModal } from '@/utils/modal'
import { checkIrr } from '../startRent/[id$]'

const { Item } = Form

function Index ({
  params: { id },
  query: {
    canEditFlags = 'true',
    bizType,
    businessVersion,
    taskStatus,
    taskActivityId,
    processInstanceId,
    modelKey,
  },
}) {
  let canEditFlagsFormAuth = canEditFlags === 'true'
  const isFormApproval = getQuery('typeId') == 'approval'
  const tab = getQuery('tab')
  // 合同起租-系统自动发起
  // ContractStartRentAutoFlow和ContractAddNewReceiptAutoFlow两个流程在发起人节点和项目主办节点都有导入实际租金表的按钮
  if (modelKey === 'ContractAddNewReceiptAutoFlow') {
    // 我收到的
    canEditFlagsFormAuth =
      tab === 'query'
        ? false
        : ['userTask_startUser', 'userTask_projectSponsor'].includes(taskActivityId)
  }
  const [paymemntList, setPaymentList] = useState([])
  const [contractInfo, setContractInfo] = useState({})

  const [form] = Form.useForm()
  const $tableRef = useRef(null)

  const getPaymentList = async (contractId) => {
    const { data } = await Api.getPaymentList({ contractId })
    setPaymentList(data)
  }

  const $createModal = new ModalStore({
    onFinish: async (values) => {
      const { fileList } = DataUpload.classify(values.file)
      await Api.addReceipt({
        contractId: id,
        file: fileList[0],
        paymentId: values.paymentId,
        receiptStartDate: values.receiptStartDate,
      })
      message.success('导入成功')
      $createModal.close()
      $tableRef.current.refresh()
    },
  })

  const onSubmit = async () => {

    await checkIrr({ contractId: id, operation: 'NEW_RECEIPT' })

    await Api.submitStart({
      contractId: id,
    })
    message.success('提交成功')
  }
  const onReceiveDataFromChild = (values) => {
    if (values.length > 0) {
      form.setFieldsValue({
        actualLeaseDate: values[0].actualStartDate ? moment(values[0].actualStartDate) : undefined,
      })
    }
    // 更新
    hasPermission('paymentnoreceiptlist') && getPaymentList(id)
  }

  const onReceiptRentDateChange = async (value, data) => {
    if (value) {
      const params = {
        receiptId: data.receiptId,
        receiptStartDate: timeFormat(value),
        processInstanceId: isFormApproval ? processInstanceId : undefined,
      }
      isFormApproval
        ? await Api.updateProcessActualStartDate(params)
        : await Api.updateEditActualStartDate(params)
      message.success('更新成功')
      $tableRef.current.refresh()
    }
  }

  const cancelFlow = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.cancelFlow({
          contractId: id,
        })
        message.success('操作成功')
        setTimeout(() => {
          history.push(`/contract/list`)
        }, 500)
      },
    })
  }

  const onEmitData = (data) => {
    setContractInfo(data)
  }

  return (
    <Page header={null}>
      <div className={styles.page}>
        <Form form={form}>
          <div className={styles.header}>
            <div className={styles.title}>新增投放</div>
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
              onEmitData={onEmitData}
              contractId={id}
              businessVersion={businessVersion}
              canEditFlag={false}
              isDetail
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
              canEdit={canEditFlagsFormAuth}
              taskStatus={taskStatus}
            ></PaymentApply>
          </div>
          <div className={styles.subHeader}>
            <h3>{`实际${bizTypeMapText[bizType]?.rentTitle}`}</h3>
            <Space>
              {/* <ContractIRR contractId={id} /> */}
              <Item
                label={`合同实际${bizTypeMapText[bizType]?.dateText}`}
                name={'actualLeaseDate'}
                rules={[{ required: true, message: '请选择日期' }]}
              >
                <DatePicker placeholder={'请选择'} style={{ width: 200 }} disabled />
              </Item>
              <Item>
                {/* 租赁、转租赁业务。非直租：存在关联显示，直租：隐藏 */}
                {paymemntList?.length > 0 && contractInfo.leaseType !== 'zhi_zu' && (
                  <Button
                    disabled={!canEditFlagsFormAuth}
                    onClick={$createModal.open}
                    type="primary"
                    icon={<IconFont type="icon-icon_add" />}
                  >
                    新增借据
                  </Button>
                )}
              </Item>
            </Space>
          </div>
        </Form>
        <div className={styles.wrap}>
          <ActualTable
            extra={(item, _index) => {
              return (
                <Form
                  initialValues={{
                    receiptStartDate: item.actualStartDate
                      ? moment(item.actualStartDate)
                      : undefined,
                  }}
                >
                  <Item label="借据起租日期" name={'receiptStartDate'}>
                    <DatePicker
                      placeholder={'请选择'}
                      onChange={(e) => onReceiptRentDateChange(e, item)}
                      disabled={item.isFirstReceipt === 1}
                    />
                  </Item>
                </Form>
              )
            }}
            businessVersion={businessVersion}
            bizType={bizType}
            ref={$tableRef}
            contractId={id}
            showSubTitle={contractInfo.leaseType !== 'zhi_zu'}
            onReceiveDataFromChild={onReceiveDataFromChild}
            showImportBtn={canEditFlagsFormAuth}
            showIRRTips={'NEW_RECEIPT'}
            // 债券转让、经营性租赁不显示,
            showTax={contractInfo.bizType !== 'ZR' && contractInfo.leaseType !== 'jyx_zu'}
          ></ActualTable>
        </div>
      </div>
      <ImportRent
        $createModal={$createModal}
        bizType={bizType}
        paymemntList={paymemntList}
      ></ImportRent>
    </Page>
  )
}

export default observer(Index)
