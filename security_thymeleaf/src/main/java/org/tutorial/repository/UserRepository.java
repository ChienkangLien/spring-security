package org.tutorial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tutorial.model.PO.UserPO;

@Repository
public interface UserRepository extends JpaRepository<UserPO, Integer> {

	// 密碼由DaoAuthenticationProvider處理
//	public UserPO findByUsernameAndPassword(String username, String password);

	public UserPO findByUsername(String username);
}
