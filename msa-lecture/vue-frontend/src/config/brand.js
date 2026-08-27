/**
 * 서비스 표기를 한곳에서 관리한다.
 * 워드마크는 "SK"와 "AM"을 분리해 렌더링하므로 prefix/suffix를 따로 둔다.
 */
export const BRAND = {
  /** 헤더·사이드바·푸터에 노출되는 이름 */
  name: "SK AM",
  /** 워드마크 앞부분 — SK Red로 강조한다 */
  prefix: "SK",
  /** 워드마크 뒷부분 */
  suffix: "AM",
  /** 사이드바가 좁아졌을 때 쓰는 축약 표기 */
  mark: "AM",
  /** 이름이 무엇의 약자인지 풀어 쓴 표기 */
  fullName: "SK API Market",
  /** 랜딩 코드 예시에 노출되는 API 호스트 */
  apiHost: "api.sk-am.co.kr",
};
