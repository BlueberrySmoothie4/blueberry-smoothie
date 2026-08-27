import api from './index.js'

export const recommendApi = {
  getRecommendations(userId) {
    return api.get(`/api/recommend/${userId}`)
  }
}