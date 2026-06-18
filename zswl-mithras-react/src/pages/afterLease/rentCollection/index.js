import { App, Page, SearchBar, Form, Button } from '@zswl/components'
import Store from './store'
import styles from './index.less'
import { ClientSelect, FounderSelect, OrgSelect } from '@/components/Select'
import { useEffect, useMemo, useState } from 'react'
import { Pagination, Radio, Space } from 'antd'
import { observer } from '@zswl/admin'
import { RentCollectionListRender as ListRender } from '@/components/AfterLease/RentCollectionEntries'
import { LifeCycleNoData as NoData } from '@/components/LifeCycle/LifeCycleEntries'
import InterestModal from './InterestModal'

const { Item } = Form
const RentCollection = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const {
    queryParams,
    setQueryParams,
    list,
    getList,
    groupChange,
    groupValue,
    penaltyInterest,
    setPenaltyInterest,
  } = store
  const { list: dataSource = [], total } = list || {}
  const radioList = [
    { key: 'HIDE_FINISH', value: '隐藏收款完成项' },
    { key: 'NOT_NOTICE_YET', value: '只看未通知' },
    { key: 'OVERDUE', value: '只看逾期' },
    { key: 'ALL', value: '显示全部' },
  ]

  useEffect(() => {
    getList()
  }, [])

  return (
    <Page>
      <SearchBar
        className={styles.table}
        labelCol={{ span: 7 }}
        items={[
          <Item label="客户名称" name="clientId" key="clientId">
            <ClientSelect canJump={false} functionCode="clientlist-rentCollection"></ClientSelect>
          </Item>,
          {
            label: '合同编号',
            name: 'contractCode',
          },

          <Item label="项目主办" name="projSponsorUserId" key="projSponsorUserId">
            <FounderSelect
              functionCode="selectfounder-rentCollection"
              params={{ job: 'projmanager' }}
            ></FounderSelect>
          </Item>,
          <Item label="业务部门" name="bizDeptId" key="bizDeptId">
            <OrgSelect functionCode="selectorgs-rentCollection"></OrgSelect>
          </Item>,
          {
            label: '业务类型',
            name: 'bizType',
            options: App.getData().optionsType.projEstablishBizType || [],
            allowClear: true,
          },
          {
            label: '合同状态',
            name: 'contractStatus',
            options: 'rentCollectionIndexContractStatus',
            allowClear: true,
          },
          {
            label: '收款日期范围',
            name: 'planCollectionDate',
            type: 'rangePicker',
          },
        ]}
        store={store.searchBar}
      />

      <div className={styles.wrap}>
        <div className={styles.filterBar}>
          <Space>
            <Button
              type="primary"
              onClick={() => setPenaltyInterest(true)}
              disabled={penaltyInterest}
            >
              罚息减免
            </Button>
            <Radio.Group
              value={queryParams.filterConditionType}
              onChange={(e) =>
                setQueryParams({
                  page: 1,
                  filterConditionType: e.target.value,
                })
              }
            >
              {radioList.map((item) => {
                return (
                  <Radio key={item.key} value={item.key}>
                    {item.value}
                  </Radio>
                )
              })}
            </Radio.Group>
          </Space>
          <Pagination
            current={queryParams.page}
            pageSize={queryParams.pageSize}
            total={total}
            showTotal={() => `共${total || 0}条`}
            showSizeChanger
            pageSizeOptions={[3, 5, 10, 20]}
            onChange={(page, pageSize) => {
              setQueryParams({
                page,
                pageSize,
              })
            }}
          />
        </div>
        {dataSource?.length > 0 ? (
          <>
            <ListRender
              dataSource={dataSource}
              penaltyInterest={penaltyInterest}
              groupChange={groupChange}
              groupValue={groupValue}
            />
            {penaltyInterest && (
              <div className="z-flex-jsb">
                <div></div>
                <Space>
                  <Button onClick={store.cancel}>取消</Button>
                  <Button onClick={store.submit} type="primary">
                    提交审批
                  </Button>
                </Space>
              </div>
            )}
          </>
        ) : (
          <NoData text={'暂无数据'} />
        )}
      </div>
      <InterestModal modal={store.interestModal} table={store.interestTable} />
    </Page>
  )
}
export default observer(RentCollection)
