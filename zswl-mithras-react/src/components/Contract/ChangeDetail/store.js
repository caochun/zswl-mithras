import { TableStore, ModalStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message, Modal } from 'antd'
import { hasValue, timeFormat, amountStrToNumber, numToFixed } from '@/utils'
import moment from 'moment'
import mathjs from '@/utils/math'
import { bizTypePriceDetailMap } from '../bizTypeConfig'
import Api from './api'

class Store {
  constructor(data) {
    this.bizType = data?.bizType
    this.contractId = data?.id
    this.changeType = data?.changeType
    this.businessVersion = data?.businessVersion
    this.isFormApproval = data?.isFormApproval
    this.form = data?.form
    makeAutoObservable(this)
  }

  isLoading = false
  page = new PageStore({
    request: async ({ id }) => {
      this.getDescDetail(id)
      this.get_old_baseinfo(id)
      return {}
    },
  })
  saveRemark = async (values) => {
    const res = await Api.saveAdjustRemark({ contractId: this.contractId, ...values })
    this.page.init()
  }

  showVal = false
  setShowVal = (flag) => {
    this.showVal = flag
  }
  remarkInfo = {}
  getDescDetail = async (id) => {
    if (['LPR_CHANGE'].includes(this.changeType)) {
      this.get_baseinfo(id)
    } else if (['CHANGE_REPAY_PLAN'].includes(this.changeType)) {
      const res = await Api.getBaseInfo({ id, businessVersion: this.businessVersion })
      this.remarkInfo = res
    } else if (['EXTENSION'].includes(this.changeType)) {
      this.get_baseinfo(id)
      const res = await Api.getBaseInfo({ id, businessVersion: this.businessVersion })
      this.remarkInfo = res
    }
  }

  // 获取上一版本数据
  old_baseinfo = {}
  get_old_baseinfo = async (contractId) => {
    const data = await Api.get_old_baseinfo({
      contractId,
      businessVersion: this.businessVersion,
    })
    const res = data[bizTypePriceDetailMap[this.bizType]] ?? {}
    this.old_baseinfo = {
      ...res,
      lprPercent: numToFixed(this.formatPercent(res.lprPercent)),
      lprAddPercent: numToFixed(this.formatPercent(res.lprAddPercent)),
    }
  }
  // 展期、LPR变更
  baseinfo = {}
  get_baseinfo = async (contractId) => {
    const data = await Api.get_baseinfo({
      contractId,
      businessVersion: this.businessVersion,
    })
    const res = data[bizTypePriceDetailMap[this.bizType]] ?? {}
    this.baseinfo = {
      ...res,
      lprPercent: numToFixed(this.formatPercent(res.lprPercent)),
      lprAddPercent: numToFixed(this.formatPercent(res.lprAddPercent)),
    }
  }

  formatPercent = (val) => {
    return hasValue(val)
      ? mathjs.toNonExponentialPlus(mathjs.format(mathjs.divide(val, 10000)))
      : undefined
  }
  formatNum = (key, values) => {
    if (hasValue(values[key])) {
      const v = values[key].toString().replace(/,/g, '')
      return mathjs.toNonExponentialPlus(mathjs.format(mathjs.multiply(v, 10000)))
    }
    return values[key]
  }

  onSubmit = async (isSave) => {
    const { form } = this.page.getParams()
    const commonParams = {
      changeType: this.changeType,
      contractId: this.contractId,
    }
    if (this.changeType === 'EARLY_REPAYMENT') {
      await Api.submitChange({
        ...commonParams,
      })
      message.success('提交成功')
      history.push('/contract/list')
      return
    }
    form.validateFields().then(async (values) => {
      if (this.changeType === 'LPR_CHANGE') {
        if (isSave) {
          this.isLoading = true
          await Api.update_baseinfo({
            ...commonParams,
            lprPercent: this.formatNum('lprPercent', values),
          }).finally(() => {
            this.isLoading = false
          })
          message.success('保存成功')
          this.setShowVal(true)
          this.getDescDetail(this.contractId)
          this.get_old_baseinfo(this.contractId)
        } else {
          if (!this.showVal) {
            message.info('请先保存变更方案')
            return
          }
          this.isLoading = true
          const res = await Api.submitChange({
            ...commonParams,
            onlyCheck:true,
            lprPercent: this.formatNum('lprPercent', values),
          }).finally(() => {
            this.isLoading = false
          })
          this.reSubmitChange(
            res,
            {
              ...commonParams,
              onlyCheck:false,
              lprPercent: this.formatNum('lprPercent', values)
            },
            () => {
              message.success('提交成功')
              this.setShowVal(true)
              history.push('/contract/list')
            }
          )
        }
      } else if (this.changeType === 'EXTENSION') {
        if (isSave) {
          this.isLoading = true
          await Api.update_baseinfo({
            ...commonParams,
            leaseMonthCount: mathjs.format(
              mathjs.add(
                amountStrToNumber(values.old_leaseMonthCount || 0),
                amountStrToNumber(values.leaseMonthCountAdd || 0)
              )
            ),
          }).finally(() => {
            this.isLoading = false
          })
          message.success('保存成功')
          this.setShowVal(true)
          this.getDescDetail(this.contractId)
          this.get_old_baseinfo(this.contractId)
        } else {
          if (!this.showVal) {
            message.info('请先保存变更方案')
            return
          }
          this.isLoading = true
          const res = await Api.submitChange({
            ...commonParams,
            onlyCheck:true,
            leaseMonthCount: mathjs.format(
              mathjs.add(
                amountStrToNumber(values.old_leaseMonthCount || 0),
                amountStrToNumber(values.leaseMonthCountAdd || 0)
              )
            ),
          }).finally(() => {
            this.isLoading = false
          })
          this.reSubmitChange(
            res,
            {
              ...commonParams,
              onlyCheck:false,
              leaseMonthCount: mathjs.format(
                mathjs.add(
                  amountStrToNumber(values.old_leaseMonthCount || 0),
                  amountStrToNumber(values.leaseMonthCountAdd || 0)
                )
              )
            },
            () => {
              message.success('提交成功')
              this.setShowVal(true)
              history.push('/contract/list')
            }
          )
        }
      } else if (this.changeType === 'CHANGE_REPAY_PLAN') {
        this.isLoading = true
        const res = await Api.submitChange({
          ...commonParams,
          onlyCheck:true,
          actualLeaseDate: timeFormat(form.getFieldValue('actualLeaseDate')),
        }).finally(() => {
          this.isLoading = false
        })
        this.reSubmitChange(
          res,
          {
            ...commonParams,
            onlyCheck:false,
            actualLeaseDate: timeFormat(form.getFieldValue('actualLeaseDate')),
          },
          () => {
            message.success('提交成功')
            history.push('/contract/list')
          }
        )
      }
    })
  }

  reSubmitChange = async(msg,commonParams, cb) => {
    if(!msg){
      await Api.submitChange(commonParams)
      cb&&cb()
      return;
    }
    Modal.confirm({
      title: msg,
      onOk: async () => {
        await Api.submitChange(commonParams)
        cb&&cb()
      },
    })
  }

  cancelFlow = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.cancelFlow({
          contractId: this.contractId,
          changeType: this.changeType,
        })
        message.success('操作成功')
        setTimeout(() => {
          history.push(`/contract/list`)
        }, 500)
      },
    })
  }

  removeFile = async ({ fileId }) => {
    Modal.confirm({
      title: '是否删除该文件？',
      onOk: async () => {
        await Api.deleteMaterialsUpload({
          fileId,
          contractId: this.contractId,
          changeType: this.changeType,
        })
        message.success('删除成功')
        this.$table.search({ contractId: this.contractId })
      },
    })
  }
}


export default Store
