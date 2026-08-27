import { defineStore } from "pinia";
import { ref } from "vue";
import { productApi } from "@/api/product.js";
import {
  CATEGORY_LABELS,
  categoryLabel,
  categoryValue,
} from "@/config/catalog.js";

export const useProductStore = defineStore("product", () => {
  const products = ref([]);
  const selectedProduct = ref(null);
  const loading = ref(false);
  const error = ref(null);
  const selectedCategory = ref("전체");

  const categories = ["전체", ...CATEGORY_LABELS];

  /** 목록/상세 응답은 category가 enum(VISION)이라 화면 표기용 라벨로 바꿔둔다. */
  function normalizeProduct(product) {
    if (!product || typeof product !== "object") return product;

    return {
      ...product,
      category: categoryLabel(product.category),
    };
  }

  function unwrapList(res) {
    if (Array.isArray(res.data?.data)) return res.data.data;
    if (Array.isArray(res.data)) return res.data;
    return [];
  }

  async function fetchProducts(category = selectedCategory.value) {
    loading.value = true;
    error.value = null;

    try {
      const res =
        category === "전체"
          ? await productApi.getAll()
          : await productApi.getByCategory(categoryValue(category));

      products.value = unwrapList(res).map(normalizeProduct);
    } catch (e) {
      console.error("[ProductStore] fetchProducts failed:", e);
      error.value = "API 상품 목록을 불러오지 못했습니다.";
      products.value = [];
    } finally {
      loading.value = false;
    }
  }

  async function fetchProduct(id) {
    loading.value = true;
    error.value = null;

    try {
      const res = await productApi.getById(id);
      const raw =
        res.data?.data && typeof res.data.data === "object"
          ? res.data.data
          : res.data;

      selectedProduct.value = normalizeProduct(raw);
    } catch (e) {
      console.error("[ProductStore] fetchProduct failed:", e);
      error.value = "API 상품 정보를 불러오지 못했습니다.";
      selectedProduct.value = null;
    } finally {
      loading.value = false;
    }
  }

  function setCategory(cat) {
    selectedCategory.value = cat;
    fetchProducts(cat);
  }

  return {
    products,
    selectedProduct,
    loading,
    error,
    categories,
    selectedCategory,
    normalizeProduct,
    fetchProducts,
    fetchProduct,
    setCategory,
  };
});
