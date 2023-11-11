package org.tutorial.model.PO.embeddings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class RolePermissionId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "RID", nullable = false)
	private Integer rid;
	
	@Column(name = "PID", nullable = false)
	private Integer pid;
}
