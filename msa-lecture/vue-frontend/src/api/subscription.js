import api from "./index.js";

export const subscriptionApi = {
  getMySubscriptions() {
    return api.get("/api/subscriptions/my");
  },
  subscribe(productId) {
    return api.post("/api/subscriptions", { productId });
  },
}
