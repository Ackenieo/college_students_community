import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useMessageStore = defineStore('message', () => {
  const conversations = ref([
    {
      id: 1,
      user: { name: '小红', avatar: '', id: 2 },
      lastMessage: '好的，明天见！',
      time: '10:30',
      unread: 2,
      messages: [
        { id: 1, senderId: 2, content: '你好，在吗？', time: '10:25' },
        { id: 2, senderId: 1, content: '在的，什么事？', time: '10:26' },
        { id: 3, senderId: 2, content: '明天一起去图书馆吗？', time: '10:28' },
        { id: 4, senderId: 1, content: '好的，几点？', time: '10:29' },
        { id: 5, senderId: 2, content: '好的，明天见！', time: '10:30' }
      ]
    },
    {
      id: 2,
      user: { name: '学习小组', avatar: '', id: 3 },
      lastMessage: '作业已经发群里了',
      time: '昨天',
      unread: 0,
      messages: [
        { id: 1, senderId: 3, content: '作业已经发群里了', time: '昨天' }
      ]
    }
  ])

  return {
    conversations
  }
})
