import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import { bizTypeMapText } from '../bizTypeConfig'
import FormIrr from '@/components/FormIrr'
import { formatPercent, rules, saveServer } from '@/utils'
import { getQuery, observer } from '@zswl/admin'
import { Button, Form, Table } from '@zswl/components'
import { Space } from 'antd'
import { isFunction } from 'lodash'
import { forwardRef, useEffect, useImperativeHandle, useMemo } from 'react'
import ActualTax from './ActualTax'
import BeforeData from './BeforeData'
import ImportRent from './ImportRent'
import styles from './index.less'
import Store from './store'
import { useColumn } from './useColumn'

function Index (
  {
    headerTitle,
    contractId,
    showImportBtn = true,
    showSubTitle = true,
    showTax = false,
    bizType,
    contractStatus,
    businessVersion,
    onReceiveDataFromChild = () => { },
    onImportSuccess,
    scene,
    showIRRTips = false,
    extra,
  },
  ref
) {
  const isFormApproval = getQuery('typeId') == 'approval'
  // 合同详情不展示
  const hideRemoveBtn = scene === 'create'

  const column = useColumn(bizType)

  const store = useMemo(() => {
    return new Store({
      onReceiveDataFromChild,
      onImportSuccess,
      isFormApproval,
      bizType,
      contractId,
      businessVersion,
      showTax,
      showIRRTipsOperation: showIRRTips,
    })
  }, [])

  const { actualDetail, getActualList, handleMenuClick, removeReceipt, changeList, setShowDrawer } =
    store

  useImperativeHandle(ref, () => ({
    refresh: () => {
      getActualList(contractId)
    },
  }))

  useEffect(() => {
    if (contractId) {
      getActualList(contractId)
    }
  }, [contractId])

  useEffect(() => {
    const run = async () => {
      store.setIrrTipsOperation(showIRRTips)
      await store.refreshIrrTips()
    }
    run()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [showIRRTips, contractId])

  const templateName = useMemo(() => {
    return ['BL', 'ZR'].includes(bizType)
      ? 'TEMPLATE_OSS_NAME_ACTUAL_PAYMENT_ITEM'
      : 'TEMPLATE_OSS_NAME_ACTUAL_RENT_ITEM'
  }, [bizType])

  const bizTypeRentTitle = useMemo(() => {
    return bizTypeMapText[bizType]?.rentTitle ?? ''
  }, [bizType])

  return (
    <div className={styles.pages}>
      {actualDetail.length > 0 ? (
        <>
          {actualDetail.map((item, index) => {
            return (
              <div className={styles.page} key={index}>
                {showSubTitle && (
                  <div className={styles.titleWrap}>
                    <div className={styles.subTitle}>
                      <div>
                        借据编号：
                        <span style={{ color: changeList[index]?.receiptCode ? 'red' : undefined }}>
                          {item.receiptCode || '-'}
                        </span>
                      </div>
                    </div>
                  </div>
                )}
                <div className={styles.headerAction}>
                  <Space wrap align="center" size="small" className={styles.headerSpace}>
                    {isFunction(extra) ? extra(item, index) : extra}
                    {isFormApproval && (
                      <Button
                        type="primary"
                        onClick={() => {
                          setShowDrawer(true)
                        }}
                      >
                        变更前数据
                      </Button>
                    )}
                    {item.remove && !hideRemoveBtn && (
                      <Button
                        onClick={() => {
                          removeReceipt(item)
                        }}
                      >
                        删除借据
                      </Button>
                    )}
                    <DownloadTemplate
                      params={{
                        templateName,
                        moduleType: 'CONTRACT',
                      }}
                    />
                    {showImportBtn && (
                      <Button
                        onClick={() => store.$createModal.open(item)}
                      >{`导入${bizTypeRentTitle}`}</Button>
                    )}
                    <Button
                      onClick={(e) => handleMenuClick(e, item, 'CURRENT')}
                      items={[
                        {
                          name: `导出${bizTypeRentTitle}`,
                          key: '1',
                        },
                        {
                          name: '导出现金流表',
                          key: '2',
                        },
                      ]}
                      loading={store.exportLoading}
                    >
                      导出
                    </Button>
                  </Space>

                  <Space wrap className={styles.headerSpace} style={{ marginTop: 10 }}>
                    {showTax && (
                      <ActualTax store={store} currentTableData={item} showTips={store.irrTipsFlag}></ActualTax>
                    )}
                    {!['NEW', 'INVALID'].includes(item.contractStatus) && (
                      <Form
                        layout="inline"
                        store={store.baseForm}
                        initialValues={{ irr: formatPercent(item.actualIrr) }}
                      >
                        <FormIrr.Item
                          rules={[rules.required('请输入实际IRR')]}
                          onChange={(irrValue) => store.irrOnChange(irrValue, index)}
                          name="irr"
                          label={'实际IRR'}
                          onBlur={() => store.irrOnBlur(item, index)}
                          handleOpen={() => store.handleOpen(item)}
                          canEdit={showImportBtn}
                        />
                      </Form>
                    )}
                  </Space>
                </div>
                <Table
                  columnsFilter="Component_ActualTable_1"
                  onFilter={(key, val) => saveServer('Component_ActualTable_1', val)}
                  scroll={{ x: 1500 }}
                  rowKey={(record) => {
                    return record.id?.value ?? record.id
                  }}
                  dataSource={item.rentActualList}
                  columns={column}
                />
              </div>
            )
          })}
        </>
      ) : (
        <>
          <div className={styles.btnWrap}>
            <div className={styles.title}>{headerTitle}</div>
            <Space>
              <DownloadTemplate
                params={{
                  templateName,
                  moduleType: 'CONTRACT',
                }}
              />
              <Button
                onClick={store.$createModal.open}
                disabled={contractStatus === 'INVALID' || isFormApproval}
              >
                {`导入${bizTypeRentTitle}`}
              </Button>
            </Space>
          </div>
          <Table
            columnsFilter="Component_ActualTable_2"
            onFilter={(key, val) => saveServer('Component_ActualTable_2', val)}
            scroll={{ x: 1500 }}
            columns={column}
          />
        </>
      )}
      <ImportRent bizTypeRentTitle={bizTypeRentTitle} store={store}></ImportRent>
      <BeforeData bizType={bizType} baseStore={store}></BeforeData>
    </div>
  )
}

export default observer(forwardRef(Index))
