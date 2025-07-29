package com.example.memowithtags.common.model.enums

enum class SocialType {
    KAKAO,
    NAVER,
    GOOGLE;

    override fun toString(): String {
        return when (this) {
            KAKAO -> "kakao"
            NAVER -> "naver"
            GOOGLE -> "google"
        }
    }
}
