import { ModalStore, Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/layout/announcementApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  // 获取banner详情
  bannerDetailLoading = false
  bannerDetail = {}
  getBannerDetail = async (id) => {
    if (!id) return
    this.bannerDetailLoading = true
    const res = await Api.workbenchAnnouncementDetail({
      id,
    })
    this.bannerDetail = res
    this.bannerDetailLoading = false
  }
  // 获取banner列表
  initLoading = true
  bannerListData = {}
  allBannerList = []
  PAGESIZE = 100

  getAllBannerList = async (page = 1) => {
    this.initLoading = true
    const res = await Api.workbenchAnnouncementList({
      page,
      pageSize: this.PAGESIZE,
      // exception: isInformationpost(),
    })
    this.bannerListData = res
    this.allBannerList = res?.list
    this.initLoading = false
  }

  $bannerModal = new ModalStore({
    onOpen: (data) => {
      this.curBannerId = data?.id
      this.pageStatus = 'view'
    },
    onFinish: async (values) => {
      const { title, content, expiration } = values
      const dateFormat = (date) => date && moment(date).format('YYYY-MM-DD')
      await Api.workbenchAnnouncementModify({
        id: this.curBannerId,
        title,
        content,
        expirationFrom: dateFormat(expiration?.[0]),
        expirationTo: dateFormat(expiration?.[1]),
      })
      this.pageStatus = 'view'
      this.curBannerId = ''
      this.getAllBannerList()
    },
  })
  pageStatus = ''
  curBannerId
  onclickItem = ({ id }) => {
    this.curBannerId = id
    this.pageStatus = 'view'
  }

  // 新增
  handleCreate = async () => {
    this.$createBanner.open()
  }

  // 编辑
  handleEdit = async () => {
    this.pageStatus = 'create'
  }
  // 删除
  handleDelete = async () => {
    Modal.confirm({
      title: `请确认是否删除？`,
      onOk: async () => {
        await Api.workbenchAnnouncementRemove({ id: this.curBannerId })
        message.success('删除成功')
        this.curBannerId = ''
        this.bannerDetail = {}
        this.getAllBannerList()
      },
    })
  }

  // 置顶
  handleSetTop = async () => {
    await Api.workbenchAnnouncementTop({ id: this.curBannerId })
    message.success('操作成功')
    this.getAllBannerList()
    this.getBannerDetail(this.curBannerId)
  }

  // 取消
  handleCancel = async () => {
    this.curBannerId = ''
    this.bannerDetail = {}
    this.pageStatus = 'view'
    this.getAllBannerList()
  }

  // 新增banner
  $createBanner = new ModalStore({
    onFinish: async (values) => {
      const { title } = values
      const res = await Api.workbenchAnnouncementAdd({
        title,
      })
      this.curBannerId = res
      this.bannerDetail = {
        title,
      }
      this.pageStatus = 'create'
      this.$createBanner.close()
      if (!this.$bannerModal.visible) {
        this.$bannerModal.open({ id: res })
      }
    },
  })
}
export default Store
