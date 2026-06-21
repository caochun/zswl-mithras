import { observer, toJS } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { Switch, Tooltip, Empty } from 'antd'
import { useState } from 'react'
import styles from './index.less'
import moment from 'moment'
import { renderSpan, changeTY } from './baseUtils.js'
import { preProcessData, isEmpty } from '@/utils'
import {
  CommerceColums,
  shareholderTypeList,
  relationshipTypeList,
  currencyTypeList,
  continuousStatusList,
  orgScaleTypeList,
} from '../general'
import { saveServer } from '@/utils'

const dateFormat = 'yyyy-MM-DD'
const diffType = {
  ADD: '新增',
  MODIFY: '修改',
  DELETE: '删除',
}

//新增地址
function CustomerSynchronizationModal({ store }) {
  const [industrialList, setIndustrialList] = useState([])
  const [addressList, setAddressList] = useState([])
  const [shareholdersList, setShareholdersList] = useState([])
  const [enterpriseList, setEnterpriseList] = useState([])
  const { syncAllData } = store
  let changedCommerceInfo = Object.keys(syncAllData).length > 0 && syncAllData.changedCommerceInfo
  delete changedCommerceInfo.id

  let changedCommerceInfoList = Object.keys(changedCommerceInfo).map((key) => {
    if (key == 'industryType') {
      return {
        name: key,
        value: changedCommerceInfo[key],
        keys: changedCommerceInfo.industryTypeWithParent || [],
        nameTitle: changedCommerceInfo.industryTypeName,
      }
    }

    return { name: key, value: changedCommerceInfo[key] }
  })
  let syarr = []
  changedCommerceInfoList.forEach((v) => {
    if (v.value != null) {
      syarr.push(v)
    }
  })
  const modalCloseChange = () => {
    setIndustrialList([])
    setAddressList([])
    setShareholdersList([])
    setEnterpriseList([])
  }
  //点击后不可编辑
  const disabledChange = (i) => {
    industrialList.push(i)
    setIndustrialList([...industrialList])
  }

  const addressChange = (i, value) => {
    addressList.push(i)
    setAddressList([...addressList])
    const { registerAddressChangedItemList } = syncAllData
    if (value.id) {
      let syncData = {},
        initData = {}
      registerAddressChangedItemList?.list?.length > 0 &&
        registerAddressChangedItemList?.list?.forEach((v) => {
          if (v.id == value.id) {
            initData = v
            syncData = value
          }
        })
      syncData = preProcessData(syncData)
      store.synchAddress(Object.assign(initData, syncData))
    } else {
      store.synchAddress(value)
    }
  }
  const shareholdersChange = (i, value) => {
    shareholdersList.push(i)
    setShareholdersList([...shareholdersList])
    const { shareholderInfoChangedItemList } = syncAllData
    if (value.id) {
      let syncData = {},
        initData = {}
      shareholderInfoChangedItemList?.list?.length > 0 &&
        shareholderInfoChangedItemList?.list?.forEach((v) => {
          if (v.id == value.id) {
            initData = v
            syncData = value
          }
        })

      syncData = preProcessData(syncData)
      store.synchShareholder(Object.assign(initData, syncData))
    } else {
      store.synchShareholder(value)
    }
  }
  const enterpriseListChange = (i, value) => {
    enterpriseList.push(i)
    setEnterpriseList([...enterpriseList])
    const { relatedEnterpriseChangedItemList } = syncAllData
    if (value.id) {
      let syncData = {},
        initData = {}
      relatedEnterpriseChangedItemList?.list?.length > 0 &&
        relatedEnterpriseChangedItemList?.list?.forEach((v) => {
          if (v.id == value.id) {
            initData = v
            syncData = value
          }
        })
      syncData = preProcessData(syncData)
      store.synchEnterprise(Object.assign(initData, syncData))
    } else {
      store.synchEnterprise(value)
    }
  }
  const switchChange = (e, result) => {
    if (result.name == 'approvalDate') {
      store.industrialForm.setFieldsValue({
        [result.name]: result.value && moment(result.value, dateFormat),
      })
    } else if (result.name == 'establishDate') {
      store.industrialForm.setFieldsValue({
        [result.name]: result.value && moment(result.value, dateFormat),
      })
    } else if (result.name == 'bizLicenseEndDate') {
      store.industrialForm.setFieldsValue({
        [result.name]: result.value && moment(result.value, dateFormat),
      })
    } else if (result.name == 'industryType') {
      store.industrialForm.setFieldsValue({
        // industryType: result.nameTitle,
        industryType: result.keys,
      })
    } else {
      store.industrialForm.setFieldsValue({
        [result.name]: result.value,
      })
    }
  }
  return (
    <Modal
      title={'相关信息同步'}
      store={store.synchronizationModal}
      okText={'确定'}
      width={960}
      destroyOnClose
      footer={null}
      afterClose={modalCloseChange}
      bodyStyle={{ height: '640px', overflowY: 'auto' }}
      wrapClassName={styles.tbModal}
    >
      <h4 style={{ paddingBottom: '8px' }}>工商信息</h4>
      <div>
        <div className={styles.commerceTitle} style={{ background: '#f2f3f5' }}>
          <span className={styles.commerceTitleChild}>字段名称</span>
          <span className={styles.commerceTitleChild}>天眼查数据</span>
          <span className={styles.commerceTitleChild}>是否以天眼查为准</span>
        </div>
        {syarr.length > 0 ? (
          changedCommerceInfoList.map((s, i) => {
            return (
              s.value != null &&
              s.name != 'id' &&
              s.name != 'industryTypeWithParent' &&
              s.name != 'industryTypeName' && (
                <div className={styles.commerceInfo} key={s.name}>
                  <span className={styles.commerceInfoChild}>{CommerceColums[s.name]}</span>
                  {renderSpan(s)}
                  <span className={styles.commerceInfoChild}>
                    <Switch
                      checkedChildren="是"
                      unCheckedChildren="否"
                      defaultChecked={false}
                      onClick={() => {
                        disabledChange(i)
                      }}
                      // disabled={industrialList.includes(i)}
                      onChange={(e) => {
                        switchChange(e, s)
                      }}
                    />
                  </span>
                </div>
              )
            )
          })
        ) : (
          <div>
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
          </div>
        )}
      </div>
      <h4 style={{ paddingTop: '24px', paddingBottom: '8px' }}>地址信息</h4>
      <Table
              columnsFilter={'detail_Basic_SynchronizationModal_1'}
              onFilter={(key,val) => saveServer('detail_Basic_SynchronizationModal_1',val)}
        store={store.addressInfoSync}
        autoRequest={false}
        scroll={{
          x: 1000,
        }}
        columns={[
          {
            title: '差异类别',
            dataIndex: 'changedType',
            width: 120,
            fixed: 'left',
            render: (item, index) => {
              return diffType[item]
            },
          },
          {
            title: '地址类型',
            dataIndex: 'addressType',
            render: (item, n, index) => {
              if (item == 'WORK_ADDRESS') {
                return '办公地址'
              }
              return '注册地址'
            },
          },
          {
            title: '国家/地区',
            dataIndex: 'countryName',
            tooltip: true,
          },
          { title: '省份', dataIndex: 'provinceName' },
          { title: '市', dataIndex: 'cityName' },
          { title: '区/县', dataIndex: 'districtName', tooltip: true },
          { title: '详细地址', dataIndex: 'detail', tooltip: true },
          { title: '行政区划代码', dataIndex: 'regionCode', tooltip: true },
          {
            title: '是否以天眼查为准',
            width: 160,
            fixed: 'right',
            actions(value) {
              return [
                {
                  name: (
                    <Switch
                      checkedChildren="是"
                      unCheckedChildren="否"
                      // disabled={addressList.includes(value.cityName)}
                      defaultChecked={false}
                      onClick={() => {
                        addressChange(value.cityName, value)
                      }}
                    />
                  ),
                },
              ]
            },
          },
        ]}
      />
      <h4 style={{ paddingTop: '24px', paddingBottom: '8px' }}>股东信息</h4>
      <Table
                    columnsFilter={'detail_Basic_SynchronizationModal_2'}
                    onFilter={(key,val) => saveServer('detail_Basic_SynchronizationModal_2',val)}
        rowKey={'paidTotal'}
        store={store.shareholderSync}
        autoRequest={false}
        scroll={{
          x: 1000,
        }}
        columns={[
          {
            title: '差异类别',
            dataIndex: 'changedType',
            width: 120,
            fixed: 'left',
            render: (item, t) => {
              return changeTY(t, diffType[item], 'changedType')
            },
          },
          {
            title: '股东类型',
            width: 150,
            dataIndex: 'shareholderType',
            render: (v, t) => {
              return changeTY(t, shareholderTypeList[v], 'shareholderType')
            },
          },
          {
            title: '认缴金额（万元）',
            width: 150,
            align: 'right',
            dataIndex: 'paidTotal',
            render: (v, t) => {
              return changeTY(t, v / 10000, 'paidTotal')
            },
          },
          {
            title: '股东名称',
            dataIndex: 'shareholderName',
            width: 150,
            render: (v, t) => {
              return changeTY(t, v, 'shareholderName')
            },
          },
          {
            title: '实缴金额(万元)',
            dataIndex: 'actualPaidTotal',
            width: 150,
            align: 'right',
            render: (v, t) => {
              return changeTY(t, v / 10000, 'actualPaidTotal')
            },
          },
          {
            title: '认缴出资方式',
            dataIndex: 'capitalWay',
            width: 150,
            render: (v, t) => {
              return changeTY(t, v, 'capitalWay')
            },
          },
          {
            title: '认缴出资占比',
            dataIndex: 'capitalPercent',
            width: 150,
            align: 'right',
            render: (v, t) => {
              return changeTY(t, v / 10000, 'capitalPercent')
            },
          },
          // {
          //   title: '是否实际控制人',
          //   dataIndex: 'realController',
          //   render: (item, index) => {
          //     if (item) {
          //       return '是'
          //     }
          //     return '否'
          //   },
          // },
          {
            title: '是否以天眼查为准',
            width: 160,
            fixed: 'right',
            actions(value) {
              return [
                {
                  name: (
                    <Switch
                      // disabled={shareholdersList.includes(value.shareholderName)}
                      checkedChildren="是"
                      unCheckedChildren="否"
                      defaultChecked={false}
                      onClick={() => {
                        shareholdersChange(value.shareholderName, value)
                      }}
                    />
                  ),
                },
              ]
            },
          },
        ]}
      />
      <h4 style={{ paddingTop: '24px', paddingBottom: '8px' }}>关联企业</h4>
      <Table
                    columnsFilter={'detail_Basic_SynchronizationModal_3'}
                    onFilter={(key,val) => saveServer('detail_Basic_SynchronizationModal_3',val)}
        rowKey={'investAmount'}
        store={store.affiliatedSync}
        scroll={{
          x: 1000,
        }}
        columns={[
          {
            title: '差异类别',
            dataIndex: 'changedType',
            width: 120,
            fixed: 'left',
            render: (item, t) => {
              return changeTY(t, diffType[item], 'changedType')
            },
          },
          {
            title: '关联企业名称',
            dataIndex: 'enterpriseName',
            width: 210,
            render: (v, t) => {
              return changeTY(t, v, 'enterpriseName')
            },
          },
          {
            title: '关联关系',
            width: 120,
            dataIndex: 'relationship',
            render: (v, t) => {
              return changeTY(t, relationshipTypeList[v], 'relationship')
            },
          },
          {
            title: '成立年份',
            dataIndex: 'establishDate',
            dateFormat: 'yyyy-MM-DD',
            width: 120,
            render: (v, t) => {
              return changeTY(t, v, 'establishDate')
            },
          },
          {
            title: '行业',
            dataIndex: 'industryTypeName',
            width: 120,
            render: (v, t) => {
              return changeTY(t, v, 'industryTypeName')
            },
          },
          {
            title: '存续状态',
            dataIndex: 'continuousStatus',
            width: 130,
            render: (v, t) => {
              return changeTY(t, continuousStatusList[v], 'continuousStatus')
            },
          },
          {
            title: '注册资本(万元)',
            dataIndex: 'registerCapital',
            width: 130,
            align: 'right',
            render: (v, t) => {
              return changeTY(t, v / (10000*10000), 'registerCapital')
            },
          },
          {
            title: '持股比例',
            dataIndex: 'shareholdingRatio',
            width: 120,
            align: 'right',
            render: (v, t) => {
              return changeTY(t, v / 10000, 'shareholdingRatio')
            },
          },
          {
            title: '投资金额(万元)',
            dataIndex: 'investAmount',
            width: 130,
            align: 'right',
            render: (v, t) => {
              return changeTY(t, v / (10000*10000), 'investAmount')
            },
          },
          {
            title: '是否以天眼查为准',
            width: 160,
            fixed: 'right',
            actions(value, i) {
              return [
                {
                  name: (
                    <Switch
                      // disabled={enterpriseList.includes(value.enterpriseName)}
                      checkedChildren="是"
                      unCheckedChildren="否"
                      defaultChecked={false}
                      onChange={() => {
                        enterpriseListChange(value.enterpriseName, value)
                      }}
                    />
                  ),
                },
              ]
            },
          },
        ]}
      />
    </Modal>
  )
}

export default observer(CustomerSynchronizationModal)
