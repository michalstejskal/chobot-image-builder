package cz.chobot.image_builder.repository

import cz.chobot.image_builder.bo.Module
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
internal interface ModuleRepository : JpaRepository<Module, Long>