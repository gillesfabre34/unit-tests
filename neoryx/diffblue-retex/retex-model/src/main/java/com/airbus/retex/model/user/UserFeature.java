package com.airbus.retex.model.user;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@IdClass(UserFeature.UserFeatureId.class)
public class UserFeature implements Serializable {
	private static final long serialVersionUID = 6523020817525684130L;

	@Id
	@Column(name = "user_id")
	private Long userId;

	@Id
	@Enumerated(EnumType.STRING)
	private FeatureCode code;

	@Enumerated(EnumType.STRING)
	private EnumRightLevel rightLevel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private User user;

	public UserFeature(Long userId, FeatureCode code, EnumRightLevel rightLevel) {
		this.userId = userId;
		this.code = code;
		this.rightLevel = rightLevel;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserFeatureId implements Serializable {

		@Column(name = "user_id")
		private Long userId;

		@Enumerated(EnumType.STRING)
		private FeatureCode code;
	}
}
