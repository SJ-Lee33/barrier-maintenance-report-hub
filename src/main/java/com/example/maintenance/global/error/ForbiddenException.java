// 권한부족 에러 - 403 forbidden

package com.example.maintenance.global.error;

public class ForbiddenException extends RuntimeException {

	public ForbiddenException(String message) {
		super(message);
	}
}