package com.example.maintenance.domain.technician;

import com.example.maintenance.domain.user.User;
import com.example.maintenance.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "technicians")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Technician extends BaseTimeEntity {

	@Id // 기본키
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * User 계정과 Technician 프로필을 1:1 연결
	 * user_id는 technicians 테이블의 외래키
	 */
	@OneToOne(fetch = FetchType.LAZY)
	// JPA에게 알려주는것. LAZY -> Technician을 조회한다고 해서 User 정보를 무조건 바로 가져오지 않고, 필요할 때 가져오게 하는 설정
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	// unique=true 로써, 실제 테이블에서 중복저장을 방지하는 제약조선
	// technicians 테이블에 user_id 컬럼을 만들고, 그 컬럼이 users 테이블의 id를 참조하게 한다.
	private User user;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(nullable = false, length = 50)
	private String department;

	@Column(name = "emp_no", nullable = false, unique = true, length = 30)
	private String empNo;

	public Technician(User user, String phone, String department, String empNo) {
		this.user = user;
		this.phone = phone;
		this.department = department;
		this.empNo = empNo;
	}
}