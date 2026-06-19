import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { PageStore, ModalStore, TableStore, FormStore, SearchBarStore } from '@zswl/components'
import { message } from 'antd'
import archivesManageApi from '@/api/archives/manage'
import { downFile } from '@/utils'
class Store {
	constructor() {
		makeAutoObservable(this, { tableStores: true })
	}
	data = []
	uploadData = []
	downloadTreeData = []
	tableStores = {}
	archiveId = null
	fileSearch = []
	detail = {}
	page = new PageStore({
		request: async (params) => {
			const { id } = params
			this.archiveId = id
			const detail = await archivesManageApi.detail({
				id: Number(id)
			})
			this.detail = detail
			await this.pageSearch()
			this.getTable()
		}
	})

	getTable = async () => {
		const res = []
		const tempObj = {
			key: '待补充文档',
			href: "#待补充文档",
			title: "待补充文档",
			...this.detail
		}
		res.push(tempObj)
		this.fileSearch?.map(item => {
			const _data = item?.files?.map(v => {
				return {
					...v,
					key: v.fileType
				}
			})
			const fileObj = {
				key: item.groupName,
				href: `#${item.groupName}`,
				title: item.groupName,
				id: item.groupName,
				files: _data
			}
			res.push(fileObj)
		})
		this.data = res
		const obj = {}
		this.data?.forEach(item => {
			if (item?.title !== '待补充文档') {
				obj[item.id] = new TableStore({
					pagination: false,
					request: () => {
						const files = item.files?.map(file => {
							return {
								...file,
								parentId: item.id,
								title: item.title
							}
						})
						return files
					}
				})
			}
		})
		this.tableStores = obj
		return res
	}

	// // 搜索内容：非受控组件
	// searchValue = ''
	// setSearchValue = (value) => {
	// 	this.searchValue = value
	// }
	//页面搜索
	pageSearch = async (val) => {
		const fileSearch = await archivesManageApi.fileSearch({
			id: this.archiveId,
			content: val
		})
		this.fileSearch = fileSearch
		this.getTable()
	}

	//页面搜索：受控组件
	searchBar = new SearchBarStore({
		onSearch: async (params) => {
			const fileSearch = await archivesManageApi.fileSearch({
				id: this.archiveId,
				content: params.content
			})
			this.fileSearch = fileSearch
			this.getTable()
		}
	})

	//借阅申请下载操作
	download = () => {
		let tree = []
		Object.keys(this.tableStores).forEach(key => {
			const { rows } = this.tableStores[key].getSelected()
			let uniqueIdTree = {}
			rows.map(item => {
				if (!Array.isArray(uniqueIdTree[item.parentId])) {
					uniqueIdTree[item.parentId] = []
				}
				uniqueIdTree[item.parentId].push({
					title: item.title,
					key: item.key,
					children: item
				})
			})
			let obj = {}
			Object.keys(uniqueIdTree).forEach(item => {
				uniqueIdTree[item].forEach(child => {
					obj.title = child.title;
					obj.key = child.children.parentId;
					if (!Array.isArray(obj.children)) {
						obj.children = []
					}
					obj.children.push({
						title: child.children.fileName,
						key: child.children.fileId
					})
				})
				tree.push(obj)
			})
		})
		this.downloadTreeData = tree
	}

	//可借阅数据下载
	downloadFile = async (params) => {
		const res = await archivesManageApi.getFileDownload(params)
		if (res?.code === 200) {
			downFile(res)
			message.success('下载成功!')
		} else if (res?.msg) {
			message.error(res.msg)
		}
	}

	/**
	* 申请下载Modal
	*/
	downloadModal = new ModalStore({
		onOpen: () => {
			this.download()
			return this.downloadTreeData
		}
	})
	submit = async (data) => {
		const { reason } = await this.form.submit()
		const params = {
			files: data,
			reason,
			archivesIds: [this.archiveId]
		}
		await archivesManageApi.downloadEffect(params)
		message.success('申请借阅成功！')
		this.downloadModal.close()
		this.pageSearch()
	}

	form = new FormStore()
	form2 = new FormStore()

	getFileUpload = async (id) => {
		const uploadData = await archivesManageApi.uploadInfo(id)
		this.uploadData = uploadData
	}
	//文件上传弹窗
	uploadModal = new ModalStore({
		onOpen: (id) => {
			this.getFileUpload(id)
		},
		onFinish: (values) => {
			this.upload(values, () => this.uploadModal.close())
		},
	})

	//文件删除
	remove = async (params) => {
		const functionCode = 'ARCHIVESFileRemove'
		const { code, msg } = await archivesManageApi.postFileRemove(
			params,
			functionCode
		)
		if (code === 200) {
			message.info('文件删除成功！')
			this.getFileUpload({ id: params.mainId })
		} else {
			msg && message.info(msg)
		}
	}

	//文件上传操作
	upload = async (params, callback) => {
		const formData = new FormData()
		const { materialsType, file } = params
		formData.append('file', file)
		formData.append('mainId', this.archiveId)
		formData.append('moduleType', 'ARCHIVES')
		formData.append('materialsType', materialsType)
		const { code, msg, data } = await archivesManageApi.postUpload(formData)
		if (code === 200) {
			message.success('上传成功')
			this.getFileUpload({ id: this.archiveId })
			// callback && callback()
		} else {
			message.info(msg)
		}
		return { code, data }
	}

	// 提醒催办
	remind = async () => {
		const params = {
			id: this.archiveId
		}
		await archivesManageApi.remind(params)
		message.success('提醒催办成功！')
	}

	//提交审批
	effectSubmit = async () => {
		const params = {
			id: this.archiveId
		}
		await archivesManageApi.effect(params)
		message.success('提交审批成功！')
		this.uploadModal.close()
		this.searchBar.reset()
		this.page.init()
	}
}
export default Store
