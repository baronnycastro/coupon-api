package com.coupon.infra.web;

public record ErrorResponse(int status, String error, String message) {
}
