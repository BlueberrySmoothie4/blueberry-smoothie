import { defineStore } from 'pinia'
import { ref } from 'vue'
import { productApi } from '@/api/product.js'

export const useProductStore = defineStore('product', () => {
  const products = ref([])
  const selectedProduct = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const selectedCategory = ref('전체')

  const categories = ['전체', '비전', '음성', '언어', '문서', '생성형', '예측', '이상탐지', '기타']

  // 백엔드 카테고리 enum → 프론트 표시용 라벨 (ApiProduct.Category와 1:1)
  const categoryLabelMap = {
    VISION: '비전',
    SPEECH: '음성',
    LANGUAGE: '언어',
    DOCUMENT: '문서',
    GENERATIVE: '생성형',
    FORECAST: '예측',
    ANOMALY: '이상탐지',
    OTHER: '기타'
  }

  // 썸네일 이미지 매핑 (파일명은 유지하고 카테고리에 임시 재매핑 - 로고 교체 시 여기만 바꾸면 된다)
  const thumbnailMap = {
    SPRING: new URL('../assets/images/courses/spring_boot.png', import.meta.url).href,
    VUE: new URL('../assets/images/courses/vue_js.png', import.meta.url).href,
    DOCKER: new URL('../assets/images/courses/docker.png', import.meta.url).href,
    KUBERNETES: new URL('../assets/images/courses/kubernetes.png', import.meta.url).href,
    PYTHON: new URL('../assets/images/courses/python.png', import.meta.url).href,
    AI: new URL('../assets/images/courses/generative_ai.png', import.meta.url).href,
  }

  const categoryThumbnailMap = {
    '비전': thumbnailMap.PYTHON,
    '음성': thumbnailMap.VUE,
    '언어': thumbnailMap.SPRING,
    '문서': thumbnailMap.DOCKER,
    '생성형': thumbnailMap.AI,
    '예측': thumbnailMap.KUBERNETES,
    '이상탐지': thumbnailMap.DOCKER
  }

  function normalizeCategory(category) {
    if (!category) return ''
    return categoryLabelMap[category] || category
  }

  function normalizeProduct(product) {
    if (!product || typeof product !== 'object') return product

    return {
      ...product,
      category: normalizeCategory(product.category)
    }
  }

  function getThumbnail(product) {
    const thumbKey = product?.thumbnail?.toUpperCase?.() || ''
    if (thumbKey && thumbnailMap[thumbKey]) {
      return thumbnailMap[thumbKey]
    }

    return categoryThumbnailMap[product?.category] || null
  }

  async function fetchProducts() {
    loading.value = true
    error.value = null

    try {
      const res = await productApi.getAll()
      console.log('[ProductStore] fetchProducts response =', res.data)

      const rawProducts = Array.isArray(res.data?.data)
        ? res.data.data
        : Array.isArray(res.data)
          ? res.data
          : []

      products.value = rawProducts.map(normalizeProduct)

      console.log('[ProductStore] normalized products =', products.value)
    } catch (e) {
      console.error('[ProductStore] fetchProducts failed:', e)
      error.value = e.message || 'API 상품 목록을 불러오지 못했습니다.'
      products.value = []
    } finally {
      loading.value = false
    }
  }

  async function fetchProduct(id) {
    loading.value = true
    error.value = null

    try {
      const res = await productApi.getById(id)
      console.log('[ProductStore] fetchProduct response =', res.data)

      const rawProduct =
        res.data?.data && typeof res.data.data === 'object'
          ? res.data.data
          : res.data

      selectedProduct.value = normalizeProduct(rawProduct)

      console.log('[ProductStore] normalized selectedProduct =', selectedProduct.value)
    } catch (e) {
      console.error('[ProductStore] fetchProduct failed:', e)
      error.value = e.message || 'API 상품 정보를 불러오지 못했습니다.'
      selectedProduct.value = null
    } finally {
      loading.value = false
    }
  }

  function setCategory(cat) {
    selectedCategory.value = cat
  }

  return {
    products,
    selectedProduct,
    loading,
    error,
    categories,
    selectedCategory,
    thumbnailMap,
    categoryLabelMap,
    normalizeCategory,
    normalizeProduct,
    getThumbnail,
    fetchProducts,
    fetchProduct,
    setCategory
  }
})
