import { Table, Page } from '@zswl/components'
import { Collapse, Divider, Spin, Badge } from 'antd'
import { observer } from '@zswl/admin'
import Store from './store'
import { useEffect, useMemo } from 'react'
import ColumsObj from './ColumsType'
import IndustrialLog from './IndustrialLog'
import {
  genderTypeList,
  certTypeList,
  shareholderTypeList,
  continuousStatusList,
  relationshipTypeList,
} from '../../general'
import { formateCard, hasValue } from '@/utils'
import { ChangeLogDiff } from '@/components/ChangeLogDiff/ChangeLogDiffEntries'
import { saveServer } from '@/utils'

const { Panel } = Collapse

function Index({ params: { id }, query: { clientId } }) {
  const store = useMemo(() => new Store(), [])
  const { initListData, loading } = store
  useEffect(() => {
    store.initList(id, clientId)
  }, [id, clientId])

  return (
    <Page store={store}>
      <div>
        <Spin spinning={loading}>
          <h3>变更日志版本对比</h3>
          <Collapse defaultActiveKey={[]} accordion>
            <Panel
              header={
                <div>
                  <span>工商信息变更日志</span>
                  {Object.keys(initListData).length > 0 &&
                    initListData.moduleChanged?.CORP_COMMERCE && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                </div>
              }
              key="1"
            >
              <Divider orientation="left" plain>
                变更之前
              </Divider>
              {Object.keys(initListData).length > 0 && (
                <IndustrialLog
                  detailData={initListData.oldData && initListData.oldData.CORP_COMMERCE[0]}
                  flag={'OLD'}
                />
              )}
              <Divider orientation="left" plain>
                变更之后
              </Divider>
              {Object.keys(initListData).length > 0 && (
                <IndustrialLog
                  detailData={initListData.newData && initListData.newData.CORP_COMMERCE[0]}
                  flag={'NEW'}
                />
              )}
            </Panel>
            <Panel
              header={
                <div>
                  <span>地址信息变更日志</span>
                  {Object.keys(initListData).length > 0 &&
                    initListData.moduleChanged?.CORP_ADDRESS && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                </div>
              }
              key="2"
            >
              <Divider orientation="left" plain>
                变更之前
              </Divider>D:\dep\mithras-react\src\pages\customer\maintain\detail\log\diffInfo\[id$].js
              <Table
                      columnsFilter={'detail_log_diffInfo_idjs_1'}
                      onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_1',val)}
                store={store.addressInfo}
                autoRequest={false}
                columns={[
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
                  { title: '国家/地区', dataIndex: 'countryName' },
                  { title: '省份', dataIndex: 'provinceName' },
                  { title: '市', dataIndex: 'cityName' },
                  { title: '区/县', dataIndex: 'districtName' },
                  { title: '详细地址', dataIndex: 'detail' },
                  { title: '行政区划代码', dataIndex: 'regionCode' },
                ]}
              />
              <Divider orientation="left" plain>
                变更之后
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_2'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_2',val)}
                store={store.addressInfoAfter}
                autoRequest={false}
                columns={ColumsObj.addressColums}
              />
            </Panel>
            <Panel
              header={
                <div>
                  <span>联系人信息变更日志</span>
                  {Object.keys(initListData).length > 0 &&
                    initListData.moduleChanged?.CORP_CONTACT && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                </div>
              }
              key="3"
            >
              <Divider orientation="left" plain>
                变更之前
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_3'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_3',val)}
                store={store.linkManInfo}
                autoRequest={false}
                columns={[
                  {
                    title: '是否主联系人',
                    dataIndex: 'main',
                    render: (item, n, index) => {
                      if (item) {
                        return '是'
                      }
                      return '否'
                    },
                  },
                  { title: '职务', dataIndex: 'position' },
                  { title: '姓名', dataIndex: 'name' },
                  {
                    title: '性别',
                    dataIndex: 'gender',
                    render: (v, t) => {
                      return genderTypeList[v]
                    },
                  },
                  { title: '电话', dataIndex: 'telephone' },
                  { title: '邮箱', dataIndex: 'mail' },
                  {
                    title: '证件类型',
                    dataIndex: 'certType',
                    render: (v, t) => {
                      return certTypeList[v]
                    },
                  },
                  { title: '证件号码', dataIndex: 'certNumber' },
                ]}
              />
              <Divider orientation="left" plain>
                变更之后
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_4'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_4',val)}
                store={store.linkManInfoAfter}
                autoRequest={false}
                columns={ColumsObj.linkManInfoColums}
              />
            </Panel>
            <Panel
              header={
                <div>
                  <span>发债及评级变更日志</span>
                  {Object.keys(initListData).length > 0 &&
                    initListData.moduleChanged?.CORP_BOND && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                </div>
              }
              key="4"
            >
              <Divider orientation="left" plain>
                变更之前
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_5'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_5',val)}
                store={store.bondRating}
                autoRequest={false}
                columns={[
                  { title: '评级时间', dataIndex: 'rateDate' },
                  { title: '评级公司', dataIndex: 'rateCompany' },
                  { title: '评级', dataIndex: 'rate' },
                  { title: '评级展望', dataIndex: 'rateFuture' },
                  {
                    title: '发行总额(亿元)',
                    dataIndex: 'issueTotal',
                  },
                  { title: '发行数量（只）', dataIndex: 'issueAmount' },
                  {
                    title: '存量规模(亿元)',
                    dataIndex: 'stockScale',
                  },
                  { title: '存量只数', dataIndex: 'stockAmount' },
                  {
                    title: '到期规模(亿元)',
                    dataIndex: 'maturityScale',
                  },
                  { title: '到期只数', dataIndex: 'maturityAmount' },
                ]}
              />
              <Divider orientation="left" plain>
                变更之后
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_6'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_6',val)}
                store={store.bondRatingAfter}
                autoRequest={false}
                columns={ColumsObj.bondRatingColums}
              />
            </Panel>
            <Panel
              header={
                <div>
                  <span>股东信息变更日志</span>
                  {Object.keys(initListData).length > 0 &&
                    initListData.moduleChanged?.CORP_SHAREHOLDER && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                </div>
              }
              key="5"
            >
              <Divider orientation="left" plain>
                变更之前
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_7'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_7',val)}
                store={store.shareholder}
                autoRequest={false}
                columns={[
                  {
                    title: '股东类型',
                    dataIndex: 'shareholderType',
                    render: (v, t) => {
                      return shareholderTypeList[v]
                    },
                  },
                  {
                    title: '股东名称',
                    dataIndex: 'shareholderName',
                    render: (v, t) => {
                      return <span>{v}</span>
                    },
                  },
                  {
                    title: '实缴金额(万)',
                    dataIndex: 'paidTotal',
                  },
                  {
                    title: '出资方式',
                    dataIndex: 'capitalWay',
                    //奇怪问题
                  },
                  { title: '出资占比', dataIndex: 'capitalPercent' },
                  {
                    title: '是否实际控制人',
                    dataIndex: 'realController',
                    render: (item, index) => {
                      if (item) {
                        return '是'
                      }
                      return '否'
                    },
                  },
                ]}
              />
              <Divider orientation="left" plain>
                变更之后
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_8'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_8',val)}
                store={store.shareholderAfter}
                autoRequest={false}
                columns={ColumsObj.shareholderColums}
              />
            </Panel>
            <Panel
              header={
                <div>
                  <span>关联企业变更日志</span>
                  {Object.keys(initListData).length > 0 &&
                    initListData.moduleChanged?.CORP_RELATED_ENTERPRISE && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                </div>
              }
              key="6"
            >
              <Divider orientation="left" plain>
                变更之前
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_9'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_9',val)}
                store={store.affiliated}
                autoRequest={false}
                columns={[
                  { title: '关联企业名称', dataIndex: 'enterpriseName' },
                  {
                    title: '关联关系',
                    dataIndex: 'relationship',
                    render: (val) => {
                      return relationshipTypeList[val] || '-'
                    },
                  },
                  {
                    title: '注册资本(万元)',
                    dataIndex: 'registerCapital',
                    render: (val) => {
                      return hasValue(val) ? val / 10000 : '-'
                    },
                  },
                  {
                    title: '存续状态',
                    dataIndex: 'continuousStatus',
                    render: (v, t) => {
                      return continuousStatusList[v]
                    },
                  },
                  {
                    title: '持股比例(%)',
                    dataIndex: 'shareholdingRatio',
                    render: (val) => {
                      return hasValue(val) ? val / 10000 : '-'
                    },
                  },
                  {
                    title: '投资金额(万元)',
                    dataIndex: 'investAmount',
                  },
                ]}
              />
              <Divider orientation="left" plain>
                变更之后
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_10'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_10',val)}
                store={store.affiliatedAfter}
                autoRequest={false}
                columns={ColumsObj.affiliatedColums}
              />
            </Panel>
            <Panel
              header={
                <div>
                  <span>银行账户变更日志</span>
                  {Object.keys(initListData).length > 0 &&
                    initListData.moduleChanged?.CORP_BANK_ACCOUNT && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                </div>
              }
              key="7"
            >
              <Divider orientation="left" plain>
                变更之前
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_11'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_11',val)}
                store={store.bankAccount}
                autoRequest={false}
                columns={[
                  {
                    title: '是否主账号',
                    dataIndex: 'mainAccount',
                    render: (item, index) => {
                      if (item) {
                        return '是'
                      }
                      return '否'
                    },
                  },
                  { title: '银行账户', dataIndex: 'accountNumber', render: (t) => formateCard(t) },
                  { title: '账户名称', dataIndex: 'accountName' },
                  { title: '开户行', dataIndex: 'accountBank' },
                ]}
              />
              <Divider orientation="left" plain>
                变更之后
              </Divider>
              <Table
                                    columnsFilter={'detail_log_diffInfo_idjs_12'}
                                    onFilter={(key,val) => saveServer('detail_log_diffInfo_idjs_12',val)}
                store={store.bankAccountAfter}
                autoRequest={false}
                columns={ColumsObj.bankAccountColums}
              />
            </Panel>
            <Panel header="文件变更日志" key="file" forceRender>
              <ChangeLogDiff
                version={id}
                moduleType="CLIENT"
                functionCode="filelistversioncompare"
                options={[
                  { label: '基础资料', value: 'BASIC_INFORMATION' },
                  { label: '租赁业务申请书', value: 'LEASE_APPLICATION' },
                  { label: '征信授权书', value: 'CREDIT_LETTER' },
                  { label: '财务资料', value: 'FINANCIAL_INFORMATION' },
                  { label: '经营资料', value: 'BUSINESS_INFORMATION' },
                  { label: '其他', value: 'OTHERS' },
                  { label: '身份证', value: 'ID_CARD' },
                  { label: '户口本', value: 'HOUSEHOLD' },
                  { label: '结婚证', value: 'MARRIAGE_CERT' },
                  { label: '个人信用报告', value: 'PERSONAL_CREDIT_REPORT' },
                  { label: '名下资产', value: 'NAMED_ASSETS' },
                ]}
              />
            </Panel>
          </Collapse>
        </Spin>
      </div>
    </Page>
  )
}
export default observer(Index)
