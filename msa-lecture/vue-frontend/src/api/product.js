import api from "./index.js";

export const productApi = {
  /** 전체 API 상품 목록 — GET /api/products */
  getAll(params) {
    return api.get("/api/products", { params });
  },

  /** 카테고리별 탐색 — GET /api/products/category/{category} */
  getByCategory(category) {
    return api.get(`/api/products/category/${category}`);
  },

  getById(id) {
    return api.get(`/api/products/${id}`);
  },

  create(data) {
    return api.post("/api/products", data);
  },

  // 주의: 백엔드에 PUT /api/products/{id} 는 아직 없다 (호출 시 404). 치환 전부터 있던 미사용 스텁.
  update(id, data) {
    return api.put(`/api/products/${id}`, data);
  },
};
