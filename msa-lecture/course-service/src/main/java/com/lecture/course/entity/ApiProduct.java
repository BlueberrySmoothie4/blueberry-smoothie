package com.lecture.course.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ApiProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // 월 구독료 (정액제)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // API 제공사 ID (users 테이블 참조 - 직접 JOIN 없이 ID만 보관)
    @Column(nullable = false)
    private Long providerId;

    // 구독자 수 (추천 서비스 정렬 기준)
    @Column(nullable = false)
    @Builder.Default
    private Integer subscriberCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PUBLISHED;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * 기능(capability) 기준 카테고리
     * - 산업 기준이 아니라 기능 기준: OCR API 하나가 제조/물류/금융에 모두 쓰이므로
     *   산업은 본질적으로 다중값이라 단일 enum과 맞지 않는다.
     */
    public enum Category {
        VISION,      // 이미지·영상 인식
        SPEECH,      // 음성 (STT/TTS)
        LANGUAGE,    // 자연어 처리
        DOCUMENT,    // 문서 처리 (OCR/양식 추출)
        GENERATIVE,  // 생성형
        FORECAST,    // 예측·시계열
        ANOMALY,     // 이상 탐지
        OTHER
    }

    /**
     * 상품 상태 - 구독 상태(Subscription.Status.ACTIVE)와 이름이 겹치지 않도록 분리
     */
    public enum Status {
        PUBLISHED,   // 판매 중
        DEPRECATED   // 지원 종료
    }

    public void increaseSubscriberCount() {
        this.subscriberCount++;
    }
}
