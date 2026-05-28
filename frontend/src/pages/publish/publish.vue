<template>
  <div class="publish-page">
    <header class="publish-hero">
      <div>
        <p class="eyebrow">Campus Studio</p>
        <h1>今天想创作什么？</h1>
        <p>用一种发布入口承载长文、笔记、直播和短视频。</p>
      </div>
      <button class="ghost-btn" type="button" @click="saveDraft">草稿</button>
    </header>

    <section class="type-grid" aria-label="选择创作形式">
      <button
        v-for="format in formats"
        :key="format.value"
        type="button"
        class="type-card"
        :class="{ active: activeType === format.value }"
        @click="activeType = format.value"
      >
        <span>{{ format.icon }}</span>
        <strong>{{ format.label }}</strong>
        <small>{{ format.desc }}</small>
      </button>
    </section>

    <main class="publish-layout">
      <section class="editor-card shared-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">Shared</p>
            <h2>基础信息</h2>
          </div>
          <span class="status-chip">{{ activeFormat.label }}</span>
        </div>

        <label class="field-block">
          <span>标题</span>
          <input v-model="currentDraft.title" class="text-field" type="text" placeholder="给这次创作起个名字" />
        </label>

        <label class="field-block">
          <span>封面占位</span>
          <input v-model="currentDraft.cover" class="text-field" type="text" placeholder="例如：图书馆夜景 / 直播海报 / 视频封面" />
        </label>

        <label class="field-block">
          <span>话题标签</span>
          <input v-model="topicInput" class="text-field" type="text" placeholder="用空格分隔，例如 摄影 社团招新 夜拍" @blur="syncTopics" @keydown.enter.prevent="syncTopics" />
        </label>

        <div class="field-block">
          <span>可见范围</span>
          <div class="segmented">
            <button v-for="item in visibilityOptions" :key="item.value" type="button" :class="{ active: currentDraft.visibility === item.value }" @click="currentDraft.visibility = item.value">
              {{ item.label }}
            </button>
          </div>
        </div>
      </section>

      <section v-if="activeType === 'article'" class="editor-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">Long Form</p>
            <h2>长文 / 推文编辑</h2>
          </div>
          <button class="mini-btn" type="button" @click="addArticleBlock('paragraph')">加段落</button>
        </div>

        <label class="field-block">
          <span>摘要</span>
          <textarea v-model="article.summary" class="text-area compact" placeholder="一句话概括这篇推文" />
        </label>

        <div class="block-list">
          <article v-for="(block, index) in article.blocks" :key="block.id" class="content-block">
            <div class="block-topline">
              <strong>{{ blockLabel(block.type) }} {{ index + 1 }}</strong>
              <button type="button" class="text-btn" @click="removeArticleBlock(index)">删除</button>
            </div>

            <textarea v-if="block.type === 'paragraph'" v-model="block.text" class="text-area" placeholder="写一段正文、活动说明或创作想法" />

            <template v-else>
              <input v-model="block.label" class="text-field" type="text" placeholder="媒体标题，例如：湖边样张" />
              <div class="media-placeholder" :class="'tone-' + block.tone" :style="{ borderRadius: block.radius + 'px' }">
                {{ block.label || blockLabel(block.type) }} · 圆角 {{ block.radius }}px
              </div>
              <div v-if="block.type === 'image'" class="radius-row" aria-label="图片圆角">
                <button v-for="radius in radiusOptions" :key="radius" type="button" :class="{ active: block.radius === radius }" @click="block.radius = radius">
                  {{ radius }}
                </button>
              </div>
            </template>
          </article>
        </div>

        <div class="tool-row">
          <button type="button" @click="addArticleBlock('paragraph')">段落</button>
          <button type="button" @click="addArticleBlock('image')">图片</button>
          <button type="button" @click="addArticleBlock('gif')">GIF</button>
          <button type="button" @click="addArticleBlock('video')">视频</button>
          <button type="button" @click="addArticleBlock('carousel')">轮播</button>
        </div>
      </section>

      <section v-if="activeType === 'note'" class="editor-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">Notebook</p>
            <h2>笔记目录与分页</h2>
          </div>
          <button class="mini-btn" type="button" @click="addNotePage">新增页</button>
        </div>

        <div class="note-tabs">
          <button v-for="tab in note.tabs" :key="tab.id" type="button" :class="{ active: note.activeTabId === tab.id }" @click="selectNoteTab(tab.id)">
            {{ tab.label }}
          </button>
          <button type="button" class="add-tab" @click="addNoteTab">+ 目录</button>
        </div>

        <label class="field-block">
          <span>当前目录名</span>
          <input v-model="activeNoteTab.label" class="text-field" type="text" />
        </label>

        <div class="page-switcher">
          <button type="button" @click="moveNotePage(-1)">上一页</button>
          <strong>第 {{ note.currentPageIndex + 1 }} / {{ note.pages.length }} 页</strong>
          <button type="button" @click="moveNotePage(1)">下一页</button>
        </div>

        <div class="page-dots">
          <button v-for="(page, index) in note.pages" :key="page.id" type="button" :class="{ active: note.currentPageIndex === index }" @click="selectNotePage(index)">
            {{ index + 1 }}
          </button>
        </div>

        <label class="field-block">
          <span>页面标题</span>
          <input v-model="activeNotePage.title" class="text-field" type="text" placeholder="例如：准备材料" />
        </label>

        <label class="field-block">
          <span>页面内容</span>
          <textarea v-model="activeNotePage.body" class="text-area" placeholder="这一页的笔记内容" />
        </label>
      </section>

      <section v-if="activeType === 'live'" class="editor-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">Live</p>
            <h2>直播预告 / 开播壳</h2>
          </div>
          <span class="status-chip warn">不接真实推流</span>
        </div>

        <label class="field-block">
          <span>直播简介</span>
          <textarea v-model="live.description" class="text-area compact" placeholder="告诉同学直播会讲什么" />
        </label>

        <div class="segmented live-mode">
          <button type="button" :class="{ active: live.mode === 'scheduled' }" @click="live.mode = 'scheduled'">预约直播</button>
          <button type="button" :class="{ active: live.mode === 'instant' }" @click="live.mode = 'instant'">立即开播</button>
        </div>

        <label v-if="live.mode === 'scheduled'" class="field-block">
          <span>开播时间</span>
          <input v-model="live.scheduledAt" class="text-field" type="datetime-local" />
        </label>

        <div class="switch-list">
          <label v-for="item in liveSettings" :key="item.key" class="switch-row">
            <span>{{ item.label }}</span>
            <input v-model="live.settings[item.key]" type="checkbox" />
          </label>
        </div>
      </section>

      <section v-if="activeType === 'short_video'" class="editor-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">Short Video</p>
            <h2>短视频发布</h2>
          </div>
          <span class="status-chip">9:16</span>
        </div>

        <label class="field-block">
          <span>视频占位</span>
          <input v-model="shortVideo.videoLabel" class="text-field" type="text" placeholder="例如：开放麦 15 秒片段" />
        </label>

        <div class="phone-preview">
          <div class="play-symbol">▶</div>
          <strong>{{ shortVideo.videoLabel || '短视频预览' }}</strong>
        </div>

        <label class="field-block">
          <span>视频描述</span>
          <textarea v-model="shortVideo.description" class="text-area compact" placeholder="一句话描述视频亮点" />
        </label>

        <div class="switch-list">
          <label v-for="item in videoSettings" :key="item.key" class="switch-row">
            <span>{{ item.label }}</span>
            <input v-model="shortVideo.settings[item.key]" type="checkbox" />
          </label>
        </div>
      </section>
    </main>

    <section class="preview-card" aria-label="发布预览">
      <div class="section-head">
        <div>
          <p class="eyebrow">Preview</p>
          <h2>{{ currentDraft.title || '未命名创作' }}</h2>
        </div>
        <button class="ghost-btn" type="button" @click="previewOpen = true">放大预览</button>
      </div>

      <p class="preview-text">{{ previewText }}</p>
      <div v-if="activeType === 'article'" class="preview-blocks">
        <template v-for="block in article.blocks" :key="block.id">
          <p v-if="block.type === 'paragraph'">{{ block.text || '正文段落占位' }}</p>
          <div v-else class="media-placeholder small" :class="'tone-' + block.tone" :style="{ borderRadius: block.radius + 'px' }">
            {{ block.label || blockLabel(block.type) }}
          </div>
        </template>
      </div>
      <div v-if="activeType === 'note'" class="note-preview-tabs">
        <span v-for="tab in note.tabs" :key="tab.id">{{ tab.label }}</span>
      </div>
      <div v-if="activeType === 'live'" class="live-preview-line">
        {{ live.mode === 'instant' ? '立即开播准备中' : live.scheduledAt || '待选择开播时间' }}
      </div>
      <div v-if="activeType === 'short_video'" class="video-preview-line">
        {{ shortVideo.videoLabel || '待选择视频' }}
      </div>
    </section>

    <footer class="publish-actions">
      <button class="ghost-btn" type="button" @click="previewOpen = true">预览</button>
      <button class="ghost-btn" type="button" @click="saveDraft">存草稿</button>
      <button class="publish-btn" type="button" @click="handlePublish">发布</button>
    </footer>

    <div v-if="previewOpen" class="sheet-backdrop" role="dialog" aria-modal="true" aria-label="创作预览" @click="previewOpen = false">
      <section class="preview-sheet" @click.stop>
        <div class="sheet-handle"></div>
        <div class="section-head">
          <div>
            <p class="eyebrow">{{ activeFormat.label }}预览</p>
            <h2>{{ currentDraft.title || '未命名创作' }}</h2>
          </div>
          <button class="ghost-btn" type="button" @click="previewOpen = false">关闭</button>
        </div>
        <p class="preview-text">{{ previewText }}</p>
        <div class="topic-list">
          <span v-for="topic in currentDraft.topics" :key="topic">#{{ topic }}</span>
        </div>
      </section>
    </div>

    <div v-if="toastMessage" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '@/store/post'

const router = useRouter()
const postStore = usePostStore()

const formats = [
  { value: 'article', label: '长文/推文', icon: '文', desc: '图文排版与富媒体块' },
  { value: 'note', label: '笔记', icon: '记', desc: '目录标签与分页阅读' },
  { value: 'live', label: '直播', icon: '播', desc: '预约直播或开播配置' },
  { value: 'short_video', label: '短视频', icon: '影', desc: '竖屏视频与封面话题' }
]
const visibilityOptions = [
  { value: 'public', label: '公开' },
  { value: 'campus', label: '本校' },
  { value: 'private', label: '仅自己' }
]
const liveSettings = [
  { key: 'allowChat', label: '允许聊天' },
  { key: 'allowReplay', label: '允许回放' },
  { key: 'notifyFollowers', label: '提醒关注者' }
]
const videoSettings = [
  { key: 'allowComments', label: '允许评论' },
  { key: 'allowDownload', label: '允许下载' }
]
const radiusOptions = [0, 8, 16, 24, 36]
const tones = ['green', 'blue', 'amber', 'violet']

const activeType = ref('article')
const topicInput = ref('摄影 社团 活动')
const previewOpen = ref(false)
const toastMessage = ref('')

const drafts = reactive({
  article: createDraft('article'),
  note: createDraft('note'),
  live: createDraft('live'),
  short_video: createDraft('short_video')
})

const currentDraft = computed(() => drafts[activeType.value])
const activeFormat = computed(() => formats.find(item => item.value === activeType.value))
const article = computed(() => drafts.article.content)
const note = computed(() => drafts.note.content)
const live = computed(() => drafts.live.content)
const shortVideo = computed(() => drafts.short_video.content)
const activeNoteTab = computed(() => note.value.tabs.find(tab => tab.id === note.value.activeTabId) || note.value.tabs[0])
const activeNotePage = computed(() => note.value.pages[note.value.currentPageIndex])

const previewText = computed(() => {
  const draft = currentDraft.value
  if (draft.type === 'article') return draft.content.summary || draft.content.blocks.find(block => block.text)?.text || '长文预览会展示段落、图片圆角和媒体块。'
  if (draft.type === 'note') return activeNotePage.value?.body || '笔记预览会按目录标签和页码组织内容。'
  if (draft.type === 'live') return draft.content.description || '直播预告会展示开播时间和互动设置。'
  return draft.content.description || draft.content.videoLabel || '短视频预览会展示视频占位、封面和话题。'
})

watch(activeType, () => {
  topicInput.value = currentDraft.value.topics.join(' ')
})

function createDraft(type) {
  const base = {
    type,
    title: defaultTitle(type),
    cover: '',
    topics: ['摄影', '社团', '活动'],
    visibility: 'public',
    content: {}
  }
  if (type === 'article') {
    base.content = {
      summary: '适合零基础同学收藏的校园创作推文。',
      blocks: [
        { id: createId(), type: 'paragraph', text: '把活动背景、集合方式和亮点写清楚，让同学一眼知道为什么值得参加。' },
        { id: createId(), type: 'image', label: '封面样张', tone: 'green', radius: 16 }
      ]
    }
  }
  if (type === 'note') {
    base.content = {
      tabs: [
        { id: createId(), label: '准备' },
        { id: createId(), label: '流程' }
      ],
      pages: [],
      currentPageIndex: 0,
      activeTabId: ''
    }
    base.content.activeTabId = base.content.tabs[0].id
    base.content.pages = [
      { id: createId(), tabId: base.content.tabs[0].id, title: '准备材料', body: '列出参加活动前需要准备的物品、时间和集合地点。' },
      { id: createId(), tabId: base.content.tabs[1].id, title: '活动流程', body: '按时间线拆解每一步，适合做成分页阅读。' }
    ]
  }
  if (type === 'live') {
    base.content = {
      mode: 'scheduled',
      description: '直播介绍社团活动亮点，并回答同学报名问题。',
      scheduledAt: '',
      settings: { allowChat: true, allowReplay: true, notifyFollowers: true }
    }
  }
  if (type === 'short_video') {
    base.content = {
      videoLabel: '校园活动 15 秒片段',
      description: '用短视频快速展示现场氛围。',
      settings: { allowComments: true, allowDownload: false }
    }
  }
  return base
}

function defaultTitle(type) {
  const map = {
    article: '周五夜拍路线开放报名',
    note: '校园夜拍入门笔记',
    live: '摄影协会夜拍公开课直播',
    short_video: '开放麦现场高光片段'
  }
  return map[type]
}

function createId() {
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

function syncTopics() {
  currentDraft.value.topics = topicInput.value.split(/\s+/).map(item => item.trim()).filter(Boolean)
}

function addArticleBlock(type) {
  const block = { id: createId(), type }
  if (type === 'paragraph') block.text = ''
  else {
    block.label = ''
    block.tone = tones[article.value.blocks.length % tones.length]
    block.radius = 16
  }
  article.value.blocks.push(block)
}

function removeArticleBlock(index) {
  if (article.value.blocks.length === 1) {
    showToast('至少保留一个内容块')
    return
  }
  article.value.blocks.splice(index, 1)
}

function blockLabel(type) {
  const map = { paragraph: '段落', image: '图片', gif: 'GIF', video: '视频', carousel: '轮播' }
  return map[type] || '内容块'
}

function addNoteTab() {
  const tab = { id: createId(), label: `目录 ${note.value.tabs.length + 1}` }
  note.value.tabs.push(tab)
  note.value.activeTabId = tab.id
  addNotePage()
}

function selectNoteTab(tabId) {
  note.value.activeTabId = tabId
  const pageIndex = note.value.pages.findIndex(page => page.tabId === tabId)
  if (pageIndex >= 0) note.value.currentPageIndex = pageIndex
}

function addNotePage() {
  note.value.pages.push({
    id: createId(),
    tabId: note.value.activeTabId,
    title: `第 ${note.value.pages.length + 1} 页`,
    body: ''
  })
  note.value.currentPageIndex = note.value.pages.length - 1
}

function selectNotePage(index) {
  note.value.currentPageIndex = index
  note.value.activeTabId = activeNotePage.value.tabId
}

function moveNotePage(offset) {
  const next = Math.min(Math.max(note.value.currentPageIndex + offset, 0), note.value.pages.length - 1)
  selectNotePage(next)
}

function saveDraft() {
  syncTopics()
  showToast('草稿已保存在当前会话')
}

function handlePublish() {
  syncTopics()
  const error = validateDraft(currentDraft.value)
  if (error) {
    showToast(error)
    return
  }
  postStore.addPost(JSON.parse(JSON.stringify(currentDraft.value)))
  showToast('发布成功')
  setTimeout(() => router.push('/feed'), 500)
}

function validateDraft(draft) {
  if (!draft.title.trim()) return '请填写标题'
  if (draft.type === 'article') {
    const hasContent = draft.content.blocks.some(block => block.type === 'paragraph' ? block.text?.trim() : block.label?.trim())
    if (!hasContent) return '请至少填写一个长文内容块'
  }
  if (draft.type === 'note') {
    const hasPage = draft.content.pages.some(page => page.title?.trim() && page.body?.trim())
    if (!hasPage) return '请至少完善一页笔记'
  }
  if (draft.type === 'live' && draft.content.mode === 'scheduled' && !draft.content.scheduledAt) return '请选择开播时间或切换立即开播'
  if (draft.type === 'short_video' && !draft.content.videoLabel.trim()) return '请填写短视频占位名称'
  return ''
}

function showToast(message) {
  toastMessage.value = message
  window.clearTimeout(showToast.timer)
  showToast.timer = window.setTimeout(() => {
    toastMessage.value = ''
  }, 1800)
}
</script>

<style lang="scss" scoped>
.publish-page {
  min-height: 100vh;
  padding: 18px 16px 96px;
  color: #e8edf1;
}

.publish-hero,
.editor-card,
.preview-card,
.preview-sheet {
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  background: linear-gradient(145deg, rgba(22, 24, 30, 0.86), rgba(12, 14, 19, 0.78));
  box-shadow: 0 22px 60px rgba(0, 0, 0, 0.26);
}

.publish-hero {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 20px;
  margin-bottom: 16px;
}

.publish-hero h1,
.section-head h2 {
  margin: 4px 0 6px;
  font-size: 24px;
  letter-spacing: -0.04em;
}

.publish-hero p,
.preview-text,
.field-block span,
.type-card small {
  margin: 0;
  color: #9ca8b4;
  line-height: 1.6;
}

.eyebrow {
  margin: 0;
  color: #5cc9b7;
  font-size: 11px;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.14em;
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 14px;
}

.type-card {
  min-height: 112px;
  padding: 14px;
  text-align: left;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  background: rgba(22, 24, 30, 0.72);
  color: #e8edf1;
  cursor: pointer;
}

.type-card span {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  margin-bottom: 10px;
  border-radius: 12px;
  background: rgba(92, 201, 183, 0.16);
  color: #8be4d4;
  font-weight: 900;
}

.type-card strong,
.type-card small {
  display: block;
}

.type-card.active {
  border-color: rgba(92, 201, 183, 0.72);
  background: linear-gradient(145deg, rgba(92, 201, 183, 0.22), rgba(22, 24, 30, 0.82));
}

.publish-layout {
  display: grid;
  gap: 14px;
}

.editor-card,
.preview-card {
  padding: 16px;
}

.section-head,
.block-topline,
.page-switcher,
.publish-actions,
.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.field-block {
  display: grid;
  gap: 8px;
  margin-top: 14px;
}

.text-field,
.text-area {
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  background: rgba(8, 10, 14, 0.68);
  color: #e8edf1;
  font: inherit;
  outline: none;
}

.text-field {
  height: 46px;
  padding: 0 14px;
}

.text-area {
  min-height: 116px;
  padding: 13px 14px;
  resize: vertical;
}

.text-area.compact {
  min-height: 82px;
}

.segmented,
.tool-row,
.radius-row,
.note-tabs,
.page-dots,
.topic-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.segmented button,
.tool-row button,
.radius-row button,
.note-tabs button,
.page-dots button,
.ghost-btn,
.mini-btn,
.text-btn {
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.05);
  color: #dce5eb;
  cursor: pointer;
}

.segmented button,
.tool-row button,
.radius-row button,
.note-tabs button,
.page-dots button {
  padding: 9px 12px;
}

.segmented button.active,
.radius-row button.active,
.note-tabs button.active,
.page-dots button.active {
  background: rgba(92, 201, 183, 0.18);
  border-color: rgba(92, 201, 183, 0.55);
  color: #8be4d4;
}

.ghost-btn,
.mini-btn {
  padding: 9px 13px;
  white-space: nowrap;
}

.text-btn {
  padding: 6px 10px;
  color: #ffb4a8;
}

.status-chip {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(92, 201, 183, 0.16);
  color: #8be4d4;
  font-size: 12px;
  font-weight: 800;
}

.status-chip.warn {
  background: rgba(255, 189, 89, 0.16);
  color: #ffd28a;
}

.block-list {
  display: grid;
  gap: 12px;
  margin: 14px 0;
}

.content-block {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
}

.media-placeholder,
.phone-preview {
  display: grid;
  place-items: center;
  min-height: 132px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #f1f7f7;
  font-weight: 850;
}

.media-placeholder.small {
  min-height: 82px;
  margin-top: 10px;
}

.tone-green { background: linear-gradient(135deg, rgba(92, 201, 183, 0.32), rgba(15, 82, 71, 0.62)); }
.tone-blue { background: linear-gradient(135deg, rgba(87, 145, 255, 0.32), rgba(25, 45, 91, 0.62)); }
.tone-amber { background: linear-gradient(135deg, rgba(255, 190, 92, 0.32), rgba(99, 68, 24, 0.62)); }
.tone-violet { background: linear-gradient(135deg, rgba(173, 112, 255, 0.32), rgba(60, 35, 104, 0.62)); }

.tool-row {
  margin-top: 12px;
}

.note-tabs {
  margin: 14px 0;
}

.add-tab {
  border-style: dashed !important;
}

.page-switcher {
  margin: 14px 0 10px;
  padding: 10px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
}

.page-switcher button {
  border: 0;
  background: transparent;
  color: #8be4d4;
  cursor: pointer;
}

.switch-list {
  display: grid;
  gap: 10px;
  margin-top: 14px;
}

.switch-row {
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
}

.live-mode {
  margin-top: 14px;
}

.phone-preview {
  width: min(220px, 72vw);
  min-height: 320px;
  margin: 14px auto;
  border-radius: 30px;
  background: radial-gradient(circle at 50% 20%, rgba(92, 201, 183, 0.4), rgba(13, 16, 23, 0.96));
}

.play-symbol {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
}

.preview-card {
  margin-top: 14px;
}

.note-preview-tabs span,
.topic-list span,
.live-preview-line,
.video-preview-line {
  display: inline-flex;
  margin-top: 10px;
  padding: 7px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #cfd8df;
}

.publish-actions {
  position: fixed;
  left: 12px;
  right: 12px;
  bottom: 12px;
  z-index: 10;
  padding: 10px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 22px;
  background: rgba(12, 14, 19, 0.86);
  backdrop-filter: blur(20px);
}

.publish-btn {
  flex: 1;
  height: 44px;
  border: 0;
  border-radius: 999px;
  background: linear-gradient(135deg, #5cc9b7, #4a9e8f);
  color: #07110f;
  font-weight: 900;
  cursor: pointer;
}

.sheet-backdrop {
  position: fixed;
  inset: 0;
  z-index: 20;
  display: flex;
  align-items: flex-end;
  padding: 16px;
  background: rgba(0, 0, 0, 0.58);
}

.preview-sheet {
  width: 100%;
  max-height: 70vh;
  padding: 18px;
  overflow: auto;
}

.sheet-handle {
  width: 44px;
  height: 4px;
  margin: 0 auto 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
}

.toast {
  position: fixed;
  left: 50%;
  bottom: 84px;
  z-index: 30;
  transform: translateX(-50%);
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(18, 22, 28, 0.94);
  color: #f4fbfb;
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.32);
}

@media (min-width: 780px) {
  .publish-page {
    max-width: 980px;
    margin: 0 auto;
  }

  .type-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .publish-layout {
    grid-template-columns: 0.9fr 1.1fr;
    align-items: start;
  }

  .shared-card {
    position: sticky;
    top: 16px;
  }
}
</style>
