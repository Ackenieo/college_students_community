import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePostStore = defineStore('post', () => {
  const posts = ref([
    {
      id: 1,
      type: 'article',
      club: '林予安',
      org: '摄影协会',
      avatar: '林',
      tag: '报名中',
      time: '2 小时前',
      title: '周五夜拍路线开放报名，图书馆南门集合',
      text: '适合零基础同学，现场讲低光构图、校园建筑取景和手机后期。三脚架可在评论区预约。',
      likes: 128,
      comments: 34,
      shares: 12,
      liked: false,
      media: [
        { type: 'image', label: '图书馆夜景', tone: 'blue' },
        { type: 'image', label: '湖边步道', tone: 'green' },
        { type: 'image', label: '旧教学楼', tone: 'amber' }
      ]
    },
    {
      id: 2,
      type: 'short_video',
      club: '陈一诺',
      org: '音乐社',
      avatar: '陈',
      tag: '开放麦',
      time: '今天 13:20',
      title: '开放麦还差 3 组表演，设备清单已确认',
      text: '主办方提供两支麦克风、一把民谣吉他和键盘，节目顺序活动前一天私信确认。',
      likes: 76,
      comments: 19,
      shares: 8,
      liked: false,
      media: [
        { type: 'video', label: '上期现场片段', tone: 'violet' }
      ]
    },
    {
      id: 3,
      type: 'note',
      club: '周嘉禾',
      org: '青年志愿者协会',
      avatar: '周',
      tag: '志愿服务',
      time: '昨天',
      title: '图书漂流市集志愿排班开放，优先招募下午场',
      text: '需要同学协助登记、引导和物资整理，完成后可生成服务证明。',
      likes: 93,
      comments: 27,
      shares: 15,
      liked: false,
      media: [
        { type: 'image', label: '摊位布置', tone: 'green' },
        { type: 'video', label: '市集动线', tone: 'blue' }
      ]
    }
  ])

  const categories = ['全部', '直播', '视频', '长文', '笔记']

  function getPost(postId) {
    return posts.value.find(p => p.id === postId)
  }

  function addPost(draft) {
    const typeTagMap = {
      article: '长文',
      note: '笔记',
      live: '直播',
      short_video: '视频'
    }
    const media = buildMediaPreview(draft)
    const post = {
      id: Date.now(),
      type: draft.type,
      club: '林若安',
      org: '校园创作者',
      avatar: '林',
      tag: typeTagMap[draft.type] || '动态',
      time: '刚刚',
      title: draft.title,
      text: buildPostText(draft),
      likes: 0,
      comments: 0,
      shares: 0,
      liked: false,
      visibility: draft.visibility,
      topics: draft.topics,
      content: draft.content,
      cover: draft.cover,
      media
    }
    posts.value.unshift(post)
    return post
  }

  function buildPostText(draft) {
    if (draft.type === 'article') {
      return draft.content.summary || draft.content.blocks.find(block => block.text)?.text || '发布了一篇新的长文。'
    }
    if (draft.type === 'note') {
      return draft.content.pages.find(page => page.body)?.body || '发布了一组分页笔记。'
    }
    if (draft.type === 'live') {
      const mode = draft.content.mode === 'instant' ? '正在准备开播' : `${draft.content.scheduledAt || '稍后'} 开播`
      return `${mode} · ${draft.content.description || '直播预告已创建。'}`
    }
    return draft.content.description || '发布了一条新的短视频。'
  }

  function buildMediaPreview(draft) {
    if (draft.type === 'article') {
      const blocks = draft.content.blocks.filter(block => block.type !== 'paragraph')
      return blocks.length ? blocks.map((block, index) => ({ type: block.type === 'video' ? 'video' : 'image', label: block.label || `媒体 ${index + 1}`, tone: block.tone || 'green', radius: block.radius })) : [{ type: 'image', label: draft.cover || '长文封面', tone: 'green' }]
    }
    if (draft.type === 'note') {
      return draft.content.pages.slice(0, 3).map((page, index) => ({ type: 'image', label: page.title || `第 ${index + 1} 页`, tone: ['blue', 'green', 'amber'][index % 3] }))
    }
    if (draft.type === 'live') {
      return [{ type: 'video', label: draft.content.mode === 'instant' ? '立即开播' : '直播预告', tone: 'amber' }]
    }
    return [{ type: 'video', label: draft.content.videoLabel || '短视频', tone: 'violet' }]
  }

  function toggleLike(postId) {
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.liked = !post.liked
      post.likes += post.liked ? 1 : -1
    }
  }

  function toggleCollect(postId) {
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.isCollected = !post.isCollected
    }
  }

  return {
    posts,
    categories,
    getPost,
    addPost,
    toggleLike,
    toggleCollect
  }
})
