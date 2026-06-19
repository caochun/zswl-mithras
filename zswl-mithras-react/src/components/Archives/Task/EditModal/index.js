import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { Input, Tree, Switch, Upload, List, Button } from 'antd'
import { PlusOutlined, DeleteOutlined, MinusSquareOutlined, PlusSquareOutlined } from '@ant-design/icons'
import styles from '../index.less'
import store from '../store'

const { Item } = Form
function AddModal({ modalStore }) {
	const { data, disableAdd, disableList, template, isDetail, isFold } = store
	return (
		<Modal
			title={`${isDetail ? '查看' : '新建'}模版`}
			store={modalStore}
			okText={'确定'}
			onOk={store.submit}
			// onCancel={() => {
			// 	App.resetStore(store)
			// 	store.table.search()
			// }}
			destroyOnClose
		>
			<Form store={store.form} labelCol={{ span: 6 }}>
				<Item
					label={'模版名称'}
					name={'templateName'}
					rules={[{ required: true, message: '请输入模版名称！' }]}
				>
					<Input placeholder='请输入模版名称' disabled={isDetail} />
				</Item>
				<Item
					label={'适用业务类型'}
					name={'bizType'}
					rules={[{ required: true, message: '请选择适用业务类型！' }]}
				>
					<Select
						mode='multiple'
						placeholder='请选择适用业务类型'
						disabled={isDetail}
						options={'projEstablishBizType'}
					/>
				</Item>
				<Item
					label={<div><span className={styles.requireTag}>*</span>模版状态</div>}
					name={'status'}
				// rules={[{ required: true }]}
				>
					<Switch
						checkedChildren="启用"
						unCheckedChildren="禁用"
						defaultChecked={template.status === 'ENABLE' || false}
					// disabled={isDetail}
					/>
				</Item>
				{
					!isDetail &&
					<Item name={'addBlock'} noStyle>
						<div className={styles.dragger} onClick={() => store.setDisableAdd(false)}>
							<div className={styles.draggerItem}>
								<PlusOutlined className={styles.icon} />
								<span className={styles.text}>新增资料类型</span>
							</div>
						</div>
					</Item>
				}
				{
					!disableAdd &&
					<Item name={'addModule'} noStyle>
						<div className={styles.uploadArea} style={{ marginTop: 10 }}>
							<div className={styles.areaInput} >
								<Item
									noStyle
									name={'groupName'}
									rules={[{ required: true, message: '请输入资料类型名称！' }]}>
									<Input placeholder='资料类型名称' className={styles.inputType} />
								</Item>
								<Button type="text" size='small' onClick={() => store.setDisableAdd(true)}>取消</Button>
								<Button type="link" size='small' onClick={() => store.getFileInfo()}>确定</Button>
							</div>
						</div>
					</Item>
				}
				{
					!disableList &&
					<Item name={'listModule'} noStyle>
						{
							data && data?.map((item, index) => {
								return <div key={item.groupId} className={styles.test}>
									<List
										itemLayout="horizontal"
										dataSource={item?.items || []}
										key={item.groupId}
										header={
											<>
												<div className={styles.fileList}>
													<div className={styles.fileTitle}>
														{
															(isFold[index]) ?
																<PlusSquareOutlined
																	className={styles.icon}
																	onClick={() => { store.changeStyleStatus(index, false) }}
																/>
																: <MinusSquareOutlined
																	className={styles.icon}
																	onClick={() => { store.changeStyleStatus(index, true) }}
																/>
														}

														<span className={styles.title}>{item.groupName || ''}</span>
													</div>
													{
														!isDetail &&
														<>
															<div className={styles.fileDelete}>
																<DeleteOutlined className={styles.iconDelete} />
																<span className={styles.textDelete} onClick={() => store.delModule(item.id)}>删除</span>
															</div>
															<div className={styles.fileAdd} onClick={() => store.setSwitch(item.id)}>
																<PlusOutlined className={styles.iconAdd} />
																<span className={styles.textAdd}>文档类型</span>
															</div>
														</>
													}
												</div>
											</>
										}
										size="small"
										renderItem={(v) => (
											<List.Item
												key={item.groupId}
												actions={!isDetail && [
													<a key="list-delete" onClick={() => store.delItem(item.id, v)}>删除</a>
												]}
												className={
													isFold[index] ?
														styles.listItemFold : styles.listItemNotFold
												}
											>
												{(v.need ? '(必传)' : '(非必传)') + v.fileType}
											</List.Item>
										)}
									/>
									{
										!(item.disabled) &&
										<>
											<div className={styles.uploadArea}>
												<div className={styles.areaSwitch}>
													<Item
														label="是否必传"
														name={'need' + item.id}
														className={styles.switchItem}
													>
														<Switch className={styles.switch} />
													</Item>
												</div>
												<div className={styles.areaInput}>
													<Item
														noStyle
														name={'fileType' + item.id}
														rules={[{ required: true, message: '请输入文档类型！' }]}>
														<Input placeholder='文档类型' className={styles.inputType} />
													</Item>
													<Button type="text" size='small' onClick={() => store.setCancel(item.id)}>取消</Button>
													<Button type="link" size='small' onClick={() => store.setFileList(item.id)}>确定</Button>
												</div>
											</div>
										</>
									}
								</div>
							})
						}
					</Item>
				}
			</Form>
		</Modal>
	)
}

export default observer(AddModal)
