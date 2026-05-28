<template>
  <header class="topbar">
    <div class="feed-user-head">
      <span class="avatar">林</span>
      <div class="feed-user-name">林若安</div>
    </div>
    <button class="icon-btn region-filter-btn" aria-label="地区筛选">
      <svg viewBox="0 0 24 24" aria-hidden="true">
        <path d="M9 18.5 4.5 20V6l4.5-1.5 6 1.5 4.5-1.5v14L15 20l-6-1.5Z" />
        <path d="M9 4.5v14M15 6v14" />
        <path d="M7 9.2c1.5-.8 2.8-.8 4.2 0 1.6.9 3.1.8 4.8-.3" />
      </svg>
    </button>
  </header>

  <main class="page" data-od-id="feed-screen">
    <div class="search-bar" role="search">
      <label class="search-field">
        <span class="search-icon">⌕</span>
        <input class="pixel-search-input" aria-label="搜索帖子" placeholder="" :value="query" @input="query = $event.target.value" />
        <span v-show="!query && typedPrompt" class="typed-search-hint" aria-hidden="true">
          {{ typedPrompt }}<span class="typed-cursor">_</span>
        </span>
        <button class="search-submit" type="button" @click="submitSearch">搜索</button>
      </label>
    </div>

    <div class="tabs feed-filter-tabs" :class="`active-${filterIndex}`">
      <button v-for="item in filters" :key="item" class="pill-btn" :class="{ active: filter === item }" @click="filter = item">{{ item }}</button>
    </div>

    <section v-if="filteredPosts.length === 0" class="notice">
      <h3>未找到相关内容</h3>
      <p>换个关键词试试，或发布新的活动讨论。</p>
    </section>

    <article v-for="post in filteredPosts" :key="post.id" class="card post-card" @click="openPost(post)" role="button" tabindex="0" @keydown.enter="openPost(post)" :aria-label="`查看帖子详情：${post.title}`">
      <div class="between">
        <div class="row">
          <span class="avatar">{{ post.avatar }}</span>
          <div>
            <div class="author-line">
              <strong>{{ post.club }}</strong>
              <span class="club-badge">{{ post.org }}</span>
            </div>
            <p class="meta">{{ post.time }}</p>
          </div>
        </div>
        <span class="tag">{{ post.tag }}</span>
      </div>
      <h2 class="post-title">{{ post.title }}</h2>
      <p>{{ post.text }}</p>
      <div @click.stop>
        <div class="media-grid" :class="`media-count-${Math.min(post.media.length, 3)}`" aria-label="帖子媒体预览">
          <div v-for="(item, index) in post.media.slice(0, 3)" :key="index" class="media-tile" :class="[
            'tone-' + (item.tone || 'green'),
            item.type === 'video' ? 'media-video-tile' : 'media-image-tile',
            { 'is-playing': item.type === 'video' && playingIndex === index }
          ]" @click="item.type === 'video' && toggleVideo(index)">
            <span class="media-label">{{ item.label }}</span>
            <span v-if="item.type === 'video'" class="play-badge">{{ playingIndex === index ? '播放中' : '播放' }}</span>
            <span v-if="item.type === 'video'" class="inline-video-control" aria-hidden="true">{{ playingIndex === index ? 'Ⅱ' : '▶' }}</span>
            <span v-if="index === 2 && post.media.length > 3" class="more-badge">+{{ post.media.length - 3 }}</span>
          </div>
        </div>
      </div>
      <div class="tag-row">
        <span class="tag">校园活动</span>
        <span class="tag">可报名</span>
      </div>
      <div class="actions feed-actions" @click.stop>
        <button class="action action-like" :class="{ active: post.liked }" @click="toggleLike(post)">
          <svg class="action-svg" viewBox="0 0 24 24" aria-hidden="true"><path d="M7.5 10.5v9H5.2A2.2 2.2 0 0 1 3 17.3v-4.6a2.2 2.2 0 0 1 2.2-2.2h2.3Zm0 0 4.7-6.3c.6-.8 1.9-.4 1.9.6v3.5h3.5c1.7 0 2.9 1.5 2.6 3.1l-1 5.4a3.2 3.2 0 0 1-3.1 2.6H7.5" /></svg>
          <strong v-if="post.liked">{{ post.likes }}</strong>
        </button>
        <button class="action action-comment" @click="openPost(post, 'comments')">
          <svg class="action-svg" viewBox="0 0 24 24" aria-hidden="true"><path d="M5 6.7A3.2 3.2 0 0 1 8.2 3.5h7.6A3.2 3.2 0 0 1 19 6.7v4.9a3.2 3.2 0 0 1-3.2 3.2h-3.6l-4.5 4.1v-4.1A2.7 2.7 0 0 1 5 12.1V6.7Z" /></svg>
          <strong>{{ post.comments }}</strong>
        </button>
        <button class="action action-share" @click="shareOpen = true">
          <svg class="action-svg" viewBox="0 0 24 24" aria-hidden="true"><path d="M8.2 12.6 18.8 6M8.2 11.4l10.6 6.6M7 15.3a3.3 3.3 0 1 0 0-6.6 3.3 3.3 0 0 0 0 6.6Zm12.1-7.2a2.9 2.9 0 1 0 0-5.8 2.9 2.9 0 0 0 0 5.8Zm0 13.6a2.9 2.9 0 1 0 0-5.8 2.9 2.9 0 0 0 0 5.8Z" /></svg>
          <strong>{{ post.shares || 12 }}</strong>
        </button>
      </div>
    </article>
  </main>

  <div v-if="shareOpen" class="sheet-backdrop" role="dialog" aria-modal="true" aria-label="选择转发方式" @click="shareOpen = false">
    <section class="share-sheet" @click.stop>
      <div class="sheet-handle" aria-hidden="true"></div>
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
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '@/store/post'

const router = useRouter()
const postStore = usePostStore()
const filter = ref('全部')
const query = ref('')
const shareOpen = ref(false)
const playingIndex = ref(null)

const filters = ['全部', '直播', '视频', '长文', '笔记']
const filterIndex = computed(() => filters.indexOf(filter.value))

const shareChannels = [
  ['friend', '好友', '校'],
  ['wechat', '微信好友', '微'],
  ['moments', '微信朋友圈', '圈'],
  ['qq', 'QQ 好友', 'Q'],
  ['qzone', 'QQ 空间', '空']
]

const fallbackSearchRecommendations = [
  '今晚图书馆南门活动',
  '摄影协会夜拍报名',
  '开放麦节目征集',
  '志愿服务排班',
  '周末社团活动'
]

const recommendations = ref(fallbackSearchRecommendations)
const typedPrompt = ref('')
const promptIndex = ref(0)
const typingPhase = ref('typing')
let typingTimer = null

const filteredPosts = computed(() => {
  if (!query.value) return postStore.posts
  return postStore.posts.filter(post => 
    `${post.club}${post.org}${post.title}${post.text}`.includes(query.value)
  )
})

function openPost(post, focus = 'top') {
  router.push({ path: `/detail/${post.id}`, query: { focus } })
}

function toggleLike(post) {
  postStore.toggleLike(post.id)
}

function toggleVideo(index) {
  playingIndex.value = playingIndex.value === index ? null : index
}

function submitSearch() {
  const nextQuery = query.value.trim()
  query.value = nextQuery
  refreshRecommendations(nextQuery)
}

function refreshRecommendations(seed = '') {
  const source = seed || '校园活动'
  const pool = [
    `搜索"${source}"相关活动`,
    '附近正在招募的社团',
    '本周可报名活动清单',
    '同学都在看的帖子',
    '可带朋友参加的活动',
    '摄影 / 音乐 / 志愿服务',
    '今晚有空位的活动',
    '新生适合加入的社团'
  ]
  const start = Math.abs(source.split('').reduce((sum, char) => sum + char.charCodeAt(0), 0)) % pool.length
  recommendations.value = Array.from({ length: 5 }, (_, index) => pool[(start + index) % pool.length])
  promptIndex.value = 0
  typedPrompt.value = ''
  typingPhase.value = 'typing'
  localStorage.setItem('campus-search-recommendations', JSON.stringify(recommendations.value))
}

function startTypingAnimation() {
  const text = recommendations.value[promptIndex.value] || ''
  
  if (typingPhase.value === 'typing') {
    if (typedPrompt.value.length < text.length) {
      typingTimer = setTimeout(() => {
        typedPrompt.value = text.slice(0, typedPrompt.value.length + 1)
        startTypingAnimation()
      }, 72)
    } else {
      typingTimer = setTimeout(() => {
        typingPhase.value = 'deleting'
        startTypingAnimation()
      }, 4000)
    }
  } else {
    if (typedPrompt.value.length > 0) {
      typingTimer = setTimeout(() => {
        typedPrompt.value = text.slice(0, typedPrompt.value.length - 1)
        startTypingAnimation()
      }, 34)
    } else {
      promptIndex.value = (promptIndex.value + 1) % recommendations.value.length
      typingPhase.value = 'typing'
      startTypingAnimation()
    }
  }
}

onMounted(() => {
  refreshRecommendations()
  startTypingAnimation()
})

onUnmounted(() => {
  if (typingTimer) clearTimeout(typingTimer)
})
</script>

<style lang="scss" scoped>
.topbar {
  position: sticky;
  top: 0;
  z-index: 8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 18px 14px;
  background: rgba(12, 14, 19, 0.78);
  backdrop-filter: blur(20px) saturate(1.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.feed-user-head {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.feed-user-head .avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(145deg, rgba(92, 201, 183, 0.16), rgba(255, 255, 255, 0.06));
  border-color: rgba(92, 201, 183, 0.16);
}

.feed-user-name {
  color: #e4e6ea;
  font-size: 16px;
  font-weight: 850;
  letter-spacing: -0.025em;
  line-height: 1.1;
}

.icon-btn {
  width: 44px;
  min-height: 44px;
  border: 0;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: rgba(28, 30, 38, 0.65);
  color: #e4e6ea;
  cursor: pointer;
}

.icon-btn.region-filter-btn {
  width: 36px;
  min-height: 36px;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.icon-btn.region-filter-btn svg {
  width: 23px;
  height: 23px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.75;
  stroke-linejoin: round;
  stroke-linecap: round;
}

.page {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  padding: 16px 16px 24px;
  scrollbar-width: none;
}

.page::-webkit-scrollbar {
  display: none;
}

.search-bar {
  display: block;
}

.search-field {
  position: relative;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 9px;
  min-width: 0;
  min-height: 50px;
  padding: 5px 6px 5px 15px;
  border-radius: 25px;
  background: rgba(22, 24, 30, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.30), inset 0 1px 0 rgba(255, 255, 255, 0.06);
  color: #5f636e;
  backdrop-filter: blur(18px) saturate(1.12);
}

.search-field:focus-within {
  border-color: rgba(92, 201, 183, 0.32);
  background: rgba(22, 24, 30, 0.88);
  box-shadow: 0 14px 34px rgba(0, 0, 0, 0.40), inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

.search-icon {
  font-size: 18px;
  line-height: 1;
  color: #5f636e;
}

.pixel-search-input {
  all: unset;
  width: 100%;
  min-width: 0;
  color: #e4e6ea;
  font-size: 14px;
  line-height: 1.35;
}

.typed-search-hint {
  pointer-events: none;
  position: absolute;
  left: 43px;
  right: 78px;
  top: 50%;
  transform: translateY(-50%);
  overflow: hidden;
  white-space: nowrap;
  color: rgba(139, 143, 152, 0.64);
  font-size: 14px;
  line-height: 1.35;
  letter-spacing: 0.01em;
}

.typed-cursor {
  display: inline-block;
  margin-left: 2px;
  color: rgba(92, 201, 183, 0.86);
  animation: cursorBlink 1s steps(1) infinite;
}

@keyframes cursorBlink {
  50% { opacity: 0.18; }
}

.search-submit {
  border: 0;
  min-height: 38px;
  padding: 0 14px;
  border-radius: 19px;
  background: #5cc9b7;
  color: #0c0e13;
  font-weight: 800;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.30);
  flex: 0 0 auto;
  cursor: pointer;
}

.search-submit:active {
  transform: scale(0.98);
}

.tabs {
  position: relative;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 6px;
  padding: 14px 0 6px;
}

.feed-filter-tabs::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 4px;
  width: calc((100% - 24px) / 5);
  height: 2px;
  border-radius: 999px;
  background: #5cc9b7;
  transform: translateX(calc(var(--filter-index, 0) * (100% + 6px)));
  transition: transform 0.34s cubic-bezier(0.2, 0.8, 0.2, 1), background 0.22s ease;
  box-shadow: 0 0 12px rgba(92, 201, 183, 0.28);
}

.feed-filter-tabs.active-0 { --filter-index: 0; }
.feed-filter-tabs.active-1 { --filter-index: 1; }
.feed-filter-tabs.active-2 { --filter-index: 2; }
.feed-filter-tabs.active-3 { --filter-index: 3; }
.feed-filter-tabs.active-4 { --filter-index: 4; }

.pill-btn {
  position: relative;
  z-index: 1;
  width: 100%;
  justify-content: center;
  padding: 0 0 8px;
  border: 0;
  background: transparent;
  color: #8b8f98;
  font-weight: 800;
  cursor: pointer;
  transition: color 0.22s ease, transform 0.22s ease;
}

.pill-btn.active {
  color: #5cc9b7;
  transform: translateY(-1px);
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

.post-card {
  display: grid;
  gap: 10px;
  cursor: pointer;
}

.post-card:focus-visible {
  outline: 2px solid rgba(92, 201, 183, 0.40);
  outline-offset: 2px;
}

.between {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.row {
  display: flex;
  align-items: center;
  gap: 10px;
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

.club-badge {
  display: inline-flex;
  align-items: center;
  max-width: 120px;
  padding: 3px 7px;
  border: 1px solid rgba(92, 201, 183, 0.22);
  border-radius: 999px;
  background: rgba(92, 201, 183, 0.10);
  color: #5cc9b7;
  font-size: 10px;
  font-weight: 800;
  line-height: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta {
  color: #8b8f98;
  font-size: 12px;
  font-weight: 650;
}

.tag {
  padding: 6px 9px;
  border-radius: 999px;
  color: #5cc9b7;
  font-size: 12px;
  font-weight: 800;
  background: rgba(92, 201, 183, 0.10);
  border: 1px solid rgba(92, 201, 183, 0.22);
  line-height: 1;
  flex: 0 0 auto;
}

.post-title {
  color: #e4e6ea;
  font-size: 16px;
  font-weight: 850;
  line-height: 1.35;
  margin: 0;
}

p {
  color: #8b8f98;
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
}

.media-grid {
  display: grid;
  gap: 8px;
  margin-top: 14px;
  overflow: hidden;
  border-radius: 18px;
}

.media-grid.media-count-1 {
  grid-template-columns: 1fr;
}

.media-grid.media-count-2,
.media-grid.media-count-3 {
  grid-template-columns: repeat(2, 1fr);
}

.media-grid.media-count-3 .media-tile:first-child {
  grid-row: span 2;
}

.media-tile {
  min-height: 104px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 12px;
  position: relative;
  overflow: hidden;
  color: #e4e6ea;
  display: flex;
  align-items: flex-end;
  text-align: left;
  background: var(--panel-2);
}

.media-count-1 .media-tile {
  min-height: 188px;
}

.media-tile::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 20% 18%, rgba(255, 255, 255, 0.08), transparent 30%),
    linear-gradient(145deg, rgba(255, 255, 255, 0.04), rgba(0, 0, 0, 0.30));
}

.media-tile.tone-blue {
  background: linear-gradient(135deg, #1a2e3a, #0c1a24);
  color: #b8d6e6;
}

.media-tile.tone-green {
  background: linear-gradient(135deg, #1a3a2e, #0c2218);
  color: #b8e6d4;
}

.media-tile.tone-amber {
  background: linear-gradient(135deg, #3a2e1a, #241c0c);
  color: #e6d4b8;
}

.media-tile.tone-violet {
  background: linear-gradient(135deg, #2e1a3a, #1c0c24);
  color: #d4b8e6;
}

.media-image-tile {
  cursor: default;
}

.media-video-tile {
  cursor: pointer;
}

.media-video-tile.is-playing {
  border-color: rgba(92, 201, 183, 0.38);
  box-shadow: inset 0 0 0 1px rgba(92, 201, 183, 0.14), 0 0 28px rgba(92, 201, 183, 0.10);
}

.media-video-tile.is-playing::before {
  background:
    radial-gradient(circle at 50% 42%, rgba(92, 201, 183, 0.14), transparent 36%),
    linear-gradient(145deg, rgba(255, 255, 255, 0.06), rgba(0, 0, 0, 0.30));
}

.media-label,
.play-badge,
.more-badge {
  position: relative;
  z-index: 1;
}

.media-label {
  font-size: 13px;
  font-weight: 800;
}

.play-badge,
.more-badge {
  position: absolute;
  border-radius: 999px;
  background: rgba(12, 14, 19, 0.62);
  color: rgba(228, 230, 234, 0.84);
  backdrop-filter: blur(10px);
}

.play-badge {
  left: 10px;
  top: 10px;
  padding: 6px 9px;
  font-size: 11px;
  font-weight: 800;
}

.more-badge {
  right: 10px;
  top: 10px;
  min-width: 30px;
  height: 30px;
  display: grid;
  place-items: center;
  font-size: 12px;
}

.inline-video-control {
  position: absolute;
  z-index: 1;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 36px;
  height: 36px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: rgba(92, 201, 183, 0.14);
  border: 1px solid rgba(92, 201, 183, 0.28);
  color: rgba(228, 230, 234, 0.92);
  font-size: 13px;
  font-weight: 900;
  backdrop-filter: blur(14px) saturate(1.3);
  box-shadow: 0 4px 16px rgba(92, 201, 183, 0.22), inset 0 1px 0 rgba(255, 255, 255, 0.14);
}

.tag-row {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, .06);
}

.action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 0;
  background: transparent;
  color: #8b8f98;
  font-size: 13px;
  cursor: pointer;
  padding: 0 10px;
  min-width: 0;
}

.action.active {
  color: #5cc9b7;
}

.action-like.active {
  color: #7ec7c7;
}

.action-svg {
  width: 22px;
  height: 22px;
  fill: transparent;
  stroke: currentColor;
  stroke-width: 1.9;
  stroke-linecap: round;
  stroke-linejoin: round;
  transition: fill 0.16s ease, stroke 0.16s ease, transform 0.16s ease;
}

.action:active {
  transform: scale(0.92);
}

.action-like.active .action-svg {
  fill: currentColor;
  stroke: currentColor;
}

.action-label {
  font-weight: 600;
}

.action strong {
  font-weight: 800;
}

.notice {
  text-align: center;
  padding: 40px 16px;
}

.notice h3 {
  color: #e4e6ea;
  font-size: 16px;
  margin: 0 0 8px;
}

.notice p {
  color: #8b8f98;
  font-size: 14px;
  margin: 0;
}

.sheet-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  z-index: 20;
  display: flex;
  align-items: flex-end;
}

.share-sheet {
  width: 100%;
  background: #16181e;
  border-radius: 24px 24px 0 0;
  padding: 16px;
  animation: sheetSlideUp 0.32s cubic-bezier(0.2, 0.8, 0.2, 1);
}

@keyframes sheetSlideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.sheet-handle {
  width: 36px;
  height: 4px;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.15);
  margin: 0 auto 16px;
}

.share-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.share-channel {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  border: 0;
  background: transparent;
  color: #e4e6ea;
  cursor: pointer;
}

.share-channel span {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(92, 201, 183, 0.15);
  display: grid;
  place-items: center;
  font-size: 18px;
  font-weight: 800;
}

.share-channel strong {
  font-size: 11px;
  font-weight: 600;
}

.ghost-btn {
  border: 0;
  background: transparent;
  color: #5cc9b7;
  font-weight: 700;
  cursor: pointer;
}
</style>
