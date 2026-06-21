import { AmountFormat } from '@/components/Format'
import { ClientSelect, OrgSelect } from '@/components/Select'
import { levelColor } from '../../../Level5ClassifyConfig'
import {
  getKeyOptionsLabelMapPlus,
  hasValue,
  isAssetJon,
  isSecretaryjury,
  saveServer,
} from '@/utils'
import { observer } from '@zswl/admin'
import { App, SearchBar, Select, Table } from '@zswl/components'
import { Badge, Space } from 'antd'
import { useMemo } from 'react'
import ClientSelectModal from '../ClientSelectModal'
import Edit from '../Edit'
import InnerTable from './InnerTable'
import styles from './index.less'

const { Item } = SearchBar

const AfterLeaseLevel5ClassifyList = ({ store, modelKey, curTaskActivityIds, query = {} }) => {
  const { isFormApproval } = store.page.getParams()

  const { optionsType } = App.getData()

  const getStatusColor = (value) => {
    const index = optionsType.assetClassifyResultEnum.findIndex((item) => item.value === value)
    return levelColor[index]
  }

  // 资产五级分类评审会审批流程 + 秘书汇总节点 + 秘书岗 || 资产五级分类风委会审批流程 + 资产管理岗节点 + 资产管理岗
  const canEdit = useMemo(() => {
    return (
      (modelKey === 'AssetClassifyReviewMeetingFlow' &&
        curTaskActivityIds === 'userTask_secretary' &&
        isSecretaryjury()) ||
      (modelKey === 'AssetClassifyRiskMeetingFlow' &&
        curTaskActivityIds === 'userTask_assetManager' &&
        isAssetJon())
    )
  }, [modelKey])

  const columns = useMemo(() => {
    const baseColumns = [
      {
        title: '客户名称',
        dataIndex: 'clientName',
        width: 250,
        actions({ clientName, id }) {
          let link = `/afterLease/level5Classify/detail/${id}`
          if (modelKey) {
            link = link + `?modelKey=${modelKey}&curTaskActivityIds=${curTaskActivityIds}`
          }
          // 看板过来的
          if (query.form === 'dashboard') {
            return [
              {
                name: clientName,
                onClick: () => {
                  window.open(link)
                },
              },
            ]
          }
          return [
            {
              name: clientName,
              // to: `/afterLease/level5Classify/detail/${id}`,
              to: link,
              className: 'z-single-line',
            },
          ]
        },
      },
      {
        title: '投放金额(元)',
        dataIndex: 'amount',
        align: 'right',
        render: (val) => {
          return hasValue(val) ? <AmountFormat value={val}></AmountFormat> : '-'
        },
      },
      {
        title: '存量风险敞口(元)',
        dataIndex: 'stockRiskExposure',
        align: 'right',
        render: (val) => {
          return hasValue(val) ? <AmountFormat value={val}></AmountFormat> : '-'
        },
      },
      {
        title: '资产余额(元)',
        dataIndex: 'assetBalance',
        align: 'right',
        render: (val) => {
          return hasValue(val) ? <AmountFormat value={val}></AmountFormat> : '-'
        },
      },
      {
        title: '上季度分类结果',
        width: 140,
        dataIndex: 'lastClassifyResult',
        render: (val) => {
          return hasValue(val) ? (
            <Space>
              <Badge color={getStatusColor(val)} text={''} />
              {getKeyOptionsLabelMapPlus('assetClassifyResultEnum')[val]}
            </Space>
          ) : (
            '-'
          )
        },
      },
      {
        title: '初分结果',
        width: 140,
        dataIndex: 'initClassifyResult',
        render: (val, { latestVersionClassifyResult }) => {
          return hasValue(val) ? (
            <Space>
              <Badge color={getStatusColor(val)} text={''} />
              <span
                style={
                  isFormApproval
                    ? latestVersionClassifyResult !== val
                      ? { color: 'red' }
                      : {}
                    : {}
                }
              >
                {getKeyOptionsLabelMapPlus('assetClassifyResultEnum')[val]}
              </span>
            </Space>
          ) : (
            '-'
          )
        },
      },
      {
        title: '建议分类',
        dataIndex: 'suggestResult',
        matchOption: 'assetClassifySuggestEnum',
        render: (val) => {
          return getKeyOptionsLabelMapPlus('assetClassifySuggestEnum')[val] || '-'
        },
      },
      !isFormApproval && {
        title: '复核状态',
        width: 100,
        dataIndex: 'reviewStatus',
        render: (val) => {
          return getKeyOptionsLabelMapPlus('assetClassifyReviewStatusEnum')[val] || '-'
        },
      },
      !isFormApproval && {
        title: '复核时间',
        dataIndex: 'reviewPassTime',
        width: 180,
        render: (val) => {
          return val || '-'
        },
      },
      {
        title: '所属主办',
        dataIndex: 'belongSponsorName',
      },
      {
        title: '所属部门',
        dataIndex: 'belongDeptName',
      },
      {
        title: '操作',
        width: 80,
        fixed: 'right',
        isAction: true,
        actions(record) {
          return [
            isFormApproval && {
              name: '编辑',
              access: 'assetclassifyClientRemove',
              onClick: () => store.editModal.open(record),
              disabled: !canEdit,
            },
            !isFormApproval && { name: '删除', onClick: () => store.remove(record) },
          ].filter(Boolean)
        },
      },
    ]

    return baseColumns.filter(Boolean)
  }, [isFormApproval, canEdit])

  return (
    <div className={styles.wrap}>
      <Table
        extra={[
          !isFormApproval && store.canClickMidSeasonInit && {
            name: '季中初分',
            onClick: () => store.midSeasonInitialDivision(),
            access: 'assetclassifyManualInitialDivision',
            disabled: store.isMidSeasonInitDisabled,
          },
          !isFormApproval && store.canClickMidSeasonInit && {
            name: store.midSeasonReviewButtonText,
            onClick: () => store.midSeasonReview(),
            disabled: !isAssetJon() || store.isMidSeasonFlowDisabled || !store.midInitStatue || !store.canClickMidSeasonReview,
          },
          !isFormApproval && store.canClickMidSeasonInit && {
            name: store.midSeasonReviewMeetingButtonText,
            onClick: () => store.midSeasonReviewMeeting(),
            disabled: !isAssetJon() || store.isMidSeasonFlowDisabled || !store.canClickReviewMeeting,
          },
          !isFormApproval && store.canClickMidSeasonInit && {
            name: store.midSeasonRiskMeetingButtonText,
            onClick: () => store.midSeasonRiskMeeting(),
            disabled: !isAssetJon() || store.isMidSeasonFlowDisabled || !store.canClickRiskMeeting,
          },
          {
            name: '汇总表下载',
            type: 'primary',
            onClick: store.exportSummaryFile,
            access: 'assetclassifysummaryfiledownload',
          },
        ].filter(Boolean)}
        columnsFilter="afterLease_level5Classify_list"
        onFilter={(key, val) => saveServer('afterLease_level5Classify_list', val)}
        resizable
        columnWidth={160}
        autoRequest={false}
        scroll={{ x: 1800 }}
        store={store.table}
        searchbar={
          <SearchBar labelCol={{ span: 6 }} limit="6">
            <Item label="客户名称" name="clientId" key="clientId">
              <ClientSelect
                canJump={false}
                functionCode="assetclassifyqueryclientlist"
              ></ClientSelect>
            </Item>
            <Item label="建议分类" name="classifyResult">
              <Select options={'assetClassifySuggestEnum'} />
            </Item>
            {![
              'AssetClassifyReviewMeetingFlow',
              'AssetClassifyRiskMeetingFlow',
              'AssetClassifyBoardMeetingFlow',
            ].includes(modelKey) ? (
              <Item label="复核状态" name="reviewStatus">
                <Select options={'assetClassifyReviewStatusEnum'} />
              </Item>
            ) : null}
            {isFormApproval ? (
              <Item label="业务部门" name="belongDeptId" key="belongDeptId">
                <OrgSelect />
              </Item>
            ) : (
              <Item label="是否关联方" name="isRelated" key="isRelated">
                <Select options={'yesOrNoNumberEnum'} />
              </Item>
            )}
          </SearchBar>
        }
        columns={columns}
        expandable={{
          expandedRowRender: (record) => {
            // const hasMessage = Access.validate('riskcontrolopinionmonitorunresolved')
            return (
              <div>
                <InnerTable id={record.id}></InnerTable>
              </div>
            )
          },
        }}
      />
      <Edit store={store}></Edit>
      <ClientSelectModal store={store} />
    </div>
  )
}

export default observer(AfterLeaseLevel5ClassifyList)
