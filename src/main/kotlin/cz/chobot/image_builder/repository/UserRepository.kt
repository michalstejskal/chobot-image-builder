package cz.chobot.image_builder.repository

import cz.chobot.image_builder.bo.User
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
internal interface UserRepository : JpaRepository<User, Long>