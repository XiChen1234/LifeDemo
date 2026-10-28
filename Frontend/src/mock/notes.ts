import type { NoteFolder, Note, TagStat } from '../types'

export const mockNotesBanner = {
  title: '本月写作状态不错',
  desc: '新增 26 篇笔记，本周编辑 9 篇，标签覆盖 24 个。读书笔记与产品思考是本月的主线。',
  chips: ['总量 428 篇', '本月 +26', '本周 9 篇'],
}

export const mockNotesMetrics = [
  { label: '笔记总量', value: '428', unit: '篇' },
  { label: '本月新增', value: '26', unit: '篇' },
  { label: '标签数', value: '24', unit: '个' },
  { label: '本周编辑', value: '9', unit: '篇' },
]

export const mockNoteFolders: NoteFolder[] = [
  { id: 'inbox', icon: '📥', name: '00 Inbox', count: 12 },
  { id: 'notes', icon: '📝', name: '10 Notes', count: 212 },
  { id: 'reading', icon: '📖', name: '读书笔记', count: 86, parentId: 'notes' },
  { id: 'product', icon: '💡', name: '产品思考', count: 54, parentId: 'notes' },
  { id: 'life', icon: '🌿', name: '生活记录', count: 72, parentId: 'notes' },
  { id: 'projects', icon: '🚀', name: '20 Projects', count: 34 },
  { id: 'archive', icon: '🗄️', name: '30 Archive', count: 170 },
]

export const mockNotes: Note[] = [
  {
    id: 'n1',
    folderId: 'reading',
    title: '《慢思考》读书笔记',
    tag: '#读书',
    tagClass: 'low',
    timeAgo: '2 小时前',
    words: 1240,
    paragraphs: [
      { type: 'p', text: '注意力不是无限资源，而是一种需要被保护的稀缺品。这一章讲「专注的节奏」，作者把一天分成三种状态：深度工作、浅层处理、恢复。' },
      { type: 'h4', text: '关键提醒' },
      { type: 'p', text: '不要把「休息」当成偷懒，它是下一段专注的入场券。试着把手机放在另一个房间，只用纸笔记录当天最重要的三件事。' },
      { type: 'p', text: '读到这里时，我想到自己每天下午三点的疲惫，也许不是需要咖啡，而是需要一次真正的空白。' },
      { type: 'p', text: '另外，作者在书里反复提到「单任务」的价值。我们总觉得同时处理多件事是效率，其实每一次切换都在悄悄消耗注意力。真正高效的，往往是把一件事做到彻底，然后安心离开。' },
      { type: 'p', text: '这一点放在写作上也成立：与其开五个文档各写一半，不如先把一篇写完。' },
    ],
    tags: [
      { text: '#读书', class: 'low' },
      { text: '#认知', class: 'blue' },
      { text: '#2025', class: 'mid' },
    ],
  },
  {
    id: 'n2',
    folderId: 'product',
    title: '关于「生活台」的信息架构',
    tag: '#产品',
    tagClass: 'blue',
    timeAgo: '昨天',
    words: 860,
    paragraphs: [
      { type: 'p', text: '个人 Dashboard 的核心不是「展示更多数据」，而是「在正确的时间给出正确的提醒」。信息架构应该围绕一天的使用路径展开：早晨看一眼状态，晚上做一次回顾。' },
      { type: 'h4', text: '三个原则' },
      { type: 'p', text: '第一，概览优先于明细，首页只回答「今天怎么样」。第二，同类信息聚合，健康、财富、工作各自成页，互不干扰。第三，所有数据都要能回溯来源，每一张图表都应可追问。' },
      { type: 'p', text: '下一步准备把笔记系统也纳入进来，让「记录」和「回顾」形成闭环。' },
    ],
    tags: [
      { text: '#产品', class: 'blue' },
      { text: '#设计', class: 'mid' },
    ],
  },
  {
    id: 'n3',
    folderId: 'life',
    title: '六月的三件小事',
    tag: '#生活',
    tagClass: 'low',
    timeAgo: '2 天前',
    words: 520,
    paragraphs: [
      { type: 'p', text: '第一件：重新开始晨跑。六点出门的时候，整个小区还醒着的人不多，只有鸟叫和清洁工的水枪声。' },
      { type: 'p', text: '第二件：给阳台的薄荷换了更大的盆。它长得太快了，像这个夏天一样。' },
      { type: 'p', text: '第三件：和老朋友通了两个小时电话，聊了很多不重要的废话，但挂电话的时候心情很好。' },
    ],
    tags: [
      { text: '#生活', class: 'low' },
      { text: '#随笔', class: 'mid' },
    ],
  },
  {
    id: 'n4',
    folderId: 'notes',
    title: '月度预算复盘',
    tag: '#财务',
    tagClass: 'mid',
    timeAgo: '3 天前',
    words: 940,
    paragraphs: [
      { type: 'p', text: '五月支出 10,920，比预算多了 8%。超支主要来自两笔计划外消费：一副耳机和一次短途旅行。' },
      { type: 'h4', text: '六月调整' },
      { type: 'p', text: '餐饮预算从 2,800 提到 3,000，更贴近实际；购物预算压到 2,000，非必要不买的规则继续执行。储蓄率目标保持在 50% 以上。' },
    ],
    tags: [
      { text: '#财务', class: 'mid' },
      { text: '#复盘', class: 'low' },
    ],
  },
  {
    id: 'n5',
    folderId: 'notes',
    title: '跑步训练计划调整',
    tag: '#健康',
    tagClass: 'low',
    timeAgo: '5 天前',
    words: 610,
    paragraphs: [
      { type: 'p', text: '上半年跑量 268km，距离年度 600km 的目标还有一段距离。下半年需要把月均跑量稳定在 55km 左右。' },
      { type: 'p', text: '计划调整为：周二、周四晨跑 5km，周末一次 10km 长距离。配速不追求，先保证出勤率。' },
    ],
    tags: [
      { text: '#健康', class: 'low' },
      { text: '#跑步', class: 'blue' },
    ],
  },
]

export const mockTagStats: TagStat[] = [
  { name: '读书', count: 42, percent: 100, color: 'green' },
  { name: '产品', count: 36, percent: 86, color: 'blue' },
  { name: '生活', count: 30, percent: 71, color: 'yellow' },
  { name: '灵感', count: 26, percent: 62, color: 'pink' },
  { name: '健康', count: 22, percent: 52, color: 'green' },
  { name: '财务', count: 18, percent: 43, color: 'blue' },
  { name: '旅行', count: 14, percent: 33, color: 'yellow' },
  { name: '影评', count: 11, percent: 26, color: 'pink' },
]
