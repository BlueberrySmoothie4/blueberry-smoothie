import api from "./index.js";

export const subscriptionApi = {
  getMySubscriptions() {
    return api.get("/api/subscriptions/my");
  },
  subscribe(productId) {
    return api.post("/api/subscriptions", { productId });
  },
  // 주의: 백엔드에 DELETE /api/subscriptions/{id} 는 아직 없다 (호출 시 404). 치환 전부터 있던 미사용 스텁.
  cancel(subscriptionId) {
    return api.delete(`/api/subscriptions/${subscriptionId}`);
  },
  getRecommendations(userId) {
    return api.get(`/api/recommend/${userId}`);
  },
};
