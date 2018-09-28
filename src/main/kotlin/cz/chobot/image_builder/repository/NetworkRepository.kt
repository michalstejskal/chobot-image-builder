package cz.chobot.image_builder.repository

import cz.chobot.image_builder.bo.Network
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface NetworkRepository: JpaRepository<Network, Long>