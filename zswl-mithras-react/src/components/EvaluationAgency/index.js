import evaluationAgencyApi from '@/api/evaluationAgency/evaluationAgencyApi'
import assessmentWhitelistApi from '@/api/afterLease/assessmentWhitelistApi'
import { ApiSelect, EditDescription, NoEnumFileTable } from '@/components'
import { MatchOptionColumn } from '@/components/Format'
import { PlusOutlined } from '@ant-design/icons'
import { makeAutoObservable, observer } from '@zswl/admin'
import { Button, Form, Input, Modal, ModalStore, Select, Table, TableStore } from '@zswl/components'
import { Divider, Space, Checkbox, message } from 'antd'
import { uniqueId } from 'lodash'
import { useEffect, useMemo, useState } from 'react'
import { getUserInfo, saveServer } from '@/utils'

class Store {
  constructor({ id, notLease, functionCodeList }) {
    makeAutoObservable(this)
    this.id = id
    this.notLease = notLease
    this.functionCodeList = functionCodeList
  }
  table = new TableStore({
    request: async (params) => {
      if (this.notLease) {
        const res = await evaluationAgencyApi.postEvaluationAgencyList(
          { contractId: this.id },
          this.functionCodeList?.list
        )
        return res.map((v) => ({ ...v, id: uniqueId() }))
      } else {
        const res = await evaluationAgencyApi.postLeaseItemList({ leaseItemId: this.id })
        return res.map((v) => ({ ...v, id: uniqueId() }))
      }
    },
    pagination: false,
  })
  editable = false
  setEditable = (editable) => {
    this.editable = editable
  }
  detail = {}
  detailModal = new ModalStore({
    onOpen: async (record) => {
      const res = await evaluationAgencyApi.postAppraisalDetail(
        { companyId: record.companyId },
        this.functionCodeList?.detail
      )
      this.detail = res
      return res
    },
  })
  save = async () => {
    const leaseItemId = this.id
    const { list, values } = await this.table.submit()
    const every = list.every((item) => item.companyId && item.purpose)

    const hasRepeat =
      new Set(list.map((item) => item.companyId).filter(Boolean)).size !== list.length
    // 当有白名单时，只能选择一个白名单机构
    const whitelist = list.filter((item) => item.isWhitelist)
    if (whitelist.length >= 1 && list.length !== 1) {
      message.error('最多只能选择一个白名单机构')
      return false
    }
    if (!every) {
      message.error('请填写评估机构和用途')
      return false
    }
    if (hasRepeat) {
      message.error('评估机构不能重复')
      return false
    }
    await evaluationAgencyApi.postAppraisalRelation({
      leaseItemId,
      relationList: list,
    })
    this.table.search()
    this.setEditable(false)
    message.success('保存成功')
  }
  addModal = new ModalStore({
    onFinish: async (values) => {
      const companyId = await evaluationAgencyApi.postAppraisalAdd(values)
      this.addModal.close()
      message.success('添加成功')
      this.detailModal.open({ companyId })
      await this.getCompanyList()
    },
  })
  orgTable = new TableStore({
    request: async (params) => {
      const res = await assessmentWhitelistApi.postWhitelistPagelist({
        recordStatus: 'TAKE_EFFECT',
        page: 1,
        pageSize: 999,
        ...params,
      })
      return res
    },
    pagination: false,
  })
  // 白名单选择弹窗
  whitelistModal = new ModalStore({
    onOpen: async (record) => {
      const table = this.table.getList()
      const hasWhite = table.some((item) => item.isWhitelist)
      const isFirst = table.length === 1 && record.index === 0

      if (table.length > 0 && !isFirst) {
        message.error('如需选择白名单评估机构，请先删除「是否白名单准入」为“否”的评估机构！')
        return Promise.reject()
      }
      return record
    },
    onFinish: async (values) => {
      const { rows } = this.orgTable.getSelected()
      const { index } = this.whitelistModal.getInitialValues()
      if (rows.length === 0) {
        message.warning('请选择评估机构')
        return
      }

      const item = rows[0]
      // 将选中的白名单评估机构添加到表格中
      this.table.setRowByIndex(index, {
        companyId: item.companyId,
        companyName: item.companyName,
        creditCode: item.uscCode, // 统一信用代码
        isWhitelist: 1,
        selectType: 'SELECTED',
        disabled: true,
        purpose: '', // 用途需要用户后续填写
      })
      this.whitelistModal.close()
      message.success('添加成功')
    },
  })

  // 白名单数据
  whitelistData = []
  setWhitelistData = (data) => {
    this.whitelistData = data
  }

  // 获取白名单数据
  getWhitelistData = async (searchParams = {}) => {
    try {
      const res = await assessmentWhitelistApi.postWhitelistPagelist({
        page: 1,
        pageSize: 999,
        ...searchParams,
      })
      this.setWhitelistData(res.list || [])
    } catch (error) {
      console.error('获取白名单数据失败:', error)
      this.setWhitelistData([])
    }
  }
  delete = async (record) => {
    this.table.deleteRow(record)
  }
  add = async () => {
    const table = this.table.getList()
    const hasWhite = table.some((item) => item.isWhitelist)
    if (hasWhite) {
      message.error('请删除白名单评估机构后再进行新增操作！')
      return Promise.reject()
    }
    this.table.addRow({})
  }
  companyList = []
  setCompanyList = (companyList) => {
    this.companyList = companyList
  }
  getCompanyList = async (companyName) => {
    const res = await evaluationAgencyApi.postCompanyList({ companyName, pageSize: 999 })
    this.setCompanyList(res.list)
  }
}

const AddModal = observer(({ store }) => {
  const [form] = Form.useForm()
  const getList = async (companyName) => {
    const res = await evaluationAgencyApi.postAppraisalQueryCompany({ companyName, pageSize: 20 })
    return res
  }
  const companyChange = async (value, options) => {
    form.setFieldsValue(options)
  }
  return (
    <Modal title="添加评估机构" width={500} store={store.addModal} destroyOnClose>
      <Form form={form}>
        <Form.Item label="评估机构名称">
          <Select
            options={getList}
            onChange={companyChange}
            debounceSearch
            placeholder="请输入需要查询的公司名称"
            fieldNames={{ label: 'companyName', value: 'companyName' }}
          />
        </Form.Item>
        <Form.Item label="社会统一信用代码" name={'creditCode'}>
          <Input disabled />
        </Form.Item>
      </Form>
    </Modal>
  )
})

/**
 * 白名单评估机构选择弹窗
 */
const WhitelistModal = observer(({ store }) => {
  const columns = [
    {
      title: '评估机构名称',
      dataIndex: 'companyName',
      width: 200,
      search: true,
    },
    {
      title: '社会统一信用代码',
      dataIndex: 'uscCode',
      width: 180,
    },
    {
      title: '状态',
      dataIndex: 'recordStatus',
      width: 100,
      matchOption: 'recordStatus',
    },
    {
      title: '到期日',
      dataIndex: 'recordExpireDate',
      width: 120,
    },
  ]

  return (
    <Modal title="选择白名单评估机构" width={800} store={store.whitelistModal} destroyOnClose>
      <Form></Form>
      <Table
        selectable={{
          type: 'radio',
        }}
        rowKey="id"
        columns={columns}
        store={store.orgTable}
        scroll={{ y: 400 }}
      />
    </Modal>
  )
})
const DetailModal = observer(({ store, canEdit = true }) => {
  const { detail, functionCodeList } = store
  const columns = [
    { title: '评估机构名称', dataIndex: 'companyName' },
    { title: '社会统一信用代码', dataIndex: 'creditCode' },
    { title: '成立日期', dataIndex: 'establishDate' },
    { title: '营业许可证到期日', dataIndex: 'bizLicenseEndDate' },
    { title: '业务范围', dataIndex: 'bizScope' },
  ]

  const params = {
    mainId: detail.companyId,
    materialsType: 'APPRAISAL_DATA_LIST',
    materialsTypes: ['APPRAISAL_DATA_LIST'],
    moduleType: 'LEASE_APPRAISAL_DATA_LIST',
  }

  return (
    <Modal
      title="评估机构详情"
      width={800}
      store={store.detailModal}
      bodyStyle={{ overflowY: 'auto', maxHeight: 700 }}
      destroyOnClose
      footer={null}
    >
      {canEdit && (
        <div className="z-flex-jsb">
          <div></div>
          <Button type="primary" onClick={() => store.update()}>
            更新工商信息
          </Button>
        </div>
      )}
      <EditDescription columns={columns} dataSource={detail} canEdit={false} />
      <NoEnumFileTable
        title={'资料清单'}
        canEdit={canEdit}
        params={params}
        functionCodeList={functionCodeList}
        canEditItem={false}
        canDelete={(record) => {
          return (record.createBy?.value || record.createBy) == getUserInfo().id
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </Modal>
  )
})
const Index = ({ taskActivityId, canEdit: canEditFlag, id, notLease, functionCodeList }) => {
  const baseStore = useMemo(
    () => new Store({ id, notLease, functionCodeList }),
    [id, notLease, functionCodeList]
  )
  const { editable, setEditable, getCompanyList, companyList, setCompanyList } = baseStore

  useEffect(() => {
    getCompanyList()
  }, [])
  const isCustomerManage = taskActivityId === 'projManager'
  const isLegalAffairs = taskActivityId === 'legalManagerUser'

  const canEdit = canEditFlag && (isCustomerManage || isLegalAffairs)
  const companyChange = (value, options, index) => {
    const table = baseStore.table.getList()
    const isFirst = table.length === 1 && index === 0
    if (table.length > 0 && options.isWhitelist && !isFirst) {
      message.error('如需选择白名单评估机构，请先删除「是否白名单准入」为“否”的评估机构！')
      baseStore.table.setRowByIndex(index, { companyId: null })
      return Promise.reject()
    }
    baseStore.table.setRowByIndex(
      index,
      options.isWhitelist ? { ...options, selectType: 'SELECTED' } : options
    )
  }
  const companySelect = (record, index) => ({
    element: (
      <Select
        dropdownRender={(menu) => (
          <>
            {menu}
            <Divider style={{ margin: '8px 0' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Button type="text" icon={<PlusOutlined />} onClick={() => baseStore.addModal.open()}>
                新增评估机构
              </Button>
              <Button type="link" onClick={() => baseStore.whitelistModal.open({ index })}>
                选择白名单评估机构
              </Button>
            </div>
          </>
        )}
        options={companyList}
        onChange={(val, options) => companyChange(val, options, index)}
        disabled={record.isWhitelist}
        fieldNames={{ label: 'companyName', value: 'companyId' }}
      />
    ),
  })
  const columns = [
    {
      title: '评估机构名称',
      dataIndex: 'companyId',
      width: 500,
      render: (text, record) => {
        return (
          <a href="#" onClick={() => baseStore.detailModal.open(record)}>
            {record.companyName}
          </a>
        )
      },
      editable: editable && isCustomerManage ? companySelect : false,
    },
    MatchOptionColumn({
      title: '用途',
      dataIndex: 'purpose',
      matchOption: 'leaseAppraisalPurposeEnum',
      editable: isCustomerManage,
    }),
    !notLease && {
      title: '是否白名单准入',
      dataIndex: 'isWhitelist',
      width: 180,
      editable: false,
      matchOption: 'yesOrNo',
    },
    MatchOptionColumn({
      title: '法务是否选定',
      dataIndex: 'selectType',
      matchOption: 'leaseAppraisalSelectEnum',
      editable: (record) => {
        if (!isLegalAffairs || record.isWhitelist) {
          return false
        }
        return {
          element: (
            <Select
              options={'leaseAppraisalSelectEnum'}
              allowClear
              getPopupContainer={() => document.body}
            />
          ),
        }
      },
    }),
    editable &&
      isCustomerManage && {
        title: '操作',
        dataIndex: 'action',
        actions: (record) => [{ name: '删除', onClick: () => baseStore.delete(record) }],
      },
  ].filter(Boolean)

  return (
    <>
      <div className="z-flex-jsb" style={{ marginBottom: 12 }}>
        <div
          style={{
            fontSize: 16,
            color: 'rgba(0, 0, 0, 0.85)',
            fontWeight: 500,
          }}
        >
          评估机构
        </div>
        <Space>
          {canEdit && !editable && (
            <Button.Edit onClick={() => setEditable(true)}>编辑</Button.Edit>
          )}
          {editable && <Button onClick={() => setEditable(false)}>取消</Button>}
          {editable && <Button.Save onClick={baseStore.save}>保存</Button.Save>}
          {editable && !isLegalAffairs && <Button.Add onClick={baseStore.add}>新增</Button.Add>}
        </Space>
      </div>
      <Table
        columnsFilter={'maintain_detail_EvaluationAgency'}
        onFilter={(key, val) => saveServer('maintain_detail_EvaluationAgency', val)}
        columns={columns}
        scroll={{ x: 'auto' }}
        resizable
        store={baseStore.table}
        editable={editable}
      ></Table>
      <AddModal store={baseStore} />
      <WhitelistModal store={baseStore} />
      <DetailModal store={baseStore} canEdit={canEdit} />
    </>
  )
}

export default observer(Index)
