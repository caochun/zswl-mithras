import { useEffect, useMemo, useRef, useState } from 'react'
import { observer, getQuery, history } from '@zswl/admin'
import { Button, Page, Form } from '@zswl/components'
import { Space, DatePicker } from 'antd'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import moment from 'moment'
import ActualTable from '../ActualTable'
import Protocol from '../ChangeProtocol'
import { bizTypeMapText } from '../bizTypeConfig'
import LPR from './LPR'
import Ahead from './Ahead'
import Exhibi from './Exhibi'
import Store from './store'
import CantractDetailBaseInfo from '../BaseInfo'
import styles from './index.less'
import { BusinessInfoCheck } from '@/components/BusinessInfoCheck/BusinessInfoCheckEntries'
import { EditDescription } from '@/components/Table'
import { TextAreaColumn } from '@/components/Format'
import { TrackEventModal as TrackModal } from '@/components/TrackEvent/TrackEventModalEntries'

const { Item } = Form

function Index({
  params: { id },
  query: {
    changeType,
    canEditFlags = 'true',
    bizType,
    businessVersion,
    processInstanceId,
    modelKey,
    taskActivityId,
    taskStatus,
  },
}) {
  const isFormApproval = getQuery('typeId') == 'approval'
  const [form] = Form.useForm()

  const store = useMemo(() => {
    return new Store({ isFormApproval, id, bizType, changeType, businessVersion, form })
  }, [isFormApproval, id, bizType, changeType, businessVersion, form])

  let canEditFlagsFormAuth = canEditFlags === 'true'
  // 项目经理修改节点，可修改
  if (
    changeType === 'EARLY_REPAYMENT' &&
    taskStatus === '1' &&
    taskActivityId === 'userTask_startUserModify'
  ) {
    canEditFlagsFormAuth = true
  }

  const [detail, setDetail] = useState({})
  const onCantractInfoData = (data) => {
    setDetail(data)
  }
  const { showVal, setShowVal, earlyRepaymantDetail, baseinfo, old_baseinfo, isLoading } = store
  const { contractCode, curAssigneeIds } = detail
  useEffect(() => {
    setShowVal(isFormApproval)
  }, [isFormApproval, setShowVal])

  const $tableRef = useRef(null)

  const onReceiveDataFromChild = (values) => {
    if (values.length > 0) {
      form.setFieldsValue({
        actualLeaseDate: values[0].actualStartDate ? moment(values[0].actualStartDate) : undefined,
      })
    }
  }

  const goProcess = () => {
    const search = JSON.stringify({
      projName: detail.projName,
      // clientId: contractInfo.clientId,
    })
    if (isFormApproval) {
      window.open(`/process/query?search=${search}`)
    } else {
      history.push(`/process/query?search=${search}`)
    }
  }

  return (
    <div>
      <Page
        // current={isFormApproval ? '流程详情' : '合同变更'}
        header={null}
        store={store}
        params={{ bizType, id, form, changeType, businessVersion }}
      >
        <div className={styles.page}>
          <Form form={form}>
            <div className={styles.header}>
              <div className={styles.title}>
                合同变更-{getKeyOptionsLabelMapPlus('contractChangeTypeEnum')[changeType]}
              </div>

              <Space>
                <TrackModal
                  params={{ contractCode, curAssigneeIds, bizSource: 'CONTRACT', bizId: id }}
                />
                <BusinessInfoCheck
                  taskStatus={taskStatus}
                  contractId={id}
                  flowId={processInstanceId}
                  modelKey={modelKey}
                  taskActivityId={taskActivityId}
                ></BusinessInfoCheck>
                <Button onClick={goProcess} type="link">
                  查询历史流程
                </Button>
                {!isFormApproval && canEditFlagsFormAuth && (
                  <Button onClick={store.cancelFlow}>取消操作</Button>
                )}
                {!isFormApproval && canEditFlagsFormAuth && (
                  <Button type="primary" loading={isLoading} onClick={() => store.onSubmit()}>
                    提交审批
                  </Button>
                )}
              </Space>
            </div>

            <div style={{ marginTop: 12 }}>
              {['CHANGE_REPAY_PLAN','EXTENSION'].includes(changeType) && (
                <EditDescription
                  title={'调整说明'}
                  detail={store.remarkInfo}
                  style={{ marginBottom: 12 }}
                  saveData={store.saveRemark}
                  columns={[
                    TextAreaColumn({
                      title: '调整说明',
                      dataIndex: 'adjustRemark',
                      editable: true,
                    }),
                  ]}
                  labelStyle={{ background: '#F5F6FA', width: 320 }}
                />
              )}
              <CantractDetailBaseInfo
                contractId={id}
                businessVersion={businessVersion}
                isDetail
                canEditFlag={false}
                onEmitData={onCantractInfoData}
              />
            </div>

            {['LPR_CHANGE', 'EXTENSION'].includes(changeType) ? (
              <div className={styles.subHeader}>
                <div className={styles.editWrap}>
                  {canEditFlagsFormAuth ? (
                    <>
                      {showVal ? (
                        <Button
                          type="primary"
                          onClick={() => {
                            setShowVal(false)
                            store.page.init()
                          }}
                        >
                          编辑
                        </Button>
                      ) : (
                        <Space>
                          <Button
                            onClick={() => {
                              // form.resetFields()
                              setShowVal(true)
                            }}
                          >
                            取消
                          </Button>
                          <Button type="primary" onClick={() => store.onSubmit(true)}>
                            保存
                          </Button>
                        </Space>
                      )}
                    </>
                  ) : null}
                </div>
              </div>
            ) : null}

            {changeType === 'LPR_CHANGE' && (
              <LPR
                store={store}
                detail={baseinfo}
                oldDetail={old_baseinfo}
                showValue={showVal}
              ></LPR>
            )}
            {changeType === 'EARLY_REPAYMENT' && (
              <Ahead
                contractId={id}
                canEdit={canEditFlagsFormAuth}
                businessVersion={businessVersion}
                taskActivityId={taskActivityId}
              ></Ahead>
            )}
            {changeType === 'EXTENSION' && (
              <Exhibi
                store={store}
                detail={baseinfo}
                oldDetail={old_baseinfo}
                showValue={showVal}
              ></Exhibi>
            )}
            <div className={styles.tableWrap}>
              <h3>{`实际${bizTypeMapText[bizType]?.rentTitle}`}</h3>
              <Space>
                <Item
                  label={`合同实际${bizTypeMapText[bizType]?.dateText}`}
                  name="actualLeaseDate"
                  rules={[{ required: changeType === 'CHANGE_REPAY_PLAN', message: '请选择！' }]}
                >
                  <DatePicker
                    placeholder={'请选择'}
                    style={{ width: 200 }}
                    disabled={changeType !== 'CHANGE_REPAY_PLAN' || !canEditFlagsFormAuth}
                  />
                </Item>
              </Space>
            </div>
          </Form>
          <div className={styles.wrap}>
            <ActualTable
              extra={(item, index) => {
                return (
                  <Form
                    initialValues={{
                      receiptStartDate: item.actualStartDate
                        ? moment(item.actualStartDate)
                        : undefined,
                    }}
                  >
                    <Form.Item label="借据起租日期" name={'receiptStartDate'}>
                      <DatePicker placeholder={'请选择'} disabled />
                    </Form.Item>
                  </Form>
                )
              }}
              businessVersion={businessVersion}
              bizType={bizType}
              ref={$tableRef}
              contractId={id}
              showImportBtn={canEditFlagsFormAuth}
              onReceiveDataFromChild={onReceiveDataFromChild}
            ></ActualTable>
          </div>
          <Protocol
            canEdit={canEditFlagsFormAuth}
            id={id}
            title="补充协议"
            changeType={changeType}
          ></Protocol>
        </div>
      </Page>
    </div>
  )
}

export default observer(Index)
