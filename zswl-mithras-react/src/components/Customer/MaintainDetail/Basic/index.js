import { useState, useMemo, useEffect, forwardRef, useImperativeHandle } from 'react'
import IconFont from '@/components/Icon'
import { observer, getQuery, toJS, history } from '@zswl/admin'
import { Table, Page, Button } from '@zswl/components'
import styles from './index.less'
import Store from './store'
import IndustrialDetail from './IndustrialDetail'
import AddressModal from './AddressModal'
import SynchronizationModal from './SynchronizationModal'
import BasicInformation from './BasicInformation'
import IndustrialDetailStatic from './IndustrialDetailStatic'
import BasicInformationStatic from './BasicInformationStatic'
import Contact from './Contact'
import Shareholders from './Shareholders'
import BankAccount from './BankAccount'
import IssueBonds from './IssueBonds'
import Spouse from './Spouse'
import Enterprises from './Enterprises'
import Report from './Report'
import { DetailLayout } from '@/components/Layout'
import { FiledFormat, MatchOptionColumn } from '@/components/Format'
import { Space } from 'antd'
import { saveServer } from '@/utils'

const Basic = forwardRef(({
  id,
  clientType,
  canEditFlag: canEdit,
  businessVersion,
  domesticOrAbroad,
  commonExtra,
  startUserId,
  processInstanceId,
  isCreate,
  pageData = {},
  rootStore,
}, ref) => {
  const isFormApproval = getQuery('typeId') == 'approval'
  const [save, setSave] = useState(true)
  const [num, setNum] = useState('')

  const store = useMemo(
    () => new Store(id, clientType, isFormApproval, startUserId, businessVersion),
    [id, clientType, isFormApproval, startUserId, businessVersion]
  )
  rootStore.baseStore = store
  const { addressInfoList, baseInfo } = store

  useImperativeHandle(ref, () => ({
    sync,
  }));

  //编辑与保存
  const edit = async () => {
    if (clientType == 'CORPORATION') {
      await store.legalSave(id, () => setSave(true))
      setNum(2)
    } else {
      await store.naturalSave(id, () => setSave(true))
    }
    rootStore?.page?.init()
  }

  //同步
  const sync = async () => {
    await store.synchronization(id, 'sync')
    setSave(false)
  }

  useEffect(() => {
    setSave(isCreate !== '1')
  }, [isCreate])

  let anchorList = [
    {
      label: clientType == 'NORMAL' ? '基本信息' : '工商信息',
    },
    { label: '配偶信息', isHide: clientType !== 'NORMAL' },
    { label: '地址信息', isHide: clientType !== 'CORPORATION' },
    { label: '联系人信息', isHide: clientType !== 'CORPORATION' },
    { label: '发债及评级', isHide: clientType !== 'CORPORATION' },
    { label: '股东信息', isHide: clientType !== 'CORPORATION' },
    { label: '关联企业', isHide: clientType !== 'CORPORATION' },
    { label: '银行账户' },
    { label: '资料清单' },
  ]

  // 只展示工商信息模块
  if (pageData.showCommerceInfo) {
    anchorList = anchorList.filter((item) => ['基本信息', '工商信息'].includes(item.label))
  }

  useEffect(() => {
    if (!canEdit) {
      setSave(true)
    }
  }, [canEdit])

  return (
    <Page noStyle params={{ clientType, id, isFormApproval, businessVersion }}>
      <div className={styles.basic}>
        <DetailLayout
          anchorList={anchorList}
          title={''}
          extra={[
            clientType !== 'NORMAL' && (
              <Button
                key="customer-unified-view"
                style={{ marginRight: '10px' }}
                onClick={() => {
                  const qs = new URLSearchParams({
                    enterpriseName: baseInfo?.clientName ?? '',
                    uscc: baseInfo?.uscCode ?? '',
                  })
                  history.push(`/customerView/detail/${id}?${qs.toString()}`)
                }}
              >
                客户统一视图
              </Button>
            ),
            // clientType === 'CORPORATION' && domesticOrAbroad === 'DOMESTIC' && !isFormApproval && (
            //   <Button disabled={!canEdit} onClick={sync}>
            //     同步当前页
            //   </Button>
            // ),
            commonExtra,
          ]}
          style={{ border: 0, marginTop: 10 }}
        >
          <div className={styles.tableWrap}>
            {canEdit && (
              <div className={styles.operationBtn}>
                <Space>
                  {save && (
                    <Button type="primary" onClick={() => setSave(false)}>
                      编辑
                    </Button>
                  )}
                  {!save && <Button onClick={() => setSave(true)}>取消</Button>}
                  {!save && (
                    <Button type="primary" onClick={() => edit()}>
                      保存
                    </Button>
                  )}
                </Space>
              </div>
            )}

            {clientType == 'NORMAL' ? (
              <>
                {save ? (
                  <BasicInformationStatic
                    clientType={clientType}
                    id={id}
                    save={save}
                    businessVersion={businessVersion}
                    startUserId={startUserId}
                    store={store}
                  />
                ) : (
                  <BasicInformation
                    clientType={clientType}
                    id={id}
                    save={save}
                    businessVersion={businessVersion}
                    startUserId={startUserId}
                    store={store}
                  />
                )}
              </>
            ) : (
              <>
                {save ? (
                  <IndustrialDetailStatic
                    id={id}
                    clientType={clientType}
                    save={save}
                    num={num}
                    businessVersion={businessVersion}
                    startUserId={startUserId}
                    store={store}
                  />
                ) : (
                  <IndustrialDetail
                    id={id}
                    clientType={clientType}
                    save={save}
                    store={store}
                    businessVersion={businessVersion}
                    startUserId={startUserId}
                  />
                )}
              </>
            )}
          </div>
          <div>
            <div className={styles.tableWrap}>
              <h3 className={styles.title}>配偶信息</h3>
              <Spouse
                id={id}
                canEditFlag={canEdit}
                businessVersion={businessVersion}
                startUserId={startUserId}
              />
            </div>
          </div>
          <div className={styles.tableWrap}>
            <h3 className={styles.title}>地址信息</h3>
            <Table
              columnsFilter={'detail_Basic_1'}
              onFilter={(key, val) => saveServer('detail_Basic_1', val)}
              scroll={{ x: 1100 }}
              resizable
              store={store.addressInfo}
              extra={[
                {
                  name: (
                    <span>
                      <IconFont type="icon-icon_add" />
                      新增
                    </span>
                  ),
                  type: 'primary',
                  onClick: () => {
                    store.addressModal.open(addressInfoList[0])
                  },
                  disabled: !canEdit,
                },
              ]}
              columns={[
                MatchOptionColumn({
                  title: '地址类型',
                  dataIndex: 'addressType',
                  matchOption: 'addressType',
                }),
                {
                  title: '国家',
                  dataIndex: 'countryName',
                  render: (val) => <FiledFormat title={val} />,
                },
                {
                  title: '省份',
                  dataIndex: 'provinceName',
                  render: (val) => <FiledFormat title={val} />,
                },
                {
                  title: '市',
                  dataIndex: 'cityName',
                  render: (val) => <FiledFormat title={val} />,
                },
                {
                  title: '区/县',
                  dataIndex: 'districtName',
                  render: (val) => <FiledFormat title={val} />,
                },
                {
                  title: '详细地址',
                  dataIndex: 'detail',
                  width: 220,
                  render: (val) => <FiledFormat title={val} />,
                },
                {
                  title: '行政区划代码',
                  dataIndex: 'regionCode',
                  width: 150,
                  render: (val) => <FiledFormat title={val} />,
                },
                {
                  title: '操作',
                  fixed: 'right',
                  actions() {
                    return [
                      {
                        name: '编辑',
                        onClick: store.addressModal.open,
                        disabled: !canEdit,
                      },
                      { name: '删除', onClick: store.removeAddress, disabled: !canEdit },
                    ]
                  },
                },
              ]}
            />
          </div>
          <div className={styles.tableWrap}>
            <h3 className={styles.title}>联系人信息</h3>
            <Contact
              canEditFlag={canEdit}
              businessVersion={businessVersion}
              startUserId={startUserId}
              id={id}
            />
          </div>
          <div className={styles.tableWrap}>
            <h3 className={styles.title}>发债及评级</h3>
            <IssueBonds
              canEditFlag={canEdit}
              id={id}
              businessVersion={businessVersion}
              startUserId={startUserId}
            />
          </div>
          <div className={styles.tableWrap}>
            <h3 className={styles.title}>股东信息</h3>
            <Shareholders
              canEditFlag={canEdit}
              businessVersion={businessVersion}
              startUserId={startUserId}
              id={id}
            />
          </div>
          <div className={styles.tableWrap}>
            <h3 className={styles.title}>关联企业</h3>
            <Enterprises
              canEditFlag={canEdit}
              businessVersion={businessVersion}
              startUserId={startUserId}
              id={id}
            />
          </div>
          <div className={styles.tableWrap}>
            <h3 className={styles.title}>银行账户</h3>
            <BankAccount
              clientType={clientType}
              canEditFlag={canEdit}
              businessVersion={businessVersion}
              startUserId={startUserId}
              id={id}
            />
          </div>
          <div className={styles.tableWrap}>
            <Report
              id={id}
              processInstanceId={processInstanceId}
              businessVersion={businessVersion}
              startUserId={startUserId}
              canEdit={canEdit}
              type={clientType}
            />
          </div>
        </DetailLayout>
        <AddressModal store={store} />
        <SynchronizationModal store={store} />
      </div>
    </Page>
  )
})

export default observer(Basic)
