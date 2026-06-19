import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import EclBusinessApi from '@/api/budget/provisioning/eclBusinessApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  $table = new TableStore({
    request: async (params) => {
      try {
        const data = await EclBusinessApi.postConfigList({
          ...params,
        })

        // 字段映射处理
        if (data && data.list) {
          data.list = data.list.map((item) => {
            return {
              id: item.id,
              configName: item.configName || '-',
              versionTime: item.versionTime || '-',
              configModule: item.configModule || '-',
              configCode: item.configCode || '-',
              configValue: item.configValue || '-',
            }
          })
        }
        return data
      } catch (error) {
        console.error('获取配置列表失败:', error)
        return {
          list: [],
          total: 0,
        }
      }
    },
  })

  historyModal = new ModalStore({
    onOpen: async (data) => {
      this.$historyTable.setParams({ configCode: data.configCode })
      this.$historyTable.search()
    },
  })
  $historyTable = new TableStore({
    pagination: false,
    request: async (params) => {
      const result = await EclBusinessApi.postVersionList({
        configCode: params.configCode,
      })
      return result
    },
  })
}
export default new Store()
