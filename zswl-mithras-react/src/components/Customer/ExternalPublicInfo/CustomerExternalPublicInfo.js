import { Amount } from '@/components/Format'
import React, { useEffect, forwardRef, useImperativeHandle } from 'react'
import { Button, Tooltip, Badge, Anchor } from 'antd'
import { Table } from '@zswl/components'
import styles from './index.less'
import store from './store'
import { observer, getQuery } from '@zswl/admin'
import EnvironmentModal from './EnvironmentModal'
import ZdwModal from './ZdwModal'
import { DetailLayout } from '@/components/Layout'
import { anchorList } from './utils'
import { saveServer } from '@/utils'

const Public = forwardRef(({ ids, commonExtra, canEditFlag = true }, ref) => {
  store.clientId = ids
  const { publicNum } = store
  useImperativeHandle(ref, () => ({
    sync:store.synchronizationPublic,
  }));
  useEffect(() => {
    if (ids) {
      store.chattelMortgage.search()
      store.equityPledge.search()
      store.administrative.search()
      store.environmental.search()
      store.abnormalOperation.search()
      store.judicial.search()
      store.legalAction.search()
      store.limitConsumption.search()
      store.personSubject.search()
      store.dishonestPeople.search()
      store.zhongDW.search()
    }
  }, [ids])
  return (
    <div className={styles.public}>
      {canEditFlag && (
        <div className={styles.operation}>
          {/* <Button style={{ marginRight: '10px' }} onClick={store.synchronizationPublic}>
            同步当前页
          </Button> */}
          {commonExtra}
        </div>
      )}
      <DetailLayout
        anchorList={anchorList}
        title={''}
        extra={null}
        moduleName="customerPublic"
        style={{ border: 0 }}
      >
        <div id="dcdy" className={styles.tableWrap}>
          <h3 className={styles.title}>
            动产抵押
            <Badge
              count={publicNum.dsdy}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
            columnsFilter={'maintain_detail_Public_1'}
            onFilter={(key,val) => saveServer('maintain_detail_Public_1',val)}
            scroll={{ x: 1200 }}
            autoRequest={false}
            store={store.chattelMortgage}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 60 },
              { title: '登记日期', dataIndex: 'regDate', dateFormat: 'yyyy-MM-DD', tooltip: true },
              { title: '登记编号', dataIndex: 'regNum', tooltip: true },
              { title: '抵押权人', dataIndex: 'peopleInfo', tooltip: true },
              { title: '所有权或使用归属权', dataIndex: 'belongTo', width: 160, tooltip: true },
              { title: '被担保债权类型', dataIndex: 'type', width: 130 },
              {
                title: '被担保债权数额',
                align: 'right',
                dataIndex: 'amount',
                width: 130,
                render: (v, t) => {
                  return <span>{v}</span>
                },
              },
              { title: '债务人履行债务的期限', dataIndex: 'term', tooltip: true, width: 180 },
              { title: '登记机关', dataIndex: 'regDepartment', tooltip: true },
            ]}
          />
        </div>
        <div id="gqcz" className={styles.tableWrap}>
          <h3 className={styles.title}>
            股权出质
            <Badge
              count={publicNum.gqcz}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_2'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_2',val)}
            autoRequest={false}
            store={store.equityPledge}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 60 },
              {
                title: '股权出质设立登记日期',
                dataIndex: 'regDate',
                dateFormat: 'yyyy-MM-DD',
                width: 180,
              },
              { title: '登记编号', dataIndex: 'regNumber' },
              { title: '出质人', dataIndex: 'pledgor' },
              { title: '出质股权标的企业', dataIndex: 'targetCompany', tooltip: true },
              {
                title: '出质股权数额',
                align: 'right',
                dataIndex: 'equityAmount',
              },
              { title: '状态', dataIndex: 'state' },
            ]}
          />
        </div>
        <div id="xzcf" className={styles.tableWrap}>
          <h3 className={styles.title}>
            行政处罚
            <Badge
              count={publicNum.xzcf}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_3'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_3',val)}
            autoRequest={false}
            store={store.administrative}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 70 },
              { title: '处罚日期', dataIndex: 'decisionDate', dateFormat: 'yyyy-MM-DD' },
              { title: '决定文书号', dataIndex: 'punishNumber', tooltip: true },
              {
                title: '处罚事由/违法行为类型',
                dataIndex: 'reason',
                width: 180,
                render: (v, t) => {
                  if (v) {
                    return (
                      <Tooltip title={v}>
                        <div className={styles.reason}>{v}</div>
                      </Tooltip>
                    )
                  }
                  return '-'
                },
              },
              { title: '处罚结果/内容', dataIndex: 'content', tooltip: true },
              { title: '处罚单位', dataIndex: 'departmentName', tooltip: true },
              { title: '数据来源', dataIndex: 'source', tooltip: true },
            ]}
          />
        </div>
        <div id="hbcf" className={styles.tableWrap}>
          <h3 className={styles.title}>
            环保处罚
            <Badge
              count={publicNum.hbcf}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_4'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_4',val)}
            autoRequest={false}
            store={store.environmental}
            scroll={{
              x: 1100,
            }}
            extra={[
              canEditFlag && {
                name: '新增',
                type: 'primary',
                onClick: store.environmentalModal.open,
              },
            ].filter(Boolean)}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 60 },
              { title: '处罚日期', dataIndex: 'penaltyTime', dateFormat: 'yyyy-MM-DD', width: 130 },
              { title: '决定文书号', dataIndex: 'punishNumber', tooltip: true },
              { title: '处罚事由', dataIndex: 'reason' },
              { title: '处罚结果', dataIndex: 'result' },
              {
                title: '处罚金额(万元)',
                align: 'right',
                dataIndex: 'amount',
                width: 130,
                render: (v, t) => {
                  return <Amount value={v} />
                },
              },
              { title: '处罚单位', dataIndex: 'departmentName' },
              { title: '数据来源', dataIndex: 'source' },
              { title: '执行情况', dataIndex: 'info' },
              {
                title: '操作',
                fixed: 'right',
                width: '90',
                actions() {
                  return [
                    {
                      name: '编辑',
                      onClick: store.environmentalModal.open,
                      disabled: !canEditFlag,
                    },
                    { name: '删除', onClick: store.deleteEnvironment, disabled: !canEditFlag },
                  ]
                },
              },
            ]}
          />
        </div>
        <div id="jyyc" className={styles.tableWrap}>
          <h3 className={styles.title}>
            经营异常
            <Badge
              count={publicNum.jyyc}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_5'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_5',val)}
            autoRequest={false}
            store={store.abnormalOperation}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1 },
              { title: '列入日期', dataIndex: 'putDate', dateFormat: 'yyyy-MM-DD' },
              { title: '列入原因', dataIndex: 'putReason', tooltip: true },
              { title: '作出决定机关', dataIndex: 'putDepartment', tooltip: true },
              {
                title: '移出日期',
                dataIndex: 'removeDate',
                dateFormat: 'yyyy-MM-DD',
                tooltip: true,
              },
              { title: '移出原因', dataIndex: 'removeReason', tooltip: true },
            ]}
          />
        </div>
        <div id="sfxz" className={styles.tableWrap}>
          <h3 className={styles.title}>
            司法协助
            <Badge
              count={publicNum.sfxz}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_6'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_6',val)}
            autoRequest={false}
            store={store.judicial}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1 },
              { title: '公示日期', dataIndex: 'publicityDate', dateFormat: 'yyyy-MM-DD' },
              { title: '执行通知文书号', dataIndex: 'executeNoticeNum', width: 140, tooltip: true },
              { title: '被执行人', dataIndex: 'executedPerson', tooltip: true },
              {
                title: '股权被执行的企业',
                dataIndex: 'stockExecutedCompany',
                width: 150,
                tooltip: true,
              },
              {
                title: '股权数额(万元)',
                align: 'right',
                dataIndex: 'equityAmount',
                width: 140,
                tooltip: true,
                // render: (v, t) => {
                //   return v / 10000
                // },
              },
              { title: '执行法院', dataIndex: 'executiveCourt', tooltip: true },
              { title: '类型', dataIndex: 'typeState' },
              { title: '状态', dataIndex: 'status' },
            ]}
          />
        </div>
        <div id="flss" className={styles.tableWrap}>
          <h3 className={styles.title}>
            法律诉讼
            <Badge
              count={publicNum.flss}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_7'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_7',val)}
            autoRequest={false}
            store={store.legalAction}
            scroll={{
              x: 1100,
            }}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 60 },
              {
                title: '案件名称',
                dataIndex: 'title',
                render: (v, t) => {
                  if (v) {
                    return (
                      <Tooltip title={v}>
                        <div className={styles.judgeResult}>{v}</div>
                      </Tooltip>
                    )
                  }
                  return '-'
                },
              },
              { title: '案由', dataIndex: 'caseReason', tooltip: true },
              { title: '在本案中身份', dataIndex: 'identity' },
              {
                title: '裁判结果',
                dataIndex: 'judgeResult',
                render: (v, t) => {
                  if (v) {
                    return (
                      <Tooltip title={v}>
                        <div className={styles.judgeResult}>{v}</div>
                      </Tooltip>
                    )
                  }
                  return '-'
                },
              },
              { title: '结果标签', dataIndex: 'resultTag' },
              {
                title: '案件金额(元)',
                align: 'right',
                dataIndex: 'caseMoney',
              },
              {
                fixed: 'right',
                width: 90,
                title: '操作',
                actions(value) {
                  return [{ name: '详情', href: value.detailUrl, target: '_blank' }]
                },
              },
            ]}
          />
        </div>
        <div id="xzxfl" className={styles.tableWrap}>
          <h3 className={styles.title}>
            限制消费令
            <Badge
              count={publicNum.xzxfl}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_8'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_8',val)}
            autoRequest={false}
            scroll={{
              x: 1100,
            }}
            store={store.limitConsumption}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 60 },
              { title: '立案日期', dataIndex: 'caseCreateTime', dateFormat: 'yyyy-MM-DD' },
              { title: '案号', dataIndex: 'caseCode', tooltip: true },
              { title: '限制消费对象', dataIndex: 'xname' },
              { title: '关联限制消费对象', dataIndex: 'qyinfoAlias' },
              { title: '申请人信息', dataIndex: 'applicant', tooltip: true },
              { title: '发布日期', dataIndex: 'publishDate', dateFormat: 'yyyy-MM-DD' },
              {
                title: '操作',
                width: 90,
                fixed: 'right',
                actions(value) {
                  return [{ name: '详情', href: value.detailUrl, target: '_blank' }]
                },
              },
            ]}
          />
        </div>
        <div id="bzxr" className={styles.tableWrap}>
          <h3 className={styles.title}>
            被执行人
            <Badge
              count={publicNum.bzxr}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_9'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_9',val)}
            autoRequest={false}
            store={store.personSubject}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 60 },
              { title: '立案日期', dataIndex: 'caseCreateTime', dateFormat: 'yyyy-MM-DD' },
              { title: '案号', dataIndex: 'caseCode' },
              {
                title: '执行标的(元)',
                align: 'right',
                dataIndex: 'execMoney',
                render: (v) => {
                  return v
                },
              },
              { title: '执行法院', dataIndex: 'execCourtName' },
            ]}
          />
        </div>
        <div id="sxr" className={styles.tableWrap}>
          <h3 className={styles.title}>
            失信人
            <Badge
              count={publicNum.sxr}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_10'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_10',val)}
            autoRequest={false}
            store={store.dishonestPeople}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1, width: 60 },
              { title: '立案日期', dataIndex: 'regDate', dateFormat: 'yyyy-MM-DD' },
              { title: '案号', dataIndex: 'caseCode', tooltip: true },
              { title: '执行依据文号', dataIndex: 'gistId', tooltip: true },
              { title: '执行法院', dataIndex: 'courtName', tooltip: true },
              { title: '失信行为', dataIndex: 'disruptTypeName', tooltip: true },
              { title: '履行情况', dataIndex: 'performance', tooltip: true },
              { title: '发布日期', dataIndex: 'publishDate', dateFormat: 'yyyy-MM-DD' },
            ]}
          />
        </div>
        <div id="zdw" className={styles.tableWrap}>
          <h3 className={styles.title}>
            中登网
            <Badge
              count={publicNum.zdw}
              style={{ backgroundColor: 'rgba(37,88,230,0.4)' }}
              offset={[6, -2]}
            />
          </h3>
          <Table
                      columnsFilter={'maintain_detail_Public_11'}
                      onFilter={(key,val) => saveServer('maintain_detail_Public_11',val)}
            autoRequest={false}
            store={store.zhongDW}
            extra={[
              canEditFlag && {
                name: '新增',
                type: 'primary',
                onClick: store.zDwModal.open,
              },
            ].filter(Boolean)}
            columns={[
              { title: '序号', render: (item, n, index) => index + 1 },
              { title: '交易业务类型', dataIndex: 'tradeBusinessType' },
              { title: '授信机构', dataIndex: 'creditOrg' },
              {
                title: '金额（亿元）',
                align: 'right',
                dataIndex: 'amount',
                render: (v, t) => {
                  return <Amount value={v} />
                },
              },
              { title: '登记日期', dataIndex: 'regDate', dateFormat: 'yyyy-MM-DD' },
              { title: '登记到期日', dataIndex: 'regExpireDate', dateFormat: 'yyyy-MM-DD' },
              { title: '期限（年）', dataIndex: 'term' },
              {
                title: '操作',
                actions() {
                  return [
                    { name: '编辑', onClick: store.zDwModal.open, disabled: !canEditFlag },
                    { name: '删除', onClick: store.deleteZhongdengInfo, disabled: !canEditFlag },
                  ]
                },
              },
            ]}
          />
        </div></DetailLayout><EnvironmentModal />
      <ZdwModal />
    </div>)
})
export default observer(Public)