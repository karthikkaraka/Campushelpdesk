package com.Sridevi.Campushelpdesk.Repository;

import com.Sridevi.Campushelpdesk.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

}
