<template>
  <div class="detail-page-wrapper">
    <header class="detail-actionbar" aria-label="详情页操作栏">
      <button class="detail-back" @click="goBack" aria-label="返回"></button>
      <div class="detail-club-title">{{ currentPost.org }}</div>
      <button class="detail-more" @click="settingsOpen = true" aria-label="页面设置">
        <span></span><span></span><span></span>
      </button>
    </header>

    <main class="page stack detail-page" :class="'reader-' + readerFontClass + ' theme-' + readerThemeClass">
      <article class="article-detail">
        <section class="article-body" aria-label="帖子正文详情">
          <div class="between article-meta-head">
            <div class="row">
              <span class="avatar">{{ currentPost.avatar }}</span>
              <div>
                <div class="author-line">
                  <strong>{{ currentPost.club }}</strong>
                </div>
                <p class="meta">{{ currentPost.time }} · 已发布为长文详情</p>
              </div>
            </div>
            <span class="tag">{{ currentPost.tag }}</span>
          </div>

          <h1>{{ currentPost.title }}</h1>

          <template v-for="(block, index) in richBlocks" :key="index">
            <p v-if="block.type === 'paragraph'" class="article-paragraph" :style="paragraphStyle(block)">
              {{ block.text }}
            </p>

            <figure v-if="block.type === 'image' || block.type === 'gif'" :class="['article-figure', 'tone-' + (block.tone || 'green')]">
              <div class="article-media-surface" :class="block.type === 'gif' ? 'is-gif' : 'is-image'"></div>
            </figure>

            <figure v-if="block.type === 'video'" :class="['article-video', 'tone-' + (block.tone || 'blue')]">
              <div class="video-stage">
                <button class="video-play" aria-label="播放视频">▶</button>
              </div>
            </figure>

            <section v-if="block.type === 'carousel'" class="article-carousel">
              <div class="carousel-track" :style="{ transform: 'translateX(-' + carouselIndex * 100 + '%)' }">
                <article v-for="item in block.items" :key="item.label" :class="['carousel-slide', 'tone-' + (item.tone || 'green'), 'media-' + item.type]">
                </article>
              </div>
              <div class="carousel-dots" role="tablist">
                <button v-for="(item, itemIndex) in block.items" :key="item.label" :class="{ active: carouselIndex === itemIndex }" @click="carouselIndex = itemIndex"></button>
              </div>
            </section>
          </template>
        </section>
      </article>

      <section class="card stack comments-panel" id="comments-section">
        <div class="between">
          <h2>评论</h2>
          <span class="meta">{{ floorComments.length }} 个楼层</span>
        </div>

        <div class="comment-floor-list">
          <article v-for="item in floorComments" :key="item.id" :class="['comment-floor', { expanded: expandedFloor === item.id }]">
            <div class="floor-main">
              <span class="avatar">{{ item.avatar }}</span>
              <div class="floor-content">
                <div class="floor-head">
                  <div>
                    <strong>{{ item.author }}</strong>
                    <span v-if="item.author === currentPost.club" class="owner-badge">叶主</span>
                  </div>
                  <span>{{ item.floor }}</span>
                </div>
                <button type="button" class="floor-text" @click="replyToFloor(item)">{{ item.text }}</button>
                <div class="floor-meta">
                  <span>{{ item.time }}</span>
                  <button v-if="item.replies.length > 0" type="button" @click="expandedFloor = expandedFloor === item.id ? '' : item.id">
                    {{ expandedFloor === item.id ? '收起回复' : item.replies.length + ' 条回复' }}
                  </button>
                </div>
              </div>
              <button class="action action-like comment-like-btn" :class="{ active: isCommentLiked(item.id) }" @click="toggleCommentLike(item)">
                <svg class="action-svg" viewBox="0 0 24 24"><path d="M7.5 10.5v9H5.2A2.2 2.2 0 0 1 3 17.3v-4.6a2.2 2.2 0 0 1 2.2-2.2h2.3Zm0 0 4.7-6.3c.6-.8 1.9-.4 1.9.6v3.5h3.5c1.7 0 2.9 1.5 2.6 3.1l-1 5.4a3.2 3.2 0 0 1-3.1 2.6H7.5" /></svg>
                <strong v-if="isCommentLiked(item.id)">{{ item.likes }}</strong>
              </button>
            </div>

            <div v-if="expandedFloor === item.id" class="floor-thread">
              <template v-if="item.replies.length">
                <div v-for="(reply, replyIndex) in item.replies" :key="replyIndex" class="thread-reply">
                  <span class="avatar">{{ reply.avatar }}</span>
                  <div class="thread-content-wrapper">
                    <div>
                      <div class="thread-author">
                        <strong>{{ reply.author }}</strong>
                        <span v-if="reply.owner" class="owner-badge">叶主</span>
                        <span>{{ reply.time }}</span>
                      </div>
                      <button type="button" class="thread-text-btn" @click="replyToReply(item, reply)">{{ reply.text }}</button>
                    </div>
                    <div class="thread-actions">
                      <button type="button" class="thread-action-btn thread-like-btn" :class="{ active: isReplyLiked(item.id + '-' + replyIndex) }" @click.stop="toggleReplyLike(item.id, item.id + '-' + replyIndex, reply)">
                        <svg viewBox="0 0 24 24"><path d="M7.5 10.5v9H5.2A2.2 2.2 0 0 1 3 17.3v-4.6a2.2 2.2 0 0 1 2.2-2.2h2.3Zm0 0 4.7-6.3c.6-.8 1.9-.4 1.9.6v3.5h3.5c1.7 0 2.9 1.5 2.6 3.1l-1 5.4a3.2 3.2 0 0 1-3.1 2.6H7.5" /></svg>
                        <span v-if="reply.likes > 0">{{ reply.likes }}</span>
                      </button>
                    </div>
                  </div>
                </div>
              </template>
              <p v-else class="thread-empty">暂无回复，点击回复参与讨论。</p>
            </div>
          </article>
        </div>
      </section>
    </main>

    <section ref="composerRef" :class="['detail-composer', { expanded: composerOpen }]" aria-label="发表评论">
      <template v-if="!composerOpen">
        <button class="comment-entry" @click="openComposer()">写下你的想法...</button>
        <button class="action action-like" :class="{ active: liked }" @click="toggleLike">
          <svg class="action-svg" viewBox="0 0 24 24"><path d="M7.5 10.5v9H5.2A2.2 2.2 0 0 1 3 17.3v-4.6a2.2 2.2 0 0 1 2.2-2.2h2.3Zm0 0 4.7-6.3c.6-.8 1.9-.4 1.9.6v3.5h3.5c1.7 0 2.9 1.5 2.6 3.1l-1 5.4a3.2 3.2 0 0 1-3.1 2.6H7.5" /></svg>
        </button>
        <button class="action action-save" :class="{ active: saved }" @click="toggleSave">
          <svg class="action-svg" viewBox="0 0 24 24"><path d="m12 3.6 2.5 5.1 5.6.8-4 4 1 5.5-5-2.7L7 19l1-5.5-4-4 5.6-.8L12 3.6Z" /></svg>
        </button>
        <button class="action action-share" @click="shareOpen = true">
          <svg class="action-svg" viewBox="0 0 24 24"><path d="M8.2 12.6 18.8 6M8.2 11.4l10.6 6.6M7 15.3a3.3 3.3 0 1 0 0-6.6 3.3 3.3 0 0 0 0 6.6Zm12.1-7.2a2.9 2.9 0 1 0 0-5.8 2.9 2.9 0 0 0 0 5.8Zm0 13.6a2.9 2.9 0 1 0 0-5.8 2.9 2.9 0 0 0 0 5.8Z" /></svg>
        </button>
      </template>
      <template v-else>
        <textarea ref="composerTextarea" class="composer-textarea" v-model="comment" :placeholder="replyTarget ? '回复 ' + replyTarget.author : '写下你的评论...'" aria-label="评论内容"></textarea>
        <div class="composer-row">
          <div class="composer-tools">
            <button class="tool-btn">图片</button>
            <button class="tool-btn">音频</button>
            <button class="tool-btn">文件</button>
          </div>
          <button class="primary-btn" @click="submitComment">发送</button>
        </div>
      </template>
    </section>

    <div v-if="settingsOpen" class="sheet-backdrop detail-settings-backdrop" @click="settingsOpen = false">
      <section class="detail-settings-sheet" @click.stop>
        <div class="sheet-handle"></div>
        <div class="settings-head">
          <strong>页面设置</strong>
          <button class="ghost-btn" @click="settingsOpen = false">完成</button>
        </div>
        <div class="setting-group">
          <span>字体大小</span>
          <div class="segmented">
            <button v-for="size in ['小', '标准', '大']" :key="size" :class="{ active: readerFont === size }" @click="readerFont = size">{{ size }}</button>
          </div>
        </div>
        <div class="setting-group">
          <span>阅读模式</span>
          <div class="segmented">
            <button v-for="mode in ['日间', '深色']" :key="mode" :class="{ active: readerTheme === mode }" @click="readerTheme = mode">{{ mode }}</button>
          </div>
        </div>
        <button class="report-btn" @click="doReport">举报</button>
      </section>
    </div>

    <div v-if="shareOpen" class="sheet-backdrop" @click="shareOpen = false">
      <section class="share-sheet" @click.stop>
        <div class="sheet-handle"></div>
        <div class="between">
          <h3>转发给</h3>
          <button class="ghost-btn" @click="shareOpen = false">取消</button>
        </div>
        <div class="share-grid">
          <button v-for="channel in shareChannels" :key="channel[0]" class="share-channel" @click="shareOpen = false">
            <span>{{ channel[2] }}</span>
            <strong>{{ channel[1] }}</strong>
          </button>
        </div>
      </section>
    </div>

    <div v-if="toastMessage" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '@/store/post'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()

const postId = parseInt(route.params.id)
const currentPost = computed(() => postStore.getPost(postId) || {
  id: 1,
  club: '林予安',
  org: '摄影协会',
  avatar: '林',
  tag: '报名中',
  time: '2 小时前',
  title: '周五夜拍路线开放报名，图书馆南门集合',
  likes: 128,
  comments: 34,
  saves: 52,
  shares: 12
})

const liked = computed({
  get: () => currentPost.value.liked || false,
  set: (val) => {
    if (currentPost.value) {
      currentPost.value.liked = val
    }
  }
})
const saved = ref(false)
const composerOpen = ref(false)
const replyTarget = ref(null)
const comment = ref('')
const settingsOpen = ref(false)
const shareOpen = ref(false)
const toastMessage = ref('')
const expandedFloor = ref('')
const carouselIndex = ref(0)
const readerFont = ref('标准')
const readerTheme = ref('深色')
const likedCommentIds = ref(new Set())
const likedReplyIds = ref(new Set())

function isCommentLiked(commentId) {
  return likedCommentIds.value.has(commentId)
}

function isReplyLiked(replyId) {
  return likedReplyIds.value.has(replyId)
}

function toggleCommentLikeState(commentId) {
  const newSet = new Set(likedCommentIds.value)
  if (newSet.has(commentId)) {
    newSet.delete(commentId)
  } else {
    newSet.add(commentId)
  }
  likedCommentIds.value = newSet
}

function toggleReplyLikeState(replyId) {
  const newSet = new Set(likedReplyIds.value)
  if (newSet.has(replyId)) {
    newSet.delete(replyId)
  } else {
    newSet.add(replyId)
  }
  likedReplyIds.value = newSet
}

const composerRef = ref(null)
const composerTextarea = ref(null)

const richBlocks = [
  { type: 'paragraph', text: '这次夜拍不是简单集合打卡，而是把路线拆成 3 个练习点：图书馆南门的灯带、湖边步道的反光、旧教学楼的窗格。每个点都会给出一组手机参数和构图示例。', size: 17, color: '#eef4f8', weight: 500 },
  { type: 'image', label: '图书馆南门构图样张', tone: 'blue' },
  { type: 'paragraph', text: '拍第一张样张时，先把人物放在画面右下角，让建筑灯带成为斜向引导线。这样即使手机传感器较小，画面也不会只剩一片高光。', size: 16, color: '#d8e6ee', weight: 500 },
  { type: 'gif', label: '长曝光前后对比', tone: 'green' },
  { type: 'paragraph', text: '重点提醒：零基础同学建议先打开网格线，曝光补偿降低 0.3–0.7，避免高光溢出；如果画面太暗，再优先提高快门时间。', size: 15, color: '#6fbf9c', weight: 800 },
  { type: 'video', label: '30 秒讲解：如何锁定曝光', tone: 'violet' },
  { type: 'paragraph', text: '如果需要连续展示多个站位，可以把图片、GIF、视频组成横向翻页媒体组，读者通过下方圆点知道当前看到第几页。', size: 16, color: '#d8e6ee', weight: 500 },
  { type: 'carousel', items: [
    { type: 'image', label: '站位 1', tone: 'amber' },
    { type: 'gif', label: '站位 2', tone: 'green' },
    { type: 'video', label: '站位 3', tone: 'blue' }
  ] },
  { type: 'paragraph', text: '报名同学在评论区回复"夜拍 + 设备"，作者会按手机组、相机组分别私信集合提醒。', size: 16, color: '#d8e6ee', weight: 500 }
]

const floorComments = ref([
  {
    id: 'floor-1',
    floor: '1楼',
    author: '林同学',
    avatar: '林',
    time: '18 分钟前',
    text: '可以只带手机参加吗？想学一下夜景人像，社团会不会安排路线？',
    likes: 8,
    replies: [
      { author: '林予安', avatar: '林', owner: true, time: '刚刚', text: '可以，现场会安排手机拍摄小组，也会给一条适合新手的低光路线。' },
      { author: '周嘉禾', avatar: '周', time: '2 分钟前', text: '我也想跟手机组，能不能顺便讲一下参数设置？' }
    ]
  },
  {
    id: 'floor-2',
    floor: '2楼',
    author: '陈一诺',
    avatar: '陈',
    time: '12 分钟前',
    text: '报名需要提前交作品吗？还是现场直接参加就可以？',
    likes: 5,
    replies: [
      { author: '林予安', avatar: '林', owner: true, time: '8 分钟前', text: '不用交作品，直接在帖子里点收藏，活动前一天会私信确认集合点。' }
    ]
  },
  {
    id: 'floor-3',
    floor: '3楼',
    author: '许知夏',
    avatar: '许',
    time: '6 分钟前',
    text: '如果下雨会改到室内吗？',
    likes: 3,
    replies: []
  }
])

const shareChannels = [
  ['friend', '好友', '校'],
  ['wechat', '微信好友', '微'],
  ['moments', '微信朋友圈', '圈'],
  ['qq', 'QQ 好友', 'Q'],
  ['qzone', 'QQ 空间', '空']
]

const readerFontClass = computed(() => {
  if (readerFont.value === '大') return 'large'
  if (readerFont.value === '小') return 'small'
  return 'standard'
})

const readerThemeClass = computed(() => {
  return readerTheme.value === '日间' ? 'light' : 'dark'
})

function paragraphStyle(block) {
  return {
    fontSize: block.size + 'px',
    color: block.color,
    fontWeight: block.weight || 400,
    textAlign: block.align || 'left'
  }
}

function goBack() {
  router.push('/feed')
}

function toggleLike() {
  postStore.toggleLike(postId)
}

function toggleSave() {
  saved.value = !saved.value
  currentPost.value.saves += saved.value ? 1 : -1
}

function openComposer() {
  replyTarget.value = null
  comment.value = ''
  composerOpen.value = true
  nextTick(() => {
    if (composerTextarea.value) {
      composerTextarea.value.focus()
    }
  })
}

function replyToFloor(item) {
  replyTarget.value = item
  comment.value = '回复 ' + item.author + '：'
  composerOpen.value = true
  nextTick(() => {
    if (composerTextarea.value) {
      composerTextarea.value.focus()
    }
  })
}

function submitComment() {
  if (!comment.value.trim()) {
    toastMessage.value = '请输入评论内容'
    return
  }
  toastMessage.value = replyTarget.value ? '已回复 ' + replyTarget.value.author : '评论已发布'
  comment.value = ''
  replyTarget.value = null
  composerOpen.value = false
  setTimeout(() => { toastMessage.value = '' }, 2000)
}

function doReport() {
  toastMessage.value = '已进入举报流程'
  settingsOpen.value = false
  setTimeout(() => { toastMessage.value = '' }, 2000)
}

function toggleCommentLike(item) {
  const isLiked = isCommentLiked(item.id)
  if (isLiked) {
    item.likes--
    likedCommentIds.value.delete(item.id)
  } else {
    item.likes++
    likedCommentIds.value.add(item.id)
  }
  likedCommentIds.value = new Set(likedCommentIds.value)
}

function toggleReplyLike(floorId, replyId, reply) {
  const isLiked = isReplyLiked(replyId)
  if (isLiked) {
    reply.likes = (reply.likes || 0) - 1
    likedReplyIds.value.delete(replyId)
  } else {
    reply.likes = (reply.likes || 0) + 1
    likedReplyIds.value.add(replyId)
  }
  likedReplyIds.value = new Set(likedReplyIds.value)
}

function replyToReply(floorItem, reply) {
  replyTarget.value = { ...reply, parentFloor: floorItem }
  comment.value = '回复 ' + reply.author + '：'
  composerOpen.value = true
  nextTick(() => {
    if (composerTextarea.value) {
      composerTextarea.value.focus()
    }
  })
}

onMounted(() => {
  function closeComposerOnOutside(event) {
    if (!composerOpen.value) return
    if (!composerRef.value || composerRef.value.contains(event.target)) return
    composerOpen.value = false
    replyTarget.value = null
    comment.value = ''
  }
  document.addEventListener('pointerdown', closeComposerOnOutside)

  const focus = route.query.focus
  if (focus === 'comments') {
    nextTick(() => {
      const commentsSection = document.getElementById('comments-section')
      if (commentsSection) {
        commentsSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    })
  }
})
</script>

<style lang="scss">
.detail-page-wrapper {
  width: 100%;
  height: 100vh;
  background: #0c0e13;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.detail-actionbar {
  position: sticky;
  top: 0;
  z-index: 12;
  height: 42px;
  display: grid;
  grid-template-columns: 40px 1fr 40px;
  align-items: center;
  justify-items: center;
  gap: 4px;
  padding: 0 10px;
  background: rgba(12, 14, 19, 0.80);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(22px) saturate(145%);
  flex-shrink: 0;
}

.page {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  padding: 16px 16px 24px;
  padding-bottom: 80px;
  scrollbar-width: none;
}

.page::-webkit-scrollbar {
  display: none;
}

.detail-composer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 12;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto auto;
  align-items: center;
  gap: 6px;
  padding: 10px 12px calc(12px + env(safe-area-inset-bottom));
  background: rgba(22, 24, 30, 0.82);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 -18px 40px rgba(0, 0, 0, 0.40), inset 0 1px 0 rgba(255,255,255,0.06);
  backdrop-filter: blur(22px) saturate(1.25);
}

.detail-back, .detail-more {
  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 50%;
  background: transparent;
  color: #e4e6ea;
  display: grid;
  place-items: center;
}

.detail-back::before {
  content: '';
  width: 11px;
  height: 11px;
  border-left: 2px solid rgba(228, 230, 234, 0.78);
  border-bottom: 2px solid rgba(228, 230, 234, 0.78);
  transform: rotate(45deg) translate(2px, -1px);
}

.detail-club-title {
  justify-self: center;
  max-width: 190px;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  color: #5cc9b7;
  font-size: 13px;
  font-weight: 900;
  line-height: 1;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.detail-more {
  display: flex;
  gap: 3px;
}

.detail-more span {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: rgba(228, 230, 234, 0.48);
}

.stack {
  display: grid;
  gap: 14px;
}

.row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.between {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.meta {
  color: #8b8f98;
  font-size: 12px;
  font-weight: 650;
}

.author-line {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  min-width: 0;
}

.author-line strong {
  color: #e4e6ea;
  font-size: 14px;
  line-height: 1;
}

.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(28, 30, 38, 0.65);
  border: 1px solid rgba(255, 255, 255, 0.07);
  color: #e4e6ea;
  font-weight: 800;
  flex: 0 0 auto;
}

.tag {
  padding: 6px 9px;
  border-radius: 999px;
  color: #5cc9b7;
  background: rgba(92, 201, 183, 0.10);
  font-size: 12px;
  font-weight: 700;
}

.article-detail {
  display: grid;
  gap: 14px;
  margin: 4px 0 12px;
}

.article-body {
  display: grid;
  gap: 18px;
  padding: 22px 18px 24px;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 24px;
  background: rgba(22, 24, 30, 0.62);
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.32);
  backdrop-filter: blur(14px);
}

.article-meta-head {
  gap: 12px;
  align-items: flex-start;
}

.article-body h1 {
  font-size: 28px;
  line-height: 1.18;
  letter-spacing: -0.045em;
  color: #e4e6ea;
  margin: 0;
}

.article-paragraph {
  margin: 0;
  line-height: 1.88;
  letter-spacing: 0.01em;
}

.article-figure, .article-video, .article-carousel {
  margin: 0;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 22px;
  overflow: hidden;
  background: rgba(28, 30, 38, 0.65);
}

.article-media-surface, .video-stage, .carousel-slide {
  min-height: 210px;
  position: relative;
  display: grid;
  align-content: end;
  gap: 8px;
  padding: 16px;
  overflow: hidden;
}

.article-media-surface::before, .video-stage::before, .carousel-slide::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 22% 18%, rgba(255,255,255,0.06), transparent 28%), linear-gradient(145deg, rgba(255,255,255,0.04), rgba(0,0,0,0.30));
}

.article-media-surface > *, .video-stage > *, .carousel-slide > * {
  position: relative;
  z-index: 1;
}

.tone-green { background: linear-gradient(135deg, #1a3a2e, #0c2218); color: #b8e6d4; }
.tone-blue { background: linear-gradient(135deg, #1a2e3a, #0c1a24); color: #b8d6e6; }
.tone-amber { background: linear-gradient(135deg, #3a2e1a, #241c0c); color: #e6d4b8; }
.tone-violet { background: linear-gradient(135deg, #2e1a3a, #1c0c24); color: #d4b8e6; }

.video-stage {
  place-items: center;
  align-content: center;
  min-height: 230px;
}

.video-play {
  width: 48px;
  height: 48px;
  border: 1px solid rgba(92, 201, 183, 0.30);
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(92, 201, 183, 0.14);
  backdrop-filter: blur(14px);
  color: rgba(228, 230, 234, 0.92);
  font-size: 18px;
  box-shadow: 0 0 24px rgba(92, 201, 183, 0.18), inset 0 1px 0 rgba(255,255,255,0.10);
  transition: background 0.22s ease, box-shadow 0.22s ease;
}

.article-carousel {
  position: relative;
}

.carousel-track {
  display: flex;
  transition: transform 0.28s ease;
}

.carousel-slide {
  min-width: 100%;
}

.carousel-dots {
  position: absolute;
  left: 50%;
  bottom: 14px;
  transform: translateX(-50%);
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 0;
  background: transparent;
  z-index: 2;
}

.carousel-dots button {
  width: 8px;
  height: 8px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 999px;
  padding: 0;
  background: rgba(12, 14, 19, 0.50);
  backdrop-filter: blur(10px) saturate(160%);
  box-shadow: inset 0 1px 1px rgba(255,255,255,0.08), 0 4px 14px rgba(0,0,0,0.30);
  transition: width 0.24s ease, background 0.24s ease, border-color 0.24s ease, box-shadow 0.24s ease;
}

.carousel-dots button.active {
  width: 18px;
  background: linear-gradient(135deg, rgba(92,201,183,0.68), rgba(61,170,150,0.36));
  border-color: rgba(92, 201, 183, 0.32);
  box-shadow: inset 0 1px 1px rgba(255,255,255,0.10), inset 0 -1px 4px rgba(0,0,0,0.20), 0 0 18px rgba(92,201,183,0.16), 0 4px 14px rgba(0,0,0,0.30);
}

.card {
  border: 1px solid rgba(255, 255, 255, 0.07);
  background: rgba(22, 24, 30, 0.62);
  border-radius: 24px;
  padding: 16px;
  margin: 12px 0;
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.32);
  backdrop-filter: blur(14px);
}

.comments-panel {
  gap: 12px;
}

.comments-panel h2 {
  font-size: 22px;
  letter-spacing: -0.03em;
  color: #e4e6ea;
  margin: 0;
}

.comment-floor-list {
  display: grid;
  gap: 10px;
}

.comment-floor {
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 18px;
  background: rgba(22, 24, 30, 0.55);
  overflow: hidden;
}

.comment-floor.expanded {
  border-color: rgba(92, 201, 183, 0.22);
  background: rgba(92, 201, 183, 0.05);
}

.floor-main {
  width: 100%;
  display: grid;
  grid-template-columns: 34px minmax(0, 1fr) auto;
  align-items: start;
  gap: 10px;
  padding: 12px;
  background: transparent;
  color: #e4e6ea;
  text-align: left;
}

.floor-content {
  min-width: 0;
  display: grid;
  gap: 6px;
}

.floor-head {
  display: flex;
  align-items: center;
  gap: 7px;
  min-width: 0;
  justify-content: space-between;
}

.floor-head strong {
  font-size: 13px;
  color: #e4e6ea;
}

.floor-head > span {
  flex: 0 0 auto;
  color: #5f636e;
  font-size: 11px;
  font-weight: 800;
}

.owner-badge {
  display: inline-flex;
  align-items: center;
  height: 17px;
  padding: 0 6px;
  border-radius: 999px;
  background: rgba(92, 201, 183, 0.14);
  color: #5cc9b7;
  font-size: 10px;
  font-weight: 900;
}

.floor-text {
  margin: 0;
  padding: 0;
  border: 0;
  background: transparent;
  color: rgba(228, 230, 234, 0.82);
  font: inherit;
  font-size: 14px;
  line-height: 1.55;
  text-align: left;
  cursor: pointer;
}

.floor-meta {
  display: flex;
  align-items: center;
  gap: 7px;
  min-width: 0;
  color: #5f636e;
  font-size: 11px;
  font-weight: 700;
}

.floor-meta button {
  border: 0;
  padding: 0;
  background: transparent;
  color: #5cc9b7;
  font: inherit;
  font-weight: 900;
  cursor: pointer;
}

.floor-meta span + button::before,
.floor-meta button + button::before {
  content: '·';
  margin-right: 7px;
  color: rgba(255, 255, 255, 0.14);
}

.action {
  border: 0;
  min-height: 40px;
  min-width: 40px;
  padding: 0 7px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: transparent;
  color: #8b8f98;
  line-height: 1;
  transition: color 0.16s ease, transform 0.16s ease, background 0.16s ease;
}

.action:active {
  transform: scale(0.92);
}

.action-svg {
  width: 22px;
  height: 22px;
  display: block;
  fill: transparent;
  stroke: currentColor;
  stroke-width: 1.9;
  stroke-linecap: round;
  stroke-linejoin: round;
  transition: fill 0.16s ease, stroke 0.16s ease, transform 0.16s ease;
}

.action strong {
  font-size: 13px;
  font-weight: 800;
  color: currentColor;
}

.action-like.active {
  color: #7ec7c7;
}

.action-save.active {
  color: #e2b84c;
}

.action-like.active .action-svg,
.action-save.active .action-svg {
  fill: currentColor;
  stroke: currentColor;
}

.action-share {
  color: #8b8f98;
  font-size: 13px;
  font-weight: 700;
}

.action-share strong {
  display: none;
}

.floor-thread {
  margin: 0 12px 12px 56px;
  padding: 10px;
  display: grid;
  gap: 10px;
  border-radius: 16px;
  background: rgba(28, 30, 38, 0.65);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.thread-reply {
  display: grid;
  grid-template-columns: 28px minmax(0, 1fr);
  gap: 8px;
}

.thread-reply .avatar {
  width: 28px;
  height: 28px;
  font-size: 12px;
}

.thread-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.thread-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
}

.thread-action-btn {
  border: 0;
  background: transparent;
  color: #5f636e;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 0;
  transition: color 0.2s ease;
}

.thread-action-btn:hover {
  color: #8b8f98;
}

.thread-like-btn {
  padding: 0;
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.thread-like-btn svg {
  width: 16px;
  height: 16px;
  fill: transparent;
  stroke: currentColor;
  stroke-width: 1.8;
  transition: fill 0.2s ease, color 0.2s ease;
  flex-shrink: 0;
}

.thread-like-btn.active {
  color: #5cc9b7;
}

.thread-like-btn.active svg {
  fill: #5cc9b7;
}

.thread-like-btn span {
  font-size: 11px;
  flex-shrink: 0;
}

.thread-author {
  display: flex;
  align-items: center;
  gap: 7px;
  min-width: 0;
  margin-bottom: 3px;
  color: #5f636e;
  font-size: 11px;
}

.thread-author strong {
  font-size: 13px;
  color: #e4e6ea;
}

.thread-reply p {
  margin: 0;
  color: rgba(228, 230, 234, 0.78);
  font-size: 14px;
  line-height: 1.55;
}

.thread-text-btn {
  border: 0;
  background: transparent;
  color: rgba(228, 230, 234, 0.78);
  font-size: 14px;
  line-height: 1.55;
  text-align: left;
  cursor: pointer;
  padding: 0;
  width: 100%;
  transition: color 0.2s ease;
}

.thread-text-btn:hover {
  color: #e4e6ea;
}

.thread-empty {
  margin: 0;
  color: #5f636e;
  font-size: 12px;
  line-height: 1.5;
}

.detail-composer.expanded {
  grid-template-columns: 1fr;
  gap: 10px;
}

.comment-entry {
  min-height: 44px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(28, 30, 38, 0.55);
  color: rgba(228, 230, 234, 0.50);
  text-align: left;
  padding: 0 16px;
  box-shadow: inset 0 1px 0 rgba(255,255,255,0.05);
  backdrop-filter: blur(16px) saturate(1.2);
  cursor: pointer;
}

.composer-textarea {
  width: 100%;
  min-height: 82px;
  max-height: 150px;
  resize: none;
  border: 1px solid rgba(255, 255, 255, 0.10);
  outline: none;
  border-radius: 18px;
  padding: 13px 14px;
  background: rgba(28, 30, 38, 0.55);
  color: #e4e6ea;
  box-shadow: inset 0 1px 0 rgba(255,255,255,0.05);
  backdrop-filter: blur(18px) saturate(1.2);
}

.composer-textarea:focus {
  border-color: rgba(92, 201, 183, 0.42);
}

.composer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.composer-tools {
  display: flex;
  gap: 8px;
  min-width: 0;
  overflow-x: auto;
  scrollbar-width: none;
}

.composer-tools::-webkit-scrollbar {
  display: none;
}

.tool-btn {
  border: 1px solid rgba(255, 255, 255, 0.07);
  min-height: 38px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(28, 30, 38, 0.65);
  color: #8b8f98;
  font-size: 13px;
  flex: 0 0 auto;
  cursor: pointer;
}

.primary-btn {
  padding: 0 18px;
  min-height: 44px;
  border-radius: 999px;
  background: #5cc9b7;
  color: #0c0e13;
  font-weight: 800;
  border: 0;
  cursor: pointer;
}

.sheet-backdrop {
  position: fixed;
  inset: 0;
  z-index: 38;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 18px;
  background: rgba(0, 0, 0, 0.50);
  backdrop-filter: blur(10px);
}

.detail-settings-backdrop {
  align-items: end;
}

.detail-settings-sheet {
  width: min(420px, 100%);
  display: grid;
  gap: 10px;
  padding: 8px 16px 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 22px 22px 0 0;
  background: rgba(22, 24, 30, 0.90);
  box-shadow: 0 -18px 50px rgba(0, 0, 0, 0.50);
  backdrop-filter: blur(24px) saturate(150%);
}

.sheet-handle {
  width: 42px;
  height: 4px;
  border-radius: 999px;
  margin: 0 auto 12px;
  background: rgba(255, 255, 255, 0.10);
}

.settings-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.settings-head strong {
  font-size: 17px;
  color: #e4e6ea;
}

.setting-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.setting-group span {
  color: rgba(228, 230, 234, 0.74);
  font-size: 14px;
  font-weight: 800;
}

.segmented {
  display: inline-flex;
  gap: 4px;
  padding: 4px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(28, 30, 38, 0.65);
}

.segmented button {
  border: 0;
  min-width: 48px;
  height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: transparent;
  color: rgba(228, 230, 234, 0.50);
  font-weight: 850;
  cursor: pointer;
}

.segmented button.active {
  background: rgba(92, 201, 183, 0.15);
  color: #5cc9b7;
  box-shadow: inset 0 1px 0 rgba(255,255,255,0.10);
}

.report-btn {
  width: 100%;
  height: 46px;
  border: 1px solid rgba(224, 90, 85, 0.18);
  border-radius: 16px;
  background: rgba(224, 90, 85, 0.08);
  color: #e05a55;
  font-weight: 900;
  cursor: pointer;
}

.ghost-btn {
  padding: 0 16px;
  min-height: 44px;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 999px;
  background: transparent;
  color: #e4e6ea;
  cursor: pointer;
}

.share-sheet {
  width: min(420px, 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 26px;
  padding: 10px 14px 16px;
  background: rgba(22, 24, 30, 0.90);
  box-shadow: 0 -18px 48px rgba(0, 0, 0, 0.50), inset 0 1px 0 rgba(255,255,255,0.06);
  backdrop-filter: blur(20px);
}

.share-sheet h3 {
  font-size: 17px;
  color: #e4e6ea;
  margin: 0;
}

.share-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 8px;
  margin-top: 12px;
}

.share-channel {
  border: 0;
  min-width: 0;
  display: grid;
  justify-items: center;
  gap: 8px;
  padding: 8px 2px;
  background: transparent;
  color: #8b8f98;
  cursor: pointer;
}

.share-channel span {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(92, 201, 183, 0.12);
  color: #5cc9b7;
  font-size: 13px;
  font-weight: 900;
}

.share-channel strong {
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
}

.toast {
  position: fixed;
  top: 72px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 20;
  width: min(360px, calc(100% - 32px));
  padding: 12px 14px;
  border: 1px solid rgba(92, 201, 183, 0.18);
  border-radius: 999px;
  background: rgba(22, 24, 30, 0.92);
  color: #e4e6ea;
  box-shadow: 0 16px 44px rgba(0, 0, 0, 0.42);
  text-align: center;
}

.detail-page.theme-light {
  --text: #e4e6ea;
  --muted: #8b8f98;
  --line: rgba(255, 255, 255, 0.07);
  color: var(--text);
}

.detail-page.theme-light .article-body,
.detail-page.theme-light .comments-panel,
.detail-page.theme-light .comment-floor {
  background: rgba(22, 24, 30, 0.62);
  color: var(--text);
}

.detail-page.theme-light .article-paragraph {
  color: rgba(228, 230, 234, 0.82) !important;
}

.detail-page.reader-small .article-paragraph {
  font-size: 14px !important;
}

.detail-page.reader-standard .article-paragraph {
  font-size: 16px !important;
}

.detail-page.reader-large .article-paragraph {
  font-size: 18px !important;
}
</style>
