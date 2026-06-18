import { history, makeAutoObservable } from '@zswl/admin'
import { Modal, PageStore } from '@zswl/components'
import { message } from 'antd'
import Api from '../api'
import { contractCheckIrr as checkIrr } from '@/components/Contract/ProcessDetailEntries'

class Store {
  constructor(id) {
    makeAutoObservable(this)
  }
  id
  page = new PageStore({
    request: async ({ id }) => {
      this.id = id
      const result = await Api.getDetail({ id })
      return result
    },
  })

  submitProcess = async () => {
    const { processType, businessId } = this.page.getData()
    if (processType === 'NewAfterLeaseCheckPlanPublishCreateFlow') {
      Modal.confirm({
        title: '是否确认发布计划？',
        content: '确认后系统将于租后截止日前20天推送任务至项目经理。',
        onOk: async () => {
          await Api.submitProcess({ id: this.page.getParams().id })
          message.success('提交成功！')
          history.push(`/process/application?t=${Date.now()}`)
        },
      })
    } else if (processType === 'ProjectProfitSharingFlow') {
      Modal.confirm({
        title: '确认提交吗？',
        onOk: async () => {
          await Api.postProjectdistributionSubmit({ projectDistributionId: businessId })
          message.success('提交成功！')
          history.push(`/process/application?t=${Date.now()}`)
        },
      })
    } else if (processType === 'FundFilingMaterialsApplyFlow') {
      const validate = await Api.checkFile({ id: businessId })
      if (validate) {
        Modal.confirm({
          title: (
            <>
              当前已上传材料为
              <span style={{ fontWeight: 'bold' }}>最终归档内容</span>
              ，该流程提交后再上传的资料将不再纳入归档，请确认！
            </>
          ),
          okText: '继续提交',
          onOk: async () => {
            await Api.submitProcess({ id: this.page.getParams().id })
            message.success('提交成功！')
            history.push(`/process/application?t=${Date.now()}`)
          },
        })
      }
    } else if (processType === 'MarginFlowAuto') {
      Modal.confirm({
        title: '确认提交吗？',
        onOk: async () => {
          await Api.noticeCommit(this.id)
          message.success('提交成功！')
          history.push(`/process/application?t=${Date.now()}`)
        },
      })
    } else if (['ContractStartRentAutoFlow'].includes(processType)) {
      const valid = await checkIrr({ contractId: businessId },false)
      Modal.confirm({
        title: '确认提交吗？',
        onOk: async () => {
          await Api.submitProcess({ id: this.page.getParams().id })
          message.success('提交成功！')
          history.push(`/process/application?t=${Date.now()}`)
        },
      })
    } else {
      Modal.confirm({
        title: '确认提交吗？',
        onOk: async () => {
          await Api.submitProcess({ id: this.page.getParams().id })
          message.success('提交成功！')
          history.push(`/process/application?t=${Date.now()}`)
        },
      })
    }
  }
}
export default Store
