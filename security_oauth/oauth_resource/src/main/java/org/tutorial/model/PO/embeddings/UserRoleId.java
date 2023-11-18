package org.tutorial.model.PO.embeddings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class UserRoleId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "UID", nullable = false)
	private Integer uid;
	
	@Column(name = "RID", nullable = false)
	private Integer rid;
}
