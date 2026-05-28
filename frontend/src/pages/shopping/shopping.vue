<template>
  <div class="topbar">
    <div class="search-wrapper">
      <span class="search-icon">🔍</span>
      <input
        class="search-input"
        placeholder="搜索商品..."
        v-model="searchQuery"
      />
    </div>
    <button class="icon-btn cart-btn" @click="showCart">
      <span class="cart-icon">🛒</span>
      <span class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</span>
    </button>
  </div>

  <div class="category-tabs">
    <button
      v-for="category in categories"
      :key="category"
      class="category-tab"
      :class="{ active: currentCategory === category }"
      @click="currentCategory = category"
    >
      {{ category }}
    </button>
  </div>

  <div class="shopping-grid">
    <div
      v-for="product in filteredProducts"
      :key="product.id"
      class="product-card glass-card"
      @click="viewProduct(product)"
    >
      <div class="product-image" :class="'tone-' + product.tone">
        <span class="product-image-text">{{ product.imageText }}</span>
      </div>
      <div class="product-info">
        <h3 class="product-name">{{ product.name }}</h3>
        <div class="product-footer">
          <span class="product-price">¥{{ product.price }}</span>
          <button class="add-cart-btn" @click.stop="addToCart(product)">
            <span class="add-icon">+</span>
          </button>
        </div>
        <div class="product-meta">
          <span class="product-sold">已售 {{ product.sold }}</span>
          <span class="product-location">{{ product.location }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const searchQuery = ref('')
const currentCategory = ref('全部')
const cartCount = ref(0)

const categories = ['全部', '教材', '数码', '服饰', '生活', '食品']

const products = ref([
  {
    id: 1,
    name: '高等数学教材 第七版',
    price: 35,
    sold: 128,
    location: '南校区',
    imageText: '教材',
    tone: 'blue'
  },
  {
    id: 2,
    name: '机械键盘 青轴',
    price: 199,
    sold: 56,
    location: '北校区',
    imageText: '数码',
    tone: 'purple'
  },
  {
    id: 3,
    name: '校园文化衫 L码',
    price: 59,
    sold: 234,
    location: '东校区',
    imageText: '服饰',
    tone: 'pink'
  },
  {
    id: 4,
    name: '保温杯 500ml',
    price: 45,
    sold: 89,
    location: '西校区',
    imageText: '生活',
    tone: 'green'
  },
  {
    id: 5,
    name: '校园特产零食礼包',
    price: 28,
    sold: 312,
    location: '南校区',
    imageText: '食品',
    tone: 'orange'
  },
  {
    id: 6,
    name: '英语四六级词汇书',
    price: 25,
    sold: 167,
    location: '北校区',
    imageText: '教材',
    tone: 'blue'
  }
])

const filteredProducts = computed(() => {
  let result = products.value
  
  if (currentCategory.value !== '全部') {
    result = result.filter(p => p.imageText === currentCategory.value)
  }
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(p => 
      p.name.toLowerCase().includes(query) || 
      p.location.toLowerCase().includes(query)
    )
  }
  
  return result
})

function viewProduct(product) {
  alert(product.name)
}

function addToCart(product) {
  cartCount.value++
  alert('已加入购物车')
}

function showCart() {
  alert('购物车 (' + cartCount.value + ')')
}
</script>

<style lang="scss" scoped>
.topbar {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(12, 14, 19, 0.95);
  position: sticky;
  top: 0;
  z-index: 99;
}

.search-wrapper {
  flex: 1;
  height: 40px;
  background: rgba(22, 24, 30, 0.78);
  border-radius: 20px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-icon {
  font-size: 16px;
}

.search-input {
  flex: 1;
  background: none;
  border: none;
  color: #e4e6ea;
  font-size: 14px;
}

.search-input::placeholder {
  color: #8b8f98;
}

.icon-btn {
  width: 40px;
  height: 40px;
  background: rgba(22, 24, 30, 0.78);
  border: none;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
}

.cart-icon {
  font-size: 18px;
}

.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  background: #ff4757;
  border-radius: 9px;
  font-size: 11px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.category-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  overflow-x: auto;
  background: rgba(12, 14, 19, 0.95);
  position: sticky;
  top: 64px;
  z-index: 98;
}

.category-tab {
  padding: 8px 16px;
  background: rgba(22, 24, 30, 0.78);
  border: none;
  border-radius: 16px;
  color: #8b8f98;
  font-size: 14px;
  white-space: nowrap;
  cursor: pointer;
}

.category-tab.active {
  background: #5cc9b7;
  color: #0c0e13;
  font-weight: 600;
}

.shopping-grid {
  padding: 16px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.product-card {
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.07);
  background: rgba(22, 24, 30, 0.62);
  border-radius: 24px;
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.32);
  backdrop-filter: blur(14px);
  overflow: hidden;
}

.product-image {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.product-image.tone-blue {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.product-image.tone-purple {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.product-image.tone-pink {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.product-image.tone-green {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.product-image.tone-orange {
  background: linear-gradient(135deg, #fa709a, #fee140);
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 600;
  color: #e4e6ea;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.product-price {
  font-size: 16px;
  font-weight: 700;
  color: #5cc9b7;
}

.add-cart-btn {
  width: 28px;
  height: 28px;
  background: #5cc9b7;
  border: none;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.add-icon {
  font-size: 18px;
  color: white;
  font-weight: 300;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #8b8f98;
}
</style>
