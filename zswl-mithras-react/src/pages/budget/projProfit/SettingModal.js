import { observer } from '@zswl/admin'
import { Table, Page, Modal, Form } from '@zswl/components'
import ModalDetail from '@/pages/kpi/baseSet/ModalDetail'
import { isAdminAccount } from '@/utils'
import FormListItem from './FormListItem'
import styles from './index.less'
import Api from '@/api/groupCredit/common'
import { useEffect, useState } from 'react'
import contractApi from '@/pages/contract/list/api'
import { saveServer } from '@/utils'

function Index({ store }) {
  const [contractList, setContractList] = useState([])
  const getContractList = async (val) => {
    const { list } = await contractApi.postContractList({ page: 1, pageSize: 9999 })
    const res = list.map(({ id, contractCode }) => ({ label: contractCode, value: id }))
    setContractList(res)
  }

  const [orgList, setOrgList] = useState([])
  const getOrgList = async (val) => {
    const res = await Api.getOrgList(
      {
        name: val,
        type: 1, //1:业务部门，2:领导层
      },
      {
        functionCode: 'selectorgs-groupCreditEstablish',
      }
    )
    const data = res.map(({ label, value }) => ({ label, value: +value }))
    setOrgList(data)
    return data
  }
  useEffect(() => {
    getContractList()
    getOrgList()
  }, [])
  return (
    <Modal title="参数设置" store={store.setModal} destroyOnClose width={800}>
      <Form className={styles.form}>
        <Form.List name="contractAssessDeptlDtoList" label="考核部门设置">
          {(fields, { add, remove }) => {
            return (
              <FormListItem
                fields={fields}
                add={add}
                remove={remove}
                fieldKey={'contractAssessDeptlDtoList'}
                orgList={orgList}
                contractList={contractList}
              />
            )
          }}
        </Form.List>
        <Table
          columnsFilter="budget_projProfit_SettingModal"
          onFilter={(key, val) => saveServer('budget_projProfit_SettingModal', val)}
          scroll={false}
          resizable
          pagination={false}
          store={store.paramsTable}
          columns={[
            {
              title: '参数类型',
              dataIndex: 'configDesc',
            },
            {
              title: '操作',
              fixed: 'right',
              width: 200,
              actions(record, rowIndex) {
                return [
                  {
                    name: '查看',
                    key: 'view',
                    onClick: () =>
                      store.editItem({
                        ...record,
                        isEdit: false,
                      }),
                  },
                  {
                    name: '编辑',
                    key: 'edit',
                    disabled: !isAdminAccount(),
                    onClick: () =>
                      store.editItem({
                        ...record,
                        isEdit: true,
                      }),
                  },
                ].filter(Boolean)
              },
            },
          ]}
        />
      </Form>
      <ModalDetail store={store}></ModalDetail>
    </Modal>
  )
}

export default observer(Index)
