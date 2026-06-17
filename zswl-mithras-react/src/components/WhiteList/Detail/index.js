import { observer, getQuery } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useMemo } from 'react'
import Store from './store'
import DetailLayout from '@/components/DetailLayout'
import BaseInfo from './BaseInfo'
import { Button } from 'antd'
import { EditDescription, NoEnumFileTable } from '@/components'
import { TextAreaColumn } from '@/components/Format'
import { isDept } from '@/utils'

/**
 * 白名单详情页组件
 * 用于展示评估机构的详细信息
 * @param {Object} props - 组件属性
 * @param {Object} props.params - 路由参数
 * @param {string} props.params.id - 白名单记录ID
 * @param {Object} props.query - 查询参数
 * @param {string} props.query.canEditFlags - 是否可编辑标识
 */
const Detail = ({ params: { id }, query: { canEditFlags = 'true', businessVersion, type } }) => {
  // 创建 store 实例
  const store = useMemo(() => {
    return new Store()
  }, [])

  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'

  // 获取详情数据
  const detail = store.page.getData()

  const isOut = type == 'out'
  const isChange = type == 'change'

  // 新建未提交、变更未提交、流程在发起人节点时可操作
  const isRevocation = ['sendback', 'revocation'].includes(getQuery('tab'))
  const approvalCanEdit = isFormApproval && isRevocation
  const isSameDept = isDept(detail.deptId)
  const canEdit =
    ([
      'NEW_APPROVAL_PASS',
      'NEW_UN_SUBMIT',
      'CHANGE_APPROVAL_PASS',
      'CANCEL_NEW',
      'CHANGE_UN_SUBMIT',
      'CHANGE_REJECT',
      'CANCEL_CHANGE',
    ].includes(detail.processStatus) &&
      isSameDept) ||
    approvalCanEdit

  // 出库编辑权限：出库且在审批流程中时需要撤回权限，否则默认可编辑
  const outCanEdit =
    (isOut && !['OUT_UNDER_APPROVAL'].includes(detail.processStatus)) || isRevocation

  // 锚点列表配置
  const anchorList = [
    { label: '出库说明', isHide: !isOut && !detail.outReason },
    { label: '补充资料', isHide: !isOut && !detail.outReason },
    { label: '评估机构信息' },
    { label: '资料清单' },
  ]

  const params = {
    mainId: id,
    moduleType: 'APPRAISAL_COMPANY_WHITELIST',
    businessVersion,
  }
  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]
  const hasCancel = ['change'].includes(type)
  return (
    <Page store={store.page} params={{ id, isFormApproval, version: businessVersion }}>
      <DetailLayout
        anchorList={anchorList}
        title="评估机构详情"
        extra={[
          !isFormApproval && hasCancel && canEdit && (
            <Button onClick={() => store.handleCancel()}>取消操作</Button>
          ),
          !isFormApproval && isOut && (
            <Button type="primary" onClick={() => store.handleOutSubmit()}>
              出库审批
            </Button>
          ),
          !isFormApproval && canEdit && !isOut && (
            <Button type="primary" onClick={() => store.handleSubmit()}>
              提交审批
            </Button>
          ),
        ]}
      >
        <EditDescription
          title="出库说明"
          dataSource={detail}
          canEdit={outCanEdit}
          columns={[
            TextAreaColumn({
              title: '出库原因',
              dataIndex: 'outReason',
              span: 2,
              editable: true,
              required: true,
            }),
          ]}
          saveData={store.saveData}
        />
        <NoEnumFileTable
          title="补充资料"
          canEdit={outCanEdit}
          params={{ ...params, materialsType: 'EXTRA_OUT' }}
          columns={columns}
          needApproval={false}
          canBatchDownload
        />
        <BaseInfo id="baseInfo" dataSource={detail} canEdit={canEdit && !isOut} store={store} />
        <NoEnumFileTable
          enumType={'policyMaterialsEnum'}
          title={'资料清单'}
          canEdit={canEdit && !isOut}
          columns={columns}
          canBatchDownload
          canEditItem={false}
          needApproval={false}
          params={{ ...params, materialsType: 'NORMAL' }}
        />
      </DetailLayout>
    </Page>
  )
}

export default observer(Detail)
